package acr.browser.lightning.browser.tab

import acr.browser.lightning.browser.download.PendingDownload
import acr.browser.lightning.ssl.SslCertificateInfo
import acr.browser.lightning.ssl.SslState
import acr.browser.lightning.utils.Option
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResult
import io.reactivex.Observable

interface TabModel {

    val id: Int

    // Navigation

    fun loadUrl(url: String)

    fun loadFromInitializer(tabInitializer: TabInitializer)

    fun goBack()

    fun canGoBack(): Boolean

    fun canGoBackChanges(): Observable<Boolean>

    fun goForward()

    fun canGoForward(): Boolean

    fun canGoForwardChanges(): Observable<Boolean>

    fun toggleDesktopAgent()

    fun reload()

    fun stopLoading()

    fun find(query: String)

    fun findNext()

    fun findPrevious()

    fun clearFindMatches()

    val findQuery: String?

    // Data

    val favicon: Bitmap?

    fun faviconChanges(): Observable<Option<Bitmap>>

    val themeColor: Int

    fun themeColorChanges(): Observable<Int>

    val url: String

    fun urlChanges(): Observable<String>

    val title: String

    fun titleChanges(): Observable<String>

    val sslCertificateInfo: SslCertificateInfo?

    val sslState: SslState

    fun sslChanges(): Observable<SslState>

    val loadingProgress: Int

    fun loadingProgress(): Observable<Int>

    // Lifecycle

    fun downloadRequests(): Observable<PendingDownload>

    fun fileChooserRequests(): Observable<Intent>

    fun handleFileChooserResult(activityResult: ActivityResult)

    fun showCustomViewRequests(): Observable<View>

    fun hideCustomViewRequests(): Observable<Unit>

    fun hideCustomView()

    fun createWindowRequests(): Observable<TabInitializer>

    fun closeWindowRequests(): Observable<Unit>

    var isForeground: Boolean

    fun destroy()

    fun freeze(): Bundle


}
