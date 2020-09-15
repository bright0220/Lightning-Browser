package acr.browser.lightning._browser2.tab

import acr.browser.lightning.Capabilities
import acr.browser.lightning.isSupported
import acr.browser.lightning.log.Logger
import acr.browser.lightning.preference.UserPreferences
import acr.browser.lightning.preference.userAgent
import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.view.View
import android.webkit.CookieManager
import android.webkit.WebSettings
import android.webkit.WebView

/**
 * Created by anthonycr on 9/12/20.
 */
class WebViewFactory(
    private val activity: Activity,
    private val logger: Logger,
    private val userPreferences: UserPreferences
) {

    fun createWebView(isIncognito: Boolean): WebView = WebView(activity).apply {
        id = View.generateViewId()
        isFocusableInTouchMode = true
        isFocusable = true
        if (VERSION.SDK_INT < VERSION_CODES.M) {
            isAnimationCacheEnabled = false
            isAlwaysDrawnWithCacheEnabled = false
        }
        setBackgroundColor(Color.WHITE)

        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            importantForAutofill = View.IMPORTANT_FOR_AUTOFILL_YES
        }

        isScrollbarFadingEnabled = true
        isSaveEnabled = true
        setNetworkAvailable(true)

        settings.apply {
            mediaPlaybackRequiresUserGesture = true

            if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP && !isIncognito) {
                mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
            } else if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
                mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
            }

            if (!isIncognito || Capabilities.FULL_INCOGNITO.isSupported) {
                domStorageEnabled = true
                setAppCacheEnabled(true)
                databaseEnabled = true
                cacheMode = WebSettings.LOAD_DEFAULT
            } else {
                domStorageEnabled = false
                setAppCacheEnabled(false)
                databaseEnabled = false
                cacheMode = WebSettings.LOAD_NO_CACHE
            }

            setSupportZoom(true)
            builtInZoomControls = true
            displayZoomControls = false
            allowContentAccess = true
            allowFileAccess = true
            allowFileAccessFromFileURLs = false
            allowUniversalAccessFromFileURLs = false

            // TODO: remove?
            setAppCachePath(activity.getDir("appcache", 0).path)
            setGeolocationDatabasePath(activity.getDir("geolocation", 0).path)
        }

        updateForPreferences(userPreferences, isIncognito)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun WebView.updateForPreferences(
        userPreferences: UserPreferences,
        isIncognito: Boolean
    ) {

//        lightningWebClient.updatePreferences()
//
        val modifiesHeaders = userPreferences.doNotTrackEnabled
            || userPreferences.saveDataEnabled
            || userPreferences.removeIdentifyingHeadersEnabled
//
//        if (userPreferences.doNotTrackEnabled) {
//            requestHeaders[LightningView.HEADER_DNT] = "1"
//        } else {
//            requestHeaders.remove(LightningView.HEADER_DNT)
//        }
//
//        if (userPreferences.saveDataEnabled) {
//            requestHeaders[LightningView.HEADER_SAVEDATA] = "on"
//        } else {
//            requestHeaders.remove(LightningView.HEADER_SAVEDATA)
//        }
//
//        if (userPreferences.removeIdentifyingHeadersEnabled) {
//            requestHeaders[LightningView.HEADER_REQUESTED_WITH] = ""
//            requestHeaders[LightningView.HEADER_WAP_PROFILE] = ""
//        } else {
//            requestHeaders.remove(LightningView.HEADER_REQUESTED_WITH)
//            requestHeaders.remove(LightningView.HEADER_WAP_PROFILE)
//        }

        settings.defaultTextEncodingName = userPreferences.textEncoding
//        setColorMode(userPreferences.renderingMode)

        if (!isIncognito) {
            settings.setGeolocationEnabled(userPreferences.locationEnabled)
        } else {
            settings.setGeolocationEnabled(false)
        }

        settings.userAgentString = userPreferences.userAgent(activity.application)

        settings.saveFormData = userPreferences.savePasswordsEnabled && !isIncognito

        if (userPreferences.javaScriptEnabled) {
            settings.javaScriptEnabled = true
            settings.javaScriptCanOpenWindowsAutomatically = true
        } else {
            settings.javaScriptEnabled = false
            settings.javaScriptCanOpenWindowsAutomatically = false
        }

        if (userPreferences.textReflowEnabled) {
            settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.NARROW_COLUMNS
            try {
                settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING
            } catch (e: Exception) {
                // This shouldn't be necessary, but there are a number
                // of KitKat devices that crash trying to set this
                logger.log(TAG, "Problem setting LayoutAlgorithm to TEXT_AUTOSIZING")
            }
        } else {
            settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.NORMAL
        }

        settings.blockNetworkImage = userPreferences.blockImagesEnabled
        // Modifying headers causes SEGFAULTS, so disallow multi window if headers are enabled.
        settings.setSupportMultipleWindows(userPreferences.popupsEnabled && !modifiesHeaders)

        settings.useWideViewPort = userPreferences.useWideViewPortEnabled
        settings.loadWithOverviewMode = userPreferences.overviewModeEnabled
        settings.textZoom = when (userPreferences.textSize) {
            0 -> 200
            1 -> 150
            2 -> 125
            3 -> 100
            4 -> 75
            5 -> 50
            else -> throw IllegalArgumentException("Unsupported text size")
        }

        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(this,
                !userPreferences.blockThirdPartyCookiesEnabled)
        }
    }

    private companion object {
        private const val TAG = "WebViewFactory"

        private const val HEADER_REQUESTED_WITH = "X-Requested-With"
        private const val HEADER_WAP_PROFILE = "X-Wap-Profile"
        private const val HEADER_DNT = "DNT"
        private const val HEADER_SAVEDATA = "Save-Data"
    }

}
