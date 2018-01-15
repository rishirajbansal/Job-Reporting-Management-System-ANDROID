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
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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


public class PrdDetailsActivityFragment extends Fragment {

    private final String LOG_TAG = PrdDetailsActivityFragment.class.getSimpleName();

    private View rootView = null;


    public PrdDetailsActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_prd_details, container, false);

        Bundle intentBundle = getActivity().getIntent().getExtras();

        String prdDetail = intentBundle.getString(Constants.INTENT_EXTRA_CUSTOM_PRD_DETAILS);

        renderPrdDetailsView(prdDetail);

        return rootView;

    }

    public void renderPrdDetailsView(String prdDetail){

        Context ctx = getContext();

        if (!Utility.safeTrim(prdDetail).equals(Constants.EMPTY_STRING)){

            final LinearLayout parentLayout = (LinearLayout) rootView.findViewById(R.id.scrollView_layout_prd_details);

            LinearLayout linearLayout = new LinearLayout(ctx);
            linearLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            linearLayout.setOrientation(LinearLayout.VERTICAL);

            //Add the product data fields
            String[] prdLocales = prdDetail.split(Constants.REG_EX_DYNAFIELDS_LIST_VALUES_LOCALE_SEPERATOR);
            String prdComoponents = null;
            String enPrdDetails = prdLocales[0];
            String esPrdDetails = prdLocales[1];

            String locale = ApplicationInitializer.getDeviceLocale();
            if (Utility.safeTrim(locale).equals(Constants.LOCALE_ESPANISH_SPAIN)){
                prdComoponents = esPrdDetails;
            }
            else{
                prdComoponents = enPrdDetails;
            }

            String[] prdComoponentsSplit = prdComoponents.split(Constants.REG_EX_FIELDID_VALUE_DATASET_SEPERATOR);

            for (String fieldIdValue : prdComoponentsSplit){
                String[] fieldIdValueArr = fieldIdValue.split(Constants.REG_EX_FIELDID_VALUE_SEPERATOR);
                String fieldName = fieldIdValueArr[0];
                String fieldValue = "";
                if (fieldIdValueArr.length > 1){
                    fieldValue = fieldIdValueArr[1];
                }

                TextView prdFieldNameView = new TextView(ctx);
                prdFieldNameView.setText(fieldName);
                prdFieldNameView.setTypeface(Typeface.DEFAULT_BOLD);
                prdFieldNameView.setTextSize(18);
                //prdFieldNameView.setPadding(20, 500, 50, 50);
                prdFieldNameView.setTextColor(parseColor("#275b89"));
                linearLayout.addView(prdFieldNameView);

                TextView prdFieldValueView = new TextView(ctx);
                prdFieldValueView.setText(fieldValue);
                prdFieldValueView.setTextSize(18);
                prdFieldValueView.setPadding(25, 40, 5, 70);
                prdFieldValueView.setTextColor(parseColor("#000000"));

                if (Utility.safeTrim(fieldValue).equals(Constants.EMPTY_STRING)){
                    prdFieldValueView.setText(R.string.data_not_avialable);
                    prdFieldValueView.setTextColor(parseColor("#a84b4b"));
                    prdFieldValueView.setTypeface(null, Typeface.ITALIC);
                }

                linearLayout.addView(prdFieldValueView);

            }

            parentLayout.addView(linearLayout);

        }

    }

}
