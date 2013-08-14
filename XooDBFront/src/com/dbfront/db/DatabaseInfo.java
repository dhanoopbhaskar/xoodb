/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dbfront.db;

import java.util.LinkedHashMap;

/**
 * 
 * @author dhanoopbhaskar
 */
public class DatabaseInfo {

    private String dbName = "";
    private LinkedHashMap<String, TableInfo> tables = new LinkedHashMap<String, TableInfo>();

    /**
     * @return the dbName
     */
    public String getDbName() {
        return dbName;
    }

    /**
     * @param dbName the dbName to set
     */
    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    /**
     * @return the tables
     */
    public LinkedHashMap<String, TableInfo> getTables() {
        return tables;
    }

    /**
     * @param tables the tables to set
     */
    public void setTables(LinkedHashMap<String, TableInfo> tables) {
        this.tables = tables;
    }

}
