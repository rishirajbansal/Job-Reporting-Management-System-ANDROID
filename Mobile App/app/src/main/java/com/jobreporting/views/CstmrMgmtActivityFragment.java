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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.jobreporting.R;
import com.jobreporting.base.Constants;
import com.jobreporting.business.actions.CustomersListAction;
import com.jobreporting.business.common.LogManager;

import java.util.List;
import java.util.Map;


public class CstmrMgmtActivityFragment extends Fragment {

    private final String LOG_TAG = CstmrMgmtActivityFragment.class.getSimpleName();

    private View rootView = null;

    private List<String> customerNames = null;
    private Map<String, String> customersDetails = null;

    public CstmrMgmtActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_cstmr_mgmt, container, false);

        generateCustomerList();

        return rootView;

    }

    public void generateCustomerList(){

        LogManager.log(LOG_TAG, "Fetching the customer list...", Log.DEBUG);

        CustomersListAction action = new CustomersListAction(getActivity());
        action.execute();

        customerNames = action.getCustomerNames();
        customersDetails = action.getCustomersDetails();

        ArrayAdapter<String> customerListAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_view_cstmrs, R.id.list_item_cstmrs_textview, customerNames);

        ListView listView = (ListView)rootView.findViewById(R.id.listview_cstmr_mgmt);
        listView.setAdapter(customerListAdapter);

        listView.setOnItemClickListener(mListViewClickedHandler);

    }

    private AdapterView.OnItemClickListener mListViewClickedHandler = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView parent, View v, int position, long id) {

            LogManager.log(LOG_TAG, "List item selected : " + id, Log.DEBUG);

            String cstmrName = customerNames.get((int)id);
            LogManager.log(LOG_TAG, "Customer Name -> " + cstmrName, Log.DEBUG);

            String cstmrDetails = customersDetails.get(cstmrName);
            LogManager.log(LOG_TAG, "Customer Details -> " + cstmrDetails, Log.DEBUG);

            //Redirect to Customer Details Activity
            Intent intent = new Intent(getContext(), CstmrDetailsActivity.class);
            intent.putExtra(Constants.INTENT_EXTRA_CUSTOM_CSTMR_DETAILS, cstmrDetails);
            startActivity(intent);

        }
    };

}
