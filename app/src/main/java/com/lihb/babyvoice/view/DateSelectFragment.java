package com.lihb.babyvoice.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lihb.babyvoice.R;
import com.lihb.babyvoice.customview.TitleBar;
import com.lihb.babyvoice.customview.base.BaseFragment;

import java.util.Calendar;

import static com.lihb.babyvoice.R.id.datePicker;

/**
 * Created by lihb on 2017/3/5.
 */

public class DateSelectFragment  extends BaseFragment {

    private TextView mLabelTxt;
    private DatePicker mDataPicker;
    private TextView mDoneTxt;
    private TitleBar mTitleBar;
    private int type;
    private BaseFragment mFragment;
    private int mSelYear,mSelMonth, mSelDay;

    public static DateSelectFragment create() {
        return new DateSelectFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle b = getArguments();
        if (null != b) {
            type = b.getInt("itemType");
        }
        return inflater.inflate(R.layout.fragment_date_select, container, false);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    private void initView() {
        mLabelTxt = (TextView) getView().findViewById(R.id.label_txt);
        mDoneTxt = (TextView) getView().findViewById(R.id.date_sel_done_txt);
        mTitleBar = (TitleBar) getView().findViewById(R.id.title_bar);
        mTitleBar.setLeftOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        mDataPicker = (DatePicker) getView().findViewById(datePicker);

        Calendar calendar=Calendar.getInstance();
        mSelYear = calendar.get(Calendar.YEAR);
        mSelMonth = calendar.get(Calendar.MONTH)+1;
        mSelDay = calendar.get(Calendar.DAY_OF_MONTH);
        mDataPicker.init(mSelYear, mSelMonth-1, mSelDay, new DatePicker.OnDateChangedListener(){

            public void onDateChanged(DatePicker view, int year,
                                      int monthOfYear, int dayOfMonth) {
                mSelYear = year;
                mSelMonth = monthOfYear + 1;
                mSelDay = dayOfMonth;
            }

        });
        mDoneTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (type) {
                    case AssistFragment.ITEM_EXAMINE:
                        mFragment = PregnantExamineFragment.create();
                        break;
                    case AssistFragment.ITEM_VACCINE:
                        mFragment = VaccineFragment.create();
                        break;
                    case AssistFragment.ITEM_GROWUP:
                        mFragment = GrowUpFragment.create();
                        break;
                    case AssistFragment.ITEM_HEALTH_PROTECT:
                        mFragment = HealthProtectFragment.create();
                        break;

                }
                gotoNextFragment(mFragment);
            }
        });
        setLabel();
    }

    private void setLabel() {
        switch (type) {
            case AssistFragment.ITEM_EXAMINE:
                mTitleBar.setLeftText(R.string.txt_check_assist);
                mLabelTxt.setText(R.string.enter_your_pregnant_date);
                break;
            case AssistFragment.ITEM_VACCINE:
                mTitleBar.setLeftText(R.string.txt_vaccine_assist);
                mLabelTxt.setText(R.string.enter_baby_birthday);
                break;
            case AssistFragment.ITEM_GROWUP:
                mTitleBar.setLeftText(R.string.txt_growup_record);
                mLabelTxt.setText(R.string.enter_baby_birthday);
                break;
            case AssistFragment.ITEM_HEALTH_PROTECT:
                mTitleBar.setLeftText(R.string.txt_health_protect_assist);
                mLabelTxt.setText(R.string.enter_baby_birthday);
                break;

        }
    }

    private void gotoNextFragment(BaseFragment fragment) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.hide(this);
        String tag="PregnantExamineFragment";
        if (fragment instanceof PregnantExamineFragment) {
            tag = "PregnantExamineFragment";
        } else if (fragment instanceof VaccineFragment) {
            tag = "VaccineFragment";
        }
        else if (fragment instanceof HealthProtectFragment) {
            tag = "HealthProtectFragment";
        }
        else if (fragment instanceof GrowUpFragment) {
            tag = "GrowUpFragment";
        }
        Bundle bundle = new Bundle();
        bundle.putInt("selYear", mSelYear);
        bundle.putInt("selMonth", mSelMonth);
        bundle.putInt("selDay", mSelDay);
        mFragment.setArguments(bundle);
        transaction.add(R.id.main_layout, fragment, tag)
                .show(fragment)
                .addToBackStack(null)
                .commit();
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden == false) {
            hideBottomTab();
        }
    }

    private void hideBottomTab() {
        if (getActivity() == null) {
            return;
        }
        // 隐藏底部的导航栏和分割线
        ((LinearLayout) getActivity().findViewById(R.id.linearLayout1)).setVisibility(View.GONE);
        ((View) getActivity().findViewById(R.id.divider_line2)).setVisibility(View.GONE);
    }
}
