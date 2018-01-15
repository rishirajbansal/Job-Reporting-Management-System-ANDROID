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

public class ProductsListAction {

    private final String LOG_TAG = ProductsListAction.class.getSimpleName();

    public static Context context = null;

    JobReportingDao dao = null;

    private List<String> productNames = new ArrayList<>();
    private Map<String, String> productsDetails = new HashMap<>();


    public ProductsListAction(Context context){

        this.context = context;
        dao = new JobReportingDao(this.context);

    }

    public void execute(){

        try{
            byte[] prdDetailsBlob = dao.fetchDynaData(Constants.DYNATABLE_TYPE_PRODUCT);

            if (null != prdDetailsBlob){
                Object obj = Utility.deSerializeObjToForBlob(prdDetailsBlob);
                if (obj instanceof Map){
                    productsDetails = (Map<String, String>)obj;

                    for (String name : productsDetails.keySet()){
                        productNames.add(name);

                    }

                    LogManager.log(LOG_TAG, "Total no. of products found: " + productsDetails.size(), Log.DEBUG);

                    this.setProductNames(productNames);
                    this.setProductsDetails(productsDetails);
                }
                else{
                    throw new Exception("Invalid object type returned for Product dyna details.");
                }

            }
            else{
                throw new Exception("Not able to receive the dyna data for Products");
            }

        }
        catch (Exception ex){
            LogManager.log(LOG_TAG, "ProductsListAction->Exception occurred : " + ex.getMessage(), Log.ERROR);
        }
        catch (Throwable th){
            LogManager.log(LOG_TAG, "ProductsListAction->Throwable occurred : " + th.getMessage(), Log.ERROR);
        }

    }


    public List<String> getProductNames() {
        return productNames;
    }

    public void setProductNames(List<String> productNames) {

        this.productNames = productNames;
    }

    public Map<String, String> getProductsDetails() {

        return productsDetails;
    }

    public void setProductsDetails(Map<String, String> productsDetails) {
        this.productsDetails = productsDetails;
    }

}
