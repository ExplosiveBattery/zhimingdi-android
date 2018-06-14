package cn.hellovega.zhimingdi.ui.widget;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import org.joda.time.DateTime;

import cn.hellovega.zhimingdi.app.AppController;


/**
 * Created by Battery on 2017/9/3.
 */

public class ShadowImageView extends AppCompatImageView {
    private String chineseMonth[] ={"一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月"};
    private String chineseWeekDay[] ={"星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日"};
    private int mYear,mMonth,mDay;
    private static final Xfermode msXfermode = new PorterDuffXfermode(PorterDuff.Mode.XOR);
    private  float mSingleNumSize = 850f;


    public ShadowImageView(Context context) {
        super(context);
    }

    public ShadowImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ShadowImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setDay(int mDay) {
        this.mDay = mDay;
    }
    public void setMonth(int mMonth) {
        this.mMonth = mMonth;
    }
    public void setYear(int mYear) {
        this.mYear = mYear;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //init
        Drawable drawable = getDrawable();
        if (drawable == null || getWidth() == 0 || getHeight() == 0) {
            return;
        }


        //遮罩创建
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas bitmapCanvas = new Canvas(bitmap);
        bitmapCanvas.drawColor(0xba000000);


        //镂空文字绘制
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG); //抗锯齿
        paint.setXfermode(msXfermode);
            //日数
        if( mDay>=10 ) paint.setTextSize(mSingleNumSize);
        else paint.setTextSize((float)(mSingleNumSize*1.2));
        paint.setTextAlign(Paint.Align.CENTER);   /* paint.setTypeface()*/
        Paint.FontMetricsInt fmi = paint.getFontMetricsInt();
        bitmapCanvas.drawText(""+mDay,canvas.getWidth()/2, (canvas.getHeight()-fmi.ascent-fmi.bottom)/2, paint);
            //月份
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(120f);
        paint.setTypeface(AppController.typeface);
        bitmapCanvas.drawText(chineseMonth[mMonth-1],60, canvas.getHeight()*7/8, paint);
            //星期
        paint.setTextSize(80f);
        DateTime dt =new DateTime(mYear,mMonth,mDay,0,0,1);
        bitmapCanvas.drawText(chineseWeekDay[dt.getDayOfWeek()-1],60, canvas.getHeight()*7/8+140, paint);


        //图片合并
        canvas.drawBitmap(bitmap,0, 0,paint);


    }
}
