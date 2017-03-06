package com.lihb.babyvoice.customview;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.view.View;

import com.lihb.babyvoice.R;

/**
 * Created by tangyangkai on 2016/12/27.
 */

public class StickyDecoration extends RecyclerView.ItemDecoration {

    private DecorationCallback mDecorationCallback;
    private TextPaint textPaint;
    private Paint paint;
    private int topHeight;

    public StickyDecoration(Context context, DecorationCallback callback) {
        Resources res = context.getResources();
        mDecorationCallback = callback;
        paint = new Paint();
        paint.setColor(ContextCompat.getColor(context, R.color.item_group_bg));
        textPaint = new TextPaint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(40);
        textPaint.setColor(ContextCompat.getColor(context, R.color.text_black));
        topHeight = res.getDimensionPixelSize(R.dimen.top);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position = parent.getChildAdapterPosition(view);
        if (isFirstInGroup(position)) {
            outRect.top = topHeight;
        } else {
            outRect.top = 0;
        }

    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = parent.getChildAt(i);
            int position = parent.getChildAdapterPosition(view);
            String textLine = mDecorationCallback.getGroupLabel(position);
            if (isFirstInGroup(position)) {
                float top = view.getTop() - topHeight;
                float bottom = view.getTop();
                c.drawRect(left, top, right, bottom, paint);//绘制红色矩形
                c.drawText(textLine, left + 30, bottom - 30, textPaint);//绘制文本
            }
        }
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        int position = ((LinearLayoutManager) (parent.getLayoutManager())).findFirstVisibleItemPosition();
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();
        String text = mDecorationCallback.getGroupLabel(position);
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            if (isLastInGroup(position)) {
                int bottom = child.getBottom();
                if (bottom <= topHeight) {
                    c.drawRect(left, 0, right, bottom, paint);
                    c.drawText(text, 30, topHeight / 2 + (float) getLabelHeight() / 4 - (topHeight - bottom), textPaint);
                    return;
                }
            }
        }

        c.drawRect(left, 0, right, topHeight, paint);//绘制红色矩形
        c.drawText(text, 30, topHeight - 30, textPaint);//绘制文本
    }

    private boolean isFirstInGroup(int position) {
        boolean isFirst;
        if (position == 0) {
            isFirst = true;
        } else {
            if (mDecorationCallback.getGroupLabel(position).
                    equals(mDecorationCallback.getGroupLabel(position - 1))) {
                isFirst = false;
            } else {
                isFirst = true;
            }
        }
        return isFirst;
    }

    private boolean isLastInGroup(int pos) {

        String label = mDecorationCallback.getGroupLabel(pos);
        String nextLabel;
        try {
            nextLabel = mDecorationCallback.getGroupLabel(pos + 1);
        } catch (ArrayIndexOutOfBoundsException exception) {
            return true;
        }

        if (!label.equals(nextLabel)) return true;

        return false;
    }

    private double getLabelHeight() {
        return Math.ceil(textPaint.getFontMetrics().bottom - textPaint.getFontMetrics().top);
    }

    public interface DecorationCallback {
        String getGroupLabel(int position);
    }

}
