package cn.hellovega.zhimingdi.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by vega on 3/10/18.
 */

//ShareReference
public class User {
    @SerializedName("avatar_url")
    String avatarUrl;

    @SerializedName("nick_name")
    String nickName;

    @SerializedName("access_token")
    String accessToken;



}
