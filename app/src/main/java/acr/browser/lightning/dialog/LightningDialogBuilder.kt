package acr.browser.lightning.dialog

import acr.browser.lightning.BrowserApp
import acr.browser.lightning.MainActivity
import acr.browser.lightning.R
import acr.browser.lightning.constant.FOLDER
import acr.browser.lightning.constant.HTTP
import acr.browser.lightning.controller.UIController
import acr.browser.lightning.database.HistoryItem
import acr.browser.lightning.database.bookmark.BookmarkRepository
import acr.browser.lightning.database.downloads.DownloadsRepository
import acr.browser.lightning.database.history.HistoryRepository
import acr.browser.lightning.download.DownloadHandler
import acr.browser.lightning.html.bookmark.BookmarkPage
import acr.browser.lightning.preference.UserPreferences
import acr.browser.lightning.utils.IntentUtils
import acr.browser.lightning.utils.UrlUtils
import android.app.Activity
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import androidx.core.net.toUri
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject
import javax.inject.Named

/**
 * A builder of various dialogs.
 */
class LightningDialogBuilder @Inject constructor(
        private val bookmarkManager: BookmarkRepository,
        private val downloadsModel: DownloadsRepository,
        private val historyModel: HistoryRepository,
        private val userPreferences: UserPreferences,
        private val downloadHandler: DownloadHandler,
        @Named("database") private val databaseScheduler: Scheduler
) {

    enum class NewTab {
        FOREGROUND,
        BACKGROUND,
        INCOGNITO
    }

    /**
     * Show the appropriated dialog for the long pressed link. It means that we try to understand
     * if the link is relative to a bookmark or is just a folder.
     *
     * @param activity used to show the dialog
     * @param url      the long pressed url
     */
    fun showLongPressedDialogForBookmarkUrl(activity: Activity,
                                            uiController: UIController,
                                            url: String) {
        val item: HistoryItem
        if (UrlUtils.isBookmarkUrl(url)) {
            // TODO hacky, make a better bookmark mechanism in the future
            val uri = url.toUri()
            val filename = uri.lastPathSegment
            val folderTitle = filename.substring(0, filename.length - BookmarkPage.FILENAME.length - 1)
            item = HistoryItem()
            item.setIsFolder(true)
            item.setTitle(folderTitle)
            item.imageId = R.drawable.ic_folder
            item.setUrl(FOLDER + folderTitle)
            showBookmarkFolderLongPressedDialog(activity, uiController, item)
        } else {
            bookmarkManager.findBookmarkForUrl(url)
                    .subscribeOn(databaseScheduler)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { historyItem ->
                        // TODO: 6/14/17 figure out solution to case where slashes get appended to root urls causing the item to not exist
                        showLongPressedDialogForBookmarkUrl(activity, uiController, historyItem)
                    }
        }
    }

    fun showLongPressedDialogForBookmarkUrl(activity: Activity,
                                            uiController: UIController,
                                            item: HistoryItem) =
            BrowserDialog.show(activity, R.string.action_bookmarks,
                    DialogItem(R.string.dialog_open_new_tab) {
                        uiController.handleNewTab(NewTab.FOREGROUND, item.url)
                    },
                    DialogItem(R.string.dialog_open_background_tab) {
                        uiController.handleNewTab(NewTab.BACKGROUND, item.url)
                    },
                    DialogItem(R.string.dialog_open_incognito_tab, activity is MainActivity) {
                        uiController.handleNewTab(NewTab.INCOGNITO, item.url)
                    },
                    DialogItem(R.string.action_share) {
                        IntentUtils(activity).shareUrl(item.url, item.title)
                    },
                    DialogItem(R.string.dialog_copy_link) {
                        BrowserApp.copyToClipboard(activity, item.url)
                    },
                    DialogItem(R.string.dialog_remove_bookmark) {
                        bookmarkManager.deleteBookmark(item)
                                .subscribeOn(databaseScheduler)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe { success ->
                                    if (success) {
                                        uiController.handleBookmarkDeleted(item)
                                    }
                                }
                    },
                    DialogItem(R.string.dialog_edit_bookmark) {
                        showEditBookmarkDialog(activity, uiController, item)
                    })

    /**
     * Show the appropriated dialog for the long pressed link.
     *
     * @param activity used to show the dialog
     * @param url      the long pressed url
     */
    fun showLongPressedDialogForDownloadUrl(activity: Activity,
                                            uiController: UIController,
                                            url: String) =// TODO allow individual downloads to be deleted.
            BrowserDialog.show(activity, R.string.action_downloads,
                    DialogItem(R.string.dialog_delete_all_downloads) {
                        downloadsModel.deleteAllDownloads()
                                .subscribeOn(databaseScheduler)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(uiController::handleDownloadDeleted)
                    })

    private fun showEditBookmarkDialog(activity: Activity,
                                       uiController: UIController,
                                       item: HistoryItem) {
        val editBookmarkDialog = AlertDialog.Builder(activity)
        editBookmarkDialog.setTitle(R.string.title_edit_bookmark)
        val dialogLayout = View.inflate(activity, R.layout.dialog_edit_bookmark, null)
        val getTitle = dialogLayout.findViewById<EditText>(R.id.bookmark_title)
        getTitle.setText(item.title)
        val getUrl = dialogLayout.findViewById<EditText>(R.id.bookmark_url)
        getUrl.setText(item.url)
        val getFolder = dialogLayout.findViewById<AutoCompleteTextView>(R.id.bookmark_folder)
        getFolder.setHint(R.string.folder)
        getFolder.setText(item.folder)

        bookmarkManager.getFolderNames()
                .subscribeOn(databaseScheduler)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { folders ->
                    requireNotNull(folders)
                    val suggestionsAdapter = ArrayAdapter(activity,
                            android.R.layout.simple_dropdown_item_1line, folders)
                    getFolder.threshold = 1
                    getFolder.setAdapter(suggestionsAdapter)
                    editBookmarkDialog.setView(dialogLayout)
                    editBookmarkDialog.setPositiveButton(activity.getString(R.string.action_ok)) { _, _ ->
                        val editedItem = HistoryItem()
                        editedItem.setTitle(getTitle.text.toString())
                        editedItem.setUrl(getUrl.text.toString())
                        editedItem.setUrl(getUrl.text.toString())
                        editedItem.setFolder(getFolder.text.toString())
                        bookmarkManager.editBookmark(item, editedItem)
                                .subscribeOn(databaseScheduler)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(uiController::handleBookmarksChange)
                    }
                    val dialog = editBookmarkDialog.show()
                    BrowserDialog.setDialogSize(activity, dialog)
                }
    }

    fun showBookmarkFolderLongPressedDialog(activity: Activity,
                                            uiController: UIController,
                                            item: HistoryItem) =
            BrowserDialog.show(activity, R.string.action_folder,
                    DialogItem(R.string.dialog_rename_folder) {
                        showRenameFolderDialog(activity, uiController, item)
                    },
                    DialogItem(R.string.dialog_remove_folder) {
                        bookmarkManager.deleteFolder(item.title)
                                .subscribeOn(databaseScheduler)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe {
                                    uiController.handleBookmarkDeleted(item)
                                }
                    })

    private fun showRenameFolderDialog(activity: Activity,
                                       uiController: UIController,
                                       item: HistoryItem) =
            BrowserDialog.showEditText(activity,
                    R.string.title_rename_folder,
                    R.string.hint_title,
                    item.title,
                    R.string.action_ok) { text ->
                if (!TextUtils.isEmpty(text)) {
                    val oldTitle = item.title
                    bookmarkManager.renameFolder(oldTitle, text)
                            .subscribeOn(databaseScheduler)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(uiController::handleBookmarksChange)
                }
            }

    fun showLongPressedHistoryLinkDialog(activity: Activity,
                                         uiController: UIController,
                                         url: String) =
            BrowserDialog.show(activity, R.string.action_history,
                    DialogItem(R.string.dialog_open_new_tab) {
                        uiController.handleNewTab(NewTab.FOREGROUND, url)
                    },
                    DialogItem(R.string.dialog_open_background_tab) {
                        uiController.handleNewTab(NewTab.BACKGROUND, url)
                    },
                    DialogItem(R.string.dialog_open_incognito_tab, activity is MainActivity) {
                        uiController.handleNewTab(NewTab.INCOGNITO, url)
                    },
                    DialogItem(R.string.action_share) {
                        IntentUtils(activity).shareUrl(url, null)
                    },
                    DialogItem(R.string.dialog_copy_link) {
                        BrowserApp.copyToClipboard(activity, url)
                    },
                    DialogItem(R.string.dialog_remove_from_history) {
                        historyModel.deleteHistoryItem(url)
                                .subscribeOn(databaseScheduler)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(uiController::handleHistoryChange)
                    })

    // TODO There should be a way in which we do not need an activity reference to dowload a file
    fun showLongPressImageDialog(activity: Activity,
                                 uiController: UIController,
                                 url: String,
                                 userAgent: String) =
            BrowserDialog.show(activity, url.replace(HTTP, ""),
                    DialogItem(R.string.dialog_open_new_tab) {
                        uiController.handleNewTab(NewTab.FOREGROUND, url)
                    },
                    DialogItem(R.string.dialog_open_background_tab) {
                        uiController.handleNewTab(NewTab.BACKGROUND, url)
                    },
                    DialogItem(R.string.dialog_open_incognito_tab, activity is MainActivity) {
                        uiController.handleNewTab(NewTab.INCOGNITO, url)
                    },
                    DialogItem(R.string.action_share) {
                        IntentUtils(activity).shareUrl(url, null)
                    },
                    DialogItem(R.string.dialog_copy_link) {
                        BrowserApp.copyToClipboard(activity, url)
                    },
                    DialogItem(R.string.dialog_download_image) {
                        downloadHandler.onDownloadStart(activity, userPreferences, url, userAgent, "attachment", null, "")
                    })

    fun showLongPressLinkDialog(activity: Activity,
                                uiController: UIController,
                                url: String) = BrowserDialog.show(activity, url,
            DialogItem(R.string.dialog_open_new_tab) {
                uiController.handleNewTab(NewTab.FOREGROUND, url)
            },
            DialogItem(R.string.dialog_open_background_tab) {
                uiController.handleNewTab(NewTab.BACKGROUND, url)
            },
            DialogItem(R.string.dialog_open_incognito_tab, activity is MainActivity) {
                uiController.handleNewTab(NewTab.INCOGNITO, url)
            },
            DialogItem(R.string.action_share) {
                IntentUtils(activity).shareUrl(url, null)
            },
            DialogItem(R.string.dialog_copy_link) {
                BrowserApp.copyToClipboard(activity, url)
            })

}
