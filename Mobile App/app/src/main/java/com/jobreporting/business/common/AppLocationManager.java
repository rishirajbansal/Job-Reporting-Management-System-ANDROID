/*
 * Licensed To: ThoughtExecution & 9sistemes
 * Authored By: Rishi Raj Bansal
 * Developed in: 2016
 *
 * ===========================================================================
 * This is FULLY owned and COPYRIGHTED by ThoughtExecution
 * This code may NOT be RESOLD or REDISTRIBUTED under any circumstances, and is only to be used with this application
 * Using the code from this application in another application is strictly PROHIBITED and not PERMISSIBLE
 * ===========================================================================
 */

package com.jobreporting.business.common;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import com.jobreporting.R;
import com.jobreporting.base.Constants;
import com.jobreporting.utilities.Utility;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class AppLocationManager implements LocationListener {

    private final String LOG_TAG = AppLocationManager.class.getSimpleName();

    public static Context context = null;

    private static AppLocationManager appLocationManager = null;

    private LocationManager locationManager;
    private Location location;
    private String provider;
    private boolean isGPSEnabled = false;
    private double latitude;
    private double longitude;

    private String latitudeStr = "";
    private String longitudeStr = "" ;
    private String locationString = "" ;

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 5; // meters
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute


    private AppLocationManager() {

    }

    public static AppLocationManager createInstance(Context context)  {

        AppLocationManager.context = context;

        if (appLocationManager == null) {
            appLocationManager = new AppLocationManager();
        }

        return appLocationManager;
    }

    public static AppLocationManager getAppLocationManager(){

        return appLocationManager;
    }



    public void initialize(){

        try{
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            Criteria criteria = new Criteria();
            provider = locationManager.getBestProvider(criteria, false);

            locationManager.requestLocationUpdates(provider, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

            location = locationManager.getLastKnownLocation(provider);

            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();

                latitudeStr = Double.toString(latitude);
                longitudeStr = Double.toString(longitude);

                if ((latitudeStr.substring(latitudeStr.indexOf(".")).length()) > 8) {
                    latitudeStr = latitudeStr.substring(0, latitudeStr.indexOf(".")) + latitudeStr.substring(latitudeStr.indexOf(".")).substring(0, 8);
                }
                if ((longitudeStr.substring(longitudeStr.indexOf(".")).length()) > 8){
                    longitudeStr = longitudeStr.substring(0, longitudeStr.indexOf(".")) + longitudeStr.substring(longitudeStr.indexOf(".")).substring(0, 8);
                }

                this.setLatitudeStr(latitudeStr);
                this.setLongitudeStr(longitudeStr);

                String locationString = identifyLocation();
                LogManager.log(LOG_TAG, "Location String identified: " + locationString, Log.DEBUG);
                this.setLocationString(locationString);

            }
            else{
                LogManager.log(LOG_TAG, "Location found null", Log.DEBUG);
            }

        }
        catch (SecurityException secEx){
            LogManager.log(LOG_TAG, "AppLocationManager->SecurityException occurred : " + secEx.getMessage(), Log.ERROR);
        }
        catch (Exception ex){
            LogManager.log(LOG_TAG, "AppLocationManager->Exception occurred : " + ex.getMessage(), Log.ERROR);
        }

    }

    public void refreshLocation(){

        initialize();
    }

    public boolean checkIfGPSEnabled(){

        return isGPSEnabled;

    }

    public boolean reCheckIfGPSEnabled(){

        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        return isGPSEnabled;

    }

    public String identifyLocation(){

        String locationString = "";

        try{
            Geocoder gcd = new Geocoder(context, Locale.getDefault());

            List<Address> addresses = gcd.getFromLocation(getLatitude(), getLongitude(), 1);

            if (addresses.size() > 0) {
                Address address = addresses.get(0);

                StringBuilder sb = new StringBuilder();
                for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                    sb.append(address.getAddressLine(i)).append(Constants.LOCATION_STRING_SEPERATOR);
                }
                /*if (!Utility.safeTrim(address.getLocality()).equals(Constants.EMPTY_STRING)){
                    sb.append(address.getLocality()).append(Constants.LOCATION_STRING_SEPERATOR);
                }*/
                if (!Utility.safeTrim(address.getPostalCode()).equals(Constants.EMPTY_STRING)){
                    sb.append(address.getPostalCode()).append(Constants.LOCATION_STRING_SEPERATOR);
                }
                if (!Utility.safeTrim(address.getCountryName()).equals(Constants.EMPTY_STRING)){
                    sb.append(address.getCountryName());
                }

                locationString = sb.toString();
            }
        }
        catch (IOException ioEx){
            LogManager.log(LOG_TAG, "AppLocationManager->()identifyLocation : " + ioEx.getMessage(), Log.ERROR);
            locationString = Constants.LOCATION_GEOCODE_TIMEDOUT;
        }

        return  locationString;
    }

    public void showGPSAlertMessage() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        alertDialog.setTitle(context.getText(R.string.location_detect_title));

        alertDialog.setMessage(context.getText(R.string.location_detect_message));

        alertDialog.setPositiveButton(R.string.location_btn_settings, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(intent);
            }
        });

        alertDialog.setNegativeButton(R.string.location_btn_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();

    }

    public double getLatitude(){
        if(location != null){
            latitude = location.getLatitude();
        }

        return latitude;
    }

    public double getLongitude(){
        if(location != null){
            longitude = location.getLongitude();
        }

        return longitude;
    }

    public String getLatitudeStr() {

        return latitudeStr;
    }

    public void setLatitudeStr(String latitudeStr) {

        this.latitudeStr = latitudeStr;
    }

    public String getLongitudeStr() {

        return longitudeStr;
    }

    public void setLongitudeStr(String longitudeStr) {

        this.longitudeStr = longitudeStr;
    }

    public String getLocationString() {

        return locationString;
    }

    public void setLocationString(String locationString) {

        this.locationString = locationString;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }
}
