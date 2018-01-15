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


public class Constants {

    public static final String EMPTY_STRING										        = "";
    public static final String STRING_TRUE										        = "true";
    public static final String STRING_FALSE										        = "false";

    /*
	 * Logging
	 */
    public static final String LOG_DIR										            = "jrdir";
    public static final String LOG_FILE										            = "jr.log";
    public static long LOG_FILE_MAX_SIZE      								            = 3; //in MB


    /* Data Store constants */
    public static String DYNATABLE_TYPE_PRODUCT                                         = "PRD";
    public static String DYNATABLE_TYPE_CUSTOMER                                        = "CSTMR";
    public static String DYNATABLE_TYPE_TASK                                            = "TSK";
    public static String DYNATABLE_TYPE_REPORTS                                         = "RPT";

    /* Intent constants */
    public static String INTENT_EXTRA_CUSTOM_SERVER_ERROR_CODE                          = "code";
    public static String INTENT_EXTRA_CUSTOM_SERVER_MESSAGE                             = "message";
    public static String INTENT_EXTRA_CUSTOM_SERVER_ERROR                               = "error";
    public static String INTENT_EXTRA_CUSTOM_SERVER_ERROR_OCCURRED                      = "occurred";
    public static String INTENT_EXTRA_CUSTOM_PRD_DETAILS                                = "prdDetails";
    public static String INTENT_EXTRA_CUSTOM_CSTMR_DETAILS                              = "cstmrDetails";
    public static String INTENT_EXTRA_CUSTOM_TSK_DETAILS                                = "tskDetails";
    public static String INTENT_EXTRA_CUSTOM_RPT_FORM_IMAGE_VIEW_ID                     = "imageViewId";
    public static String INTENT_EXTRA_CUSTOM_AUTH_SUCCESSFULL                           = "authenticated";


    public static final String LOCATION_STRING_SEPERATOR								= ", ";

    public static final String DYNAFIELD_IDS_SEPERATOR									= "|";
    public static final String FIELDID_VALUE_SEPERATOR									= ":";
    public static final String FIELDID_VALUE_DATASET_SEPERATOR							= "|";
    public static final String DYNAID_PREFIX_SEPERATOR						            = "_";
    public static final String TIME_CONTROL_SEPERATOR_UI					            = ":";
    public static final String TIME_CONTROL_SEPERATOR_SERVER				            = ".";
    public static final String FIELD_2TB_VALUES_SEPERATOR						        = "<>";
    public static final String FIELD_2TB_VALUES_DATASET_SEPERATOR						= ",";
    public static final String DYNAFIELDS_LIST_VALUES_LOCALE_SEPERATOR					= "<|>";


    public static final String REG_EX_PIPE_SEPERATOR									= "\\|";
    public static final String REG_EX_DYNAFIELD_IDS_SEPERATOR							= "\\|";
    public static final String REG_EX_FIELDID_VALUE_DATASET_SEPERATOR					= "\\|";
    public static final String REG_EX_FIELDID_VALUE_SEPERATOR							= "\\:";
    public static final String REG_EX_DYNAID_PREFIX_SEPERATOR						    = "\\_";
    public static final String REG_EX_TIME_CONTROL_SEPERATOR_UI							= "\\:";
    public static final String REG_EX_DYNAFIELDS_LIST_VALUES_LOCALE_SEPERATOR			= "\\<\\|>";

    /* Dyna Fields */
    public static final String DYNA_CONTROL_TYPE_TEXT                                   = "Text Box";
    public static final String DYNA_CONTROL_TYPE_TEXTAREA                               = "Textarea";
    public static final String DYNA_CONTROL_TYPE_COMBO                                  = "Combo Box";
    public static final String DYNA_CONTROL_TYPE_CHECKBOX                               = "Checkbox";
    public static final String DYNA_CONTROL_TYPE_DATE                                   = "Date";
    public static final String DYNA_CONTROL_TYPE_TIME                                   = "Time";
    public static final String DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2                    = "Dyna Text Box|2";
    public static final String DYNA_CONTROL_TYPE_IMAGE                                  = "Image";
    public static final String DYNA_CONTROL_TYPE_SIGNPAD                                = "Signpad";

    public static final String DYNAFIELDS_PRODUCT_CBID_PREFIX							= "p_";
    public static final String DYNAFIELDS_TASK_CBID_PREFIX								= "t_";
    public static final String DYNAFIELDS_WORKER_CBID_PREFIX							= "w_";
    public static final String DYNAFIELDS_CUSTOMER_CBID_PREFIX							= "c_";
    public static final String DYNAFIELDS_REPORTING_CBID_PREFIX							= "r_";

    /* Need to keep the value in single digit as to prevent 'Invalid Int' issue */
    public static final int DYNAFIELDS_CBID_PREFIX_RID_LENGTH						    = 1;
    public static final String DYNAFIELDS_PRODUCT_CBID_PREFIX_RID						= "1";
    public static final String DYNAFIELDS_TASK_CBID_PREFIX_RID							= "2";
    public static final String DYNAFIELDS_WORKER_CBID_PREFIX_RID						= "3";
    public static final String DYNAFIELDS_CUSTOMER_CBID_PREFIX_RID						= "4";
    public static final String DYNAFIELDS_REPORTING_CBID_PREFIX_RID						= "5";

    public static final String DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2_SUFFIX_FIRST           = "01";
    public static final String DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2_SUFFIX_SECOND          = "02";
    public static final String DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2_TABLE_LAYOUT_ID        = "90";
    public static final String DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2_TABLE_LAYOUT_ROW_ID    = "91";
    public static final String DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2_PLUS_BUTTON_ID         = "92";
    public static final String DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2_MINUS_BUTTON_ID        = "93";

    public static final String DYNA_CONTROL_TYPE_CHECKBOX_DEFAULT_VALUE                 = "Yes";
    public static final int DYNA_CONTROL_TYPE_TEXTAREA_LINES                            = 3;

    public static final String DYNA_CONTROL_TYPE_IMAGE_PHOTO_SELECTED_LABEL_ID          = "11";

    public static final String DYNA_CONTROL_VALUE_PLACEHOLDER                           = "-Placeholder";

    /* Generic Constants */
    public static final String REPORTDATAENTRY_STATUS_NOT_SAVED                         = "0";
    public static final String REPORTDATAENTRY_STATUS_SAVED                             = "1";

    public static final String SIGN_FILE_NAME                                           = "sign.jpg";

    /* Date Format Constants */
    public static final String DATEFORMAT_DB_CREATED_ON									= "MMM dd, yyyy hh:mm aaa";
    public static final String DATEFORMAT_LOGGING   									= "dd-MM-yyyy HH:mm:ss";

    /* UI Constants */
    public static final int TOAST_DURATION_REPORT_SUBMIT								= 60;

    /* Location Constants */
    public static final String LOCATION_GPS_NOT_ENABLED                                 = "0";
    public static final String LOCATION_GPS_ENABLED                                     = "1";
    public static final String LOCATION_GEOCODE_TIMEDOUT                                = "2";
    public static final String LOCATION_NO_LOCATION_FOUND                               = "3";

    /*
	 * Locale
	 */
    public static final String LOCALE_ENGLISH_US								        = "en_US";
    public static final String LOCALE_ESPANISH_SPAIN							        = "es_ES";

}
