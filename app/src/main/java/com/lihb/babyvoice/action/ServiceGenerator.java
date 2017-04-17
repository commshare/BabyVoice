package com.lihb.babyvoice.action;

import com.lihb.babyvoice.utils.SingleOkHttpClient;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by lhb on 2017/1/17.
 */

public class ServiceGenerator {

//    public static final String API_BASE_URL = "http://172.25.64.19:5000/";
    public static final String API_BASE_URL = "http://123.207.46.152:8080/itingbaby/";

    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(API_BASE_URL)
                    .client(SingleOkHttpClient.getInstance())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create());

    public static <T> T createService(Class<T> serviceClass) {

        Retrofit retrofit = builder.build();
        return retrofit.create(serviceClass);
    }
}
