package cn.hellovega.zhimingdi.model.network;

import java.io.File;
import java.util.List;

import butterknife.OnClick;
import cn.hellovega.zhimingdi.model.Marker;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by vega on 3/2/18.
 */

public interface NetworkService {
    //=====
    // 用户
    //=====
    @POST("users")  //不知道需不需要users.php
    @FormUrlEncoded
    Call<NetworkResult.Login> login(
            @Field("accessToken") String accessToken,
            @Field("qqID") String qqID,
            @Field("weiboID") String weiboID
    );



    //=====
    // 图片收藏
    //=====
    @POST("star")
    @FormUrlEncoded
    Call<List<String>> getStar(
            @Field("accessToken") String accessToken
    );//网站返回一系列关注的yyyyMMdd


    @POST("star")
    @FormUrlEncoded
    Call<NetworkResult> setStar(
            @Field("accessToken") String accessToken,
            @Field("starDate") String starDate,
            @Field("type") String type
    );//type=0收藏    type=1取消收藏


    //地图中的Marker
    @Multipart
    @POST("marker")
    Call<ResponseBody> uploadMarker(
            @Part("accessToken") String accesToken,
            @Part("longitude") double longitude,
            @Part("latitude") double latitude,
            @Part("name") String name,
            @Part("image\"; filename=\"image.jpg\"") RequestBody pic
    );

    @FormUrlEncoded
    @POST("marker")
    Call<List<Marker>> getMarker(
            @Field("accessToken") String accessToken,
            @Field("longitude") double longitude,
            @Field("latitude") double latitude
    );


}
