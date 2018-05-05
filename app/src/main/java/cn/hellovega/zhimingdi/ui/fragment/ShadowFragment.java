package cn.hellovega.zhimingdi.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.todddavies.components.progressbar.ProgressWheel;

import cn.hellovega.zhimingdi.GlideApp;
import cn.hellovega.zhimingdi.R;
import cn.hellovega.zhimingdi.model.network.NetworkDefine;
import cn.hellovega.zhimingdi.ui.activity.ImageActivity;
import cn.hellovega.zhimingdi.ui.widget.ShadowImageView;
import cn.hellovega.zhimingdi.ui.widget.glideprogress.GlideImageLoader;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * Created by Battery on 2017/9/22.
 */

public class ShadowFragment extends Fragment {
    private int mYear,mMonth,mDay;
    private ShadowImageView mShadowImageView;
    private ProgressWheel mProgressWheel;
    private String date;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle =getArguments();
        mYear =bundle.getInt("year");
        mMonth =bundle.getInt("month");
        mDay =bundle.getInt("day");

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mShadowImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mProgressWheel.getTag(R.id.isDownload)!=null) {
                    Intent intent = new Intent(getActivity(), ImageActivity.class);
                    intent.putExtra("bgURL", NetworkDefine.PIC_QUERY_URl+date + NetworkDefine.BG_QUERY_SUFFIX);
                    intent.putExtra("date", date);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        date =String.valueOf(mYear);
        date+=(mMonth<10)?"0"+mMonth:mMonth;
        date+=(mDay<10)?"0"+mDay:mDay;


        View view = View.inflate(getContext(), R.layout.widget_shadow, null);
        mShadowImageView =(ShadowImageView)view.findViewById(R.id.shadowImageView);
        mShadowImageView.setYear(mYear);
        mShadowImageView.setMonth(mMonth);
        mShadowImageView.setDay(mDay);
        mProgressWheel =(ProgressWheel) view.findViewById(R.id.progressbar);

        GlideApp.with(ShadowFragment.this).load(NetworkDefine.PIC_QUERY_URl+date).placeholder(R.drawable.default_backgroud).transition(withCrossFade()).into(mShadowImageView);
        GlideImageLoader.download(mProgressWheel,NetworkDefine.PIC_QUERY_URl+date+NetworkDefine.BG_QUERY_SUFFIX);


        return  view;
    }



}
