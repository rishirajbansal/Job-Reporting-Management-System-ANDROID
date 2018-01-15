/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jobreporting.dao;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

public class JobReportingProvider extends ContentProvider {

    private final String LOG_TAG = JobReportingProvider.class.getSimpleName();

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private JobReportingDbHelper mDbHelper;

    static final int DYNAFIELDS = 100;
    static final int REPORTDATA = 200;


    static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = JobReportingContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, JobReportingContract.PATH_DYNAFIELDS, DYNAFIELDS);

        matcher.addURI(authority, JobReportingContract.PATH_REPORTDATA, REPORTDATA);

        return matcher;
    }


    @Override
    public boolean onCreate() {
        mDbHelper = new JobReportingDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);

        switch (match) {
            case DYNAFIELDS:
                return JobReportingContract.DynaFieldsEntry.CONTENT_TYPE;
            case REPORTDATA:
                return JobReportingContract.ReportDataEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        Cursor retCursor;

        switch (sUriMatcher.match(uri)) {

            case DYNAFIELDS: {
                retCursor = mDbHelper.getReadableDatabase().query(
                        JobReportingContract.DynaFieldsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            case REPORTDATA: {
                retCursor = mDbHelper.getReadableDatabase().query(
                        JobReportingContract.ReportDataEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;

    }



}
