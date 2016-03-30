package com.uc3m.p4r4d0x.emergapp.servicios;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;

import com.uc3m.p4r4d0x.emergapp.Constants;
import com.uc3m.p4r4d0x.emergapp.MyResultReceiver;


/**
 * Created by Alvaro Loarte Rodriguez on 20/02/16.
 * Desc: GPS service: gets the Location by network or GPS provider and calls FetchAddress service to get the
 *       address based in the location obtained
 *
 */
public class GPSService extends Service implements LocationListener  {

    private Context sContext;
    double latitude,longitude;
    Location locationG;
    TextView paramView;



    protected MyResultReceiver mReceiver;

    //Default constructor (Neccesary for the AndroidManifest.xml)
    public GPSService (){
        //SUper ejecuta el constructor de  la clase Service extendida
        super();
        this.sContext = this.getApplicationContext();

    }
    //Constructor
    public GPSService (Context c, TextView v){
        super();
        this.sContext=c;
        this.paramView=v;
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    //pendiente de cada cuanto cambia la posicion nuestra. Actualizacion de ubicacion
    @Override
    public void onLocationChanged(Location location) {
        locationG=location;

    }
    //Si te conectas o no te conexctas
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }
    //Si esta habilitado el gps
    @Override
    public void onProviderEnabled(String provider) {

    }
    //Si no esta habilitado
    @Override
    public void onProviderDisabled(String provider) {

    }




    /*
    * Ret : True or false if the location was obtained
    * Desc: Obtain the location item by GPS or Network method.
    *       First try by GPS and if fails, try by network
    * */
    public boolean getLocation(){
        boolean locationObtained;

        //Try to get location by GPS
        if(getLocationByGPS()){
            //Put values obtained from locationG
            latitude = locationG.getLatitude();
            longitude = locationG.getLongitude();
            Log.d("ALR", "GetLocation:GPS location"+latitude+","+longitude);
            locationObtained = true;

        }
        //If fails, try to get location by network
        else if(getLocationByNetwork()){
            //Put values obtained from locationG
            latitude = locationG.getLatitude();
            longitude = locationG.getLongitude();
            Log.d("ALR", "GetLocation:NW location"+latitude+","+longitude);
            locationObtained = true;

        }
        //If both fail, return error status
        else{

            Log.d("ALR", "GetLocation:Cant get any location");
            locationObtained = false;
       }
        return locationObtained;
    }


    /*
    * Ret : True or false if the location was obtained by GPS
    * Desc: Obtain the location item by GPS
    * */
    public boolean getLocationByGPS(){
        //Local location
        Location locationL;

        try {
            //Get a local locationManager by location service
            LocationManager mLocationManager = (LocationManager) sContext.getSystemService(LOCATION_SERVICE);
            //Get GPS status checking if is enabled
            boolean isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isLocationObtainedByGPS=false;

            //Get the location by gps if GPS was enable
            if (isGPSEnabled) {

                //RequestLocationUpdates by GPS_PROVIDER
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0, this);
                Log.d("ALR", "GPS Enabled");

                //Obtain a local location
                locationL = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                //Check if the previous operation was successful
                if (locationL != null) {
                    //Set the current local location as the valid location in locationG
                    isLocationObtainedByGPS=true;
                    locationG=locationL;
                    Log.d("ALR", "GPS get the location");
                }
            }

            return isLocationObtainedByGPS;
        }
        catch (SecurityException se){
            return false;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /*
    * Ret : True or false if the location was obtained by Network
    * Desc: Obtain the location item by Network
    * */
    public boolean getLocationByNetwork(){
        Location locationL;
        try {
            //Get a local locationManager by location service
            LocationManager mLocationManager = (LocationManager) sContext.getSystemService(LOCATION_SERVICE);
            //Get Network status checking if is enabled
            boolean isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            boolean isLocationObtainedByNW=false;

            //Get the location by gps if Network was enable
            if (isNetworkEnabled) {

                mLocationManager.requestLocationUpdates( LocationManager.NETWORK_PROVIDER,  0,  0, this);
                Log.d("ALR", "Network");

                //Get a local location
                locationL = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                //Check if the previous operation was successful
                if (locationL != null) {
                    //Set the current local location as the valid location in locationG
                    isLocationObtainedByNW=true;
                    locationG=locationL;
                    Log.d("ALR", "NW get the location");

                }
            }
            return isLocationObtainedByNW;
        }
        catch (SecurityException se){
            return false;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

   /*
   * Desc: Start FetchAddress service, passing a MyResultReceiver object to
   *       get the result value and the Location obtained by this service
   * */
    public void startFetchAddressService() {

        //Iniciate MyResultReceiver object
        mReceiver = new MyResultReceiver(new android.os.Handler(),paramView);

        //Create the intent to start the FetchAddressService
        Intent intent = new Intent(sContext, FetchAddressService.class);
        //Add the params for the service
        intent.putExtra(Constants.RECEIVER, mReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, locationG);
        //Start service based on sContext (getApplicationContext fails)
        sContext.startService(intent);
  }

}