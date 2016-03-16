package com.uc3m.p4r4d0x.emergapp.servicios;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

/**
 * Created by p4r4d0x on 20/02/16.
 */
public class GPSService extends Service implements LocationListener {

    private Context sContext;
    double latitude,longitude;
    //Guarda diferentes coordenadas(latitud,longitud,precision)
    Location location;
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
    * Desc: Prints the result in the View
    * */
    public void setView(View v){
        tvWriteCoord= (TextView) v;
        tvWriteCoord.setText("Coordenadas:"+latitude+","+longitude+".");
    }

    public boolean getLocation(){
        boolean locObtained=false;
        locObtained=true;
        try{
            //Permite acceder a servicios del sistema ya implementados del telefono (LOCATION_SERVICE) que te da las coordenadas GPS
            locationManager= (LocationManager) this.sContext.getSystemService(LOCATION_SERVICE);
            gpsActive=locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            //gpsActive=locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER); //Este seria para gps por red
            if(gpsActive) {


                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_LOW);
                String provider= locationManager.getBestProvider(criteria,true);
                //Pone la ultima localizacion conocida
                location = locationManager.getLastKnownLocation(provider); //NETWORKPROVIDER si es por red
                //Extrae los datos del objeto location (que tiene todos los atributos )
                if (location == null) {
                    //Peticion al servicio: 1000 tiempo en ms para obtener actualizaciones.10 son los metros hasta actualizaciones. this porque es esta clase la que quiere el dato

                    locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 1000 * 60, 10, this);
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();

                }
                else{
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();

                }


            }
        }
        catch(SecurityException eS){}
        return locObtained;
    }
}