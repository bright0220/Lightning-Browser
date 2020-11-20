package acr.browser.lightning._browser2.tab

import acr.browser.lightning.ssl.SslState
import acr.browser.lightning.view.FreezableBundleInitializer
import acr.browser.lightning.view.TabInitializer
import android.graphics.Bitmap
import android.os.Bundle
import android.webkit.WebView
import io.reactivex.Observable

/**
 * Created by anthonycr on 9/12/20.
 */
class TabAdapter(
    tabInitializer: TabInitializer,
    private val webView: WebView,
    private val requestHeaders: Map<String, String>,
    private val tabWebViewClient: TabWebViewClient,
    private val tabWebChromeClient: TabWebChromeClient
) : TabModel {

    private var latentInitializer: FreezableBundleInitializer? = null

    private var findInPageQuery: String? = null

    init {
        webView.webViewClient = tabWebViewClient
        webView.webChromeClient = tabWebChromeClient
        if (tabInitializer is FreezableBundleInitializer) {
            latentInitializer = tabInitializer
        } else {
            loadFromInitializer(tabInitializer)
        }
    }

    override val id: Int = webView.id

    override fun loadUrl(url: String) {
        webView.loadUrl(url, requestHeaders)
    }

    override fun loadFromInitializer(tabInitializer: TabInitializer) {
        tabInitializer.initialize(webView, requestHeaders)
    }

    override fun goBack() {
        webView.goBack()
    }

    override fun canGoBack(): Boolean = webView.canGoBack()

    override fun canGoBackChanges(): Observable<Boolean> = tabWebViewClient.goBackObservable.hide()

    override fun goForward() {
        webView.goForward()
    }

    override fun canGoForward(): Boolean = webView.canGoForward()

    override fun canGoForwardChanges(): Observable<Boolean> = tabWebViewClient.goForwardObservable.hide()

    override fun reload() {
        webView.reload()
    }

    override fun stopLoading() {
        webView.stopLoading()
    }

    override fun find(query: String) {
        webView.findAllAsync(query)
        findInPageQuery = query
    }

    override fun findNext() {
        webView.findNext(true)
    }

    override fun findPrevious() {
        webView.findNext(false)
    }

    override fun clearFindMatches() {
        webView.clearMatches()
        findInPageQuery = null
    }

    override val findQuery: String?
        get() = findInPageQuery

    override val favicon: Bitmap?
        get() = webView.favicon

    override fun faviconChanges(): Observable<Bitmap> = tabWebChromeClient.faviconObservable.hide()

    // TODO do we show "new tab"
    override val url: String
        get() = webView.url.orEmpty()

    override fun urlChanges(): Observable<String> = tabWebViewClient.urlObservable.hide()

    override val title: String
        get() = latentInitializer?.initialTitle ?: webView.title.orEmpty()

    override fun titleChanges(): Observable<String> = tabWebChromeClient.titleObservable.hide()

    override val sslState: SslState
        get() = tabWebViewClient.sslState

    override fun sslChanges(): Observable<SslState> = tabWebViewClient.sslStateObservable.hide()

    override val loadingProgress: Int
        get() = webView.progress

    override fun loadingProgress(): Observable<Int> = tabWebChromeClient.progressObservable.hide()

    override var isForeground: Boolean = false
        set(value) {
            field = value
            if (field) {
                webView.onResume()
                latentInitializer?.let(::loadFromInitializer)
                latentInitializer = null
            } else {
                webView.onPause()
            }
        }

    override fun destroy() {
        webView.stopLoading()
        webView.onPause()
        webView.clearHistory()
        webView.removeAllViews()
        webView.destroy()
    }

    override fun freeze(): Bundle = latentInitializer?.bundle
        ?: Bundle(ClassLoader.getSystemClassLoader()).also {
            webView.saveState(it)
        }
}
