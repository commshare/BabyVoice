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
    private Paint mEdgeLinePaint;
    private Paint mVolumeLinePaint;
    private Paint mVerticalLinePaint;
    private int index = 0;
    private static int RECT_WIDTH = 3;
    private static int STEP = 3;
    private static int SPACE = 0;
    private boolean mIsStarted = false;
    private List<RectF> rectFList;
    private List<MyLine> lineList;

    private boolean mIsToEdge = false; //是否到达最右边
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
        mEdgeLinePaint = new Paint();
        mEdgeLinePaint.setColor(Color.RED);
        mEdgeLinePaint.setStrokeWidth(2);
        mEdgeLinePaint.setStyle(Paint.Style.STROKE);
        mEdgeLinePaint.setFlags(Paint.ANTI_ALIAS_FLAG);

        mVolumeLinePaint = new Paint();
        mVolumeLinePaint.setColor(Color.WHITE);
        mVolumeLinePaint.setStrokeWidth(3);
        mVolumeLinePaint.setStyle(Paint.Style.STROKE);
        mVolumeLinePaint.setFlags(Paint.ANTI_ALIAS_FLAG);

        mVerticalLinePaint = new Paint();
        mVerticalLinePaint.setColor(Color.RED);
        mVerticalLinePaint.setStrokeWidth(4);
        mVerticalLinePaint.setStyle(Paint.Style.STROKE);
        mVerticalLinePaint.setFlags(Paint.ANTI_ALIAS_FLAG);

        rectFList = new CopyOnWriteArrayList<>();
        lineList = new CopyOnWriteArrayList<>();

    }

    public void start() {
        mIsStarted = true;
        mIsToEdge = false;
        index = 0;
    }

    public void stop() {
        mIsStarted = false;
        mIsToEdge = false;
        index = 0;
        rectFList.clear();
        lineList.clear();
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

        drawEdge(canvas);

        if (!mIsStarted) {
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            return;
        }

        if (volume < 60) {
            volume = new Random().nextInt(60);
        }
        if (volume < 30) {
            volume = 30;
        }

//        Log.i("lihbtest", "onRender: volume = " + volume);
        if (volume >= mHeight / 2) {
            volume = mHeight / 2 - 10;
        }
//        RectF rect = new RectF(index, volume, index + RECT_WIDTH, 0);
//        rectFList.add(rect);
//        index = index + RECT_WIDTH + SPACE;
//        if (index >= mWidth) {
//            mIsToEdge = true;
//            rectFList.remove(0);
//            index = index - (RECT_WIDTH + SPACE);
//        }
        MyLine line = new MyLine(index, 0, index, volume);
        lineList.add(line);
        index = index + STEP;
        if (index >= mWidth*0.9) {
            mIsToEdge = true;
            lineList.remove(0);
            index = index - STEP;
        }

        canvas.save();
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        canvas.drawLine(index, -mHeight/2, index, mHeight/2, mVerticalLinePaint);
//        for (RectF rectF : rectFList) {
//            if (mIsToEdge) {
//                rectF.left = rectF.left - RECT_WIDTH - SPACE;
//                rectF.right = rectF.left + RECT_WIDTH;
//            }
//            canvas.drawRect(rectF, mVolumeLinePaint);
//            canvas.drawRect(rectF.left, 0, rectF.right, -rectF.top, mVolumeLinePaint);
//
//        }
        for (MyLine line1 : lineList) {
            if (mIsToEdge) {
                line1.startX = line1.startX - STEP;
                line1.stopX = line1.stopX - STEP;
            }
            canvas.drawLine(line1.startX, line1.startY, line1.stopX, line1.stopY, mVolumeLinePaint);
            canvas.drawLine(line1.startX, line1.startY, line1.stopX, -line1.stopY, mVolumeLinePaint);

        }
        drawEdge(canvas);

        canvas.restore();
    }

    private void drawEdge(Canvas canvas) {
        // 画上边界
        canvas.drawLine(0, -mHeight / 2, mWidth, -mHeight / 2, mEdgeLinePaint);
        // 画中间线
        canvas.drawLine(0, 0, mWidth, 0, mEdgeLinePaint);
        // 画下边界
        canvas.drawLine(0, mHeight / 2, mWidth, mHeight / 2, mEdgeLinePaint);
    }

    private class MyLine {
        public float startX;
        public float startY;
        public float stopX;
        public float stopY;

        public MyLine(float startX, float startY, float stopX, float stopY) {
            this.startX = startX;
            this.startY = startY;
            this.stopX = stopX;
            this.stopY = stopY;
        }
    }

}
