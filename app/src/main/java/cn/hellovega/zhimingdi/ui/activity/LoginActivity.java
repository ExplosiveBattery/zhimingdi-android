package cn.hellovega.zhimingdi.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WbConnectErrorMessage;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.hellovega.zhimingdi.R;
import cn.hellovega.zhimingdi.model.network.NetworkClient;
import cn.hellovega.zhimingdi.model.network.NetworkResult;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;

//正在登录账号的动画
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "LoginActivity";
    public static final String TAG_EXIT="EXIT";
    private final int  REQUEST_QQ =1, REQUEST_WEIBO=2;
    public static SharedPreferences xml;
    public static List<String> starPicDate =new ArrayList<>();

    //tencent sdk
    public static Tencent mTencent=null;
    private BaseUiListener loginListener=new BaseUiListener("login");
    //weibo sdk
    private SsoHandler mSsoHandler=null;
    //weixin sdk
    public static IWXAPI wxApi;

//network

    private Handler requestHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            getStarInfo();

            JSONObject jo;
            String nickName="",qqID="",weiboID="",avatarUrl="";
            final SharedPreferences.Editor editor=xml.edit();
            switch (msg.what) {
                case REQUEST_QQ:
                    jo =(JSONObject) msg.obj;
                    Log.e(TAG, "handleMessage: "+jo.toString() );
                    try{
                        nickName =jo.getString("nickname");
                        qqID =mTencent.getOpenId();
                        avatarUrl =jo.getString("figureurl_2");
                    }catch (Exception e){
                        //;
                    }
                    break;
                case REQUEST_WEIBO:
                    jo =(JSONObject) msg.obj;
                    try{
                        nickName =jo.getString("name");
                        weiboID =jo.getString("id");
                        avatarUrl =jo.getString("avatar_large");
                    }catch (Exception e){
                        //;
                    }
                    break;
                default:
                    ;
            }
            NetworkClient.service.login("",qqID,weiboID).enqueue(new retrofit2.Callback<NetworkResult.Login>() {
                @Override
                public void onResponse(Call<NetworkResult.Login> call, retrofit2.Response<NetworkResult.Login> response) {
                    Log.e(TAG, "onResponse: "+response.body().getAccessToken() );
                    //将accessToken保存
                    editor.putString("access_token", response.body().getAccessToken());
                    editor.apply();
                }

                @Override
                public void onFailure(Call<NetworkResult.Login> call, Throwable t) {
                    Log.e(TAG, "onFailure: " );
                }
            });
            editor.putString("nick_name",nickName);
            editor.putString("avatar_url",avatarUrl);
            editor.apply();

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("nick_name", nickName);
            intent.putExtra("avatar_url",avatarUrl);
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViewById(R.id.login_qq).setOnClickListener(this);
        findViewById(R.id.login_wechat).setOnClickListener(this);
        findViewById(R.id.login_weibo).setOnClickListener(this);


        //自动登录, 其实这一步应该放到开机动画中
        xml = getSharedPreferences("login", MODE_PRIVATE);
        String accessToken =xml.getString("access_token", null);
        if( accessToken!=null ) {
            getStarInfo();
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("nick_name", xml.getString("nick_name", null));
            intent.putExtra("avatar_url",xml.getString("avatar_url", null));
            startActivity(intent);
        }

        //初始化Tencent SDK
        mTencent = Tencent.createInstance(getString(R.string.qq_sdk_id), this.getApplicationContext());

        //初始化微信 SDK
        //微信的SDk 因为不支持登录所以放到了分享的MainActivity
        //为了微信分享 SDK初始化
        wxApi = WXAPIFactory.createWXAPI(this, getString(R.string.wechat_sdk_id), true);
        wxApi.registerApp(getString(R.string.wechat_sdk_id));

        //初始化 微博 SDK
        WbSdk.install(this,new AuthInfo(this, getString(R.string.weibo_sdk_id), getString(R.string.weibo_redirect_url), "direct_messages_write"));
    }



    //到时需要移动到后面去
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.login_qq:
                if(!mTencent.isSessionValid())
                    mTencent.login(this, "all", loginListener);
                break;
            case R.id.login_wechat:
                Toast.makeText(LoginActivity.this, "对不起，微信不允许个人开发者申请登录权限", Toast.LENGTH_SHORT).show();
                break;
            case R.id.login_weibo:
                if(mSsoHandler==null)   mSsoHandler = new SsoHandler(this);
                mSsoHandler.authorize(new SelfWbAuthListener());
                break;
            default:
                break;
        }

    }




    //在某些低端机上调用登录后，由于内存紧张导致APP被系统回收，登录成功后无法成功回传数据。
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_LOGIN) {
            mTencent.onActivityResultData(requestCode,resultCode,data,loginListener);
        }else if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //其他Activity 退出程序
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            boolean isExit = intent.getBooleanExtra(TAG_EXIT, false);
            if (isExit) {
                this.finish();
            }
        }
    }



    @Override
    protected void onDestroy() {
        if (mTencent != null) {
            mTencent.logout(LoginActivity.this);
        }
        super.onDestroy();
    }

    private void getStarInfo() {
        NetworkClient.service.getStar(LoginActivity.xml.getString("access_token","")).enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, retrofit2.Response<List<String>> response) {
                starPicDate =response.body();
            }
            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
            }
        });
    }




    public class BaseUiListener implements IUiListener {
        private String mScope;

        public BaseUiListener(String scope) {
            super();
            mScope =scope;
        }

        @Override
        public void onComplete(Object response) {
            if (this.mScope == "login") {
                try{
                    JSONObject jo = (JSONObject) response;
                    if (null == jo || null != jo && jo.length() == 0) {
                        //jo为null 或者 数据为空的时候 登录失败
                        Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                        return;
                    }else{
                        String openID = jo.getString("openid");
                        String accessToken = jo.getString("access_token");
                        String expires = jo.getString("expires_in");
                        mTencent.setOpenId(openID);
                        mTencent.setAccessToken(accessToken, expires);

                        UserInfo userinfo = new UserInfo(LoginActivity.this, mTencent.getQQToken());
                        userinfo.getUserInfo(new BaseUiListener("get_user_info"));
                    }
                }catch (Exception e) {
                    // TODO: handle exception
                }
            }else if (this.mScope == "get_user_info") {
                    JSONObject jo = (JSONObject) response;
                    if (null == jo || null != jo && jo.length() == 0) {
                        Toast.makeText(LoginActivity.this, "获取用户信息失败", Toast.LENGTH_SHORT).show();
                        return;
                    }else {
                        Message message = requestHandler.obtainMessage();
                        message.obj = jo;
                        message.what = REQUEST_QQ;
                        message.sendToTarget();
                    }

            }
        }


        @Override
        public void onError(UiError e) {

        }

        @Override
        public void onCancel() {

        }
    }



    private class SelfWbAuthListener implements com.sina.weibo.sdk.auth.WbAuthListener{
        @Override
        public void onSuccess(final Oauth2AccessToken token) {
            LoginActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (token.isSessionValid()) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    OkHttpClient client =new OkHttpClient();
                                    Request request =new Request.Builder().url("https://api.weibo.com/2/users/show.json?access_token="+token.getToken()+"&uid="+token.getUid()).build();
                                    Response response =client.newCall(request).execute();
                                    JSONObject jo = new JSONObject(response.body().string());
                                    Message message = requestHandler.obtainMessage();
                                    message.obj = jo;
                                    message.what = REQUEST_WEIBO;
                                    message.sendToTarget();
                                }catch (Exception e){
                                    //handle
                                }
                            }
                        }).start();
                    }
                }
            });
        }

        @Override
        public void cancel() {
            Toast.makeText(LoginActivity.this, "认证取消", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onFailure(WbConnectErrorMessage errorMessage) {
            Toast.makeText(LoginActivity.this, errorMessage.getErrorMessage(), Toast.LENGTH_LONG).show();
        }
    }
}



