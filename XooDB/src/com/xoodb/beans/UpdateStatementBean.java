/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.xoodb.beans;

import java.util.LinkedHashMap;
import java.util.LinkedList;

/**
 *
 * @author dhanoopbhaskar
 */
public class UpdateStatementBean implements SQLQuery {
    private String tableName = "";
    private LinkedHashMap<String, String> columnValues =
            new LinkedHashMap<String, String>();
    private LinkedList conditions = new LinkedList();

    /**
     * @return the tableName
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * @param tableName the tableName to set
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /**
     * @return the columnValues
     */
    public LinkedHashMap<String, String> getColumnValues() {
        return columnValues;
    }

    /**
     * @param columnValues the columnValues to set
     */
    public void setColumnValues(LinkedHashMap<String, String> columnValues) {
        this.columnValues = columnValues;
    }

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
}
