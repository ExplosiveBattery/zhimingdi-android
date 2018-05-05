package cn.hellovega.zhimingdi.model.network;

import com.google.gson.annotations.SerializedName;

/**
 * Created by vega on 3/10/18.
 */

public class NetworkResult {
    String state;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }


    public class Login extends NetworkResult {
        @SerializedName("access_token")
        String accessToken;

        public String getAccessToken() {
            return accessToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }
    }


}
