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
    private DateSelectFragment  mDateSelectFragment;

    public static final int ITEM_PREGNANT  = 1;
    public static final int ITEM_EXAMINE  = 2;
    public static final int ITEM_VACCINE  = 3;
    public static final int ITEM_HEALTH_PROTECT  = 4;
    public static final int ITEM_GROWUP  = 5;
    public static final int ITEM_CARE_MALL  = 6;
    public static final int ITEM_EXPERT_ONLINE  = 7;

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
                gotoDateSelectFragment(ITEM_EXAMINE);

            }
        });

        itemVaccineAssist = (CommonItem) getView().findViewById(R.id.item_vaccine_assist);
        itemVaccineAssist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonToast.showShortToast("itemVaccineAssist");
                gotoDateSelectFragment(ITEM_VACCINE);
            }
        });

        itemHealthProtectAssist = (CommonItem) getView().findViewById(R.id.item_health_protect_assist);
        itemHealthProtectAssist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonToast.showShortToast("itemHealthProtectAssist");
                gotoDateSelectFragment(ITEM_HEALTH_PROTECT);
            }
        });

        itemGrowUpRecord = (CommonItem) getView().findViewById(R.id.item_growup_record);
        itemGrowUpRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonToast.showShortToast("itemGrowUpRecord");
                gotoDateSelectFragment(ITEM_GROWUP);
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

}
