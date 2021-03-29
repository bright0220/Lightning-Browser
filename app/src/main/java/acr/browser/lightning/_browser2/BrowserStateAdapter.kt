package acr.browser.lightning._browser2

import acr.browser.lightning.ssl.SslCertificateInfo
import acr.browser.lightning.ssl.showSslDialog
import targetUrl.LongPress

/**
 * Created by anthonycr on 9/16/20.
 */
class BrowserStateAdapter(private val browserActivity: BrowserActivity) : BrowserContract.View {

    private var currentState: BrowserViewState? = null

    override fun renderState(viewState: BrowserViewState) {
        val (
            displayUrl,
            sslState,
            isRefresh,
            progress,
            tabs,
            isForwardEnabled,
            isBackEnabled,
            bookmarks,
            isBookmarked,
            isBookmarkEnabled,
            isRootFolder,
            findInPage
        ) = viewState

        browserActivity.renderState(
            PartialBrowserViewState(
                displayUrl = displayUrl.takeIf { it != currentState?.displayUrl },
                sslState = sslState.takeIf { it != currentState?.sslState },
                isRefresh = isRefresh.takeIf { it != currentState?.isRefresh },
                progress = progress.takeIf { it != currentState?.progress },
                tabs = tabs.takeIf { it != currentState?.tabs },
                isForwardEnabled = isForwardEnabled.takeIf { it != currentState?.isForwardEnabled },
                isBackEnabled = isBackEnabled.takeIf { it != currentState?.isBackEnabled },
                bookmarks = bookmarks.takeIf { it != currentState?.bookmarks },
                isBookmarked = isBookmarked.takeIf { it != currentState?.isBookmarked },
                isBookmarkEnabled = isBookmarkEnabled.takeIf { it != currentState?.isBookmarkEnabled },
                isRootFolder = isRootFolder.takeIf { it != currentState?.isRootFolder },
                findInPage = findInPage.takeIf { it != currentState?.findInPage }
            )
        )

        currentState = viewState
    }

    override fun showAddBookmarkDialog(title: String, url: String, folders: List<String>) {
        browserActivity.showAddBookmarkDialog(title, url, folders)
    }

    override fun showEditBookmarkDialog(title: String, url: String, folder: String, folders: List<String>) {
        browserActivity.showEditBookmarkDialog(title, url, folder, folders)
    }

    override fun showEditFolderDialog(title: String) {
        browserActivity.showEditFolderDialog(title)
    }

    override fun showFindInPageDialog() {
        browserActivity.showFindInPageDialog()
    }

    override fun showLinkLongPressDialog(longPress: LongPress) {
        browserActivity.showLinkLongPressDialog(longPress)
    }

    override fun showImageLongPressDialog(longPress: LongPress) {
        browserActivity.showImageLongPressDialog(longPress)
    }

    override fun showSslDialog(sslCertificateInfo: SslCertificateInfo) {
        browserActivity.showSslDialog(sslCertificateInfo)
    }

    override fun openBookmarkDrawer() {
        browserActivity.openBookmarkDrawer()
    }

    override fun openTabDrawer() {
        browserActivity.openTabDrawer()
    }

}
