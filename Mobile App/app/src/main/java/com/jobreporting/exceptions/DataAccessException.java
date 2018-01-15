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

package com.jobreporting.exceptions;


public class DataAccessException extends ApplicationException{

    protected Throwable throwable = null;

    String error = "";

    /**
     * Constructor for DataAccessException
     * @param msg - Message associated with the exception
     */
    public DataAccessException(String msg) {
        super(msg);
        error = msg;
    }

    /**
     * Initializes a newly created <code>DataAccessException</code> object.
     * @param	msg - the message associated with the Exception.
     * @param   cause - Throwable object
     */
    public DataAccessException(String msg, Throwable cause) {
        super(msg, cause);
    }


    /**
     * Returns the error message string of the exception object.
     *
     * @return  the error message string of this <code>Exception</code>
     *          object if it was <code>Exception</code> with an
     *          error message string; or <code>null</code> if it was
     *          <code>Exception</code> created} with no error message.
     *
     */
    public String getMessage() {
    	/*if (throwable == null) {
    		return GenericConstants.EMPTY_STRING;
        }*/

        return error;
    }
}
