package com.lihb.babyvoice.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lihb.babyvoice.R;

/**
 * 用法：
 * <com.lihb.babyvoice.customview.CommonItem
 * android:id="@+id/item_lesson_detail"
 * android:layout_width="wrap_content"
 * android:layout_height="wrap_content"
 * android:visibility="gone"
 * app:studentItemSelector="@drawable/item_selector"
 * app:studentLeftIcon="@drawable/ic_lesson_detail"
 * app:studentRightIcon="@drawable/icon_my_next"
 * app:studentItemName="@string/student_setting_item_lesson_detail"
 * app:studentDividerVisible="true"
 * />
 */

public class CommonItem extends RelativeLayout {

    private int mSelectorRes = 0;

    private int mLeftIconRes = 0;

    private int mRightIconRes = 0;

    private String mNameString;

    private boolean mDividerVisible = true;

    private RelativeLayout mContainer = null;

    private ImageView mRightImg = null;

    private ImageView mLeftImg = null;

    private TextView mNameTv = null;

    private TextView mItemValueTv = null;

    private DividerLine mDividerLine = null;

    public CommonItem(Context context) {
        this(context, null);
    }

    public CommonItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CommonItem(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public CommonItem(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CommonItem);
        if (typedArray != null) {
            mSelectorRes = typedArray.getResourceId(R.styleable.CommonItem_commonItemSelector, 0);
            mLeftIconRes = typedArray.getResourceId(R.styleable.CommonItem_commonLeftIcon, 0);
            mRightIconRes = typedArray.getResourceId(R.styleable.CommonItem_commonRightIcon, 0);
            mNameString = typedArray.getText(R.styleable.CommonItem_commonItemName).toString();
            mDividerVisible = typedArray.getBoolean(R.styleable.CommonItem_commonDividerVisible, true);
            typedArray.recycle();
        }

        inflate(context, R.layout.common_item, this);

        mContainer = (RelativeLayout) findViewById(R.id.common_item_group);
        mLeftImg = (ImageView) findViewById(R.id.item_icon_left);
        mRightImg = (ImageView) findViewById(R.id.item_icon_right);
        mNameTv = (TextView) findViewById(R.id.item_name);
        mItemValueTv = (TextView) findViewById(R.id.item_value);
        mDividerLine = (DividerLine) findViewById(R.id.item_divider);

        if (mSelectorRes != 0) {
            mContainer.setBackgroundResource(mSelectorRes);
        } else {
            mContainer.setBackground(null);
        }

        if (mLeftIconRes != 0) {
            mLeftImg.setImageResource(mLeftIconRes);
        } else {
            mLeftImg.setImageDrawable(null);
        }

        if (mRightIconRes != 0) {
            mRightImg.setImageResource(mRightIconRes);
        } else {
            mRightImg.setImageDrawable(null);
        }

        mNameTv.setText(mNameString);

        if (mDividerVisible) {
            mDividerLine.setVisibility(View.VISIBLE);
        } else {
            mDividerLine.setVisibility(View.GONE);
        }
    }

    public void setItemValue(String value) {
        mItemValueTv.setText(value);
    }

    public void setOnClickListener(OnClickListener listener) {
        if (mContainer != null) {
            mContainer.setOnClickListener(listener);
        }
    }


}
