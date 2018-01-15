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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomersListAction {

    private final String LOG_TAG = CustomersListAction.class.getSimpleName();

    public static Context context = null;

    JobReportingDao dao = null;

    private List<String> customerNames = new ArrayList<>();
    private Map<String, String> customersDetails = new HashMap<>();


    public CustomersListAction(Context context){

        this.context = context;
        dao = new JobReportingDao(this.context);

    }

    public void execute(){

        try{
            byte[] cstmrDetailsBlob = dao.fetchDynaData(Constants.DYNATABLE_TYPE_CUSTOMER);

            if (null != cstmrDetailsBlob){
                Object obj = Utility.deSerializeObjToForBlob(cstmrDetailsBlob);
                if (obj instanceof Map){
                    customersDetails = (Map<String, String>)obj;

                    for (String name : customersDetails.keySet()){
                        customerNames.add(name);

                    }

                    LogManager.log(LOG_TAG, "Total no. of customers found: " + customersDetails.size(), Log.DEBUG);

                    this.setCustomerNames(customerNames);
                    this.setCustomersDetails(customersDetails);
                }
                else{
                    throw new Exception("Invalid object type returned for Customer dyna details.");
                }

            }
            else{
                throw new Exception("Not able to receive the dyna data for Customers");
            }

        }
        catch (Exception ex){
            LogManager.log(LOG_TAG, "CustomersListAction->Exception occurred : " + ex.getMessage(), Log.ERROR);
        }
        catch (Throwable th){
            LogManager.log(LOG_TAG, "CustomersListAction->Throwable occurred : " + th.getMessage(), Log.ERROR);
        }

    }


    public List<String> getCustomerNames() {

        return customerNames;
    }

    public void setCustomerNames(List<String> customerNames) {

        this.customerNames = customerNames;
    }

    public Map<String, String> getCustomersDetails() {

        return customersDetails;
    }

    public void setCustomersDetails(Map<String, String> customersDetails) {
        this.customersDetails = customersDetails;
    }

}
