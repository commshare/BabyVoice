package com.lihb.babyvoice.action;

import com.lihb.babyvoice.model.BabyVoice;
import com.lihb.babyvoice.model.Contributor;
import com.lihb.babyvoice.model.HttpResList;
import com.lihb.babyvoice.model.HttpResponse;
import com.lihb.babyvoice.model.VaccineInfo;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by lhb on 2017/1/17.
 */

public interface ApiManager {

    @GET("repos/{owner}/{repo}/contributors")
    Observable<List<Contributor>> contributors(
            @Path("owner") String owner,
            @Path("repo") String repo);

    @GET("{user}/followers")
    Observable<List<Contributor>> followers(
            @Path("user") String user);

    /**
     * 上传文件到服务器
     *
     * @param files
     * @return
     */
    @POST("uploadfiles")
    Observable<HttpResponse<String>> uploadFiles(@Body MultipartBody files);

    /**
     * 获取录音的文件
     *
     * @param start 起始值
     * @param count 获取数量
     * @return
     */
    @GET("getVoiceRecords")
    Observable<HttpResponse<HttpResList<BabyVoice>>> getBabyVoiceRecord(
            @Query("start") int start,
            @Query("count") int count);

    /**
     * 获取疫苗信息
     *
     * @param start 起始值
     * @param count 获取数量
     * @return
     */
    @GET("getVaccineInfo")
    Observable<HttpResponse<HttpResList<VaccineInfo>>> getVaccineInfo(
            @Query("start") int start,
            @Query("count") int count);
}


