package com.lihb.babyvoice.customview;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

import com.lihb.babyvoice.R;
import com.lihb.babyvoice.command.PickedCategoryCommand;

/**
 * Created by lihb on 2017/3/4.
 */

public class PickRecordDialog extends Dialog {

    private CategoryView mHeart, mLung, mVoice, mOther;

    private OnPickRecordDialogListener mListener;


    public PickRecordDialog(Context context) {
        super(context);
    }

    public PickRecordDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected PickRecordDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        initViews();
    }

    private void initViews() {
        mHeart = (CategoryView) findViewById(R.id.record_heart_category);
        mLung = (CategoryView) findViewById(R.id.record_lung_category);
        mVoice = (CategoryView) findViewById(R.id.record_baby_voice_category);
        mOther = (CategoryView) findViewById(R.id.record_other_category);

        String[] str = getContext().getResources().getStringArray(R.array.voice_type);
        initData(mHeart, R.mipmap.catetory_heart, str[0]);
        initData(mLung, R.mipmap.category_lung, str[1]);
        initData(mVoice, R.mipmap.category_baby_voice, str[2]);
        initData(mOther, R.mipmap.category_other_voice, str[3]);

        mHeart.setOnClickListener(mOnClickListener);
        mLung.setOnClickListener(mOnClickListener);
        mVoice.setOnClickListener(mOnClickListener);
        mOther.setOnClickListener(mOnClickListener);

        // 2017.06.03隐藏掉其他音和儿童语音
        mVoice.setVisibility(false);
        mOther.setVisibility(false);
    }

    private void initData(CategoryView view, int id, String s) {
        view.setRecordImg(id);
        view.setRecordTxt(s);
        if (view == mHeart) {
            view.setPickImg(R.mipmap.pick_selected);
        }
    }

    public void setOnPickRecordDialogListener(OnPickRecordDialogListener listener) {
        this.mListener = listener;
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if (view == mHeart) {
                mHeart.setPickImg(R.mipmap.pick_selected);
                mVoice.setPickImg(R.mipmap.pick_normal);
                mLung.setPickImg(R.mipmap.pick_normal);
                mOther.setPickImg(R.mipmap.pick_normal);
                if (mListener != null) {
                    mListener.onClick(PickedCategoryCommand.TYPE_HEART);
                }
            } else if (view == mLung) {
                mHeart.setPickImg(R.mipmap.pick_normal);
                mVoice.setPickImg(R.mipmap.pick_normal);
                mLung.setPickImg(R.mipmap.pick_selected);
                mOther.setPickImg(R.mipmap.pick_normal);
                if (mListener != null) {
                    mListener.onClick(PickedCategoryCommand.TYPE_LUNG);
                }
            } else if (view == mVoice) {
                mHeart.setPickImg(R.mipmap.pick_normal);
                mVoice.setPickImg(R.mipmap.pick_selected);
                mLung.setPickImg(R.mipmap.pick_normal);
                mOther.setPickImg(R.mipmap.pick_normal);
                if (mListener != null) {
                    mListener.onClick(PickedCategoryCommand.TYPE_VOICE);
                }
            } else {
                mHeart.setPickImg(R.mipmap.pick_normal);
                mVoice.setPickImg(R.mipmap.pick_normal);
                mLung.setPickImg(R.mipmap.pick_normal);
                mOther.setPickImg(R.mipmap.pick_selected);
                if (mListener != null) {
                    mListener.onClick(PickedCategoryCommand.TYPE_OTHER);
                }
            }
            dismiss();
        }
    };

    public interface OnPickRecordDialogListener{
        void onClick(int type);
    }


}
