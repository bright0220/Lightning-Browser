package acr.browser.lightning.browser.download

/**
 * Created by anthonycr on 7/25/21.
 */
data class PendingDownload(
    val url: String,
    val userAgent: String?,
    val contentDisposition: String?,
    val mimeType: String?,
    val contentLength: Long
)
