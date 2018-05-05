package cn.hellovega.zhimingdi.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.net.Network;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.PolylineOptions;
import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.hellovega.zhimingdi.GlideApp;
import cn.hellovega.zhimingdi.R;
import cn.hellovega.zhimingdi.model.CompressPic;
import cn.hellovega.zhimingdi.model.DeleteFileUtil;
import cn.hellovega.zhimingdi.model.network.NetworkClient;
import cn.hellovega.zhimingdi.model.network.NetworkDefine;
import me.drakeet.materialdialog.MaterialDialog;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by vega on 3/19/18.
 */

public class MapActivity extends AppCompatActivity implements AMap.OnMyLocationChangeListener  {
    private static final String TAG = "MapActivity";
    MapView mMapView = null;
    private LatLng oldLalng=null,newLalng=new LatLng(0,0),getMarkerLalng=null;
    private AMap aMap;List<Integer> colorList = new ArrayList<Integer>();Random random =new Random();
    private File picture;
    private MaterialDialog materialDialog; private EditText editText;
    private AsyncTask asyncTask;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        ButterKnife.bind(this);
        mMapView = (MapView) findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);//创建地图

        setMap();
        fillColorList();

        try {
            int locationMode = Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);
            if( locationMode==0 ) Toast.makeText(this, "请打开位置信息", Toast.LENGTH_LONG);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        editText =new EditText(this);
        editText.setHint("你觉得它应该叫什么呢?");
        materialDialog=new MaterialDialog(this)
                .setTitle("信息补充")
                .setContentView(editText)
                .setPositiveButton("发送", new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        //发送已有数据
                        if(newLalng.longitude!=0 && newLalng.latitude!=0)
                            NetworkClient.service.uploadMarker(LoginActivity.xml.getString("access_token", ""),newLalng.longitude, newLalng.latitude, editText.getText().toString(), RequestBody.create(MediaType.parse("image/jpeg"), picture)).enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    try {
                                        Log.e(TAG, "getMarker: " + response.body().string());
                                    }catch (Exception e) {

                                    }
                                    Toast.makeText(MapActivity.this, "发送成功,请等待人工审核", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {

                                }
                            });
                        else
                            Toast.makeText(MapActivity.this, "请等定位成功后再点击", Toast.LENGTH_SHORT).show();
                        materialDialog.dismiss();
                        editText.setText("");
                    }
                })
                .setNegativeButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        materialDialog.dismiss();
                        editText.setText("");
                    }
                });
    }


    private void setMap() {
        aMap = mMapView.getMap();
        MyLocationStyle myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        myLocationStyle.showMyLocation(true);
        myLocationStyle.strokeWidth(0);
        aMap.setCustomMapStylePath(getExternalFilesDir("").getAbsolutePath()+"/mapstyle.data");
        aMap.setMapCustomEnable(true);
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        aMap.getUiSettings().setMyLocationButtonEnabled(true);//设置默认定位按钮是否显示，非必需设置。
        aMap.getUiSettings().setZoomControlsEnabled(false); //去除缩放按钮
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        aMap.moveCamera(CameraUpdateFactory.zoomTo(18));
        aMap.setMapTextZIndex(2); //使用 aMap.setMapTextZIndex(2) 可以将地图底图文字设置在添加的覆盖物之上
        aMap.setOnMyLocationChangeListener(this);
        aMap.setMinZoomLevel(16);
    }

    private void fillColorList() {
        colorList.add(Color.RED);
        colorList.add(Color.YELLOW);
        colorList.add(Color.GREEN);
    }

    @Override
    public void onMyLocationChange(Location location) {//不是位置改变被调动，而是按设置的时间间隔必定调用
        if( oldLalng!=null) {
            newLalng = new LatLng(location.getLatitude(), location.getLongitude());
            if (!oldLalng.equals(newLalng) && Math.abs(location.getLatitude() - oldLalng.latitude) < 0.02 && Math.abs(location.getLongitude() - oldLalng.longitude) < 0.02)
                aMap.addPolyline(new PolylineOptions().add(oldLalng, newLalng).width(20).color(colorList.get(random.nextInt(colorList.size()))));
            oldLalng = newLalng;
            if (getMarkerLalng == null) {
                getMarkerLalng = newLalng;
                getMarker();
            } else if (Math.abs(location.getLatitude() - getMarkerLalng.latitude) > 0.1 || Math.abs(location.getLongitude() - getMarkerLalng.longitude) > 0.1) {
                aMap.clear();
                getMarker();
            }
        }else {
            oldLalng = new LatLng(aMap.getMyLocation().getLatitude(), aMap.getMyLocation().getLongitude());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            CompressPic.compressByResolution(picture.getAbsolutePath(),90,90);
            materialDialog.show();
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + picture.getAbsolutePath())));
        }
    }

    @OnClick(R.id.flt_add)
    public void onAddButtonClick() {
        useCamera();
    }

    private void useCamera() {
        picture = new File(getExternalFilesDir("").getAbsolutePath()+"/photos", System.currentTimeMillis() + ".jpg");
        if (!picture.exists()) {    picture.getParentFile().mkdir();  }

        Uri imageUri;
        if(Build.VERSION.SDK_INT >= 24 ) {
            imageUri = FileProvider.getUriForFile(this, "cn.hellovega.zhimingdi.fileprovider", picture);
        }else {
            imageUri = Uri.fromFile(picture);
        }
        System.out.println(imageUri);
        Intent intent =new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent,1);
    }

    private void getMarker() {
        if( newLalng.longitude!=0 && newLalng.latitude!=0 ) {
            NetworkClient.service.getMarker(LoginActivity.xml.getString("access_token", ""), newLalng.longitude, newLalng.latitude).enqueue(new Callback<List<cn.hellovega.zhimingdi.model.Marker>>() {
                @Override
                public void onResponse(Call<List<cn.hellovega.zhimingdi.model.Marker>> call, Response<List<cn.hellovega.zhimingdi.model.Marker>> response) {
                    asyncTask = new AsyncTask<List<cn.hellovega.zhimingdi.model.Marker>, Void, Void>() {
                        @Override
                        protected Void doInBackground(List<cn.hellovega.zhimingdi.model.Marker>[] lists) {
                            for (final cn.hellovega.zhimingdi.model.Marker marker : lists[0]) {
                                try {
                                    String path = GlideApp.with(MapActivity.this).downloadOnly().load(marker.getIconUrl()).submit().get().getAbsolutePath();
                                    aMap.addMarker(new MarkerOptions().position(new LatLng(marker.getLatitude(), marker.getLongitude())).title(marker.getName()).icon(BitmapDescriptorFactory.fromPath(path)));
                                } catch (Exception e) {
                                }
                            }
                            return null;
                        }
                    }.execute(response.body());

                }

                @Override
                public void onFailure(Call<List<cn.hellovega.zhimingdi.model.Marker>> call, Throwable t) {
                }
            });
        }
    }

}
