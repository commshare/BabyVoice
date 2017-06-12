package com.lihb.babyvoice.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.lihb.babyvoice.R;
import com.lihb.babyvoice.customview.CommonItem;
import com.lihb.babyvoice.customview.TitleBar;
import com.lihb.babyvoice.customview.base.BaseFragment;
import com.lihb.babyvoice.utils.CommonToast;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by lhb on 2017/2/8.
 */

public class MeFragment extends BaseFragment {

    public static final int ITEM_SET_PREGNANT_DATE  = 100;
    public static final int ITEM_SET_BABY_BIRTHDAY  = 200;
    private static final String TAG = "MeFragment";

    private CommonItem itemMeCenter;
    private CommonItem itemPregnantDate;
    private CommonItem itemBabyInfo;
//    private CommonItem itemRemoteVideoAddress;
    private CommonItem itemLanguageSelect;
    private CommonItem itemAboutApp;
    private DateSelectFragment mDateSelectFragment;
    private TitleBar mTitleBar;

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
                gotoDateSelectFragment(ITEM_SET_PREGNANT_DATE);
            }
        });

        itemBabyInfo = (CommonItem) getView().findViewById(R.id.item_baby_info);
        itemBabyInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonToast.showShortToast("itemBabyInfo");
                gotoDateSelectFragment(ITEM_SET_BABY_BIRTHDAY);
            }
        });

        mTitleBar = (TitleBar) getView().findViewById(R.id.title_bar);
        mTitleBar.setLeftOnClickListener(v -> getActivity().onBackPressed());

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

    private void gotoDateSelectFragment(int type) {
        if (null == mDateSelectFragment) {
            mDateSelectFragment = DateSelectFragment.create();
        }
        Bundle bundle = new Bundle();
        bundle.putInt("itemType", type);
        mDateSelectFragment.setArguments(bundle);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.hide(this);
        transaction.add(R.id.main_layout, mDateSelectFragment, "DateSelectFragment")
                .show(mDateSelectFragment)
                .addToBackStack(null)
                .commit();

    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG);
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(TAG);
    }

}
