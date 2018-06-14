package cn.hellovega.zhimingdi.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.hellovega.zhimingdi.GlideApp;
import cn.hellovega.zhimingdi.R;
import cn.hellovega.zhimingdi.model.network.NetworkClient;
import cn.hellovega.zhimingdi.model.network.NetworkResult;
import cn.hellovega.zhimingdi.ui.widget.MatrixImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ImageActivity extends AppCompatActivity {
    private static final String TAG = "ImageActivity";
    private boolean isStar =false;
    private String date;
    @BindView(R.id.matrixImage)
    MatrixImageView matrixImage;
    @BindView(R.id.backButton)
    ImageButton backButton;
    @BindView(R.id.infoOpenButton)
    ImageButton infoOpenButton;
    @BindView(R.id.starButton)
    ImageButton starButton;
    @BindView(R.id.infoView)
    ScrollView infoView;
    @BindView(R.id.infoCloseButton)
    ImageButton infoCloseButton;
    @BindView(R.id.info)
    TextView tvInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        ButterKnife.bind(this);

        date =getIntent().getStringExtra("date");
        if(LoginActivity.starPicDate.contains(date))
            isStar=true;

        setStarDrawable();

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        GlideApp.with(ImageActivity.this).load(getIntent().getStringExtra("bgURL")).centerCrop().into(matrixImage);

        //backButton
        backButton.setOnClickListener(new ImageButton.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
            }
        });
        starButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                NetworkClient.service.setStar(LoginActivity.xml.getString("access_token", ""), date, String.valueOf(isStar?1:0)).enqueue(new Callback<NetworkResult>() {
                    @Override
                    public void onResponse(Call<NetworkResult> call, Response<NetworkResult> response) {
                        isStar = !isStar;
                        setStarDrawable();
                        if(isStar) LoginActivity.starPicDate.add(date);
                        else LoginActivity.starPicDate.remove(date);
                    }
                    @Override
                    public void onFailure(Call<NetworkResult> call, Throwable t) {
                    }
                });
            }
        });
        infoOpenButton.setOnClickListener(new ImageButton.OnClickListener(){
            @Override
            public void onClick(View v) {
                NetworkClient.service.getInfo(date).enqueue(new Callback<NetworkResult.ImageInfo>() {
                    @Override
                    public void onResponse(Call<NetworkResult.ImageInfo> call, Response<NetworkResult.ImageInfo> response) {
                        tvInfo.setText(response.body().getInfo());
                        infoView.setVisibility(View.VISIBLE);
                    }
                    @Override
                    public void onFailure(Call<NetworkResult.ImageInfo> call, Throwable t) {
                    }
                });
            }
        });
        infoCloseButton.setOnClickListener(new ImageButton.OnClickListener(){
            @Override
            public void onClick(View v) {

                infoView.setVisibility(View.GONE);
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
    }


    public void changeButtonState() {
        if (backButton.getVisibility() == View.VISIBLE) {
            backButton.setVisibility(View.GONE);
            infoOpenButton.setVisibility(View.GONE);
            starButton.setVisibility(View.GONE);
        } else {
            backButton.setVisibility(View.VISIBLE);
            infoOpenButton.setVisibility(View.VISIBLE);
            starButton.setVisibility(View.VISIBLE);
        }
    }

    private void setStarDrawable() {
        if(isStar)   starButton.setImageDrawable(getDrawable(R.drawable.avatar));
        else starButton.setImageDrawable(getDrawable(R.drawable.back));
    }
}
