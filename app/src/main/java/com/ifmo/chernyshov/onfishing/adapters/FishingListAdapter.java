package com.ifmo.chernyshov.onfishing.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ifmo.chernyshov.onfishing.FishingData;
import com.ifmo.chernyshov.onfishing.R;
import com.ifmo.chernyshov.onfishing.database.DataStorage;

import java.util.ArrayList;

/**
 * Created by Andrey Chernyshov on 17.12.2016.
 */
public class FishingListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<FishingData> resources;
    private DataStorage ds;

    public FishingListAdapter(Context context, DataStorage ds) {
        this.context = context;
        this.ds = ds;
        resources = ds.readResources();
    }

    @Override
    public int getCount() {
        return resources.size();
    }

    @Override
    public Object getItem(int position) {
        return resources.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public void notifyDataSetChanged() {
        resources = ds.readResources();
        super.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.fishing_list_item, null);
        FishingData curData = resources.get(position);
        TextView place = (TextView) v.findViewById(R.id.item_place);
        TextView time = (TextView) v.findViewById(R.id.item_time);
        TextView mark = (TextView) v.findViewById(R.id.item_mark);
        ImageView image = (ImageView) v.findViewById(R.id.item_image);

        place.setText(curData.place);
        time.setText(curData.time);
        mark.setText((curData.mark / 2.0) + "");
        ArrayList<String> pictures = curData.pictures;
        if (!pictures.isEmpty()) {
            image.setImageBitmap(GalleryAdapter.decodeSampledBitmapFromResource(pictures.get(0), 142, 142));
        }
        return v;
    }
}
