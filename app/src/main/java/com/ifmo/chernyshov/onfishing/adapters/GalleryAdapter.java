package com.ifmo.chernyshov.onfishing.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.ifmo.chernyshov.onfishing.activities.NewEvent;
import com.ifmo.chernyshov.onfishing.R;

import java.util.ArrayList;

/**
 * Created by Andrey Chernyshov on 17.12.2016.
 */
public class GalleryAdapter extends BaseAdapter {
    private static int WIDTH = 142;
    private static int HEIGHT = 142;
    private Context context;
    private ArrayList<String> resources;

    public GalleryAdapter(Context context, ArrayList<String> resources) {
        this.context = context;
        this.resources = resources;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = LayoutInflater.from(context).inflate(R.layout.simple_image_item, null);
        ImageView view = (ImageView)v.findViewById(R.id.simple_image_item1);
        Bitmap b = decodeSampledBitmapFromResource(resources.get(position), WIDTH, HEIGHT);
        view.setImageBitmap(b);

        return v;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        resources = NewEvent.resources;
    }

    public static Bitmap decodeSampledBitmapFromResource(String path,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

    public static int calculateInSampleSize( BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
