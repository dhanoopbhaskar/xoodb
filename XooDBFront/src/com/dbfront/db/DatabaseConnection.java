/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dbfront.db;

import com.xoodb.connector.XooDBConnector;
import com.xoodb.exception.XooDBSQLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * 
 * @author dhanoopbhaskar
 */
public class DatabaseConnection {

    public static final String MYSQL = "MySQL";
    public static final String ORACLE = "Oracle";
    public static final String MSSQL = "MS SQL";
    public static final String XOODB = "XooDB";
    private String dbServerIp = "";
    private String dbPortNo = "";
    private String dbUserName = "";
    private String dbPassword = "";
    private String dbName = "";
    private String dbType = "";
    private Connection dbConn = null;
    private XooDBConnector xooDBConnector = null;

    public Connection getConnection() throws ClassNotFoundException, SQLException {
        if (dbConn != null) {
            return dbConn;
        } else {
            createConnection();
            return dbConn;
        }
    }

    public XooDBConnector getXooDBConnector() {
        if(xooDBConnector == null) {
            createXooDBConnection();
        }
        return xooDBConnector;
    }

    private void createXooDBConnection() {
        try {
            xooDBConnector = new XooDBConnector(getDbServerIp(),
                    Integer.parseInt(getDbPortNo()), getDbName(),
                    getDbUserName(), getDbPassword());
        } catch (XooDBSQLException ex) {
            JOptionPane.showMessageDialog(new JFrame(), ex.getMessage(),
                    "XooDBSQLException", JOptionPane.ERROR_MESSAGE);
        }
        try {
                xooDBConnector.connectToXooDB();
        } catch (XooDBSQLException ex) {
            JOptionPane.showMessageDialog(new JFrame(), ex.getMessage(),
                    "XooDBSQLException", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createConnection() throws ClassNotFoundException, SQLException {
        if(!getDbType().equals(XOODB)) {
            Class.forName(getDriverName());
        }
        
        if (getDbType().equals(MYSQL)) {
            dbConn = (Connection) DriverManager.getConnection(
                    getDatabaseUrl(), getDbUserName(), getDbPassword());
        } else {
            dbConn = (Connection) DriverManager.getConnection(getDatabaseUrl());
        }
    }

    private String getDriverName() {
        if (getDbType().equals(MYSQL)) {
            return AppConstants.MYSQL_DRIVER;
        } else if (getDbType().equals(ORACLE)) {
            return AppConstants.ORACLE_DRIVER;
        } else if (getDbType().equals(MSSQL)) {
            return AppConstants.MSSQL_DRIVER;
        } else if (getDbType().equals(XOODB)) {
            return "";
        } else {
            return "";
        }
    }

    private String getDatabaseUrl() {
        if (getDbType().equals(MYSQL)) {
            /**
             * My SQL
             * jdbc:mysql://127.0.0.1:3306/databaseName
             */
            return AppConstants.MYSQL_URL + getDbServerIp()
                    + ":" + getDbPortNo() + "/" + getDbName();
        } else if (getDbType().equals(ORACLE)) {
            /**
             * ORACLE
             * jdbc:oracle:thin:userName/password@127.0.0.1:portNo:databaseName
             */
            return AppConstants.ORACLE_URL + getDbUserName()
                    + "/" + getDbPassword() + "@" + getDbServerIp()
                    + ":" + getDbPortNo() + ":" + getDbName();
        } else if (getDbType().equals(MSSQL)) {
            /**
             * MS SQL
             * jdbc:sqlserver://127.0.0.1:portNo; DatabaseName=databaseName;
             * user=userName;password=password
             */
            return AppConstants.MSSQL_URL + getDbServerIp().trim()
                    + ":" + getDbPortNo().trim() + "; DatabaseName="
                    + getDbName().trim() + ";user=" + getDbUserName().trim()
                    + ";password=" + getDbPassword().trim();
        } else if (getDbType().equals(XOODB)) {
            return "";
        } else {
            return "";
        }
    }

    /**
     * @return the dbServerIp
     */
    public String getDbServerIp() {
        return dbServerIp;
    }

    /**
     * @param dbServerIp the dbServerIp to set
     */
    public void setDbServerIp(String dbServerIp) {
        this.dbServerIp = dbServerIp;
    }

    /**
     * @return the dbPortNo
     */
    public String getDbPortNo() {
        return dbPortNo;
    }

    /**
     * @param dbPortNo the dbPortNo to set
     */
    public void setDbPortNo(String dbPortNo) {
        this.dbPortNo = dbPortNo;
    }

    /**
     * @return the dbUsername
     */
    public String getDbUserName() {
        return dbUserName;
    }

    /**
     * @param dbUsername the dbUsername to set
     */
    public void setDbUserName(String dbUserName) {
        this.dbUserName = dbUserName;
    }

    /**
     * @return the dbPassword
     */
    public String getDbPassword() {
        return dbPassword;
    }

    /**
     * @param dbPassword the dbPassword to set
     */
    public void setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
    }

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
     * @return the dbType
     */
    public String getDbType() {
        return dbType;
    }

    /**
     * @param dbType the dbType to set
     */
    public void setDbType(String dbType) {
        this.dbType = dbType;
    }
}
