package com.uc3m.p4r4d0x.emergapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.graphics.Bitmap;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.uc3m.p4r4d0x.emergapp.servicios.GPSService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Time;
import java.util.Calendar;
import java.util.Date;

import static java.lang.Long.*;

public class EmergencyActivity extends AppCompatActivity {

    ImageView ivTakePhoto, ivTakeVideo, ivGallery;
    ImageView ivPicture1,ivPicture2,ivPicture3,ivPicture4;
    VideoView ivVideo1,ivVideo2,ivVideo3,ivVideo4;


    Bitmap bitMapPhoto, bitMapVideo, bitMapGallery;
    TextView tViewGPS, tvMessagePopUp1;
    boolean gP1 = false, gP2 = false;
    private Rect rect;

    //Define constants to identify intents
    final static int C_PHOTO = 1;
    final static int C_VIDEO = 2;
    final static int C_GALLERY_IMAGE = 11;
    final static int C_GALLERY_VIDEO = 12;
    //Define constants to identify messages
    final int C_YES_YES = 1, C_YES_NO = 2, C_NO_YES = 3, C_NO_NO = 4;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Get All the image vies
        //ImageViews for taking photos or images from gallery
        ivTakePhoto = (ImageView) findViewById(R.id.ivCapturePhoto);
        ivTakeVideo = (ImageView) findViewById(R.id.ivCaptureVideo);
        ivGallery = (ImageView) findViewById(R.id.ivSelPictureGallery);
        //ImageViews for video previews images
        ivVideo1 = (VideoView) findViewById(R.id.ivVideo1);
        ivVideo2 = (VideoView) findViewById(R.id.ivVideo2);
        ivVideo3 = (VideoView) findViewById(R.id.ivVideo3);
        ivVideo4 = (VideoView) findViewById(R.id.ivVideo4);
        //ImageViews for images
        ivPicture1 = (ImageView) findViewById(R.id.ivPicture1);
        ivPicture2 = (ImageView) findViewById(R.id.ivPicture2);
        ivPicture3 = (ImageView) findViewById(R.id.ivPicture3);
        ivPicture4 = (ImageView) findViewById(R.id.ivPicture4);
        //Make all dissapear innitially
        ivVideo1.setVisibility(View.GONE);
        ivVideo2.setVisibility(View.GONE);
        ivVideo3.setVisibility(View.GONE);
        ivVideo4.setVisibility(View.GONE);
        ivPicture1.setVisibility(View.GONE);
        ivPicture2.setVisibility(View.GONE);
        ivPicture3.setVisibility(View.GONE);
        ivPicture4.setVisibility(View.GONE);

        //Get the text view
        tvMessagePopUp1 = (TextView) findViewById(R.id.tvInfoMessage);





        //Get the first image
        putFirstImages();
        //Get the first videos
        putFirstVideos();

        //Get the GPS position
        getGPSposition();
        //Load the emergency message
        loadMessage();

         tvMessagePopUp1.getText();
        ImageView ivChangeMessage= (ImageView) findViewById(R.id.ivChangeMessage);
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
                int a =0;
                dialog.show();
            }
        });


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
        //create service
        GPSService sGPS = new GPSService(getApplicationContext());
        tViewGPS = (TextView) findViewById(R.id.tvGPS);
        if (sGPS.getLocation()) {
            sGPS.setView(this.tViewGPS);
        } else {

            tViewGPS.setText(R.string.GpsNotAct);
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
            if(isMediaRecent(cursor.getString(3))) {
                //Picture is valid


                //Switch which ImageView have to fill
                switch (i) {
                    case 0:
                        //Get the ImageView
                        ivPicture1.setVisibility(View.VISIBLE);
                        //Get the image location from the cursor element
                        String imageLocation1 = cursor.getString(1);
                        //Build File with the location
                        File imageFile1 = new File(imageLocation1);
                        if (imageFile1.exists()) {
                            //Build a bit map and set this bit map into the image view
                            Bitmap bm = BitmapFactory.decodeFile(imageLocation1);
                            ivPicture1.setImageBitmap(Bitmap.createScaledBitmap(bm, 120, 120, false));
                        }
                        break;
                    case 1:
                        //Get the ImageView visible
                        ivPicture2.setVisibility(View.VISIBLE);
                        //Get the image location from the cursor element
                        String imageLocation2 = cursor.getString(1);
                        //Build File with the location
                        File imageFile2 = new File(imageLocation2);
                        if (imageFile2.exists()) {
                            //Build a bit map and set this bit map into the image view
                            Bitmap bm = BitmapFactory.decodeFile(imageLocation2);
                            ivPicture2.setImageBitmap(Bitmap.createScaledBitmap(bm, 120, 120, false));
                        }
                        break;
                    default:
                        break;
                }

            }
            else{
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
            if(isMediaRecent(cursor.getString(3))) {
                //Video is valid
               //Switch which VideoView have to fill
                switch (i) {
                    case 0:
                        //Put the video view visibile
                        ivVideo1.setVisibility(View.VISIBLE);
                        //Get the uri of the video
                        Uri videoLocation1 = Uri.parse(cursor.getString(1));
                        //Put the video in the VideoView
                        ivVideo1.setVideoURI(videoLocation1);
                        ivVideo1.setMediaController(new MediaController(this));
                        ivVideo1.requestFocus();
                        break;
                    case 1:
                        //Get the ImageView
                        ivVideo2.setVisibility(View.VISIBLE);
                        //Put the video in the VideoView
                        Uri videoLocation2 = Uri.parse(cursor.getString(1));
                        ivVideo2.setVideoURI(videoLocation2);
                        ivVideo2.setMediaController(new MediaController(this));
                        ivVideo2.requestFocus();
                        break;
                    default:
                        break;
                }

            }
            else{
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

    public boolean isMediaRecent(String mediaDate){

        boolean isValid=false;

        //Create and instanciate a Calendar object
        Calendar pictureCal = Calendar.getInstance(); // Picture Calendar
        pictureCal.setTimeInMillis(parseLong(mediaDate)); //Create by parsing picture date
        //Get the month and add a 0 if necessary
        String pictureStrMonth=""+pictureCal.get(Calendar.MONTH);
        if(pictureStrMonth.length()==1) pictureStrMonth="0"+pictureStrMonth;
        //Get the day and add a 0 if necessary
        String pictureStrDay=""+pictureCal.get(Calendar.DAY_OF_MONTH);
        if(pictureStrDay.length()==1) pictureStrDay="0"+pictureStrDay;
        //Build a string with the date values (YYYYMMDD)
        String pictureStrDate = "" + (pictureCal.get(Calendar.YEAR) - 1900) +
                "" + pictureStrMonth +
                "" + pictureStrDay;


        //Create and instanciate a Calendar object
        Calendar currentCal = Calendar.getInstance(); //Current Calendar
        //Get the month and add a 0 if necessary
        String currentStrMonth=""+currentCal.get(Calendar.MONTH);
        if(currentStrMonth.length()==1) currentStrMonth="0"+currentStrMonth;
        //Get the day and add a 0 if necessary
        String currentStrDay=""+currentCal.get(Calendar.DAY_OF_MONTH);
        if(currentStrDay.length()==1) currentStrDay="0"+currentStrDay;
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
            isValid=false;
        }
        else {
            //dates match in the same day
            //Check if the time is lesser than 30 minutes
            if (((currentTime - pictureTime) >= 0) && ((currentTime - pictureTime) <= 30)) {
                //Time is lower than half hour
                isValid=true;
            }
            else {
                //Time is greater than half hour
                isValid=false;
            }

        }

        return isValid;
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
                bitMapPhoto = (Bitmap) bundl.get("data");
                //Set the new image (bitmapped) to the imageView
                ivPicture3.setVisibility(View.VISIBLE);
                ivPicture3.setImageBitmap(bitMapPhoto);
            }
            //onActivityResult for picking and image from gallery
            else if (requestCode == C_GALLERY_IMAGE) {
                //Find the path of the selected image.
                Uri photoLocation = data.getData();

                //Open this a stream of data/bytes
                try {
                    InputStream openInputStream = getContentResolver().openInputStream(photoLocation);
                    //Take a stream of data and convert in to a bitmap
                    bitMapGallery = BitmapFactory.decodeStream(openInputStream);

                    //Assign this image to our image view
                    ivPicture4.setVisibility(View.VISIBLE);
                    ivPicture4.setImageBitmap(bitMapGallery);
                }
                //Catch an exception if the file doesnt exist
                catch (FileNotFoundException e) {
                    e.printStackTrace();
                    //alert the user that something went wrong
                    Toast.makeText(this, getString(R.string.FileNotFound), Toast.LENGTH_LONG).show();

                }
            } else if (requestCode == C_VIDEO) {
                Uri videoLocation = data.getData();
                ivVideo3.setVisibility(View.VISIBLE);
                ivVideo3.setVideoURI(videoLocation);
                ivVideo3.setMediaController(new MediaController(this));
                ivVideo3.requestFocus();


            }
            else if (requestCode == C_GALLERY_VIDEO){
                //Find the path of the selected image.
                Uri videoLocation = data.getData();
                ivVideo4.setVisibility(View.VISIBLE);
                ivVideo4.setVideoURI(videoLocation);
                ivVideo4.setMediaController(new MediaController(this));
                ivVideo4.requestFocus();
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
        photoPickerIntent.setDataAndType(picturesDirectory, "image/*"); //aqui a lo mejor  se puede especificar los ultimos videos

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

    public void onClickEditMessage(View v){


    }

    public void selectGP1(View v) {
        if (!gP1) {
            ivPicture1.setColorFilter(Color.argb(50, 50, 0, 0));
            gP1 = true;
        } else {
            ivPicture1.setColorFilter(Color.argb(0, 0, 0, 0));
            gP1 = false;
        }
    }

    public void selectGP2(View v) {
        if (!gP2) {
            ivPicture2.setColorFilter(Color.argb(50, 50, 0, 0));
            gP2 = true;
        } else {
            ivPicture2.setColorFilter(Color.argb(0, 0, 0, 0));
            gP2 = false;
        }
    }



}
