package cn.hellovega.zhimingdi.ui.widget.glideprogress;

import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.todddavies.components.progressbar.ProgressWheel;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.hellovega.zhimingdi.GlideApp;
import cn.hellovega.zhimingdi.ProgressAppGlideModule;
import cn.hellovega.zhimingdi.R;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;


/**
 * Created by vega on 3/12/18.
 */

public class GlideImageLoader {
    private static final String TAG = "GlideImageLoader";
    private static ExecutorService cachedThreadPool = Executors.newCachedThreadPool();



    public static void download(final ProgressWheel progressBar, final String url) {


        if (url == null ) return;

        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

        //set Listener & start
        ProgressAppGlideModule.expect(url, new ProgressAppGlideModule.UIonProgressListener() {
            @Override
            public void onProgress(long bytesRead, long expectedLength) {
                if (progressBar != null && expectedLength!=0) {
                    progressBar.setProgress((int) (360 * bytesRead / expectedLength));
                }
            }

            @Override
            public float getGranualityPercentage() {
                return 1.0f;
            }
        });
        //download Image
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                GlideApp.with(progressBar.getContext())
                        .downloadOnly()
                        .load(url)
                        .listener(new RequestListener<File>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<File> target, boolean isFirstResource) {
                                ProgressAppGlideModule.forget(url);
                                if (progressBar != null)
                                    progressBar.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(File resource, Object model, Target<File> target, DataSource dataSource, boolean isFirstResource) {
                                ProgressAppGlideModule.forget(url);
                                if (progressBar != null)
                                    progressBar.setVisibility(View.GONE);
                                progressBar.setTag(R.id.isDownload, true);
                                return false;
                            }
                        }).submit();
            }
        });

    }



    public static void load(final ImageView imageView, final ProgressWheel progressBar, final String url) {
        if (url == null ) return;

        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

        //set Listener & start
        ProgressAppGlideModule.expect(url, new ProgressAppGlideModule.UIonProgressListener() {
            @Override
            public void onProgress(long bytesRead, long expectedLength) {
                if (progressBar != null) {
                    progressBar.setProgress((int) (360 * bytesRead / expectedLength));
                }
            }

            @Override
            public float getGranualityPercentage() {
                return 1.0f;
            }
        });
        //download Image
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                GlideApp.with(imageView.getContext())
                        .load(url)
                        .listener(new RequestListener<Drawable>() {
                                      @Override
                                      public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                          ProgressAppGlideModule.forget(url);
                                          if (progressBar != null && imageView != null) {
                                              progressBar.setVisibility(View.GONE);
                                              imageView.setVisibility(View.VISIBLE);
                                          }
                                          return false;
                                      }

                                      @Override
                                      public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                          ProgressAppGlideModule.forget(url);
                                          if (progressBar != null && imageView != null) {
                                              progressBar.setVisibility(View.GONE);
                                              imageView.setVisibility(View.VISIBLE);
                                          }
                                          progressBar.setTag(R.id.isDownload, true);
                                          return false;
                                      }
                                  }
                        )
                        .transition(withCrossFade())
                        .into(imageView);
            }
        });
    }



}
