package cn.hellovega.zhimingdi.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.todddavies.components.progressbar.ProgressWheel;

import org.joda.time.DateTime;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.hellovega.zhimingdi.R;
import cn.hellovega.zhimingdi.model.network.NetworkDefine;
import cn.hellovega.zhimingdi.ui.adapter.HistoryImageAdapter;
import cn.hellovega.zhimingdi.ui.adapter.StarImageAdapter;
import cn.hellovega.zhimingdi.ui.adapter.SurpotClickAdapter;
import cn.hellovega.zhimingdi.ui.widget.GridSpacingItemDecoration;
import cn.hellovega.zhimingdi.ui.widget.glideprogress.GlideImageLoader;

/**
 * Created by vega on 3/12/18.
 */

public class MultiImageActivity extends AppCompatActivity {
    private static final String TAG = "MultiImageActivity";
    public static int TAG_HISTORY =1, TAG_STAR=0;
    private SurpotClickAdapter<?> adapter;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_image);
        ButterKnife.bind(this);
        recyclerView.setLayoutManager(new GridLayoutManager(this,5));
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(5, 12,true));

        if( getIntent().getIntExtra("action",0)==TAG_STAR ) {
            adapter =new StarImageAdapter(LoginActivity.starPicDate);
            setAdapter();
        }else {
            adapter =new HistoryImageAdapter();
            setAdapter();
        }

    }



    private void setAdapter() {
        recyclerView.setAdapter(adapter);
        adapter.setViewClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                int position =recyclerView.getChildAdapterPosition(view);
                String date;
                if(HistoryImageAdapter.class.isInstance(adapter) )
                    date =new DateTime().minusDays(adapter.getItemCount()-position-1).toString("yyyyMMdd");
                else
                    date =LoginActivity.starPicDate.get(position);
                String url =NetworkDefine.PIC_QUERY_URl
                        +date
                        +NetworkDefine.BG_QUERY_SUFFIX;

                if(view.findViewById(R.id.progressbar).getTag(R.id.isDownload)!=null) {
                    Intent intent =new Intent(MultiImageActivity.this, ImageActivity.class);
                    intent.putExtra("bgURL",url);
                    intent.putExtra("date", date);
                    startActivity(intent);
                }else {
                    GlideImageLoader.download((ProgressWheel) view.findViewById(R.id.progressbar),url);
                }
            }
        });
    }

//                失效了的办法
//                DiskCache disk  = DiskLruCacheWrapper.get(GlideApp.getPhotoCacheDir(MultiImageActivity.this), 250 * 1024 * 1024);
//                DataCacheKey dataCacheKey = new DataCacheKey(new GlideUrl(url), EmptySignature.obtain());
//                File file =disk.get(dataCacheKey);



}
