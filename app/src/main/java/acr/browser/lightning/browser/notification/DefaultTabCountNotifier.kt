package acr.browser.lightning.browser.notification

/**
 * Created by anthonycr on 7/27/22.
 */
object DefaultTabCountNotifier : TabCountNotifier {
    override fun notifyTabCountChange(total: Int) = Unit
}
