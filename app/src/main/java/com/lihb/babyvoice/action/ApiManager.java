package com.lihb.babyvoice.action;

import com.lihb.babyvoice.model.Article;
import com.lihb.babyvoice.model.BabyVoice;
import com.lihb.babyvoice.model.Contributor;
import com.lihb.babyvoice.model.GrowUpRecord;
import com.lihb.babyvoice.model.HttpResList;
import com.lihb.babyvoice.model.HttpResponse;
import com.lihb.babyvoice.model.ITingBeiResponse;
import com.lihb.babyvoice.model.ProductionInspection;
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
     * 上传声音文件到服务器
     *
     * @param files
     * @return
     */
    @POST("web/voice/doUploadSound.do")
    Observable<HttpResponse<String>> uploadVoiceFiles(
            @Query("username") String userName,
            @Body MultipartBody files);

    /**
     * 获取录音的文件
     *
     * @param start 起始值
     * @param count 获取数量
     * @return
     */
    @GET("getVoiceRecords.do")
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

    /**
     * 获取start到end周的产检信息
     *
     * @param start 起始周
     * @param end   结束周
     * @return
     */
    @GET("getProductionInfo")
    Observable<HttpResponse<HttpResList<ProductionInspection>>> getProductionInfo(
            @Query("start") int start,
            @Query("end") int end);

    /*-----------------------------------------------成长记录---------------------------------*/

    /**
     * 获取成长记录数据
     */
    @GET("mobile/growup/itemList.do")
    Observable<HttpResponse<GrowUpRecord>> getGrowupRecord(
            @Query("page") int page,
            @Query("rows") int rows);

    /**
     * 上传成长记录数据
     */
    @GET("mobile/growup/addInfo.do")
    Observable<HttpResponse<GrowUpRecord>> createGrowupRecord(
            @Query("createdate") String createDate,
            @Query("content") String content,
            @Query("username") String userName,
            @Query("pic1") String pic1,
            @Query("pic2") String pic2);


    /**
     * 删除成长记录数据
     */
    @GET("mobile/growup/deleteInfo.do")
    Observable<HttpResponse<GrowUpRecord>> delGrowupRecord(
            @Query("id") int id);

/*-----------------------------------------------注册登录---------------------------------------*/
    /**
     * 获取手机验证码
     * @param mobileNumber
     * @return
     */
    @GET("web/getMobileCode.do")
    Observable<HttpResponse<Void>> getSmsCode(
            @Query("mobilenumber") String mobileNumber);



    /**
     * 手机验证码登录
     * @param mobileNumber
     * @param smsCode
     * @return
     */
    @GET("web/applogin.do")
    Observable<HttpResponse<Void>> loginBySmsCode(
            @Query("mobilenumber") String mobileNumber,
            @Query("smscode")String smsCode);


    /**
     * 用户密码登录
     * @param userName
     * @param passWord
     * @return
     */
    @GET("mobile/login.do")
    Observable<HttpResponse<Void>> loginByPassword(
            @Query("username") String userName,
            @Query("password") String passWord);


    /**
     * 用户注册
     * @param userName
     * @param passWord
     * @param realName
     * @return
     */
    @GET("mobile/user/registerUser.do")
    Observable<HttpResponse<Void>> register(
            @Query("username") String userName,
            @Query("password") String passWord,
            @Query("realname") String realName);
    /*-----------------------------------------------孕婴圈---------------------------------------*/

    /**
     * 孕婴圈文章发布
     * @param title 文章标题
     * @param content 文章内容
     * @param realName 名字，一般为手机号
     * @param type 文章类别
     * @param attachment 附件
     * @return
     */
    @GET("mobile/article/addInfo.do")
    Observable<HttpResponse<Void>> addPregnantArticle(
            @Query("title") String title,
            @Query("content") String content,
            @Query("realname") String realName,
            @Query("type") int type,
            @Query("attachment") String attachment);

    /**
     * 获取孕婴圈文章
     * @param page 页码
     * @param rows 每页提取的记录数，默认值10
     * @param type 文章类别，默认值10000，表示是用户发布的文章，90000表示是系统文章，由管理员发布的文章
     * @return
     */
    @GET("mobile/article/itemList.do")
    Observable<ITingBeiResponse<Article>> getPregnantArticleList(
            @Query("page") int page,
            @Query("rows") int rows,
            @Query("type") int type);


    /**
     * 孕婴圈文章删除
     * @param id 文章id
     * @return
     */
    @GET("mobile/article/deleteInfo.do")
    Observable<HttpResponse<Void>> delPregnantArticle(
            @Query("id") int id);

    /**
     * 孕婴圈文章查看详情
     * @param id 文章id
     * @return
     */
    @GET("mobile/article/detailInfo.do")
    Observable<HttpResponse<Void>> getPregnantArticleById(
            @Query("id") int id,
            @Query("rows") int rows);
    /*-----------------------------------------------孕婴圈---------------------------------------*/

    /**
     * 上传一张图片到服务器
     *
     * @param files
     * @return
     */
    @POST("web/picture/doUploadPiture.do")
    Observable<HttpResponse<Void>> uploadPicFiles(
            @Query("username") String userName,
            @Body MultipartBody files);

    /**
     * 批量上传图片到服务器
     *
     * @param files
     * @return
     */
    @POST("web/picture/doUploadPitureBat.do")
    Observable<HttpResponse<Void>> uploadBatchPicFiles(
            @Query("username") String userName,
            @Query("longitude") long longitude,
            @Query("latitude")  long latitude,
            @Body MultipartBody files);


}


