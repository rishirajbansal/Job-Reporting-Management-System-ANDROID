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
import com.jobreporting.business.actions.ProductsListAction;
import com.jobreporting.business.common.LogManager;

import java.util.List;
import java.util.Map;


public class PrdsMgmtActivityFragment extends Fragment {

    private final String LOG_TAG = PrdsMgmtActivityFragment.class.getSimpleName();

    private View rootView = null;

    private List<String> productNames = null;
    private Map<String, String> productsDetails = null;

    public PrdsMgmtActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_prds_mgmt, container, false);

        generateProductList();

        return rootView;

    }

    public void generateProductList(){

        LogManager.log(LOG_TAG, "Fetching the product list...", Log.DEBUG);

        ProductsListAction action = new ProductsListAction(getActivity());
        action.execute();

        productNames = action.getProductNames();
        productsDetails = action.getProductsDetails();

        ArrayAdapter<String> productListAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_view_prds, R.id.list_item_prds_textview, productNames);

        ListView listView = (ListView)rootView.findViewById(R.id.listview_prd_mgmt);
        listView.setAdapter(productListAdapter);

        listView.setOnItemClickListener(mListViewClickedHandler);

    }

    private AdapterView.OnItemClickListener mListViewClickedHandler = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView parent, View v, int position, long id) {

            LogManager.log(LOG_TAG, "List item selected : " + id, Log.DEBUG);

            String prdName = productNames.get((int)id);
            LogManager.log(LOG_TAG, "Product Name -> " + prdName, Log.DEBUG);

            String prdDetails = productsDetails.get(prdName);
            LogManager.log(LOG_TAG, "Product Details -> " + prdDetails, Log.DEBUG);

            //Redirect to Product Details Activity
            Intent intent = new Intent(getContext(), PrdDetailsActivity.class);
            intent.putExtra(Constants.INTENT_EXTRA_CUSTOM_PRD_DETAILS, prdDetails);
            startActivity(intent);

        }
    };

}
