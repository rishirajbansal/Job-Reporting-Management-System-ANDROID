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


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;

import com.jobreporting.business.common.LogManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class SignPadUtility {

    private static final String LOG_TAG = SignPadUtility.class.getSimpleName();


    public static byte[] bitmapToJPG(Bitmap signBitmap) {

        byte[] signFileBytes = null;

        try{
            Bitmap newBitmap = Bitmap.createBitmap(signBitmap.getWidth(), signBitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(newBitmap);
            canvas.drawColor(Color.WHITE);
            canvas.drawBitmap(signBitmap, 0, 0, null);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            newBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
            signFileBytes = stream.toByteArray();
            stream.close();
        }
        catch (IOException ioEx) {
            LogManager.log(LOG_TAG, "IOException occurred during bitmap to jpg coversion for signpad :" + ioEx.getMessage(), Log.ERROR);
        }

        return signFileBytes;

    }


}
