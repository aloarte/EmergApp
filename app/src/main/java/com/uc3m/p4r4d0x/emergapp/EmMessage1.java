package com.uc3m.p4r4d0x.emergapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class EmMessage1 extends AppCompatActivity {

    //Define constants to send info
    final int C_YES=1,C_NO=2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_em_message1);
    }

    /*
    * onClick method from button bYpop1
    * Save the "yes" info and run next activity
    * */
    public void onClickYesPopUp1(View v){
        Intent i = new Intent(getApplicationContext(), EmMessage2.class);
        //Set value to var popUp1
        i.putExtra("popUp1",C_YES);
        //Launch intent
        startActivity(i);

    }

    /*
    * onClick method from button bNpop1
    * Save the "no" info and run next activity
    * */
    public void onClickNoPopUp1(View v){
        Intent i = new Intent(getApplicationContext(), EmMessage2.class);
        //Set value to var popUp1
        i.putExtra("popUp1",C_NO);
        //Launch intent
        startActivity(i);

    }
}
