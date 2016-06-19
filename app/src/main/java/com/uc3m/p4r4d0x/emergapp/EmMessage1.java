package com.uc3m.p4r4d0x.emergapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.uc3m.p4r4d0x.emergapp.servicios.GPSService;

public class EmMessage1 extends AppCompatActivity {

    //Define constants to send info
    final int C_YES=1,C_NO=2;
    TextView tViewGPS, tViewGPSCoord;
    String   sGPSAddr, sGPSCoord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_em_message1);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().hide();

        //Get the GPS position
        getGPSposition();
    }

    /*
    * onClick method from button bYpop1
    * Save the "yes" info and run next activity
    * */
    public void onClickYesPopUp1(View v){

        //Check if the gps result is ready

        if(retrieveGPSPosition()){
            Intent i = new Intent(getApplicationContext(), EmMessage2.class);
            //Set value to var popUp1
            i.putExtra("popUp1", C_YES);
            i.putExtra("GPSC", sGPSCoord);
            i.putExtra("GPSA", sGPSAddr);
            //Launch intent
            startActivity(i);
        }
        //if is not ready, dont do anything when the button is pressed
        else{}

    }

    /*
    * onClick method from button bNpop1
    * Save the "no" info and run next activity
    * */
    public void onClickNoPopUp1(View v){

        //Check if the gps result is ready
        if(retrieveGPSPosition()){
            Intent i = new Intent(getApplicationContext(), EmMessage2.class);
            //Set value to var popUp1
            i.putExtra("popUp1",C_NO);
            i.putExtra("GPSC",sGPSCoord);
            i.putExtra("GPSA",sGPSAddr);
            //Launch intent
            startActivity(i);
        }
        //if is not ready, dont do anything when the button is pressed
        else{}



    }

    /*
    * Desc: Calls GPS Service and prints in the TextView the result
    * */
    public void getGPSposition() {

        //Get the TextView to show the address value
        tViewGPS      = (TextView) findViewById(R.id.tvGPSEM1);
        tViewGPSCoord = (TextView) findViewById(R.id.tvGPSCoordEM1);

        //create service passing two TextViews as a param
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
    * Try to get in strings the GPS position
    * Return true or false if is not obtained
    * */
    public boolean retrieveGPSPosition(){
        sGPSCoord = (String) tViewGPSCoord.getText();
        sGPSAddr  = (String) tViewGPS.getText();

        return (!sGPSAddr.isEmpty() && !sGPSCoord.isEmpty());


    }
}
