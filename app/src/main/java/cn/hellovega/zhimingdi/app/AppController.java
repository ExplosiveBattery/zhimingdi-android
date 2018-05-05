package cn.hellovega.zhimingdi.app;

import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;

import net.danlew.android.joda.JodaTimeAndroid;
import java.lang.reflect.Field;


public class AppController extends Application implements Thread.UncaughtExceptionHandler {
    public static Typeface typeface;

    @Override
    public void onCreate() {
        super.onCreate();

        JodaTimeAndroid.init(this);
        typeface =Typeface.createFromAsset(getAssets(), "fonts/fangzhengqiti.ttf");
        replaceTypefaceField("MONOSPACE", typeface);

    }

    @Override
    public void uncaughtException(Thread thread, Throwable e) {
        System.exit(1);
    }


    private void replaceTypefaceField(String fieldName, Object value) {
        try {
            Field defaultField = Typeface.class.getDeclaredField(fieldName);
            defaultField.setAccessible(true);
            defaultField.set(null, value);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
