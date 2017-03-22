package com.lihb.babyvoice.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lihb.babyvoice.R;
import com.lihb.babyvoice.customview.DividerLine;
import com.lihb.babyvoice.db.impl.PregnantDataImpl;
import com.lihb.babyvoice.model.ProductionInspection;
import com.lihb.babyvoice.utils.CommonToast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by lhb on 2017/3/8.
 */

public class PregnantExamineAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // 数据类型
    public static final int TYPE_GROUP = 0;
    public static final int TYPE_ITEM = 1;

    private Context mContext;
    private List<ProductionInspection> mDataList;

    private SortedMap<Integer, List<ProductionInspection>> dataMap = new TreeMap<>();

    private HashMap<Integer, String> mGroupPosition = new HashMap<Integer, String>();

    private HashMap<Integer, ProductionInspection> mItemPosition = new HashMap<Integer, ProductionInspection>();
    private int count;


    public PregnantExamineAdapter(Context mContext, List<ProductionInspection> mData) {
        this.mContext = mContext;
        this.mDataList = mData;
    }

    public List<ProductionInspection> getData() {
        return mDataList;
    }

    public Object getItem(int position) {
        if (mGroupPosition.containsKey(position)) {
            return mGroupPosition.get(position);
        } else {
            return mItemPosition.get(position);
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == TYPE_GROUP) {
            view = LayoutInflater.from(
                    parent.getContext()).inflate(R.layout.pregnant_examine_group_item, parent, false);
            return new GroupViewHolder(view);
        } else {
            view = LayoutInflater.from(
                    parent.getContext()).inflate(R.layout.pregnant_examine_item, parent, false);
            return new PregnantViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof GroupViewHolder) {
            GroupViewHolder groupViewHolder = (GroupViewHolder) holder;
            groupViewHolder.groupTitle.setText((String) getItem(position));
        } else {
            PregnantViewHolder pregnantViewHolder = (PregnantViewHolder) holder;
            ProductionInspection productionInspection = (ProductionInspection) getItem(position);
            pregnantViewHolder.bindData(productionInspection);

        }

    }

    @Override
    public int getItemViewType(int position) {
//        if (mDataList == null || mDataList.isEmpty()) {
//            return EMPTY_VIEW;
//        }
        if (mGroupPosition.containsKey(position)) {
            return TYPE_GROUP;
        }
        return TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        if (mDataList.isEmpty()) {
            return 0;
        }

        count = 0;
        mGroupPosition.clear();
        mItemPosition.clear();
        Set<Integer> keys = dataMap.keySet();
        for (Integer key : keys) {
            mGroupPosition.put(count++, "第"+key+"次");
            List<ProductionInspection> list = dataMap.get(key);
            for (int i = 0; i < list.size(); i++) {
                mItemPosition.put(count++, list.get(i));
            }
        }
        return count;
    }

    public void updateData(final List<ProductionInspection> listData) {
        mDataList = listData;
        buildMapData();
        notifyDataSetChanged();
    }

    private void buildMapData() {
        dataMap.clear();
        if (mDataList.isEmpty()) {
            return;
        }

        List<ProductionInspection> list = new ArrayList<>();
        int oldNo = mDataList.get(0).no;
        for (int i = 0; i < mDataList.size(); i++) {
            final ProductionInspection inspection = mDataList.get(i);
            int newNo = inspection.no;
            if (oldNo == newNo) {
                list.add(inspection);
            } else {
                dataMap.put(oldNo, list);
                list = new ArrayList<>();
                list.add(inspection);
                oldNo = newNo;
            }
        }
        dataMap.put(oldNo, list); // 处理最后一组数据
    }
    private void updateDatabase(final ProductionInspection inspection){
        PregnantDataImpl.getInstance()
                .updateData(inspection)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        Log.i("PregnantExamineAdapter", inspection.toString());
                        if (aBoolean) {
                            CommonToast.showShortToast("更新数据成功");
                        }else {
                            CommonToast.showShortToast("更新数据失败");
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        CommonToast.showShortToast("error"+throwable.getMessage());
                        Log.e("PregnantExamineAdapter", throwable.getMessage());
                    }
                });
    }


    private class GroupViewHolder extends RecyclerView.ViewHolder {

        public TextView groupTitle;

        public GroupViewHolder(View itemView) {
            super(itemView);
            groupTitle = (TextView) itemView.findViewById(R.id.pregnant_group_title_txt);
        }
    }

    private class PregnantViewHolder extends RecyclerView.ViewHolder {
        public TextView pregnantIndexTxt;
        public TextView pregnantTitleTxt;
        public ImageView pregnantDoneImg;
        public RelativeLayout pregnantContentRl;
        public DividerLine dividerLine;

        public PregnantViewHolder(View itemView) {
            super(itemView);
            pregnantIndexTxt = (TextView) itemView.findViewById(R.id.pregnant_index_txt);
            pregnantTitleTxt = (TextView) itemView.findViewById(R.id.pregnant_title_txt);
            pregnantDoneImg = (ImageView) itemView.findViewById(R.id.pregnant_done_img);
            pregnantContentRl = (RelativeLayout) itemView.findViewById(R.id.pregnant_content_rl);
            dividerLine = (DividerLine) itemView.findViewById(R.id.divider_line);
        }

        public void bindData(final ProductionInspection inspection) {
            if (null == inspection) {
                return;
            }
            pregnantIndexTxt.setText(inspection.event_id + "");
            pregnantTitleTxt.setText(inspection.event_name);
            if (inspection.isDone == 1) {
                pregnantDoneImg.setImageResource(R.mipmap.selected);
            } else {
                pregnantDoneImg.setImageResource(R.mipmap.normal);

            }
            int position = getLayoutPosition();
            if (mGroupPosition.containsKey(position - 1)) {
                // 该组第一个item
                if ((position + 1) < count && mGroupPosition.containsKey(position + 1) || (position == count - 1)) {
                    //同时，也是该组最后一个
                    dividerLine.setVisibility(View.GONE);
                    pregnantContentRl.setBackgroundResource(R.drawable.pregant_item_shape);
                } else {
                    dividerLine.setVisibility(View.VISIBLE);
                    pregnantContentRl.setBackgroundResource(R.drawable.round_rect_top);
                }
            } else if ((position + 1) < count && mGroupPosition.containsKey(position + 1) || (position == count - 1)) {
                // 该组最后一个
                pregnantContentRl.setBackgroundResource(R.drawable.round_rect_bottom);
                dividerLine.setVisibility(View.GONE);
            } else {
                // 中间item
                dividerLine.setVisibility(View.VISIBLE);
                pregnantContentRl.setBackgroundResource(R.drawable.round_rect_center);
            }
            pregnantDoneImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (inspection.isDone==1) {
                        inspection.isDone = 0;
                        pregnantDoneImg.setImageResource(R.mipmap.normal);
                        updateDatabase(inspection);
                    }else {
                        inspection.isDone = 1;
                        pregnantDoneImg.setImageResource(R.mipmap.selected);
                        updateDatabase(inspection);
                    }
                }
            });

        }
    }

}
