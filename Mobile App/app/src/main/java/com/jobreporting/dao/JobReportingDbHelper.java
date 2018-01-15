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


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.jobreporting.dao.JobReportingContract.DynaFieldsEntry;
import com.jobreporting.dao.JobReportingContract.ReportDataEntry;
import com.jobreporting.dao.JobReportingContract.TokenEntry;


public class JobReportingDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "jobreporting.db";


    public JobReportingDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_TOKEN_TABLE = "CREATE TABLE " + TokenEntry.TABLE_NAME + " (" +
                TokenEntry._ID + " INTEGER PRIMARY KEY," +
                TokenEntry.COLUMN_TOKEN_KEY + " TEXT NOT NULL, " +
                TokenEntry.COLUMN_CREATED_ON + " TEXT " +
                " );";

        final String SQL_CREATE_DYNAFIELDS_TABLE = "CREATE TABLE " + DynaFieldsEntry.TABLE_NAME + " (" +
                DynaFieldsEntry._ID + " INTEGER PRIMARY KEY," +
                DynaFieldsEntry.COLUMN_DYNA_TYPE + " TEXT NOT NULL, " +
                DynaFieldsEntry.COLUMN_DYNA_DATA + " BLOB NOT NULL, " +
                DynaFieldsEntry.COLUMN_CREATED_ON + " TEXT " +
                " );";

        final String SQL_CREATE_REPORTDATA_TABLE = "CREATE TABLE " + ReportDataEntry.TABLE_NAME + " (" +
                ReportDataEntry._ID + " INTEGER PRIMARY KEY," +
                ReportDataEntry.COLUMN_DATA + " TEXT NOT NULL, " +
                ReportDataEntry.COLUMN_PHOTO + " BLOB, " +
                ReportDataEntry.COLUMN_PHOTO_NAME + " TEXT, " +
                ReportDataEntry.COLUMN_SIGN + " BLOB, " +
                ReportDataEntry.COLUMN_STATUS + " INTEGER NOT NULL, " +
                ReportDataEntry.COLUMN_CREATED_ON + " TEXT " +
                " );";


        sqLiteDatabase.execSQL(SQL_CREATE_TOKEN_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_DYNAFIELDS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_REPORTDATA_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TokenEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DynaFieldsEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ReportDataEntry.TABLE_NAME);

        onCreate(sqLiteDatabase);

    }
}
