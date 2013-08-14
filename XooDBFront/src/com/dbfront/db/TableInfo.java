/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dbfront.db;

import java.util.LinkedHashMap;
import java.util.LinkedList;

/**
 * 
 * @author dhanoopbhaskar
 */
public class TableInfo {

    private String tableName = "";
    private LinkedHashMap<String, FieldInfo> fields =
            new LinkedHashMap<String, FieldInfo>();
    private LinkedList primaryKeys = new LinkedList();
    private LinkedHashMap<String, ForeignKey> foreignKeys =
            new LinkedHashMap<String, ForeignKey>();

    public LinkedHashMap<String, ForeignKey> getForeignKeys() {
        return foreignKeys;
    }

    public void setForeignKeys(LinkedHashMap<String, ForeignKey> foreignKeys) {
        this.foreignKeys = foreignKeys;
    }

    public LinkedList getPrimaryKeys() {
        return primaryKeys;
    }

    public void setPrimaryKeys(LinkedList primaryKeys) {
        this.primaryKeys = primaryKeys;
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
     * @return the fields
     */
    public LinkedHashMap<String, FieldInfo> getFields() {
        return fields;
    }

    /**
     * @param fields the fields to set
     */
    public void setFields(LinkedHashMap<String, FieldInfo> fields) {
        this.fields = fields;
    }
}
