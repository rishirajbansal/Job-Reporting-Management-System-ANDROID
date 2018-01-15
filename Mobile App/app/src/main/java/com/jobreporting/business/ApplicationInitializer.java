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

package com.jobreporting.business;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.jobreporting.base.Constants;
import com.jobreporting.business.common.AppDeviceManager;
import com.jobreporting.business.common.AppLocationManager;
import com.jobreporting.business.common.LogManager;
import com.jobreporting.business.services.ReportDispatchService;
import com.jobreporting.business.services.SyncherService;
import com.jobreporting.views.HomeActivity;

import java.util.Locale;

public class ApplicationInitializer {

    private final String LOG_TAG = ApplicationInitializer.class.getSimpleName();

    public static Context context = null;

    private static ApplicationInitializer applicationInitializer = null;

    private static Locale locale = null;


    private ApplicationInitializer() {

    }

    public static ApplicationInitializer igniteInitializer(Context context) {

        ApplicationInitializer.context = context;

        if (applicationInitializer == null) {
            applicationInitializer = new ApplicationInitializer();

            applicationInitializer.initialize();
        }

        return applicationInitializer;
    }

    public static ApplicationInitializer getApplicationInitializer(){

        return applicationInitializer;
    }

    private void initialize(){

        /* Configuration of Device manager */
        configureDeviceManager();

        /* Configuration of Location manager */
        configureLocationManager();

        /* Initiate Syncher Service */
        callSyncherService();

        /* Call Report Dispatch service to verify if there are pending reports to be submitted */
        callReportDispatchService();



    }

    public void redirectToHome(boolean isAlreadyAuthenticated){

        Intent intent = new Intent(context, HomeActivity.class);
        intent.putExtra(Constants.INTENT_EXTRA_CUSTOM_AUTH_SUCCESSFULL, isAlreadyAuthenticated);
        context.startActivity(intent);

    }

    private void callSyncherService(){

        LogManager.log(LOG_TAG, "Syncing the local data with the server to check if there are any updates...", Log.DEBUG);

        Intent intent = new Intent(context, SyncherService.class);

        /* If this service is not already running, it will be instantiated and started (creating a process for it if needed);
        if it is running then it remains running. */

        ComponentName service = context.startService(intent);
        LogManager.log(LOG_TAG, "Syncher Service called : " + service.getShortClassName(), Log.DEBUG);

    }

    private void callReportDispatchService(){

        LogManager.log(LOG_TAG, "Checking if any pending report exist to send to server...", Log.DEBUG);

        Intent intent = new Intent(context, ReportDispatchService.class);

        /* If this service is not already running, it will be instantiated and started (creating a process for it if needed);
        if it is running then it remains running. */
        ComponentName service = context.startService(intent);
        LogManager.log(LOG_TAG, "Report Dispatch Service called : " + service.getShortClassName(), Log.DEBUG);

    }

    private void configureLocationManager(){

        AppLocationManager lManager = AppLocationManager.createInstance(context);
        lManager.initialize();

        boolean isGPSEnabled = lManager.checkIfGPSEnabled();
        if (!isGPSEnabled){
            LogManager.log(LOG_TAG, "GPS is not enabled, asking user to enable it.", Log.DEBUG);
            lManager.showGPSAlertMessage();
        }
        else{
            LogManager.log(LOG_TAG, "GPS is enabled.", Log.DEBUG);
        }

    }

    private void configureDeviceManager(){

        AppDeviceManager dManager = AppDeviceManager.createInstance(context);
        dManager.initialize();

    }

    public static String getDeviceLocale(){

        locale = context.getResources().getConfiguration().locale;

        return locale.toString();

    }

}
