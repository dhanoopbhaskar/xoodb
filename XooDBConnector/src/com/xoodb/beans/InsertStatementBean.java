/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.xoodb.beans;

/**
 *
 * @author dhanoopbhaskar
 */
public class InsertStatementBean extends SQLStatementBean {
    private String tableName = null;
    private String[] columnNames = null;
    private String[] values = null;

    /**
     * @param tableName the tableName to set
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /**
     * @return the tableName
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * @param columnNames the columnNames to set
     */
    public void setColumnNames(String[] columnNames) {
        this.columnNames = columnNames;
    }

    /**
     * @return the columnNames
     */
    public String[] getColumnNames() {
        return columnNames;
    }

    /**
     * @param values the values to set
     */
    public void setValues(String[] values) {
        this.values = values;
    }

    /**
     * @return the values
     */
    public String[] getValues() {
        return values;
    }
}
