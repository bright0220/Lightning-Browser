package acr.browser.lightning.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.TypedValue;

import acr.browser.lightning.R;

public class ThemeUtils {

    private static final TypedValue sTypedValue = new TypedValue();

    public static int getPrimaryColor(@NonNull Context context) {
        return getColor(context, R.attr.colorPrimary);
    }

    public static int getPrimaryColorDark(@NonNull Context context) {
        return getColor(context, R.attr.colorPrimaryDark);
    }

    public static int getAccentColor(@NonNull Context context) {
        return getColor(context, R.attr.colorAccent);
    }

    @TargetApi(21)
    public static int getStatusBarColor(@NonNull Context context) {
        return getColor(context, android.R.attr.statusBarColor);
    }

    public static int getColor(@NonNull Context context, @AttrRes int resource) {
        TypedArray a = context.obtainStyledAttributes(sTypedValue.data, new int[]{resource});
        int color = a.getColor(0, 0);
        a.recycle();
        return color;
    }

    @ColorInt
    public static int getIconLightThemeColor(@NonNull Context context) {
        return ContextCompat.getColor(context, R.color.icon_light_theme);
    }

    @ColorInt
    public static int getIconDarkThemeColor(@NonNull Context context) {
        return ContextCompat.getColor(context, R.color.icon_dark_theme);
    }

    @ColorInt
    public static int getIconThemeColor(@NonNull Context context, boolean dark) {
        return (dark) ? getIconDarkThemeColor(context) : getIconLightThemeColor(context);
    }

    @NonNull
    private static Drawable getVectorDrawable(@NonNull Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);

        Preconditions.checkNonNull(drawable);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }
        return drawable;
    }

    // http://stackoverflow.com/a/38244327/1499541
    @NonNull
    private static Bitmap getBitmapFromVectorDrawable(@NonNull Context context, int drawableId) {
        Drawable drawable = getVectorDrawable(context, drawableId);

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
            Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    @NonNull
    public static Bitmap getThemedBitmap(@NonNull Context context, @DrawableRes int res, boolean dark) {
        int color = dark ? getIconDarkThemeColor(context) : getIconLightThemeColor(context);
        Bitmap sourceBitmap = getBitmapFromVectorDrawable(context, res);
        Bitmap resultBitmap = Bitmap.createBitmap(sourceBitmap.getWidth(), sourceBitmap.getHeight(),
            Bitmap.Config.ARGB_8888);
        Paint p = new Paint();
        ColorFilter filter = new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN);
        p.setColorFilter(filter);
        Canvas canvas = new Canvas(resultBitmap);
        canvas.drawBitmap(sourceBitmap, 0, 0, p);
        sourceBitmap.recycle();
        return resultBitmap;
    }

    @NonNull
    public static Drawable getThemedDrawable(@NonNull Context context, @DrawableRes int res, boolean dark) {
        int color = dark ? getIconDarkThemeColor(context) : getIconLightThemeColor(context);
        final Drawable drawable = getVectorDrawable(context, res);
        drawable.mutate();
        drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        return drawable;
    }

    public static int getThemedTextHintColor(boolean dark) {
        return 0x80ffffff & (dark ? Color.WHITE : Color.BLACK);
    }

    public static int getTextColor(@NonNull Context context) {
        return getColor(context, android.R.attr.editTextColor);
    }
}
