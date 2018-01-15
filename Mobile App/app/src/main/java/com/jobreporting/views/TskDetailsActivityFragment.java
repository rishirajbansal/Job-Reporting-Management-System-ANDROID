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

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jobreporting.R;
import com.jobreporting.base.Constants;
import com.jobreporting.business.ApplicationInitializer;
import com.jobreporting.utilities.Utility;

import static android.graphics.Color.parseColor;


public class TskDetailsActivityFragment extends Fragment {

    private final String LOG_TAG = TskDetailsActivityFragment.class.getSimpleName();

    private View rootView = null;

    public TskDetailsActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_tsk_details, container, false);

        Bundle intentBundle = getActivity().getIntent().getExtras();

        String tskDetail = intentBundle.getString(Constants.INTENT_EXTRA_CUSTOM_TSK_DETAILS);

        renderTskDetailsView(tskDetail);

        return rootView;

    }

    public void renderTskDetailsView(String tskDetail){

        Context ctx = getContext();

        if (!Utility.safeTrim(tskDetail).equals(Constants.EMPTY_STRING)){
            final LinearLayout parentLayout = (LinearLayout) rootView.findViewById(R.id.scrollView_layout_tsk_details);

            LinearLayout linearLayout = new LinearLayout(ctx);
            linearLayout.setLayoutParams(new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT));
            linearLayout.setOrientation(LinearLayout.VERTICAL);

            //Add the task data fields
            String[] tskLocales = tskDetail.split(Constants.REG_EX_DYNAFIELDS_LIST_VALUES_LOCALE_SEPERATOR);
            String tskComoponents = null;
            String enTskDetails = tskLocales[0];
            String esTskDetails = tskLocales[1];

            String locale = ApplicationInitializer.getDeviceLocale();
            if (Utility.safeTrim(locale).equals(Constants.LOCALE_ESPANISH_SPAIN)){
                tskComoponents = esTskDetails;
            }
            else{
                tskComoponents = enTskDetails;
            }

            String[] tskComoponentsSplit = tskComoponents.split(Constants.REG_EX_FIELDID_VALUE_DATASET_SEPERATOR);

            for (String fieldIdValue : tskComoponentsSplit){
                String[] fieldIdValueArr = fieldIdValue.split(Constants.REG_EX_FIELDID_VALUE_SEPERATOR);
                String fieldName = fieldIdValueArr[0];
                String fieldValue = "";
                if (fieldIdValueArr.length > 1){
                    fieldValue = fieldIdValueArr[1];
                }

                TextView tskFieldNameView = new TextView(ctx);
                tskFieldNameView.setText(fieldName);
                tskFieldNameView.setTypeface(Typeface.DEFAULT_BOLD);
                tskFieldNameView.setTextSize(18);
                //tskFieldNameView.setPadding(20, 500, 50, 50);
                tskFieldNameView.setTextColor(parseColor("#275b89"));
                linearLayout.addView(tskFieldNameView);

                TextView tskFieldValueView = new TextView(ctx);
                tskFieldValueView.setText(fieldValue);
                tskFieldValueView.setTextSize(18);
                tskFieldValueView.setPadding(25, 40, 5, 70);
                tskFieldValueView.setTextColor(parseColor("#000000"));

                if (Utility.safeTrim(fieldValue).equals(Constants.EMPTY_STRING)){
                    tskFieldValueView.setText(R.string.data_not_avialable);
                    tskFieldValueView.setTextColor(parseColor("#a84b4b"));
                    tskFieldValueView.setTypeface(null, Typeface.ITALIC);
                }

                linearLayout.addView(tskFieldValueView);

            }

            parentLayout.addView(linearLayout);

        }

    }

}
