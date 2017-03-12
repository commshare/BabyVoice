package com.lihb.babyvoice.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;

import com.lihb.babyvoice.R;


/**
 */
public class FixedRatio {

    public int width, height;
    public int baseOn = 1;
    public int fixed;

    public void init(Context context, AttributeSet attrs) {
        if (attrs == null) {
            return;
        }
        TypedArray typedArray = null;
        try {
            typedArray = context.obtainStyledAttributes(attrs, R.styleable.FixedRatioLayout);

            width = typedArray.getInteger(R.styleable.FixedRatioLayout_widthRatio, 0);
            height = typedArray.getInteger(R.styleable.FixedRatioLayout_heightRatio, 0);
            fixed = typedArray.getLayoutDimension(R.styleable.FixedRatioLayout_fixed, 0);
            baseOn = typedArray.getInteger(R.styleable.FixedRatioLayout_baseOn, 1);
        } finally {
            if (typedArray != null) {
                typedArray.recycle();
            }
        }
    }

    public boolean setRatio(int width, int height) {
        if (this.width == width && this.height == height) {
            return false;
        }
        this.width = width;
        this.height = height;
        return true;
    }

    public static class MeasureSize {
        public int width;
        public int height;

        public MeasureSize(int w, int h) {
            width = w;
            height = h;
        }
    }

    public MeasureSize measureSize(int widthMeasureSpec, int heightMeasureSpec) {
        if (height == 0 || width == 0) {
            return new MeasureSize(widthMeasureSpec, heightMeasureSpec);
        } else if (baseOn ==1) {//width
            int h = View.MeasureSpec.getSize(widthMeasureSpec) * height / width;
            int newHeightSpec = View.MeasureSpec.makeMeasureSpec(h, View.MeasureSpec.EXACTLY);
            return new MeasureSize( (fixed!=0) ? fixed : widthMeasureSpec, newHeightSpec);
        } else if(baseOn ==2){//height
            final int w = View.MeasureSpec.getSize(heightMeasureSpec) * width / height;
            int newWidthSpec = View.MeasureSpec.makeMeasureSpec(w, View.MeasureSpec.EXACTLY);
            return new MeasureSize(newWidthSpec, (fixed!=0) ? fixed : heightMeasureSpec);
        }
        return new MeasureSize(widthMeasureSpec,heightMeasureSpec);
    }
}
