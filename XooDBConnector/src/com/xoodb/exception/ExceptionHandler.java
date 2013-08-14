/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.xoodb.exception;

import com.xoodb.connector.XooDBConnector;

/**
 *
 * @author dhanoopbhaskar
 */
public class ExceptionHandler implements Runnable {
    XooDBConnector xooDBConnector = null;

    public ExceptionHandler(XooDBConnector xooDBConnector) {
        this.xooDBConnector = xooDBConnector;
    }
    
    public void run() {
//        if(xooDBConnector.getIsError()) {
//            XooDBSQLException xooDBSQLException = new XooDBSQLException();
//            xooDBSQLException.setMessage(xooDBConnector.getErrorMsg());
//            throw xooDBSQLException;
//        }
    }

}
