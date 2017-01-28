package com.ifmo.chernyshov.onfishing.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.util.Log;

import com.ifmo.chernyshov.onfishing.FishingData;

import java.util.ArrayList;

/**
 * Created by Andrey Chernyshov on 17.12.2016.
 */
public class DataStorage {
    DatabaseHelper db;
    SQLiteDatabase sql;

    public DataStorage(Context c) {
        db = new DatabaseHelper(c, "database.db", null, 1);
    }

    public void putPlace(String name) {
        sql = db.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(db.PLACE_NAME, name);

        long result = sql.insert(db.TABLE_PLACES, null, values);
        if (result == -1) {
            Log.i("FISHING", "unsuccessful insertion");
        } else {
            Log.i("FISHING", "successful insertion");
        }

        db.close();
    }

    public void updatePlace(String old, String value) {
        sql = db.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(db.PLACE_NAME, value);
        int result = sql.update(db.TABLE_PLACES, values, db.PLACE_NAME + " = ? ", new String[]{old});
        Log.i("FISHING", "updated " + result + " items");

        db.close();
    }

    public void removePlace(String name) {
        sql = db.getWritableDatabase();

        int result = sql.delete(db.TABLE_PLACES, db.PLACE_NAME + " = ? ", new String[]{name});
        Log.i("FISHING", "deleted " + result + " items");

        db.close();
    }

    public void addFishingData(String place, String time, int vote, ArrayList<String> pictures,
                               int temp, int pres, int speed, String dir) {
        String cur = "";
        int k = 0;
        for (String s : pictures) {
            cur = cur.concat(s);
            if (k != pictures.size() - 1) {
                cur = cur.concat("$");
            }
        }

        addFishingData(place, time, vote, cur, temp, pres, speed, dir);
    }

    public void addFishingData(String place, String time, int vote, String pictures, int temp, int pres,
                               int speed, String dir) {
        sql = db.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(db.FISHING_PLACE, place);
        values.put(db.FISHING_TIME, time);
        values.put(db.FISHING_MARK, vote);
        values.put(db.FISHING_PHOTOS, pictures);
        values.put(db.TEMPERATURE, temp);
        values.put(db.PRESSURE, pres);
        values.put(db.WIND_SPEED, speed);
        values.put(db.WIND_DIR, dir);

        long result = sql.insert(db.TABLE_FISHINGS, null, values);
        if (result == -1) {
            Log.i("FISHING", "insertion failed");
        } else {
            Log.i("FISHING", "insertion succeed ");
        }

        db.close();
    }

    public void updateFishingData(String place, @NonNull String time, int vote, ArrayList<String> pictures,
                                  int temp, int pres, int speed, String dir, String exactDate) {
        String res = "";
        for (int i = 0; i < pictures.size(); i++) {
            res = res.concat(pictures.get(i));
            if (i != pictures.size() - 1) {
                res = res.concat("$");
            }
        }
        updateFishingData(place, time, vote, res, temp, pres, speed, dir, exactDate);
    }

    public void updateFishingData(String place, @NonNull String time, int vote, String pictures,
                                  Integer temp, Integer pres, Integer speed, String dir, String exactDate) {
        sql = db.getWritableDatabase();

        ContentValues values = new ContentValues();
        if (place != null) {
            values.put(db.FISHING_PLACE, place);
        }

        values.put(db.FISHING_TIME, time);

        if (vote != -1) {
            values.put(db.FISHING_MARK, vote);
        }
        if (pictures != null) {
            values.put(db.FISHING_PHOTOS, pictures);
        }
        if (temp != null) {
            values.put(db.TEMPERATURE, temp);
        }
        if (pres != null) {
            values.put(db.PRESSURE, pres);
        }
        if (speed != null) {
            values.put(db.WIND_SPEED, speed);
        }
        if (dir != null) {
            values.put(db.WIND_DIR, dir);
        }

        int result = sql.update(db.TABLE_FISHINGS, values, db.FISHING_TIME + " = ?", new String[]{exactDate});
        Log.i("FISHING", "updated " + result + " items");

        db.close();
    }

    public void removeFishingData(String time) {
        sql = db.getWritableDatabase();

        int result = sql.delete(db.TABLE_FISHINGS, db.FISHING_TIME + " = ?", new String[]{time});
        Log.i("FISHING", "deleted " + result + " items");

        db.close();
    }

    public ArrayList<FishingData> readResources() {
        sql = db.getReadableDatabase();

        Cursor cursor = sql.query(db.TABLE_FISHINGS, null, null, null, null, null, null);
        cursor.moveToFirst();

        ArrayList<FishingData> result = new ArrayList<>();

        while (!cursor.isAfterLast()) {
            String place = getString(cursor, db.FISHING_PLACE);
            String time = getString(cursor, db.FISHING_TIME);
            String pictures = getString(cursor, db.FISHING_PHOTOS);
            String dir = getString(cursor, db.WIND_DIR);

            int mark = getInt(cursor, db.FISHING_MARK);
            int temp = getInt(cursor, db.TEMPERATURE);
            int pres = getInt(cursor, db.PRESSURE);
            int speed = getInt(cursor, db.WIND_SPEED);

            result.add(new FishingData(place, time, mark, pictures, temp, pres, speed, dir));
            cursor.moveToNext();
        }
        cursor.close();
        db.close();

        return result;
    }

    private String getString(Cursor cursor, String key) {
        return cursor.getString(cursor.getColumnIndex(key));
    }

    private int getInt(Cursor cursor, String key) {
        return cursor.getInt(cursor.getColumnIndex(key));
    }

    public ArrayList<String> getPlaces() {
        sql = db.getReadableDatabase();

        Cursor cursor = sql.query(db.TABLE_PLACES, null, null, null, null, null, null);
        cursor.moveToFirst();

        ArrayList<String> result = new ArrayList<>();
        while (!cursor.isAfterLast()) {
            result.add(cursor.getString(cursor.getColumnIndex(db.PLACE_NAME)));
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        return result;
    }

    public FishingData findDataByTime(String time) {
        sql = db.getReadableDatabase();
        Cursor cursor = sql.query(db.TABLE_FISHINGS, null, db.FISHING_TIME + " = ?", new String[]{time}, null, null, null);
        cursor.moveToFirst();
        String place = getString(cursor, db.FISHING_PLACE);
        int rating = getInt(cursor, db.FISHING_MARK);
        String photos = getString(cursor, db.FISHING_PHOTOS);

        int temp = getInt(cursor, db.TEMPERATURE);
        int pres = getInt(cursor, db.PRESSURE);
        int speed = getInt(cursor, db.WIND_SPEED);
        String dir = getString(cursor, db.WIND_DIR);

        cursor.close();
        db.close();
        return new FishingData(place, time, rating, photos, temp, pres, speed, dir);
    }
}
