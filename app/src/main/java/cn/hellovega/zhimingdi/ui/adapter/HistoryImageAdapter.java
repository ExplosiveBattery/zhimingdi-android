package cn.hellovega.zhimingdi.ui.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.joda.time.DateTime;
import org.joda.time.Days;


import cn.hellovega.zhimingdi.GlideApp;
import cn.hellovega.zhimingdi.R;
import cn.hellovega.zhimingdi.model.network.NetworkDefine;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * Created by vega on 3/12/18.
 */

public class HistoryImageAdapter extends SurpotClickAdapter<HistoryImageAdapter.ViewHolder> {
    private static final String TAG = "HistoryImageAdapter";
    private Context context;

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public ViewHolder(View itemView) {
            super(itemView);
            imageView =itemView.findViewById(R.id.imageview);
        }
    }
    public HistoryImageAdapter() {
        super();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context =parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.widget_history_image_item, null);
        view.setOnClickListener(clickListener);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        DateTime dt =new DateTime();
        DateTime start =new DateTime("2018-03-01");
        return Days.daysBetween(start, dt).getDays()+1;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        GlideApp.with(context).load(NetworkDefine.PIC_QUERY_URl+ new DateTime().minusDays(getItemCount()-position-1).toString("yyyyMMdd")+NetworkDefine.SM_QUERY_SUFFIX).transition(withCrossFade()).into(holder.imageView);
    }
}
