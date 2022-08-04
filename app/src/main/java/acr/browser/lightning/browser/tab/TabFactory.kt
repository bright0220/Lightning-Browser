package acr.browser.lightning.browser.tab

import acr.browser.lightning.browser.image.IconFreeze
import acr.browser.lightning.browser.proxy.Proxy
import acr.browser.lightning.adblock.AdBlocker
import acr.browser.lightning.adblock.allowlist.AllowListModel
import acr.browser.lightning.browser.di.DiskScheduler
import acr.browser.lightning.favicon.FaviconModel
import acr.browser.lightning.preference.UserPreferences
import acr.browser.lightning.browser.webrtc.WebRtcPermissionsModel
import android.app.Activity
import android.graphics.Bitmap
import android.webkit.WebView
import io.reactivex.Scheduler
import javax.inject.Inject

/**
 * Constructs a [TabModel].
 */
class TabFactory @Inject constructor(
    private val webViewFactory: WebViewFactory,
    private val activity: Activity,
    private val adBlocker: AdBlocker,
    private val allowListModel: AllowListModel,
    private val faviconModel: FaviconModel,
    @DiskScheduler private val diskScheduler: Scheduler,
    private val urlHandler: UrlHandler,
    private val userPreferences: UserPreferences,
    @DefaultUserAgent private val defaultUserAgent: String,
    @IconFreeze private val iconFreeze: Bitmap,
    private val proxy: Proxy,
    private val webRtcPermissionsModel: WebRtcPermissionsModel
) {

    /**
     * Constructs a tab from the [webView] with the provided [tabInitializer].
     */
    fun constructTab(tabInitializer: TabInitializer, webView: WebView): TabModel {
        val headers = webViewFactory.createRequestHeaders()
        return TabAdapter(
            tabInitializer,
            webView,
            headers,
            TabWebViewClient(adBlocker, allowListModel, urlHandler, headers, proxy),
            TabWebChromeClient(
                activity,
                faviconModel,
                diskScheduler,
                userPreferences,
                webRtcPermissionsModel
            ),
            userPreferences,
            defaultUserAgent,
            iconFreeze,
            proxy
        )
    }
}
