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

package com.jobreporting.business.services;


import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.jobreporting.base.ConfigConstants;
import com.jobreporting.base.Constants;
import com.jobreporting.base.ENotificationType;
import com.jobreporting.business.common.AppLocationManager;
import com.jobreporting.business.common.AppNotificationManager;
import com.jobreporting.business.common.LogManager;
import com.jobreporting.business.facade.ServicesFacade;
import com.jobreporting.dao.JobReportingDao;
import com.jobreporting.dao.dataObjects.ReportData;
import com.jobreporting.requests.WSReportsRequest;
import com.jobreporting.responses.WSBaseResponse;
import com.jobreporting.responses.WSErrorResponse;
import com.jobreporting.responses.WSReportsResponse;
import com.jobreporting.utilities.ServiceUtility;
import com.jobreporting.utilities.Utility;
import com.jobreporting.views.ServerErrorActivity;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

public class ReportDispatchService extends IntentService {

    private final String LOG_TAG = ReportDispatchService.class.getSimpleName();


    JobReportingDao dao = null;

    public ReportDispatchService() {

        super("ReportDispatchService");

    }

    @Override
    protected void onHandleIntent(Intent intent) {

        dao = new JobReportingDao(this);

        LogManager.log(LOG_TAG, "Report Dispatch Service is started to send the report data to server...", Log.DEBUG);

        boolean running = true;
        long threadTimer = ConfigConstants.REPORT_DISPATCH_SERVICE_TIMER * 60 * 1000;

        try{
            while (running){

                LogManager.log(LOG_TAG, "Report Dispatch Service igniting...", Log.DEBUG);

                //Check the connectivity
                boolean isConnected = ServiceUtility.checkConnectivity(this);

                if (isConnected){
                    LogManager.log(LOG_TAG, "Connectivity is available, system will try to connect with server and send the report data.", Log.DEBUG);

                    //Fetch token key
                    String tokenKey = dao.fetchToken();
                    LogManager.log(LOG_TAG, "Token key : " + tokenKey, Log.DEBUG);

                    //Fetch the Report Data from local DB
                    LogManager.log(LOG_TAG, "Checking if report data exists", Log.DEBUG);
                    List<ReportData> dataList = new ArrayList<>();
                    boolean isDataAvailable = dao.fetchAllReportData(dataList);

                    if (isDataAvailable){
                        LogManager.log(LOG_TAG, "Data found in Reports table and will be send to the server", Log.DEBUG);

                        //Get Location Details
                        AppLocationManager lManager = AppLocationManager.getAppLocationManager();
                        lManager.refreshLocation();
                        String latitude = lManager.getLatitudeStr();
                        String longitude = lManager.getLongitudeStr();
                        String location = lManager.getLocationString();
                        if (Utility.safeTrim(location).equals(Constants.EMPTY_STRING)){
                            boolean isGPSEnabled = lManager.reCheckIfGPSEnabled();
                            if (!isGPSEnabled){
                                location = Constants.LOCATION_GPS_NOT_ENABLED;
                            }
                            else{
                                location = Constants.LOCATION_GPS_ENABLED;
                            }
                        }

                        for (ReportData reportData : dataList){

                            int id = reportData.getId();

                            //Call Service to send the data
                            LogManager.log(LOG_TAG, "Calling web service to send the report data to server", Log.DEBUG);
                            String generatedReportName = callServiceFacade(tokenKey, reportData, latitude, longitude, location);

                            if (!Utility.safeTrim(generatedReportName).equals("-1")){
                                LogManager.log(LOG_TAG, "Report data send successfully to server", Log.DEBUG);

                                //Update the status of the report data
                                boolean isUpdated = dao.updateDispatchedReportStatus(id);
                                if (isUpdated){
                                    LogManager.log(LOG_TAG, "Updated the report data status in database successfully", Log.DEBUG);

                                    //Generate the user notification
                                    AppNotificationManager anm = new AppNotificationManager(this);
                                    anm.setGeneratedReportName(generatedReportName);
                                    anm.sendNotification(ENotificationType.REPORT_DISPATCH);

                                }
                                else{
                                    LogManager.log(LOG_TAG, "Failed to update the report data status in database", Log.DEBUG);
                                }
                            }
                            else{
                                LogManager.log(LOG_TAG, "Failed to send Report data to server, will be tried again", Log.DEBUG);
                            }

                        }
                    }
                    else{
                        LogManager.log(LOG_TAG, "No Data in Reports table and no connection will be made to the server", Log.DEBUG);
                    }

                    //This service will stopped if there is no more report data avaialble to send server to save the processing cycles
                    LogManager.log(LOG_TAG, "All data has been send to server and there is no report data more, hence this service will be stopped now.", Log.DEBUG);
                    running = false;

                }
                else{
                    LogManager.log(LOG_TAG, "Connectivity is NOT available, system will try after some time to connect with server.", Log.DEBUG);

                    Thread.sleep(threadTimer);

                    LogManager.log(LOG_TAG, "Report Dispatch Service sleeping for : " + ConfigConstants.REPORT_DISPATCH_SERVICE_TIMER, Log.DEBUG);
                }

            }

        }
        catch (Exception ex){
            LogManager.log(LOG_TAG, "Exception occurred in Report Dispatch Service : " + ex.getMessage(), Log.ERROR);
        }

        LogManager.log(LOG_TAG, "Report Dispatch Service is done with sending the data to server...", Log.DEBUG);

    }

    private String callServiceFacade(String tokenKey, ReportData reportData, String latitude, String longitude, String location) throws Exception{

        String generatedReportName = "-1";

        ServicesFacade servicesFacade = new ServicesFacade();

        WSReportsRequest request = new WSReportsRequest();
        request.setTokenKey(tokenKey);
        request.setReportData(reportData.getData());
        request.setPhoto(reportData.getPhoto());
        request.setPhotoName(reportData.getPhotoName());
        request.setSign(reportData.getSign());
        request.setLatitude(latitude);
        request.setLongitude(longitude);
        request.setLocation(location);

        LogManager.log(LOG_TAG, "Calling Services...", Log.DEBUG);

        WSBaseResponse baseResponse = servicesFacade.callReportsService(request);

        if (null != baseResponse){

            LogManager.log(LOG_TAG, "Report service executed successfully.", Log.DEBUG);

            String status = baseResponse.getStatus();

            if (Utility.safeTrim(status).equals(Integer.toString(HttpURLConnection.HTTP_OK))){

                LogManager.log(LOG_TAG, "Success response returned from Server.", Log.DEBUG);

                WSReportsResponse response = (WSReportsResponse)baseResponse;
                String responseStatus = response.getResponse();
                String reportId = response.getReportId();

                LogManager.log(LOG_TAG, "Report Service response : " + responseStatus, Log.DEBUG);
                LogManager.log(LOG_TAG, "Generated Report Id : " + reportId, Log.DEBUG);

                if (Utility.safeTrim(responseStatus).equals(ConfigConstants.RESPONSE_SUCCESS)){
                    generatedReportName = reportId;
                }

            }
            else{
                LogManager.log(LOG_TAG, "ERROR response returned from Server.", Log.DEBUG);

                WSErrorResponse errorResponse = (WSErrorResponse)baseResponse;
                String code = errorResponse.getCode();
                String message = errorResponse.getUserMessage();
                String errorMessage = errorResponse.getErrorMessage();

                LogManager.log(LOG_TAG, "Error Message from server : " + errorMessage, Log.DEBUG);

                //Forward the redirection to Server Error Activity
                Intent intent = new Intent(this, ServerErrorActivity.class);
                intent.putExtra(Constants.INTENT_EXTRA_CUSTOM_SERVER_ERROR_CODE, code);
                intent.putExtra(Constants.INTENT_EXTRA_CUSTOM_SERVER_MESSAGE, message);
                intent.putExtra(Constants.INTENT_EXTRA_CUSTOM_SERVER_ERROR, errorMessage);
                intent.putExtra(Constants.INTENT_EXTRA_CUSTOM_SERVER_ERROR_OCCURRED, "Report Service: Sending Report data to server");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);

            }

        }
        else{
            LogManager.log(LOG_TAG, "Response returned null.", Log.ERROR);
            throw new Exception("Response returned null.");
        }

        return generatedReportName;

    }



}
