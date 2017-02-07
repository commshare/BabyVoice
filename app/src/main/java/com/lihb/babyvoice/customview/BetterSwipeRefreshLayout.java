package com.lihb.babyvoice.customview;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by chentong1 on 2016/1/18.
 */
public class BetterSwipeRefreshLayout extends SwipeRefreshLayout {
    private boolean mAcceptEvents;

    public BetterSwipeRefreshLayout(Context context) {
        super(context);
    }

    public void setAcceptEvents(boolean mAcceptEvents) {
        this.mAcceptEvents = mAcceptEvents;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mAcceptEvents ? super.onInterceptTouchEvent(ev) : true;
    }

    public BetterSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mAcceptEvents = true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mAcceptEvents = false;
    }
}
