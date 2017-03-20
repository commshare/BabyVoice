package com.lihb.babyvoice.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lihb.babyvoice.R;
import com.lihb.babyvoice.model.HealthQuota;

import java.util.List;

/**
 * Created by lihb on 2017/3/11.
 */

public class HealthProtectAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<HealthQuota> mData;

    public HealthProtectAdapter(Context mContext, List<HealthQuota> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(
                parent.getContext()).inflate(R.layout.health_show_item, parent, false);
        return new HealthQuotaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HealthQuotaViewHolder) {
            HealthQuotaViewHolder viewHolder = (HealthQuotaViewHolder) holder;
            HealthQuota healthQuota = mData.get(position);
            viewHolder.bindData(healthQuota);
        }
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public void updateData(List<HealthQuota> data) {
        mData = data;
        notifyDataSetChanged();
    }

    private class HealthQuotaViewHolder extends RecyclerView.ViewHolder{

        public TextView health_show_title_txt;
        public TextView health_show_date_txt;
        public TextView health_show_head_height_txt;
        public TextView health_show_weight_temperature_txt;
        public TextView health_show_grow_up_txt;


        public HealthQuotaViewHolder(View itemView) {
            super(itemView);
            health_show_title_txt = (TextView) itemView.findViewById(R.id.health_show_title_txt);
            health_show_date_txt = (TextView) itemView.findViewById(R.id.health_show_date_txt);
            health_show_head_height_txt = (TextView) itemView.findViewById(R.id.health_show_head_height_txt);
            health_show_weight_temperature_txt = (TextView) itemView.findViewById(R.id.health_show_weight_temperature_txt);
            health_show_grow_up_txt = (TextView) itemView.findViewById(R.id.health_show_grow_up_txt);
        }

        public void bindData(HealthQuota quota) {
            if (quota == null) {
                return;
            }
            health_show_head_height_txt.setText(String.format(mContext.getString(R.string.head_height_size), quota.headSize+"",quota.height+""));
            health_show_weight_temperature_txt.setText(String.format(mContext.getString(R.string.weight_temperature_size), quota.weight+"",quota.temperature+""));
            health_show_grow_up_txt.setText("体重低、头围大\n舒服舒服发舒服舒服");

        }
    }
}
