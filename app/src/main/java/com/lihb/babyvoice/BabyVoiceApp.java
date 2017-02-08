package com.lihb.babyvoice;

import android.app.Application;

import com.bumptech.glide.Glide;
import com.bumptech.glide.integration.okhttp.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.facebook.stetho.Stetho;
import com.lihb.babyvoice.utils.BroadcastWatcher;
import com.lihb.babyvoice.utils.SingleOkHttpClient;

import java.io.InputStream;

public class BabyVoiceApp extends Application {

    private static BabyVoiceApp instance = null;

    private boolean mScreenOn = false;

    private BroadcastWatcher mBroadcastWatcher;


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

    public boolean isScreenOn() {
        return mScreenOn;
    }

    public void setScreenOn(boolean mScreenOn) {
        this.mScreenOn = mScreenOn;
    }
}
