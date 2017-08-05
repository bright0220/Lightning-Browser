package acr.browser.lightning.animation

import android.support.annotation.DrawableRes
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.ImageView

/**
 * Animation specific helper code.
 */
object AnimationUtils {

    /**
     * Creates an animation that rotates an [ImageView] around the Y axis by 180 degrees and changes
     * the image resource shown when the view is rotated 90 degrees to the user.
     *
     * @param imageView   the view to rotate.
     * @param drawableRes the drawable to set when the view is rotated by 90 degrees.
     * @return an animation that will change the image shown by the view.
     */
    @JvmStatic
    fun createRotationTransitionAnimation(imageView: ImageView,
                                          @DrawableRes drawableRes: Int): Animation {
        val animation = object : Animation() {

            private var mSetFinalDrawable: Boolean = false

            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                if (interpolatedTime < 0.5f) {
                    imageView.rotationY = 90f * interpolatedTime * 2f
                } else {
                    if (!mSetFinalDrawable) {
                        mSetFinalDrawable = true
                        imageView.setImageResource(drawableRes)
                    }
                    imageView.rotationY = -90 + 90f * (interpolatedTime - 0.5f) * 2f
                }
            }
        }

        animation.duration = 300
        animation.interpolator = AccelerateDecelerateInterpolator()

        return animation
    }

}
