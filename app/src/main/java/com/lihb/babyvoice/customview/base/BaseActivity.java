package com.lihb.babyvoice.customview.base;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.lihb.babyvoice.BuildConfig;
import com.lihb.babyvoice.customview.IUiState;
import com.lihb.babyvoice.utils.NotificationCenter;
import com.lihb.babyvoice.utils.StringUtils;
import com.lihb.babyvoice.utils.ViewServer;
import com.trello.rxlifecycle.components.RxActivity;

/**
 * Activity 基类
 * <p/>
 * 请务必使每一个 Activity 都由此类派生，以支持海度统计等其他统一业务
 * <p/>
 * Created by caijw on 2015/2/5.
 */
@SuppressLint("Registered")
public abstract class BaseActivity extends RxActivity implements IUiState, View.OnClickListener {
    protected final Handler mHandler = new Handler(Looper.getMainLooper());
    private int mUiState = kUiInit;
    private String mPath = "";

    protected String getPath() {
        if (StringUtils.isBlank(this.mPath)) {
            return getLocalClassName();
        }
        return this.mPath;
    }

    protected void setPath(String path) {
        this.mPath = path;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (BuildConfig.DEBUG) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    ViewServer.get(BaseActivity.this).addWindow(BaseActivity.this);
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        NotificationCenter.INSTANCE.addObserver(this);
        mUiState = kUiActive;
        if (BuildConfig.DEBUG) {
            ViewServer.get(this).setFocusedWindow(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        NotificationCenter.INSTANCE.removeObserver(this);
        if (mUiState == kUiActive) {
            mUiState = isFinishing() ? kUiDestroyed : kUiPaused;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NotificationCenter.INSTANCE.removeObserver(this);
        mUiState = kUiDestroyed;
        if (BuildConfig.DEBUG) {
            ViewServer.get(this).removeWindow(this);
        }
    }


    @Override
    public void finish() {
        super.finish();
        NotificationCenter.INSTANCE.removeObserver(this);
        mUiState = kUiDestroyed;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mUiState != kUiDestroyed) {
            mUiState = isFinishing() ? kUiDestroyed : kUiInstanceStateSaved;
        }
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
        return mUiState >= kUiInstanceStateSaved;
    }


    @Override
    public boolean isUiDestroyed() {
        return mUiState == kUiDestroyed;
    }

    @Override
    public void onClick(View view) {

    }

    public int getResourceColor(int resId) {
        return getResources().getColor(resId);
    }

    public Handler getHandler() {
        return mHandler;
    }
}
