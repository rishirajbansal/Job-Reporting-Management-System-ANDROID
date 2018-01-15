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

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.jobreporting.R;
import com.jobreporting.base.Constants;
import com.jobreporting.business.common.LogManager;


public class HomeActivityFragment extends Fragment {

    private final String LOG_TAG = AuthActivityFragment.class.getSimpleName();

    private View rootView = null;

    public HomeActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_home, container, false);

        authenticationMessage();

        Button prdDetailsButton = (Button)rootView.findViewById(R.id.btn_home_prd_details);
        prdDetailsButton.setOnClickListener(mBtnListener);

        Button cstmrDetailsButton = (Button)rootView.findViewById(R.id.btn_home_cstmr_details);
        cstmrDetailsButton.setOnClickListener(mBtnListener);

        Button tskDetailsButton = (Button)rootView.findViewById(R.id.btn_home_tsk_details);
        tskDetailsButton.setOnClickListener(mBtnListener);

        Button subReprotButton = (Button)rootView.findViewById(R.id.btn_home_subReport);
        subReprotButton.setOnClickListener(mBtnListener);

        return rootView;

    }

    private View.OnClickListener mBtnListener = new View.OnClickListener() {

        public void onClick(View view) {

            switch (view.getId()) {

                case R.id.btn_home_prd_details:

                    showProductList();

                    break;

                case R.id.btn_home_cstmr_details:

                    showCustomerList();

                    break;

                case R.id.btn_home_tsk_details:

                    showTaskList();

                    break;

                case R.id.btn_home_subReport:

                    showReportForm();

                    break;

                default:
                    break;

            }

        }
    };

    public void showProductList(){

        //Call Products Mgmt Activity
        Intent intent = new Intent(getContext(), PrdsMgmtActivity.class);
        startActivity(intent);

    }

    public void showCustomerList(){

        //Call Customer Mgmt Activity
        Intent intent = new Intent(getContext(), CstmrMgmtActivity.class);
        startActivity(intent);

    }

    public void showTaskList(){

        //Call Task Mgmt Activity
        Intent intent = new Intent(getContext(), TskMgmtActivity.class);
        startActivity(intent);

    }

    public void showReportForm(){

        //Call Report form Activity
        Intent intent = new Intent(getContext(), RptFormActivity.class);
        startActivity(intent);

    }

    public void authenticationMessage(){

        Bundle intentBundle = getActivity().getIntent().getExtras();

        if (null != intentBundle){
            boolean isAlreadyAuthenticated = intentBundle.getBoolean(Constants.INTENT_EXTRA_CUSTOM_AUTH_SUCCESSFULL);

            if (!isAlreadyAuthenticated){
                LogManager.log(LOG_TAG, "First time authentication, toast will be displayed", Log.DEBUG);

                Toast authenticatedToast = Toast.makeText(getContext(), R.string.toast_message_authenticated, Toast.LENGTH_LONG);
                authenticatedToast.show();
            }
        }


    }
    
}
