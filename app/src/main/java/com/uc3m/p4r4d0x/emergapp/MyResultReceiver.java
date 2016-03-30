package com.uc3m.p4r4d0x.emergapp;

import android.os.Bundle;
import android.os.ResultReceiver;
import android.widget.TextView;


/**
 * Created by Alvaro Loarte Rodriguez on 30/03/16.
 * Desc: Object that receive the result from the FetchAddress service and prints the result on an TextView
 */

public class MyResultReceiver extends ResultReceiver {
        private Receiver mReceiver=null;
        private String address="";
        private TextView addressView;

    /*
    * Param: Handler and the TextView for printing the address
    * Desc: Main Constructor
    * */
    public MyResultReceiver(android.os.Handler handler, TextView addrView) {
        super(handler);
        addressView=addrView;
    }

    /*
    * Desc: Neccesary interface for onReceiveResult method
    * */
    public interface Receiver {
            void onReceiveResult(int resultCode,Bundle resultData);
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
            //If the resultCode was success, print the address on the TextView
            if (resultCode == Constants.SUCCESS_RESULT) {

                // Get the address from the resultData
                address = resultData.getString(Constants.RESULT_DATA_KEY);

                //Print the addres on the TextView
                setView(true);
            }
            else{
                //Print an error message on the Textview
                setView(false);
            }
        }
    }


  /*
  * Param: boolean value: true if the addres is obtained or false if not
  * Desc:  Prints the address in the TextView
  * */
    public void setView(boolean addressObtained){
        //Check if address is obtained
        if(addressObtained){
            //Display the address
            addressView.setText(address);
        }
        else{
            //Display an error message
            addressView.setText(R.string.address_not_obtained);
        }

    }

    }

