package com.lihb.babyvoice.utils;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.IntDef;
import android.support.annotation.StringRes;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.lihb.babyvoice.BabyVoiceApp;
import com.lihb.babyvoice.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 统一了UI风格的Toast
 * <p/>
 * Created by caijw on 2015/7/2.
 */
public final class CommonToast {

    final static private Handler mainHandler = new Handler(Looper.getMainLooper());
    final static public long EXTEND_LONG = 8000;

    private CommonToast() {
        throw new AssertionError("Should not create instance of CommonToast.");
    }

    public static void showToast(final CharSequence text, final @Duration int duration) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                makeText(text, duration).show();
            }
        });
    }

    public static void showToast(@StringRes final int resId, final @Duration int duration) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                makeText(resId, duration).show();
            }
        });
    }

    public static void showShortToast(final CharSequence text) {
        showToast(text, Toast.LENGTH_SHORT);
    }

    public static void showShortToast(@StringRes final int resId) {
        showToast(resId, Toast.LENGTH_SHORT);
    }

    public static void showLongToast(final CharSequence text) {
        showToast(text, Toast.LENGTH_LONG);
    }

    public static void showLongToast(@StringRes final int resId) {
        showToast(resId, Toast.LENGTH_LONG);
    }

    public static void showExtendLongToast(@StringRes final int resId) {
        showToastWithTime(resId, EXTEND_LONG);
    }

    public static void showExtendLongToast(final CharSequence text) {
        showToastWithTime(text, EXTEND_LONG);
    }


    @SuppressLint("ShowToast")
    public static Toast makeText(CharSequence text, @Duration int duration) {
        return customToast(Toast.makeText(BabyVoiceApp.getInstance(),
                text,
                duration));
    }

    @SuppressLint("ShowToast")
    public static Toast makeText(@StringRes final int resId, @Duration int duration) {
        return customToast(Toast.makeText(BabyVoiceApp.getInstance(),
                resId,
                duration));
    }

    private static Toast customToast(Toast t) {
        t.getView().setBackgroundResource(R.drawable.toast_frame_background);

        View textView = t.getView().findViewById(android.R.id.message);
        ((TextView) textView).setGravity(Gravity.CENTER);
        if (textView != null) {
            // Meizu 特殊处理
            textView.setBackgroundColor(Color.TRANSPARENT);
            textView.setPadding(0, 0, 0, 0);
        }

        return t;
    }

    /**
     * 设置自定义时长的Toast，利用计时器，以1s为单位，重复show该Toast，
     * 在时间结束点调用 cancel 收起 Toast。
     * 限制设置的最小时间为2s。
     */
    public static void showToastWithTime(@StringRes final int resId, long duration) {
        final Toast toast = makeText(resId, Toast.LENGTH_LONG);
        showToast(toast, duration, null);
    }

    public static void showToastWithTime(final CharSequence text, long duration) {
        showToast(text, duration, null);
    }

    public static void showToast(@StringRes final int resId, long duration, final ToastTimeFinish cb) {
        final Toast toast = makeText(resId, Toast.LENGTH_LONG);
        showToast(toast, duration, cb);
    }

    public static void showToast(final CharSequence text, long duration, final ToastTimeFinish cb) {
        final Toast toast = makeText(text, Toast.LENGTH_LONG);
        showToast(toast, duration, cb);
    }

    private static void showToast(final Toast toast, long duration, final ToastTimeFinish cb) {
        final long ONE_SECOND = 1000;
        final long MIN_TIME = 2000;
        new CountDownTimer(Math.max(MIN_TIME, duration), ONE_SECOND) {

            @Override
            public void onTick(long millisUntilFinished) {
                toast.show();
            }

            @Override
            public void onFinish() {
                toast.cancel();
                if (cb != null) {
                    cb.onFinish();
                }
            }
        }.start();
    }

    public interface ToastTimeFinish {
        void onFinish();
    }

    /**
     * @hide
     */
    @IntDef({Toast.LENGTH_SHORT, Toast.LENGTH_LONG})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Duration {
    }
}
