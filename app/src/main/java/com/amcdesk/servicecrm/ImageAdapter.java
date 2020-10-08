package com.amcdesk.servicecrm;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    List<OtherImage> otherListImage=null;
    String url;
    // Constructor
    public ImageAdapter(Context c,List<OtherImage> contact_List,String url) {
        mContext = c;
        this.url = url;
        this.otherListImage = new ArrayList<OtherImage>();
        this.otherListImage = contact_List;

    }

    public int getCount() {
        return otherListImage.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
         ImageView imageView;

        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 350));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        }
        else{
            imageView = (ImageView) convertView;
        }
        try{
            UrlClass urlClass = new UrlClass(mContext);
            final String url = urlClass.getFileUrl()+otherListImage.get(position).getImageUrl();
            Picasso.with(mContext)
                    .load(url).into(imageView);

        } catch(Exception e){
            Log.e("Image Adapter",""+e.getMessage());
        }
        return imageView;
    }

    // Keep all Images in array
    public Integer[] mThumbIds = {
            R.drawable.badge_circle, R.drawable.badge_circle,
    };

}
