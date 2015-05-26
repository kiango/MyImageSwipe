package com.example.torsh.myimageswipe;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.File;
import java.util.ArrayList;


public class MainActivity extends FragmentActivity implements UpdatePositionListener{

    ViewPager viewPager; // responsible for get the right fragment through the adaptor

    public FragmentManager fragmentManager;
    public static ArrayList<String> imageFileNames;
    public static ArrayList<String> imageFilePath;
    public String pathToImages;
    public UpdatePositionListener positionListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        positionListener = this;

        // getting info about images from the JSON-Webservice and download to sdCard
        new JSONReader(MainActivity.this).execute("http://www.itu.dk/people/jacok/MMAD/services/images/");
        //Log.d("jsonReader", jsonReader.getStatus().toString());

        // locate the view pager
        viewPager = (ViewPager) findViewById(R.id.pager);
        // view pager uses view -adaptor to move between different items
        fragmentManager = getSupportFragmentManager();
        // pass the file paths and file names to the adapter and set it to fragment
        // we pass fragmentManager since MyAdapter takes fragmentManager in the constructors
        viewPager.setAdapter(new MyAdapter(fragmentManager));
        //viewPager.setOffscreenPageLimit(5); // max limit for keeping active pages
        //currentItemNo = viewPager.getCurrentItem();

        // get images from sdCard for swipe display
        //Log.d("sdCardImgFileMap", ImageStorage.sdCardImgFileMap.toString());
        //getImagesFromSdCard();
        //Log.d("test", getImagesFromSdCard().toString());

    }

    public ArrayList<String> getImagesFromSdCard(){
        pathToImages = Environment.getExternalStorageDirectory() + "/Pictures/myImageSwipeApp/";
        File file = new File(pathToImages);

        imageFileNames = new ArrayList<>();
        for (File f : file.listFiles()){
            imageFileNames.add(f.getName());
        }
        //Log.d("imageNameFile", imageFileNames.toString());
        return imageFileNames;
    }

    public void showDialogFragment(View v){
        FragmentManager fragmentManager = getSupportFragmentManager();
        ImageGridDialogFragment imageGridDialogFragment = new ImageGridDialogFragment();
        imageGridDialogFragment.show(fragmentManager, "imageGridDialogFragment");
    }

    public void setViewPagerImg(int positionImg){
        viewPager.setCurrentItem(positionImg);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // implementing interface for getting clicked image position from Dialog Fragment
    @Override
    public void onNewPosition(int pos) {
        viewPager.setCurrentItem(pos);
        //Log.d("position", String.valueOf(pos));
    }


    // FragmentStatePagerAdapter saves data in the fragment and destroys the other fragments
    // adapter contains file path and file names in the fields ??
    public class MyAdapter extends FragmentStatePagerAdapter {

        public String imageName;
        public ArrayList<String> imageFileNames;
        public int imageNoSdCard;

        MyAdapter(FragmentManager fm) {
            super(fm);

            // get images from sdCard //
            String pathToImages = Environment.getExternalStorageDirectory() + "/Pictures/myImageSwipeApp/";
            File file = new File(pathToImages);
            // makes list of image names
            ArrayList<String> imageFileNames = new ArrayList<>();
            for (File f : file.listFiles()){
                imageFileNames.add(f.getName());
            }
            this.imageFileNames = imageFileNames;
            imageNoSdCard = imageFileNames.size();
            // makes list of full image path
            imageFilePath = new ArrayList<>();
            for (File f : file.listFiles()){
                imageFilePath.add(file.getAbsolutePath()+ "/" +f.getName());
            }
        }

        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }


        // responsible for getting the fragment at the given position
        // view pager calls and asks for a fragment of position 'i'
        @Override
        public Fragment getItem(int i) {

            // full image path converted to array
            //imageFilePathArr = imageFilePath.toArray( new String[imageFilePath.size()]);

            //Log.d("getItem", "getItem called i: " + i);

            imageName = "/" + imageFileNames.get(i);

            Fragment fragment = new DemoFragment();
            Bundle bundle = new Bundle();
            bundle.putInt(DemoFragment.PICTURE_ID, i);
            fragment.setArguments(bundle);

            return fragment;
        }

        @Override
        public int getCount() {
            // returns the number of swipe pages
            // Log.d("getCount", "getCount called");
            //return 6;
            return imageFileNames.size();
        }

        // method for getting each page titles for each fragment
        @Override
        public CharSequence getPageTitle(int position) {

            //notifyDataSetChanged();

            for (int j = 0; j <= imageNoSdCard; j++) {
                if (position == j)
                    return imageName;
            }
            return null;
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
        }
    }


    // Fragment template class
    public static class DemoFragment extends Fragment{

        public static final String PICTURE_ID = "pic_id";
        public ImageView imageView;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.frag_a, container, false);

            // getting data from MyAdapter in Bundle and passing it to setImageResource
            Bundle args = this.getArguments();
            Bitmap bitmap = BitmapFactory.decodeFile(imageFilePath.get(args.getInt(PICTURE_ID)));
            //((ImageView) rootView.findViewById(R.id.imageView_a)).setImageBitmap(bitmap); // works
            imageView = (ImageView) rootView.findViewById(R.id.imageView_a);
            imageView.setImageBitmap(bitmap);
            imageView.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(rootView.getContext(), "clicked", Toast.LENGTH_SHORT).show();
                    Activity activity = getActivity();
                    ((MainActivity) activity).showDialogFragment(rootView);
                }
            });

            return rootView;
        }
    }
}

