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
public class QueryBean implements Serializable {
    private String query = null;
    private String databaseName = null;
    private String tableName = null;
    private String queryType = null;
    private String fieldName = null;
    private String cellData = null;
    private int selectedRow = -1;
    private int selectedColumn = -1;

    /**
     * @param query the query to set
     */
    public void setQuery(String query) {
        this.query = query;
    }

    /**
     * @return the query
     */
    public String getQuery() {
        return query;
    }

    /**
     * @param databaseName the databaseName to set
     */
    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    /**
     * @return the databaseName
     */
    public String getDatabaseName() {
        return databaseName;
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

    /**
     * @param queryType the queryType to set
     */
    public void setQueryType(String queryType) {
        this.queryType = queryType;
    }

    /**
     * @return the queryType
     */
    public String getQueryType() {
        return queryType;
    }

    /**
     * @param fieldName the fieldName to set
     */
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    /**
     * @return the fieldName
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * @param cellData the cellData to set
     */
    public void setCellData(String cellData) {
        this.cellData = cellData;
    }

    /**
     * @return the cellData
     */
    public String getCellData() {
        return cellData;
    }

    /**
     * @param selectedRow the selectedRow to set
     */
    public void setSelectedRow(int selectedRow) {
        this.selectedRow = selectedRow;
    }

    /**
     * @return the selectedRow
     */
    public int getSelectedRow() {
        return selectedRow;
    }

    /**
     * @param selectedColumn the selectedColumn to set
     */
    public void setSelectedColumn(int selectedColumn) {
        this.selectedColumn = selectedColumn;
    }

    /**
     * @return the selectedColumn
     */
    public int getSelectedColumn() {
        return selectedColumn;
    }
}
