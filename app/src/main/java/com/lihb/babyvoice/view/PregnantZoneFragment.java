package com.lihb.babyvoice.view;

import android.content.Intent;
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
import com.lihb.babyvoice.action.ApiManager;
import com.lihb.babyvoice.action.ServiceGenerator;
import com.lihb.babyvoice.adapter.PregnantZoneAdapter;
import com.lihb.babyvoice.customview.RefreshLayout;
import com.lihb.babyvoice.customview.TitleBar;
import com.lihb.babyvoice.customview.base.BaseFragment;
import com.lihb.babyvoice.customview.base.BaseRecyclerView;
import com.lihb.babyvoice.model.Article;
import com.lihb.babyvoice.model.ITingBeiResponse;
import com.lihb.babyvoice.utils.CommonToast;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by lhb on 2017/2/8.
 */

public class PregnantZoneFragment extends BaseFragment {

    private RefreshLayout mRefreshLayout;
    private BaseRecyclerView mRecyclerView;
    private PregnantZoneAdapter mAdapter;
    private List<Article> mData = new ArrayList<>();
    private View emptyView;
    private ImageView mAdd_growup_record_img;

    private boolean hasMoreData = false;
    private static final int COUNT = 10;

    private TitleBar mTitleBar;


    public static PregnantZoneFragment create() {
        return new PregnantZoneFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pregnant_zone, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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
        mAdd_growup_record_img = (ImageView) getView().findViewById(R.id.add_pregnant_zone_record_img);

        mAdd_growup_record_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoEditGrowUpRecordActivity();
            }
        });

        mRefreshLayout = (RefreshLayout) getView().findViewById(R.id.pregnant_zone_swipe_refresh_widget);
        mRecyclerView = (BaseRecyclerView) getView().findViewById(R.id.pregnant_zone_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

//        mRecyclerView.setEmptyView(emptyView);

        mAdapter = new PregnantZoneAdapter(getContext(), mData);
        mRecyclerView.setAdapter(mAdapter);

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
            showBottomTab();
            getData(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getData(true);
    }

    private void showBottomTab() {
        if (getActivity() == null) {
            return;
        }
        // 隐藏底部的导航栏和分割线
        ((LinearLayout) getActivity().findViewById(R.id.linearLayout1)).setVisibility(View.VISIBLE);
        ((View) getActivity().findViewById(R.id.divider_line2)).setVisibility(View.VISIBLE);
    }

    private void getData(final boolean refresh) {

        int page = 1;
        if (refresh) {
            page = 1;
        } else {
            page++;
        }
        ServiceGenerator.createService(ApiManager.class)
                .getPregnantArticleList(page, COUNT, 10000)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ITingBeiResponse<Article>>() {
                    @Override
                    public void call(ITingBeiResponse<Article> httpResListHttpResponse) {
                        List<Article> list = httpResListHttpResponse.list;
                        if (refresh) {
                            mData.clear();
                        }

                        mData.addAll(list);

                        hasMoreData = mData.size() < httpResListHttpResponse.total;

                        mAdapter.notifyDataSetChanged();
                        onLoadedData(refresh);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        CommonToast.showShortToast("获取数据失败");
                        Logger.e(throwable.toString());
                        onLoadedData(refresh);
                    }
                });
    }

    private void onLoadedData(final boolean refresh) {
        if (refresh) {
            mRefreshLayout.setRefreshing(false);
        } else {
            mRefreshLayout.setLoading(false);
        }
    }

    private void gotoEditGrowUpRecordActivity() {
        Intent intent = new Intent(getActivity(), EditGrowUpRecordActivity.class);

        intent.putExtra("from", EditGrowUpRecordActivity.From.PREGNANT_ZONE_FRAGMENT);

        startActivity(intent);
    }

}
