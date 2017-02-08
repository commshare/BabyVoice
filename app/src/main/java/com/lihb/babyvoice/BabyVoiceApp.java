package com.lihb.babyvoice;

import android.app.Application;

import com.bumptech.glide.Glide;
import com.bumptech.glide.integration.okhttp.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.facebook.stetho.Stetho;
import com.lihb.babyvoice.utils.NotificationCenter;
import com.lihb.babyvoice.utils.PhoneStateChangedCallback;
import com.lihb.babyvoice.utils.SingleOkHttpClient;

import java.io.InputStream;

public class BabyVoiceApp extends Application {

    public static BabyVoiceApp instance = null;

    private BabyVoiceApp() {

    }

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
        NotificationCenter.INSTANCE.addCallbacks(PhoneStateChangedCallback.class);
    }

}
