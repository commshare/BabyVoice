package com.lihb.babyvoice.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lihb.babyvoice.R;

/**
 * Created by lihb on 2017/3/4.
 */

public class CategoryView extends RelativeLayout {

    private ImageView recordImg;
    private ImageView pickImg;
    private TextView recordTxt;

    public CategoryView(Context context) {
        this(context, null);
    }

    public CategoryView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CategoryView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        inflate(getContext(), R.layout.record_category, this);
        recordImg = (ImageView) findViewById(R.id.record_img);
        pickImg = (ImageView) findViewById(R.id.pick_img);
        recordTxt = (TextView) findViewById(R.id.record_txt);
    }

    public void setRecordImg(int id) {
        recordImg.setImageResource(id);
    }

    public void setPickImg(int id) {
        this.pickImg.setImageResource(id);
    }

    public void setRecordTxt(String txt) {
        this.recordTxt.setText(txt);
    }

    public void setVisibility(boolean visible) {
        setVisibility(visible ? VISIBLE : GONE);
    }
}
