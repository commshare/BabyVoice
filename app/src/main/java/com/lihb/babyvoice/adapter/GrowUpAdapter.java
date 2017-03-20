package com.lihb.babyvoice.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lihb.babyvoice.R;
import com.lihb.babyvoice.db.GrowUpImpl;
import com.lihb.babyvoice.model.GrowUpRecord;
import com.lihb.babyvoice.view.ImageBrowseActivity;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by lihb on 2017/3/11.
 */

public class GrowUpAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<GrowUpRecord> mData;

    public GrowUpAdapter(Context mContext, List<GrowUpRecord> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(
                parent.getContext()).inflate(R.layout.grow_up_item, parent, false);
        return new GrowUpRecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof GrowUpRecordViewHolder) {
            GrowUpRecordViewHolder viewHolder = (GrowUpRecordViewHolder) holder;
            GrowUpRecord growUpRecord = mData.get(position);
            viewHolder.bindData(growUpRecord);
        }
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public void updateData(List<GrowUpRecord> data) {
        mData = data;
        notifyDataSetChanged();
    }

    private class GrowUpRecordViewHolder extends RecyclerView.ViewHolder{

        public TextView grow_up_title_txt;
        public ImageView grow_up_del_img;
        public ImageView grow_up_dot_img;
        public TextView grow_up_content_txt;
        public ImageView grow_up_content_img1;
        public ImageView grow_up_content_img2;
        public ArrayList<String> pics;
        public GrowUpRecord mRecord;


        public GrowUpRecordViewHolder(View itemView) {
            super(itemView);
            grow_up_title_txt = (TextView) itemView.findViewById(R.id.grow_up_title_txt);
            grow_up_del_img = (ImageView) itemView.findViewById(R.id.grow_up_del_img);
            grow_up_dot_img = (ImageView) itemView.findViewById(R.id.grow_up_dot_img);
            grow_up_content_txt = (TextView) itemView.findViewById(R.id.grow_up_content_txt);
            grow_up_content_img1 = (ImageView) itemView.findViewById(R.id.grow_up_content_img1);
            grow_up_content_img2 = (ImageView) itemView.findViewById(R.id.grow_up_content_img2);

            grow_up_content_img1.setOnClickListener(mOnClickListener);
            grow_up_content_img2.setOnClickListener(mOnClickListener);
            grow_up_del_img.setOnClickListener(mOnClickListener);
        }

        public void bindData(GrowUpRecord growUpRecord) {
            if (growUpRecord == null) {
                return;
            }
            mRecord = growUpRecord;
            pics = (ArrayList<String>) growUpRecord.picList;
            grow_up_title_txt.setText(growUpRecord.date);
            grow_up_content_txt.setText(growUpRecord.content);
            if (pics.size() == 1) {
                Glide.with(mContext)
                        .load(pics.get(0))
                        .into(grow_up_content_img1);
                grow_up_content_img1.setVisibility(View.VISIBLE);
                grow_up_content_img2.setVisibility(View.GONE);
            }else if (pics.size() == 2) {
                Glide.with(mContext)
                        .load(pics.get(0))
                        .into(grow_up_content_img1);
                Glide.with(mContext)
                        .load(pics.get(1))
                        .into(grow_up_content_img2);
                grow_up_content_img1.setVisibility(View.VISIBLE);
                grow_up_content_img2.setVisibility(View.VISIBLE);
            }else {
                grow_up_content_img1.setVisibility(View.GONE);
                grow_up_content_img2.setVisibility(View.GONE);
            }

        }

        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == grow_up_content_img1) {
                    ImageBrowseActivity.startActivity(mContext, pics, 0);
                } else if (v == grow_up_content_img2) {
                    ImageBrowseActivity.startActivity(mContext, pics, 1);
                } else if (v == grow_up_del_img) {
                    delItem(mRecord);
                    mData.remove(mRecord);
                    notifyItemRemoved(getLayoutPosition());
                }
            }
        };

    }

    private void delItem(final GrowUpRecord growUpRecord) {
        GrowUpImpl.getInstance()
                .delData(growUpRecord)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        if (aBoolean) {
                            com.orhanobut.logger.Logger.i("del growuprecord success, "+ growUpRecord.toString());
                        }else {
                            com.orhanobut.logger.Logger.i("del growuprecord failed, "+ growUpRecord.toString());
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        com.orhanobut.logger.Logger.e(throwable.getMessage());
                    }
                });
    }
}
