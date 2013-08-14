/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.xoodb.query;

import java.util.ArrayList;

/**
 *
 * @author dhanoopbhaskar
 */
public class ResultSet {
    public String[] columnNames = null;
    public String[][] tableData = null;
    public ArrayList data = new ArrayList();
    private int pointer = -1;
    private int noOfRows = -1;
    private int noOfCols = -1;

    public boolean next() {
        boolean isNext = false;        
        pointer++;

        if(pointer < noOfRows) {
            isNext = true;
        }
        
        return isNext;
    }

    /**
     *
     * @param colNo
     * @return
     */
    public String getString(int colNo) {        
        if(colNo >= 0 && colNo < noOfCols) {
            return tableData[pointer][colNo];
        } else {
            return null;
        }
    }

    /**
     * 
     * @param colName
     * @return
     */
    public String getString(String colName) {
        String value = null;
        int colNo = -1;

        for(int i = 0 ; i < noOfCols ; i++) {
            if(colName.equals(columnNames[i])) {
                colNo = i;
            }
        }

        if(colNo != -1) {
            value = tableData[pointer][colNo];
        }

        return value;
    }
 
    public int getColumnCount() {
        return noOfCols;
    }
    
    public int getRowCount() {
        return noOfRows;
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
    public String[] getColumnNames() {
        return columnNames;
    }

    /**
     * @param tableColumnNames the tableColumnNames to set
     */
    public void setColumnNames(String[] columnNames) {
        this.columnNames = columnNames;
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

        if(tableData != null && tableData.length > 0) {
            noOfRows = tableData.length;
            String[] temp = tableData[0];
            int i = 0;
            for(String cell: temp) {
                i++;
            }
            noOfCols = i;
        }
    }    
}
