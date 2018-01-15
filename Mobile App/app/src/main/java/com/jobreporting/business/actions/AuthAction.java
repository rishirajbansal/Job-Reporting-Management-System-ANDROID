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

package com.jobreporting.business.actions;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;

import com.jobreporting.R;
import com.jobreporting.base.Constants;
import com.jobreporting.business.ApplicationInitializer;
import com.jobreporting.business.common.AppDeviceManager;
import com.jobreporting.business.common.LogManager;
import com.jobreporting.business.facade.ServicesFacade;
import com.jobreporting.dao.JobReportingDao;
import com.jobreporting.entities.WSDeviceAuth;
import com.jobreporting.entities.WSUserEntity;
import com.jobreporting.exceptions.ServicesFacadeException;
import com.jobreporting.requests.WSOneTimeAuthRequest;
import com.jobreporting.responses.WSBaseResponse;
import com.jobreporting.responses.WSErrorResponse;
import com.jobreporting.responses.WSOneTimeAuthResponse;
import com.jobreporting.utilities.ServiceUtility;
import com.jobreporting.utilities.Utility;
import com.jobreporting.views.ServerErrorActivity;

import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

public class AuthAction {

    private final String LOG_TAG = AuthAction.class.getSimpleName();

    public static Context context = null;
    public static Activity authActivity = null;

    JobReportingDao dao = null;

    ServicesFacade facade = new ServicesFacade();

    private String orgName;

    private String wrkName;


    public AuthAction(Context context, Activity authActivity){

        this.context = context;
        this.authActivity = authActivity;
        dao = new JobReportingDao(this.context);

    }

    public void execute(){

        try{
            //Check if connectivity is available
            boolean isConnected = ServiceUtility.checkConnectivity(this.context);

            if (isConnected) {
                LogManager.log(LOG_TAG, "Connectivity is available, system will try to connect with server to authenticate the data.", Log.DEBUG);

                //Call AsyncTask to send the info to server and get the token key in return. This key will be saved in the file.
                OneTimeAuthTask oneTimeAuthTask = new OneTimeAuthTask(orgName, wrkName);
                oneTimeAuthTask.execute();

            }
            else{
                LogManager.log(LOG_TAG, "Connectivity is NOT available.", Log.DEBUG);
                showConnectivityNotAvailableAlertMessage();
            }

        }
        catch(ServicesFacadeException sfEx){
            LogManager.log(LOG_TAG, "ServicesFacadeException occurred : " + sfEx.getMessage(), Log.ERROR);
        }
        catch (Exception ex){
            LogManager.log(LOG_TAG, "Exception occurred : " + ex.getMessage(), Log.ERROR);
        }
        catch (Throwable th){
            LogManager.log(LOG_TAG, "Throwable occurred : " + th.getMessage(), Log.ERROR);
        }

    }

    private class OneTimeAuthTask extends AsyncTask<Void, Integer, WSBaseResponse> {

        private String orgName = null;
        private String wrkName = null;

        private boolean isErrorResponse = false;

        ProgressBar progressBar = (ProgressBar)authActivity.findViewById(R.id.progressBar_auth);

        ProgressDialog progressDialog = new ProgressDialog(context);

        public OneTimeAuthTask(String orgName, String wrkName){
            this.orgName = orgName;
            this.wrkName = wrkName;

            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(context.getResources().getString(R.string.progress_auth));
            progressDialog.show();

        }

        @Override
        protected WSBaseResponse doInBackground(Void... params) {

            WSBaseResponse baseResponse = null;

            try{
                AppDeviceManager dManager = AppDeviceManager.getAppDeviceManager();

                WSDeviceAuth deviceAuth = new WSDeviceAuth();
                deviceAuth.setDeviceId(dManager.getDeviceId());
                deviceAuth.setAndroidId(dManager.getAndroidId());

                WSOneTimeAuthRequest request = new WSOneTimeAuthRequest();
                request.setOrgName(orgName);
                request.setWorkerName(wrkName);
                request.setDeviceAuth(deviceAuth);

                LogManager.log(LOG_TAG, "Calling Services...", Log.DEBUG);

                baseResponse = facade.callOneTimeAuthService(request);
                if (null != baseResponse){

                    LogManager.log(LOG_TAG, "One Time Auth service executed successfully.", Log.DEBUG);

                    String status = baseResponse.getStatus();

                    if (Utility.safeTrim(status).equals(Integer.toString(HttpURLConnection.HTTP_OK))){

                        LogManager.log(LOG_TAG, "Success response returned from Server.", Log.DEBUG);
                        WSOneTimeAuthResponse response = (WSOneTimeAuthResponse)baseResponse;

                        LogManager.log(LOG_TAG, "Saving the data in the database...", Log.DEBUG);

                        String tokenKey = response.getTokenKey();
                        Map<String, String> productsDetails = response.getProductsDetails();
                        List<WSUserEntity> reportStructsList = response.getAllWSUserEntites();

                        //Save Token
                        boolean isTokenDeleted = dao.flushOldToken();
                        long _id = dao.saveToken(tokenKey);
                        LogManager.log(LOG_TAG, "Token saved successfully", Log.DEBUG);

                        //Save Dyna Details
                        boolean isDynaDetailsDeleted = dao.flushOldDynaData();

                        byte[] prdDetailsBlob = Utility.serializeObjToForBlob(productsDetails);
                        long dyna_id_prd = dao.saveDynaData(prdDetailsBlob, Constants.DYNATABLE_TYPE_PRODUCT);
                        LogManager.log(LOG_TAG, "Product Details saved successfully", Log.DEBUG);

                        byte[] rptDetailsBlob = Utility.serializeObjToForBlob(reportStructsList);
                        long dyna_id_rpt = dao.saveDynaData(rptDetailsBlob, Constants.DYNATABLE_TYPE_REPORTS);
                        LogManager.log(LOG_TAG, "Report Details saved successfully", Log.DEBUG);

                        LogManager.log(LOG_TAG, "All details are saved in the database successfully.", Log.DEBUG);

                    }
                    else{
                        LogManager.log(LOG_TAG, "ERROR response returned from Server.", Log.DEBUG);
                        isErrorResponse = true;
                    }

                }
                else{
                    LogManager.log(LOG_TAG, "Response returned null.", Log.ERROR);
                }
            }
            catch(ServicesFacadeException sfEx){
                LogManager.log(LOG_TAG, "ServicesFacadeException occurred : " + sfEx.getMessage(), Log.ERROR);
                isErrorResponse = true;
            }

            return baseResponse;

        }

        @Override
        protected void onPostExecute(WSBaseResponse response) {

            if (isErrorResponse){

                if (null != response){
                    WSErrorResponse errorResponse = (WSErrorResponse)response;
                    String code = errorResponse.getCode();
                    String message = errorResponse.getUserMessage();
                    String errorMessage = errorResponse.getErrorMessage();

                    LogManager.log(LOG_TAG, "Error Message from server : " + errorMessage, Log.DEBUG);

                    //Forward the redirection to Server Error Activity
                    Intent intent = new Intent(AuthAction.context, ServerErrorActivity.class);
                    intent.putExtra(Constants.INTENT_EXTRA_CUSTOM_SERVER_ERROR_CODE, code);
                    intent.putExtra(Constants.INTENT_EXTRA_CUSTOM_SERVER_MESSAGE, message);
                    intent.putExtra(Constants.INTENT_EXTRA_CUSTOM_SERVER_ERROR, errorMessage);
                    intent.putExtra(Constants.INTENT_EXTRA_CUSTOM_SERVER_ERROR_OCCURRED, "Auth Async Task: Authenticating with server");
                    AuthAction.context.startActivity(intent);
                }
                else{
                    //Display the alert box message
                    ServiceUtility.showErrorAlertMessage(context, context.getResources().getString(R.string.not_connect_server_title_auth), context.getResources().getString(R.string.not_connect_server_message_auth));
                }

            }
            else{
                //Initialize the application and redirect to Home screen
                ApplicationInitializer applicationInitializer = ApplicationInitializer.igniteInitializer(context);
                applicationInitializer.redirectToHome(false);

                //End Auth activity so that user should not come back to this by 'Back' navigation from phone,
                //Finishing it here as to prevent the closing of the auth screen before home screen appears
                authActivity.finish();
            }

            progressDialog.dismiss();
            LogManager.log(LOG_TAG, "Done Post Execute.", Log.DEBUG);

        }

    }

    public void showConnectivityNotAvailableAlertMessage() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        alertDialog.setTitle(context.getText(R.string.no_connectivity_title_alert_auth));

        alertDialog.setMessage(context.getText(R.string.no_connectivity_message_alert_auth));

        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();

    }

    public String getWrkName() {

        return wrkName;
    }

    public void setWrkName(String wrkName) {

        this.wrkName = wrkName;
    }

    public String getOrgName() {

        return orgName;
    }

    public void setOrgName(String orgName) {

        this.orgName = orgName;
    }
}
