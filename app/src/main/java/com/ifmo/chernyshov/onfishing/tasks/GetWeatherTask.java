package com.ifmo.chernyshov.onfishing.tasks;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.TextView;

import com.ifmo.chernyshov.onfishing.dialogs.WeatherPickerDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Andrey Chernyshov on 17.12.2016.
 */
public class GetWeatherTask extends AsyncTask<Double, Void, Void> {
    private final String REQUEST = "http://api.openweathermap.org/data/2.5/weather?";
    private final String API = "APPID=d6cfccaf324920c8cfae50c6fe7c8793";
    private TextView view;
    private Activity activity;

    public GetWeatherTask(Activity activity, TextView view) {
        this.view = view;
        this.activity = activity;
    }

    @Override
    protected Void doInBackground(Double... coords) {
        double lat = Math.floor(coords[0] * 100) / 100;
        double lon = Math.floor(coords[1] * 100) / 100;
        String url = REQUEST + "lat=" + lat + "&" + "lon=" + lon + "&" + API;
        try {
            final int[] answer = parseJSON(downloadUrl(url));
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    view.setText(WeatherPickerDialog.makeWeatherString(answer[0] - 273, (int) (answer[1] * 0.75), answer[2], convertDegreesInDirection(answer[3])));
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String convertDegreesInDirection(int deg) {
        if (deg >= 11 && deg < 56) {
            return "North East";
        }
        if (deg >= 56 && deg < 101) {
            return "East";
        }
        if (deg >= 101 && deg < 146) {
            return "South East";
        }
        if (deg >= 146 && deg < 191) {
            return "South";
        }
        if (deg >= 191 && deg < 236) {
            return "South West";
        }
        if (deg >= 236 && deg < 281) {
            return "West";
        }
        if (deg >= 281 && deg < 326) {
            return "North West";
        }
        return "North";
    }

    private String downloadUrl(String myurl) throws IOException {
        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        int len = 500;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);

            conn.connect();
            int response = conn.getResponseCode();
            is = conn.getInputStream();

            String contentAsString = readIt(is, len);
            return contentAsString;
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }

    private int[] parseJSON(String s) {
        int temp = 0, pressure = 0, speed = 0, deg = 0;
        try {
            JSONObject answer = new JSONObject(s);
            JSONObject main = answer.getJSONObject("main"); // main conditions
            JSONObject wind = answer.getJSONObject("wind"); // wind info
            temp = main.getInt("temp");
            pressure = main.getInt("pressure");
            speed = wind.getInt("speed");
            deg = wind.getInt("deg");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new int[]{temp, pressure, speed, deg};

    }
}
