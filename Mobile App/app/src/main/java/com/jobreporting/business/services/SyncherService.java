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
import com.jobreporting.business.common.LogManager;
import com.jobreporting.business.facade.ServicesFacade;
import com.jobreporting.dao.JobReportingDao;
import com.jobreporting.entities.WSUserEntity;
import com.jobreporting.requests.WSSyncherRequest;
import com.jobreporting.responses.WSBaseResponse;
import com.jobreporting.responses.WSErrorResponse;
import com.jobreporting.responses.WSSyncherResponse;
import com.jobreporting.utilities.ServiceUtility;
import com.jobreporting.utilities.Utility;
import com.jobreporting.views.ServerErrorActivity;

import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

public class SyncherService extends IntentService {

    private final String LOG_TAG = SyncherService.class.getSimpleName();


    JobReportingDao dao = null;

    public SyncherService() {

        super("SyncherService");

    }

    @Override
    protected void onHandleIntent(Intent intent) {

        dao = new JobReportingDao(this);

        LogManager.log(LOG_TAG, "Syncher Service is started to sync the data with the server...", Log.DEBUG);

        boolean running = true;
        long threadTimer = ConfigConstants.SYNCHER_SERVICE_TIMER * 60 * 1000;

        try{
            while (running){

                LogManager.log(LOG_TAG, "Syncher Service igniting...", Log.DEBUG);

                //Check the connectivity
                boolean isConnected = ServiceUtility.checkConnectivity(this);

                if (isConnected){
                    LogManager.log(LOG_TAG, "Connectivity is available, system will try to connect with server and sync the data with the server.", Log.DEBUG);

                    //Fetch token key
                    String tokenKey = dao.fetchToken();
                    LogManager.log(LOG_TAG, "Token key : " + tokenKey, Log.DEBUG);

                    LogManager.log(LOG_TAG, "Calling Syncher Services...", Log.DEBUG);

                    WSSyncherResponse response = callServiceFacade(tokenKey);

                    if (null != response){

                        LogManager.log(LOG_TAG, "Dyna data will be updated in the local DB", Log.DEBUG);

                        int totalPrds = response.getTotalPrds();
                        int totalCstmrs = response.getTotalCstmrs();
                        int totalTsks = response.getTotalTsks();
                        Map<String, String> productsDetails = response.getProductsDetails();
                        Map<String, String> customersDetails = response.getCustomersDetails();
                        Map<String, String> tasksDetails = response.getTasksDetails();
                        List<WSUserEntity> reportStructsList = response.getAllWSUserEntites();

                        LogManager.log(LOG_TAG, "Total Products count : " + totalPrds, Log.DEBUG);
                        LogManager.log(LOG_TAG, "Total Customers count : " + totalCstmrs, Log.DEBUG);
                        LogManager.log(LOG_TAG, "Total Tasks count : " + totalTsks, Log.DEBUG);

                        boolean isDynaDetailsDeleted = dao.flushOldDynaData();

                        byte[] prdDetailsBlob = Utility.serializeObjToForBlob(productsDetails);
                        long dyna_id_prd = dao.saveDynaData(prdDetailsBlob, Constants.DYNATABLE_TYPE_PRODUCT);
                        LogManager.log(LOG_TAG, "Product Details saved successfully", Log.DEBUG);

                        byte[] cstmrDetailsBlob = Utility.serializeObjToForBlob(customersDetails);
                        long dyna_id_cstmr = dao.saveDynaData(cstmrDetailsBlob, Constants.DYNATABLE_TYPE_CUSTOMER);
                        LogManager.log(LOG_TAG, "Customer Details saved successfully", Log.DEBUG);

                        byte[] tskDetailsBlob = Utility.serializeObjToForBlob(tasksDetails);
                        long dyna_id_tsk = dao.saveDynaData(tskDetailsBlob, Constants.DYNATABLE_TYPE_TASK);
                        LogManager.log(LOG_TAG, "Task Details saved successfully", Log.DEBUG);

                        byte[] rptDetailsBlob = Utility.serializeObjToForBlob(reportStructsList);
                        long dyna_id_rpt = dao.saveDynaData(rptDetailsBlob, Constants.DYNATABLE_TYPE_REPORTS);
                        LogManager.log(LOG_TAG, "Report Details saved successfully", Log.DEBUG);

                        LogManager.log(LOG_TAG, "All details are saved in the database successfully.", Log.DEBUG);

                    }

                    //This service will keep running always as to keep the data in sync with the server and should not be stopped


                }
                else{
                    LogManager.log(LOG_TAG, "Connectivity is NOT available, system will try after some time to connect with server.", Log.DEBUG);
                }

                Thread.sleep(threadTimer);

               LogManager.log(LOG_TAG, "Syncher Service sleeping for : " + ConfigConstants.SYNCHER_SERVICE_TIMER , Log.DEBUG);

            }

        }
        catch (Exception ex){
            LogManager.log(LOG_TAG, "Exception occurred in Syncher Service : " + ex.getMessage(), Log.ERROR);
        }

        LogManager.log(LOG_TAG, "Syncher Service is done with sending the data to server...", Log.DEBUG);

    }

    private WSSyncherResponse callServiceFacade(String tokenKey) throws Exception{

        WSSyncherResponse response = null;

        ServicesFacade servicesFacade = new ServicesFacade();

        WSSyncherRequest request = new WSSyncherRequest();
        request.setTokenKey(tokenKey);

        LogManager.log(LOG_TAG, "Calling Syncher Services...", Log.DEBUG);

        WSBaseResponse baseResponse = servicesFacade.callSyncherService(request);

        if (null != baseResponse){

            LogManager.log(LOG_TAG, "Syncher service executed successfully.", Log.DEBUG);

            String status = baseResponse.getStatus();

            if (Utility.safeTrim(status).equals(Integer.toString(HttpURLConnection.HTTP_OK))){

                LogManager.log(LOG_TAG, "Success response returned from Server.", Log.DEBUG);

                response = (WSSyncherResponse)baseResponse;

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
                intent.putExtra(Constants.INTENT_EXTRA_CUSTOM_SERVER_ERROR_OCCURRED, "Syncher Service: Synching with server");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);

            }

        }
        else{
            LogManager.log(LOG_TAG, "Response returned null.", Log.ERROR);
            throw new Exception("Response returned null.");
        }

        return response;

    }


}
