package com.lihb.babyvoice.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
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
    private BabyVoice mVoice;

    public HeartViewHolder(final View itemView) {
        super(itemView);
        voice_layout = (LinearLayout) itemView.findViewById(R.id.voice_layout);
        titleText = (TextView) itemView.findViewById(R.id.voice_title_txt);
        dateText = (TextView) itemView.findViewById(R.id.voice_date_txt);
        durationText = (TextView) itemView.findViewById(R.id.voice_duration_txt);
        deleteText = (TextView) itemView.findViewById(R.id.voice_delete_txt);
    }

    public void bindData(BabyVoice voice) {
        mVoice = voice;
        titleText.setText(mVoice.name);
        dateText.setText(mVoice.date);
        durationText.setText(StringUtils.formatTime(Integer.parseInt(mVoice.duration)));
    }


}
