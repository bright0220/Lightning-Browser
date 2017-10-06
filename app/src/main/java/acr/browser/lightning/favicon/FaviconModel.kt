package acr.browser.lightning.favicon

import acr.browser.lightning.R
import acr.browser.lightning.utils.*
import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.support.annotation.ColorInt
import android.support.annotation.WorkerThread
import android.text.TextUtils
import android.util.Log
import android.util.LruCache
import com.anthonycr.bonsai.Completable
import com.anthonycr.bonsai.CompletableAction
import com.anthonycr.bonsai.Single
import com.anthonycr.bonsai.SingleAction
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Reactive model that can fetch favicons
 * from URLs and also cache them.
 */
@Singleton
class FaviconModel @Inject constructor(private val application: Application) {

    private val loaderOptions = BitmapFactory.Options()
    private val bookmarkIconSize = application.resources.getDimensionPixelSize(R.dimen.bookmark_item_icon_size)
    private val faviconCache = object : LruCache<String, Bitmap>(FileUtils.megabytesToBytes(1).toInt()) {
        override fun sizeOf(key: String, value: Bitmap) = value.byteCount
    }

    /**
     * Retrieves a favicon from the memory cache.Bitmap may not be present if no bitmap has been
     * added for the URL or if it has been evicted from the memory cache.
     *
     * @param url the URL to retrieve the bitmap for.
     * @return the bitmap associated with the URL, may be null.
     */
    private fun getFaviconFromMemCache(url: String): Bitmap? {
        Preconditions.checkNonNull(url)
        synchronized(faviconCache) {
            return faviconCache.get(url)
        }
    }

    fun getDefaultBitmapForString(title: String?): Bitmap {
        val firstTitleCharacter = if (!TextUtils.isEmpty(title)) title!![0] else '?'

        @ColorInt val defaultFaviconColor = DrawableUtils.characterToColorHash(firstTitleCharacter, application)

        return DrawableUtils.getRoundedLetterImage(firstTitleCharacter,
                bookmarkIconSize,
                bookmarkIconSize,
                defaultFaviconColor)
    }

    /**
     * Adds a bitmap to the memory cache for the given URL.
     *
     * @param url    the URL to map the bitmap to.
     * @param bitmap the bitmap to store.
     */
    private fun addFaviconToMemCache(url: String, bitmap: Bitmap) {
        Preconditions.checkNonNull(url)
        Preconditions.checkNonNull(bitmap)
        synchronized(faviconCache) {
            faviconCache.put(url, bitmap)
        }
    }

    /**
     * Retrieves the favicon for a URL, may be from network or cache.
     *
     * @param url   The URL that we should retrieve the favicon for.
     * @param title The title for the web page.
     */
    fun faviconForUrl(url: String,
                      title: String): Single<Bitmap> = Single.create(SingleAction { subscriber ->
        val uri = safeUri(url)

        if (uri == null) {

            val newFavicon = Utils.padFavicon(getDefaultBitmapForString(title))

            subscriber.onItem(newFavicon)
            subscriber.onComplete()

            return@SingleAction
        }

        val faviconCacheFile = getFaviconCacheFile(application, uri)

        var favicon = getFaviconFromMemCache(url)

        if (faviconCacheFile.exists() && favicon == null) {
            favicon = BitmapFactory.decodeFile(faviconCacheFile.path, loaderOptions)

            if (favicon != null) {
                addFaviconToMemCache(url, favicon)
            }
        }

        if (favicon != null) {
            val newFavicon = Utils.padFavicon(favicon)

            subscriber.onItem(newFavicon)
            subscriber.onComplete()

            return@SingleAction
        }

        favicon = getDefaultBitmapForString(title)

        val newFavicon = Utils.padFavicon(favicon)

        subscriber.onItem(newFavicon)
        subscriber.onComplete()
    })

    /**
     * Caches a favicon for a particular URL.
     *
     * @param favicon the favicon to cache.
     * @param url     the URL to cache the favicon for.
     * @return an observable that notifies the consumer when it is complete.
     */
    fun cacheFaviconForUrl(favicon: Bitmap,
                           url: String): Completable =
            Completable.create(CompletableAction { subscriber ->
                val uri = safeUri(url)

                if (uri == null) {
                    subscriber.onComplete()
                    return@CompletableAction
                }

                Log.d(TAG, "Caching icon for " + uri.host)
                val image = getFaviconCacheFile(application, uri)
                FileOutputStream(image).safeUse {
                    favicon.compress(Bitmap.CompressFormat.PNG, 100, it)
                    it.flush()
                }
            })

    companion object {

        private const val TAG = "FaviconModel"

        /**
         * Creates the cache file for the favicon image. File name will be in the form of "hash of URI host".png
         *
         * @param app the context needed to retrieve the
         * cache directory.
         * @param uri the URI to use as a unique identifier.
         * @return a valid cache file.
         */
        @WorkerThread
        fun getFaviconCacheFile(app: Application, uri: Uri): File {
            requireUriSafe(uri)

            val hash = uri.host.hashCode().toString()

            return File(app.cacheDir, hash + ".png")
        }
    }

}
