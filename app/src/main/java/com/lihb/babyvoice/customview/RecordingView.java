package com.lihb.babyvoice.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by lhb on 2017/2/24.
 */

public class RecordingView extends View {

    private Paint mLinePaint;
    private Paint mRectPaint;
    private int step = 0;
    private static int RECT_WIDTH = 10;
    private static int SPACE = 2;
    private boolean mIsStarted = false;
    float mVolume;
    protected int mWidth, mHeight;
    private List<RectF> rectFList;
    private boolean mFlag = false;

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
        mRectPaint.setColor(Color.WHITE);
        rectFList = new ArrayList<>();
    }

    public void start() {
        mIsStarted = true;
        step = 0;
    }

    public void setVolume(float volume) {
        mVolume = volume;
        float left = step;
        float top = (mHeight / 2 - mVolume - 150);
        float right = step + RECT_WIDTH;
        float bottom = mHeight / 2;
        Log.e("lihbtest", "left = " + left + ",top = " + top + ",right = " + right + ",bottom = " + bottom /*+ ",mCount = " + mCount*/);
        RectF rect = new RectF(left, new Random().nextInt(200), right, bottom);
        rectFList.add(rect);
        step = step + RECT_WIDTH + SPACE;
        if (step >= mWidth) {
            mFlag = true;
            rectFList.remove(0);
            step = step - (RECT_WIDTH + SPACE);
        }
        postInvalidateDelayed(100);
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
        canvas.save();
//        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        canvas.drawLine(0, mHeight / 2, mWidth, mHeight / 2, mLinePaint);
        for (int i = 0; i < rectFList.size(); i++) {
            RectF rectF = rectFList.get(i);
            Log.e("----onRender1-----", rectF.toString() + "threadid = " + Thread.currentThread().getId());
            if (mFlag) {
                rectF.left = rectF.left - RECT_WIDTH - SPACE;
                rectF.right = rectF.left + RECT_WIDTH;
            }
//            Log.e("----onRender2-----", rectF.toString()+"threadid = " + Thread.currentThread().getId());
//            if (rectF.left < 0 || rectF.right < 0) {
//                rectF.left = -rectF.left;
//                rectF.right = -rectF.right;
//            }
            canvas.drawRect(rectF, mRectPaint);

        }
        canvas.restore();
    }
}
