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
import com.jobreporting.business.actions.TasksListAction;
import com.jobreporting.business.common.LogManager;

import java.util.List;
import java.util.Map;


public class TskMgmtActivityFragment extends Fragment {

    private final String LOG_TAG = TskMgmtActivityFragment.class.getSimpleName();

    private View rootView = null;

    private List<String> tasksNames = null;
    private Map<String, String> tasksDetails = null;

    public TskMgmtActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_tsk_mgmt, container, false);

        generateTasksList();

        return rootView;

    }

    public void generateTasksList(){

        LogManager.log(LOG_TAG, "Fetching the tasks list...", Log.DEBUG);

        TasksListAction action = new TasksListAction(getActivity());
        action.execute();

        tasksNames = action.getTaskNames();
        tasksDetails = action.getTasksDetails();

        ArrayAdapter<String> taskListAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_view_tsks, R.id.list_item_tsks_textview, tasksNames);

        ListView listView = (ListView)rootView.findViewById(R.id.listview_tsk_mgmt);
        listView.setAdapter(taskListAdapter);

        listView.setOnItemClickListener(mListViewClickedHandler);

    }

    private AdapterView.OnItemClickListener mListViewClickedHandler = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView parent, View v, int position, long id) {

            LogManager.log(LOG_TAG, "List item selected : " + id, Log.DEBUG);

            String tskName = tasksNames.get((int)id);
            LogManager.log(LOG_TAG, "Task Name -> " + tskName, Log.DEBUG);

            String tskDetails = tasksDetails.get(tskName);
            LogManager.log(LOG_TAG, "Task Details -> " + tskDetails, Log.DEBUG);

            //Redirect to Task Details Activity
            Intent intent = new Intent(getContext(), TskDetailsActivity.class);
            intent.putExtra(Constants.INTENT_EXTRA_CUSTOM_TSK_DETAILS, tskDetails);
            startActivity(intent);

        }
    };


}
