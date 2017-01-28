package com.ifmo.chernyshov.onfishing.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.ifmo.chernyshov.onfishing.FishingData;
import com.ifmo.chernyshov.onfishing.R;
import com.ifmo.chernyshov.onfishing.adapters.FishingListAdapter;
import com.ifmo.chernyshov.onfishing.database.DataStorage;
import com.ifmo.chernyshov.onfishing.dialogs.WeatherPickerDialog;

/**
 * Created by Andrey Chernyshov on 16.12.2016.
 */
public class MainActivity extends Activity {
    static final String MODE_INTENT = "MODE_INTENT";
    static final int ADD_MODE = 1;
    static final int VIEW_EDIT_MODE = 2;

    static DataStorage ds;
    static FishingListAdapter adapter;
    static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);
        context = this;

        ds = new DataStorage(this);

        ListView lv = (ListView) findViewById(R.id.fishing_list);
        adapter = new FishingListAdapter(this, ds);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                TextView time = (TextView) view.findViewById(R.id.item_time);
                FishingData cur = ds.findDataByTime(time.getText().toString());
                Intent intent = new Intent(context, NewEvent.class);
                intent.putExtra(NewEvent.PLACE, cur.place);
                intent.putExtra(NewEvent.TIME, cur.time);
                intent.putExtra(NewEvent.RATING, cur.mark);
                intent.putExtra(NewEvent.PICTURES, cur.pictures);
                if (cur.pres != 0) {
                    intent.putExtra(NewEvent.WEATHER, WeatherPickerDialog.makeWeatherString(cur.temp, cur.pres, cur.speed, cur.dir));
                } else {
                    intent.putExtra(NewEvent.WEATHER, "");
                }
                intent.putExtra(MODE_INTENT, VIEW_EDIT_MODE);
                startActivity(intent);
            }
        });
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, final View view, int position, long id) {
                ImageButton b = (ImageButton) view.findViewById(R.id.delete_button);
                b.setVisibility(View.VISIBLE);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TextView time = (TextView) view.findViewById(R.id.item_time);
                        ds.removeFishingData(time.getText().toString());
                        adapter.notifyDataSetChanged();
                    }
                });
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_button: {
                Intent intent = new Intent(this, NewEvent.class);
                intent.putExtra(MODE_INTENT, ADD_MODE);
                startActivity(intent);
            }
        }
        return super.onMenuItemSelected(featureId, item);
    }
}
