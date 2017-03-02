package com.lihb.babyvoice.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;

import com.lihb.babyvoice.customview.base.BaseSurfaceView;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by lhb on 2017/2/24.
 */

public class AnimatedRecordingView extends BaseSurfaceView {
    private static final String TAG = "AnimatedRecordingView";
    private Context mContext;
    private Paint mLinePaint;
    private Paint mRectPaint;
    private int step = 0;
    private static int RECT_WIDTH = 3;
    private static int SPACE = 0;
    private boolean mIsStarted = false;
    private List<RectF> rectFList;

    private boolean mFlag = false;
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
        mLinePaint.setStrokeWidth(2);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setFlags(Paint.ANTI_ALIAS_FLAG);

        mRectPaint = new Paint();
        mRectPaint.setColor(Color.WHITE);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setFlags(Paint.ANTI_ALIAS_FLAG);

        rectFList = new CopyOnWriteArrayList<>();

    }

    public void start() {
        mIsStarted = true;
        mFlag = false;
        step = 0;
    }

    public void stop() {
        mIsStarted = false;
        mFlag = false;
        step = 0;
        drawThread.setRun(false);
    }

    @Override
    public void setVolume(float volume) {
        super.setVolume(volume);
        Log.d(TAG, "setVolume: volume = " + volume);
    }

    @Override
    protected void onRender(Canvas canvas, float volume) {
        super.onRender(canvas, volume);
        canvas.translate(0, mHeight / 2); // 移动Y坐标到canvas中间
        canvas.scale(1, -1); // 翻转Y坐标
        // 画上边界
        canvas.drawLine(0, -mHeight / 2, mWidth, -mHeight / 2, mLinePaint);
        // 画中间线
        canvas.drawLine(0, 0, mWidth, 0, mLinePaint);
        // 画下边界
        canvas.drawLine(0, mHeight / 2, mWidth, mHeight / 2, mLinePaint);

        if (!mIsStarted) {
            return;
        }

//        float left = step;
//        float top = (mHeight / 2 - mVolume - 150);
//        float right = step + RECT_WIDTH;
//        float bottom = 0;
        if (volume < 60) {
            volume = new Random().nextInt(60);
        }
        if (volume < 30) {
            volume = 30;
        }

        Log.i("lihbtest", "onRender: volume = " + volume);
        if (volume >= mHeight / 2) {
            volume = mHeight / 2 - 10;
        }
        RectF rect = new RectF(step, volume, step + RECT_WIDTH, 0);
        rectFList.add(rect);
        step = step + RECT_WIDTH + SPACE;
        if (step >= mWidth) {
            mFlag = true;
            rectFList.remove(0);
            step = step - (RECT_WIDTH + SPACE);
        }
        canvas.save();
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        for (RectF rectF : rectFList) {
            if (mFlag) {
                rectF.left = rectF.left - RECT_WIDTH - SPACE;
                rectF.right = rectF.left + RECT_WIDTH;
            }
            canvas.drawRect(rectF, mRectPaint);
            canvas.drawRect(rectF.left, 0, rectF.right, -rectF.top, mRectPaint);

        }
        // 画上边界
        canvas.drawLine(0, -mHeight / 2, mWidth, -mHeight / 2, mLinePaint);
        // 画中间线
        canvas.drawLine(0, 0, mWidth, 0, mLinePaint);
        // 画下边界
        canvas.drawLine(0, mHeight / 2, mWidth, mHeight / 2, mLinePaint);
        canvas.restore();
    }
}
