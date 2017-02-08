package com.lihb.babyvoice.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;

import com.lihb.babyvoice.R;


/**
 * 布局分割线
 * <p/>
 * 可在 layout xml中通过 orientation = "horizontal|vertical" 指定方向，默认水平
 * 使用 lineColor 设定颜色
 * 使用 lineWidth 设置线宽
 * <p/>
 * 建议 layout_width 和 layout_height 都使用为 wrap_content
 * <p/>
 * Created by caijw on 2015/7/22.
 */
public class DividerLine extends View {
    private int orientation = 0;    // 0 - horizontal, 1 - vertical
    private int lineWidth = 1;

    public DividerLine(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(attrs);
    }

    public DividerLine(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(attrs);
    }

    private void initView(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.DividerLine);

            setBackgroundColor(ta.getColor(R.styleable.DividerLine_lineColor, getResources().getColor(R.color.divider_color)));
            orientation = ta.getInt(R.styleable.DividerLine_android_orientation, 0);
            lineWidth = ta.getDimensionPixelSize(R.styleable.DividerLine_lineWidth, getResources().getDimensionPixelSize(R.dimen.divider_line_width));

            ta.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int wSize = MeasureSpec.getSize(widthMeasureSpec);
        int hSize = MeasureSpec.getSize(heightMeasureSpec);

        if (orientation == 0) {
            hSize = lineWidth;
        } else {
            wSize = lineWidth;
        }

        setMeasuredDimension(wSize, hSize);
    }
}
