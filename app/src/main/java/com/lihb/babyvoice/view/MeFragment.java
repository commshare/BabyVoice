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

public class MeFragment extends BaseFragment {

    private CommonItem itemMeCenter;
    private CommonItem itemPregnantDate;
    private CommonItem itemBabyInfo;
//    private CommonItem itemRemoteVideoAddress;
    private CommonItem itemLanguageSelect;
    private CommonItem itemAboutApp;

    public static MeFragment create() {
        return new MeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_me, container, false);
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
        itemMeCenter = (CommonItem) getView().findViewById(R.id.item_me_center);
        itemMeCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonToast.showShortToast("itemMeCenter");
            }
        });

        itemPregnantDate = (CommonItem) getView().findViewById(R.id.item_pregnant_date);
        itemPregnantDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonToast.showShortToast("itemPregnantDate");
            }
        });

        itemBabyInfo = (CommonItem) getView().findViewById(R.id.item_baby_info);
        itemBabyInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonToast.showShortToast("itemBabyInfo");
            }
        });

//        itemRemoteVideoAddress = (CommonItem) getView().findViewById(R.id.item_remote_video_address);
//        itemRemoteVideoAddress.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                CommonToast.showShortToast("itemRemoteVideoAddress");
//            }
//        });

        itemLanguageSelect = (CommonItem) getView().findViewById(R.id.item_language_select);
        itemLanguageSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonToast.showShortToast("itemLanguageSelect");
            }
        });

        itemAboutApp = (CommonItem) getView().findViewById(R.id.item_about_app);
        itemAboutApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonToast.showShortToast("itemAboutApp");
            }
        });


    }

}
