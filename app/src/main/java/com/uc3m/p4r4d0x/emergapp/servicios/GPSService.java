package com.uc3m.p4r4d0x.emergapp.servicios;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.uc3m.p4r4d0x.emergapp.EmergencyActivity;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by p4r4d0x on 20/02/16.
 */
public class GPSService extends Service implements LocationListener {

    private Context sContext;
    double latitude,longitude;
    //Guarda diferentes coordenadas(latitud,longitud,precision)
    Location locationG;
    LocationManager locationManager;
    boolean gpsActive;
    TextView tvWriteCoord;


    //Default constructor
    public GPSService (){
        //SUper ejecuta el constructor de  la clase Service extendida
        super();
        this.sContext = this.getApplicationContext();
    }
    //Constructor
    public GPSService (Context c){
        super();
        this.sContext=c;
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
    * Par : A View object to set new values on it
    * Desc: Prints the latitude and longitude values  in the View
    * */
    public void setView(View v){
        tvWriteCoord= (TextView) v;
        tvWriteCoord.setText("Coordenadas:"+latitude+","+longitude+".");
    }


    /*
    * Ret : True or false if the location was obtained
    * Desc: Obtain the location item by GPS or Network method.
    *       First try by GPS and if fails, try by network
    * */
    public boolean getLocation(){
        //Try to get location by GPS
        if(getLocationByGPS()){
            //Put values obtained from locationG
            latitude = locationG.getLatitude();
            longitude = locationG.getLongitude();
            Log.d("ALR", "GetLocation:GPS location"+latitude+","+longitude);
            return true;
        }
        //If fails, try to get location by network
        else if(getLocationByNetwork()){
            //Put values obtained from locationG
            latitude = locationG.getLatitude();
            longitude = locationG.getLongitude();
            Log.d("ALR", "GetLocation:NW location"+latitude+","+longitude);
            return true;
        }
        //If both fail, return error status
        else{
            Log.d("ALR", "GetLocation:Cant get any location");
            return false;
        }
        /*
            Toast.makeText(
                    getBaseContext(),
                    "Location changed: Lat: " + locationG.getLatitude() + " Lng: "
                            + locationG.getLongitude(), Toast.LENGTH_SHORT).show();
            String longitude = "Longitude: " + locationG.getLongitude();
            Log.v("Emergapp", longitude);
            String latitude = "Latitude: " + locationG.getLatitude();
            Log.v("Emergapp", latitude);

        //------- To get city name from coordinates --------
            String cityName = null;
            Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
            List<Address> addresses;
            try {
                addresses = gcd.getFromLocation(locationG.getLatitude(),
                        locationG.getLongitude(), 1);
                if (addresses.size() > 0) {
                    System.out.println(addresses.get(0).getLocality());
                    cityName = addresses.get(0).getLocality();
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
   */

    }


    /*
    * Ret : True or false if the location was obtained by GPS
    * Desc: Obtain the location item by GPS
    * */
    public boolean getLocationByGPS(){
        //Local location
        Location locationL = null;

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

                //Check if the previos operation was successful
                if (mLocationManager != null) {

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
        Location locationL = null;
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

                //Check if the previos operation was successful
                if (mLocationManager != null) {

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
}