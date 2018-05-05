package cn.hellovega.zhimingdi.ui.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.hellovega.zhimingdi.R;
import cn.hellovega.zhimingdi.ui.fragment.FunctionFragment;

/**
 * Created by vega on 3/17/18.
 */

public class FunctionFragmentAdapter extends SurpotClickAdapter<FunctionFragmentAdapter.ViewHolder>{
    private static final String TAG = "FunctionFragmentAdapter";
    private SharedPreferences.Editor editor;
    private List<String> contentList =new ArrayList<>();
    private View.OnLongClickListener onLongClickListener;
    public void setOnLongClickListener(View.OnLongClickListener onLongClickListener) {
        this.onLongClickListener = onLongClickListener;
    }

    public void init() {
        editor =FunctionFragment.mentionXml.edit();
    }

    public void setContents(String date) {
        contentList.clear();
        int i1 =0;String str;
        while((str=FunctionFragment.mentionXml.getString(date+"_"+i1,null))!=null) {
            contentList.add(str);
            ++i1;
        }
        notifyDataSetChanged();
    }


    public void change(String date, int i1, String newMention) {
        contentList.remove(i1);contentList.add(i1, newMention);
        notifyItemChanged(i1);

        editor.putString(date+"_"+i1, newMention);
        editor.commit();
    }

    public void insert(String date, int i1, String mention){
        contentList.add(mention);
        notifyItemInserted(i1);

        editor.putString(date+"_"+i1, mention);
        editor.commit();
    }

    public void delete(String date, int i1) {
        contentList.remove(i1);
        notifyItemRemoved(i1);

        String str;
        while((str=FunctionFragment.mentionXml.getString(date+"_"+(i1+1), null))!=null) {
            editor.putString(date+"_"+i1, str);
            ++i1;
        }
        editor.remove(date+"_"+i1);
        editor.commit();

    }

    public void deleteBirth(String date, int i1, String mention) {
        contentList.remove(i1);
        notifyItemRemoved(i1);

        String birthDate =date.substring(4,8);
        Map<String,String> map =(Map<String,String>)FunctionFragment.mentionXml.getAll();
        for(String key : map.keySet())
            if( key.substring(4,8).equals(birthDate) && mention.equals(map.get(key)) ) {
                i1 =Integer.valueOf(key.split("_")[1]).intValue();  String str,tmpDate=key.substring(0,8);
                while((str=FunctionFragment.mentionXml.getString(tmpDate+"_"+(i1+1), null))!=null) {
                    editor.putString(tmpDate+"_"+i1, str);
                    ++i1;
                }
                editor.remove(tmpDate+"_"+i1);
            }
        editor.commit();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public ViewHolder(View itemView) {
            super(itemView);
            textView =itemView.findViewById(R.id.textView);
        }
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view =View.inflate(parent.getContext(), R.layout.widget_mention, null);
        view.setOnClickListener(clickListener);
        view.setOnLongClickListener(onLongClickListener);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.textView.setText(contentList.get(position));
    }

    @Override
    public int getItemCount() {
        return contentList.size();
    }

}
