package com.lihb.babyvoice.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by lhb on 2017/2/24.
 */

public class AnalysisBarChartingView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    /**
     * 屏幕的大小
     */
    private int width, height;

    /**
     * view的大小
     */
    private int viewWidth, viewHeight;

    private SurfaceHolder holder;//这个对象可以在线程里异步操作获取的canvas对象。

    private Integer[] origin;

    private int lineWidth = 1;

    private boolean running = false;

    private int gridWidth = 40;

    private int space = 20;

    private String[] bgs = {"#FFD39B", "#EEB422", "#E0FFFF", "#919191"};

    private int[] itemBg = {Color.BLACK, Color.BLUE, Color.DKGRAY, Color.GREEN};

    public AnalysisBarChartingView(Context context) {
        super(context);
        init(context);
    }

    public AnalysisBarChartingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AnalysisBarChartingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        manager.getDefaultDisplay().getMetrics(metrics);
        width = metrics.widthPixels;
        height = metrics.heightPixels;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        viewWidth = getDefaultSize(width, widthMeasureSpec);
        viewHeight = getDefaultSize(height, heightMeasureSpec);
        holder = getHolder();
        holder.addCallback(this);
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        origin = new Integer[]{10, (viewHeight - 10)};
        Rect r = new Rect(0, 0, viewWidth, viewHeight);
        Canvas canvas = holder.lockCanvas(r);
        canvas.drawColor(Color.parseColor("#CFCFCF"));
        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setColor(Color.RED);
        p.setStrokeWidth(lineWidth);
        canvas.drawLine(origin[0], 5, origin[0], origin[1], p);
        canvas.drawLine(origin[0], origin[1], viewWidth - origin[0], viewHeight - origin[0], p);
        holder.unlockCanvasAndPost(canvas);
        new Thread(this).start();
        running = true;
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {


    }


    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        running = false;
    }


    @Override
    public void run() {
        int num = (viewWidth - origin[0]) / (space + gridWidth);
        List<Rect> rects = new ArrayList<>();
        List<Paint> paints = new ArrayList<>();
        int startX = origin[0], startY = origin[1], endX = origin[0], endY = origin[1];
        for (int i = 0; i < num; i++) {
            startX = (space * (i + 1) + gridWidth * i);
            endX = ((space + gridWidth) * (i + 1));
            rects.add(new Rect(startX, startY, endX, endY));
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(itemBg[Math.abs(new Random().nextInt(itemBg.length))]);
            paints.add(paint);
        }
        while (running) {
            Random random = new Random();
            Canvas canvas = holder.lockCanvas(new Rect(origin[0], origin[0], viewWidth - origin[0], origin[1]));
            canvas.drawColor(Color.parseColor(bgs[Math.abs(new Random().nextInt(bgs.length))]));
            for (int i = 0; i < num; i++) {
                int y = Math.abs(random.nextInt(origin[1]));
                while (y < origin[0] || y > origin[1] / 2)
                    y = Math.abs(random.nextInt(origin[1]));
                Log.i("asd", "y=" + y);
                Rect rect = rects.get(i);
                rect.top = y;
                canvas.drawRect(rect, paints.get(i));
            }
            holder.unlockCanvasAndPost(canvas);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}