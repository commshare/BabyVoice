package com.lihb.babyvoice;

import android.app.Application;
import android.os.Environment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.facebook.stetho.Stetho;
import com.lihb.babyvoice.command.HeadSetPluginChangedCommand;
import com.lihb.babyvoice.command.LoginStateChangedCommand;
import com.lihb.babyvoice.utils.BroadcastWatcher;
import com.lihb.babyvoice.utils.NotificationCenter;
import com.lihb.babyvoice.utils.RxBus;
import com.lihb.babyvoice.utils.SingleOkHttpClient;
import com.lihb.babyvoice.utils.UserProfileChangedNotification;

import java.io.InputStream;

public class BabyVoiceApp extends Application {


    public static final String DATA_DIRECTORY = Environment
            .getExternalStorageDirectory() + "/babyVoiceRecord/";

    private static BabyVoiceApp instance = null;

    private boolean mScreenOn = false;

    private BroadcastWatcher mBroadcastWatcher;

    private boolean mIsLogin = false;

    /**
     * 耳机是否插入
     */
    private boolean mIsPlugIn = false;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        //Stetho
        Stetho.initializeWithDefaults(this);
        //Glide与OkHttpClient集成

        //Glide与OkHttpClient集成
        Glide.get(this)
                .register(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(SingleOkHttpClient.getInstance()));
//        NotificationCenter.INSTANCE.addCallbacks(PhoneStateChangedCallback.class);
        NotificationCenter.INSTANCE.addCallbacks(UserProfileChangedNotification.class);

        mBroadcastWatcher = new BroadcastWatcher(this);
        mBroadcastWatcher.startWatch();

    }

    public static BabyVoiceApp getInstance() {
        return instance;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        if (mBroadcastWatcher != null) {
            mBroadcastWatcher.stopWatch();
            mBroadcastWatcher = null;
        }
    }

    public boolean getLogin() {
        return mIsLogin;
    }

    public void setLogin(boolean value) {
        if (mIsLogin != value) {
            mIsLogin = value;
            LoginStateChangedCommand.LoginState loginState = LoginStateChangedCommand.LoginState.LOGIN_OFF;
            if (value) {
                loginState = LoginStateChangedCommand.LoginState.LOGIN_ON;
            }
            LoginStateChangedCommand loginStateChangedCommand = new LoginStateChangedCommand(loginState);
            RxBus.getDefault().post(loginStateChangedCommand);
        }
    }

    public boolean isPlugIn() {
        return mIsPlugIn;
    }

    public void setPlugIn(boolean isPlugIn) {
        if (mIsPlugIn != isPlugIn) {
            mIsPlugIn = isPlugIn;
            HeadSetPluginChangedCommand.HeadSetPluginState headSetPluginState = HeadSetPluginChangedCommand.HeadSetPluginState.HEAD_SET_OUT;
            if (isPlugIn) {
                headSetPluginState = HeadSetPluginChangedCommand.HeadSetPluginState.HEAD_SET_IN;
            }
            HeadSetPluginChangedCommand headSetPluginChangedCommand = new HeadSetPluginChangedCommand(headSetPluginState);
            RxBus.getDefault().post(headSetPluginChangedCommand);
        }
    }

    public boolean isScreenOn() {
        return mScreenOn;
    }

    public void setScreenOn(boolean mScreenOn) {
        this.mScreenOn = mScreenOn;
    }


}
