package com.lihb.babyvoice.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.ScrollView;

/**
 * Created by Administrator on 2015/7/6.
 */
public class SoftInputUtil {

    private Handler handler = new Handler();
    private ScrollView mScrollView;

    public SoftInputUtil(ScrollView view) {
        this.mScrollView = view;
    }

    /**
     * 隐藏软键盘
     *
     * @param act
     */
    public static void hideSoftInput(Activity act) {
        View view = act.getCurrentFocus();
        if (view != null) {
            InputMethodManager im = (InputMethodManager) act.getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * 显示软键盘
     * @param view
     * @param context
     */
    public static void showSoftInput(View view, Context context) {
        if (view != null) {
            InputMethodManager im = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            im.showSoftInput(view, InputMethodManager.SHOW_FORCED);
        }
    }

    /**
     * 缓存软键盘高度
     *
     * @param height
     */
    public static void setSoftInputHeight(Context context, int height) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences("wewatch", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putInt("mSoftInputHeight", height);
        editor.commit();
    }

    /**
     * 获取软键盘高度
     *
     * @param context
     * @return
     */
    public static int getSoftInputHeight(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("wewatch", Activity.MODE_PRIVATE);
        return sharedPreferences.getInt("mSoftInputHeight", 0);
    }

    /**
     * 设置软键盘弹出隐藏监听事件
     *
     * @param view
     */
    public void setSoftInputEvent(final Activity act, final View view, final boolean hasStatusBar) {
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // TODO Auto-generated method stub
                Rect r = new Rect();
                act.getWindow().getDecorView().getWindowVisibleDisplayFrame(r);

                int screenHeight = view.getRootView().getHeight();
                int statusBarHeight = 0;
                if (hasStatusBar) {
                    statusBarHeight = r.top;
                }
                int heightDiff = screenHeight - (r.bottom - r.top) - statusBarHeight;

                if (heightDiff > screenHeight / 3) {
                    //缓存键盘高度
                    SoftInputUtil.setSoftInputHeight(act, heightDiff);
                    //滚动输入框
                    changeScrollView();
                }
            }
        });
    }

    /**
     * 使ScrollView指向底部
     */
    private void changeScrollView() {
        if(mScrollView != null) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScrollView.scrollTo(0, mScrollView.getHeight());
                }
            }, 400);
        }
    }

    /**
     * 设置输入框的响应事件
     *
     * @param view
     */
    public void setOnTouchEvent(View view) {
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                changeScrollView();
                return false;
            }
        });
    }

}
