package com.lihb.babyvoice.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
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
import com.lihb.babyvoice.model.HealthQuota;
import com.lihb.babyvoice.utils.CommonToast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lihb on 2017/3/11.
 */

public class HealthShowFragment extends BaseFragment {

    private RefreshLayout mRefreshLayout;
    private BaseRecyclerView mRecyclerView;
    private HealthProtectAdapter mHealthProtectAdapter;
    private List<HealthQuota> mData = new ArrayList<>();
    private boolean hasMoreData = false;
    private View emptyView;
    private ImageView mAdd_record_img;

    private static final int COUNT = 10;

    private TitleBar mTitleBar;

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
                CommonToast.showShortToast("mAdd_record_img was clicked!!");
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
        mRefreshLayout.registerLoadMoreListenerForChildView(mRecyclerView, new RefreshLayout.OnLoadListener() {
            @Override
            public void onLoad() {
                if (hasMoreData) {
                    getData(false);
                    return;
                } else {
                    CommonToast.showShortToast("加载完毕");
                }
                mRefreshLayout.setLoading(false);
            }
        });
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
    private void getData(final boolean refresh) {

        List<HealthQuota> tempList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            HealthQuota quota = new HealthQuota(40+i,70+i,20+i,35+i,"女",0,0);
            tempList.add(quota);
        }
        if (refresh) {
            mData.clear();
        }
        mData.addAll(tempList);
        mHealthProtectAdapter.notifyDataSetChanged();
        hasMoreData = mData.size() < 50;
        onLoadedData(refresh);
    }



//    private void getData(final boolean refresh) {
//        int start = 0;
//        if (refresh) {
//            start = 0;
//        } else {
//            start = mData.size();
//        }
//        ServiceGenerator.createService(ApiManager.class)
//                .getBabyVoiceRecord(start, COUNT)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Action1<HttpResponse<HttpResList<BabyVoice>>>() {
//                    @Override
//                    public void call(HttpResponse<HttpResList<BabyVoice>> httpResListHttpResponse) {
//                        if (httpResListHttpResponse.code == 200) {
//                            HttpResList<BabyVoice> httpResList = httpResListHttpResponse.data;
//                            if (refresh) {
//                                mData.clear();
//                            }
//                            hasMoreData = mData.size() < httpResList.total;
//                            List<BabyVoice> list = httpResList.dataList;
//
////                            mData.addAll(list);
//                            mHealthProtectAdapter.notifyDataSetChanged();
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

    private void onLoadedData(final boolean refresh) {
        if (refresh) {
            mRefreshLayout.setRefreshing(false);
        } else {
            mRefreshLayout.setLoading(false);
        }
    }
}
