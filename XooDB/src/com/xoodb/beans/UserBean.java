/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.xoodb.beans;

import java.io.File;

/**
 *
 * @author dhanoopbhaskar
 */
public class UserBean extends SQLStatementBean {
    private String username = null;
    private String password = null;
    private String schemaFileName = null;
    private boolean remoteAccess = false;

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the schemaFileName
     */
    public String getSchemaFileName() {
        return schemaFileName;
    }

    /**
     * @param schemaFileName the schemaFileName to set
     */
    public void setSchemaFileName(String schemaFileName) {
        this.schemaFileName = schemaFileName;
    }

    /**
     * @return the remoteAccess
     */
    public boolean isRemoteAccess() {
        return remoteAccess;
    }

    /**
     * @param remoteAccess the remoteAccess to set
     */
    public void setRemoteAccess(boolean remoteAccess) {
        this.remoteAccess = remoteAccess;
    }

    /**
     * @return the User Directory
     */
    public String getUserDir(){
        if(getSchemaFileName().lastIndexOf(File.separator) != -1){
            return getSchemaFileName().substring(
                    0, getSchemaFileName().lastIndexOf(File.separator));
        } else {
            return getSchemaFileName();
        }
    }
}
