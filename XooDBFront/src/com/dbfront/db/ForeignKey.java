/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dbfront.db;

/**
 * 
 * @author dhanoopbhaskar
 */
public class ForeignKey {

    private String foreignKeyName = "";
    private String fkColumnName = "";
    private String parentTableName = "";
    private String parentPKColumName = "";

    /**
     * @return the foreignKeyName
     */
    public String getForeignKeyName() {
        return foreignKeyName;
    }

    /**
     * @param foreignKeyName the foreignKeyName to set
     */
    public void setForeignKeyName(String foreignKeyName) {
        this.foreignKeyName = foreignKeyName;
    }

    /**
     * @return the fkColumnName
     */
    public String getFkColumnName() {
        return fkColumnName;
    }

    /**
     * @param fkColumnName the fkColumnName to set
     */
    public void setFkColumnName(String fkColumnName) {
        this.fkColumnName = fkColumnName;
    }

    /**
     * @return the parantTableName
     */
    public String getParantTableName() {
        return parentTableName;
    }

    /**
     * @param parantTableName the parantTableName to set
     */
    public void setParantTableName(String parantTableName) {
        this.parentTableName = parantTableName;
    }

    /**
     * @return the parantPKColumName
     */
    public String getParantPKColumName() {
        return parentPKColumName;
    }

    /**
     * @param parantPKColumName the parantPKColumName to set
     */
    public void setParantPKColumName(String parantPKColumName) {
        this.parentPKColumName = parantPKColumName;
    }

}
