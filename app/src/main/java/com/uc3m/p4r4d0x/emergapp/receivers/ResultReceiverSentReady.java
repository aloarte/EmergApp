package com.uc3m.p4r4d0x.emergapp.receivers;

import android.content.Context;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.uc3m.p4r4d0x.emergapp.R;
import com.uc3m.p4r4d0x.emergapp.helpers.Constants;


/**
 * Created by Alvaro Loarte Rodriguez on 30/03/16.
 * Desc: Object that receive the result from the FetchAddress service and prints the result on TextViews
 */

public class ResultReceiverSentReady extends ResultReceiver {
        private Receiver mReceiver=null;
        private RelativeLayout rlToHide;
        private ImageView ivRotate;
        private Context contextRR;
        private String resultMessage="";



    /*
    * Param: Handler and the TextViews for printing the address and the latitude and longitude
    * Desc: Main Constructor
    * */
    public ResultReceiverSentReady(android.os.Handler handler,RelativeLayout layoutToHide,ImageView ivparam,Context context,int idToRotate) {
        super(handler);
        rlToHide              = layoutToHide;
        contextRR=context;
        ivRotate=ivparam;

        //Create and load the animation
        Animation rotateAnimation;
        rotateAnimation= AnimationUtils.loadAnimation(contextRR, idToRotate);
        //Reset and start it
        rotateAnimation.reset();
        ivRotate.startAnimation(rotateAnimation);

    }

    /*
    * Desc: Neccesary interface for onReceiveResult method
    * */
    public interface Receiver {
            void onReceiveResult(int resultCode, Bundle resultData);
    }

   /*
   * Param: resultCode and resultData obtained from the operation from FetchAddressService
   * Desc:  Overrided method called when any result on the ResultReceiver is obtained
   *        Check the value and call setView function for printing the address
   * */
    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {

        //Check if the method was called proper
        if(mReceiver != null) {
            mReceiver.onReceiveResult(resultCode,resultData);
        }
        else {

            // Get the address and the latitude and longitude from the resultData object
            resultMessage = resultData.getString(Constants.RESULT_DATA_KEY);

            //Toast the result
            Toast.makeText(contextRR, resultMessage, Toast.LENGTH_LONG).show();

            //Hide the layer
            rlToHide.setVisibility(View.INVISIBLE);

        }
    }




    }

