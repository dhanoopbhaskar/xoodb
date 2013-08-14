/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.xoodb.beans;

import java.io.Serializable;

/**
 *
 * @author dhanoopbhaskar
 */
public class TableBean extends SQLStatementBean implements Serializable {
    private String queryType = "";
    private String tableName = "";
    private TableColumnBean[] columns = null;

    /**
     * @return the queryType
     */
    public String getQueryType() {
        return queryType;
    }

    /**
     * @param queryType the queryType to set
     */
    public void setQueryType(String queryType) {
        this.queryType = queryType;
    }

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
     * @return the columns
     */
    public TableColumnBean[] getColumns() {
        return columns;
    }

    /**
     * @param columns the columns to set
     */
    public void setColumns(TableColumnBean[] columns) {
        this.columns = columns;
    }
}
