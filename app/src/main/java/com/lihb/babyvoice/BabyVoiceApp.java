package com.lihb.babyvoice;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.bumptech.glide.Glide;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.facebook.stetho.Stetho;
import com.lihb.babyvoice.utils.BroadcastWatcher;
import com.lihb.babyvoice.utils.NotificationCenter;
import com.lihb.babyvoice.utils.SingleOkHttpClient;
import com.lihb.babyvoice.utils.UserProfileChangedNotification;

import java.io.InputStream;

public class BabyVoiceApp extends Application {

    private final static String SHARED_PREF_NAME = "SHARE_PREF_FOR_100_ASK";
    private final static String ISFIRST_LAUNCH_KEY = "isfirst_launch_pref_key";

    private static BabyVoiceApp instance = null;

    private boolean mScreenOn = false;

    private BroadcastWatcher mBroadcastWatcher;
    private SharedPreferences mSharedPref;



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

        mSharedPref = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
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

    public boolean isScreenOn() {
        return mScreenOn;
    }

    public void setScreenOn(boolean mScreenOn) {
        this.mScreenOn = mScreenOn;
    }

    public boolean isFirstLaunch() {
        return mSharedPref.getBoolean(ISFIRST_LAUNCH_KEY, true);
    }

    public void setFirstLaunch(boolean firstLaunch) {
        mSharedPref.edit().putBoolean(ISFIRST_LAUNCH_KEY, firstLaunch).apply();
    }
}
