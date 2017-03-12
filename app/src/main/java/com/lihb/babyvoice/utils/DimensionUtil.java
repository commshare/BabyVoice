package com.lihb.babyvoice.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

public class DimensionUtil {

    public static int dipToPx(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int pxToDip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int spToPx(Context context, float spValue) {
        final float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * scale + 0.5f);
    }

    public static int targetDensity(Context context) {
        return context.getResources().getDisplayMetrics().densityDpi;
    }
    /**
     * Gets the width of the display, in pixels.
     * <p>
     * Note that this value should not be used for computing layouts, since a
     * device will typically have screen decoration (such as a status bar) along
     * the edges of the display that reduce the amount of application space
     * available from the size returned here. Layouts should instead use the
     * window size.
     * <p>
     * The size is adjusted based on the current rotation of the display.
     * <p>
     * The size returned by this method does not necessarily represent the
     * actual raw size (native resolution) of the display. The returned size may
     * be adjusted to exclude certain system decoration elements that are always
     * visible. It may also be scaled to provide compatibility with older
     * applications that were originally designed for smaller displays.
     *
     * @return Screen width in pixels.
     */
    public static int getScreenWidth(Context context) {
        return getScreenSize(context, null).x;
    }

    /**
     * Gets the height of the display, in pixels.
     * <p>
     * Note that this value should not be used for computing layouts, since a
     * device will typically have screen decoration (such as a status bar) along
     * the edges of the display that reduce the amount of application space
     * available from the size returned here. Layouts should instead use the
     * window size.
     * <p>
     * The size is adjusted based on the current rotation of the display.
     * <p>
     * The size returned by this method does not necessarily represent the
     * actual raw size (native resolution) of the display. The returned size may
     * be adjusted to exclude certain system decoration elements that are always
     * visible. It may also be scaled to provide compatibility with older
     * applications that were originally designed for smaller displays.
     *
     * @return Screen height in pixels.
     */
    public static int getScreenHeight(Context context) {
        return getScreenSize(context, null).y;
    }

    /**
     * Gets the size of the display, in pixels.
     * <p>
     * Note that this value should not be used for computing layouts, since a
     * device will typically have screen decoration (such as a status bar) along
     * the edges of the display that reduce the amount of application space
     * available from the size returned here. Layouts should instead use the
     * window size.
     * <p>
     * The size is adjusted based on the current rotation of the display.
     * <p>
     * The size returned by this method does not necessarily represent the
     * actual raw size (native resolution) of the display. The returned size may
     * be adjusted to exclude certain system decoration elements that are always
     * visible. It may also be scaled to provide compatibility with older
     * applications that were originally designed for smaller displays.
     *
     * @param outSize null-ok. If it is null, will create a Point getInstance inside,
     *                otherwise use it to fill the output. NOTE if it is not null,
     *                it will be the returned value.
     * @return Screen size in pixels, the x is the width, the y is the height.
     */
    @SuppressWarnings("deprecation")
    @TargetApi(13)
    public static Point getScreenSize(Context context, Point outSize) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point ret = outSize == null ? new Point() : outSize;
        final Display defaultDisplay = wm.getDefaultDisplay();
        if (Build.VERSION.SDK_INT >= 13) {
            defaultDisplay.getSize(ret);
        } else {
            ret.x = defaultDisplay.getWidth();
            ret.y = defaultDisplay.getHeight();
        }
        return ret;
    }

    public static int getDpi(Context context) {
        return context.getResources().getDisplayMetrics().densityDpi;
    }

    public static float getScreenRatio(Context context) {
        Point screenSize = new Point();
        getScreenSize(context, screenSize);
        float screenRatio = screenSize.x > screenSize.y ? (float) screenSize.x / screenSize.y : (float) screenSize.y / screenSize.x;
        return screenRatio;
    }

    public static DisplayMetrics getDisplayMetrics(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        return dm;
    }

    public static boolean hitCheck(View v, int x, int y) {
        if (v.getVisibility() == View.VISIBLE) {
            Rect hit = new Rect();
            v.getHitRect(hit);
            return hit.contains(x, y);
        }
        return false;
    }

    public static boolean hitCheckInWindow(View v, int x, int y) {
        if (v.getVisibility() == View.VISIBLE) {
            Rect hit = getRectInWindow(v);
            return hit.contains(x, y);
        }
        return false;
    }

    public static Rect getRectInWindow(View anchor) {
        final int[] location = new int[2];
        anchor.getLocationInWindow(location);
        return new Rect(location[0], location[1], location[0] + anchor.getWidth(), location[1] + anchor.getHeight());
    }
}
