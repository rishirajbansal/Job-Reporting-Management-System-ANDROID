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


import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.jobreporting.R;
import com.jobreporting.base.ENotificationType;

public class AppNotificationManager {

    private final String LOG_TAG = AppNotificationManager.class.getSimpleName();

    public static Context context = null;

    public String generatedReportName;


    public AppNotificationManager(Context context){

        this.context = context;
    }


    public void sendNotification(ENotificationType notificationType){

        try{
            switch (notificationType){

                case REPORT_DISPATCH:

                    reportDispatchNotification();
                    break;

                default:
                    throw new Exception("Unsupported notificationType type.");

            }

        }
        catch (Exception ex){
            LogManager.log(LOG_TAG, "AppNotificationManager->Exception occurred : " + ex.getMessage(), Log.ERROR);
        }

    }

    private void reportDispatchNotification(){

        int notificationIdentifier = 101;

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                                                    .setSmallIcon(R.drawable.rpt_notify)
                                                    .setContentTitle(context.getText(R.string.notify_report_cTitle))
                                                    .setContentText(context.getText(R.string.notify_report_cText) + generatedReportName);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(notificationIdentifier, mBuilder.build());

    }

    public String getGeneratedReportName() {

        return generatedReportName;
    }

    public void setGeneratedReportName(String generatedReportName) {
        this.generatedReportName = generatedReportName;
    }
}
