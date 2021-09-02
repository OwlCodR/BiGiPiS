package com.bigipis.bigipis.source;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.bigipis.bigipis.MainActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import static com.bigipis.bigipis.MainActivity.user;

public class SQLiteHelper extends SQLiteOpenHelper {

    public static final String USER_TABLE = "User";
    public static final String KEY_ID = "_id";
    public static final String KEY_IMAGE_ID = "IMAGE_ID";
    public static final String KEY_USER_ID = "USER_ID";
    public static final String KEY_NICKNAME = "NICKNAME";
    public static final String KEY_HEIGHT = "HEIGHT";
    public static final String KEY_WEIGHT = "WEIGHT";
    public static final String KEY_ALL_DISTANCE = "ALL_DISTANCE";
    public static final String KEY_MAX_DISTANCE = "MAX_DISTANCE";
    public static final String KEY_MAX_SPEED = "MAX_SPEED";
    public static final String KEY_RATING = "RATING";
    public static final String KEY_BATTERY = "BATTERY";
    public static final String KEY_TUTORIAL = "TUTORIAL";
    public static final String KEY_SAVED_ROUTES = "SAVED_ROUTES";
    public static final int DATABASE_VERSION = 1;

    public SQLiteHelper(Context context) {
        super(context, USER_TABLE, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table " + USER_TABLE +
                "(" +
                KEY_ID + " integer primary key autoincrement," +
                KEY_USER_ID + " text," +
                KEY_NICKNAME + " text," +
                KEY_IMAGE_ID + " integer," +
                KEY_HEIGHT + " integer," +
                KEY_WEIGHT + " integer," +
                KEY_ALL_DISTANCE + " integer," +
                KEY_MAX_DISTANCE + " integer," +
                KEY_MAX_SPEED + " integer," +
                KEY_BATTERY + " integer," +
                KEY_TUTORIAL + " numeric," +
                KEY_SAVED_ROUTES + " text," +
                KEY_RATING + " real" + ")");
    }

    private void updateData(ContentValues values, String uid) {
        int count = getWritableDatabase().update(
                USER_TABLE,
                values,
                KEY_USER_ID + "=" + uid,
                null);
        if (count != 0)
            Log.i("SQLite", "Database updated! Number of rows affected = " + count);
        else {
            Log.i("SQLite", "Database not updated! Number of rows affected = " + count);
            Log.i("SQLite", "uid = " + uid);
            Log.i("SQLite", "values = " + values);
        }
    }

    private void createRow(ContentValues values) {
        long newRowId = getWritableDatabase().insert(USER_TABLE, null, values);
        Log.i("SQLite", "Created new row id = " + newRowId);
    }

    public void saveUser(User user) {
        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.KEY_RATING, user.getRating());
        values.put(SQLiteHelper.KEY_BATTERY, user.getBattery());
        values.put(SQLiteHelper.KEY_ALL_DISTANCE, user.getAllDistance());
        values.put(SQLiteHelper.KEY_MAX_DISTANCE, user.getMaxDistance());
        values.put(SQLiteHelper.KEY_HEIGHT, user.getHeight());
        values.put(SQLiteHelper.KEY_IMAGE_ID, user.getIconId());
        values.put(SQLiteHelper.KEY_NICKNAME, user.getNickname());
        values.put(SQLiteHelper.KEY_SAVED_ROUTES, user.getJsonRoutes());
        values.put(SQLiteHelper.KEY_MAX_SPEED, user.getMaxSpeed());
        values.put(SQLiteHelper.KEY_TUTORIAL, user.isTutorialCompleted());
        values.put(SQLiteHelper.KEY_USER_ID, user.getUid());
        values.put(SQLiteHelper.KEY_WEIGHT, user.getWeight());

        saveData(values, user.getUid());
    }

    private long getProfilesCount() {
        SQLiteDatabase db = getReadableDatabase();
        long count = DatabaseUtils.queryNumEntries(db, USER_TABLE);
        db.close();
        return count;
    }


    public void saveData(ContentValues values, String uid) {
        Log.i("SQLite", "pageSize == " + getProfilesCount());
        if (getProfilesCount() > 0) {
            Log.i("SQLite", "Not Empty!");
            updateData(values, uid);
        } else {
            Log.i("SQLite", "Empty!");
            createRow(values);
        }
    }

    public Cursor getCursor(String[] columns, String uid) {
        Cursor cursor = getReadableDatabase().query(
                USER_TABLE,
                columns,
                KEY_USER_ID + " = ?",
                new String[]{uid},
                null,
                null,
                null
        );
        Log.i("SQLite", "Data has been got! Count = " + cursor.getCount());
        return cursor;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists " + USER_TABLE);
        onCreate(sqLiteDatabase);
    }
    @Override
    public void onDowngrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists " + USER_TABLE);
        onCreate(sqLiteDatabase);
    }
}
