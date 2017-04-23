package com.lihb.babyvoice.utils;


import android.util.Log;

import com.lihb.babyvoice.BabyVoiceApp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;


/**
 * Created by Administrator on 2015/12/31.
 * OkHttpClient单例类。
 */
public class SingleOkHttpClient {

    public static final String TAG = "SingleOkHttpClient";
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

                    final CookieJar cookieJar = new CookieJar() {

                        @Override
                        public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                            Log.i(TAG, "saveFromResponse-->url : "+ url+", cookies : " + cookies);
                            for (Cookie cookie : cookies) {
                                if (cookie.name().equals("JSESSIONID")) {
                                    String jsessionid = cookie.value();
                                    SharedPreferencesUtil.setCookie(BabyVoiceApp.getInstance(), jsessionid);
                                    break;
                                }
                            }
                        }

                        @Override
                        public List<Cookie> loadForRequest(HttpUrl url) {
                            List<Cookie> cookies = new ArrayList<>();
                            Cookie.Builder builder = new Cookie.Builder();
                            builder.name("JSESSIONID");
                            builder.value(SharedPreferencesUtil.getCookie(BabyVoiceApp.getInstance()));
                            builder.domain(url.host());
                            builder.httpOnly();
                            Cookie cookie = builder.build();
                            cookies.add(cookie);
                            Log.i(TAG, "loadForRequest-->url : "+ url+", cookies : " + cookies);
                            return cookies;
                        }
                    };

                    OkHttpClient.Builder builder = new OkHttpClient.Builder();
                    builder.addInterceptor(logging)
                            .retryOnConnectionFailure(true)
                            .cookieJar(cookieJar)
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
