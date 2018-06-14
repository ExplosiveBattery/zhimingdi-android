package cn.hellovega.zhimingdi.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.allenliu.versionchecklib.v2.AllenVersionChecker;
import com.allenliu.versionchecklib.v2.builder.UIData;
import com.allenliu.versionchecklib.v2.callback.RequestVersionListener;
import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.share.WbShareHandler;
import com.tencent.connect.share.QzonePublish;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;

import org.joda.time.DateTime;

import java.io.FileInputStream;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.hellovega.zhimingdi.GlideApp;
import cn.hellovega.zhimingdi.R;
import cn.hellovega.zhimingdi.model.DeleteFileUtil;
import cn.hellovega.zhimingdi.model.network.NetworkDefine;
import cn.hellovega.zhimingdi.ui.fragment.FunctionFragment;
import cn.hellovega.zhimingdi.ui.fragment.ShadowFragment;
import cn.hellovega.zhimingdi.ui.widget.NavigationItem;
import de.hdodenhof.circleimageview.CircleImageView;
import me.drakeet.materialdialog.MaterialDialog;


public class MainActivity extends AppCompatActivity implements  View.OnClickListener {
    private static final String TAG = "MainActivity";
    private boolean needExit =false;
    private DateTime dt =new DateTime();

    /* Icon Animation */
    public final static int ICON_OFFSET = 130;
    private final int ANIMATION_TIME=400;
    private boolean mShareButtonState=false;
    private TranslateAnimation mQQAnim = new TranslateAnimation(0, 0, 0, ICON_OFFSET);
    private TranslateAnimation mWechatAnim = new TranslateAnimation(0, 0, 0, 2*ICON_OFFSET);
    private TranslateAnimation mWeibioAnim = new TranslateAnimation(0, 0, 0, 3*ICON_OFFSET);
    /* ShadowFragment */
    MyFragmentPagerAdapter myFragmentPagerAdapter;
    private static final int YEAR_MAX = 2100;
    private static final int YEAR_MIN = 1900;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final int i =msg.what;
            final String str =(String)msg.obj;

            if(i!=R.id.weiboShareButton) share(i, "", str);
            else {
                //文字信息
                View view = getLayoutInflater().inflate(R.layout.widget_share_note, null);
                final EditText editText = view.findViewById(R.id.text);
                final MaterialDialog materialDialog = new MaterialDialog(MainActivity.this)
                        .setBackground(getDrawable(R.drawable.note))
                        .setTitle("心情便签")
                        .setContentView(view);
                materialDialog.setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        share(i, editText.getText().toString(), str);
                        materialDialog.dismiss();
                    }
                }).setNegativeButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        materialDialog.dismiss();
                    }
                });
                materialDialog.show();
            }
        }
    };

    @BindView(R.id.drawerLayoutButton)
    ImageButton drawerLayoutButton;
    @BindView(R.id.drawerLayout)
    DrawerLayout drawerLayout;
    @BindView(R.id.back_to_today)
    TextView tvBackToToday;
    @BindView(R.id.shareButton)
    ImageButton shareButton;
    @BindView(R.id.qqShareButton)
    ImageButton qqShareButton;
    @BindView(R.id.wechatShareButton)
    ImageButton wechatShareButton;
    @BindView(R.id.weiboShareButton)
    ImageButton weiboShareButton;
    @BindView(R.id.viewpaper)
    ViewPager viewPager;
    //drawerLayout
    @BindView(R.id.drawer_nick_name)
    TextView tvNickName;
    @BindView(R.id.drawer_img_avatar)
    CircleImageView ciAvatar;
    @BindView(R.id.drawer_logout)
    TextView tvLogout;
    @BindView(R.id.drawer_star_pic)
    NavigationItem niStarPic;
    @BindView(R.id.drawer_pic_history)
    NavigationItem niPicHistory;
    @BindView(R.id.drawer_clear_cache)
    NavigationItem niClearCache;
    @BindView(R.id.drawer_donate)
    NavigationItem niDonate;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //版本检查
        AllenVersionChecker
                .getInstance()
                .requestVersion()
                .setRequestUrl(NetworkDefine.VERSION_URL)
                .request(new RequestVersionListener() {
                    @Nullable
                    @Override
                    public UIData onRequestVersionSuccess(String result) {
                        JsonObject obj = new JsonParser().parse(result).getAsJsonObject();
                        if(obj.get("version").getAsFloat()>0.0f)
                            return  UIData.create()
                                    .setDownloadUrl(obj.get("downloadUrl").getAsString())
                                    .setTitle("版本更新")
                                    .setContent("发现新版本 v"+obj.get("version").getAsString()+" , 建议在WiFi下更新");
                        else return null;
                    }
                    @Override
                    public void onRequestVersionFailure(String message) {
                    }
                })
                .excuteMission(this);



        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        tvBackToToday.setOnClickListener(this);
        //drawerLayout
        Intent intent = getIntent();
        tvNickName.setText(intent.getStringExtra("nick_name"));
        GlideApp.with(this).load(intent.getStringExtra("avatar_url")).into(ciAvatar);
        drawerLayoutButton.setOnClickListener(this);
        tvLogout.setOnClickListener(this);
        niPicHistory.setOnClickListener(this);
        niStarPic.setOnClickListener(this);
        niClearCache.setOnClickListener(this);
        niDonate.setOnClickListener(this);
        //set share
        setShareAnimation();

        //viewpaper
        myFragmentPagerAdapter=new MyFragmentPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(myFragmentPagerAdapter);
        viewPager.setCurrentItem(myFragmentPagerAdapter.getStartPageIndex());
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                setToNormalState();
                if( 1+position==myFragmentPagerAdapter.getCount() ) {
                    tvBackToToday.setVisibility(View.GONE);
                    shareButton.setVisibility(View.GONE);
                    drawerLayoutButton.setVisibility(View.GONE);
                    ((FunctionFragment)(myFragmentPagerAdapter.instantiateItem(viewPager ,position))).showDrawerLayoutButton(true);
                }
                else if( 2+position==myFragmentPagerAdapter.getCount() ){
                    tvBackToToday.setVisibility(View.GONE);
                    shareButton.setVisibility(View.VISIBLE);
                    drawerLayoutButton.setVisibility(View.VISIBLE);
                    ((FunctionFragment)(myFragmentPagerAdapter.instantiateItem(viewPager ,position+1))).showDrawerLayoutButton(false);
                }else {
                    tvBackToToday.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });



    }




    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.drawerLayoutButton:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.drawer_img_avatar:
            case R.id.drawer_nick_name:
                //查看用户信息
                break;
            case R.id.back_to_today:
                viewPager.setCurrentItem( myFragmentPagerAdapter.getCount()-2);
                break;
            case R.id.drawer_star_pic: {
                Intent intent = new Intent(this, MultiImageActivity.class);
                intent.putExtra("action", MultiImageActivity.TAG_STAR);
                startActivity(intent);
                break;
            }
            case R.id.drawer_pic_history: {
                Intent intent = new Intent(this, MultiImageActivity.class);
                intent.putExtra("action", MultiImageActivity.TAG_HISTORY);
                startActivity(intent);
                break;
            }
            case R.id.drawer_logout: {
                //MaterialDialog button响应函数如果设置为null，则点击按钮窗口不会消除
                MaterialDialog materialDialog = new MaterialDialog(this)
                        .setMessage("确定退出(需要重新登录)?")
                        .setPositiveButton("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (LoginActivity.mTencent != null)
                                    LoginActivity.mTencent.logout(MainActivity.this);
                                SharedPreferences.Editor editor = LoginActivity.xml.edit();
                                editor.clear();
                                editor.commit();
                                MainActivity.this.finish();
                            }
                        })
                        .setNegativeButton("取消", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        });
                materialDialog.show();
                break;
            }case R.id.drawer_clear_cache:{
                Toast.makeText(this, "正在清除...", Toast.LENGTH_SHORT).show();
                Glide.get(this).clearMemory();
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        Glide.get(MainActivity.this).clearDiskCache();
                        DeleteFileUtil.deleteFilesInDirectory(getExternalFilesDir("").getAbsolutePath()+"/photos");
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        Toast.makeText(MainActivity.this, "清除完成", Toast.LENGTH_SHORT).show();
                    }
                }.execute();
                break;
            }case R.id.drawer_donate:{
                Toast.makeText(this, "其实, 我开玩笑的", Toast.LENGTH_LONG).show();
                break;
            }default:
                break;
        }
    }


    @OnClick({R.id.qqShareButton, R.id.weiboShareButton, R.id.wechatShareButton})
    public void onXXShareButtonClick(final View v) {
        //获取图片本地路径后,触发心情便签
        final DateTime dt1 =dt.plusDays(viewPager.getCurrentItem() - myFragmentPagerAdapter.getCount() + 2);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String path =GlideApp.with(MainActivity.this).downloadOnly().load(NetworkDefine.PIC_QUERY_URl + dt1.toString("yyyyMMdd")).submit().get().getAbsolutePath();
                    Log.e(TAG, "run: "+path );
                    Message message =handler.obtainMessage();
                    message.what =v.getId();
                    message.obj =path;
                    message.sendToTarget();
                }catch(Exception ex){

                }
            }
        }).start();


    }
    private void share(int id, String text, String imagePath) {
        Log.e(TAG, "share: "+id+","+text+","+imagePath );
        switch(id) {
            case R.id.qqShareButton:
                Bundle bundle =new Bundle();
                bundle.putInt(QzonePublish.PUBLISH_TO_QZONE_KEY_TYPE, QzonePublish.PUBLISH_TO_QZONE_TYPE_PUBLISHMOOD);
//                bundle.putString(QzonePublish.PUBLISH_TO_QZONE_SUMMARY, text);
                ArrayList<String> arrayList=new ArrayList<String>();arrayList.add(imagePath);
                bundle.putStringArrayList(QzonePublish.PUBLISH_TO_QZONE_IMAGE_URL, arrayList);
                LoginActivity.mTencent.publishToQzone(this, bundle, null);
                break;
            case R.id.wechatShareButton:
                WXImageObject imgObj = new WXImageObject();
                imgObj.setImagePath(imagePath);
                WXMediaMessage msg = new WXMediaMessage();
                msg.mediaObject = imgObj;
                SendMessageToWX.Req req = new SendMessageToWX.Req();
                req.transaction = String.valueOf(System.currentTimeMillis());
                req.message = msg;
                req.scene = SendMessageToWX.Req.WXSceneTimeline;
                LoginActivity.wxApi.sendReq(req); //发送

                //这种方式限制了文件后缀名
//                Intent intent = new Intent();
//                intent.setComponent(new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareToTimeLineUI"));
//                intent.setAction(Intent.ACTION_SEND);
//                intent.setType("image/*");
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                intent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID+".fileprovider", new File(imagePath)));
//                intent.putExtra("Kdescription", text);
//                startActivity(intent);
                break;
            case R.id.weiboShareButton:
                WbShareHandler shareHandler = new WbShareHandler(this);
                shareHandler.registerApp();
                shareHandler.setProgressColor(0xff33b5e5);
                WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
                TextObject textObject =new TextObject();
                textObject.text =text;textObject.title ="植名地";
                weiboMessage.textObject=textObject;
                try {
                    ImageObject imageObject = new ImageObject();
                    FileInputStream fis = new FileInputStream(imagePath);
                    Bitmap bitmap = BitmapFactory.decodeStream(fis);
                    imageObject.setImageObject(bitmap);
                    weiboMessage.imageObject = imageObject;
                }catch (Exception e) {
                    //图片过大会内存泄露
                }
                shareHandler.shareMessage(weiboMessage, false);
                break;
            default:
                ;
        }
    }


    @Override
    public void onBackPressed() {
        if(!needExit) {
            needExit=true;
            Toast.makeText(this,"再按一次退出程序", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    needExit = false;
                }
            }, 2000);
        } else {
            Intent intent =new Intent(this, LoginActivity.class);
            intent.putExtra(LoginActivity.TAG_EXIT, true);
            startActivity(intent);
        }

    }



    public void  setShareAnimation() {
        ButterKnife.bind(this);
        //qq 分享按钮动画设置
        mQQAnim.setDuration(ANIMATION_TIME);
        mQQAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                qqShareButton.clearAnimation();
                qqShareButton.offsetTopAndBottom(ICON_OFFSET);
            }


        });

        //微信 分享按钮动画设置
        mWechatAnim.setDuration(ANIMATION_TIME);
        mWechatAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                wechatShareButton.clearAnimation();
                wechatShareButton.offsetTopAndBottom(2*ICON_OFFSET);
            }
        });

        //微博 分享按钮动画设置
        mWeibioAnim.setDuration(ANIMATION_TIME);
        mWeibioAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                weiboShareButton.clearAnimation();
                weiboShareButton.offsetTopAndBottom(3*ICON_OFFSET);
            }
        });

        //layout 方法可能是一种镜像移动
        //share button
        shareButton.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( !mShareButtonState ) {
                    qqShareButton.setVisibility(View.VISIBLE);
                    wechatShareButton.setVisibility(View.VISIBLE);
                    weiboShareButton.setVisibility(View.VISIBLE);
                    qqShareButton.startAnimation(mQQAnim);
                    wechatShareButton.startAnimation(mWechatAnim);
                    weiboShareButton.startAnimation(mWeibioAnim);
                }else {
                    setToNormalState();
                }
                mShareButtonState =!mShareButtonState;
            }
        });
    }


    private void setToNormalState() {
        qqShareButton.setVisibility(View.INVISIBLE);
        wechatShareButton.setVisibility(View.INVISIBLE);
        weiboShareButton.setVisibility(View.INVISIBLE);
        qqShareButton.layout(shareButton.getLeft(),shareButton.getTop(),shareButton.getRight(),shareButton.getBottom());
        weiboShareButton.layout(shareButton.getLeft(),shareButton.getTop(),shareButton.getRight(),shareButton.getBottom());
        wechatShareButton.layout(shareButton.getLeft(),shareButton.getTop(),shareButton.getRight(),shareButton.getBottom());
        shareButton.setBackground(getDrawable(R.drawable.ic_share_white_24dp));
    }

    public void showDrawer() {
        if(drawerLayout!=null)
            drawerLayout.openDrawer(GravityCompat.START);
    }


    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return (YEAR_MAX-YEAR_MIN)*365;
        }

        public int getStartPageIndex() {
            return getCount()-2;
        }


        @Override
        public Fragment getItem(int position) {
            if(position!=getCount()-1) {
                ShadowFragment fragment = new ShadowFragment();
                Bundle bundle = new Bundle();
                DateTime dt1 =dt.plusDays(position - getStartPageIndex());
                bundle.putInt("year", dt1.getYear());
                bundle.putInt("month", dt1.getMonthOfYear());
                bundle.putInt("day", dt1.getDayOfMonth());
                fragment.setArguments(bundle);

                return fragment;
            } else {
                FunctionFragment fragment = new FunctionFragment();
                return fragment;
            }


        }
    }


}
