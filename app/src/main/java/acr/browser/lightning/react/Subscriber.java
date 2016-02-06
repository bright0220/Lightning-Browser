package acr.browser.lightning.react;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public interface Subscriber<T> extends Subscription {

    /**
     * Called immediately upon subscribing
     * and before the Observable begins
     * emitting items. This should not be
     * called by the creator of the Observable
     * and is rather called internally by the
     * Observable class itself.
     */
    void onStart();

    /**
     * Called when the observable
     * runs into an error that will
     * cause it to abort and not finish.
     * Receiving this callback means that
     * the observable is dead and no
     * {@link #onComplete()} or {@link #onNext(Object)}
     * callbacks will be called.
     *
     * @param throwable an optional throwable that could
     *                  be sent.
     */
    void onError(@NonNull Throwable throwable);

    /**
     * Called when the Observer emits an
     * item. It can be called multiple times.
     * It cannot be called after onComplete
     * has been called.
     *
     * @param item the item that has been emitted,
     *             can be null.
     */
    void onNext(@Nullable T item);

    /**
     * This method is called when the observer is
     * finished sending the subscriber events. It
     * is guaranteed that no other methods will be
     * called on the OnSubscribe after this method
     * has been called.
     */
    void onComplete();
}
