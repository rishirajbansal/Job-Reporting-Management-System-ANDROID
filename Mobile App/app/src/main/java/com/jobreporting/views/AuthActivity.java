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

package com.jobreporting.views;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.jobreporting.R;
import com.jobreporting.base.Constants;
import com.jobreporting.business.ApplicationInitializer;
import com.jobreporting.business.common.AppDeviceManager;
import com.jobreporting.business.common.LogManager;
import com.jobreporting.dao.JobReportingDao;
import com.jobreporting.utilities.Utility;

import okhttp3.OkHttpClient;


public class AuthActivity extends AppCompatActivity {

    private final String LOG_TAG = AuthActivity.class.getSimpleName();


    @Override
    @TargetApi(21)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_auth);

        this.setTitle(getResources().getString(R.string.title_activity_auth));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#607D8B")));
        Utility.setStatusBarColor(this);

        //For Testing purposes, can be removed, however, no impact on application
        runTimeInspection();

        /* Initialize the application */
        initialize();

    }

    private void initialize(){

        /*Instantiate Log Manager*/
        LogManager lMan = new LogManager();

        boolean isAuthenticated = verifyAuthentication();

        if (isAuthenticated){
            //Initialize the application and redirect to Home screen
            ApplicationInitializer applicationInitializer = ApplicationInitializer.igniteInitializer(this);
            applicationInitializer.redirectToHome(true);

            finish();
        }
        else{
            AppDeviceManager dManager = AppDeviceManager.createInstance(this);
            dManager.initialize();

            //Continue with Auth screen
        }

    }



    public boolean verifyAuthentication(){

        boolean isAuthenticated = false;

        LogManager.log(LOG_TAG, "Checking if the app is already authenticated", Log.DEBUG);

        JobReportingDao dao = new JobReportingDao(this);

        String tokenKey = dao.fetchToken();
        LogManager.log(LOG_TAG, "Token key returned while launching the app : " + tokenKey, Log.DEBUG);

        if (Utility.safeTrim(tokenKey).equals(Constants.EMPTY_STRING)){
            LogManager.log(LOG_TAG, "It seems the app is not authenticated, hence will initiate the process of authentication", Log.DEBUG);
        }
        else{
            LogManager.log(LOG_TAG, "App is authenticated already and will be redirected to home screen", Log.DEBUG);

            isAuthenticated = true;
        }

        return isAuthenticated;

    }

    public void runTimeInspection(){

        try{
            Stetho.initializeWithDefaults(this);
            new OkHttpClient.Builder().addNetworkInterceptor(new StethoInterceptor()).build();
        }
        catch (Exception ex){
            LogManager.log(LOG_TAG, "Inspection Testing Ex : "  + ex.getMessage(), Log.ERROR);
        }


    }

}


