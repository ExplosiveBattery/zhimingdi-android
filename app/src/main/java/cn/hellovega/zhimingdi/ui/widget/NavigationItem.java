package cn.hellovega.zhimingdi.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.hellovega.zhimingdi.R;

/**
 * Created by vega on 2/26/18.
 */

public class NavigationItem extends FrameLayout {

    @BindView(R.id.icon_item)
    View iconItem;

    @BindView(R.id.img_icon)
    ImageView imgIcon;

    @BindView(R.id.tv_title)
    TextView tvTitle;


    private Drawable icon;

    public NavigationItem(@NonNull Context context) {
        super(context);
        init(context, null, 0, 0);
    }

    public NavigationItem(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    public NavigationItem(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public NavigationItem(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        LayoutInflater.from(context).inflate(R.layout.widget_navigation_item, this, true);
        ButterKnife.bind(this, this);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.NavigationItem, defStyleAttr, defStyleRes);
        imgIcon.setImageDrawable(a.getDrawable(R.styleable.NavigationItem_icon));
        tvTitle.setText(a.getText(R.styleable.NavigationItem_title));
        a.recycle();
    }

}
