/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.xoodb.beans;

import java.util.LinkedList;

/**
 *
 * @author dhanoopbhaskar
 */
public class DeleteStatementBean implements SQLQuery {
    private String tableName = null;
    private LinkedList conditions = new LinkedList();

    /**
     * @param conditions the conditions to set
     */
    public void setConditions(LinkedList conditions) {
        this.conditions = conditions;
    }

    /**
     * @return the conditions
     */
    public LinkedList getConditions() {
        return conditions;
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
