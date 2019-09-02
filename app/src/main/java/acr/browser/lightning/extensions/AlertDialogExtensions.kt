package acr.browser.lightning.extensions

import acr.browser.lightning.dialog.BrowserDialog
import androidx.appcompat.app.AlertDialog

/**
 * Show single choice items.
 *
 * @param items A list of items and their user readable string description.
 * @param checkedItem The item that will be checked when the dialog is displayed.
 * @param onClick Called when an item is clicked. The item clicked is provided.
 */
inline fun <T> AlertDialog.Builder.withSingleChoiceItems(
    items: List<Pair<T, String>>,
    checkedItem: T,
    crossinline onClick: (T) -> Unit
) {
    val checkedIndex = items.map(Pair<T, String>::first).indexOf(checkedItem)
    val titles = items.map(Pair<T, String>::second).toTypedArray()
    setSingleChoiceItems(titles, checkedIndex) { _, which ->
        onClick(items[which].first)
    }
}

/**
 * Ensures that the dialog is appropriately sized and displays it.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun AlertDialog.Builder.resizeAndShow() = BrowserDialog.setDialogSize(context, this.show())
