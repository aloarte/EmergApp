package com.uc3m.p4r4d0x.emergapp.receivers;

import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.uc3m.p4r4d0x.emergapp.helpers.Constants;


/**
 * Created by Alvaro Loarte Rodriguez on 30/03/16.
 * Desc: Object that receive the result from the FetchAddress service and prints the result on TextViews
 */

public class ResultReceiverSentReady extends ResultReceiver {
        private Receiver mReceiver=null;
    private RelativeLayout rlToHide;
    private TextView tvPointsResultMessage,tvOKResultMessage,tvNotOKResultMessage;
        private String resultMessage="";
        private String errorMessage="";



    /*
    * Param: Handler and the TextViews for printing the address and the latitude and longitude
    * Desc: Main Constructor
    * */
    public ResultReceiverSentReady(android.os.Handler handler,RelativeLayout layoutToHide,TextView tvSMPoints,TextView tvSMOK,TextView tvSMNotOK) {
        super(handler);
        rlToHide              = layoutToHide;
        tvPointsResultMessage = tvSMPoints;
        tvOKResultMessage     = tvSMOK;
        tvNotOKResultMessage  = tvSMNotOK;

        Log.d("ALRALR","constructor RRSR");
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
        //tvOKResultMessage.setVisibility(View.GONE);
        //tvNotOKResultMessage.setVisibility(View.GONE);
        tvPointsResultMessage.setVisibility(View.VISIBLE);
        //Check if the method was called proper
        if(mReceiver != null) {
            mReceiver.onReceiveResult(resultCode,resultData);
        }
        else {

            // Get the address and the latitude and longitude from the resultData object
            resultMessage = resultData.getString(Constants.RESULT_DATA_KEY);
            Log.d("ALRALR", "Result code: " + resultCode + " resultmessage: " + resultMessage);

            switch (resultCode){
                case 1:
                    tvPointsResultMessage.setVisibility(View.GONE);
                   // tvOKResultMessage.setVisibility(View.VISIBLE);
                                        Log.d("ALRALR", "11");
                    break;
                case 2:
                    //tvPointsResultMessage.setVisibility(View.GONE);
                    //tvNotOKResultMessage.setVisibility(View.VISIBLE);


                    Log.d("ALRALR", "22");
                    break;
                default:
                    //tvPointsResultMessage.setVisibility(View.GONE);

                    break;
            }

            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            rlToHide.setVisibility(View.INVISIBLE);

        }
    }




    }

