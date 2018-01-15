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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.jobreporting.R;
import com.jobreporting.business.actions.AuthAction;
import com.jobreporting.business.common.LogManager;
import com.jobreporting.utilities.Utility;


public class AuthActivityFragment extends Fragment {

    private final String LOG_TAG = AuthActivityFragment.class.getSimpleName();

    private View rootView = null;

    public AuthActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_auth, container, false);

        Button submitButton = (Button)rootView.findViewById(R.id.btn_auth_submit);
        submitButton.setOnClickListener(mBtnListener);

        Button resetButton = (Button)rootView.findViewById(R.id.btn_auth_reset);
        resetButton.setOnClickListener(mBtnListener);


        return rootView;
    }

    private View.OnClickListener mBtnListener = new View.OnClickListener() {

        public void onClick(View view) {

            switch (view.getId()) {

                case R.id.btn_auth_submit:

                    authSubmit();

                    break;

                case R.id.btn_auth_reset:

                    reset();

                    break;

                default:
                    break;

            }

        }
    };

    public void authSubmit(){

        LogManager.log(LOG_TAG, "One time Authentication being started...", Log.DEBUG);

        //Get the Org Name
        EditText nameField = (EditText) rootView.findViewById(R.id.text_auth_org_name);
        Editable nameEditable = nameField.getText();
        String orgName = nameEditable.toString();

        //Get the Worker Name
        nameField = (EditText) rootView.findViewById(R.id.text_auth_wrk_name);
        nameEditable = nameField.getText();
        String wrkName = nameEditable.toString();

        LogManager.log(LOG_TAG, "Org Name : ", Log.DEBUG);
        LogManager.log(LOG_TAG, "Worker Name : ", Log.DEBUG);

        AuthAction action = new AuthAction(getActivity(), getActivity());
        action.setOrgName(orgName);
        action.setWrkName(wrkName);

        action.execute();

    }

    public void reset(){

        Utility.resetForm((ViewGroup)rootView.findViewById(R.id.scrollView_home));

    }



}
