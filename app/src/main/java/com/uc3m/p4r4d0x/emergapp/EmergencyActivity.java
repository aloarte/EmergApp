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
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.graphics.Bitmap;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.uc3m.p4r4d0x.emergapp.helpers.Constants;
import com.uc3m.p4r4d0x.emergapp.helpers.database.DBUserManager;
import com.uc3m.p4r4d0x.emergapp.receivers.ResultReceiverGPSCoord;
import com.uc3m.p4r4d0x.emergapp.receivers.ResultReceiverSentReady;
import com.uc3m.p4r4d0x.emergapp.servicios.FetchAddressService;
import com.uc3m.p4r4d0x.emergapp.servicios.MailSenderService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static java.lang.Long.*;

public class EmergencyActivity extends AppCompatActivity implements OnMapReadyCallback,GoogleMap.OnMarkerDragListener {

    //Define constants to identify intents
    final static int C_PHOTO         = 1;
    final static int C_VIDEO         = 2;
    final static int C_GALLERY_IMAGE = 11;
    final static int C_GALLERY_VIDEO = 12;

    //Define constants to identify which previous message has been chosen
    final int C_YES_YES = 1
            , C_YES_NO  = 2
            , C_NO_YES  = 3
            , C_NO_NO   = 4;

    /*
    * Variables to hold and control all the logic related with the images and videos
    * from taking them from phone to sending through a message
    * */
    //------Views---------
    //Arrays with VideoViews and ImageViews for videos and pictures
    VideoView[] videoViewsVideos           = new VideoView[4];
    ImageView[] imageViewsPictures         = new ImageView[4];

    //Arrays with VideoViews and ImageViews for the selected pictures (pop up screen before-sending)
    VideoView[] videoViewsVideosSelected   = new VideoView[4];
    ImageView[] imageViewsPicturesSelected = new ImageView[4];

    //Arrays with ImageViews for the X icons to delete selected images
    ImageView[] imageViewsDeleteSelected   = new ImageView[8];
    ImageView[] imageViewDeletePicture     = new ImageView[4];
    ImageView[] imageViewDeleteVideo     = new ImageView[4];

    //-----Control arrays -----
    //Arrays with info if the pictures are selected and obtained
    boolean[] obtainedImages               = new boolean[4];
    boolean[] obtainedVideos               = new boolean[4];
    //Arrays with info if the pictures is selected to delete in sending
    boolean[] deletedImages                = new boolean[4];
    boolean[] deletedVideos                = new boolean[4];

    //-----Auxiliar arrays -----
    //Bit map array used for print images
    Bitmap[] bitMapPictures                = new Bitmap[4];
    //Uri array used to display videos
    Uri[] uriVideos                        = new Uri[4];

    //----Auxiliar strings used to send the info
    //Strings used to stock all the info that will be sended
    String[] toSendPicturesPath            = new String[]{"", "", "", ""};
    String[] toSendVideosPath              = new String[]{"", "", "", ""};
    String toSendGPSCoord                  = "";
    String toSendGPSStreet                 = "";
    String toSendMessage                   = "";


    /*
    * Variables related with the elements used on the screen like buttons
    * */
    //Text views to get the GPS data and display the emergency message
    TextView tViewGPS, tViewGPSCoord, tvMessagePopUp1;

    //ImageViews for buttons
    ImageView ivTakePhoto, ivTakeVideo, ivGallery;

    //For the screen movements when the info is sended
    RelativeLayout rlSendMessage, rlReloadScreen;
    LinearLayout llAfterSendingMessage;
    ImageView ivLoadingRotate;

    //ResultReceiver for sending when the message is sended
    protected ResultReceiverSentReady mReceiverReady;
    //ResultReceiver for renew the gps position
    protected ResultReceiverGPSCoord mReceiverGPS;


    /*Google maps variables*/
    //Frame layout to show and hide map
    FrameLayout flMap;
    //map fragment containing the google map
    MapFragment mapFragment;
    GoogleMap googleMapCP;

    //Info to use shared preferences to have a session
    final String MyPREFERENCES = "userPreferences";
    SharedPreferences sharedpreferences;


    //-------------------------------------------------------//
    //---------------------OVERRIDED METHODS-----------------//
    //-------------------------------------------------------//

    /*
   * Desc: method overrided from AppCompatActivity
   *       this method is called when activity starts
   *       Initialize all the neccessary parts of the main screen
   * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Load the toolbar
        loadToolbar();
        //Load the views
        loadViews();

        //Get the first images
        putFirstImages();
        //Get the first videos
        putFirstVideos();
        //Get the GPS position
        getGPSposition();
        //Load the emergency message
        loadMessage();
        //Load the color
        loadColor();

        //ON CLICK LISTENER for the alert dialog screen to modify the message
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


        //ON CLICK LISTENER for the alert dialog screen for sending the message
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
                //Set a button to send the message and close the popup
                alertBuilder.setCancelable(true)
                        .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Send by mail
                                sendInfoByMail();
                            }
                        });
                Dialog dialog = alertBuilder.create();
                dialog.show();
            }
        });




    }

    /*
    * Desc: method overrided from AppCompatActivity
    *       this method is called when activity starts
    *       Prepare the toolbar menu
    * */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_emergency_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /*
    * Desc: method overrided from AppCompatActivity
    *       this method is called when activity starts
    *       Prepare the elements on the toolbar menu
    * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent myIntent;

        switch (item.getItemId()) {
            case R.id.action_close_session:
                Toast.makeText(this, getText(R.string.action_close_session), Toast.LENGTH_SHORT).show();
                performLogout();
                return true;
            case R.id.action_acount_configuration:
                myIntent= new Intent(getApplicationContext(), AccountConfigurationActivity.class);
                startActivity(myIntent);
                return true;
            case R.id.action_profile:
                myIntent= new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(myIntent);
                return true;
            case R.id.action_ranking:
                myIntent= new Intent(getApplicationContext(), RankingActivity.class);
                startActivity(myIntent);
                return true;
            case R.id.action_achievements:
                myIntent= new Intent(getApplicationContext(), AchievementsActivity.class);
                startActivity(myIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    /*
    * Desc: method overrided from OnMapReadyCallback
    *       this method is called when the map is ready
    *       Prepare the map configuration and set a marker where the current location is
    * */
    @Override
    public void onMapReady(GoogleMap map) {

        googleMapCP=map;
        float lat,longit;
        String[] parts;

        //Get the gps position into strings
        getGPSposition();

        //Set the map type
        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        //Get the string
        toSendGPSCoord= tViewGPSCoord.getText().toString();

        //Check if is empty (not obtained)
        if(toSendGPSCoord.compareTo("") != 0){

            //Split the string into 2 parts, separated by the comma
            parts = toSendGPSCoord.split(",");

            //Parse into float both strings
            lat=Float.parseFloat(parts[0]);
            longit=Float.parseFloat(parts[1]);

            //Create a LatLng object with the GPS position
            LatLng currentLatLng = new LatLng(lat, longit);

            //Check permissions
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }

            map.setOnMarkerDragListener(this);
            //Set my location enabled
            map.setMyLocationEnabled(true);

            //Load a marker with the position
            map.addMarker(new MarkerOptions()
                    .title("Current Location")
                    .snippet("Location obtained by GPS")
                    .position(currentLatLng)
                    .draggable(true));


            map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 13));




        }



    }

    /*
    * Desc: method overrided from GoogleMap.OnMarkerDragListener
    *       this method get when the marker starts of being dragged
    * */
    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    /*
    * Desc: method overrided from GoogleMap.OnMarkerDragListener
    *       this method get when the marker is being dragged
    * */
    @Override
    public void onMarkerDrag(Marker marker) {
    }

    /*
    * Desc: method overrided from GoogleMap.OnMarkerDragListener
    *       this method get when the marker end of being dragged
    *       When the drag ends, this method get the marker and call
     *      fetchAddressService to set a new location.
    * */
    @Override
    public void onMarkerDragEnd(Marker marker) {

        //Create a blank new Location
        Location markerLocation=new Location("");

        //Set into this location the latitude and longitude of the marker
        markerLocation.setLatitude(marker.getPosition().latitude);
        markerLocation.setLongitude(marker.getPosition().longitude);

        toSendGPSCoord=marker.getPosition().latitude+","+marker.getPosition().longitude;

        //Start the fetchAddressService to get the address into the TextViews.
        startFetchAddressService(tViewGPS, tViewGPSCoord, markerLocation);

        //Toast that a new location is selected
        Toast.makeText(getApplicationContext(), "Selected a new position", Toast.LENGTH_LONG).show();
    }


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

                bitMapPictures[2] = BitmapFactory.decodeFile(toSendPicturesPath[2]);

                //Set the new image (bitmapped) to the imageView
                imageViewsPictures[2].setVisibility(View.VISIBLE);
                imageViewDeletePicture[2].setVisibility(View.VISIBLE);
                imageViewsPictures[2].setImageBitmap(Bitmap.createScaledBitmap(bitMapPictures[2], 120, 120, false));
                imageViewsPictures[2].setImageBitmap(bitMapPictures[2]);

                //Set if the image in the position 3 is obtained
                obtainedImages[2]=true;
            }
            //onActivityResult for taking a video
            else if (requestCode == C_VIDEO) {
                uriVideos[2] = data.getData();
                videoViewsVideos[2].setVisibility(View.VISIBLE);
                imageViewDeleteVideo[2].setVisibility(View.VISIBLE);
                videoViewsVideos[2].setVideoURI(uriVideos[2]);
                videoViewsVideos[2].setMediaController(new MediaController(this));
                videoViewsVideos[2].requestFocus();
                obtainedVideos[2] = true;
            }
            //onActivityResult for taking a video from gallery
            else if (requestCode == C_GALLERY_VIDEO) {

                //Get the uri
                Uri videoLocation = data.getData();
                //Get the pattern to make a query for retrieving an image
                String[] filePathColumn = {MediaStore.Video.Media.DATA};
                //Make the query based on the previous URI obtained by the previous intent
                Cursor cursor = getContentResolver().query(
                        videoLocation, filePathColumn, null, null, null);
                //get the element
                cursor.moveToFirst();

                //Get the index where the path is
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                //Obtain the path using the cursor
                String filePath = cursor.getString(columnIndex);
                //close cursor
                cursor.close();

                //Save path into string array to send the video
                toSendVideosPath[3]=filePath;

                //Put the video on the screen
                //Find the path of the selected image.
                uriVideos[3] = data.getData();
                //Set visibility on
                videoViewsVideos[3].setVisibility(View.VISIBLE);
                imageViewDeleteVideo[3].setVisibility(View.VISIBLE);
                //set the uri
                videoViewsVideos[3].setVideoURI(uriVideos[3]);
                //set the video
                videoViewsVideos[3].setMediaController(new MediaController(this));
                videoViewsVideos[3].requestFocus();
                obtainedVideos[3]=true;

            }
            //onActivityResult for picking and image from gallery
            else if (requestCode == C_GALLERY_IMAGE) {
                //Find the path of the selected image.
                //Get the uri
                Uri photoLocation = data.getData();
                //Get the pattern to make a query for retrieving an image
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                //Make the query based on the previous URI obtained by the previous intent
                Cursor cursor = getContentResolver().query(
                        photoLocation, filePathColumn, null, null, null);
                //get the element
                cursor.moveToFirst();

                //Get the index where the path is
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                //Obtain the path using the cursor
                String filePath = cursor.getString(columnIndex);
                //close cursor
                cursor.close();

                //Save path into string array to send the picture
                toSendPicturesPath[3]=filePath;

                //Put the image on the screen
                //Open this a stream of data/bytes
                try {
                    InputStream openInputStream = getContentResolver().openInputStream(photoLocation);
                    //Take a stream of data and convert in to a bitmap
                    bitMapPictures[3] = BitmapFactory.decodeStream(openInputStream);

                    //Assign this image to our image view
                    imageViewsPictures[3].setVisibility(View.VISIBLE);
                    imageViewDeletePicture[3].setVisibility(View.VISIBLE);
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
            }

        }
    }




    //---------------------------------------------------//
    //---------------------INNER METHODS-----------------//
    //---------------------------------------------------//
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

                default:
                    tvMessagePopUp1.setText("Write an emergency message here");
                    break;
            }
        }
    }

    /*
     * Desc: This method retrieve the GPS position from the previous intent (extras)
     * and set into the text views and strings
     * */
    public void getGPSposition() {

        //Get the text views
        tViewGPS      = (TextView) findViewById(R.id.tvGPS);
        tViewGPSCoord = (TextView) findViewById(R.id.tvGPSCoord);

        //Retrieve the information from the previous activity
        Bundle extras = getIntent().getExtras();

        //Check if there is any problem
        if (extras != null) {
            //Get the string values
            String valueGPSAddress = extras.getString("GPSA");
            String valueGPSCoord = extras.getString("GPSC");

            //Put into text views
            tViewGPS.setText(valueGPSAddress);
            tViewGPSCoord.setText(valueGPSCoord);

        }
    }


    /*
    * Desc: Select all camera images from the phone and take the 2 first photos.
    *       Put photos on their correspondant image views
    * */
    public void putFirstImages() {

        File imageFile1,imageFile2;

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

        // Put the 2 photos in the image view
        //Iterate to get the first, second and third element from our cursor with all the images from camera ordered by date
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
            //Check if cursor is not empty
            if(cursor!=null && cursor.getCount()>0){
                //Check if picture is valid ( was taken recently )
                if (isMediaRecent(cursor.getString(3))) {

                    //Switch which ImageView have to fill
                    switch (i) {
                        case 0:
                            //Get the ImageView
                            imageViewsPictures[0].setVisibility(View.VISIBLE);
                            imageViewDeletePicture[0].setVisibility(View.VISIBLE);
                            //Get the image location from the cursor element
                            toSendPicturesPath[0] = cursor.getString(1);
                            //Build File with the location
                            imageFile1 = new File(toSendPicturesPath[0]);
                            if (imageFile1.exists()) {
                                //Build a bit map and set this bit map into the image view
                                bitMapPictures[0] = BitmapFactory.decodeFile(toSendPicturesPath[0]);
                                imageViewsPictures[0].setImageBitmap(Bitmap.createScaledBitmap(bitMapPictures[0], 120, 120, false));
                                //Set if the image in the position 1 is obtained
                                obtainedImages[0] = true;
                            }
                            break;
                        case 1:
                            //Get the ImageView visible
                            imageViewsPictures[1].setVisibility(View.VISIBLE);
                            imageViewDeletePicture[1].setVisibility(View.VISIBLE);
                            //Get the image location from the cursor element
                            toSendPicturesPath[1] = cursor.getString(1);
                            //Build File with the location
                            imageFile2 = new File(toSendPicturesPath[1]);
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

                }
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
            //Check if cursor is not empty
            if(cursor!=null && cursor.getCount()>0) {
                //Check if video is valid ( was taken recently )
                if (isMediaRecent(cursor.getString(3))) {
                    //Video is valid
                    //Switch which VideoView have to fill
                    switch (i) {
                        case 0:
                            //set the video view visibile
                            videoViewsVideos[0].setVisibility(View.VISIBLE);
                            imageViewDeleteVideo[0].setVisibility(View.VISIBLE);
                            //Get video's path
                            toSendVideosPath[0] = cursor.getString(1);
                            //Get the uri of the video
                            uriVideos[0] = Uri.parse(toSendVideosPath[0]);
                            obtainedVideos[0] = true;
                            //Put the video in the VideoView
                            videoViewsVideos[0].setVideoURI(uriVideos[0]);
                            videoViewsVideos[0].setMediaController(new MediaController(this));
                            videoViewsVideos[0].requestFocus();
                            break;
                        case 1:
                            //set the video view visibile
                            videoViewsVideos[1].setVisibility(View.VISIBLE);
                            imageViewDeleteVideo[1].setVisibility(View.VISIBLE);
                            //Get video's path
                            toSendVideosPath[1] = cursor.getString(1);
                            //Get the uri of the video
                            uriVideos[1] = Uri.parse(cursor.getString(1));
                            obtainedVideos[1] = true;
                            //Put the video in the VideoView
                            videoViewsVideos[1].setVideoURI(uriVideos[1]);
                            videoViewsVideos[1].setMediaController(new MediaController(this));
                            videoViewsVideos[1].requestFocus();
                            break;
                        default:
                            break;
                    }
                }
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

        boolean isValid         = false;

        //Create and instanciate a Calendar object
        Calendar pictureCal     = Calendar.getInstance(); // Picture Calendar
        pictureCal.setTimeInMillis(parseLong(mediaDate)); //Create by parsing picture date
        //Get the month and add a 0 if necessary
        String pictureStrMonth  = "" + pictureCal.get(Calendar.MONTH);
        if (pictureStrMonth.length() == 1) pictureStrMonth = "0" + pictureStrMonth;
        //Get the day and add a 0 if necessary
        String pictureStrDay = "" + pictureCal.get(Calendar.DAY_OF_MONTH);
        if (pictureStrDay.length() == 1) pictureStrDay = "0" + pictureStrDay;
        //Build a string with the date values (YYYYMMDD)
        String pictureStrDate   = "" + (pictureCal.get(Calendar.YEAR) - 1900) +
                "" + pictureStrMonth +
                "" + pictureStrDay;


        //Create and instanciate a Calendar object
        Calendar currentCal     = Calendar.getInstance(); //Current Calendar
        //Get the month and add a 0 if necessary
        String currentStrMonth  = "" + currentCal.get(Calendar.MONTH);
        if (currentStrMonth.length() == 1) currentStrMonth = "0" + currentStrMonth;
        //Get the day and add a 0 if necessary
        String currentStrDay    = "" + currentCal.get(Calendar.DAY_OF_MONTH);
        if (currentStrDay.length() == 1) currentStrDay = "0" + currentStrDay;
        //Build a string with the date values (YYYYMMDD)
        String currentStrDate   = "" + (currentCal.get(Calendar.YEAR) - 1900) +
                "" + currentStrMonth +
                "" + currentStrDay;


        //Get hours and minutes in int values
        int pictureMin          = pictureCal.get(Calendar.MINUTE);
        int pictureHour         = pictureCal.get(Calendar.HOUR_OF_DAY);
        int currentMin          = currentCal.get(Calendar.MINUTE);
        int currentHour         = currentCal.get(Calendar.HOUR_OF_DAY);
        //Express time in a integer (HHMM)
        int pictureTime         = pictureHour * 100 + pictureMin;
        int currentTime         = currentHour * 100 + currentMin;

        //Check if the dates matches in the same day
        if (parseLong(pictureStrDate) != parseLong(currentStrDate)) {
            //Dates didnt match
            isValid = false;
        } else {
            //dates match in the same day
            //Check if the time is lesser than 30 minutes
            if (((currentTime - pictureTime) >= 0) && ((currentTime - pictureTime) <= 30)) {
                //Time is lower than half hour
                isValid         = true;
            } else {
                //Time is greater than half hour
                isValid         = false;
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
        sharedpreferences                  = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String username                    = sharedpreferences.getString("username", "default");

        //Check the username
        if(username.compareTo("default")==0){
            //If is empty (error) do nothing
        }
        else{
            //Put username in the toolbar text view
            TextView tvToolbarUser         = (TextView) findViewById(R.id.tvToolbarUser);
            tvToolbarUser.setText(username);

        }
        DBUserManager managerDB                = new DBUserManager(this);
        //Select the user
        Cursor resultQuery                 = managerDB.selectUser(username);
        //If the user exists
        if(resultQuery.moveToFirst()==true){
            //Get the password by searching first the column index
            String level                      = resultQuery.getString(resultQuery.getColumnIndex(DBUserManager.TU_LEVEL));
            int APpoints                     = resultQuery.getInt(resultQuery.getColumnIndex(DBUserManager.TU_AP_POINTS));
            int XPpoints                     = resultQuery.getInt(resultQuery.getColumnIndex(DBUserManager.TU_XP_POINTS));

            TextView tvToolbarLevel = (TextView) findViewById(R.id.tvToolbarLevel);
            tvToolbarLevel.setText(level);

            TextView tvToolbarAP = (TextView) findViewById(R.id.tvToolbarCurrentAP);
            tvToolbarAP.setText(""+APpoints);

            TextView tvToolbarXPMax = (TextView) findViewById(R.id.tvToolBarNextLevelXP);
            TextView tvToolbarXP = (TextView) findViewById(R.id.tvToolbarCurrentXP);

            switch(level){
                case "Traveler":
                    tvToolbarXPMax.setText(""+50);
                    tvToolbarXP.setText(""+XPpoints);
                    break;
                case "Veteran":
                    tvToolbarXPMax.setText(""+100);
                    tvToolbarXP.setText(""+XPpoints);
                    break;
                case "Champion":
                    tvToolbarXPMax.setText(""+300);
                    tvToolbarXP.setText(""+XPpoints);
                    break;
                case "Hero":
                    tvToolbarXPMax.setText(""+500);
                    tvToolbarXP.setText(""+XPpoints);
                    break;
                case "Legend":
                    tvToolbarXPMax.setText(""+999);
                    tvToolbarXP.setText(""+XPpoints);
                    break;
                default:
                    break;

            }



        }
    }

    /*
    * Desc: load all the views used in this activity
    *
    * */
    public void loadViews(){
        //Get All the image views
        //ImageViews for taking photos or images from gallery
        ivTakePhoto           = (ImageView) findViewById(R.id.ivCapturePhoto);
        ivTakeVideo           = (ImageView) findViewById(R.id.ivCaptureVideo);
        ivGallery             = (ImageView) findViewById(R.id.ivSelPictureGallery);
        //VideoViews for video previews images
        videoViewsVideos[0]   = (VideoView) findViewById(R.id.ivVideo1);
        videoViewsVideos[1]   = (VideoView) findViewById(R.id.ivVideo2);
        videoViewsVideos[2]   = (VideoView) findViewById(R.id.ivVideo3);
        videoViewsVideos[3]   = (VideoView) findViewById(R.id.ivVideo4);
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

        //For the popup screen
        imageViewDeletePicture[0] = (ImageView) findViewById(R.id.ivDeleteMainPicture1);
        imageViewDeletePicture[1] = (ImageView) findViewById(R.id.ivDeleteMainPicture2);
        imageViewDeletePicture[2] = (ImageView) findViewById(R.id.ivDeleteMainPicture3);
        imageViewDeletePicture[3] = (ImageView) findViewById(R.id.ivDeleteMainPicture4);
        imageViewDeleteVideo[0]   = (ImageView) findViewById(R.id.ivDeleteMainVideo1);
        imageViewDeleteVideo[1]   = (ImageView) findViewById(R.id.ivDeleteMainVideo2);
        imageViewDeleteVideo[2]   = (ImageView) findViewById(R.id.ivDeleteMainVideo3);
        imageViewDeleteVideo[3]   = (ImageView) findViewById(R.id.ivDeleteMainVideo4);

        imageViewDeletePicture[0].setVisibility(View.GONE);
        imageViewDeletePicture[1].setVisibility(View.GONE);
        imageViewDeletePicture[2].setVisibility(View.GONE);
        imageViewDeletePicture[3].setVisibility(View.GONE);
        imageViewDeleteVideo[0].setVisibility(View.GONE);
        imageViewDeleteVideo[1].setVisibility(View.GONE);
        imageViewDeleteVideo[2].setVisibility(View.GONE);
        imageViewDeleteVideo[3].setVisibility(View.GONE);

        //Get the text view for the emerg message
        tvMessagePopUp1       = (TextView) findViewById(R.id.tvInfoMessage);

        //Get the image view for the animated loading image
        ivLoadingRotate       = (ImageView) findViewById(R.id.ivLoading);

        //Get the FrameLayout and RelativeLayout to hide map layer and send layer
        flMap                 = (FrameLayout) findViewById(R.id.mapLL);
        rlSendMessage         = (RelativeLayout) findViewById(R.id.sendMessageRL);
        rlReloadScreen        = (RelativeLayout) findViewById(R.id.reloadScreenRL);
        llAfterSendingMessage = (LinearLayout) findViewById(R.id.afterSendingLL);

        //Get the map
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.google_MAPVIEW);
        mapFragment.getMapAsync(this);
    }

    /*
    * Desc: Upgrade into the database the points and level for the logged user
    *
    public void changeUserStats(){
        String username = sharedpreferences.getString("username", "default");

        long devuelto1=0,devuelto2;
        DBUserManager managerDB = new DBUserManager(this);

        //Select the user
        Cursor resultQuery= managerDB.selectUser(username);

        //If the user exists
        if(resultQuery.moveToFirst()==true) {
            //Get the password by searching first the column index

            devuelto1 = managerDB.upgrade(
                    resultQuery.getString(resultQuery.getColumnIndex(DBUserManager.FN_NAME)),
                    resultQuery.getString(resultQuery.getColumnIndex(DBUserManager.FN_PASSWORD)),
                    resultQuery.getString(resultQuery.getColumnIndex(DBUserManager.FN_EMAIL)),
                    resultQuery.getString(resultQuery.getColumnIndex(DBUserManager.FN_DATE)),
                    resultQuery.getInt(resultQuery.getColumnIndex(DBUserManager.FN_LEVEL)) + 1,
                    resultQuery.getInt(resultQuery.getColumnIndex(DBUserManager.FN_POINTS)));
        }

        resultQuery= managerDB.selectUser(username);

        //If the user exists
        if(resultQuery.moveToFirst()==true) {
            devuelto2 = managerDB.upgrade(
                    resultQuery.getString(resultQuery.getColumnIndex(DBUserManager.FN_NAME)),
                    resultQuery.getString(resultQuery.getColumnIndex(DBUserManager.FN_PASSWORD)),
                    resultQuery.getString(resultQuery.getColumnIndex(DBUserManager.FN_EMAIL)),
                    resultQuery.getString(resultQuery.getColumnIndex(DBUserManager.FN_DATE)),
                    resultQuery.getInt(resultQuery.getColumnIndex(DBUserManager.FN_LEVEL)),
                    resultQuery.getInt(resultQuery.getColumnIndex(DBUserManager.FN_POINTS)) + 8);
        }


        loadToolbar();
    }

*/


    /*
    * Desc:  function to, after the confirmation, send the email with the info
    * */
    public void sendInfoByMail(){
        //Get the string values for the message, the gps addres and the gps longitude&latitude
        toSendMessage= tvMessagePopUp1.getText().toString();
        toSendGPSStreet= tViewGPS.getText().toString();
        toSendGPSCoord= tViewGPSCoord.getText().toString();

        //Auxiliar string array for not sending selected videos
        String [] toSendVideosPathAux= new String []{"","","",""};
        //Check if any element of the array is selected to delete (wont be sended)
        for(int i=0;i<4;i++){
            if(deletedVideos[i]){
                //If is deleted, do nothing
            }
            else
            {
                //If is not deleted, copy the path value
                toSendVideosPathAux[i]= toSendVideosPath[i];
            }
        }

        //Auxiliar string array for not sending selected images
        String [] toSendPicturesPathAux= new String []{"","","",""};
        //Check if any element of the array is selected to delete (wont be sended)
        for(int i=0;i<4;i++){
            if(deletedImages[i]){
                //If is deleted, do nothing
            }
            else
            {
                //If is not deleted, copy the path value
                toSendPicturesPathAux[i]= toSendPicturesPath[i];
            }
        }

        mReceiverReady = new ResultReceiverSentReady(new android.os.Handler(),llAfterSendingMessage,rlSendMessage,rlReloadScreen,ivLoadingRotate,getApplicationContext(),R.animator.girar);

        //Iniciate the mail sender service
        MailSenderService sMSS = new MailSenderService(getApplicationContext(),mReceiverReady);

        //Send the message with all the info (message, all the pictures, all the videos, the gps latitude&longitude and the address)
        sMSS.sendMessage(toSendMessage,toSendPicturesPathAux,toSendVideosPathAux,toSendGPSCoord,toSendGPSStreet);

        //Re initializate deletedArrays
        deletedImages=new boolean[4];
        deletedVideos=new boolean[4];

        llAfterSendingMessage.setVisibility(View.VISIBLE);

    }

    /*
    * Desc: Start FetchAddress service, passing a ResultReceiverGPSCoord object to
    *       get the result value and the Location obtained by the change on the map marker
    * */
    public void startFetchAddressService(TextView paramViewAddress, TextView paramViewCoord, Location locationG) {

        //Iniciate ResultReceiverGPSCoord object
        mReceiverGPS = new ResultReceiverGPSCoord(new android.os.Handler(), paramViewAddress,paramViewCoord);

        //Create the intent to start the FetchAddressService
        Intent intent = new Intent(getApplicationContext(), FetchAddressService.class);
        //Add the params for the service
        intent.putExtra(Constants.RECEIVER, mReceiverGPS);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, locationG);

        //Start service
        startService(intent);
    }

    /*
        * Desc: on click function to logout from the aplication
        * */
    public void performLogout(){

        //Remove from the shared preferences the username
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.remove("username");
        editor.remove("colorprimary");
        editor.remove("colorsecondary");
        editor.commit();

        //Create and launch login activity
        Intent myIntent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(myIntent);
    }

    /*
    * Desc: load the color on the toolbar and other elements
    * */
    public void loadColor(){

        //Check if there is any user logged into the aplication checking shared preferences
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String primaryColor = sharedpreferences.getString("colorprimary", "default");
        String secondaryColor = sharedpreferences.getString("colorsecondary", "default");
        //if there is no color
        if(primaryColor.compareTo("default")==0 || secondaryColor.compareTo("default")==0){
            //Load default color
        }
        else{

            //Load the new color
            Toolbar t= (Toolbar) findViewById(R.id.toolbarE);
            t.setBackgroundColor(Color.parseColor(primaryColor));

        }
    }

    //-----------------------------------------------------------//
    //---------------------ON CLICK BUTTON METHODS---------------//
    //-----------------------------------------------------------//
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

        //Get the timestamp that will be used to name the file
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        //Get the complete path for the folder MyImages where the photo will be saved
        File imagesFolder = new File(Environment.getExternalStorageDirectory(), "MyImages");
        //Create directory if doesnt exist
        imagesFolder.mkdirs();

        //Get the path for the photo, constructed with the timestamp
        File image = new File(imagesFolder, "QR_" + timeStamp + ".png");

        //Save the path in the string array for sending the video
        toSendPicturesPath[2]=image.getPath();

        //get the uri
        Uri uriSavedImage = Uri.fromFile(image);

        //Put extra on intent and start the activity (C_PHOTO is the number that identifies this intent)
        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
        startActivityForResult(intentCamera, C_PHOTO);

    }

    /*
    * Desc: Invoked when bCaptureVideo is pressed
    *       This method gets a video by making a video from the phone
    * */
    public void onClickTakeVideo(View v) {

        //Create the intent to open the camera capture
        Intent intentVideo = new Intent(android.provider.MediaStore.ACTION_VIDEO_CAPTURE);

        //Get the timestamp that will be used to name the file
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        //Get the complete path for the folder MyVideos where the video will be saved
        File videosFolder = new File(Environment.getExternalStorageDirectory(), "MyVideos");
        //Create directory if doesnt exist
        videosFolder.mkdirs();

        //Get the path for the video, constructed with the timestamp
        File video = new File(videosFolder, "QR_" + timeStamp + ".3gp");

        //Save the path in the string array for sending the video
        toSendVideosPath[2]=video.getPath();

        //Get the uri
        Uri uriSavedVideo = Uri.fromFile(video);

        //Put extra on intent and start the activity (C_VIDEO is the number that identifies this intent)
        intentVideo.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedVideo);
        startActivityForResult(intentVideo, C_VIDEO);
    }


    /*
    * Desc: on click function to set the map view visible
    * */
    public void onClickChangeLocation(View v) {
        flMap.setVisibility(View.VISIBLE);
    }

    /*
   * Desc: on click function to set the map view invisible
   * */
    public void onClickCloseMap(View v) {
        flMap.setVisibility(View.INVISIBLE);
    }

    /*
    * Desc: on click function to send the data to the web service, and load the gamify system
    * */
    public void onClickSendInfo(View v){
        //Add points and 1 level to the user
       // changeUserStats();
        /*
        * Send info to the web service // check changing to another activity        * */

        /*
        * write that any user have send succesfouly the information
        * */


    }

    /*
    * Desc: on click function to remove an item right before sending all the info
    * */
    public void onClickDeleteSelectedItem(View v) {
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
                deletedImages[index]=true;
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
                deletedVideos[index]=true;
            }
        }


    }

    /*
    * Desc: on click function to remove a picture
    * */
    public void onClickDeletePicture(View v) {
        //Get the tag from the imageview
        int tag = Integer.parseInt((String) v.getTag());
        int index= tag-1;

        //If this image was already obtained
        if(obtainedImages[index]){
            //Make invisible the selection
            imageViewsPictures[index].setVisibility(View.GONE);
            imageViewDeletePicture[index].setVisibility(View.GONE);
            obtainedImages[index]=false;
        }
    }

    /*
    * Desc: on click function to remove a video
    * */
    public void onClickDeleteVideo(View v) {
        //Get the tag from the imageview
        int tag = Integer.parseInt((String) v.getTag());
        int index= tag-1;

        //If this image was already obtained
        if(obtainedVideos[index]){
            //Make invisible the selection
            videoViewsVideos[index].setVisibility(View.GONE);
            imageViewDeleteVideo[index].setVisibility(View.GONE);
            obtainedVideos[index]=false;
        }
    }

    /*
   * Desc: on click function to reload a new report after sending one
   * */
    public void onClickReloadInitialScreen(View v){
        //Create a new intent and save the info on it
        Intent i = new Intent(getApplicationContext(), HomeScreenActivity.class);
        //Launch next activity
        startActivity(i);

    }

    public void onClickCenterAtMarker(View v){

        float lat,longit;
        String[] parts;

        //Split the string into 2 parts, separated by the comma
        parts = toSendGPSCoord.split(",");

        //Parse into float both strings
        lat=Float.parseFloat(parts[0]);
        longit=Float.parseFloat(parts[1]);

        //Create a LatLng object with the GPS position
        LatLng currentLatLng = new LatLng(lat, longit);
        googleMapCP.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 16));
    }
}
