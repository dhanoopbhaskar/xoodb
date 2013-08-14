/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dbfront.db;

/**
 *
 * @author dhanoopbhaskar
 */
public class AppConstants {
    public static final String MYSQL_DRIVER = "com.mysql.jdbc.Driver";
    public static final String MSSQL_DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    public static final String ORACLE_DRIVER = "oracle.jdbc.driver.OracleDriver";

    public static final String MYSQL_URL = "jdbc:mysql://";
    public static final String MSSQL_URL = "jdbc:sqlserver://";
    public static final String ORACLE_URL = "jdbc:oracle:thin:";
    
    public static final String[] ORACLE_DATA_TYPES  = {"Varchar2", "Number"};

    public static boolean isJarFile = true;
}
