package com.lihb.babyvoice.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lihb.babyvoice.R;
import com.lihb.babyvoice.action.ApiManager;
import com.lihb.babyvoice.action.ServiceGenerator;
import com.lihb.babyvoice.model.Contributor;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


/**
 * Created by lhb on 2017/1/17.
 */

public class ContributorAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Contributor> mData = new ArrayList<Contributor>();
    private Context mContext;

    public ContributorAdapter(Context context, ArrayList<Contributor> dataList) {
        mContext = context;
        mData = dataList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.i("ContributorAdapter", "onCreateViewHolder");
        return new FollowViewHolder(LayoutInflater.from(
                parent.getContext()).inflate(R.layout.repoitem, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        FollowViewHolder followViewHolder = (FollowViewHolder) holder;
        final Contributor contributor = mData.get(position);
        followViewHolder.userNameTxt.setText(contributor.login);
        Glide.with(mContext)
                .load(mData.get(position).avatar_url)
                .placeholder(R.mipmap.ic_launcher)
                .override(200, 200)
                .into(followViewHolder.userAvatarImg);
        Log.i("ContributorAdapter", "onBindViewHolder");

        followViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ServiceGenerator.createService(ApiManager.class)
                        .followers(contributor.login)
                        .flatMap(new Func1<List<Contributor>, Observable<Contributor>>() {

                            @Override
                            public Observable<Contributor> call(List<Contributor> contributors) {
                                return Observable.from(contributors);
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<Contributor>() {

                            @Override
                            public void onStart() {
                                super.onStart();
                                mData.clear();
                            }

                            @Override
                            public void onCompleted() {
                                Log.i("MainActivity", "onCompleted");

                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.i("MainActivity", "onError");
                            }

                            @Override
                            public void onNext(Contributor contributor) {
//                                mListView.setVisibility(View.VISIBLE);
//                                mProgressBar.setVisibility(View.GONE);
                                Log.i("MainActivity", contributor.login + " : " + contributor.avatar_url);
                                mData.add(contributor);
                                notifyDataSetChanged();

                            }
                        });

            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    private class FollowViewHolder extends RecyclerView.ViewHolder {

        public TextView userNameTxt;
        public ImageView userAvatarImg;

        public FollowViewHolder(View itemView) {
            super(itemView);
            userNameTxt = (TextView) itemView.findViewById(R.id.followers_txt);
            userAvatarImg = (ImageView) itemView.findViewById(R.id.user_avatar_img);


        }
    }

    public void updateData(List<Contributor> datas) {
        mData.clear();
        mData.addAll(datas);
        notifyDataSetChanged();
    }
}
