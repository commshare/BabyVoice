package com.lihb.babyvoice.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.lihb.babyvoice.R;
import com.lihb.babyvoice.adapter.PregnantExamineAdapter;
import com.lihb.babyvoice.customview.RefreshLayout;
import com.lihb.babyvoice.customview.TitleBar;
import com.lihb.babyvoice.customview.base.BaseFragment;
import com.lihb.babyvoice.db.impl.PregnantDataImpl;
import com.lihb.babyvoice.model.ProductionInspection;
import com.lihb.babyvoice.utils.CommonToast;
import com.lihb.babyvoice.utils.SharedPreferencesUtil;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by lihb on 2017/3/5.
 */

public class PregnantExamineFragment extends BaseFragment {

    private TitleBar mTitleBar;
    private int mSelYear, mSelMonth, mSelDay;

    private RefreshLayout mRefreshLayout;

    private RecyclerView mRecyclerView;

    private PregnantExamineAdapter mAdapter;

    private boolean hasMoreData = false;

    private List<ProductionInspection> mData = new ArrayList<>();

    public static PregnantExamineFragment create() {
        return new PregnantExamineFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            mSelYear = bundle.getInt("selYear");
            mSelMonth = bundle.getInt("selMonth");
            mSelDay = bundle.getInt("selDay");
        }

        if(mSelYear == 0 || mSelMonth == 0 || mSelDay == 0) {
            String pregnantDateInfo = SharedPreferencesUtil.getPregnantDateInfo(getContext());
            String[] array = pregnantDateInfo.split("/");
            mSelYear  = Integer.valueOf(array[0]);
            mSelMonth = Integer.valueOf(array[1]);
            mSelDay   = Integer.valueOf(array[2]);
        }
        CommonToast.showShortToast(mSelYear + "/" + mSelMonth + "/" + mSelDay);
        return inflater.inflate(R.layout.fragment_pregnant_examine, container, false);
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

        mRefreshLayout = (RefreshLayout) getView().findViewById(R.id.pregnant_swipe_refresh_widget);
        mRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mRecyclerView = (RecyclerView) getView().findViewById(R.id.pregnant_recycler_view);

        mAdapter = new PregnantExamineAdapter(getContext(), mData);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
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
//                //刷新数据的时候禁止加载更多，如果在加载更多数据的时候下拉刷新，
//                //加载更多的请求结果将不做处理。
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

    //    private void getData(final boolean refresh) {
//        int start = 0;
//        if (refresh) {
//            start = 0;
//        } else {
//            start = mData.size();
//        }
//        ServiceGenerator.createService(ApiManager.class)
//                .getProductionInfo(start, 20)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Action1<HttpResponse<HttpResList<ProductionInspection>>>() {
//                    @Override
//                    public void call(HttpResponse<HttpResList<ProductionInspection>> httpResListHttpResponse) {
//                        if (httpResListHttpResponse.code == 200) {
//                            HttpResList<ProductionInspection> httpResList = httpResListHttpResponse.data;
//                            if (refresh) {
//                                mData.clear();
//                            }
//                            hasMoreData = mData.size() < httpResList.total;
//                            List<ProductionInspection> list = httpResList.dataList;
//
//                            mData.addAll(list);
//                            mAdapter.updateData(mData);
//                            onLoadedData(refresh);
//                        }
//                    }
//                }, new Action1<Throwable>() {
//                    @Override
//                    public void call(Throwable throwable) {
//                        CommonToast.showShortToast("获取数据失败");
//                        Logger.e(throwable.toString());
//                        onLoadedData(refresh);
//                    }
//                });
//
//    }
//
//    private void onLoadedData(final boolean refresh) {
//        if (refresh) {
//            mRefreshLayout.setRefreshing(false);
//        } else {
//            mRefreshLayout.setLoading(false);
//        }
//    }
    private void getData(boolean refresh) {
        PregnantDataImpl.getInstance()
                .queryAllData()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<List<ProductionInspection>>() {
                    @Override
                    public void call(List<ProductionInspection> productionInspections) {
//                        CommonToast.showShortToast("查询成功！");
                        Logger.i("query pregnant data success");
                        mData = productionInspections;
                        mAdapter.updateData(mData);
                        mRefreshLayout.setRefreshing(false);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
//                        CommonToast.showShortToast("查询失败！" + throwable.getMessage());
                        Logger.e("query pregnant data failed. cause :%s", throwable.getMessage());
                        mRefreshLayout.setRefreshing(false);
                    }
                });
    }
}
