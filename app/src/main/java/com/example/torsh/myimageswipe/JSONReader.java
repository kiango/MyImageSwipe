package com.example.torsh.myimageswipe;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;
import android.view.View;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by torsh on 5/8/15.
 * Class to read JSON webservice and download images from the server directory
 */


public class JSONReader extends AsyncTask<String, Void, List<String>> {

    Activity activity;
    public ArrayList<String> imgToBeDownloaded;
    ProgressDialog progress;


    public JSONReader(Activity activity){
        super();
        this.activity = activity;

        progress = new ProgressDialog(activity);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setTitle("Network & IO operations...");
        progress.show();
    }

    InputStream inputStream = null;
    JsonReader jsonReader = null;

    protected List<String> doInBackground(String... urls) {

        try {
            URL url_webservice = new URL(urls[0]);
            URLConnection connection = url_webservice.openConnection();

            inputStream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            // resultList is a list of image names to be kept
            List<String> resultList = new ArrayList<>();
            StringBuilder stringBuilder;

            jsonReader = new JsonReader(reader);
            jsonReader.beginArray();
            while ( jsonReader.peek() != JsonToken.END_ARRAY ){
                stringBuilder = new StringBuilder();
                stringBuilder.append(jsonReader.nextString());
                resultList.add(stringBuilder.toString());
            }
            jsonReader.endArray();

            /*for ( String imgNamePath : resultList ){
                Log.d("resultList", imgNamePath);
            }*/

            if (resultList == null)
                Log.d("resultList","--- resultList is null !! ---");

            // check the app directory and its images in the sdCard
            ImageStorage imageStorage = new ImageStorage();
            imageStorage.imgList = resultList;
            imageStorage.checkDirsOnExternalStorage();

            // trim image names,
            // map the images that are stored (true) and not stored (false) in the sdCard
            Map<String, String> sdCardImgMap = new HashMap<>();
            for (String temp: resultList){
                //Log.d("testing resultList", temp);
                if ( ImageStorage.imageExists(temp.substring(4)) )
                    sdCardImgMap.put(temp.substring(4), "true");
                if ( ! ImageStorage.imageExists(temp.substring(4)) )
                    sdCardImgMap.put(temp.substring(4), "false");
            }
            //Log.d("sdCardImgMap", sdCardImgMap.toString());
            // map of all the image-files of the sdCard
            imageStorage.sdCardImgFileMap = sdCardImgMap;

            // pick the images that are not stored in the sdCard
            String value = "false";
            imgToBeDownloaded = new ArrayList<>();
            for(Map.Entry<String, String> imgEntry : sdCardImgMap.entrySet()) {
                if (value.equals(imgEntry.getValue())) {
                    imgToBeDownloaded.add(imgEntry.getKey());
                }
                //Log.d("imgToBeDownloaded", imgEntry.getKey() + " :: " + imgEntry.getValue());
            }
            //Log.d("imgToBeDownloaded", imgToBeDownloaded.toString());


            // downloading for each of the image names in the imgToBeDownloaded list
            String imagePath = resultList.get(0).substring(0, 4);
            int imgToBeDownloaded_size = imgToBeDownloaded.size();
            //Log.d("imgToBeDownloaded_size",String.valueOf(imgToBeDownloaded_size));
            if ( imgToBeDownloaded_size >= 1 ){

                for ( int i=0; i<imgToBeDownloaded_size; i++){
                    try {
                        URL url_images = new URL(urls[0] + imagePath +imgToBeDownloaded.get(i));
                        URLConnection urlConnection  = url_images.openConnection();
                        Bitmap bitmap = BitmapFactory.decodeStream(urlConnection.getInputStream());
                        Log.d("download", "downloaded");
                        if ( bitmap != null ) {
                            imageStorage.writeToSdCard(bitmap, imgToBeDownloaded.get(i));
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d("download","failed download");
                        //return null;
                    }
                }
            } else {
                Log.d("no download", "--- Images already exist in the sd-card ---");
            }


        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            try {
                if (inputStream != null)
                    inputStream.close();

                if (jsonReader != null)
                    jsonReader.close();

            } catch (IOException e){
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);

    }

    @Override
    protected void onPostExecute(List<String> strings) {
        super.onPostExecute(strings);
        if (progress.isShowing())
            progress.dismiss();

        //imgForDownload = imgToBeDownloaded.toArray( new String[imgToBeDownloaded.size()] );
    }
}
