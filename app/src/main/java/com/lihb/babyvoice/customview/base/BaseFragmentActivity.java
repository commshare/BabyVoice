package com.lihb.babyvoice.customview.base;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.WindowManager;

import com.lihb.babyvoice.BuildConfig;
import com.lihb.babyvoice.customview.IUiState;
import com.lihb.babyvoice.utils.StringUtils;
import com.lihb.babyvoice.utils.ViewServer;
import com.trello.rxlifecycle.components.support.RxFragmentActivity;

/**
 * FragmentActivity 基类
 * <p/>
 * 请务必使每一个 FragmentActivity 都由此类派生，以支持海度统计等其他统一业务
 * <p/>
 * Created by caijw on 2015/2/5.
 */
public abstract class BaseFragmentActivity extends RxFragmentActivity implements IUiState {

    protected final Handler mHandler = new Handler(Looper.getMainLooper());
    private int mUiState = kUiInit;
    private String path = "";

    protected String getPath() {
        if (StringUtils.isBlank(this.path)) {
            return getLocalClassName();
        }
        return this.path;
    }

    protected void setPath(String path) {
        this.path = path;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    ViewServer.get(BaseFragmentActivity.this)
                            .addWindow(BaseFragmentActivity.this);
                }
            });
        }
        // 设置状态栏透明度
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0及以上
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//4.4到5.0
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

//        NotificationCenter.INSTANCE.addObserver(this);
        mUiState = kUiActive;
        if (BuildConfig.DEBUG) {
            ViewServer.get(this).setFocusedWindow(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

//        NotificationCenter.INSTANCE.removeObserver(this);
        if (mUiState == kUiActive) {
            mUiState = isFinishing() ? kUiDestroyed : kUiPaused;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

//        NotificationCenter.INSTANCE.removeObserver(this);
        mUiState = kUiDestroyed;
        if (BuildConfig.DEBUG) {
            ViewServer.get(this).removeWindow(this);
        }
    }

    @Override
    public void finish() {
        super.finish();

//        NotificationCenter.INSTANCE.removeObserver(this);
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
        return mUiState >= kUiPaused;
    }


    @Override
    public boolean isUiDestroyed() {
        return mUiState == kUiDestroyed;
    }
}
