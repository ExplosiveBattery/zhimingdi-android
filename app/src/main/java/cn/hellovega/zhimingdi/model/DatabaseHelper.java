package cn.hellovega.zhimingdi.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by vega on 3/18/18.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "test.db";
    private SQLiteDatabase dbReader;
    private SQLiteDatabase dbWriter;

    private final String CREATE_MENTION ="Create table if not exists Mention(" +
            "Date text," +
            "Mention text);";


    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        dbReader = getReadableDatabase();
        dbWriter = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_MENTION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists AmountOfSport");
        db.execSQL("drop table if exists Record");
        db.execSQL("drop table if exists Note");
        db.execSQL("drop table if exists TargetAmountOfSport");
        onCreate(db);
    }


}