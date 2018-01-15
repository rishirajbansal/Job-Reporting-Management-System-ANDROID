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


import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.jobreporting.base.Constants;
import com.jobreporting.business.common.LogManager;
import com.jobreporting.exceptions.ApplicationException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Utility {

    private static final String LOG_TAG = Utility.class.getSimpleName();


    public static String safeTrim(String s) {
        if ((s == null) || s.equals("null")) {
            return "";
        }
        else {
            return s.trim();
        }
    }

    public static byte[] serializeObjToForBlob(Object obj){

        byte[] blobBytes = null;

        try{
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
            oos.flush();
            baos.close();
            oos.close();

            blobBytes = baos.toByteArray();
        }
        catch(IOException ioEx){
            throw new ApplicationException("IOException exception occurred in serializeObjToForBlob" + ioEx.getMessage());
        }

        return blobBytes;

    }

    public static Object deSerializeObjToForBlob(byte[] blobBytes){

        Object obj = null;

        try{
            ByteArrayInputStream bais = new ByteArrayInputStream(blobBytes);
            ObjectInputStream oInStream = new ObjectInputStream(bais);
            obj = (Object)oInStream.readObject();
            oInStream.close();
            bais.close();

        }
        catch(ClassNotFoundException cnfEx){
            LogManager.log(LOG_TAG, "ClassNotFoundException exception occurred in deSerializeObjToForBlob" + cnfEx.getMessage(), Log.ERROR);
            throw new ApplicationException("ClassNotFoundException exception occurred in deSerializeObjToForBlob" + cnfEx.getMessage());
        }
        catch(IOException ioEx){
            LogManager.log(LOG_TAG, "IOException exception occurred in deSerializeObjToForBlob" + ioEx.getMessage(), Log.ERROR);
            throw new ApplicationException("IOException exception occurred in deSerializeObjToForBlob" + ioEx.getMessage());
        }

        return obj;

    }

    public static String createDateStringFromPattern(String pattern){

        String dateStr = "";

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        dateStr = simpleDateFormat.format(new Date());

        return dateStr;

    }

    public static String get12HourFormatTime(int hour, int minute){

        String formattedTime = "";

        if (hour < 12 ){
            String hrStr = Integer.toString(hour);
            if (hour <= 9){
                hrStr = "0" + hour;
            }
            String minStr = Integer.toString(minute);
            if (minute <= 9){
                minStr = "0" + minute;
            }
            formattedTime = hrStr + Constants.TIME_CONTROL_SEPERATOR_UI + minStr + Constants.TIME_CONTROL_SEPERATOR_UI + "00" + " " + "AM";
        }
        else if (hour == 12 ){
            String hrStr = Integer.toString(hour);
            String minStr = Integer.toString(minute);
            if (minute <= 9){
                minStr = "0" + minute;
            }
            formattedTime = hrStr + Constants.TIME_CONTROL_SEPERATOR_UI + minStr + Constants.TIME_CONTROL_SEPERATOR_UI + "00" + " " + "PM";
        }
        else{
            int pmHour = hour - 12;

            String hrStr = Integer.toString(pmHour);
            if (pmHour <= 9){
                hrStr = "0" + pmHour;
            }
            String minStr = Integer.toString(minute);
            if (minute <= 9){
                minStr = "0" + minute;
            }

            formattedTime = hrStr + Constants.TIME_CONTROL_SEPERATOR_UI + minStr + Constants.TIME_CONTROL_SEPERATOR_UI + "00" + " " + "PM";
        }

        return formattedTime;

    }

    @TargetApi(21)
    public static void setStatusBarColor(Activity activity){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = activity.getWindow();

            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            window.setStatusBarColor(Color.parseColor("#455A64"));
        }
    }

    public static void resetForm(ViewGroup group) {

        for (int i = 0, count = group.getChildCount(); i < count; ++i) {
            View view = group.getChildAt(i);

            if (view instanceof EditText) {
                ((EditText)view).setText("");
            }

            if (view instanceof RadioGroup) {
                ((RadioButton)((RadioGroup) view).getChildAt(0)).setChecked(true);
            }

            if (view instanceof Spinner) {
                ((Spinner) view).setSelection(0);
            }

            if (view instanceof CheckBox) {
                ((CheckBox) view).setChecked(false);
            }

            if (view instanceof ViewGroup && (((ViewGroup)view).getChildCount() > 0)) {
                resetForm((ViewGroup)view);
            }

        }

    }

    public static void checkErrors(ViewGroup group, List<String> isErrors) {

        for (int i = 0, count = group.getChildCount(); i < count; ++i) {
            View view = group.getChildAt(i);

            if (view instanceof EditText) {
                if (null != ((EditText)view).getError()){
                    isErrors.set(0, "NOT_OK");
                }
            }

            if (view instanceof ViewGroup && (((ViewGroup)view).getChildCount() > 0)) {
                checkErrors((ViewGroup)view, isErrors);
            }

        }

        return;

    }

}
