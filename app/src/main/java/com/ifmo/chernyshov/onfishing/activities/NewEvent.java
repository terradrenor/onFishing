package com.ifmo.chernyshov.onfishing.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.DatePicker;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.ifmo.chernyshov.onfishing.R;
import com.ifmo.chernyshov.onfishing.adapters.GalleryAdapter;
import com.ifmo.chernyshov.onfishing.database.DataStorage;
import com.ifmo.chernyshov.onfishing.dialogs.AddNewPlaceDialog;
import com.ifmo.chernyshov.onfishing.dialogs.WeatherPickerDialog;
import com.ifmo.chernyshov.onfishing.tasks.GetWeatherTask;

import org.lucasr.twowayview.TwoWayView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;

/**
 * Created by Andrey Chernyshov on 17.12.2016.
 */
public class NewEvent extends Activity {
    private final int PICK_IMAGE_REQUEST = 1;
    private final int TAKE_PHOTO_REQUEST = 2;
    public static final String TIME = "TIME";
    public static final String PLACE = "PLACE";
    public static final String PICTURES = "PICTURES";
    public static final String RATING = "RATING";
    public static final String WEATHER = "WEATHER";

    private DataStorage ds;
    private Spinner sp;
    private Button setDate, setTime, loadPhoto, takePhoto, weather;
    private static int hour = 0, minutes = 0;
    private TextView time, weatherData;
    private RatingBar rating;
    private TwoWayView images;
    public static ArrayList<String> resources;
    private ArrayList<String> places = new ArrayList<>();
    private TwoWayView list;
    private GalleryAdapter adapter;

    private String currentPhotoPath;
    private String oldDate;

    private int MODE;
    private int year = 0, monthOfYear = 0, dayOfMonth = 0;

    private double latitude = 0, longtitude = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_event_screen);

        ds = new DataStorage(this);
        time = (TextView) findViewById(R.id.exact_time_text);
        rating = (RatingBar) findViewById(R.id.rating_bar);
        weatherData = (TextView) findViewById(R.id.weather_data);
        Intent current = getIntent();
        Bundle b = current.getExtras();
        MODE = b.getInt(MainActivity.MODE_INTENT);
        if (MODE == MainActivity.VIEW_EDIT_MODE) {
            savedInstanceState = current.getExtras();
        }

        spinnerSetUp();
        restoreSavedInstance(savedInstanceState);

        setUpDateAndTimeButtons();
        imageActionListener();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        String s = "";
        places = ds.getPlaces();
        if(sp.getSelectedItemPosition() != -1) {
            s = places.get(sp.getSelectedItemPosition());
        }
        if (!s.isEmpty()) {
            outState.putString(PLACE, s);
        }
        s = time.getText().toString();
        if (!s.isEmpty()) {
            outState.putString(TIME, s);
        }
        outState.putInt(RATING, rating.getProgress());
        if (resources.size() > 0) {
            outState.putStringArrayList(PICTURES, resources);
        }
        outState.putString(WEATHER, weatherData.getText().toString());
    }

    private void restoreSavedInstance(Bundle savedInstance) {
        if (savedInstance != null) {
            if ((resources = savedInstance.getStringArrayList(PICTURES)) == null) {
                resources = new ArrayList<>();
            }

            String s = savedInstance.getString(PLACE);
            if (s != null) {
                sp.setSelection(places.indexOf(s));
            }
            s = savedInstance.getString(TIME);
            if (s != null) {
                time.setText(s);
                oldDate = s;
            }

            rating.setProgress(savedInstance.getInt(RATING));
            weatherData.setText(savedInstance.getString(WEATHER));
        } else {
            resources = new ArrayList<>();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_place_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ok_place: {
                if (time.getText().toString().isEmpty()) {
                    Toast.makeText(this, "You need to set up date!", Toast.LENGTH_SHORT).show();
                    break;
                }
                String[] parseWeather = parseWeatherData(weatherData.getText().toString());
                if (MODE == MainActivity.ADD_MODE) {
                    if (parseWeather != null) {
                        String s = "";
                        if(places.size() == 0) {
                            s = (String)sp.getAdapter().getItem(0);
                        } else {
                            s = places.get(sp.getSelectedItemPosition());
                        }
                        ds.addFishingData(s, time.getText().toString(),
                                rating.getProgress(), resources, Integer.valueOf(parseWeather[0]),
                                Integer.valueOf(parseWeather[1]), Integer.valueOf(parseWeather[2]),
                                parseWeather[3]);
                    } else {
                        String s = "";
                        if(places.size() == 0) {
                            s = (String)sp.getAdapter().getItem(0);
                        } else {
                            s = places.get(sp.getSelectedItemPosition());
                        }
                        ds.addFishingData(s, time.getText().toString(),
                                rating.getProgress(), resources, 0, 0, 0, "");
                    }
                } else if (MODE == MainActivity.VIEW_EDIT_MODE) {
                    if (parseWeather != null) {
                        String s = "";
                        if(places.size() == 0) {
                            s = (String)sp.getAdapter().getItem(0);
                        } else {
                            s = places.get(sp.getSelectedItemPosition());
                        }
                        ds.updateFishingData(s, time.getText().toString(),
                                rating.getProgress(), resources, Integer.valueOf(parseWeather[0]),
                                Integer.valueOf(parseWeather[1]), Integer.valueOf(parseWeather[2]),
                                parseWeather[3], oldDate);
                    } else {
                        String s = "";
                        if(places.size() == 0) {
                            s = (String)sp.getAdapter().getItem(0);
                        } else {
                            s = places.get(sp.getSelectedItemPosition());
                        }
                        ds.updateFishingData(s, time.getText().toString(),
                                rating.getProgress(), resources, 0, 0, 0, "", oldDate);
                    }
                }
                try {
                    MainActivity.adapter.notifyDataSetChanged();
                } catch (NullPointerException exc) {
                    // sometimes application reloads and MainActivity too
                }
                finish();
            }
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
                Uri uri = data.getData();
                String scheme = uri.getScheme();
                switch (scheme) {
                    case "content": {
                        String[] projection = {MediaStore.Images.Media.DATA};
                        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
                        if (cursor != null) {
                            cursor.moveToFirst();
                            int columnIndex = cursor.getColumnIndex(projection[0]);
                            resources.add(cursor.getString(columnIndex));
                            cursor.close();
                        }
                        break;
                    }
                    case "file": {
                        resources.add(uri.getPath());
                    }
                }
                adapter.notifyDataSetChanged();
            } else if (requestCode == TAKE_PHOTO_REQUEST) {
                resources.add(currentPhotoPath);
                adapter.notifyDataSetChanged();
            }
        }
    }

    private File createImageFile() throws IOException {
        String time = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageName = "JPEG_" + time + "_";
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageName, ".jpg", dir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void spinnerSetUp() {
        sp = (Spinner) findViewById(R.id.item_spinner);
        places = ds.getPlaces();
        sp.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, places));
    }

    private void imageActionListener() {
        images = (TwoWayView) findViewById(R.id.list);
        adapter = new GalleryAdapter(this, resources);
        images.setAdapter(adapter);

        loadPhoto = (Button) findViewById(R.id.button_load_photo);
        loadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent.createChooser(intent, "Choose image"), PICK_IMAGE_REQUEST);
            }
        });

        takePhoto = (Button) findViewById(R.id.button_take_photo);
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException exc) {
                        exc.printStackTrace();
                        Log.e("FISHING", "Error occured while creating file");
                    }
                    if (photoFile != null) {
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                        startActivityForResult(takePictureIntent, TAKE_PHOTO_REQUEST);
                    }
                }
            }
        });
    }

    private void setUpDateAndTimeButtons() {
        setDate = (Button) findViewById(R.id.button_set_date);
        setTime = (Button) findViewById(R.id.button_set_time);
        final DatePickerDialog.OnDateSetListener setDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int y, int m, int d) {
                year = y;
                monthOfYear = m;
                dayOfMonth = d;
                time.setText(compoundTimeAndDate());
            }
        };
        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        monthOfYear = c.get(Calendar.MONTH);
        dayOfMonth = c.get(Calendar.DAY_OF_MONTH);

        final TimePickerDialog.OnTimeSetListener setTimeListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                hour = hourOfDay;
                minutes = minute;
                time.setText(compoundTimeAndDate());
            }
        };

        hour = c.get(Calendar.HOUR_OF_DAY);
        minutes = c.get(Calendar.MINUTE);

        final Context context = this;

        setDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog d = new DatePickerDialog(context, setDateListener, year, monthOfYear, dayOfMonth);
                d.setTitle(R.string.date_of_fishing);
                d.show();
            }
        });
        setTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog t = new TimePickerDialog(context, setTimeListener, hour, minutes, true);
                t.setTitle(R.string.time_of_fishing);
                t.show();
            }
        });
    }

    public void addNewPlace(View v) {
        Dialog d = new AddNewPlaceDialog(this, ds, sp);
        d.show();
    }

    public void getWeather(final View v) {
        final LocationManager location = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Handler h = new Handler();
        try {
            location.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, netWorkListener);
            location.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, netWorkListener);
            h.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (latitude == 0 && longtitude == 0) {
                            getWeather(v);
                            return;
                        }
                        location.removeUpdates(netWorkListener);
                    } catch (SecurityException exc) {
                        exc.printStackTrace();
                    }
                }
            }, 30000);
        } catch (SecurityException exc) {
            exc.printStackTrace();
        }

        new GetWeatherTask(this, weatherData).execute(latitude, longtitude);
    }

    private final LocationListener netWorkListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            latitude = location.getLatitude();
            longtitude = location.getLongitude();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    };

    public void weatherPick(View v) {
        Dialog dialog = new WeatherPickerDialog(this, weatherData);
        dialog.show();
    }

    private String compoundTimeAndDate() {
        Formatter formatter = new Formatter();
        return formatter.format("%d / %d / %d %02d : %02d", dayOfMonth, monthOfYear, year, hour, minutes).toString();
    }

    public static String[] parseWeatherData(String s) {
//        "%d °C, %d mm Hg, %d m/sec %s"
        if (!s.isEmpty()) {
            String[] parse = s.split(" ");
            String dir = parse[7];
            if (parse.length > 8) {
                dir = dir.concat(" ").concat(parse[8]);
            }
            return new String[]{parse[0], parse[2], parse[5], dir};
        }
        return null;
    }
}
