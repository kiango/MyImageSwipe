package com.example.torsh.myimageswipe;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by torsh on 5/12/15.
 */
public class ImageAdapterGrid extends BaseAdapter {

    private Context mContext;
    public int imageCount;
    public ArrayList<String> imageFilePath;

    public ImageAdapterGrid(Context c){
        mContext = c;

        // get images from sdCard //
        String pathToImages = Environment.getExternalStorageDirectory() + "/Pictures/myImageSwipeApp/";
        File file = new File(pathToImages);
        // makes list of full image path
        ArrayList<String> imageFilePath = new ArrayList<>();
        for (File f : file.listFiles()){
            imageFilePath.add(file.getAbsolutePath()+ "/" +f.getName());
        }
        //Log.d("imageFilePathAdapter", imageFilePath.toString());
        this.imageFilePath = imageFilePath;
        imageCount = imageFilePath.size();
    }

    @Override
    public int getCount() {
        return imageCount;
        //return 6;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView imageView;

        if (convertView == null){

            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(140, 140));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(10, 10,10, 10);
        } else {
            imageView = (ImageView) convertView;
        }

        // the position integer passed into the method is used to select an image into the imageView
        //imageView.setImageBitmap(BitmapFactory.decodeFile(imageFilePath.get(args.getInt(PICTURE_ID))));
        imageView.setImageBitmap(BitmapFactory.decodeFile(imageFilePath.get(position)));
        return imageView;

        }
}
