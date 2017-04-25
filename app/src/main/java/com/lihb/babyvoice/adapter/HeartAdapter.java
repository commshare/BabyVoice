package com.lihb.babyvoice.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lihb.babyvoice.R;
import com.lihb.babyvoice.db.impl.BabyVoiceDataImpl;
import com.lihb.babyvoice.model.BabyVoice;
import com.orhanobut.logger.Logger;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by lhb on 2017/2/9.
 */
public class HeartAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<BabyVoice> mData;

    public HeartAdapter(Context mContext, List<BabyVoice> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(
                parent.getContext()).inflate(R.layout.heart_voice_list_item, parent, false);
        return new HeartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeartViewHolder) {
            HeartViewHolder viewHolder = (HeartViewHolder) holder;
            BabyVoice voice = mData.get(position);
            viewHolder.bindData(voice);
        }
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public void removeItem(int position) {
        //从数据库中删除
        delDataFromDb(mData.get(position));
        mData.remove(position);
        notifyItemRemoved(position);
    }

    private void delDataFromDb(final BabyVoice voice) {
        BabyVoiceDataImpl.getInstance()
                .delData(voice)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        if (aBoolean) {
                            Logger.i("del voice success, "+ voice.toString());
                        }else {
                            Logger.i("del voice failed, "+ voice.toString());
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Logger.e(throwable.getMessage());
                    }
                });
    }


}
