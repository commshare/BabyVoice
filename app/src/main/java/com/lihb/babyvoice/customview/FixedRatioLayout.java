package com.lihb.babyvoice.customview;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;


/**
 * 支持指定纵横比例的 RelativeLayout 布局
 * Created by ruoshili on 8/28/15.
 */
public class FixedRatioLayout extends RelativeLayout {
    private FixedRatio ratio = new FixedRatio();

    public FixedRatioLayout(Context context) {
        super(context);
    }

    public FixedRatioLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FixedRatioLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(21)
    public FixedRatioLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    public int getHeightRatio() {
        return ratio.height;
    }

    public int getWidthRatio() {
        return ratio.width;
    }

    public void setRatio(int widthRatio, int heightRatio) {

        if (ratio.setRatio(widthRatio,heightRatio)) {
            requestLayout();
        }
    }

    private void init(Context context, AttributeSet attrs) {
        ratio.init(context,attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        FixedRatio.MeasureSize size = ratio.measureSize(widthMeasureSpec,heightMeasureSpec);
        super.onMeasure(size.width,size.height);
    }
}
