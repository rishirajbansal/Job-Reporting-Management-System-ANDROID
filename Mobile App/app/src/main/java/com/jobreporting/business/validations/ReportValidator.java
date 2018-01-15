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

package com.jobreporting.business.validations;

import android.content.Context;

/**
 * Created by Rishi Raj on 09 Oct 2016, 0009.
 */

public class ReportValidator extends AbstractBusinessValidator {

    private final String LOG_TAG = ReportValidator.class.getSimpleName();

    public Context context = null;

    public ReportValidator(Context context){

        this.context = context;

    }


    @Override
    public String validate(String request) {

        String message = "OK";

        message = validateChars(request, context);
        if (!message.equals("OK")){
            return message;
        }

        /* Next validation will come here*/
        /*message = validateXYZ(request, context);
        if (!message.equals("OK")){
            return message;
        }*/

        return message;
    }


}
