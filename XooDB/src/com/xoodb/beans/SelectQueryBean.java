/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.xoodb.beans;

import java.io.Serializable;
import java.util.LinkedList;

/**
 *
 * @author dhanoopbhaskar
 */
public class SelectQueryBean implements SQLQuery, Serializable {
    private String tableName = "";
    private String[] columnNames = null;
    private LinkedList conditions = new LinkedList();

    /**
     * @return the conditions
     */
    public LinkedList getConditions() {
        return conditions;
    }

    /**
     * @param conditions the conditions to set
     */
    public void setConditions(LinkedList conditions) {
        this.conditions = conditions;
    }

    /**
     * @return the columnNames
     */
    public String[] getColumnNames() {
        return columnNames;
    }

    /**
     * @param columnNames the columnNames to set
     */
    public void setColumnNames(String[] columnNames) {
        this.columnNames = columnNames;
    }

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
}
