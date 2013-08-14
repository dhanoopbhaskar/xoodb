/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.xoodb.beans;

/**
 *
 * @author dhanoopbhaskar
 */
public class CreateDatabaseBean extends SQLStatementBean {
    private String databaseName = null;

    /**
     * @param databaseName the databaseName to set
     */
    public void setDatabaseName(String databasename) {
        this.databaseName = databasename;
    }

    /**
     * @return the databaseName
     */
    public String getDatabaseName() {
        return databaseName;
    }
}
