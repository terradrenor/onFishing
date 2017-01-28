package com.ifmo.chernyshov.onfishing.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.ifmo.chernyshov.onfishing.activities.NewEvent;
import com.ifmo.chernyshov.onfishing.R;

import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by Andrey Chernyshov on 17.12.2016.
 */
public class WeatherPickerDialog extends Dialog {

    private NumberPicker pressure, wind;
    private com.ifmo.chernyshov.onfishing.MyNumberPicker temperature;
    private Spinner dir;
    private Context context;
    private Button ok;

    class MyFormatter implements NumberPicker.Formatter {

        @Override
        public String format(int value) {
            return "a";
        }
    }

    HashMap<String, Integer> directions;

    public WeatherPickerDialog(final Context context, final TextView weatherData) {
        super(context);
        this.context = context;
        setContentView(R.layout.weather_picker);
        setTitle(R.string.weather_choose);
        pressure = (NumberPicker) findViewById(R.id.pressure_picker);
        pressure.setMinValue(700);
        pressure.setMaxValue(800);
        pressure.setWrapSelectorWheel(false);
//        pressure.setValue(756);


        final int minValue = -50;
        final int maxValue = 50;
        temperature = (com.ifmo.chernyshov.onfishing.MyNumberPicker) findViewById(R.id.temperature_picker);
        temperature.removeFilter();
        temperature.setMinValue(0);
        temperature.setMaxValue(maxValue - minValue);
        String[] a = new String[maxValue - minValue + 1];
        for (int i = 0; i < a.length; i++) {
            a[i] = " " + Integer.toString(i + minValue);
        }
        temperature.setDisplayedValues(a);
//        temperature.setValue(10);
        temperature.setWrapSelectorWheel(false);

        wind = (NumberPicker) findViewById(R.id.wind_picker);
        wind.setMinValue(0);
        wind.setMaxValue(30);
        wind.setWrapSelectorWheel(false);
//        wind.setValue(2);
        //TODO: localization
        directions = new HashMap<>();
        final String[] direct = {"East", "West", "South", "North", "North East", "North West", "South East", "South West"};
        for (int i = 0; i < 8; i++) {
            directions.put(direct[i], i);
        }
        dir = (Spinner) findViewById(R.id.wind_dir_spinner);
        dir.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, Arrays.asList(direct)));

        setValues(weatherData.getText().toString());

        ok = (Button) findViewById(R.id.weather_pick_ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weatherData.setText(makeWeatherString(temperature.getValue() - 50, pressure.getValue(),
                        wind.getValue(), direct[dir.getSelectedItemPosition()]));
                dismiss();
            }
        });
    }

    private void setValues(String s) {
        if (!s.isEmpty()) {
            String[] parse = NewEvent.parseWeatherData(s);
            temperature.setValue(Integer.valueOf(parse[0]) + 50);
            pressure.setValue(Integer.valueOf(parse[1]));
            wind.setValue(Integer.valueOf(parse[2]));
            dir.setSelection(directions.get(parse[3]));
        } else {
            temperature.setValue(60);
            pressure.setValue(756);
            wind.setValue(2);
            dir.setSelection(0);
        }
    }

    public static String makeWeatherString(int temp, int pressure, int windSpeed, String windDir) {
        return String.format("%d °C, %d mm Hg, %d m/sec %s", temp, pressure, windSpeed, windDir);
    }
}
