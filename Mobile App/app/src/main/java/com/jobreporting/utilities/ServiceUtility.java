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

package com.jobreporting.utilities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.jobreporting.business.common.LogManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class ServiceUtility {

    private static final String LOG_TAG = ServiceUtility.class.getSimpleName();


    public static byte[] serializeObjToByteStream(Object obj){

        byte[] requestBytes = null;

        try{
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
            oos.flush();
            baos.close();
            oos.close();

            requestBytes = baos.toByteArray();
        }
        catch(IOException ioEx){
            LogManager.log(LOG_TAG, "IOException exception occurred in serializeObjToByteStream" + ioEx.getMessage(), Log.ERROR);
        }

        return requestBytes;

    }

    public static boolean checkConnectivity(Context context){

        boolean isConnected = false;

        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if (null != activeNetwork){
            isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

            if (isConnected){
                String connType = activeNetwork.getTypeName();
                LogManager.log(LOG_TAG, "(Report Dispatch Service) Connectivity type is : " + connType, Log.DEBUG);
            }

        }

        return isConnected;
    }

    public static void showErrorAlertMessage(Context context, String title, String message) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        alertDialog.setTitle(title);

        alertDialog.setMessage(message);

        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();

    }

}
