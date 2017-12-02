/*
 * Copyright 2014 A.C.R. Development
 */
package acr.browser.lightning.settings.fragment

import acr.browser.lightning.BrowserApp
import acr.browser.lightning.R
import acr.browser.lightning.dialog.BrowserDialog
import acr.browser.lightning.preference.PreferenceManager
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.Gravity
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import javax.inject.Inject

class DisplaySettingsFragment : AbstractSettingsFragment() {

    private lateinit var themeOptions: Array<String>

    @Inject internal lateinit var preferenceManager: PreferenceManager

    override fun providePreferencesXmlResource() = R.xml.preference_display

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        BrowserApp.appComponent.inject(this)

        // preferences storage
        themeOptions = this.resources.getStringArray(R.array.themes)

        clickableDynamicPreference(
                preference = SETTINGS_THEME,
                summary = themeOptions[preferenceManager.useTheme],
                onClick = this::showThemePicker
        )

        clickablePreference(
                preference = SETTINGS_TEXTSIZE,
                onClick = this::showTextSizePicker
        )

        checkBoxPreference(
                preference = SETTINGS_HIDESTATUSBAR,
                isChecked = preferenceManager.hideStatusBarEnabled,
                onCheckChange = preferenceManager::setHideStatusBarEnabled
        )

        checkBoxPreference(
                preference = SETTINGS_FULLSCREEN,
                isChecked = preferenceManager.fullScreenEnabled,
                onCheckChange = preferenceManager::setFullScreenEnabled
        )

        checkBoxPreference(
                preference = SETTINGS_VIEWPORT,
                isChecked = preferenceManager.useWideViewportEnabled,
                onCheckChange = preferenceManager::setUseWideViewportEnabled
        )

        checkBoxPreference(
                preference = SETTINGS_OVERVIEWMODE,
                isChecked = preferenceManager.overviewModeEnabled,
                onCheckChange = preferenceManager::setOverviewModeEnabled
        )

        checkBoxPreference(
                preference = SETTINGS_REFLOW,
                isChecked = preferenceManager.textReflowEnabled,
                onCheckChange = preferenceManager::setTextReflowEnabled
        )

        checkBoxPreference(
                preference = SETTINGS_BLACK_STATUS,
                isChecked = preferenceManager.useBlackStatusBar,
                onCheckChange = preferenceManager::setUseBlackStatusBar
        )

        checkBoxPreference(
                preference = SETTINGS_DRAWERTABS,
                isChecked = preferenceManager.getShowTabsInDrawer(true),
                onCheckChange = preferenceManager::setShowTabsInDrawer
        )

        checkBoxPreference(
                preference = SETTINGS_SWAPTABS,
                isChecked = preferenceManager.bookmarksAndTabsSwapped,
                onCheckChange = preferenceManager::setBookmarkAndTabsSwapped
        )
    }

    private fun showTextSizePicker() {
        val maxValue = 5
        val dialog = AlertDialog.Builder(activity).apply {
            val layoutInflater = activity.layoutInflater
            val customView = (layoutInflater.inflate(R.layout.dialog_seek_bar, null) as LinearLayout).apply {
                val text = TextView(activity).apply {
                    setText(R.string.untitled)
                    layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
                    gravity = Gravity.CENTER_HORIZONTAL
                }
                addView(text)
                findViewById<SeekBar>(R.id.text_size_seekbar).apply {
                    setOnSeekBarChangeListener(TextSeekBarListener(text))
                    max = maxValue
                    progress = maxValue - preferenceManager.textSize
                }
            }
            setView(customView)
            setTitle(R.string.title_text_size)
            setPositiveButton(android.R.string.ok) { _, _ ->
                val seekBar = customView.findViewById<SeekBar>(R.id.text_size_seekbar)
                preferenceManager.textSize = maxValue - seekBar.progress
            }
        }.show()

        BrowserDialog.setDialogSize(activity, dialog)
    }

    private fun showThemePicker(summaryUpdater: SummaryUpdater) {
        val currentTheme = preferenceManager.useTheme

        val dialog = AlertDialog.Builder(activity).apply {
            setTitle(resources.getString(R.string.theme))
            setSingleChoiceItems(themeOptions, currentTheme) { _, which ->
                preferenceManager.useTheme = which
                if (which < themeOptions.size) {
                    summaryUpdater.updateSummary(themeOptions[which])
                }
            }
            setPositiveButton(resources.getString(R.string.action_ok)) { _, _ ->
                if (currentTheme != preferenceManager.useTheme) {
                    activity.onBackPressed()
                }
            }
            setOnCancelListener {
                if (currentTheme != preferenceManager.useTheme) {
                    activity.onBackPressed()
                }
            }
        }.show()

        BrowserDialog.setDialogSize(activity, dialog)
    }

    private class TextSeekBarListener(
            private val sampleText: TextView
    ) : SeekBar.OnSeekBarChangeListener {

        override fun onProgressChanged(view: SeekBar, size: Int, user: Boolean) {
            this.sampleText.textSize = getTextSize(size)
        }

        override fun onStartTrackingTouch(arg0: SeekBar) {}

        override fun onStopTrackingTouch(arg0: SeekBar) {}

    }

    companion object {

        private val SETTINGS_HIDESTATUSBAR = "fullScreenOption"
        private val SETTINGS_FULLSCREEN = "fullscreen"
        private val SETTINGS_VIEWPORT = "wideViewPort"
        private val SETTINGS_OVERVIEWMODE = "overViewMode"
        private val SETTINGS_REFLOW = "text_reflow"
        private val SETTINGS_THEME = "app_theme"
        private val SETTINGS_TEXTSIZE = "text_size"
        private val SETTINGS_DRAWERTABS = "cb_drawertabs"
        private val SETTINGS_SWAPTABS = "cb_swapdrawers"
        private val SETTINGS_BLACK_STATUS = "black_status_bar"

        private const val XX_LARGE = 30.0f
        private const val X_LARGE = 26.0f
        private const val LARGE = 22.0f
        private const val MEDIUM = 18.0f
        private const val SMALL = 14.0f
        private const val X_SMALL = 10.0f

        private fun getTextSize(size: Int): Float = when (size) {
            0 -> X_SMALL
            1 -> SMALL
            2 -> MEDIUM
            3 -> LARGE
            4 -> X_LARGE
            5 -> XX_LARGE
            else -> MEDIUM
        }
    }
}
