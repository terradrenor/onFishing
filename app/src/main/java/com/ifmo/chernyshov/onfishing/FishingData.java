package com.ifmo.chernyshov.onfishing;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Andrey Chernyshov on 15.12.2016.
 */
public class FishingData {
    public String place;
    public String time;
    public String dir;
    public int temp;
    public int pres;
    public int speed;
    public int mark;
    public ArrayList<String> pictures;

    public FishingData(String place, String time, int mark, int temp, int pres, int speed, String dir) {
        this.place = place;
        this.time = time;
        this.mark = mark;
        this.temp = temp;
        this.pres = pres;
        this.speed = speed;
        this.dir = dir;
        pictures = new ArrayList<>();
    }

    public FishingData(String place, String time, int mark, String pictures, int temp, int pres,
                       int speed, String dir) {
        this.place = place;
        this.time = time;
        this.mark = mark;
        this.pictures = new ArrayList<>();
        this.temp = temp;
        this.pres = pres;
        this.speed = speed;
        this.dir = dir;
        parseString(pictures);
    }

    public void parseString(String s) {
        if(!s.isEmpty()) {
            String[] str = s.split("\\$");

            for (int i = 0; i < str.length; i++) {
                pictures.add(str[i]);
            }
        }
    }
}
