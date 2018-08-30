package acr.browser.lightning.di

import acr.browser.lightning.BrowserApp
import android.app.Application
import android.app.DownloadManager
import android.app.NotificationManager
import android.content.ClipboardManager
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.ShortcutManager
import android.net.ConnectivityManager
import android.os.Build
import android.support.annotation.RequiresApi
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import dagger.Module
import dagger.Provides
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import net.i2p.android.ui.I2PAndroidHelper
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
class AppModule(private val browserApp: BrowserApp) {

    @Provides
    fun provideApplication(): Application = browserApp

    @Provides
    fun provideContext(): Context = browserApp.applicationContext

    @Provides
    @Named(Name.SETTINGS)
    fun provideDebugPreferences(): SharedPreferences = browserApp.getSharedPreferences("settings", 0)

    @Provides
    @Named(Name.DEVELOPER_SETTINGS)
    fun provideUserPreferences(): SharedPreferences = browserApp.getSharedPreferences("developer_settings", 0)

    @Provides
    fun providesClipboardManager() = browserApp.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    @Provides
    fun providesInputMethodManager() = browserApp.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    @Provides
    fun providesDownloadManager() = browserApp.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

    @Provides
    fun providesConnectivityManager() = browserApp.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    @Provides
    fun providesNotificationManager() = browserApp.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    @Provides
    fun providesWindowManager() = browserApp.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    @RequiresApi(Build.VERSION_CODES.N_MR1)
    @Provides
    fun providesShortcutManager() = browserApp.getSystemService(Context.SHORTCUT_SERVICE) as ShortcutManager

    @Provides
    @Named("database")
    @Singleton
    fun providesIoThread(): Scheduler = Schedulers.from(Executors.newSingleThreadExecutor())

    @Provides
    @Named("disk")
    @Singleton
    fun providesDiskThread(): Scheduler = Schedulers.from(Executors.newSingleThreadExecutor())

    @Provides
    @Named("network")
    @Singleton
    fun providesNetworkThread(): Scheduler = Schedulers.from(ThreadPoolExecutor(0, 4, 60, TimeUnit.SECONDS, LinkedBlockingDeque()))

    @Provides
    @Singleton
    fun provideI2PAndroidHelper(): I2PAndroidHelper = I2PAndroidHelper(browserApp)

}

object Name {
    const val DEVELOPER_SETTINGS = "developer_settings"
    const val SETTINGS = "settings"

    const val DATABASE = "database"
    const val DISK = "disk"
    const val NETWORK = "network"
}
