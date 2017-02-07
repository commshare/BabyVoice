package com.lihb.babyvoice.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.lihb.babyvoice.R;


/**
 * Created by wangjiajun on 2015/7/29.
 */
public class RadiusImageView extends ImageView {
    private Paint paint;
    private int topLeftRadiusWidth = 0;
    private int topRightRadiusWidth = 0;
    private int bottomLeftRadiusWidth = 0;
    private int bottomRightRadiusWidth = 0;
    private Paint paint2;

    public RadiusImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    public RadiusImageView(Context context, AttributeSet attr) {
        super(context, attr);
        init(context, attr);
    }

    public RadiusImageView(Context context) {
        super(context);
        init(context, null);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RadiusImageView);
            topLeftRadiusWidth = a.getDimensionPixelOffset(R.styleable.RadiusImageView_topLeftRadiusWidth, 0);
            topRightRadiusWidth = a.getDimensionPixelOffset(R.styleable.RadiusImageView_topRightRadiusWidth, 0);
            bottomLeftRadiusWidth = a.getDimensionPixelOffset(R.styleable.RadiusImageView_bottomLeftRadiusWidth, 0);
            bottomRightRadiusWidth = a.getDimensionPixelOffset(R.styleable.RadiusImageView_bottomRightRadiusWidth, 0);
        }

        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));

        paint2 = new Paint();
        paint2.setXfermode(null);
    }

    @Override
    public void draw(Canvas canvas) {
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas2 = new Canvas(bitmap);
        super.draw(canvas2);
        drawTopLeft(canvas2);
        drawTopRight(canvas2);
        drawBottomLeft(canvas2);
        drawBottomRight(canvas2);
        canvas.drawBitmap(bitmap, 0, 0, paint2);
        bitmap.recycle();
    }

    private void drawTopLeft(Canvas canvas) {
        Path path = new Path();
        path.moveTo(0, topLeftRadiusWidth);
        path.lineTo(0, 0);
        path.lineTo(topLeftRadiusWidth, 0);
        path.arcTo(new RectF(0, 0, topLeftRadiusWidth * 2, topLeftRadiusWidth * 2), -90, -90);
        path.close();
        canvas.drawPath(path, paint);
    }

    private void drawTopRight(Canvas canvas) {
        Path path = new Path();
        path.moveTo(getWidth(), topRightRadiusWidth);
        path.lineTo(getWidth(), 0);
        path.lineTo(getWidth() - topRightRadiusWidth, 0);
        path.arcTo(new RectF(getWidth() - topRightRadiusWidth * 2, 0, getWidth(), topLeftRadiusWidth * 2), -90, 90);
        path.close();
        canvas.drawPath(path, paint);
    }

    private void drawBottomLeft(Canvas canvas) {
        Path path = new Path();
        path.moveTo(0, getHeight() - bottomLeftRadiusWidth);
        path.lineTo(0, getHeight());
        path.lineTo(bottomLeftRadiusWidth, getHeight());
        path.arcTo(new RectF(
                        0,
                        getHeight() - bottomLeftRadiusWidth * 2,
                        0 + bottomLeftRadiusWidth * 2,
                        getHeight()),
                90,
                90);
        path.close();
        canvas.drawPath(path, paint);
    }

    private void drawBottomRight(Canvas canvas) {
        Path path = new Path();
        path.moveTo(getWidth() - bottomRightRadiusWidth, getHeight());
        path.lineTo(getWidth(), getHeight());
        path.lineTo(getWidth(), getHeight() - bottomRightRadiusWidth);
        path.arcTo(new RectF(
                getWidth() - bottomRightRadiusWidth * 2,
                getHeight() - bottomRightRadiusWidth * 2,
                getWidth(),
                getHeight()), 0, 90);
        path.close();
        canvas.drawPath(path, paint);
    }
}
