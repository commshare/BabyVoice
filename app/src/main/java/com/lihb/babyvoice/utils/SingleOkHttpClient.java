package com.lihb.babyvoice.utils;


import com.lihb.babyvoice.BabyVoiceApp;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;


/**
 * Created by Administrator on 2015/12/31.
 * OkHttpClient单例类。
 */
public class SingleOkHttpClient {

    private static final long HTTP_RESPONSE_DISK_CACHE_MAX_SIZE = 10 * 1024 * 1024;
    private static OkHttpClient instance = null;

    private SingleOkHttpClient() {

    }

    public static OkHttpClient getInstance() {
        if (instance == null) {
            synchronized (SingleOkHttpClient.class) {
                if (instance == null) {
                    final HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                    logging.setLevel(HttpLoggingInterceptor.Level.BODY);

                    OkHttpClient.Builder builder = new OkHttpClient.Builder();
                    builder.addInterceptor(logging)
                            .retryOnConnectionFailure(true)
                            .connectTimeout(15, TimeUnit.SECONDS);

                    final File baseDir = BabyVoiceApp.getInstance().getCacheDir();
                    if (null != baseDir) {
                        final File cacheDir = new File(baseDir, "HttpResponseCache");
                        builder.cache(new Cache(cacheDir, HTTP_RESPONSE_DISK_CACHE_MAX_SIZE));
                    }
                    instance = builder.build();
                }
            }
        }
        return instance;
    }
}
