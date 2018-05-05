package cn.hellovega.zhimingdi.ui.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import cn.hellovega.zhimingdi.R;
import cn.hellovega.zhimingdi.ui.activity.MainActivity;

/**
 * Created by vega on 3/15/18.
 */

public class MainDrawerLayout extends DrawerLayout {
    private static final String TAG = "MainDrawerLayout";
    private boolean isAnimExec =false;
    View qqShareButton, weiboShareButton, wechatShareButton;

    public MainDrawerLayout(@NonNull Context context) {
        super(context);
    }

    public MainDrawerLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MainDrawerLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }




    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        qqShareButton =findViewById(R.id.qqShareButton);
        weiboShareButton =findViewById(R.id.weiboShareButton);
        wechatShareButton =findViewById(R.id.wechatShareButton);
        if( isAnimExec ) {
            qqShareButton.offsetLeftAndRight(MainActivity.ICON_OFFSET);
            weiboShareButton.offsetLeftAndRight(3*MainActivity.ICON_OFFSET);
            wechatShareButton.offsetLeftAndRight(2*MainActivity.ICON_OFFSET);
        }
    }

    public void setAnimExec(boolean animExec) {
        isAnimExec = animExec;
    }
}
