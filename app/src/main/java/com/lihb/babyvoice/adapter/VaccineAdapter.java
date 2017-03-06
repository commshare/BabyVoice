package com.lihb.babyvoice.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lihb.babyvoice.R;
import com.lihb.babyvoice.model.VaccineInfo;

import java.util.List;

/**
 * Created by lhb on 2017/3/6.
 */

public class VaccineAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<VaccineInfo> mData;

    public VaccineAdapter(Context mContext, List<VaccineInfo> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(
                parent.getContext()).inflate(R.layout.vaccine_info_item, parent, false);
        return new VaccineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof VaccineViewHolder) {
            VaccineViewHolder viewHolder = (VaccineViewHolder) holder;
            VaccineInfo vaccineInfo = mData.get(position);
            viewHolder.bindData(vaccineInfo);
        }
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public class VaccineViewHolder extends RecyclerView.ViewHolder {

        public TextView vaccine_name_txt;
        public TextView vaccine_inject_txt;

        public ImageView vaccine_inject_img;
        public ImageView vaccine_label_img;

        public VaccineViewHolder(View itemView) {
            super(itemView);
            vaccine_label_img = (ImageView) itemView.findViewById(R.id.vaccine_label_img);
            vaccine_inject_img = (ImageView) itemView.findViewById(R.id.vaccine_inject_img);
            vaccine_name_txt = (TextView) itemView.findViewById(R.id.vaccine_name_txt);
            vaccine_inject_txt = (TextView) itemView.findViewById(R.id.vaccine_inject_txt);
        }


        public void bindData(VaccineInfo vaccineInfo) {
            if (vaccineInfo == null) {
                return;
            }
            vaccine_name_txt.setText(vaccineInfo.vaccineName);
            if (vaccineInfo.isInjected) {
                vaccine_inject_img.setImageResource(R.mipmap.selected);
                vaccine_inject_txt.setText(vaccineInfo.injectDate + mContext.getString(R.string.injected));
                vaccine_inject_txt.setTextColor(mContext.getResources().getColor(R.color.color_999999));
            } else {
                vaccine_inject_img.setImageResource(R.mipmap.normal);
                vaccine_inject_txt.setText(mContext.getString(R.string.not_injected));
                vaccine_inject_txt.setTextColor(mContext.getResources().getColor(R.color.text_black));
            }

        }
    }
}
