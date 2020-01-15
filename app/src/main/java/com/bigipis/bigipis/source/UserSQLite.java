package com.bigipis.bigipis.source;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UserSQLite extends SQLiteOpenHelper {

    public static final String VEGETABLES_TABLE = "User";
    public static final String KEY_ID = "_id";
    public static final String KEY_IMAGE_ID = "IMAGE_ID";
    public static final String KEY_NICKNAME = "NICKNAME";
    public static final String KEY_HEIGHT = "HEIGHT";
    public static final String KEY_WEIGHT = "WEIGHT";
    public static final String KEY_ALL_DISTANCE = "ALL_DISTANCE";
    public static final String KEY_MAX_DISTANCE = "MAX_DISTANCE";
    public static final String KEY_MAX_SPEED = "MAX_SPEED";
    public static final String KEY_RATING = "RATING";
    public static final String SERIAL_S = "RATING";
    public static final String SERIAL_W = "RATING";
    public static final int DATABASE_VERSION = 1;

    public UserSQLite(Context context) {
        super(context, VEGETABLES_TABLE, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table " + VEGETABLES_TABLE + "(" + KEY_ID
                + " integer primary key," + KEY_NICKNAME + " text," + KEY_IMAGE_ID + " integer,"
                + KEY_HEIGHT + " integer," + KEY_WEIGHT + " integer," + KEY_ALL_DISTANCE + " integer,"
                + KEY_MAX_DISTANCE + " integer," + KEY_MAX_SPEED + " integer," + KEY_RATING + " real,"
                + SERIAL_S + " text," + SERIAL_W + " text" + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists " + VEGETABLES_TABLE);
        onCreate(sqLiteDatabase);
    }
    @Override
    public void onDowngrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists " + VEGETABLES_TABLE);
        onCreate(sqLiteDatabase);
    }
}
