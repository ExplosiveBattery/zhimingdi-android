package cn.hellovega.zhimingdi.ui.activity;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import org.joda.time.DateTime;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import cn.hellovega.zhimingdi.GlideApp;
import cn.hellovega.zhimingdi.R;
import cn.hellovega.zhimingdi.model.network.NetworkDefine;


/**
 * Created by vega on 3/12/18.
 */

public class StartActivity extends AppCompatActivity {
    private static final String TAG = "StartActivity";
    private Handler handler = new Handler();


    //从今天开始倒数 cx. 三日的图片下载,  耗时的似乎只有网络操作
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);





        DateTime dt =new DateTime();
        new AsyncTask<Void,Void,Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                copyfile(getExternalFilesDir("").getAbsolutePath(), "mapstyle.data", R.raw.mapstyle);
                return null;
            }
        }.execute();
        GlideApp.with(this).downloadOnly().load(NetworkDefine.PIC_QUERY_URl+dt.toString("yyyyMMdd")).submit();
        GlideApp.with(this).downloadOnly().load(NetworkDefine.PIC_QUERY_URl+dt.minusDays(1).toString("yyyyMMdd")).submit();
        GlideApp.with(this).downloadOnly().load(NetworkDefine.PIC_QUERY_URl+dt.minusDays(2).toString("yyyyMMdd")).submit();
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA}, 1);
        else
            goNext();

        initNotification();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if(grantResults.length>0
                        && grantResults[0]==PackageManager.PERMISSION_GRANTED
                        && grantResults[1]==PackageManager.PERMISSION_GRANTED
                        && grantResults[2]==PackageManager.PERMISSION_GRANTED) goNext();
                else finish();
                break;
            default:
                ;
        }
    }

    private void initNotification() {
        Log.e(TAG, "initNotification: "+android.os.Build.MODEL );
        DateTime dt =new DateTime();
        String date =dt.toString("yyyyMMdd");
        String dateTomorrow =dt.plusDays(1).toString("yyyyMMdd");
        for(String key:getSharedPreferences("mention", MODE_PRIVATE).getAll().keySet()) {
            String str=key.substring(0,8);
            if (str.equals(date) || str.equals(dateTomorrow)) {
                NotificationManager manager =( NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                PendingIntent pi =PendingIntent.getActivity(this, 1,new Intent(), PendingIntent.FLAG_UPDATE_CURRENT);
                PendingIntent pi2 =PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class),0);
                Notification notification = new Notification.Builder(this)
                        .setSmallIcon(R.drawable.notification_icon)
                        .setContentTitle("提醒")
                        .setContentText("今明两天, 您有事情安排.")
                        .setTicker("悬浮通知")
                        .setAutoCancel(true)
                        .setDefaults(~0)
                        .setPriority(Notification.PRIORITY_MAX)
//                        .setFullScreenIntent(pi, false)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setContentIntent(pi)
                        .build();
                manager.notify(0, notification);
            }
        }
    }




    private void goNext() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent =new Intent(StartActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(0,0);
            }
        }, 1000);
    }

    //屏蔽物理返回键
    @Override public boolean onKeyDown(int keyCode, KeyEvent event) { if (keyCode == KeyEvent.KEYCODE_BACK) { return true; } return super.onKeyDown(keyCode, event); }

    private void copyfile(String fileDirPath,String fileName,int id) {
        String filePath = fileDirPath + "/" + fileName;// 文件路径
        try {
            File dir = new File(fileDirPath);// 目录路径
            if (!dir.exists()) {// 如果不存在，则创建路径名
                dir.mkdirs();
            }
            // 目录存在，则将apk中raw文件夹中的需要的文档复制到该目录下
            File file = new File(filePath);
            if (!file.exists()) {// 文件不存在
                InputStream is = getResources().openRawResource(
                        id);// 通过raw得到数据资源
                FileOutputStream fs = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int count = 0;// 循环写出
                while ((count = is.read(buffer)) > 0) {
                    fs.write(buffer, 0, count);
                }
                fs.close();// 关闭流
                is.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
