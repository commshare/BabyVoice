package com.lihb.babyvoice.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.Random;

/**
 * Created by lhb on 2017/2/24.
 */

public class RecordingView extends View {

    private Paint mLinePaint;
    private Paint mRectPaint;
    private int step = 0;
    private static int RECT_WIDTH = 10;
    private static int SPACE = -5;
    private boolean mIsStarted = false;
    private int mCount;
    float mVolume;
    protected int mWidth, mHeight;

    public RecordingView(Context context) {
        this(context, null);
    }

    public RecordingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecordingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mLinePaint = new Paint();
        mLinePaint.setColor(Color.RED);

        mRectPaint = new Paint();
        mRectPaint.setColor(Color.BLACK);

    }

    public void start() {
        mIsStarted = true;
        mCount = 0;
        step = 0;
    }

    public void setVolume(float volume) {
        mVolume = volume;
        mCount++;
//        postInvalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawLine(0, mHeight / 2, mWidth, mHeight / 2, mLinePaint);
        int index = 0;
//        canvas.save();
        while (index < 200) {
            index++;
            float left = step;
            float top = (mHeight / 2 - mVolume - 150);
            float right = step + RECT_WIDTH;
            float bottom = mHeight / 2;
            Log.e("lihbtest", "left = " + left + ",top = " + top + ",right = " + right + ",bottom = " + bottom + ",mCount = " + mCount);
            canvas.drawRect(left, new Random().nextInt(200), right, bottom, mRectPaint);
            step = step + RECT_WIDTH + SPACE;
            if (step > mWidth - SPACE) {
                step = 0;
            }
        }
//        canvas.restore();
    }
}
