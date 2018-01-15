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

package com.jobreporting.base;



public class ConfigConstants {

    /* Services Constants */
    //public static final String SERVICE_BASE_URL                                         = "http://10.0.2.2:8088/JobReporting/services/";
    public static final String SERVICE_BASE_URL                                         = "http://82.223.24.223:8030/JobReporting/services/";
    public static final String SERVICE_ONETIMEAUTH                                      = "oneTimeAuth";
    public static final String SERVICE_SYNCHER                                          = "sync";
    public static final String SERVICE_REPORT                                           = "reports";

    public static final String RESPONSE_SUCCESS										    = "true";
    public static final String RESPONSE_FALSE											= "false";

    /* Android Services Constants */
    public static final int REPORT_DISPATCH_SERVICE_TIMER                               = 2; //minutes
    public static final int SYNCHER_SERVICE_TIMER                                       = 2; //minutes

    /*Logging Constants*/
    public static final boolean LOG_OUTPUT_FILE											= true;   //true or false


}
