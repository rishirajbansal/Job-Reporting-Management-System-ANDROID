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

import com.jobreporting.R;
import com.jobreporting.base.Constants;
import com.jobreporting.utilities.Utility;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public abstract class AbstractBusinessValidator {

    private final String LOG_TAG = AbstractBusinessValidator.class.getSimpleName();

    public abstract String validate(String request);


    public String validateChars(String request, Context ctx){

        String message = "OK";

        if (!Utility.safeTrim(request).equals(Constants.EMPTY_STRING)){

            //String regex = "[A-Za-z0-9\\s`~!@#$%^&*()+={}'\".\\/?\\\\-]*";
            String regex = "[^:|,;<>]*";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(request);
            boolean valid = matcher.matches();
            if (!valid){
                message = ctx.getResources().getString(R.string.validation_alphanumeric_notAcceptableChars).toString();
            }

        }

        return message;

    }

}
