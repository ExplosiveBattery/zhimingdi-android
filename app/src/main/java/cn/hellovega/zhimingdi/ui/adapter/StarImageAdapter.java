package cn.hellovega.zhimingdi.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.util.List;

import cn.hellovega.zhimingdi.GlideApp;
import cn.hellovega.zhimingdi.R;
import cn.hellovega.zhimingdi.model.network.NetworkDefine;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * Created by vega on 3/15/18.
 */

public class StarImageAdapter extends SurpotClickAdapter<StarImageAdapter.ViewHolder> {
    private Context context;
    private List<String> dateList;

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public ViewHolder(View itemView) {
            super(itemView);
            imageView =itemView.findViewById(R.id.imageview);
        }
    }

    public StarImageAdapter(List<String> dateList) {
        super();
        this.dateList =dateList;
    }

    @Override
    public StarImageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context =parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.widget_history_image_item, null);
        view.setOnClickListener(clickListener);
        return new StarImageAdapter.ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return dateList.size();
    }

    @Override
    public void onBindViewHolder(StarImageAdapter.ViewHolder holder, int position) {
        GlideApp.with(context).load(NetworkDefine.PIC_QUERY_URl+dateList.get(position)+NetworkDefine.SM_QUERY_SUFFIX).transition(withCrossFade()).into(holder.imageView);
    }

}

