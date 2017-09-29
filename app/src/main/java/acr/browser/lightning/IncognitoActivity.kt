package acr.browser.lightning

import acr.browser.lightning.browser.activity.BrowserActivity
import android.content.Intent
import android.os.Build
import android.view.Menu
import android.webkit.CookieManager
import android.webkit.CookieSyncManager
import com.anthonycr.bonsai.Completable

class IncognitoActivity : BrowserActivity() {

    @Suppress("DEPRECATION")
    public override fun updateCookiePreference(): Completable = Completable.create { subscriber ->
        val cookieManager = CookieManager.getInstance()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.createInstance(this@IncognitoActivity)
        }
        cookieManager.setAcceptCookie(preferences.incognitoCookiesEnabled)
        subscriber.onComplete()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.incognito, menu)
        return super.onCreateOptionsMenu(menu)
    }

    @Suppress("RedundantOverride")
    override fun onNewIntent(intent: Intent) =// handleNewIntent(intent);
            super.onNewIntent(intent)

    @Suppress("RedundantOverride")
    override fun onPause() = super.onPause()
    // saveOpenTabs();

    override fun updateHistory(title: String?, url: String) =// addItemToHistory(title, url);
            Unit

    override val isIncognito = true

    override fun closeActivity() = closeDrawers { closeBrowser() }
}
