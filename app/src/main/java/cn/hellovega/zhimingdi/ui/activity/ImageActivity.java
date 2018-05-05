package cn.hellovega.zhimingdi.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

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
    @BindView(R.id.introduceButton)
    ImageButton introduceButton;
    @BindView(R.id.starButton)
    ImageButton starButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        ButterKnife.bind(this);

        date =getIntent().getStringExtra("date");
        if(LoginActivity.starPicDate.contains(date))
            isStar=true;

        setStarDrawable();

        GlideApp.with(ImageActivity.this).load(getIntent().getStringExtra("bgURL")).into(matrixImage);

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
                        Log.e(TAG, "onResponse: success" );
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



    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
    }


    public void changeButtonState() {
        if (backButton.getVisibility() == View.VISIBLE) {
            backButton.setVisibility(View.GONE);
            introduceButton.setVisibility(View.GONE);
            starButton.setVisibility(View.GONE);
        } else {
            backButton.setVisibility(View.VISIBLE);
            introduceButton.setVisibility(View.VISIBLE);
            starButton.setVisibility(View.VISIBLE);
        }
    }

    private void setStarDrawable() {
        if(isStar)   starButton.setImageDrawable(getDrawable(R.drawable.avatar));
        else starButton.setImageDrawable(getDrawable(R.drawable.back));
    }
}
