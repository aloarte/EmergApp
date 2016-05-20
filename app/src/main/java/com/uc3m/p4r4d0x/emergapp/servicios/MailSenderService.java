package com.uc3m.p4r4d0x.emergapp.servicios;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.uc3m.p4r4d0x.emergapp.GMailSender;

/**
 * Created by p4r4d0x on 17/05/16.
 */

public class MailSenderService extends Service {

    String storedSimSerial;
    String currentSimSerial;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public int sendMessage(final String toSendMessage, final String[] toSendPicturesLocation,
                           final String[] toSendvideosLocation,final String toSendGPSCoord,
                           final String toSendGPSStreet){
            new AsyncTask<Void, Void, Void>() {

                @Override
                protected void onPreExecute()
                {
                    Log.d("ALR", "onPreExecute()");
                }

                @Override
                protected Void doInBackground(Void... params)
                {
                    Log.d("ALR", "doInBackground() -- Here is the download");
                    GMailSender sender = new GMailSender("ereporteruc3m@gmail.com", "3r3p0rt3ruc3m");
                    //GMailSender sender = new GMailSender("eReporter@outlook.com", "3r3p0rt3ruc3m");

                    try {
                        Log.d("ALR","Antes de enviar");
                        sender.sendMail("This is Subject","This is Body","aloarter@gmail.com", "albrathojaverde@gmail.com",
                                toSendMessage,toSendPicturesLocation,toSendvideosLocation,toSendGPSCoord,toSendGPSStreet);
                        Log.d("ALR", "despues de enviar");
                    } catch (Exception e) {
                        Log.d("ALR","Catch "+e+" de enviar");
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void res)
                {
                    Log.d("ALR", "onPostExecute()");
                }
            }.execute();


        return 0;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}