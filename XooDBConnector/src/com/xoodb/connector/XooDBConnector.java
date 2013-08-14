/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.xoodb.connector;

import com.xoodb.query.ResultSet;
import com.xoodb.beans.ResultBean;
import com.xoodb.conn.AppConstants;
import com.xoodb.conn.ConnectionManager;
import com.xoodb.conn.ResponseListener;
import com.xoodb.exception.ExceptionHandler;
import com.xoodb.exception.XooDBSQLException;
import com.xoodb.query.QueryHandler;
import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dhanoopbhaskar
 */
public class XooDBConnector implements ResponseListener {
    public String userName = "";
    public String databaseName = "";
    public String tableName = "";
    private String password = "";
    private String ipAddress = "";
    private int portNumber;
    public static ConnectionManager connectionManager = null;
    private boolean isAnswerAvailable = false;
    private boolean notSelectStatement = false;
    QueryHandler queryHandler = null;
    ResultBean resultBean = null;
    ExceptionHandler exceptionHandler = null;
    private boolean isResultBean = false;
    private boolean isError = false;
    private String errorMsg = null;

    public XooDBConnector(String ipAddress, int portNumber, String databaseName,
            String userName, String password) throws XooDBSQLException {
        this.ipAddress = ipAddress;
        this.portNumber = portNumber;
        this.databaseName = databaseName;
        this.userName = userName;
        this.password = password;
        connectionManager = new ConnectionManager();
        queryHandler = new QueryHandler(this);
        exceptionHandler = new ExceptionHandler(this);
        Thread thread = new Thread(exceptionHandler);
        thread.start();
    }

    public String getDatabaseName() {
        return databaseName;
    }
    
    public String getUserName() {
        return userName;
    }

    public void connectToXooDB() throws XooDBSQLException {
        try {
            connectionManager.setResponseListener(this);
            connectionManager.connectToServer(ipAddress, portNumber);
            connectionManager.writeToServer("LOGIN:" + userName + "@" + password);
            Thread thread = new Thread(connectionManager);
            thread.start();
        } catch (UnknownHostException ex) {
            Logger.getLogger(XooDBConnector.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ConnectException ex) {
            //System.out.println("Connection refused");
            //System.exit(0);
            XooDBSQLException xooDBSQLException = new XooDBSQLException();
            xooDBSQLException.setMessage("Connection refused");
            throw xooDBSQLException;
        } catch (IOException ex) {
            Logger.getLogger(XooDBConnector.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void close() {
        System.exit(0);
    }

    public ResultSet executeQuery(String query) throws XooDBSQLException {
        ResultSet resultSet = new ResultSet();
        queryHandler.executeStatement(query);
        
        while(true) {
	    System.out.print("");
            if(isAnswerAvailable) {
                resultSet = queryHandler.getResultSet();
                break;
            }
        }                

        if(isError) {
            XooDBSQLException xooDBSQLException = new XooDBSQLException();
            xooDBSQLException.setMessage(errorMsg);
            throw xooDBSQLException;
        }
        
        return resultSet;
    }

    public void executeUpdate(String query) throws XooDBSQLException {
        queryHandler.executeStatement(query);

        if(isError) {
            XooDBSQLException xooDBSQLException = new XooDBSQLException();
            xooDBSQLException.setMessage(errorMsg);
            throw xooDBSQLException;
        }
    }

    public void response(Object response) {
        if (response instanceof String) {
            if (((String) response).equals(AppConstants.FAIL)) {
                try {
                    XooDBSQLException xooDBSQLException = new XooDBSQLException();
                    xooDBSQLException.setMessage("Connection to XooDB failed");
                    throw xooDBSQLException;
                } catch (XooDBSQLException ex) {
                    Logger.getLogger(XooDBConnector.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else if (response instanceof ResultBean) {            
            ResultBean resultBean2 = (ResultBean) response;
            if(resultBean2.getType().equalsIgnoreCase("TABLE")) {
                queryHandler.display(resultBean2);
            } else if(resultBean2.getType().equalsIgnoreCase("SHOW DATABASES")) {
                setResultBean(resultBean2);
                setIsResultBean(true);
            }
        }
    }

    public void setIsAnsAvailable(boolean status) {
        isAnswerAvailable = status;
    }

    public boolean getIsAnsAvailable() {
        return isAnswerAvailable;
    }

    public void setIsResultBean(boolean status) {
        isResultBean = status;
    }

    public boolean getIsResultBean() {
        return isResultBean;
    }

    public ResultBean getResultBean() {
        return resultBean;
    }

    public void setResultBean(ResultBean resultBean) {
        this.resultBean = resultBean;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setIsError(boolean isError) {
        this.isError = isError;
    }

    public boolean getIsError() {
        return isError;
    }
}
