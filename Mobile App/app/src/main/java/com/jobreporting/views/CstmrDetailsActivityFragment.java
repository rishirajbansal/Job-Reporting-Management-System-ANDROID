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


public class CstmrDetailsActivityFragment extends Fragment {

    private final String LOG_TAG = CstmrDetailsActivityFragment.class.getSimpleName();

    private View rootView = null;

    public CstmrDetailsActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_cstmr_details, container, false);

        Bundle intentBundle = getActivity().getIntent().getExtras();

        String cstmrDetail = intentBundle.getString(Constants.INTENT_EXTRA_CUSTOM_CSTMR_DETAILS);

        renderCstmrDetailsView(cstmrDetail);

        return rootView;

    }

    public void renderCstmrDetailsView(String cstmrDetail){

        Context ctx = getContext();

        if (!Utility.safeTrim(cstmrDetail).equals(Constants.EMPTY_STRING)){

            final LinearLayout parentLayout = (LinearLayout) rootView.findViewById(R.id.scrollView_layout_cstmr_details);

            LinearLayout linearLayout = new LinearLayout(ctx);
            linearLayout.setLayoutParams(new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT));
            linearLayout.setOrientation(LinearLayout.VERTICAL);

            //Add the customer data fields
            String[] cstmrLocales = cstmrDetail.split(Constants.REG_EX_DYNAFIELDS_LIST_VALUES_LOCALE_SEPERATOR);
            String cstmrComoponents = null;
            String enCstmrDetails = cstmrLocales[0];
            String esCstmrDetails = cstmrLocales[1];

            String locale = ApplicationInitializer.getDeviceLocale();
            if (Utility.safeTrim(locale).equals(Constants.LOCALE_ESPANISH_SPAIN)){
                cstmrComoponents = esCstmrDetails;
            }
            else{
                cstmrComoponents = enCstmrDetails;
            }

            String[] cstmrComoponentsSplit = cstmrComoponents.split(Constants.REG_EX_FIELDID_VALUE_DATASET_SEPERATOR);

            for (String fieldIdValue : cstmrComoponentsSplit){
                String[] fieldIdValueArr = fieldIdValue.split(Constants.REG_EX_FIELDID_VALUE_SEPERATOR);
                String fieldName = fieldIdValueArr[0];
                String fieldValue = "";
                if (fieldIdValueArr.length > 1){
                    fieldValue = fieldIdValueArr[1];
                }

                TextView cstmrFieldNameView = new TextView(ctx);
                cstmrFieldNameView.setText(fieldName);
                cstmrFieldNameView.setTypeface(Typeface.DEFAULT_BOLD);
                cstmrFieldNameView.setTextSize(18);
                //cstmrFieldNameView.setPadding(20, 500, 50, 50);
                cstmrFieldNameView.setTextColor(parseColor("#275b89"));
                linearLayout.addView(cstmrFieldNameView);

                TextView cstmrFieldValueView = new TextView(ctx);
                cstmrFieldValueView.setText(fieldValue);
                cstmrFieldValueView.setTextSize(18);
                cstmrFieldValueView.setPadding(25, 40, 5, 70);
                cstmrFieldValueView.setTextColor(parseColor("#000000"));

                if (Utility.safeTrim(fieldValue).equals(Constants.EMPTY_STRING)){
                    cstmrFieldValueView.setText(R.string.data_not_avialable);
                    cstmrFieldValueView.setTextColor(parseColor("#a84b4b"));
                    cstmrFieldValueView.setTypeface(null, Typeface.ITALIC);
                }

                linearLayout.addView(cstmrFieldValueView);

            }

            parentLayout.addView(linearLayout);

        }

    }

}
