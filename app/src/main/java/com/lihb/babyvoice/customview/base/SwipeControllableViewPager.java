package com.lihb.babyvoice.customview.base;

/**
 * 不能左右滑动的viewpager
 */

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;


public class SwipeControllableViewPager extends ViewPager {
    private boolean swipeEnabled;

    public SwipeControllableViewPager(Context context) {
        this(context, null);
    }

    public SwipeControllableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.swipeEnabled = true;
    }

    public void setSwipeEnabled(boolean enabled) {
        this.swipeEnabled = enabled;
    }

    public boolean onInterceptTouchEvent(MotionEvent event) {
        try {
            return this.swipeEnabled && super.onInterceptTouchEvent(event);
        } catch (Throwable t) {
            Log.e("SwipeViewPager", "onInterceptTouchEvent error", t);
            return false;
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        try {
            return this.swipeEnabled && super.onTouchEvent(event);
        } catch (Throwable t) {
            Log.e("SwipeViewPager", "onTouchEvent error", t);
            return false;
        }
    }
}