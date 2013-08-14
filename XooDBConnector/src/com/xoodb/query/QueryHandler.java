/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.xoodb.query;

import com.xoodb.beans.QueryBean;
import com.xoodb.beans.ResultBean;
import com.xoodb.conn.ResponseListener;
import com.xoodb.connector.XooDBConnector;
import com.xoodb.exception.XooDBSQLException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author dhanoopbhaskar
 */
public class QueryHandler implements ResponseListener {
    QueryBean queryBean = new QueryBean();
    ResultSet resultSet = null;
    XooDBConnector xooDBConnector = null;

    public QueryHandler(XooDBConnector xooDBConnector) {
        this.xooDBConnector = xooDBConnector;
    }

    public void executeStatement(String query) {
        xooDBConnector.setIsAnsAvailable(false);
        xooDBConnector.setIsError(false);
        queryBean.setDatabaseName(xooDBConnector.getDatabaseName());
        if(query.indexOf(";") != -1) {
            query = query.substring(0, query.lastIndexOf(";"));
        }

        if(query.indexOf("\n") != -1) {
            query.replaceAll("\n", " ");
        }

        query = query.trim();

        if (query.toLowerCase().startsWith("select")) {
            queryBean.setQueryType("SELECT");
        } else if (query.toLowerCase().startsWith("show")) {
            System.out.println("show command sent");
            queryBean.setQueryType("SHOW DATABASES");            
        } else {
            queryBean.setQueryType("");
        }
        queryBean.setQuery(query);
        XooDBConnector.connectionManager.setResponseListener(this);
        XooDBConnector.connectionManager.writeToServer(queryBean);
    }

    public void response(Object response) {
        if (response instanceof String) {
            try {
                //System.out.println(response);
                XooDBSQLException xooDBSQLException = new XooDBSQLException();
                xooDBSQLException.setMessage((String) response);
                throw xooDBSQLException;
            } catch (XooDBSQLException ex) {
                System.out.println(ex);
                JOptionPane.showMessageDialog(new JFrame(), ex.getMessage(), 
                        "XooDBSQLException",
                        JOptionPane.ERROR_MESSAGE);
                xooDBConnector.setIsError(true);
                xooDBConnector.setErrorMsg(ex.getMessage());
            }
        } else if (response instanceof ResultBean) {
            ResultBean resultBean = (ResultBean) response;            
            //System.out.println("Type: " + resultBean.getType());
            if(resultBean.getType().equalsIgnoreCase("TABLE")) {
                changeFormat(resultBean);
                xooDBConnector.setIsAnsAvailable(true);
            } 
        }
    }

    public void changeFormat(ResultBean resultBean) {
        ResultSet resultSet1 = new ResultSet();
        resultSet1.setColumnNames(resultBean.getTableColumnNames());
        resultSet1.setTableData(resultBean.getTableData());
        this.resultSet = resultSet1;
    }

    public ResultSet getResultSet() {
        return resultSet;
    }

    public void display(ResultBean resultBean) {
        String[][] data = resultBean.getTableData();        
        for(String[] row: data) {
            for(String cell : row) {
                System.out.print(cell + " ");
            }
            System.out.print("\n");
        }
    }
}
