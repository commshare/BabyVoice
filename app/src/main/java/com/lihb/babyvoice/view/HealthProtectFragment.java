package com.lihb.babyvoice.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lihb.babyvoice.R;
import com.lihb.babyvoice.customview.TitleBar;
import com.lihb.babyvoice.customview.base.BaseFragment;
import com.lihb.babyvoice.db.HealthDataImpl;
import com.lihb.babyvoice.model.HealthQuota;
import com.lihb.babyvoice.utils.CommonToast;
import com.lihb.babyvoice.utils.StringUtils;
import com.orhanobut.logger.Logger;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by lihb on 2017/3/5.
 */

public class HealthProtectFragment extends BaseFragment {

    private TextView compute_grow_up_txt;
    private EditText head_size_edit_txt,height_size_edit_txt,weight_size_edit_txt ,temperature_edit_txt;
    private  EditText gender_edit_txt,heart_count_edit_txt,fontanel_size_edit_txt;

    private View more_item_view;
    private TextView more_item_txt;
    private HealthShowFragment mHealthShowFragment;

    private TitleBar mTitleBar;

    public static HealthProtectFragment create() {
        return new HealthProtectFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
//            mFileName = bundle.getString("fileName");
        }
        return inflater.inflate(R.layout.fragment_health_protect, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        hideBottomTab();
        initView();
    }

    private void initView() {

        mTitleBar = (TitleBar) getView().findViewById(R.id.title_bar);
        mTitleBar.setLeftOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        compute_grow_up_txt = (TextView) getView().findViewById(R.id.compute_grow_up_txt);
        head_size_edit_txt = (EditText) getView().findViewById(R.id.head_size_edit_txt);
        height_size_edit_txt = (EditText) getView().findViewById(R.id.height_size_edit_txt);
        weight_size_edit_txt = (EditText) getView().findViewById(R.id.weight_size_edit_txt);
        temperature_edit_txt = (EditText) getView().findViewById(R.id.temperature_edit_txt);

        more_item_txt = (TextView) getView().findViewById(R.id.more_item);

        compute_grow_up_txt.setOnClickListener(mOnClickListener);
        more_item_txt.setOnClickListener(mOnClickListener);


    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == compute_grow_up_txt) {
                CommonToast.showShortToast("compute_grow_up_txt clicked.");
                HealthQuota quota = new HealthQuota();
                quota.headSize = Integer.valueOf(head_size_edit_txt.getText().toString().trim());
                quota.height = Integer.valueOf(height_size_edit_txt.getText().toString().trim());
                quota.weight = Integer.valueOf(weight_size_edit_txt.getText().toString().trim());
                quota.temperature = Integer.valueOf(temperature_edit_txt.getText().toString().trim());
                if (null != more_item_view) {
                    if (StringUtils.areEqual(gender_edit_txt.getText().toString().trim(), "男")) {
                        quota.gender = 1;
                    }else {
                        quota.gender = 0;
                    }
//                    quota.gender = Integer.valueOf(gender_edit_txt.getText().toString().trim());
                    quota.fontanelSize = Integer.valueOf(fontanel_size_edit_txt.getText().toString().trim());
                    quota.heartBeat = Integer.valueOf(heart_count_edit_txt.getText().toString().trim());
                }
                insertHealthData(quota);
//                gotoHealthShowFragment();

            } else if (v == more_item_txt) {
                if (null == more_item_view) {
                    more_item_view = ((ViewStub) getView().findViewById(R.id.more_item_view_stub)).inflate();
                    gender_edit_txt = (EditText) more_item_view.findViewById(R.id.gender_edit_txt);
                    heart_count_edit_txt = (EditText) more_item_view.findViewById(R.id.heart_count_edit_txt);
                    fontanel_size_edit_txt = (EditText) more_item_view.findViewById(R.id.fontanel_size_edit_txt);
                }
                if (StringUtils.areEqual(more_item_txt.getText().toString(),
                        getString(R.string.show_more_item))) {
                    more_item_txt.setText(getString(R.string.hide_more_item));
                    more_item_view.setVisibility(View.VISIBLE);
                }else {
                    more_item_txt.setText(getString(R.string.show_more_item));
                    more_item_view.setVisibility(View.GONE);
                }
            }
        }
    };

    private void insertHealthData(final HealthQuota quota) {
        HealthDataImpl.getInstance()
                .insertData(quota)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        if (aBoolean) {
                            Logger.i("插入儿保数据成功");
                            gotoHealthShowFragment();
                        }else {
                            Logger.i("插入儿保数据失败");
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Logger.e(throwable.getMessage());
                    }
                });
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

    private void gotoHealthShowFragment() {
        if (null == mHealthShowFragment) {
            mHealthShowFragment = HealthShowFragment.create();
        }

        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.hide(this);
        transaction.add(R.id.main_layout, mHealthShowFragment, "HealthShowFragment")
                .show(mHealthShowFragment)
                .addToBackStack(null)
                .commit();

    }
}
