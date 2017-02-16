package com.lihb.babyvoice.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.lihb.babyvoice.R;
import com.lihb.babyvoice.customview.CommonItem;
import com.lihb.babyvoice.customview.base.BaseFragment;
import com.lihb.babyvoice.utils.CommonToast;

/**
 * Created by lhb on 2017/2/8.
 */

public class AssistFragment extends BaseFragment {

    private CommonItem itemPregnantZone;
    private CommonItem itemCheckAssist;
    private CommonItem itemVaccineAssist;
    private CommonItem itemHealthProtectAssist;
    private CommonItem itemGrowUpRecord;
    private CommonItem itemCareMall;
    private CommonItem itemExpertOnline;

    public static AssistFragment create() {
        return new AssistFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_assist, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden == false) {
            showBottomTab();
        }
    }

    private void showBottomTab() {
        if (getActivity() == null) {
            return;
        }
        // 隐藏底部的导航栏和分割线
        ((LinearLayout) getActivity().findViewById(R.id.linearLayout1)).setVisibility(View.VISIBLE);
        ((View) getActivity().findViewById(R.id.divider_line2)).setVisibility(View.VISIBLE);
    }

    private void initView() {
        itemPregnantZone = (CommonItem) getView().findViewById(R.id.item_pregnant_zone);
        itemPregnantZone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonToast.showShortToast("itemPregnantZone");
            }
        });

        itemCheckAssist = (CommonItem) getView().findViewById(R.id.item_check_assist);
        itemCheckAssist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonToast.showShortToast("itemCheckAssist");
            }
        });

        itemVaccineAssist = (CommonItem) getView().findViewById(R.id.item_vaccine_assist);
        itemVaccineAssist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonToast.showShortToast("itemVaccineAssist");
            }
        });

        itemHealthProtectAssist = (CommonItem) getView().findViewById(R.id.item_health_protect_assist);
        itemHealthProtectAssist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonToast.showShortToast("itemHealthProtectAssist");
            }
        });

        itemGrowUpRecord = (CommonItem) getView().findViewById(R.id.item_growup_record);
        itemGrowUpRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonToast.showShortToast("itemGrowUpRecord");
            }
        });

        itemCareMall = (CommonItem) getView().findViewById(R.id.item_care_mall);
        itemCareMall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonToast.showShortToast("itemCareMall");
            }
        });

        itemExpertOnline = (CommonItem) getView().findViewById(R.id.item_expert_online);
        itemExpertOnline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonToast.showShortToast("itemExpertOnline");
            }
        });

    }

}
