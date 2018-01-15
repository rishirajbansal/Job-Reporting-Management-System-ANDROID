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


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.jobreporting.base.Constants;
import com.jobreporting.base.ERequestType;
import com.jobreporting.business.common.LogManager;
import com.jobreporting.business.services.ReportDispatchService;
import com.jobreporting.dao.JobReportingDao;
import com.jobreporting.entities.WSUserEntity;
import com.jobreporting.utilities.Utility;

import java.util.ArrayList;
import java.util.List;

public class ReportAction {

    private final String LOG_TAG = ReportAction.class.getSimpleName();

    public static Context context = null;

    JobReportingDao dao = null;

    private List<WSUserEntity> reportDynaDetails = new ArrayList<>();

    private String postedIdValues = "";

    private byte[] photoBytesData = null;
    private String photoImageName = null;
    private byte[] signFileBytes = null;


    public ReportAction(Context context){

        this.context = context;
        dao = new JobReportingDao(this.context);

    }

    public void execute(ERequestType requestType){

        try{
            switch(requestType){

                case REPORT_GET_DYNADATA:
                    exe_retrieveReportsDynaData();
                    break;

                case SAVE_REPORT:
                    exe_saveReport();
                    break;

                default:
                    throw new Exception("Unsupported request type.");

            }

        }
        catch (Exception ex){
            LogManager.log(LOG_TAG, "ReportAction->Exception occurred : " + ex.getMessage(), Log.ERROR);
        }
        catch (Throwable th){
            LogManager.log(LOG_TAG, "ReportAction->Throwable occurred : " + th.getMessage(), Log.ERROR);
        }

    }

    public void exe_retrieveReportsDynaData() throws Exception{

        byte[] rptDetailsBlob = dao.fetchDynaData(Constants.DYNATABLE_TYPE_REPORTS);

        if (null != rptDetailsBlob){
            Object obj = Utility.deSerializeObjToForBlob(rptDetailsBlob);
            if (obj instanceof List){
                reportDynaDetails = (List<WSUserEntity>)obj;

                LogManager.log(LOG_TAG, "Total no. of products found: " + reportDynaDetails.size(), Log.DEBUG);

                this.setReportDynaDetails(reportDynaDetails);

            }
            else{
                throw new Exception("Invalid object type returned for Report dyna details.");
            }

        }
        else{
            throw new Exception("Not able to receive the dyna data for Report");
        }

    }

    public void exe_saveReport() throws Exception{

        String reportData = getPostedIdValues();

        if (Utility.safeTrim(reportData).equals(Constants.EMPTY_STRING)){
            LogManager.log(LOG_TAG, "Report data is empty, no need to call Report Service", Log.DEBUG);
            return;
        }

        //Save report in database
        long _id = dao.saveReportData(reportData, photoBytesData, photoImageName, signFileBytes);
        LogManager.log(LOG_TAG, "Report data saved successfully", Log.DEBUG);

        //Call Service
        LogManager.log(LOG_TAG, "Registering the report data dispatch request to the service.", Log.DEBUG);

        Intent intent = new Intent(context, ReportDispatchService.class);

        /* If this service is not already running, it will be instantiated and started (creating a process for it if needed);
        if it is running then it remains running. */
        ComponentName service = ReportAction.context.startService(intent);
        LogManager.log(LOG_TAG, "Report Dispatch Service called : " + service.getShortClassName(), Log.DEBUG);


    }


    public List<WSUserEntity> getReportDynaDetails() {

        return reportDynaDetails;
    }

    public void setReportDynaDetails(List<WSUserEntity> reportDynaDetails) {
        this.reportDynaDetails = reportDynaDetails;
    }

    public String getPostedIdValues() {

        return postedIdValues;
    }

    public void setPostedIdValues(String postedIdValues) {

        this.postedIdValues = postedIdValues;
    }

    public byte[] getPhotoBytesData() {

        return photoBytesData;
    }

    public void setPhotoBytesData(byte[] photoBytesData) {

        this.photoBytesData = photoBytesData;
    }

    public String getPhotoImageName() {

        return photoImageName;
    }

    public void setPhotoImageName(String photoImageName) {

        this.photoImageName = photoImageName;
    }

    public byte[] getSignFileBytes() {
        return signFileBytes;
    }

    public void setSignFileBytes(byte[] signFileBytes) {
        this.signFileBytes = signFileBytes;
    }
}
