package com.lihb.babyvoice.utils;


import com.facebook.stetho.okhttp.StethoInterceptor;
import com.squareup.okhttp.OkHttpClient;

/**
 * Created by Administrator on 2015/12/31.
 * OkHttpClient单例类。
 */
public class SingleOkHttpClient extends OkHttpClient {

    private static final long HTTP_RESPONSE_DISK_CACHE_MAX_SIZE = 10 * 1024 * 1024;
    private static SingleOkHttpClient instance = null;

    private SingleOkHttpClient() {

    }

    public static SingleOkHttpClient getInstance() {
        if (instance == null) {
            synchronized (SingleOkHttpClient.class) {
                if (instance == null) {
                    instance = new SingleOkHttpClient();
                    instance.configureClient();
                }
            }
        }
        return instance;
    }

    public void configureClient() {
        // 设置目录缓存
//        final File baseDir = BabyVoiceApp.instance.getCacheDir();
//        if (baseDir != null) {
//            final File cacheDir = new File(baseDir, "HttpResponseCache");
//            instance.setCache(new Cache(cacheDir, HTTP_RESPONSE_DISK_CACHE_MAX_SIZE));
//            instance.cache();
//        }
        // Integrate with Stetho
        instance.networkInterceptors().add(new StethoInterceptor());
    }

}
