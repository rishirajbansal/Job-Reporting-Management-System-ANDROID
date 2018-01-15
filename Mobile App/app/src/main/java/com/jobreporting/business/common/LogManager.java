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

package com.jobreporting.business.common;

import android.os.Environment;
import android.util.Log;

import com.jobreporting.base.ConfigConstants;
import com.jobreporting.base.Constants;
import com.jobreporting.utilities.Utility;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class LogManager {

    private final static String LOG_TAG = LogManager.class.getSimpleName();

    private static String fileName = null;
    private static File logFile = null;

    static {
        configure();
    }

    public LogManager(){

    }

    private static void configure() {

        final File path = new File(Environment.getExternalStorageDirectory(), Constants.LOG_DIR);
        if (!path.exists()) {
            path.mkdir();
        }

        fileName = path + "/" + Constants.LOG_FILE;
        logFile = new File(fileName);

        Log.d(LOG_TAG, "LogManager Initialized.");
    }

    private static void checkFileSize(){

        long size = logFile.length();
        if (size > Constants.LOG_FILE_MAX_SIZE * 1024 * 1024){
            logFile.delete();
        }

    }

    public static void log(String logTag, String text, int logLevel){

        StringBuffer logMsg = new StringBuffer();

        try
        {
            boolean logOutFile = ConfigConstants.LOG_OUTPUT_FILE;
            if (logOutFile){
                checkFileSize();

                String logtime = Utility.createDateStringFromPattern(Constants.DATEFORMAT_LOGGING);

                logMsg.append(logtime);
                logMsg.append("    PID: ").append(android.os.Process.myPid());
                logMsg.append("    TID: ").append(android.os.Process.myTid());
                switch (logLevel){
                    case Log.DEBUG:
                        logMsg.append("   DEBUG ").append(logTag);
                        break;

                    case Log.INFO:
                        logMsg.append("   INFO ").append(logTag);
                        break;

                    case Log.ERROR:
                        logMsg.append("   ERROR ").append(logTag);
                        break;

                    case Log.WARN:
                        logMsg.append("   WARN ").append(logTag);
                        break;

                    default:
                        break;
                }
                logMsg.append("  -> ").append(text);

                //BufferedWriter for performance
                BufferedWriter bfWrite = new BufferedWriter(new FileWriter(fileName, true));
                bfWrite.append(logMsg);
                bfWrite.newLine();
                bfWrite.close();
            }

            switch (logLevel){
                case Log.DEBUG:
                    Log.d(logTag, text);
                    break;

                case Log.INFO:
                    Log.i(logTag, text);
                    break;

                case Log.ERROR:
                    Log.e(logTag, text);
                    break;

                case Log.WARN:
                    Log.w(logTag, text);
                    break;

                default:
                    break;
            }

        }
        catch (IOException ioEx) {
            Log.e(LOG_TAG, "(log) IOException occurred while logging the message : " + ioEx.getMessage());
        }
        catch (Exception ex) {
            Log.e(LOG_TAG, "(log) Exception occurred while logging the message : " + ex.getMessage());
        }

    }
}
