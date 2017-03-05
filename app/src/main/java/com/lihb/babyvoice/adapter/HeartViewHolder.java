package com.lihb.babyvoice.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lihb.babyvoice.R;
import com.lihb.babyvoice.model.BabyVoice;
import com.lihb.babyvoice.utils.StringUtils;

/**
 * Created by lhb on 2017/2/9.
 */

public class HeartViewHolder extends RecyclerView.ViewHolder {

    public TextView titleText = null;
    public TextView dateText = null;
    public TextView durationText = null;
    public LinearLayout voice_layout = null;
    public TextView deleteText = null;
    public ImageView catergoryImg = null;
    private BabyVoice mVoice;
    private View itemView;

    public HeartViewHolder(final View itemView) {
        super(itemView);
        this.itemView = itemView;
        voice_layout = (LinearLayout) itemView.findViewById(R.id.voice_layout);
        titleText = (TextView) itemView.findViewById(R.id.voice_title_txt);
        dateText = (TextView) itemView.findViewById(R.id.voice_date_txt);
        durationText = (TextView) itemView.findViewById(R.id.voice_duration_txt);
        deleteText = (TextView) itemView.findViewById(R.id.voice_delete_txt);
        catergoryImg = (ImageView) itemView.findViewById(R.id.voice_category_img);
    }

    public void bindData(BabyVoice voice) {
        mVoice = voice;
        titleText.setText(mVoice.name);
        dateText.setText(mVoice.date);
        durationText.setText(StringUtils.formatTime(Integer.parseInt(mVoice.duration)));
        setCategoryImg(voice.category);
    }

    private void setCategoryImg(String category) {
        String[] items = itemView.getResources().getStringArray(R.array.voice_type);
        int[] ids = {R.mipmap.catetory_heart, R.mipmap.category_lung, R.mipmap.category_baby_voice, R.mipmap.category_other_voice};
        if (StringUtils.areEqual(category, items[0])) {
            catergoryImg.setImageResource(ids[0]);
        } else if (StringUtils.areEqual(category, items[1])) {
            catergoryImg.setImageResource(ids[1]);
        } else if (StringUtils.areEqual(category, items[2])) {
            catergoryImg.setImageResource(ids[2]);
        } else {
            catergoryImg.setImageResource(ids[3]);
        }
    }


}
