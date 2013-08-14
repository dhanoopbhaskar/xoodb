/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.xoodb.beans;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author dhanoopbhaskar
 */
public class ResultBean implements Serializable {
    private String type = "";
    private ArrayList data = new ArrayList();
    private String[] tableColumnNames = null;
    private String[][] tableData = null;

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @return the data
     */
    public ArrayList getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(ArrayList data) {
        this.data = data;
    }

    /**
     * @return the tableColumnNames
     */
    public String[] getTableColumnNames() {
        return tableColumnNames;
    }

    /**
     * @param tableColumnNames the tableColumnNames to set
     */
    public void setTableColumnNames(String[] tableColumnNames) {
        this.tableColumnNames = tableColumnNames;
    }

    /**
     * @return the tableData
     */
    public String[][] getTableData() {
        return tableData;
    }

    /**
     * @param tableData the tableData to set
     */
    public void setTableData(String[][] tableData) {
        this.tableData = tableData;
    }
}
