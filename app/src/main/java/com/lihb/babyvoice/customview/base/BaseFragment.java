package com.lihb.babyvoice.customview.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lihb.babyvoice.customview.IUiState;
import com.lihb.babyvoice.utils.NotificationCenter;
import com.lihb.babyvoice.utils.StringUtils;
import com.trello.rxlifecycle.components.support.RxFragment;

/**
 * Fragment 基类
 * <p/>
 * 请务必使每一个 Fragment 都由此类派生，以支持海度统计等其他统一业务
 * <p/>
 * Created by caijw on 2015/2/5.
 */
public class BaseFragment extends RxFragment implements IUiState {

    private int mUiState = kUiInit;
    private String mPath = "";

    protected String getPath() {
        if (StringUtils.isBlank(this.mPath)) {
            return getClass().getSimpleName();
        }
        return this.mPath;
    }

    protected void setPath(String path) {
        this.mPath = path;
    }

    @Override
    public void onAttach(android.app.Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        NotificationCenter.INSTANCE.addObserver(this);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mUiState = kUiActive;
    }

    @Override
    public void onPause() {
        super.onPause();
        mUiState = kUiPaused;
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUiState = kUiDestroyed;
        NotificationCenter.INSTANCE.removeObserver(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mUiState = kUiDestroyed;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mUiState = kUiDestroyed;
    }

    @Override
    public boolean isUiActive() {
        return mUiState == kUiActive;
    }

    @Override
    public boolean isUiPaused() {
        return mUiState > kUiActive;
    }

    @Override
    public boolean isUiInstanceStateSaved() {
        return mUiState >= kUiPaused;
    }

    @Override
    public boolean isUiDestroyed() {
        return mUiState == kUiDestroyed;
    }
}
