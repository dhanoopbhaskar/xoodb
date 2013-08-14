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
public class TableColumnBean implements Serializable {
    private String columnName = null;
    private String dataType = null;
    private int size = 0;
    private String defaultValue = null;
    private boolean nullAllowed = false;
    private boolean autoIncrement = false;
    private boolean privateKey = false;

    /**
     * @return the columnName
     */
    public String getColumnName() {
        return columnName;
    }

    /**
     * @param columnName the columnName to set
     */
    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    /**
     * @return the dataType
     */
    public String getDataType() {
        return dataType;
    }

    /**
     * @param dataType the dataType to set
     */
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    /**
     * @return the defaultValue
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * @param defaultValue the defaultValue to set
     */
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * @return the nullAllowed
     */
    public boolean isNullAllowed() {
        return nullAllowed;
    }

    /**
     * @param nullAllowed the nullAllowed to set
     */
    public void setNullAllowed(boolean nullAllowed) {
        this.nullAllowed = nullAllowed;
    }

    /**
     * @return the autoIncrement
     */
    public boolean isAutoIncrement() {
        return autoIncrement;
    }

    /**
     * @param autoIncrement the autoIncrement to set
     */
    public void setAutoIncriment(boolean autoIncrement) {
        this.autoIncrement = autoIncrement;
    }

    /**
     * @return the privateKey
     */
    public boolean isPrivateKey() {
        return privateKey;
    }

    /**
     * @param privateKey the privateKey to set
     */
    public void setPrivateKey(boolean privateKey) {
        this.privateKey = privateKey;
    }

    /**
     * @return the size
     */
    public int getSize() {
        return size;
    }

    /**
     * @param size the size to set
     */
    public void setSize(int size) {
        this.size = size;
    }
}
