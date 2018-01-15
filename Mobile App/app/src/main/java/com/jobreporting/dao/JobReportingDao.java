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

package com.jobreporting.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.jobreporting.base.Constants;
import com.jobreporting.business.common.LogManager;
import com.jobreporting.dao.dataObjects.ReportData;
import com.jobreporting.exceptions.DataAccessException;
import com.jobreporting.utilities.Utility;

import java.util.List;

public class JobReportingDao {

    private final String LOG_TAG = JobReportingDao.class.getSimpleName();

    private JobReportingDbHelper mDbHelper;
    private SQLiteDatabase db;


    public JobReportingDao(Context context){

        mDbHelper = new JobReportingDbHelper(context);
        db = mDbHelper.getWritableDatabase();

    }

    /*
    * Used in : ReportDispatchSerice, SyncherService, Auth Activity
    */
    public String fetchToken(){

        String tokenKey = "";

        try{

            String[] projection = {JobReportingContract.TokenEntry.COLUMN_TOKEN_KEY};

            Cursor cursor = db.query(
                    JobReportingContract.TokenEntry.TABLE_NAME,
                    projection,
                    null,
                    null,
                    null,
                    null,
                    null
            );

            LogManager.log(LOG_TAG, "(fetchToken) Query : " + cursor.toString(), Log.DEBUG);

            boolean isDataAvailable = cursor.moveToFirst();

            if (isDataAvailable) {

                int columnIndex = cursor.getColumnIndex(JobReportingContract.TokenEntry.COLUMN_TOKEN_KEY);
                tokenKey = cursor.getString(columnIndex);
            }
            else{
                LogManager.log(LOG_TAG, "No records found in Token data table.", Log.DEBUG);
            }

            cursor.close();

        }
        catch (Exception ex){
            LogManager.log(LOG_TAG, "(fetchToken) Exception occurred : " + ex.getMessage(), Log.ERROR);
        }

        return tokenKey;

    }

    public long saveToken(String tokenKey){

        long _id = -1;

        try{
            ContentValues dataSetValues = new ContentValues();
            dataSetValues.put(JobReportingContract.TokenEntry.COLUMN_TOKEN_KEY, tokenKey);
            dataSetValues.put(JobReportingContract.TokenEntry.COLUMN_CREATED_ON, Utility.createDateStringFromPattern(Constants.DATEFORMAT_DB_CREATED_ON));

            _id = db.insert(
                    JobReportingContract.TokenEntry.TABLE_NAME,
                    null,
                    dataSetValues
            );

            if (_id > 0){
                LogManager.log(LOG_TAG, "(saveToken) Id generated: " + _id, Log.DEBUG);
            }
            else {
                throw new DataAccessException("Failed to insert record for token.");
            }

        }
        catch (DataAccessException daEx){
            throw daEx;
        }
        catch (SQLException sqlEx){
            LogManager.log(LOG_TAG, "(saveToken) Exception occurred : " + sqlEx.getMessage(), Log.ERROR);
            throw new DataAccessException("saveToken() -> SQLException occurred in DAO layer : " + sqlEx.getMessage());
        }

        return _id;

    }

    public boolean flushOldToken(){

        boolean isDeleted = false;

        try{
            String whereClause = "1"; //To the count of deleted rows, 1 is passed here
            String[] whereArgs = null;

            int count = db.delete(
                    JobReportingContract.TokenEntry.TABLE_NAME,
                    whereClause,
                    whereArgs
            );

            LogManager.log(LOG_TAG, "(flushOldDynaData) No. of rows deleted: " + count, Log.DEBUG);

            isDeleted = true;

        }
        catch (DataAccessException daEx){
            throw daEx;
        }
        catch (SQLException sqlEx){
            LogManager.log(LOG_TAG, "(flushOldDynaData) Exception occurred : " + sqlEx.getMessage(), Log.ERROR);
            throw new DataAccessException("flushOldDynaData() -> SQLException occurred in DAO layer : " + sqlEx.getMessage());
        }

        return isDeleted;

    }

    public long saveDynaData(byte[] blobBytes, String dynaType){

        long _id = -1;

        try{
            ContentValues dataSetValues = new ContentValues();
            dataSetValues.put(JobReportingContract.DynaFieldsEntry.COLUMN_DYNA_TYPE, dynaType);
            dataSetValues.put(JobReportingContract.DynaFieldsEntry.COLUMN_DYNA_DATA, blobBytes);
            dataSetValues.put(JobReportingContract.DynaFieldsEntry.COLUMN_CREATED_ON, Utility.createDateStringFromPattern(Constants.DATEFORMAT_DB_CREATED_ON));

            _id = db.insert(
                    JobReportingContract.DynaFieldsEntry.TABLE_NAME,
                    null,
                    dataSetValues
            );

            if (_id > 0){
                LogManager.log(LOG_TAG, "(saveDynaData) Id generated: " + _id, Log.DEBUG);
            }
            else {
                throw new DataAccessException("Failed to insert record for dyna data.");
            }
        }
        catch (DataAccessException daEx){
            throw daEx;
        }
        catch (SQLException sqlEx){
            LogManager.log(LOG_TAG, "(saveDynaData) Exception occurred : " + sqlEx.getMessage(), Log.ERROR);
            throw new DataAccessException("saveDynaData() -> SQLException occurred in DAO layer : " + sqlEx.getMessage());
        }

        return _id;

    }

    public boolean flushOldDynaData(){

        boolean isDeleted = false;

        try{
            String whereClause = "1"; //To the count of deleted rows, 1 is passed here
            String[] whereArgs = null;

            int count = db.delete(
                    JobReportingContract.DynaFieldsEntry.TABLE_NAME,
                    whereClause,
                    whereArgs
            );

            LogManager.log(LOG_TAG, "(flushOldDynaData) No. of rows deleted: " + count, Log.DEBUG);

            isDeleted = true;

        }
        catch (DataAccessException daEx){
            throw daEx;
        }
        catch (SQLException sqlEx){
            LogManager.log(LOG_TAG, "(flushOldDynaData) Exception occurred : " + sqlEx.getMessage(), Log.ERROR);
            throw new DataAccessException("flushOldDynaData() -> SQLException occurred in DAO layer : " + sqlEx.getMessage());
        }

        return isDeleted;

    }

    public byte[] fetchDynaData(String dynaType){

        byte[] data = null;

        try{
            String[] projection = {JobReportingContract.DynaFieldsEntry.COLUMN_DYNA_DATA};
            String whereClause = JobReportingContract.DynaFieldsEntry.COLUMN_DYNA_TYPE + " = ?";
            String[] whereArgs = { dynaType };

            Cursor cursor = db.query(
                    JobReportingContract.DynaFieldsEntry.TABLE_NAME,
                    projection,
                    whereClause,
                    whereArgs,
                    null,
                    null,
                    null
            );

            LogManager.log(LOG_TAG, "(fetchDynaData) Query : " + cursor.toString(), Log.DEBUG);

            boolean isDataAvailable = cursor.moveToFirst();

            if (isDataAvailable) {

                int columnIndex = cursor.getColumnIndex(JobReportingContract.DynaFieldsEntry.COLUMN_DYNA_DATA);
                data = cursor.getBlob(columnIndex);

            }
            else{
                LogManager.log(LOG_TAG, "No records found in dyna data table.", Log.ERROR);
                throw new DataAccessException("No records found in dyna data table.");
            }

            cursor.close();

        }
        catch (DataAccessException daEx){
            throw daEx;
        }
        catch (SQLException sqlEx){
            LogManager.log(LOG_TAG, "(fetchDynaData) Exception occurred : " + sqlEx.getMessage(), Log.ERROR);
            throw new DataAccessException("fetchDynaData() -> SQLException occurred in DAO layer : " + sqlEx.getMessage());
        }

        return data;

    }

    public long saveReportData(String reportData, byte[] photoBytesData, String photoImageName, byte[] signature){

        long _id = -1;

        try{
            ContentValues dataSetValues = new ContentValues();
            dataSetValues.put(JobReportingContract.ReportDataEntry.COLUMN_DATA, reportData);
            dataSetValues.put(JobReportingContract.ReportDataEntry.COLUMN_PHOTO, photoBytesData);
            dataSetValues.put(JobReportingContract.ReportDataEntry.COLUMN_PHOTO_NAME, photoImageName);
            dataSetValues.put(JobReportingContract.ReportDataEntry.COLUMN_SIGN, signature);
            dataSetValues.put(JobReportingContract.ReportDataEntry.COLUMN_STATUS, Constants.REPORTDATAENTRY_STATUS_NOT_SAVED);
            dataSetValues.put(JobReportingContract.ReportDataEntry.COLUMN_CREATED_ON, Utility.createDateStringFromPattern(Constants.DATEFORMAT_DB_CREATED_ON));

            _id = db.insert(
                    JobReportingContract.ReportDataEntry.TABLE_NAME,
                    null,
                    dataSetValues
            );

            if (_id > 0){
                LogManager.log(LOG_TAG, "(saveReportData) Id generated: " + _id, Log.DEBUG);
            }
            else {
                throw new DataAccessException("Failed to insert record for report data.");
            }
        }
        catch (DataAccessException daEx){
            throw daEx;
        }
        catch (SQLException sqlEx){
            LogManager.log(LOG_TAG, "(saveReportData) Exception occurred : " + sqlEx.getMessage(), Log.ERROR);
            throw new DataAccessException("saveReportData() -> SQLException occurred in DAO layer : " + sqlEx.getMessage());
        }

        return _id;

    }

    public boolean fetchAllReportData(List<ReportData> dataList){

        boolean isDataAvailable = false;

        try{

            String[] projection = {JobReportingContract.ReportDataEntry._ID, JobReportingContract.ReportDataEntry.COLUMN_DATA, JobReportingContract.ReportDataEntry.COLUMN_PHOTO, JobReportingContract.ReportDataEntry.COLUMN_PHOTO_NAME, JobReportingContract.ReportDataEntry.COLUMN_SIGN};
            String whereClause = JobReportingContract.ReportDataEntry.COLUMN_STATUS + " = ?";
            String[] whereArgs = { Constants.REPORTDATAENTRY_STATUS_NOT_SAVED };
            String sortOrder = JobReportingContract.ReportDataEntry.COLUMN_CREATED_ON + " ASC";

            Cursor reportCursor = db.query(
                    JobReportingContract.ReportDataEntry.TABLE_NAME,
                    projection,
                    whereClause,
                    whereArgs,
                    null,
                    null,
                    sortOrder
            );

            LogManager.log(LOG_TAG, "(fetchAllReportData) Query : " + reportCursor.toString(), Log.DEBUG);

            isDataAvailable = reportCursor.moveToFirst();

            if (isDataAvailable) {

                do {
                    ReportData reportData = new ReportData();

                    int columnIndex = reportCursor.getColumnIndex(JobReportingContract.ReportDataEntry.COLUMN_DATA);
                    String data = reportCursor.getString(columnIndex);
                    reportData.setData(data);

                    columnIndex = reportCursor.getColumnIndex(JobReportingContract.ReportDataEntry.COLUMN_PHOTO);
                    byte[] photo = reportCursor.getBlob(columnIndex);
                    reportData.setPhoto(photo);

                    columnIndex = reportCursor.getColumnIndex(JobReportingContract.ReportDataEntry.COLUMN_PHOTO_NAME);
                    String photoImageName = reportCursor.getString(columnIndex);
                    reportData.setPhotoName(photoImageName);

                    columnIndex = reportCursor.getColumnIndex(JobReportingContract.ReportDataEntry.COLUMN_SIGN);
                    byte[] sign = reportCursor.getBlob(columnIndex);
                    reportData.setSign(sign);

                    columnIndex = reportCursor.getColumnIndex(JobReportingContract.ReportDataEntry._ID);
                    int id = reportCursor.getInt(columnIndex);
                    reportData.setId(id);

                    dataList.add(reportData);
                }
                while (reportCursor.moveToNext());

            }
            else{
                LogManager.log(LOG_TAG, "No records found in Report data table.", Log.DEBUG);
            }

            reportCursor.close();

        }
        catch (Exception ex){
            LogManager.log(LOG_TAG, "(fetchAllReportData) Exception occurred : " + ex.getMessage(), Log.ERROR);
        }

        return isDataAvailable;

    }

    public boolean updateDispatchedReportStatus(int id){

        boolean isUpdated = false;

        try{
            ContentValues dataSetValues = new ContentValues();
            dataSetValues.put(JobReportingContract.ReportDataEntry.COLUMN_STATUS, Constants.REPORTDATAENTRY_STATUS_SAVED);

            String whereClause = JobReportingContract.ReportDataEntry._ID + " = ?";
            String[] whereArgs = { Integer.toString(id) };

            int count = db.update(
                    JobReportingContract.ReportDataEntry.TABLE_NAME,
                    dataSetValues,
                    whereClause,
                    whereArgs
            );

            if (count > 0){
                LogManager.log(LOG_TAG, "(updateDispatchedReportStatus) Report data status updated in the records. ", Log.DEBUG);

                isUpdated = true;
            }
            else{
                LogManager.log(LOG_TAG, "(updateDispatchedReportStatus) Failed to update the data in records. ", Log.ERROR);
            }

        }
        catch (Exception ex){
            LogManager.log(LOG_TAG, "(updateDispatchedReportStatus) Exception occurred : " + ex.getMessage(), Log.ERROR);
        }

        return isUpdated;

    }




}
