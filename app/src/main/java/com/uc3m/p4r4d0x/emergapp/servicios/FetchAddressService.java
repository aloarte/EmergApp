package com.uc3m.p4r4d0x.emergapp.servicios;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;
import com.uc3m.p4r4d0x.emergapp.Constants;
import com.uc3m.p4r4d0x.emergapp.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Alvaro Loarte Rodríguez on 26/03/16.
 * Define an intent service to fetch address : http://developer.android.com/intl/es/training/location/display-address.html
 * Desc: Obtains the address based on a Location item previously obtained by GPS or network
 */
public class FetchAddressService extends IntentService {
    protected ResultReceiver mSender;
    public Context sContext;

    //Default constructor (Neccesary for the AndroidManifest.xml)
    public FetchAddressService() {
        super("");
    }
    //Constructor
    public FetchAddressService (Context c, String name){

        super(name);
        this.sContext=c;

    }

    /*
    * Overrided method: triggered when startService(intent) is called
    * Param: an intent with the two params for this service: Location with the location data
    *        and the value for the ResultReceiver to return the address value
    * Desc:  Based on the Location, get the Address and calls deliverResultToReceiver to return
    *        the address value
    * */
    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("ALR", "Entro en on handleIntent de FAS");
        String errorMessage = "";
        //Create a Geocoder object
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        // Get the location passed to this service through an extra.
        Location locationFromGPS = intent.getParcelableExtra(
                Constants.LOCATION_DATA_EXTRA);
        // Get the ResultReceiver object passed to this service through an extra.
        mSender=intent.getParcelableExtra(Constants.RECEIVER);

        //Create a List of addresses
        List<Address> addresses = null;

        try {
            //Obtain the addres based on the latitude and longitude values from the location
            addresses = geocoder.getFromLocation(
                    locationFromGPS.getLatitude(),
                    locationFromGPS.getLongitude(),
                    //Get just a single address.
                    1);
        } catch (IOException ioException) {
            // Catch network or other I/O problems.
            errorMessage = getString(R.string.service_not_available);
            Log.e("ALR", errorMessage, ioException);
        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values.
            errorMessage = getString(R.string.invalid_lat_long_used);
            Log.e("ALR", errorMessage + ". " +
                    "Latitude = " + locationFromGPS.getLatitude() +
                    ", Longitude = " +
                    locationFromGPS.getLongitude(), illegalArgumentException);
        }

        // Handle case where no address was found.
        if (addresses == null || addresses.size() == 0) {
            if (errorMessage.isEmpty()) {
                errorMessage = getString(R.string.no_address_found);
            }

            //Call deliverResultToReceiver with failure result code and error message
            deliverResultToReceiver(Constants.FAILURE_RESULT, errorMessage);
        }
        //Handle case where address was found
        else {
            Address address = addresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<String>();

            // Fetch the address lines using getAddressLine,join in them, and send them to the thread.
            for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                addressFragments.add(address.getAddressLine(i));
            }

            //Call deliverResultToReceiver with success result code and the address
            deliverResultToReceiver(Constants.SUCCESS_RESULT,
                    TextUtils.join(System.getProperty("line.separator"),
                            addressFragments));
        }
    }

    /*
    * param: resultCode and a message to deliver to the ResultReceiver
    * desc:  Deliver through the ResultReceiver the result of this service
    */
    private void deliverResultToReceiver(int resultCode, String message) {

        //Put the result message or the address on a Bundle
        Bundle bundle = new Bundle();
        bundle.putString(Constants.RESULT_DATA_KEY, message);

        //Send the values
        mSender.send(resultCode, bundle);
    }
}
