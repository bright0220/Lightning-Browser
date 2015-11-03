package acr.browser.lightning.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.webkit.WebView;

import com.squareup.otto.Bus;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import acr.browser.lightning.R;
import acr.browser.lightning.bus.BrowserEvents;
import acr.browser.lightning.constant.Constants;
import acr.browser.lightning.preference.PreferenceManager;
import acr.browser.lightning.utils.UrlUtils;
import acr.browser.lightning.utils.Utils;
import acr.browser.lightning.view.LightningView;

/**
 * @author Stefano Pacifici
 * @date 2015/09/14
 */
@Singleton
public class TabsManager {

    private static final String TAG = TabsManager.class.getSimpleName();
    private final List<LightningView> mWebViewList = new ArrayList<>();
    private LightningView mCurrentTab;

    @Inject
    PreferenceManager mPreferenceManager;

    @Inject
    Bus mEventBus;

    @Inject
    public TabsManager() {}

    public synchronized void restoreTabsAndHandleIntent(final Activity activity,
                                                        final Intent intent,
                                                        final boolean incognito) {
        String url = null;
        if (intent != null) {
            url = intent.getDataString();
        }
        mWebViewList.clear();
        mCurrentTab = null;
        if (!incognito && mPreferenceManager.getRestoreLostTabsEnabled()) {
            final String mem = mPreferenceManager.getMemoryUrl();
            mPreferenceManager.setMemoryUrl("");
            String[] array = Utils.getArray(mem);
            for (String urlString : array) {
                if (!urlString.isEmpty()) {
                    newTab(activity, urlString, incognito);
                }
            }
        }
        if (url != null) {
            if (url.startsWith(Constants.FILE)) {
                final String urlToLoad = url;
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setCancelable(true)
                        .setTitle(R.string.title_warning)
                        .setMessage(R.string.message_blocked_local)
                        .setNegativeButton(android.R.string.cancel, null)
                        .setPositiveButton(R.string.action_open, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                newTab(activity, urlToLoad, incognito);
                            }
                        })
                        .show();
            } else {
                newTab(activity, url, incognito);
            }
        }
        if (mWebViewList.size() == 0) {
            newTab(activity, null, incognito);
        }
    }

    /**
     * Return a clone of the current tabs list. The list will not be updated, the user has to fetch
     * a new copy when notified.
     *
     * @return a copy of the current tabs list
     */
    public List<LightningView> getTabsList() {
        return new ArrayList<>(mWebViewList);
    }

    /**
     * Return the tab at the given position in tabs list, or null if position is not in tabs list
     * range.
     *
     * @param position the index in tabs list
     * @return the corespondent {@link LightningView}, or null if the index is invalid
     */
    @Nullable
    public synchronized LightningView getTabAtPosition(final int position) {
        if (position < 0 || position >= mWebViewList.size()) {
            return null;
        }

        return mWebViewList.get(position);
    }

    /**
     * Try to low memory pressure
     */
    public synchronized void freeMemory() {
        for (LightningView tab : mWebViewList) {
            tab.freeMemory();
        }
    }

    /**
     * Shutdown the manager
     */
    public synchronized void shutdown() {
        for (LightningView tab : mWebViewList) {
            tab.onDestroy();
        }
        mWebViewList.clear();
        mCurrentTab = null;
    }

    /**
     * Resume the tabs
     *
     * @param context
     */
    public synchronized void resume(final Context context) {
        for (LightningView tab : mWebViewList) {
            tab.initializePreferences(null, context);
        }
    }

    /**
     * Forward network connection status to the webviews.
     *
     * @param isConnected
     */
    public synchronized void notifyConnectionStatus(final boolean isConnected) {
        for (LightningView tab : mWebViewList) {
            final WebView webView = tab.getWebView();
            if (webView != null) {
                webView.setNetworkAvailable(isConnected);
            }
        }
    }

    /**
     * @return The number of currently opened tabs
     */
    public synchronized int size() {
        return mWebViewList.size();
    }

    /**
     * Create and return a new tab. The tab is automatically added to the tabs list.
     *
     * @param activity
     * @param url
     * @param isIncognito
     * @return
     */
    public synchronized LightningView newTab(final Activity activity,
                                             final String url,
                                             final boolean isIncognito) {
        final LightningView tab = new LightningView(activity, url, isIncognito);
        mWebViewList.add(tab);
        return tab;
    }

    /**
     * Remove a tab and return its reference or null if the position is not in tabs range
     *
     * @param position The position of the tab to remove
     * @return The removed tab reference or null
     */
    @Nullable
    public synchronized LightningView removeTab(final int position) {
        if (position >= mWebViewList.size()) {
            return null;
        }
        final LightningView tab = mWebViewList.remove(position);
        if (mCurrentTab == tab) {
            mCurrentTab = null;
        }
        tab.onDestroy();
        Log.d(Constants.TAG, tab.toString());
        return tab;
    }

    public synchronized void deleteTab(int position) {
        final LightningView currentTab = getCurrentTab();
        int current = positionOf(currentTab);

        if (current == position) {
            if (size() == 1) {
                mCurrentTab = null;
            } else if (current < size() - 1 ) {
                // There is another tab after this one
                mCurrentTab = getTabAtPosition(current + 1);
            } else {
                mCurrentTab = getTabAtPosition(current - 1);
            }
            removeTab(current);
        } else {
            removeTab(position);
        }
    }

    /**
     * Return the position of the given tab.
     *
     * @param tab the tab to look for
     * @return the position of the tab or -1 if the tab is not in the list
     */
    public synchronized int positionOf(final LightningView tab) {
        return mWebViewList.indexOf(tab);
    }

    /**
     * @return A string representation of the currently opened tabs
     */
    public String tabsString() {
        final StringBuilder builder = new StringBuilder();
        for (LightningView tab : mWebViewList) {
            final String url = tab.getUrl();
            if (!url.isEmpty()) {
                builder.append(url).append("|$|SEPARATOR|$|");
            }
        }
        return builder.toString();
    }

    /**
     * Return the {@link WebView} associated to the current tab, or null if there is no current tab
     *
     * @return a {@link WebView} or null
     */
    @Nullable
    public synchronized WebView getCurrentWebView() {
        return mCurrentTab != null ? mCurrentTab.getWebView() : null;
    }

    /**
     * TODO We should remove also this, but probably not
     *
     * @return
     */
    @Nullable
    public synchronized LightningView getCurrentTab() {
        return mCurrentTab;
    }

    /**
     * Switch the current tab to the one at the given position. It returns the selected. After this
     * call {@link TabsManager#getCurrentTab()} return the same reference returned by this method if
     * position is valid.
     *
     * @return the selected tab or null if position is out of tabs range
     */
    @Nullable
    public synchronized LightningView switchToTab(final int position) {
        if (position < 0 || position >= mWebViewList.size()) {
            Log.e(TAG, "Returning a null LightningView requested for position: " + position);
            return null;
        } else {
            final LightningView tab = mWebViewList.get(position);
            if (tab != null) {
                mCurrentTab = tab;
            }
            return tab;
        }
    }

}
