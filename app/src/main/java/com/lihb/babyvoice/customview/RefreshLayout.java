package com.lihb.babyvoice.customview;

/**
 * Created by chenjh on 2016/3/21.
 * Desc:滑到底部加载更多数据的SwipeRefreshLayout
 */

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;

public class RefreshLayout extends BetterSwipeRefreshLayout {

    private int mLastIndex;
    private OnLoadListener mLoadListener;

    private boolean isLoading = false;

    public RefreshLayout(Context context) {
        super(context);
    }

    public RefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public final void registerLoadMoreListenerForChildView(@NonNull final RecyclerView mRecyclerView, @NonNull final OnLoadListener mListener) {
        this.mLoadListener = mListener;
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                mLastIndex = getLastVisiblePosition(recyclerView.getLayoutManager());
                super.onScrolled(recyclerView, dx, dy);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && mLastIndex + 1 == mRecyclerView.getAdapter().getItemCount()) {
                    if (!isLoading() && mLoadListener != null) {
                        isLoading = true;
                        mLoadListener.onLoad();
                    }
                }
            }
        });
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        this.isLoading = loading;
    }

    /**
     * 获取最后一条展示的位置
     *
     * @return
     */
    private int getLastVisiblePosition(RecyclerView.LayoutManager layoutManager) {
        int position;
        if (layoutManager instanceof GridLayoutManager || layoutManager instanceof LinearLayoutManager) {
            position = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager mLayoutManager = (StaggeredGridLayoutManager) layoutManager;
            int[] lastPositions = mLayoutManager.findLastVisibleItemPositions(new int[mLayoutManager.getSpanCount()]);
            position = getMaxPosition(lastPositions);
        } else {
            position = layoutManager.getItemCount() - 1;
        }
        return position;
    }

    /**
     * 获得最大的位置
     *
     * @param positions
     * @return
     */
    private int getMaxPosition(int[] positions) {
        int size = positions.length;
        int maxPosition = Integer.MIN_VALUE;
        for (int i = 0; i < size; i++) {
            maxPosition = Math.max(maxPosition, positions[i]);
        }
        return maxPosition;
    }

    public interface OnLoadListener {
        void onLoad();
    }
}