package com.ifmo.chernyshov.onfishing.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by Andrey Chernyshov on 17.12.2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper implements BaseColumns {

    static final String DATABASE_NAME = "fishing_data";

    static final String TABLE_PLACES = "TABLE_PLACES";
    static final String PLACE_NAME = "PLACE_NAME";

    static final String TABLE_FISHINGS = "TABLE_FISHINGS";
    static final String FISHING_PLACE = "FISHING_PLACE";
    static final String FISHING_TIME = "FISHING_TIME";
    static final String FISHING_MARK = "FISHING_MARK";
    static final String FISHING_PHOTOS = "FISHING_PHOTOS";
    static final String TEMPERATURE = "TEMPERATURE";
    static final String PRESSURE = "PRESSURE";
    static final String WIND_SPEED = "WIND_SPEED";
    static final String WIND_DIR = "WIND_DIR";

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String scriptPlaces = "create table " + TABLE_PLACES + " ( " +
                BaseColumns._ID + " integer primary key autoincrement, " +
                PLACE_NAME + " text not null);";
        db.execSQL(scriptPlaces);

        String scriptFishings = "create table " + TABLE_FISHINGS + " ( " +
                BaseColumns._ID + " integer primary key autoincrement, " +
                FISHING_PLACE + " text not null, " +
                FISHING_TIME + " text not null, " +
                FISHING_MARK + " integer, " +
                FISHING_PHOTOS + " text, " +
                TEMPERATURE + " integer, " +
                PRESSURE + " integer, " +
                WIND_SPEED + " integer, " +
                WIND_DIR + " text not null);";
        db.execSQL(scriptFishings);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
