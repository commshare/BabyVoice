package com.lihb.babyvoice.action;

import com.lihb.babyvoice.model.BaseResponse;
import com.lihb.babyvoice.model.Contributor;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
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
    Observable<BaseResponse<String>> uploadFiles(@Body MultipartBody files);
}
