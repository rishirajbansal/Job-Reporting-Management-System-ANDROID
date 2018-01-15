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

package com.jobreporting.business.facade;


import android.util.Log;

import com.jobreporting.base.ConfigConstants;
import com.jobreporting.business.common.LogManager;
import com.jobreporting.exceptions.ServicesFacadeException;
import com.jobreporting.requests.WSBaseRequest;
import com.jobreporting.responses.WSBaseResponse;
import com.jobreporting.utilities.ServiceUtility;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ServicesFacade {

    private final String LOG_TAG = ServicesFacade.class.getSimpleName();


    public ServicesFacade(){

    }

    public Object connectService(Object request, String service){

        Object response = null;

        try{

            byte[] requestBStream = ServiceUtility.serializeObjToByteStream(request);

            HttpURLConnection urlConnection = null;
            URL url = new URL(ConfigConstants.SERVICE_BASE_URL + service);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "*/*");

            urlConnection.getOutputStream().write(requestBStream);

            int responseCode = urlConnection.getResponseCode();
            LogManager.log(LOG_TAG, "Response Code : " + responseCode, Log.DEBUG);

            InputStream inputStream = urlConnection.getInputStream();

            ObjectInputStream oinStream = new ObjectInputStream(inputStream);
            response = oinStream.readObject();
            oinStream.close();
            inputStream.close();

        }
        catch(Exception ex){
            throw new ServicesFacadeException("Exception occurred while calling the service : " + ex.getMessage());
        }

        return response;

    }

    public WSBaseResponse callOneTimeAuthService(WSBaseRequest request){

        WSBaseResponse baseResponse = null;

        try{

            Object obj = connectService(request, ConfigConstants.SERVICE_ONETIMEAUTH);
            if (obj instanceof WSBaseResponse){

                baseResponse = (WSBaseResponse)obj;
            }
            else{
                throw new ServicesFacadeException("(callOneTimeAuthService) Invalid Response returned by the server, not a signed class");
            }

        }
        catch(Exception ex){
            throw new ServicesFacadeException("(callOneTimeAuthService) Exception occurred while calling the service : " + ex.getMessage());
        }

        return baseResponse;

    }

    public WSBaseResponse callSyncherService(WSBaseRequest request){

        WSBaseResponse baseResponse = null;

        try{

            Object obj = connectService(request, ConfigConstants.SERVICE_SYNCHER);
            if (obj instanceof WSBaseResponse){

                baseResponse = (WSBaseResponse)obj;
            }
            else{
                throw new ServicesFacadeException("(callSyncherService) Invalid Response returned by the server, not a signed class");
            }

        }
        catch(Exception ex){
            throw new ServicesFacadeException("(callSyncherService) Exception occurred while calling the service : " + ex.toString());
        }

        return baseResponse;

    }

    public WSBaseResponse callReportsService(WSBaseRequest request){

        WSBaseResponse baseResponse = null;

        try{

            Object obj = connectService(request, ConfigConstants.SERVICE_REPORT);
            if (obj instanceof WSBaseResponse){

                baseResponse = (WSBaseResponse)obj;
            }
            else{
                throw new ServicesFacadeException("(callReportsService) Invalid Response returned by the server, not a signed class");
            }

        }
        catch(Exception ex){
            throw new ServicesFacadeException("(callReportsService) Exception occurred while calling the service : " + ex.getMessage());
        }

        return baseResponse;

    }


}
