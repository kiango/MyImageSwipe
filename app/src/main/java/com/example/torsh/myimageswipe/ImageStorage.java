package com.example.torsh.myimageswipe;

/**
 * Created by torsh on 5/8/15.
 * class for manage image files in the sd-card storage
 */
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ImageStorage{

    private File file_sdCard_dir;
    private String sdCard_pictures_path;
    public List<String> imgList; // list of all images retrieved by JSONReader
    public static Map<String, String> sdCardImgFileMap; // images existing on the sdCard by JSONReader
    public ArrayList<String> imagesToDownload; // only image names


    // makes the directory if sdCard available and directory not exists
    public void checkDirsOnExternalStorage() {

        sdCard_pictures_path = Environment.getExternalStorageDirectory() + "/Pictures/myImageSwipeApp/";
        //Log.d("sdCardPicPath: ", sdCard_pictures_path);
        file_sdCard_dir = new File(sdCard_pictures_path);

        String externalStorageState = Environment.getExternalStorageState();
        //Log.d("storageState: ", externalStorageState); // returns 'mounted'

        if (externalStorageState.equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            if (!file_sdCard_dir.exists()) {
                file_sdCard_dir.mkdirs();
                Log.d("Created dirs: ", Environment.DIRECTORY_PICTURES);
            }
        }
    }

    // check Image exists in the sdCard pictures path
    public static boolean imageExists(String imageName){

        Log.d("imageExists: ", "called");

        // make sure that the directory is ready
        //new ImageStorage().checkDirsOnExternalStorage();

        // define the file path
        String sdCard_pictures_path = Environment.getExternalStorageDirectory() + "/Pictures/myImageSwipeApp/" + imageName;
        File file_onPath = new File(sdCard_pictures_path);

        //Log.d("sdCard_pictures_path: ", sdCard_pictures_path);

        // check if file exist on the path
        if ( file_onPath.exists()) {
            Log.d("Image File: ", imageName + " exists");
            return true;

        } else {
            Log.d("Image File: ", imageName + " exists Not");
            return false;
        }
    }

    public boolean writeToSdCard(Bitmap bitmap, String fileName){

        //checkDirsOnExternalStorage();

        Log.d("Saving... ", fileName);
        File file_toSave = new File(Environment.getExternalStorageDirectory() + "/Pictures/myImageSwipeApp", fileName);
        if (file_toSave.exists()) {
            Log.d("file exists: ", fileName);
            return false;
        }

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file_toSave);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("Saved: ", fileName);
        return true;
    }


    public static Bitmap getImage(String imageName){

        Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/Pictures/myImageSwipeApp/" + imageName);

        File file_image;
        try {
            File file_dir = new File(Environment.getExternalStorageDirectory().toString());
            if ( !file_dir.exists() ) {
                return null;
            }

            file_image = new File( file_dir.getPath() + "/Pictures/myImageSwipeApp" + imageName );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }
}
