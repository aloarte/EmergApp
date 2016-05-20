package com.uc3m.p4r4d0x.emergapp;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.graphics.Bitmap;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.uc3m.p4r4d0x.emergapp.servicios.GPSService;
import com.uc3m.p4r4d0x.emergapp.servicios.MailSenderService;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;

import static java.lang.Long.*;

public class EmergencyActivity extends AppCompatActivity {

    //ImageViews for buttons
    ImageView ivTakePhoto, ivTakeVideo, ivGallery;
    //Arrays with VideoViews and ImageViews for videos and pictures
    VideoView[] videoViewsVideos = new VideoView [4];
    ImageView[] imageViewsPictures = new ImageView [4];
    VideoView[] videoViewsVideosSelected = new VideoView [4];
    ImageView[] imageViewsPicturesSelected = new ImageView [4];
    ImageView[] imageViewsDeleteSelected = new ImageView[8];

    //Arrays with info if the pictures are selected and obtained
    boolean[] obtainedImages = new boolean[4];
    boolean[] obtainedVideos = new boolean[4];

    String [] toSendPicturesPath= new String[]{"","","",""};
    String [] toSendVideosPath= new String[]{"","","",""};
    String toSendGPSCoord="";
    String toSendGPSStreet="";
    String toSendMessage;

    final String MyPREFERENCES="userPreferences";
    SharedPreferences sharedpreferences;




    //Text views to displayu messages
    TextView tViewGPS,tViewGPSCoord, tvMessagePopUp1;
    //Bit maps to print an image
    Bitmap [] bitMapPictures= new Bitmap[4];
    Uri [] uriVideos= new Uri [4];
    //Define constants to identify intents
    final static int C_PHOTO = 1;
    final static int C_VIDEO = 2;
    final static int C_GALLERY_IMAGE = 11;
    final static int C_GALLERY_VIDEO = 12;
    //Define constants to identify messages
    final int C_YES_YES = 1, C_YES_NO = 2, C_NO_YES = 3, C_NO_NO = 4;
    //Elements to display a googlemap view
    GoogleMap googleMap;
    MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        loadToolbar();


        //Get All the image vies
        //ImageViews for taking photos or images from gallery
        ivTakePhoto = (ImageView) findViewById(R.id.ivCapturePhoto);
        ivTakeVideo = (ImageView) findViewById(R.id.ivCaptureVideo);
        ivGallery = (ImageView) findViewById(R.id.ivSelPictureGallery);
        //VideoViews for video previews images
        videoViewsVideos[0] = (VideoView) findViewById(R.id.ivVideo1);
        videoViewsVideos[1] = (VideoView) findViewById(R.id.ivVideo2);
        videoViewsVideos[2] = (VideoView) findViewById(R.id.ivVideo3);
        videoViewsVideos[3] = (VideoView) findViewById(R.id.ivVideo4);
        //ImageViews for images
        imageViewsPictures[0] = (ImageView) findViewById(R.id.ivPicture1);
        imageViewsPictures[1] = (ImageView) findViewById(R.id.ivPicture2);
        imageViewsPictures[2] = (ImageView) findViewById(R.id.ivPicture3);
        imageViewsPictures[3] = (ImageView) findViewById(R.id.ivPicture4);



        //Make all dissapear innitially
        videoViewsVideos[0].setVisibility(View.GONE);
        videoViewsVideos[1].setVisibility(View.GONE);
        videoViewsVideos[2].setVisibility(View.GONE);
        videoViewsVideos[3].setVisibility(View.GONE);
        imageViewsPictures[0].setVisibility(View.GONE);
        imageViewsPictures[1].setVisibility(View.GONE);
        imageViewsPictures[2].setVisibility(View.GONE);
        imageViewsPictures[3].setVisibility(View.GONE);







        //Get the text view
        tvMessagePopUp1 = (TextView) findViewById(R.id.tvInfoMessage);

        //initialize map view
        mapView = (MapView) findViewById(R.id.google_MAPVIEW);
        mapView.onCreate(savedInstanceState);
        googleMap = mapView.getMap();
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        googleMap.setMyLocationEnabled(true);
        mapView.setVisibility(View.INVISIBLE);
        /*
        LatLng sydney = new LatLng(-33.867, 151.206);
        Log.d("ALR", "8");


        Log.d("ALR", "9");
        googleMap.setMyLocationEnabled(true);
        Log.d("ALR", "10");
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 13));
        Log.d("ALR", "11");
        googleMap.addMarker(new MarkerOptions()
                .title("Sydney")
                .snippet("The most populous city in Australia.")
                .position(sydney));
        Log.d("ALR", "12");*/

        //Get the first image
        putFirstImages();
        //Get the first videos
        putFirstVideos();

        //Get the GPS position
        getGPSposition();
        //Load the emergency message
        loadMessage();
        //Get the default message with the answer in the previous boxes
        tvMessagePopUp1.getText();
        //Set an onclick to rewrite info in the message
        ImageView ivChangeMessage = (ImageView) findViewById(R.id.ivChangeMessage);
        ivChangeMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(EmergencyActivity.this);
                View layView = (LayoutInflater.from(EmergencyActivity.this)).inflate(R.layout.user_input, null);
                alertBuilder.setView(layView);
                final EditText userInput = (EditText) layView.findViewById(R.id.tvContentMessage);
                userInput.setText(tvMessagePopUp1.getText());

                alertBuilder.setCancelable(true)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                tvMessagePopUp1.setText(userInput.getText());
                            }
                        });
                Dialog dialog = alertBuilder.create();
                dialog.show();
            }
        });

        ImageView ivSendInfo = (ImageView) findViewById(R.id.ivSendMessage);
        ivSendInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(EmergencyActivity.this);
                View layView = (LayoutInflater.from(EmergencyActivity.this)).inflate(R.layout.sel_images, null);
                alertBuilder.setView(layView);

                //FOR THE SELECTION MENU
                //VideoViews for video previews images

                videoViewsVideosSelected[0] = (VideoView) layView.findViewById(R.id.ivSelectedVid1);
                videoViewsVideosSelected[1] = (VideoView) layView.findViewById(R.id.ivSelectedVid2);
                videoViewsVideosSelected[2] = (VideoView) layView.findViewById(R.id.ivSelectedVid3);
                videoViewsVideosSelected[3] = (VideoView) layView.findViewById(R.id.ivSelectedVid4);
                //ImageViews for images
                imageViewsPicturesSelected[0] = (ImageView) layView.findViewById(R.id.ivSelectedPic1);
                imageViewsPicturesSelected[1] = (ImageView) layView.findViewById(R.id.ivSelectedPic2);
                imageViewsPicturesSelected[2] = (ImageView) layView.findViewById(R.id.ivSelectedPic3);
                imageViewsPicturesSelected[3] = (ImageView) layView.findViewById(R.id.ivSelectedPic4);

                //Make all dissapear innitially
                videoViewsVideosSelected[0].setVisibility(View.GONE);
                videoViewsVideosSelected[1].setVisibility(View.GONE);
                videoViewsVideosSelected[2].setVisibility(View.GONE);
                videoViewsVideosSelected[3].setVisibility(View.GONE);
                imageViewsPicturesSelected[0].setVisibility(View.GONE);
                imageViewsPicturesSelected[1].setVisibility(View.GONE);
                imageViewsPicturesSelected[2].setVisibility(View.GONE);
                imageViewsPicturesSelected[3].setVisibility(View.GONE);


                //For the popup screen
                imageViewsDeleteSelected[0] = (ImageView) layView.findViewById(R.id.ivDeletePic1);
                imageViewsDeleteSelected[1] = (ImageView) layView.findViewById(R.id.ivDeletePic2);
                imageViewsDeleteSelected[2] = (ImageView) layView.findViewById(R.id.ivDeletePic3);
                imageViewsDeleteSelected[3] = (ImageView) layView.findViewById(R.id.ivDeletePic4);
                imageViewsDeleteSelected[4] = (ImageView) layView.findViewById(R.id.ivDeleteVid1);
                imageViewsDeleteSelected[5] = (ImageView) layView.findViewById(R.id.ivDeleteVid2);
                imageViewsDeleteSelected[6] = (ImageView) layView.findViewById(R.id.ivDeleteVid3);
                imageViewsDeleteSelected[7] = (ImageView) layView.findViewById(R.id.ivDeleteVid4);
                imageViewsDeleteSelected[0].setVisibility(View.GONE);
                imageViewsDeleteSelected[1].setVisibility(View.GONE);
                imageViewsDeleteSelected[2].setVisibility(View.GONE);
                imageViewsDeleteSelected[3].setVisibility(View.GONE);
                imageViewsDeleteSelected[4].setVisibility(View.GONE);
                imageViewsDeleteSelected[5].setVisibility(View.GONE);
                imageViewsDeleteSelected[6].setVisibility(View.GONE);
                imageViewsDeleteSelected[7].setVisibility(View.GONE);
                //end popup screen


                for(int i=0;i<4;i++) {
                    if (obtainedImages[i]){
                        imageViewsPicturesSelected[i].setVisibility(View.VISIBLE);
                        imageViewsDeleteSelected[i].setVisibility(View.VISIBLE);
                        imageViewsPicturesSelected[i].setImageBitmap(Bitmap.createScaledBitmap(bitMapPictures[i], 120, 120, false));
                    }
                    if(obtainedVideos[i]){

                        videoViewsVideosSelected[i].setVisibility(View.VISIBLE);
                        imageViewsDeleteSelected[i+4].setVisibility(View.VISIBLE);
                        videoViewsVideosSelected[i].setVideoURI(uriVideos[i]);
                        videoViewsVideosSelected[i].setMediaController(new MediaController(EmergencyActivity.this));
                        videoViewsVideosSelected[i].requestFocus();
                    }

                }
                Dialog dialog = alertBuilder.create();
                dialog.show();
            }
        });




    }

    /*
     *Aplication life cicle methods overrided to include mapView life cicle
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }
    //---------------------INNER METHODS---------------

    /*
 * Desc: This method retrieve the info from the popups activities and writes in
 *       the textview the appropiate message
 * */
    public void loadMessage() {

        //Retrieve the information from the previos activity
        Bundle extras = getIntent().getExtras();
        //Check if there is any problem
        if (extras != null) {
            //Get the integer value
            int valueFromPopups = extras.getInt("popUp2");
            //Switch all possible messages to display
            switch (valueFromPopups) {
                case C_YES_YES:
                    tvMessagePopUp1.setText("There are human damages and I need help");
                    break;
                case C_YES_NO:
                    tvMessagePopUp1.setText("There are human damages and I dont need help");
                    break;
                case C_NO_YES:
                    tvMessagePopUp1.setText("There are no human damages and I need help");
                    break;
                case C_NO_NO:
                    tvMessagePopUp1.setText("There are no human damages and I dont need help");
                    break;
                //default case: if fails anything
                default:
                    tvMessagePopUp1.setText("FAIL");
                    break;
            }
        }
    }

    /*
    * Desc: Calls GPS Service and prints in the TextView the result
    * */
    public void getGPSposition() {
        //Get the TextView to show the address value
        tViewGPS = (TextView) findViewById(R.id.tvGPS);
        tViewGPSCoord= (TextView) findViewById(R.id.tvGPSCoord);

        //create service passing the TextView as a param
        GPSService sGPS = new GPSService(getApplicationContext(), this.tViewGPS, this.tViewGPSCoord);

        //Try to get the location from GPS or network
        if (sGPS.getLocation()) {
            //If was successful call startFetchAddressService, who will obtain the address bassed on the location obtained
            sGPS.startFetchAddressService();

        } else {
            //If the location couldnt get obtained
            tViewGPS.setText(R.string.address_not_obtained);
        }

    }

    /*
    * Desc: Select all camera images from the phone and take the 2 first photos.
    *       Put photos on their correspondant image views
    * */
    public void putFirstImages() {
        //Gets the columns from the Images table to make a query media to our ContentResolver
        String[] projection = new String[]{
                MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Images.ImageColumns.DATE_TAKEN,
                MediaStore.Images.ImageColumns.MINI_THUMB_MAGIC
        };
        /*Get content of all photos in the phone in the table MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        * getting 'projection' columns
        * WHERE BUCKET_DISPLAY_NAME = "Camera" (all photos taken in the album camera)
        * ORDER_BY date descent order.
        */
        //query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
        final Cursor cursor = getContentResolver()
                .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        projection,
                        MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME + " LIKE ? ", new String[]{"Camera"},
                        MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC");

       /* final Cursor cursor = getContentResolver()
                .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        projection,
                        MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME + " LIKE ? AND "+MediaStore.Images.ImageColumns.DATE_TAKEN+" < ?", new String[]{"Camera"},
                        MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC");
        */
        // Put the 2 photos in the image view
        //We iterate to get the first, second and third element from our cursor with all the images from camera ordered by date
        //cursor.moveToFirst();
        for (int i = 0; i < 2; i++) {
            //in each iteration we get the next element on cursor
            if (i == 0) {
                //In the first iteration we take the moveToFirst element
                if (cursor != null) {
                    cursor.moveToFirst();
                }
            }
            //In next iterations take the moveToNext element
            else {
                cursor.moveToNext();
            }
            //Check if picture is valid ( was taken recently )
            if (isMediaRecent(cursor.getString(3))) {
                //Picture is valid


                //Switch which ImageView have to fill
                switch (i) {
                    case 0:
                        //Get the ImageView
                        imageViewsPictures[0].setVisibility(View.VISIBLE);
                        //Get the image location from the cursor element
                        toSendPicturesPath[0] = cursor.getString(1);
                        //Build File with the location
                        File imageFile1 = new File(toSendPicturesPath[0]);
                        if (imageFile1.exists()) {
                            //Build a bit map and set this bit map into the image view
                            bitMapPictures[0] = BitmapFactory.decodeFile(toSendPicturesPath[0]);
                            imageViewsPictures[0].setImageBitmap(Bitmap.createScaledBitmap(bitMapPictures[0], 120, 120, false));
                            //Set if the image in the position 1 is obtained
                            obtainedImages[0]=true;
                        }
                        break;
                    case 1:
                        //Get the ImageView visible
                        imageViewsPictures[1].setVisibility(View.VISIBLE);

                        //Get the image location from the cursor element
                        toSendPicturesPath[1] = cursor.getString(1);
                        //Build File with the location
                        File imageFile2 = new File(toSendPicturesPath[1]);
                        if (imageFile2.exists()) {
                            //Build a bit map and set this bit map into the image view
                            bitMapPictures[1] = BitmapFactory.decodeFile(toSendPicturesPath[1]);
                            imageViewsPictures[1].setImageBitmap(Bitmap.createScaledBitmap(bitMapPictures[1], 120, 120, false));

                            //Set if the image in the position 2 is obtained
                            obtainedImages[1] = true;
                        }
                        break;
                    default:
                        break;
                }

            } else {
                //Picture is not valid

            }
        }
    }

    /*
    * Desc: Select all videos from the phone and take the 2 first videos.
    *       Put videos on their correspondant video views
    * */
    public void putFirstVideos() {
        //Gets the columns from the Video table to make a query media to our ContentResolver
        String[] projection = new String[]{
                MediaStore.Video.VideoColumns._ID,
                MediaStore.Video.VideoColumns.DATA,
                MediaStore.Video.VideoColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Video.VideoColumns.DATE_TAKEN,
                MediaStore.Video.VideoColumns.MINI_THUMB_MAGIC
        };
        /*Get content of all photos in the phone in the table MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        * getting 'projection' columns
        * WHERE BUCKET_DISPLAY_NAME = "Camera" (all videos taken in the album camera)
        * ORDER_BY date descent order.
        */
        //query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
        final Cursor cursor = getContentResolver()
                .query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        projection,
                        MediaStore.Video.VideoColumns.BUCKET_DISPLAY_NAME + " LIKE ? ", new String[]{"Camera"},
                        MediaStore.Video.VideoColumns.DATE_TAKEN + " DESC");

        // Put the 2 videos in the video views
        //iterate to get the first and second video from the cursor with all the videos from camera phone ordered by date
        for (int i = 0; i < 2; i++) {
            //in each iteration we get the next element on cursor
            if (i == 0) {
                //In the first iteration we take the moveToFirst element
                if (cursor != null) {
                    cursor.moveToFirst();
                }
            }
            //In next iterations take the moveToNext element
            else {
                cursor.moveToNext();
            }
            //Check if video is valid ( was taken recently )
            if (isMediaRecent(cursor.getString(3))) {
                //Video is valid
                //Switch which VideoView have to fill
                switch (i) {
                    case 0:
                        //Put the video view visibile
                        videoViewsVideos[0].setVisibility(View.VISIBLE);
                        //Get the uri of the video
                        toSendVideosPath[0]=cursor.getString(1);
                        uriVideos[0] = Uri.parse(toSendVideosPath[0]);
                        //Put the video in the VideoView
                        obtainedVideos[0]=true;
                        videoViewsVideos[0].setVideoURI(uriVideos[0]);
                        videoViewsVideos[0].setMediaController(new MediaController(this));
                        videoViewsVideos[0].requestFocus();
                        break;
                    case 1:
                        //Get the ImageView
                        videoViewsVideos[1].setVisibility(View.VISIBLE);

                        //Put the video in the VideoView
                        toSendVideosPath[1]=cursor.getString(1);
                        uriVideos[1] = Uri.parse(cursor.getString(1));
                        obtainedVideos[1]=true;
                        videoViewsVideos[1].setVideoURI(uriVideos[1]);
                        videoViewsVideos[1].setMediaController(new MediaController(this));
                        videoViewsVideos[1].requestFocus();
                        break;
                    default:
                        break;
                }

            } else {
                //video is not valid

            }
        }
    }

    /*
    * Desc: Compare the current date with the date from the media and and check if
    *       the content is older than 30'
    * Param: a string mediaDate, wich contains the date obtained from the query
    * Ret: true if the media is not older than 30 or false if it does
    *
    * */

    public boolean isMediaRecent(String mediaDate) {

        boolean isValid = false;

        //Create and instanciate a Calendar object
        Calendar pictureCal = Calendar.getInstance(); // Picture Calendar
        pictureCal.setTimeInMillis(parseLong(mediaDate)); //Create by parsing picture date
        //Get the month and add a 0 if necessary
        String pictureStrMonth = "" + pictureCal.get(Calendar.MONTH);
        if (pictureStrMonth.length() == 1) pictureStrMonth = "0" + pictureStrMonth;
        //Get the day and add a 0 if necessary
        String pictureStrDay = "" + pictureCal.get(Calendar.DAY_OF_MONTH);
        if (pictureStrDay.length() == 1) pictureStrDay = "0" + pictureStrDay;
        //Build a string with the date values (YYYYMMDD)
        String pictureStrDate = "" + (pictureCal.get(Calendar.YEAR) - 1900) +
                "" + pictureStrMonth +
                "" + pictureStrDay;


        //Create and instanciate a Calendar object
        Calendar currentCal = Calendar.getInstance(); //Current Calendar
        //Get the month and add a 0 if necessary
        String currentStrMonth = "" + currentCal.get(Calendar.MONTH);
        if (currentStrMonth.length() == 1) currentStrMonth = "0" + currentStrMonth;
        //Get the day and add a 0 if necessary
        String currentStrDay = "" + currentCal.get(Calendar.DAY_OF_MONTH);
        if (currentStrDay.length() == 1) currentStrDay = "0" + currentStrDay;
        //Build a string with the date values (YYYYMMDD)
        String currentStrDate = "" + (currentCal.get(Calendar.YEAR) - 1900) +
                "" + currentStrMonth +
                "" + currentStrDay;


        //Get hours and minutes in int values
        int pictureMin = pictureCal.get(Calendar.MINUTE);
        int pictureHour = pictureCal.get(Calendar.HOUR_OF_DAY);
        int currentMin = currentCal.get(Calendar.MINUTE);
        int currentHour = currentCal.get(Calendar.HOUR_OF_DAY);
        //Express time in a integer (HHMM)
        int pictureTime = pictureHour * 100 + pictureMin;
        int currentTime = currentHour * 100 + currentMin;

        //Check if the dates matches in the same day
        if (parseLong(pictureStrDate) != parseLong(currentStrDate)) {
            //Dates didnt match
            isValid = false;
        } else {
            //dates match in the same day
            //Check if the time is lesser than 30 minutes
            if (((currentTime - pictureTime) >= 0) && ((currentTime - pictureTime) <= 30)) {
                //Time is lower than half hour
                isValid = true;
            } else {
                //Time is greater than half hour
                isValid = false;
            }

        }

        return isValid;
    }

    /*
    * Desc: load the user content into the toolbar
    *
    * */
    public void loadToolbar(){
        //Get sharedpreferences item and the username asociated
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String username = sharedpreferences.getString("username", "default");

        //Check the username
        if(username.compareTo("default")==0){
            //If is empty (error) do nothing
        }
        else{
            //Put username in the toolbar text view
            TextView tvToolbarUser=(TextView) findViewById(R.id.tvToolbarUser);
            tvToolbarUser.setText(username);

        }

        DBManager managerDB = new DBManager(this);

        //Select the user
        Cursor resultQuery= managerDB.selectUser(username);
        //If the user exists
        if(resultQuery.moveToFirst()==true){
            //Get the password by searching first the column index
            int level = resultQuery.getInt(resultQuery.getColumnIndex(DBManager.FN_LEVEL));
            int points = resultQuery.getInt(resultQuery.getColumnIndex(DBManager.FN_POINTS));
            TextView tvToolbarLevelNumber=(TextView) findViewById(R.id.tvToolbarLevelNumber);
            tvToolbarLevelNumber.setText(""+level);
            TextView tvToolbarPointsNumber=(TextView) findViewById(R.id.tvToolbarPointsNumber);
            tvToolbarPointsNumber.setText(""+points);

        }
    }

    /*
    * Desc: Upgrade into the database the points and level for the logged user
    * */
    public void changeUserStats(){
        String username = sharedpreferences.getString("username", "default");

        long devuelto1=0,devuelto2;
        DBManager managerDB = new DBManager(this);

        //Select the user
        Cursor resultQuery= managerDB.selectUser(username);

        //If the user exists
        if(resultQuery.moveToFirst()==true) {
            //Get the password by searching first the column index

            devuelto1 = managerDB.upgrade(
                    resultQuery.getString(resultQuery.getColumnIndex(DBManager.FN_NAME)),
                    resultQuery.getString(resultQuery.getColumnIndex(DBManager.FN_PASSWORD)),
                    resultQuery.getString(resultQuery.getColumnIndex(DBManager.FN_EMAIL)),
                    resultQuery.getString(resultQuery.getColumnIndex(DBManager.FN_DATE)),
                    resultQuery.getInt(resultQuery.getColumnIndex(DBManager.FN_LEVEL)) + 1,
                    resultQuery.getInt(resultQuery.getColumnIndex(DBManager.FN_POINTS)));
        }

        resultQuery= managerDB.selectUser(username);

        //If the user exists
        if(resultQuery.moveToFirst()==true) {
            devuelto2 = managerDB.upgrade(
                    resultQuery.getString(resultQuery.getColumnIndex(DBManager.FN_NAME)),
                    resultQuery.getString(resultQuery.getColumnIndex(DBManager.FN_PASSWORD)),
                    resultQuery.getString(resultQuery.getColumnIndex(DBManager.FN_EMAIL)),
                    resultQuery.getString(resultQuery.getColumnIndex(DBManager.FN_DATE)),
                    resultQuery.getInt(resultQuery.getColumnIndex(DBManager.FN_LEVEL)),
                    resultQuery.getInt(resultQuery.getColumnIndex(DBManager.FN_POINTS)) + 8);
        }


        loadToolbar();
    }


    //---------------------OVERRIDED METHODS---------------

    /*
    * desc: Method overrided from the phone activity. Is executed when any photo is taken or when
    *       a image is selected from the gallery.
    * par: requestCode: used to determinate which activity calls this method
    *      resultCode: used to tell if the action was ok
    *      data: data from the activity
    * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Check if everything procesed successfully
        if (resultCode == Activity.RESULT_OK) {
            //onActivityResult for taking a photo
            if (requestCode == C_PHOTO) {
                //Obtains the image. Parse with a bundle and a bitmap
                Bundle bundl = data.getExtras();
                bitMapPictures[2] = (Bitmap) bundl.get("data");
                //Set the new image (bitmapped) to the imageView
                imageViewsPictures[2].setVisibility(View.VISIBLE);
                imageViewsPictures[2].setImageBitmap(Bitmap.createScaledBitmap(bitMapPictures[2], 120, 120, false));
                //imageViewsPictures[2].setImageBitmap(bitMapPictures[2]);

                //Set if the image in the position 3 is obtained
                obtainedImages[2]=true;
            }
            //onActivityResult for picking and image from gallery
            else if (requestCode == C_GALLERY_IMAGE) {
                //Find the path of the selected image.
                Uri photoLocation = data.getData();

                //Open this a stream of data/bytes
                try {
                    InputStream openInputStream = getContentResolver().openInputStream(photoLocation);
                    //Take a stream of data and convert in to a bitmap
                    bitMapPictures[3] = BitmapFactory.decodeStream(openInputStream);

                    //Assign this image to our image view
                    imageViewsPictures[3].setVisibility(View.VISIBLE);
                    //imageViewsPictures[3].setImageBitmap(bitMapPictures[3]);
                    imageViewsPictures[3].setImageBitmap(Bitmap.createScaledBitmap(bitMapPictures[3], 120, 120, false));
                    //Set if the image in the position 4 is obtained
                    obtainedImages[3]=true;
                }
                //Catch an exception if the file doesnt exist
                catch (FileNotFoundException e) {
                    e.printStackTrace();
                    //alert the user that something went wrong
                    Toast.makeText(this, getString(R.string.FileNotFound), Toast.LENGTH_LONG).show();

                }
            } else if (requestCode == C_VIDEO) {
                uriVideos[2] = data.getData();
                videoViewsVideos[2].setVisibility(View.VISIBLE);
                videoViewsVideos[2].setVideoURI(uriVideos[2]);
                videoViewsVideos[2].setMediaController(new MediaController(this));
                videoViewsVideos[2].requestFocus();
                obtainedVideos[2]=true;



            } else if (requestCode == C_GALLERY_VIDEO) {
                //Find the path of the selected image.
                uriVideos[3] = data.getData();
                videoViewsVideos[3].setVisibility(View.VISIBLE);
                videoViewsVideos[3].setVideoURI(uriVideos[3]);
                videoViewsVideos[3].setMediaController(new MediaController(this));
                videoViewsVideos[3].requestFocus();
                obtainedVideos[3]=true;

            }
        }

    }


    //---------------------ON CLICK BUTTON METHODS---------------

    /*
    * Desc: Invoked when bSelectImageGallery is pressed
    *       This method gets an image from the phone gallery
    * */
    public void onClickGalleryImages(View v) {
        //Define a intent for pick an image from gallery
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);

        //Specify where to find the image using data
        //Give the path (file system) where all images are stored
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath();

        //Convert the String path to an URI
        Uri picturesDirectory = Uri.parse(path);

        //Set the data and type on this intent: tell it where to look for find images and what file types we want
        photoPickerIntent.setDataAndType(picturesDirectory, "image/*");

        //start the activity (C_GALLERY is the number that identifies this intent
        startActivityForResult(photoPickerIntent, C_GALLERY_IMAGE);
    }

    public void onClickGalleryVideos(View v) {
        //Define a intent for pick an image from gallery
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);

        //Specify where to find the image using data
        //Give the path (file system) where all images are stored
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).getPath();

        //Convert the String path to an URI
        Uri picturesDirectory = Uri.parse(path);

        //Set the data and type on this intent: tell it where to look for find images and what file types we want
        photoPickerIntent.setDataAndType(picturesDirectory, "video/*"); //aqui a lo mejor  se puede especificar los ultimos videos

        //start the activity (C_GALLERY is the number that identifies this intent
        startActivityForResult(photoPickerIntent, C_GALLERY_VIDEO);
    }

    /*
    * Desc: Invoked when bCapturePhoto is pressed
    *       This method gets an image by making a photo
    * */
    public void onClickTakePhoto(View v) {
        //Create the intent to open the camera capture
        Intent intentCamera = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        //Start the activity (C_PHOTO is the number that identifies this intent)
        startActivityForResult(intentCamera, C_PHOTO);
    }

    /*
    * Desc: Invoked when bCaptureVideo is pressed
    *       This method gets a video by making a video from the phone
    * */
    public void onClickTakeVideo(View v) {
        //Create the intent to open the camera capture
        Intent intentVideo = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        //Start the activity (C_VIDEO is the number that identifies this intent)
        startActivityForResult(intentVideo, C_VIDEO);
    }


    /*
    * Desc: on click function to set the map view visible
    * */
    public void onClickChangeLocation(View v) {
        mapView.setVisibility(View.VISIBLE);
    }

    /*
    * Desc: on click function to logout from the aplication
    * */
    public void onClickLogout(View v){

        //Remove from the shared preferences the username
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.remove("username");
        editor.commit();

        //Create and launch login activity
        Intent myIntent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(myIntent);
    }

    /*
    * Desc: on click function to send the data to the web service, and load the gamify system
    * */
    public void onClickSendInfo(View v){
        //Add points and 1 level to the user
        changeUserStats();





        /*
        * Send info to the web service // check changing to another activity        * */

        /*
        * write that any user have send succesfouly the information
        * */


    }

    /*
    * Desc: on click function to remove an item right before sending all the info
    * */
    public void onClickDeleteSelectedPicture(View v) {
        //Get the tag from the imageview
        int tag = Integer.parseInt((String) v.getTag());
        int index= tag-1;
        //If the index is from 0 to 3, its a tag for pictures
        if(index >= 0 && index<4){
            //If this image was already selected
            if(obtainedImages[index]){
                //Make invisible the selection
                imageViewsDeleteSelected[index].setVisibility(View.GONE);
                imageViewsPicturesSelected[index].setVisibility(View.GONE);

                //TODO: drop image before sending it
            }
        }
        //If the index is from 4 to 7, its a tag for videos
        else if( index >= 4 && index <8){
            index-=4;
            //If this image was already selected
            if(obtainedVideos[index]){
                //Make invisible the selection
                imageViewsDeleteSelected[index+4].setVisibility(View.GONE);
                videoViewsVideosSelected[index].setVisibility(View.GONE);

                //TODO: drop image before sending it
            }
        }


    }


    public void onClickSendByMail(View v){
        //Get the string values for the message, the gps addres and the gps longitude&latitude
        toSendMessage=(String)tvMessagePopUp1.getText();
        toSendGPSStreet=(String) tViewGPS.getText();
        toSendGPSCoord=(String) tViewGPSCoord.getText();
        Log.d("ALR","imprimo array");
        Log.d("ALR","0 "+toSendPicturesPath[0]);
        Log.d("ALR","1 "+toSendPicturesPath[1]);
        Log.d("ALR","2 "+toSendPicturesPath[2]);
        Log.d("ALR","3 "+toSendPicturesPath[3]);
        //Iniciate the mail sender service
        MailSenderService sMSS = new MailSenderService();

        //Send the message with all the info (message, all the pictures, all the videos, the gps latitude&longitude and the address)
        sMSS.sendMessage(toSendMessage,toSendPicturesPath,toSendVideosPath,toSendGPSCoord,toSendGPSStreet);

        /*try {
            Log.d("SendMail", "antesdenviar");
            GMailSender sender = new GMailSender("ereporteruc3m@gmail.com", "ereporterwuc3m");
            sender.sendMail("This is Subject",
                    "This is Body",
                    "aloarter@gmail.com",
                    "albrathojaverde@gmail.com");
            Log.d("SendMail", "despuesdedenviar");
        } catch (Exception e) {
            Log.d("SendMail", "falloemail");
            Log.e("SendMail", e.getMessage(), e);
        }*/

    }
}
