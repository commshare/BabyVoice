package com.lihb.babyvoice.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lihb.babyvoice.R;


/**
 * Created by caijw on 2015/2/13.
 */
public class TitleBar extends RelativeLayout {
    private TextView mLeftText;
    private TextView mCenterText;
    private TextView mRightText;

    public TitleBar(Context context) {
        super(context);
        initView(null);
    }

    public TitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(attrs);
    }

    public TitleBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(attrs);
    }

    private void initView(AttributeSet attrs) {
        setId(R.id.title_bar);

        inflate(getContext(), R.layout.title_bar, this);
        mLeftText = (TextView) findViewById(R.id.title_left_text);
        mCenterText = (TextView) findViewById(R.id.title_center_text);
        mRightText = (TextView) findViewById(R.id.title_right_text);

        if (attrs != null) {
            TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.TitleBar);

            setTitle(ta.getString(R.styleable.TitleBar_titleText));
            setLeftText(ta.getString(R.styleable.TitleBar_leftText));
            setRightText(ta.getString(R.styleable.TitleBar_rightText));
            setLeftDrawable(ta.getDrawable(R.styleable.TitleBar_leftDrawable));
            setRightDrawable(ta.getDrawable(R.styleable.TitleBar_rightDrawable));

            int titleTextColor = getResources().getColor(R.color.text_black);
            titleTextColor = ta.getColor(R.styleable.TitleBar_titleTextColor, titleTextColor);
            mCenterText.setTextColor(titleTextColor);

            int leftTextColor = getResources().getColor(R.color.base_color);// FIXME: 2017/2/8
            leftTextColor = ta.getColor(R.styleable.TitleBar_leftTextColor, leftTextColor);
            mLeftText.setTextColor(leftTextColor);

            int rightTextColor = getResources().getColor(R.color.base_color);// FIXME: 2017/2/8
            rightTextColor = ta.getColor(R.styleable.TitleBar_rightTextColor, rightTextColor);
            mRightText.setTextColor(rightTextColor);

            ta.recycle();
        }
    }

    public TextView getLeftText() {
        return mLeftText;
    }

    public TextView getCenterText() {
        return mCenterText;
    }

    public TextView getRightText() {
        return mRightText;
    }

    public void setLeftVisible(boolean visible) {
        mLeftText.setVisibility(visible ? VISIBLE : INVISIBLE);
    }

    public void setRightVisible(boolean visible) {
        mRightText.setVisibility(visible ? VISIBLE : INVISIBLE);
    }

    public void setTitleVisible(boolean visible) {
        mCenterText.setVisibility(visible ? VISIBLE : INVISIBLE);
    }

    public void setLeftDrawable(Drawable drawable) {
        mLeftText.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
    }

    public void setLeftText(final String txt) {
        mLeftText.setText(txt);
    }

    public void setLeftText(int resid) {
        mLeftText.setText(resid);
    }

    public void setLeftOnClickListener(OnClickListener listener) {
        mLeftText.setOnClickListener(listener);
    }

    public void setLeftEnabled(boolean enabled) {
        mLeftText.setEnabled(enabled);
    }

    public void setRightText(final String txt) {
        mRightText.setText(txt);
    }

    public void setRightText(int resid) {
        mRightText.setText(resid);
    }

    public void setRightDrawable(Drawable drawable) {
        mRightText.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
    }

    public void setRightOnClickListener(OnClickListener listener) {
        mRightText.setOnClickListener(listener);
    }

    public void setRightEnabled(boolean enabled) {
        mRightText.setEnabled(enabled);
    }

    public void setTitle(final String txt) {
        mCenterText.setText(txt);
    }

    public void setTitle(int resid) {
        mCenterText.setText(resid);
    }

    public void setTitleOnClickListener(OnClickListener listener) {
        mCenterText.setOnClickListener(listener);
    }

    public void setRightTextColor(int color) {
        mRightText.setTextColor(color);
    }

    public void setLeftTextColor(int color) {
        mLeftText.setTextColor(color);
    }
}
