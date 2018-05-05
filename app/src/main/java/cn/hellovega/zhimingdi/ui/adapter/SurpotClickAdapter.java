package cn.hellovega.zhimingdi.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.view.View.OnClickListener;


public abstract class SurpotClickAdapter<T extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<T>
{
    public View.OnClickListener clickListener;
    public SurpotClickAdapter() {}

    public void setViewClickListener(View.OnClickListener clickListener)
    {
        this.clickListener = clickListener;
    }


}