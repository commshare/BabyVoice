package com.lihb.babyvoice.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.lihb.babyvoice.R;
import com.lihb.babyvoice.adapter.GrowUpAdapter;
import com.lihb.babyvoice.customview.RefreshLayout;
import com.lihb.babyvoice.customview.TitleBar;
import com.lihb.babyvoice.customview.base.BaseFragment;
import com.lihb.babyvoice.customview.base.BaseRecyclerView;
import com.lihb.babyvoice.db.impl.GrowUpImpl;
import com.lihb.babyvoice.model.GrowUpRecord;
import com.lihb.babyvoice.utils.CommonToast;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by lihb on 2017/3/5.
 */

public class GrowUpFragment extends BaseFragment {

    private RefreshLayout mRefreshLayout;
    private BaseRecyclerView mRecyclerView;
    private GrowUpAdapter mGrowUpAdapter;
    private List<GrowUpRecord> mData = new ArrayList<>();
    private boolean hasMoreData = false;
    private View emptyView;
    private ImageView mAdd_growup_record_img;

    private static final int COUNT = 10;

    private TitleBar mTitleBar;
    private EditGrowUpRecordActivity mEditGrowUpRecordFragment;

    public static GrowUpFragment create() {
        return new GrowUpFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
//            mFileName = bundle.getString("fileName");
        }
        return inflater.inflate(R.layout.fragment_grow_up, container, false);
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
        mAdd_growup_record_img = (ImageView) getView().findViewById(R.id.add_grow_up_record_img);

        mAdd_growup_record_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonToast.showShortToast("mAdd_grow_up_record_img was clicked!!");
                gotoEditGrowUpRecordActivity();
            }
        });

        mRefreshLayout = (RefreshLayout) getView().findViewById(R.id.grow_up_swipe_refresh_widget);
        mRecyclerView = (BaseRecyclerView) getView().findViewById(R.id.grow_up_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

//        mRecyclerView.setEmptyView(emptyView);

        mGrowUpAdapter = new GrowUpAdapter(getContext(), mData);
        mRecyclerView.setAdapter(mGrowUpAdapter);

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

    @Override
    public void onResume() {
        super.onResume();
        getData(true);
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

        GrowUpImpl.getInstance()
                .queryAllData()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<List<GrowUpRecord>>() {
                    @Override
                    public void call(List<GrowUpRecord> growUpRecords) {
                        Logger.i("growUpRecords size = %d", growUpRecords.size());
                        if (!growUpRecords.isEmpty()) {
                            mData = growUpRecords;
                        }
                        mGrowUpAdapter.updateData(mData);
                        mRefreshLayout.setRefreshing(false);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e("GrowUpFragment", throwable.getMessage());
                        mRefreshLayout.setRefreshing(false);
                    }
                });
//        List<GrowUpRecord> tempList = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            String content = "宝贝今天会走路了，会叫爸爸妈妈了，哈哈。" + i;
//            List<String> picList = new ArrayList<>();
//            picList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1489246556661&di=a277f1407cee312b4a555a80b32cbe4f&imgtype=0&src=http%3A%2F%2Fp.3761.com%2Fpic%2F95231402965163.jpg");
////            picList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1489246626842&di=60720be2120548c5a3455483e4561063&imgtype=0&src=http%3A%2F%2Fwww.wsfjq.com%2Fphotos%2Fbd16565644.jpg");
//            picList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1489252122032&di=e00d02c7e75eca7741f7d01069ffbc61&imgtype=0&src=http%3A%2F%2Fimg.pconline.com.cn%2Fimages%2Fupload%2Fupc%2Ftx%2Fphotoblog%2F1112%2F28%2Fc11%2F10084076_10084076_1325087736046.jpg");
//            GrowUpRecord quota = new GrowUpRecord("2017/2/23",content,picList);
//            tempList.add(quota);
//        }
//        if (refresh) {
//            mData.clear();
//        }
//        mData.addAll(tempList);
//        mGrowUpAdapter.notifyDataSetChanged();
//        hasMoreData = mData.size() < 50;
//        onLoadedData(refresh);
    }

//    private void onLoadedData(final boolean refresh) {
//        if (refresh) {
//            mRefreshLayout.setRefreshing(false);
//        } else {
//            mRefreshLayout.setLoading(false);
//        }
//    }

    private void gotoEditGrowUpRecordActivity() {
        Intent intent = new Intent(getActivity(), EditGrowUpRecordActivity.class);

        startActivity(intent);
    }
}
