package com.lihb.babyvoice.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lihb.babyvoice.BabyVoiceApp;
import com.lihb.babyvoice.R;
import com.lihb.babyvoice.action.ApiManager;
import com.lihb.babyvoice.action.ServiceGenerator;
import com.lihb.babyvoice.model.Article;
import com.lihb.babyvoice.model.HttpResponse;
import com.lihb.babyvoice.utils.StringUtils;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by lihb on 2017/3/11.
 */

public class PregnantZoneAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<Article> mData;

    public PregnantZoneAdapter(Context mContext, List<Article> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(
                parent.getContext()).inflate(R.layout.grow_up_item, parent, false);
        return new PregnantZoneViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof PregnantZoneViewHolder) {
            PregnantZoneViewHolder viewHolder = (PregnantZoneViewHolder) holder;
            Article article = mData.get(position);
            viewHolder.bindData(article);
        }
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public void updateData(List<Article> data) {
        mData = data;
        notifyDataSetChanged();
    }

    private class PregnantZoneViewHolder extends RecyclerView.ViewHolder{

        public TextView grow_up_title_txt;
        public ImageView grow_up_del_img;
        public ImageView grow_up_dot_img;
        public TextView grow_up_content_txt;
        public ImageView grow_up_content_img1;
        public ImageView grow_up_content_img2;
        public String[] pics;
        public Article mArticle;


        public PregnantZoneViewHolder(View itemView) {
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

        public void bindData(Article article) {
            if (article == null) {
                return;
            }
            mArticle = article;
            grow_up_title_txt.setText(article.title);
            grow_up_content_txt.setText(article.content);
            String picUrls = article.attachment;
            if (StringUtils.isBlank(picUrls)) {
                grow_up_content_img1.setVisibility(View.GONE);
                grow_up_content_img2.setVisibility(View.GONE);
                return;
            }
            if (!picUrls.contains(",")) {
                pics= new String[]{picUrls};
            }else {
                pics = picUrls.split(",");
            }
            if (pics.length == 1) {
                Glide.with(mContext)
                        .load(getPicUrl(pics[0]))
                        .into(grow_up_content_img1);
                grow_up_content_img1.setVisibility(View.VISIBLE);
                grow_up_content_img2.setVisibility(View.GONE);
            }else if (pics.length == 2) {
                Glide.with(mContext)
                        .load(getPicUrl(pics[0]))
                        .into(grow_up_content_img1);
                Glide.with(mContext)
                        .load(getPicUrl(pics[1]))
                        .into(grow_up_content_img2);
                grow_up_content_img1.setVisibility(View.VISIBLE);
                grow_up_content_img2.setVisibility(View.VISIBLE);
            }

        }

        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if (v == grow_up_del_img) {
                    delItem(mArticle);
                    mData.remove(mArticle);
                    notifyItemRemoved(getLayoutPosition());
                }
            }
        };

    }

    private void delItem(final Article article) {

        ServiceGenerator.createService(ApiManager.class)
                .delPregnantArticle(article.id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<HttpResponse<Void>>() {
                    @Override
                    public void call(HttpResponse<Void> voidHttpResponse) {
                        if (voidHttpResponse.code == 0) {
                            com.orhanobut.logger.Logger.i("del pregnant zone record success, "+ article.toString());
                        }else {
                            com.orhanobut.logger.Logger.i("del pregnantzone record failed, "+ article.toString());
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        com.orhanobut.logger.Logger.e(throwable.getMessage());
                    }
                });


    }

    /**
     * 拼接得到图片下载地址
     * @param picName
     * @return
     */
    private String getPicUrl(String picName) {
        StringBuilder sb = new StringBuilder("http://123.207.46.152:8080/itingbaby/upload/uploadImages/");
        sb.append(BabyVoiceApp.currUserName).append("/");
        sb.append(picName);
        return sb.toString();
    }
}
