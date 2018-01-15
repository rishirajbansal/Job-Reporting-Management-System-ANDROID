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


import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.OpenableColumns;
import android.util.Log;

import com.jobreporting.business.common.LogManager;

import java.io.InputStream;
import java.util.Map;

public class FileUtility {

    private static final String LOG_TAG = FileUtility.class.getSimpleName();


    public static void getImageByteStream(Context context, Uri uri, Map<String, byte[]> photoBytesDataList){

        String imageName = "";

        try{

            imageName = resolveFileMetaData(context, uri, null, null);

            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            //DataInputStream dataIStream = new DataInputStream(inputStream);

            int totalBytes = inputStream.available();
            byte[] photoBytesData = new byte[totalBytes];
            inputStream.read(photoBytesData, 0, totalBytes);
            LogManager.log(LOG_TAG, "Total Bytes : " + totalBytes, Log.DEBUG);

            photoBytesDataList.put(imageName, photoBytesData);

        }
        catch (Exception ex){
            LogManager.log(LOG_TAG, "Exception occurred while resolving file path : " + ex.getMessage(), Log.ERROR);
        }

        return;

    }

    public static String resolveFilePath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        String filePath = "";

        try{
            /*if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
                if (isExternalStorageDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    if ("primary".equalsIgnoreCase(type)) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    }
                }
                else if (isDownloadsDocument(uri)) {

                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                    return resolveFileMetaData(context, contentUri, null, null);
                }
                else if (isMediaDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }

                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[] { split[1] };

                    return resolveFileMetaData(context, contentUri, selection, selectionArgs);
                }
            }
            else */if ("content".equalsIgnoreCase(uri.getScheme())) {

                if (isGooglePhotosUri(uri)) {
                    filePath =  uri.getLastPathSegment();
                }
                else {
                    filePath = resolveFileMetaData(context, uri, null, null);
                }
            }
            else if ("file".equalsIgnoreCase(uri.getScheme())) {
                filePath = uri.getPath();
            }


        }
        catch (Exception ex){
            LogManager.log(LOG_TAG, "Exception occurred while resolving file path : " + ex.getMessage(), Log.ERROR);
        }

        return filePath;

    }

    public static String resolveFileMetaData(Context context, Uri uri, String selection, String[] selectionArgs) {

        String dataPath = "";
        String displayName = "";

                Cursor cursor = null;
        final String column = "_data";
        final String[] projection = { column };

        try {

            cursor = context.getContentResolver().query(uri, null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                LogManager.log(LOG_TAG, "File Display Name: " + displayName, Log.DEBUG);

                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                String size = null;
                if (!cursor.isNull(sizeIndex)) {
                    size = cursor.getString(sizeIndex);
                }
                else {
                    size = "Unknown";
                }
                LogManager.log(LOG_TAG, "File Size: " + size, Log.DEBUG);

                /*final int index = cursor.getColumnIndexOrThrow(column);
                dataPath = cursor.getString(index);*/
            }

        }
        catch (Exception ex){
            LogManager.log(LOG_TAG, "Exception occurred while resolving meta data : " + ex.getMessage(), Log.ERROR);
        }
        finally {
            if (cursor != null)
                cursor.close();
        }

        return displayName;

    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }


}
