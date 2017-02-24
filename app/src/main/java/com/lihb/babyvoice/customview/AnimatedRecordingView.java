package com.lihb.babyvoice.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.Log;

import com.lihb.babyvoice.customview.base.BaseSurfaceView;

/**
 * Created by lhb on 2017/2/24.
 */

public class AnimatedRecordingView extends BaseSurfaceView {
    private static final String TAG = "AnimatedRecordingView";
    private Context mContext;
    private Paint mLinePaint;
    private Paint mRectPaint;
    private int step = 0;
    private static int RECT_WIDTH = 10;
    private static int SPACE = -5;
    private boolean mIsStarted = false;
    private int mCount;

    public AnimatedRecordingView(Context context) {
        this(context, null);
    }

    public AnimatedRecordingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnimatedRecordingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context ctx) {
        mContext = ctx;
        mLinePaint = new Paint();
        mLinePaint.setColor(Color.RED);

        mRectPaint = new Paint();
        mRectPaint.setColor(Color.BLACK);
    }

    public void start() {
        mIsStarted = true;
        mCount = 0;
    }

    @Override
    public void setVolume(float volume) {
        super.setVolume(volume);
    }

    @Override
    protected void onRender(Canvas canvas, float volume) {
        super.onRender(canvas, volume);
//        Log.i(TAG, "mVolume = " + mVolume);
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.OVERLAY);
        canvas.drawLine(0, mHeight / 2, mWidth, mHeight / 2, mLinePaint);
        canvas.save();
        if (mIsStarted && mCount < 50) {
            float left = step;
            float top = (mHeight / 2 - mVolume - 150);
            float right = step + RECT_WIDTH;
            float bottom = mHeight / 2;
            Log.e("lihbtest", "left = " + left + ",top = " + top + ",right = " + right + ",bottom = " + bottom + ",mCount = " + mCount);
            canvas.drawRect(left, top, right, bottom, mRectPaint);
            step = step + RECT_WIDTH + SPACE;
            mCount++;
           /* if (step > mWidth - SPACE) {
                step = 0;
            }*/
        }
        canvas.restore();
    }
}
