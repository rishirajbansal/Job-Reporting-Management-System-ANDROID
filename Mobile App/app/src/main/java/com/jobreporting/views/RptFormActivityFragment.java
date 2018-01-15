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

package com.jobreporting.views;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.github.gcacace.signaturepad.views.SignaturePad;
import com.jobreporting.R;
import com.jobreporting.base.Constants;
import com.jobreporting.base.ERequestType;
import com.jobreporting.business.ApplicationInitializer;
import com.jobreporting.business.actions.ReportAction;
import com.jobreporting.business.common.LogManager;
import com.jobreporting.business.validations.AbstractBusinessValidator;
import com.jobreporting.business.validations.ReportValidator;
import com.jobreporting.entities.WSUserEntity;
import com.jobreporting.utilities.FileUtility;
import com.jobreporting.utilities.ServiceUtility;
import com.jobreporting.utilities.SignPadUtility;
import com.jobreporting.utilities.Utility;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static android.graphics.Color.parseColor;


public class RptFormActivityFragment extends Fragment {

    private final String LOG_TAG = RptFormActivityFragment.class.getSimpleName();

    private View rootView = null;

    private List<WSUserEntity> reportDynaDetails = null;

    /* List to maintain the spinner values from runtime and save them on submit */
    Map<Integer, String> spinnerValues = new HashMap<>();

    /* List to maintain the checkbox values from runtime and save them on submit */
    Map<Integer, String> checkBoxValues = new HashMap<>();

    /* Counter for additional 2TB controls on run time */
    private static int dyna2TBIdCounter = 1;
    /* List to maintain 2TB controls and counter ids
    * Format: DynaId and counter value => DynaId | Counter value
    */
    private static Map<String, String> dyna2TBIdsCtrs = new LinkedHashMap<>();

    /* Photo view */
    private static final int PICK_PHOTO_REQUEST = 1;
    Map<String, byte[]> photoBytesDataList = new HashMap<>();

    private static Map<String, String> requestCodesWithViewIds = new HashMap<>();

    /* Signature pad */
    private SignaturePad mSignaturePad;
    private byte[] signFileBytes;
    private boolean isSignaturePadUsed;

    public RptFormActivityFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_rpt_form, container, false);

        Button submitButton = (Button)rootView.findViewById(R.id.btn_rpt_form_save);
        submitButton.setOnClickListener(mFormBtnsListener);

        Button resetButton = (Button)rootView.findViewById(R.id.btn_rpt_form_reset);
        resetButton.setOnClickListener(mFormBtnsListener);

        generateReportForm();

        return rootView;

    }

    public void generateReportForm(){

        LogManager.log(LOG_TAG, "Fetching the Report dyna details...", Log.DEBUG);

        ReportAction action = new ReportAction(getActivity());
        action.execute(ERequestType.REPORT_GET_DYNADATA);

        reportDynaDetails = action.getReportDynaDetails();

        renderRptDetailsView();

    }

    public void renderRptDetailsView(){

        Context ctx = getContext();

        if (null != reportDynaDetails && !reportDynaDetails.isEmpty()){

            final LinearLayout parentLayout = (LinearLayout) rootView.findViewById(R.id.layout_rpt_form_dyna);

            LinearLayout linearLayout = new LinearLayout(ctx);
            linearLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            linearLayout.setOrientation(LinearLayout.VERTICAL);

            //Add the Report Dyna fields

            for (WSUserEntity userEntity : reportDynaDetails){

                String htmlType = userEntity.getHtmlType();

                String ctrlName = "";
                String locale = ApplicationInitializer.getDeviceLocale();
                if (Utility.safeTrim(locale).equals(Constants.LOCALE_ESPANISH_SPAIN)){
                    ctrlName = userEntity.getNameEs();
                }
                else{
                    ctrlName = userEntity.getNameEn();
                }

                String controlId = userEntity.getIdDynaFields();

                if (Utility.safeTrim(htmlType).equals(Constants.DYNA_CONTROL_TYPE_CHECKBOX) ||
                    Utility.safeTrim(htmlType).equals(Constants.DYNA_CONTROL_TYPE_SIGNPAD)){

                }
                else{
                    View labelView = createInputLabel(ctx, ctrlName);
                    linearLayout.addView(labelView);
                }

                int id = generate_RID(controlId);

                switch (htmlType) {

                    case Constants.DYNA_CONTROL_TYPE_TEXT:

                        View textBoxView = createTextView(ctx, id);
                        linearLayout.addView(textBoxView);

                        break;

                    case Constants.DYNA_CONTROL_TYPE_TEXTAREA:

                        View textAreaView = createTextAreaView(ctx, id);
                        linearLayout.addView(textAreaView);

                        break;

                    case Constants.DYNA_CONTROL_TYPE_CHECKBOX:

                        View checkBoxView = createCheckBoxView(ctx, id, ctrlName);
                        linearLayout.addView(checkBoxView);

                        break;

                    case Constants.DYNA_CONTROL_TYPE_COMBO:

                        List<String> selectedListValues = null;
                        if (Utility.safeTrim(locale).equals(Constants.LOCALE_ESPANISH_SPAIN)){
                            selectedListValues = userEntity.getSelectedListValuesEs();
                        }
                        else{
                            selectedListValues = userEntity.getSelectedListValuesEn();
                        }

                        View spinnerView = createComboBoxView(ctx, id, selectedListValues);
                        linearLayout.addView(spinnerView);

                        break;

                    case Constants.DYNA_CONTROL_TYPE_DATE:

                        View dateView = createDateView(ctx, id);
                        linearLayout.addView(dateView);

                        break;

                    case Constants.DYNA_CONTROL_TYPE_TIME:

                        View timeView = createTimeView(ctx, id);
                        linearLayout.addView(timeView);

                        break;

                    case Constants.DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2:

                        View dyna2TextBoxView = createDyna2TextBoxView(ctx, id);
                        linearLayout.addView(dyna2TextBoxView);

                        break;

                    case Constants.DYNA_CONTROL_TYPE_IMAGE:

                        View imageView = createImageUploadView(ctx, id);
                        linearLayout.addView(imageView);

                        break;

                    case Constants.DYNA_CONTROL_TYPE_SIGNPAD:

                        createSignPadView(ctx, id);

                        break;

                    default:
                        LogManager.log(LOG_TAG, "Invalid html type: " + htmlType, Log.ERROR);
                        break;
                }

            }

            parentLayout.addView(linearLayout);

        }

    }

    public void submitReport(){

        if (null != reportDynaDetails && !reportDynaDetails.isEmpty()){

            List<String> isErrors = new ArrayList<>();
            isErrors.add("OK");
            Utility.checkErrors((ViewGroup)rootView.findViewById(R.id.scrollView_rpt_form), isErrors);
            if (isErrors.get(0).equals("NOT_OK")){
                ServiceUtility.showErrorAlertMessage(getActivity(), getResources().getString(R.string.rpt_data_invalid_title_alert), getResources().getString(R.string.rpt_data_invalid_message_alert));
                return;
            }

            String postedIdValues = updatePostedValuesInEntities();
            if (postedIdValues.equals("NOT_OK")){
                ServiceUtility.showErrorAlertMessage(getActivity(), getResources().getString(R.string.rpt_data_invalid_title_alert), getResources().getString(R.string.validation_alphanumeric_notAcceptableChars));
                return;
            }

            if (Utility.safeTrim(postedIdValues).equals(Constants.EMPTY_STRING)){
                //Display the alert box message
                ServiceUtility.showErrorAlertMessage(getActivity(), getResources().getString(R.string.rpt_data_empty_title_alert), getResources().getString(R.string.rpt_data_empty_message_alert));
            }
            else{
                ReportAction action = new ReportAction(getActivity());

                //Set Report Data
                action.setPostedIdValues(postedIdValues);

                //Set Image Data
                if (null != photoBytesDataList && photoBytesDataList.size() > 0){
                    action.setPhotoImageName(photoBytesDataList.keySet().iterator().next());
                    action.setPhotoBytesData(photoBytesDataList.get(action.getPhotoImageName()));
                }

                //Set Signature Data
                if (null != signFileBytes && signFileBytes.length > 0){
                    action.setSignFileBytes(signFileBytes);
                }

                action.execute(ERequestType.SAVE_REPORT);

                //Empty 2TB controls and counter ids list so that it should not contain null controls
                dyna2TBIdsCtrs.clear();
                dyna2TBIdCounter = 1;

                //Redirect to Home Activity
                ApplicationInitializer.getApplicationInitializer().redirectToHome(true);

                //Display Toast
                Toast reportToast = Toast.makeText(getContext(), R.string.toast_message_submit_report, Toast.LENGTH_LONG);
                reportToast.show();
            }

        }

    }

    public void reset(){

        Utility.resetForm((ViewGroup)rootView.findViewById(R.id.scrollView_rpt_form));

        //Empty spinner values
        spinnerValues.clear();

        //Empty checkbox values
        checkBoxValues.clear();

        //Empty Photo
        if (!photoBytesDataList.isEmpty()){
            String buttonId = requestCodesWithViewIds.get(Integer.toString(PICK_PHOTO_REQUEST));
            String imageUploadLabelViewId = buttonId + Constants.DYNA_CONTROL_TYPE_IMAGE_PHOTO_SELECTED_LABEL_ID;
            TextView imageUploadLabelView = (TextView) rootView.findViewById(Integer.parseInt(imageUploadLabelViewId));
            imageUploadLabelView.setText("");

            photoBytesDataList.clear();
            requestCodesWithViewIds.clear();
        }

        //Empty Signpad
        if (null != signFileBytes){
            signFileBytes = null;
        }
        if (rootView.findViewById(R.id.rpt_form_signpad_clear_btn).isEnabled()){
            mSignaturePad.clear();
        }

    }

    public String updatePostedValuesInEntities(){

        String postedIdValues = "";
        String errors = "NOT_OK";

        //Traverse the User entities and load the values
        for (WSUserEntity userEntity : reportDynaDetails){

            String htmlType = userEntity.getHtmlType();
            String controlId = userEntity.getIdDynaFields();
            int id = generate_RID(controlId);

            switch (htmlType) {

                case Constants.DYNA_CONTROL_TYPE_TEXT:

                    EditText textView = (EditText) rootView.findViewById(id);

                    if (null != textView && null != textView.getText() && !Utility.safeTrim(textView.getText().toString()).equals(Constants.EMPTY_STRING) ){
                        String validatorMessage = validateInputText(textView);
                        if (!validatorMessage.equals("OK")){
                            return errors;
                        }
                        String value = textView.getText().toString();
                        postedIdValues = postedIdValues + controlId + Constants.FIELDID_VALUE_SEPERATOR + value + Constants.FIELDID_VALUE_DATASET_SEPERATOR;
                    }
                    break;

                case Constants.DYNA_CONTROL_TYPE_TEXTAREA:

                    EditText textAreaView = (EditText) rootView.findViewById(id);

                    if (null != textAreaView && null != textAreaView.getText() && !Utility.safeTrim(textAreaView.getText().toString()).equals(Constants.EMPTY_STRING) ){
                        String validatorMessage = validateInputText(textAreaView);
                        if (!validatorMessage.equals("OK")){
                            return errors;
                        }
                        String value = textAreaView.getText().toString();
                        postedIdValues = postedIdValues + controlId + Constants.FIELDID_VALUE_SEPERATOR + value + Constants.FIELDID_VALUE_DATASET_SEPERATOR;
                    }
                    break;

                case Constants.DYNA_CONTROL_TYPE_CHECKBOX:

                    if (checkBoxValues.containsKey(id)){
                        postedIdValues = postedIdValues + controlId + Constants.FIELDID_VALUE_SEPERATOR + checkBoxValues.get(id) + Constants.FIELDID_VALUE_DATASET_SEPERATOR;
                    }

                    break;

                case Constants.DYNA_CONTROL_TYPE_COMBO:

                    if (spinnerValues.containsKey(id)){
                        postedIdValues = postedIdValues + controlId + Constants.FIELDID_VALUE_SEPERATOR + spinnerValues.get(id) + Constants.FIELDID_VALUE_DATASET_SEPERATOR;
                    }

                    break;

                case Constants.DYNA_CONTROL_TYPE_DATE:

                    EditText dateView = (EditText) rootView.findViewById(id);
                    if (null != dateView && null != dateView.getText() && !Utility.safeTrim(dateView.getText().toString()).equals(Constants.EMPTY_STRING) ){
                        String value = dateView.getText().toString();
                        postedIdValues = postedIdValues + controlId + Constants.FIELDID_VALUE_SEPERATOR + value + Constants.FIELDID_VALUE_DATASET_SEPERATOR;
                    }
                    break;

                case Constants.DYNA_CONTROL_TYPE_TIME:

                    EditText timeView = (EditText) rootView.findViewById(id);
                    if (null != timeView && null != timeView.getText() && !Utility.safeTrim(timeView.getText().toString()).equals(Constants.EMPTY_STRING) ){
                        String value = timeView.getText().toString();
                        value = value.replaceAll(Constants.REG_EX_TIME_CONTROL_SEPERATOR_UI, Constants.TIME_CONTROL_SEPERATOR_SERVER);
                        postedIdValues = postedIdValues + controlId + Constants.FIELDID_VALUE_SEPERATOR + value + Constants.FIELDID_VALUE_DATASET_SEPERATOR;
                    }
                    break;

                case Constants.DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2:

                    String consDyna2TBValues = "";

                    if (dyna2TBIdsCtrs.size() > 0){
                        for (String idAndCtr : dyna2TBIdsCtrs.keySet()){
                            String dyna2TBValue = "";
                            if (idAndCtr.startsWith(Integer.toString(id))){
                                String mapValue = dyna2TBIdsCtrs.get(idAndCtr);

                                String[] parsedIds = mapValue.split(Constants.REG_EX_PIPE_SEPERATOR);
                                String dyna2TBId = parsedIds[0];
                                String ctrValue = parsedIds[1];

                                String dyna2TBId_1 = dyna2TBId + Constants.DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2_SUFFIX_FIRST + ctrValue;
                                String dyna2TBId_2 = dyna2TBId + Constants.DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2_SUFFIX_SECOND + ctrValue;

                                EditText textBoxView1 = (EditText) rootView.findViewById(Integer.parseInt(dyna2TBId_1));
                                EditText textBoxView2 = (EditText) rootView.findViewById(Integer.parseInt(dyna2TBId_2));

                                if (null != textBoxView1 && null != textBoxView1.getText() && !Utility.safeTrim(textBoxView1.getText().toString()).equals(Constants.EMPTY_STRING) ){
                                    String validatorMessage = validateInputText(textBoxView1);
                                    if (!validatorMessage.equals("OK")){
                                        return errors;
                                    }
                                    dyna2TBValue = textBoxView1.getText().toString();
                                }
                                dyna2TBValue = dyna2TBValue + Constants.FIELD_2TB_VALUES_SEPERATOR;
                                if (null != textBoxView2 && null != textBoxView2.getText() && !Utility.safeTrim(textBoxView2.getText().toString()).equals(Constants.EMPTY_STRING) ){
                                    String validatorMessage = validateInputText(textBoxView2);
                                    if (!validatorMessage.equals("OK")){
                                        return errors;
                                    }
                                    dyna2TBValue = dyna2TBValue + textBoxView2.getText().toString();
                                }

                                if (!Utility.safeTrim(dyna2TBValue).equals(Constants.EMPTY_STRING) && !Utility.safeTrim(dyna2TBValue).equals(Constants.FIELD_2TB_VALUES_SEPERATOR)){
                                    consDyna2TBValues = consDyna2TBValues + dyna2TBValue + Constants.FIELD_2TB_VALUES_DATASET_SEPERATOR;
                                }

                            }

                        }

                        if (!Utility.safeTrim(consDyna2TBValues).equals(Constants.EMPTY_STRING)){
                            consDyna2TBValues = consDyna2TBValues.substring(0, consDyna2TBValues.length() - Constants.FIELD_2TB_VALUES_DATASET_SEPERATOR.length());

                            postedIdValues = postedIdValues + controlId + Constants.FIELDID_VALUE_SEPERATOR + consDyna2TBValues + Constants.FIELDID_VALUE_DATASET_SEPERATOR;
                        }
                    }

                    break;

                case Constants.DYNA_CONTROL_TYPE_IMAGE:

                    if (null != photoBytesDataList && photoBytesDataList.size() > 0){
                        String value = controlId + Constants.DYNA_CONTROL_VALUE_PLACEHOLDER;
                        postedIdValues = postedIdValues + controlId + Constants.FIELDID_VALUE_SEPERATOR + value + Constants.FIELDID_VALUE_DATASET_SEPERATOR;
                    }

                    break;

                case Constants.DYNA_CONTROL_TYPE_SIGNPAD:

                    if (isSignaturePadUsed){
                        Bitmap signBitmap = mSignaturePad.getSignatureBitmap();

                        signFileBytes = SignPadUtility.bitmapToJPG(signBitmap);

                        if (null != signFileBytes && signFileBytes.length > 0){
                            String value = controlId + Constants.DYNA_CONTROL_VALUE_PLACEHOLDER;
                            postedIdValues = postedIdValues + controlId + Constants.FIELDID_VALUE_SEPERATOR + value + Constants.FIELDID_VALUE_DATASET_SEPERATOR;
                        }
                    }

                    break;

                default:
                    LogManager.log(LOG_TAG, "Invalid html type: " + htmlType, Log.ERROR);
                    break;
            }

        }

        if (!Utility.safeTrim(postedIdValues).equals(Constants.EMPTY_STRING)){
            postedIdValues = postedIdValues.substring(0, postedIdValues.length() - Constants.FIELDID_VALUE_DATASET_SEPERATOR.length());
        }

        return postedIdValues;

    }

    public View createInputLabel(Context ctx, String labelName){

        TextView labelView = new TextView(ctx);
        labelView.setText(labelName + " :");
        //labelView.setTypeface(Typeface.DEFAULT_BOLD);
        labelView.setTextSize(18);
        labelView.setTextColor(parseColor("#275b89"));
        labelView.setPadding(0, 90, 0, 20);

        return labelView;
    }

    public View createTextView(Context ctx, int id){

        EditText textBoxView = new EditText(ctx);
        textBoxView.setId(id);
        textBoxView.setTextSize(18);
        textBoxView.setBackgroundDrawable(getResources().getDrawable(R.drawable.rpt_form_edittextview));
        textBoxView.setOnFocusChangeListener(mTextValidationsListener);

        return textBoxView;

    }

    public View createTextAreaView(Context ctx, int id){

        EditText textAreaView = new EditText(ctx);
        textAreaView.setId(id);
        textAreaView.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        textAreaView.setLines(Constants.DYNA_CONTROL_TYPE_TEXTAREA_LINES);
        textAreaView.setGravity(Gravity.TOP);
        textAreaView.setTextSize(18);
        textAreaView.setBackgroundDrawable(getResources().getDrawable(R.drawable.rpt_form_edittextview));
        textAreaView.setOnFocusChangeListener(mTextValidationsListener);

        return textAreaView;

    }

    public View createCheckBoxView(Context ctx, int id, String ctrlName){

        CheckBox checkBoxView = new CheckBox(ctx);
        checkBoxView.setId(id);
        checkBoxView.setText(ctrlName);
        checkBoxView.setOnClickListener(mChkBoxListener);

        //checkBoxView.setTypeface(Typeface.DEFAULT_BOLD);
        checkBoxView.setTextSize(18);
        checkBoxView.setTextColor(parseColor("#275b89"));

        /*final float scale = this.getResources().getDisplayMetrics().density;
        checkBoxView.setPadding(checkBoxView.getPaddingLeft() + (int)(10.0f * scale + 0.5f),
                checkBoxView.getPaddingTop(),
                checkBoxView.getPaddingRight(),
                checkBoxView.getPaddingBottom());*/

        checkBoxView.setPadding(0, 70, 0, 70);

        return checkBoxView;

    }

    public View createComboBoxView(Context ctx, int id, List<String> listValues){

        Spinner spinnerView = new Spinner(ctx);
        spinnerView.setId(id);

        List<String> listValuesWithInitialValue = new ArrayList<>();
        listValuesWithInitialValue.add(getString(R.string.rpt_form_spinner_initialvalue));
        listValuesWithInitialValue.addAll(listValues);

        //ArrayAdapter<String> spinnerListAdapter = new ArrayAdapter<String>(ctx, android.R.layout.simple_spinner_dropdown_item, listValues);
        ArrayAdapter<String> spinnerListAdapter = new ArrayAdapter<String>(ctx, R.layout.rpt_form_spinner, listValuesWithInitialValue);
        spinnerView.setAdapter(spinnerListAdapter);
        spinnerView.setOnItemSelectedListener(new SpinnerItemListener());

        return spinnerView;

    }

    public View createDateView(Context ctx, int id){

        //Date Picker control displays the big date calender which consumes lots of space
        //Due to this reason, date picker is displayed via dialog
        /*DatePicker dateView = new DatePicker(ctx);
        dateView.setId(id);
        dateView.setOnClickListener(mDatePickerListener);*/

        EditText dateView = new EditText(ctx);
        dateView.setId(id);
        dateView.setInputType(InputType.TYPE_NULL);
        dateView.setOnClickListener(mDatePickerListener);
        dateView.setHint(getString(R.string.rpt_form_doubletap_hint));
        dateView.setTextSize(18);
        dateView.setBackgroundDrawable(getResources().getDrawable(R.drawable.rpt_form_edittextview));

        return dateView;

    }

    public View createTimeView(Context ctx, int id){

        /*TimePicker timeView = new TimePicker(ctx);
        timeView.setId(id);
        timeView.setOnClickListener(mDatePickerListener);
        timeView.setOnTimeChangedListener(mTimePickerOnChangedListener);*/

        EditText timeView = new EditText(ctx);
        timeView.setId(id);
        timeView.setInputType(InputType.TYPE_NULL);
        timeView.setOnClickListener(mTimePickerListener);
        timeView.setHint(getString(R.string.rpt_form_doubletap_hint));
        timeView.setTextSize(18);
        timeView.setBackgroundDrawable(getResources().getDrawable(R.drawable.rpt_form_edittextview));

        return timeView;

    }

    public View createDyna2TextBoxView(Context ctx, int id){

        String tableId = Constants.DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2_TABLE_LAYOUT_ID + id;
        String tableRowId = Constants.DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2_TABLE_LAYOUT_ROW_ID + id + dyna2TBIdCounter;
        String plusButtonID = Constants.DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2_PLUS_BUTTON_ID + id + dyna2TBIdCounter;
        String minusButtonID = Constants.DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2_MINUS_BUTTON_ID + id + dyna2TBIdCounter;

        String tb1Id = id + Constants.DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2_SUFFIX_FIRST + dyna2TBIdCounter;
        String tb2Id = id + Constants.DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2_SUFFIX_SECOND + dyna2TBIdCounter;

        TableLayout tableLayout = new TableLayout(ctx);
        tableLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        tableLayout.setId(Integer.parseInt(tableId));

        TableRow row1 = new TableRow(ctx);
        row1.setId(Integer.parseInt(tableRowId));
        //row1.setWeightSum(1);

        TableRow.LayoutParams rowTextParams = new TableRow.LayoutParams(0, LayoutParams.WRAP_CONTENT, .35f);
        TableRow.LayoutParams rowButtonParams = new TableRow.LayoutParams(0, LayoutParams.WRAP_CONTENT, .15f);
        rowTextParams.setMargins(15, 0, 0, 0);
        rowButtonParams.setMargins(5, 0, 0, 0);

        EditText textBoxView1 = new EditText(ctx);
        textBoxView1.setId(Integer.parseInt(tb1Id));
        textBoxView1.setLayoutParams(rowTextParams);
        textBoxView1.setTextSize(18);
        textBoxView1.setBackgroundDrawable(getResources().getDrawable(R.drawable.rpt_form_edittextview));
        textBoxView1.setOnFocusChangeListener(mTextValidationsListener);
        row1.addView(textBoxView1);

        EditText textBoxView2 = new EditText(ctx);
        textBoxView2.setId(Integer.parseInt(tb2Id));
        textBoxView2.setLayoutParams(rowTextParams);
        textBoxView2.setTextSize(18);
        textBoxView2.setBackgroundDrawable(getResources().getDrawable(R.drawable.rpt_form_edittextview));
        textBoxView2.setOnFocusChangeListener(mTextValidationsListener);
        row1.addView(textBoxView2);

        Button minusBtn = new Button(ctx);
        minusBtn.setId(Integer.parseInt(minusButtonID));
        minusBtn.setText("-");
        minusBtn.setTextSize(18);
        minusBtn.setLayoutParams(rowButtonParams);
        minusBtn.setTextColor(parseColor("#275b89"));
        minusBtn.setTypeface(Typeface.DEFAULT_BOLD);
        minusBtn.setOnClickListener(mDyna2TextBoxesListener);
        row1.addView(minusBtn);

        Button plusBtn = new Button(ctx);
        plusBtn.setId(Integer.parseInt(plusButtonID));
        plusBtn.setText("+");
        plusBtn.setTextSize(18);
        plusBtn.setLayoutParams(rowButtonParams);
        plusBtn.setTextColor(parseColor("#275b89"));
        plusBtn.setTypeface(Typeface.DEFAULT_BOLD);
        plusBtn.setOnClickListener(mDyna2TextBoxesListener);
        row1.addView(plusBtn);

        tableLayout.addView(row1);

        dyna2TBIdsCtrs.put(Integer.toString(id) + dyna2TBIdCounter, id + "|" + dyna2TBIdCounter);
        ++dyna2TBIdCounter;

        return tableLayout;

    }

    public View createImageUploadView(Context ctx, int id){

        TableLayout tableLayout = new TableLayout(ctx);
        tableLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        TableRow row = new TableRow(ctx);

        Button imageUploadButton = new Button(ctx);
        imageUploadButton.setId(id);
        imageUploadButton.setText(ctx.getText(R.string.btn_rpt_form_dyna_imageUpload));
        imageUploadButton.setTransformationMethod(null);
        imageUploadButton.setOnClickListener(mImageViewBtnListener);
        row.addView(imageUploadButton);

        String photoSelectedId = id + Constants.DYNA_CONTROL_TYPE_IMAGE_PHOTO_SELECTED_LABEL_ID;
        TextView imageUploadLabelView = new TextView(ctx);
        imageUploadLabelView.setId(Integer.parseInt(photoSelectedId));
        imageUploadLabelView.setTextColor(parseColor("#E57373"));
        row.addView(imageUploadLabelView);

        tableLayout.addView(row);

        return tableLayout;

    }

    public void createSignPadView(Context ctx, int id){

        LinearLayout signPadLayout = (LinearLayout)rootView.findViewById(R.id.layout_rpt_form_signpad);
        signPadLayout.setVisibility(View.VISIBLE);

        mSignaturePad = (SignaturePad) rootView.findViewById(R.id.signature_pad);
        mSignaturePad.setOnSignedListener(mSignPadListener);

        Button signPadClearBtn = (Button) rootView.findViewById(R.id.rpt_form_signpad_clear_btn);
        signPadClearBtn.setOnClickListener(mSignPadBtnsListener);

        return;

    }


    private View.OnClickListener mFormBtnsListener = new View.OnClickListener() {

        public void onClick(View view) {

            switch (view.getId()) {

                case R.id.btn_rpt_form_save:

                    submitReport();

                    break;

                case R.id.btn_rpt_form_reset:

                    reset();

                    break;

                default:
                    break;

            }

        }
    };

    private class SpinnerItemListener implements AdapterView.OnItemSelectedListener  {

        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            int spinnerId = parent.getId();
            String selectedItem = parent.getItemAtPosition(position).toString();

            if (!Utility.safeTrim(selectedItem).equals(getString(R.string.rpt_form_spinner_initialvalue))){
                spinnerValues.put(spinnerId, selectedItem);
            }

        }

        public void onNothingSelected(AdapterView<?> parent) {


        }

    }

    private View.OnClickListener mChkBoxListener = new View.OnClickListener() {

        public void onClick(View view) {

            int chkBoxId = view.getId();
            boolean checked = ((CheckBox) view).isChecked();
            if (checked){
                checkBoxValues.put(chkBoxId, Constants.DYNA_CONTROL_TYPE_CHECKBOX_DEFAULT_VALUE);
            }
            else{
                if (checkBoxValues.containsKey(chkBoxId)){
                    checkBoxValues.remove(chkBoxId);
                }
            }
        }
    };

    private View.OnClickListener mDatePickerListener = new View.OnClickListener() {

        public void onClick(View view) {

            DialogFragment newFragment = new DatePickerFragment();
            newFragment.show(getActivity().getFragmentManager(), Integer.toString(view.getId()));

        }
    };

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            String id = this.getTag();

            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {

            String id = this.getTag();

            month = month + 1;
            String selectedDate = day + "/" + month + "/" + year;

            EditText dateView = (EditText) this.getActivity().findViewById(Integer.parseInt(id));
            dateView.setText(selectedDate);
        }

    }

    private View.OnClickListener mTimePickerListener = new View.OnClickListener() {

        public void onClick(View view) {

            DialogFragment newFragment = new TimePickerFragment();
            newFragment.show(getActivity().getFragmentManager(), Integer.toString(view.getId()));

        }
    };

    public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener  {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            String id = this.getTag();

            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            return new TimePickerDialog(getActivity(), this, hour, minute, false);
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            String id = this.getTag();

            String selectedTime = Utility.get12HourFormatTime(hourOfDay, minute);

            EditText timeView = (EditText) this.getActivity().findViewById(Integer.parseInt(id));
            timeView.setText(selectedTime);
        }

    }

    private View.OnClickListener mDyna2TextBoxesListener = new View.OnClickListener() {

        public void onClick(View view) {

            Context ctx = view.getContext();

            String buttonId = Integer.toString(view.getId());

            if (buttonId.startsWith(Constants.DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2_PLUS_BUTTON_ID)){

                String idAndCtr = buttonId.substring(Constants.DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2_PLUS_BUTTON_ID.length());
                idAndCtr = dyna2TBIdsCtrs.get(idAndCtr);

                String[] parsedIds = idAndCtr.split(Constants.REG_EX_PIPE_SEPERATOR);
                String orgId = parsedIds[0];
                String ctrValue = parsedIds[1];

                String tableId = Constants.DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2_TABLE_LAYOUT_ID + orgId;
                String tableRowId = Constants.DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2_TABLE_LAYOUT_ROW_ID + orgId + dyna2TBIdCounter;
                String plusButtonID = Constants.DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2_PLUS_BUTTON_ID + orgId + dyna2TBIdCounter;
                String minusButtonID = Constants.DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2_MINUS_BUTTON_ID + orgId + dyna2TBIdCounter;

                String tb1Id = orgId + Constants.DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2_SUFFIX_FIRST + dyna2TBIdCounter;
                String tb2Id = orgId + Constants.DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2_SUFFIX_SECOND + dyna2TBIdCounter;

                TableRow.LayoutParams rowTextParams = new TableRow.LayoutParams(0, LayoutParams.WRAP_CONTENT, .35f);
                TableRow.LayoutParams rowButtonParams = new TableRow.LayoutParams(0, LayoutParams.WRAP_CONTENT, .15f);
                rowTextParams.setMargins(15, 0, 0, 0);
                rowButtonParams.setMargins(5, 0, 0, 0);

                TableLayout tableLayout = (TableLayout) rootView.findViewById(Integer.parseInt(tableId));

                TableRow row1 = new TableRow(ctx);
                row1.setId(Integer.parseInt(tableRowId));

                EditText textBoxView1 = new EditText(ctx);
                textBoxView1.setId(Integer.parseInt(tb1Id));
                textBoxView1.setLayoutParams(rowTextParams);
                textBoxView1.setTextSize(18);
                textBoxView1.setBackgroundDrawable(getResources().getDrawable(R.drawable.rpt_form_edittextview));
                textBoxView1.setOnFocusChangeListener(mTextValidationsListener);
                row1.addView(textBoxView1);

                EditText textBoxView2 = new EditText(ctx);
                textBoxView2.setId(Integer.parseInt(tb2Id));
                textBoxView2.setLayoutParams(rowTextParams);
                textBoxView2.setTextSize(18);
                textBoxView2.setBackgroundDrawable(getResources().getDrawable(R.drawable.rpt_form_edittextview));
                textBoxView2.setOnFocusChangeListener(mTextValidationsListener);
                row1.addView(textBoxView2);

                Button minusBtn = new Button(ctx);
                minusBtn.setId(Integer.parseInt(minusButtonID));
                minusBtn.setText("-");
                minusBtn.setTextSize(18);
                minusBtn.setLayoutParams(rowButtonParams);
                minusBtn.setTextColor(parseColor("#275b89"));
                minusBtn.setTypeface(Typeface.DEFAULT_BOLD);
                minusBtn.setOnClickListener(mDyna2TextBoxesListener);
                row1.addView(minusBtn);

                Button plusBtn = new Button(ctx);
                plusBtn.setId(Integer.parseInt(plusButtonID));
                plusBtn.setText("+");
                plusBtn.setTextSize(18);
                plusBtn.setLayoutParams(rowButtonParams);
                plusBtn.setTextColor(parseColor("#275b89"));
                plusBtn.setTypeface(Typeface.DEFAULT_BOLD);
                plusBtn.setOnClickListener(mDyna2TextBoxesListener);
                row1.addView(plusBtn);

                tableLayout.addView(row1);

                dyna2TBIdsCtrs.put(orgId + dyna2TBIdCounter, orgId + "|" + dyna2TBIdCounter);
                ++dyna2TBIdCounter;

                //Remove current plus button
                tableLayout.removeView(view);

            }
            else if (buttonId.startsWith(Constants.DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2_MINUS_BUTTON_ID)){

                String mapKey = buttonId.substring(Constants.DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2_PLUS_BUTTON_ID.length());
                String idAndCtr = dyna2TBIdsCtrs.get(mapKey);

                String[] parsedIds = idAndCtr.split(Constants.REG_EX_PIPE_SEPERATOR);
                String orgId = parsedIds[0];
                String ctrValue = parsedIds[1];

                String tableId = Constants.DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2_TABLE_LAYOUT_ID + orgId;
                String tableRowId = Constants.DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2_TABLE_LAYOUT_ROW_ID + orgId + ctrValue;
                String plusButtonID = Constants.DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2_PLUS_BUTTON_ID + orgId + ctrValue;
                String minusButtonID = Constants.DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2_MINUS_BUTTON_ID + orgId + ctrValue;

                String tb1Id = orgId + Constants.DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2_SUFFIX_FIRST + ctrValue;
                String tb2Id = orgId + Constants.DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2_SUFFIX_SECOND + ctrValue;

                TableLayout tableLayout = (TableLayout) rootView.findViewById(Integer.parseInt(tableId));
                tableLayout.removeView(rootView.findViewById(Integer.parseInt(tableRowId)));

                dyna2TBIdsCtrs.remove(mapKey);

            }

        }
    };

    private View.OnClickListener mImageViewBtnListener = new View.OnClickListener() {

        public void onClick(View view) {

            Context ctx = view.getContext();

            displayPhotoChooser(view);

        }

    };

    private SignaturePad.OnSignedListener mSignPadListener = new SignaturePad.OnSignedListener() {

        @Override
        public void onStartSigning() {
            //Toast.makeText(getActivity(), "OnStartSigning", Toast.LENGTH_SHORT).show();
            isSignaturePadUsed = true;
        }

        @Override
        public void onSigned() {
            (rootView.findViewById(R.id.rpt_form_signpad_clear_btn)).setEnabled(true);
        }

        @Override
        public void onClear() {
            (rootView.findViewById(R.id.rpt_form_signpad_clear_btn)).setEnabled(false);
            isSignaturePadUsed = false;
        }

    };

    private View.OnClickListener mSignPadBtnsListener = new View.OnClickListener() {

        public void onClick(View view) {

            switch (view.getId()) {

                case R.id.rpt_form_signpad_clear_btn:

                    mSignaturePad.clear();

                    break;

                default:
                    break;

            }

        }
    };

    private View.OnFocusChangeListener mTextValidationsListener = new View.OnFocusChangeListener() {

        public void onFocusChange(View view, boolean hasFocus) {

            if (view instanceof EditText) {
                String validatorMessage = validateInputText(view);
                if (!validatorMessage.equals("OK")){
                    ((EditText) view).setError(validatorMessage);
                }
                else{
                    ((EditText) view).setError(null);
                }
            }

        }
    };

    private String validateInputText(View view){

        AbstractBusinessValidator validator = new ReportValidator(getContext());
        String value = ((EditText)view).getText().toString();
        String validatorMessage = validator.validate(value);

        return validatorMessage;
    }


    public int generate_RID(String controlId){

        int rid = -1;
        String newId = "";

        String[] splitted = controlId.split(Constants.REG_EX_DYNAID_PREFIX_SEPERATOR);
        String prefix = splitted[0] + Constants.DYNAID_PREFIX_SEPERATOR;
        String dynaId = splitted[1];

        switch (prefix){

            case Constants.DYNAFIELDS_PRODUCT_CBID_PREFIX:

                newId = Constants.DYNAFIELDS_PRODUCT_CBID_PREFIX_RID + dynaId;
                rid = Integer.parseInt(newId);
                break;

            case Constants.DYNAFIELDS_TASK_CBID_PREFIX:

                newId = Constants.DYNAFIELDS_TASK_CBID_PREFIX_RID + dynaId;
                rid = Integer.parseInt(newId);
                break;

            case Constants.DYNAFIELDS_WORKER_CBID_PREFIX:

                newId = Constants.DYNAFIELDS_WORKER_CBID_PREFIX_RID + dynaId;
                rid = Integer.parseInt(newId);
                break;

            case Constants.DYNAFIELDS_CUSTOMER_CBID_PREFIX:

                newId = Constants.DYNAFIELDS_CUSTOMER_CBID_PREFIX_RID + dynaId;
                rid = Integer.parseInt(newId);
                break;

            case Constants.DYNAFIELDS_REPORTING_CBID_PREFIX:

                newId = Constants.DYNAFIELDS_REPORTING_CBID_PREFIX_RID + dynaId;
                rid = Integer.parseInt(newId);
                break;

            default:
                LogManager.log(LOG_TAG, "Invalid dyna id type: " + prefix, Log.ERROR);
                break;

        }

        return rid;

    }

    private void displayPhotoChooser(View view) {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        //intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        requestCodesWithViewIds.put(Integer.toString(PICK_PHOTO_REQUEST), Integer.toString(view.getId()));

        startActivityForResult(Intent.createChooser(intent, getResources().getText(R.string.text_imageUpload_selectImage)), PICK_PHOTO_REQUEST);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_PHOTO_REQUEST){
            if (resultCode == Activity.RESULT_OK) {
                if (null != data && null != data.getData()){
                    Uri selectedFileUri = data.getData();
                    LogManager.log(LOG_TAG, "Uri of photo selected : " + selectedFileUri, Log.DEBUG);

                    FileUtility.getImageByteStream(getContext(), selectedFileUri, photoBytesDataList);

                    if (null != photoBytesDataList && photoBytesDataList.size() > 0){
                        String imageName = photoBytesDataList.keySet().iterator().next();

                        String buttonId = requestCodesWithViewIds.get(Integer.toString(PICK_PHOTO_REQUEST));
                        String imageUploadLabelViewId = buttonId + Constants.DYNA_CONTROL_TYPE_IMAGE_PHOTO_SELECTED_LABEL_ID;
                        TextView imageUploadLabelView = (TextView) rootView.findViewById(Integer.parseInt(imageUploadLabelViewId));
                        //imageUploadLabelView.setText(getResources().getText(R.string.text_imageUpload_label_photoSelected));
                        imageUploadLabelView.setText(imageName);
                    }

                }
                else{
                    LogManager.log(LOG_TAG, "No Photo Selected", Log.DEBUG);
                }
            }

        }

    }

}
