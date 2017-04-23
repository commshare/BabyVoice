package com.lihb.babyvoice.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.lihb.babyvoice.R;
import com.lihb.babyvoice.adapter.VaccineAdapter;
import com.lihb.babyvoice.customview.RefreshLayout;
import com.lihb.babyvoice.customview.StickyDecoration;
import com.lihb.babyvoice.customview.TitleBar;
import com.lihb.babyvoice.customview.base.BaseFragment;
import com.lihb.babyvoice.customview.base.BaseRecyclerView;
import com.lihb.babyvoice.db.impl.VaccineDataImpl;
import com.lihb.babyvoice.model.VaccineInfo;
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

public class VaccineFragment extends BaseFragment {

    private RefreshLayout mRefreshLayout;
    private BaseRecyclerView mRecyclerView;
    private List<VaccineInfo> mData = new ArrayList<>();
    private VaccineAdapter mVaccineAdapter;
    private boolean hasMoreData = false;
    private static final int COUNT = 10;

    private int mSelYear, mSelMonth, mSelDay;

    private TitleBar mTitleBar;

    public static VaccineFragment create() {
        return new VaccineFragment();
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
            String pregnantDateInfo = SharedPreferencesUtil.getBabyBirthDayInfo(getContext());
            String[] array = pregnantDateInfo.split("/");
            mSelYear  = Integer.valueOf(array[0]);
            mSelMonth = Integer.valueOf(array[1]);
            mSelDay   = Integer.valueOf(array[2]);
        }
        CommonToast.showShortToast(mSelYear + "/" + mSelMonth + "/" + mSelDay);
        return inflater.inflate(R.layout.fragment_vaccine, container, false);
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

        mRefreshLayout = (RefreshLayout) getView().findViewById(R.id.vaccine_refreshlayout);
        mRecyclerView = (BaseRecyclerView) getView().findViewById(R.id.vaccine_recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        mVaccineAdapter = new VaccineAdapter(getContext(), mData);

        StickyDecoration decoration = new StickyDecoration(getActivity(), new StickyDecoration.DecorationCallback() {
            @Override
            public String getGroupLabel(int position) {
                if (mData.size() == 0) {
                    return "";
                }
                int ageToInject = (mData.get(position).ageToInject);
                if (ageToInject == 0) {
                    return getString(R.string.in_twenty_four_hours);
                } else if (ageToInject >= 12) {
                    if (ageToInject % 12 == 0) {
                        return ageToInject / 12 + getString(R.string.years);
                    }else {
                        float xx = ageToInject / 12.0f;
                        return xx +  getString(R.string.years);
                    }

                }
                return mData.get(position).ageToInject+getString(R.string.month);
            }
        });
        mRecyclerView.addItemDecoration(decoration);

        mRecyclerView.setAdapter(mVaccineAdapter);

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


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden == false) {
            hideBottomTab();
            getData(true);
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
//                .getVaccineInfo(start, COUNT)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Action1<HttpResponse<HttpResList<VaccineInfo>>>() {
//                    @Override
//                    public void call(HttpResponse<HttpResList<VaccineInfo>> httpResListHttpResponse) {
//                        if (httpResListHttpResponse.code == 0) {
//                            HttpResList<VaccineInfo> httpResList = httpResListHttpResponse.data;
//                            if (refresh) {
//                                mData.clear();
//                            }
//                            hasMoreData = mData.size() < httpResList.total;
//                            List<VaccineInfo> list = httpResList.dataList;
//
//                            mData.addAll(list);
//                            mVaccineAdapter.notifyDataSetChanged();
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
        VaccineDataImpl.getInstance()
                .queryAllData()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<List<VaccineInfo>>() {
                    @Override
                    public void call(List<VaccineInfo> vaccineInfos) {
//                        CommonToast.showShortToast("查询成功！");
                        Logger.i("query vaccine data success");
                        mData = vaccineInfos;
                        mVaccineAdapter.updateData(mData);
                        mRefreshLayout.setRefreshing(false);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
//                        CommonToast.showShortToast("查询失败！" + throwable.getMessage());
                        Logger.e("query vaccine data failed. cause :%s", throwable.getMessage());
                        mRefreshLayout.setRefreshing(false);
                    }
                });
    }
}
