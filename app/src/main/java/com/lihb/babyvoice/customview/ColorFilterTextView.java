package com.lihb.babyvoice.customview;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 支持Drawable自动同步TextColor颜色的TextView
 * <p/>
 * Created by caijw on 2015/3/2.
 */
public class ColorFilterTextView extends TextView {
    public ColorFilterTextView(Context context) {
        super(context);
    }

    public ColorFilterTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ColorFilterTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setCompoundDrawablesWithIntrinsicBounds(Drawable left, Drawable top, Drawable right, Drawable bottom) {
        super.setCompoundDrawablesWithIntrinsicBounds(
                left != null ? new FilterDrawable(left) : null,
                top != null ? new FilterDrawable(top) : null,
                right != null ? new FilterDrawable(right) : null,
                bottom != null ? new FilterDrawable(bottom) : null);
    }

    @Override
    public void setTextColor(int color) {
        super.setTextColor(color);
        syncDrawableTextColor(); //文字颜色发生更改时，同步图标的颜色
    }

    private void syncDrawableTextColor() {
        Drawable[] drawables = getCompoundDrawables();
        for (Drawable drawable : drawables) {
            if (drawable != null) {
                drawable.setColorFilter(getCurrentTextColor(), PorterDuff.Mode.SRC_IN);
            }
        }
    }

    protected class FilterDrawable extends LayerDrawable {
        public FilterDrawable(Drawable d) {
            super(new Drawable[]{d});
        }

        @Override
        protected boolean onStateChange(int[] state) {
            syncDrawableTextColor();
            invalidateSelf();
            return super.onStateChange(state);
        }

        @Override
        public boolean isStateful() {
            return true;
        }
    }
}
