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


import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

public class AppDeviceManager {

    private final String LOG_TAG = AppDeviceManager.class.getSimpleName();

    public static Context context = null;

    private static AppDeviceManager appDeviceManager = null;

    private String deviceId;
    private String androidId;


    private AppDeviceManager() {

    }

    public static AppDeviceManager createInstance(Context context)  {

        AppDeviceManager.context = context;

        if (appDeviceManager == null) {
            appDeviceManager = new AppDeviceManager();
        }

        return appDeviceManager;
    }


    public static AppDeviceManager getAppDeviceManager(){

        return appDeviceManager;
    }


    public void initialize(){

        try{
            TelephonyManager tManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            deviceId = tManager.getDeviceId();
            androidId = android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        }
        catch (Exception ex){
            LogManager.log(LOG_TAG, "AppDeviceManager->Exception occurred : " + ex.getMessage(), Log.ERROR);
        }

    }

    public String getDeviceId() {

        return deviceId;
    }

    public void setDeviceId(String deviceId) {

        this.deviceId = deviceId;
    }

    public String getAndroidId() {

        return androidId;
    }

    public void setAndroidId(String androidId) {

        this.androidId = androidId;
    }

}
