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

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jobreporting.R;
import com.jobreporting.base.Constants;


public class ServerErrorActivityFragment extends Fragment {

    public ServerErrorActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_server_error, container, false);

        Bundle intentBundle = getActivity().getIntent().getExtras();

        String code = intentBundle.getString(Constants.INTENT_EXTRA_CUSTOM_SERVER_ERROR_CODE);
        String message = intentBundle.getString(Constants.INTENT_EXTRA_CUSTOM_SERVER_MESSAGE);
        String error = intentBundle.getString(Constants.INTENT_EXTRA_CUSTOM_SERVER_ERROR);
        String occurred = intentBundle.getString(Constants.INTENT_EXTRA_CUSTOM_SERVER_ERROR_OCCURRED);

        final LinearLayout parentLayout = (LinearLayout) rootView.findViewById(R.id.scrollView_layout_server_error);
        TextView codeCtrl = (TextView)parentLayout.findViewById(R.id.label_server_error_code_value);
        TextView msgCtrl = (TextView)parentLayout.findViewById(R.id.label_server_message_value);
        TextView errCtrl = (TextView)parentLayout.findViewById(R.id.label_server_error_value);
        TextView occCtrl = (TextView)parentLayout.findViewById(R.id.label_server_error_occurred_value);

        codeCtrl.setText(code);
        msgCtrl.setText(message);
        errCtrl.setText(error);
        occCtrl.setText(occurred);

        return rootView;

    }
}
