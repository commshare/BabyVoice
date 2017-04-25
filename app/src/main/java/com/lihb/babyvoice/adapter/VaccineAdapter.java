package com.lihb.babyvoice.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lihb.babyvoice.R;
import com.lihb.babyvoice.customview.DividerLine;
import com.lihb.babyvoice.db.impl.VaccineDataImpl;
import com.lihb.babyvoice.model.VaccineInfo;
import com.lihb.babyvoice.utils.CommonDialog;
import com.lihb.babyvoice.utils.CommonToast;
import com.lihb.babyvoice.utils.SimpleDatePickerDialog;
import com.orhanobut.logger.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by lhb on 2017/3/6.
 */

public class VaccineAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<VaccineInfo> mData;
    private SimpleDatePickerDialog datePickerDialog;

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
        Log.i("lihb","onBindViewHolder 2-----");

        if (holder instanceof VaccineViewHolder) {
            VaccineViewHolder viewHolder = (VaccineViewHolder) holder;
            VaccineInfo vaccineInfo = mData.get(position);
            viewHolder.bindData(vaccineInfo);
        }
    }

//    @Override
//    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List<Object> payloads) {
//        Log.i("lihb","onBindViewHolder 3-----");
//    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public void updateData(List<VaccineInfo> dataList) {
        mData = dataList;
        notifyDataSetChanged();
    }

    public class VaccineViewHolder extends RecyclerView.ViewHolder {

        public TextView vaccine_name_txt;
        public TextView vaccine_inject_txt;

        public ImageView vaccine_label_img;
        public ImageView vaccine_inject_img;

        public DividerLine dividerLine;

        public VaccineViewHolder(View itemView) {
            super(itemView);
            vaccine_label_img = (ImageView) itemView.findViewById(R.id.vaccine_label_img);
            vaccine_inject_img = (ImageView) itemView.findViewById(R.id.vaccine_inject_img);
            vaccine_name_txt = (TextView) itemView.findViewById(R.id.vaccine_name_txt);
            vaccine_inject_txt = (TextView) itemView.findViewById(R.id.vaccine_inject_txt);
            dividerLine = (DividerLine) itemView.findViewById(R.id.bottom_divider_line);
        }


        public void bindData(final VaccineInfo vaccineInfo) {
            if (vaccineInfo == null) {
                return;
            }
            vaccine_name_txt.setText(vaccineInfo.vaccineName);
            if (vaccineInfo.isInjected == 1) {
                vaccine_inject_img.setImageResource(R.mipmap.selected);
                vaccine_inject_txt.setText(String.format(mContext.getString(R.string.injected), vaccineInfo.injectDate));
                vaccine_inject_txt.setTextColor(ContextCompat.getColor(mContext, R.color.color_999999));
            } else {
                vaccine_inject_img.setImageResource(R.mipmap.normal);
                vaccine_inject_txt.setText(mContext.getString(R.string.not_injected));
                vaccine_inject_txt.setTextColor(ContextCompat.getColor(mContext, R.color.text_black));
            }
            vaccine_inject_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String now = new SimpleDateFormat("yyyyMMdd").format(new Date());
                    if (vaccineInfo.isInjected == 0 ) {
                        datePickerDialog = new SimpleDatePickerDialog.Builder()
                                .setContext(mContext)
                                .setTitleId(R.string.vaccine_injecte_date_dialog_title)
                                .setYYYYMMDD(now)
                                .setConfirmListener(new CommonDialog.OnActionListener() {
                                    @Override
                                    public void onAction(int which) {
                                        String injectDate = datePickerDialog.getYYYYMMDD();
                                        if (injectDate.compareTo(now) > 0) {
                                            // 选择了一个非法日期，不改变状态
                                            CommonToast.showShortToast(R.string.select_pass_date);
                                            return;
                                        }
                                        // 更新数据库
                                        vaccineInfo.injectDate = injectDate;
                                        vaccineInfo.isInjected = 1;
                                        updateDBData(vaccineInfo, getLayoutPosition());
                                    }
                                })
                                .build();
                        datePickerDialog.show();
                    }else {
                        // 更新数据库
                        vaccineInfo.injectDate = "";
                        vaccineInfo.isInjected = 0;
                        updateDBData(vaccineInfo, getLayoutPosition());
                    }
                }
            });

           /* vaccine_check_box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {

                    if (isChecked && vaccineInfo.isInjected == 0 ) {
                        datePickerDialog = new SimpleDatePickerDialog.Builder()
                                .setContext(mContext)
                                .setTitleId(R.string.vaccine_injecte_date_dialog_title)
                                .setYYYYMMDD(now)
                                .setConfirmListener(new CommonDialog.OnActionListener() {
                                    @Override
                                    public void onAction(int which) {
                                        String injectDate = datePickerDialog.getYYYYMMDD();
                                        if (injectDate.compareTo(now) > 0) {
                                            // 选择了一个非法日期，不改变状态
                                            CommonToast.showShortToast(R.string.select_pass_date);
                                            return;
                                        }
                                        // 更新数据库
                                        vaccineInfo.injectDate = injectDate;
                                        vaccineInfo.isInjected = 1;
                                        updateDBData(vaccineInfo, getLayoutPosition());
                                    }
                                })
                                .setCancelListener(new CommonDialog.OnActionListener() {
                                    @Override
                                    public void onAction(int which) {
                                        // 点击取消按钮，不改变状态
                                        vaccine_check_box.setChecked(!isChecked);
                                    }
                                })
                                .build();
                        datePickerDialog.show();
                    }else {
                        vaccine_inject_txt.setText(mContext.getString(R.string.not_injected));
                    }
                }
            });*/
            if (isLastInGroup(getLayoutPosition())) {
                dividerLine.setVisibility(View.GONE);
            } else {
                dividerLine.setVisibility(View.VISIBLE);
            }

        }
    }

    /**
     * 更新本地数据库
     * @param vaccineInfo
     */
    private void updateDBData(final VaccineInfo vaccineInfo, final int postion) {
        VaccineDataImpl.getInstance()
                .updateData(vaccineInfo)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        if (aBoolean) {
                            Logger.i("update vaccin info success.");
                            notifyItemChanged(postion);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Logger.e("update vaccine info failed. cause :%s", throwable.getMessage());
                    }
                });
    }

    private boolean isFirstInGroup(int position) {
        boolean isFirst;
        if (position == 0) {
            isFirst = true;
        } else {
            if (mData.get(position).ageToInject ==
                    (mData.get(position - 1).ageToInject)) {
                isFirst = false;
            } else {
                isFirst = true;
            }
        }
        return isFirst;
    }

    private boolean isLastInGroup(int pos) {

        int preAge = mData.get(pos).ageToInject;
        int nextAge;
        try {
            nextAge = mData.get(pos + 1).ageToInject;
        } catch (IndexOutOfBoundsException exception) {
            return true;
        }

        if (!(preAge == nextAge)) return true;

        return false;
    }
}
