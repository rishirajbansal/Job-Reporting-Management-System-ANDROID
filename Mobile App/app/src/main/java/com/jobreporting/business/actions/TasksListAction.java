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

package com.jobreporting.business.actions;


import android.content.Context;
import android.util.Log;

import com.jobreporting.base.Constants;
import com.jobreporting.business.common.LogManager;
import com.jobreporting.dao.JobReportingDao;
import com.jobreporting.utilities.Utility;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TasksListAction {

    private final String LOG_TAG = TasksListAction.class.getSimpleName();

    public static Context context = null;

    JobReportingDao dao = null;

    private List<String> taskNames = new ArrayList<>();
    private Map<String, String> tasksDetails = new LinkedHashMap<>();


    public TasksListAction(Context context){

        this.context = context;
        dao = new JobReportingDao(this.context);

    }

    public void execute(){

        try{
            byte[] tskDetailsBlob = dao.fetchDynaData(Constants.DYNATABLE_TYPE_TASK);

            if (null != tskDetailsBlob){
                Object obj = Utility.deSerializeObjToForBlob(tskDetailsBlob);
                if (obj instanceof Map){
                    tasksDetails = (Map<String, String>)obj;

                    for (String name : tasksDetails.keySet()){
                        taskNames.add(name);

                    }

                    LogManager.log(LOG_TAG, "Total no. of tasks found: " + tasksDetails.size(), Log.DEBUG);

                    this.setTaskNames(taskNames);
                    this.setTasksDetails(tasksDetails);
                }
                else{
                    throw new Exception("Invalid object type returned for Task dyna details.");
                }

            }
            else{
                throw new Exception("Not able to receive the dyna data for Tasks");
            }

        }
        catch (Exception ex){
            LogManager.log(LOG_TAG, "TasksListAction->Exception occurred : " + ex.getMessage(), Log.ERROR);
        }
        catch (Throwable th){
            LogManager.log(LOG_TAG, "TasksListAction->Throwable occurred : " + th.getMessage(), Log.ERROR);
        }

    }


    public List<String> getTaskNames() {
        return taskNames;
    }

    public void setTaskNames(List<String> taskNames) {
        this.taskNames = taskNames;
    }

    public Map<String, String> getTasksDetails() {
        return tasksDetails;
    }

    public void setTasksDetails(Map<String, String> tasksDetails) {
        this.tasksDetails = tasksDetails;
    }
}
