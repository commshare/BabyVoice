package com.lihb.babyvoice.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.lihb.babyvoice.R;
import com.lihb.babyvoice.adapter.HealthProtectAdapter;
import com.lihb.babyvoice.customview.RefreshLayout;
import com.lihb.babyvoice.customview.TitleBar;
import com.lihb.babyvoice.customview.base.BaseFragment;
import com.lihb.babyvoice.customview.base.BaseRecyclerView;
import com.lihb.babyvoice.db.impl.HealthDataImpl;
import com.lihb.babyvoice.model.HealthQuota;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by lihb on 2017/3/11.
 */

public class HealthShowFragment extends BaseFragment {

    private static final String TAG = "HealthShowFragment";

    private RefreshLayout mRefreshLayout;
    private BaseRecyclerView mRecyclerView;
    private HealthProtectAdapter mHealthProtectAdapter;
    private List<HealthQuota> mData = new ArrayList<>();
    private boolean hasMoreData = false;
    private View emptyView;
    private ImageView mAdd_record_img;

    private static final int COUNT = 10;

    private TitleBar mTitleBar;
    private HealthProtectFragment mHealthProtectFragment;

    public static HealthShowFragment create() {
        return new HealthShowFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
//            mFileName = bundle.getString("fileName");
        }
        return inflater.inflate(R.layout.fragment_health_show, container, false);
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
        mAdd_record_img = (ImageView) getView().findViewById(R.id.add_health_record_img);

        mAdd_record_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoHealthProtectFragment();
            }
        });

        mRefreshLayout = (RefreshLayout) getView().findViewById(R.id.health_swipe_refresh_widget);
        mRecyclerView = (BaseRecyclerView) getView().findViewById(R.id.health_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

//        mRecyclerView.setEmptyView(emptyView);

        mHealthProtectAdapter = new HealthProtectAdapter(getContext(), mData);
        mRecyclerView.setAdapter(mHealthProtectAdapter);

        mRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mRefreshLayout.setRefreshing(true);
                getData(true);
            }
        });
//        mRefreshLayout.registerLoadMoreListenerForChildView(mRecyclerView, new RefreshLayout.OnLoadListener() {
//            @Override
//            public void onLoad() {
//                if (hasMoreData) {
//                    getData(false);
//                    return;
//                } else {
//                    CommonToast.showShortToast("加载完毕");
//                }
//                mRefreshLayout.setLoading(false);
//            }
//        });
        getData(true);

    }

    private void gotoHealthProtectFragment() {
        if (null == mHealthProtectFragment) {
            mHealthProtectFragment = HealthProtectFragment.create();
        }

        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.hide(this);
        transaction.add(R.id.main_layout, mHealthProtectFragment, "HealthProtectFragment")
                .show(mHealthProtectFragment)
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
    private void getData(final boolean refresh) {
        HealthDataImpl.getInstance()
                .queryAllData()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<List<HealthQuota>>() {
                    @Override
                    public void call(List<HealthQuota> healthQuotas) {
//                        CommonToast.showShortToast("查询成功！");
                        mData = healthQuotas;
                        mHealthProtectAdapter.updateData(mData);
                        mRefreshLayout.setRefreshing(false);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
//                        CommonToast.showShortToast("查询失败！" + throwable.getMessage());
                        Log.e(TAG, throwable.getMessage());
                        mRefreshLayout.setRefreshing(false);
                    }
                });
    }

}
