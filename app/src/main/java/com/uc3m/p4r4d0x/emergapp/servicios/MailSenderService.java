package com.uc3m.p4r4d0x.emergapp.servicios;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.uc3m.p4r4d0x.emergapp.Constants;
import com.uc3m.p4r4d0x.emergapp.GMailSender;
import com.uc3m.p4r4d0x.emergapp.R;
import com.uc3m.p4r4d0x.emergapp.ResultReceiverSentReady;

/**
 * Created by Alvaro Loarte Rodriguez on 17/05/16..
 * Desc: Mail Sender service: gets the info from the EmergencyActivity (the main message, GPS position, pictures and videos)
 *       and send it by mail.
 *
 */

public class MailSenderService extends Service {
    private Context sContext;
    protected ResultReceiver mSender;

    public MailSenderService(Context c,ResultReceiverSentReady mReceiver) {
        mSender=mReceiver;
        this.sContext = c;
    }


    @Override
    public void onCreate() {

        super.onCreate();
    }


    /*
    * desc: This method performs in an asyncr mode a call to GMailSender.sendMail method.
    *       Asynch is required because the operation may take a while
    * par: toSendPicturesLocation toSendvideosLocation : array strings with the location in the phone of the pictures/videos
    *       toSendMessage, toSendGPSStreet, toSendGPSCoord : strings with the values
    * */
    public int sendMessage(final String toSendMessage, final String[] toSendPicturesLocation,
                           final String[] toSendvideosLocation,final String toSendGPSCoord,
                           final String toSendGPSStreet){
            new AsyncTask<Void, Void, Void>() {
                String statusMessage = "";
                int errorCode =-1;

                @Override
                protected void onPreExecute()
                {}

                @Override
                protected Void doInBackground(Void... params)
                {
                    //Create a new GMailSender with the sender address and its password
                    GMailSender sender = new GMailSender("ereporteruc3m@gmail.com", "3r3p0rt3ruc3m");
                    //GMailSender sender = new GMailSender("eReporter@outlook.com", "3r3p0rt3ruc3m");

                    try {
                        //Send the mail, giving all the parameters to the method
                        sender.sendMail("This is Subject", "This is Body", "aloarter@gmail.com", "albrathojaverde@gmail.com",
                                toSendMessage, toSendPicturesLocation, toSendvideosLocation, toSendGPSCoord, toSendGPSStreet);


                        Log.d("ALRALR", "MSS mensaje enviado");
                        errorCode=1;
                        statusMessage="Message sended";
                        deliverResultToReceiver(errorCode, statusMessage);


                    } catch (Exception e) {
                        Log.d("ALRALR", "MSS mensaje falla");
                        errorCode=2;
                        statusMessage="Fail in sending";
                        deliverResultToReceiver(errorCode, statusMessage);
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void res)
                {}
            }.execute();


        return 0;
    }


    private void deliverResultToReceiver(int resultCode, String message) {

        //Put the result message or the address on a Bundle
        Bundle bundle = new Bundle();
        bundle.putString(Constants.RESULT_DATA_KEY, message);
        //Send the values
        mSender.send(resultCode, bundle);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}