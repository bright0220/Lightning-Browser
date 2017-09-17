package acr.browser.lightning.di

import acr.browser.lightning.BrowserApp
import acr.browser.lightning.database.bookmark.BookmarkDatabase
import acr.browser.lightning.database.bookmark.BookmarkModel
import acr.browser.lightning.database.downloads.DownloadsDatabase
import acr.browser.lightning.database.downloads.DownloadsModel
import acr.browser.lightning.database.history.HistoryDatabase
import acr.browser.lightning.database.history.HistoryModel
import acr.browser.lightning.download.DownloadHandler
import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import net.i2p.android.ui.I2PAndroidHelper
import javax.inject.Singleton

@Module
class AppModule(private val app: BrowserApp) {

    @Provides
    fun provideApplication(): Application = app

    @Provides
    fun provideContext(): Context = app.applicationContext

    @Provides
    @Singleton
    fun provideBookmarkModel(): BookmarkModel = BookmarkDatabase(app)

    @Provides
    @Singleton
    fun provideDownloadsModel(): DownloadsModel = DownloadsDatabase(app)

    @Provides
    @Singleton
    fun providesHistoryModel(): HistoryModel = HistoryDatabase(app)

    @Provides
    @Singleton
    fun provideDownloadHandler(): DownloadHandler = DownloadHandler()

    @Provides
    @Singleton
    fun provideI2PAndroidHelper(): I2PAndroidHelper = I2PAndroidHelper(app.applicationContext)

}
