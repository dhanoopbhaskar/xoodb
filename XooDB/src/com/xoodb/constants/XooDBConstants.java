/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.xoodb.constants;

import java.io.File;
import java.util.Arrays;
import java.util.Vector;

/**
 *
 * @author dhanoopbhaskar
 */
public class XooDBConstants {
    public static final String DB_CONF_FILE = System.getProperty("user.dir") 
            + File.separator + "DbConfig.properties";
    public static final boolean DEBUG = true;
    public static final boolean LOG = true;
    public static final String FAIL = "FAIL";
    public static final String SUCCESS = "SUCCESS";

    public static final String DATE_PATTERN = "yyyy-MM-dd";
    public static final String DATETIME_PATTERN = "yyyy-MM-dd hh:mm:ss";

    /*****SQL COMMANDS*****/
    public static final String CREATE = "CREATE";
    public static final String TABLE = "TABLE";
    public static final String DATABASE = "DATABASE";
    public static final String DROP = "DROP";
    public static final String USE = "USE";
    public static final String USER = "USER";
    public static final String IDENTIFIED = "IDENTIFIED";
    public static final String BY = "BY";
    
    public static final String INSERT = "INSERT";
    public static final String INTO = "INTO";
    public static final String VALUES = "VALUES";
    
    public static final String UPDATE = "UPDATE";
    public static final String SET = "SET";
    
    public static final String SELECT = "SELECT";
    public static final String FROM = "FROM";
    public static final String WHERE = "WHERE";
    public static final String DELETE = "DELETE";
    
    public static final String SHOW = "SHOW";
    public static final String DATABASES = "DATABASES";
    
    public static final String AND = "AND";
    public static final String OR = "OR";

    /**********CONSTRAINTS************/
    public static final String NOT = "NOT";
    public static final String NULL = "NULL";
    public static final String AUTOINCREMENT = "AUTOINCREMENT";
    public static final String PRIMARY = "PRIMARY";
    public static final String KEY = "KEY";
    public static final String DEFAULT = "DEFAULT";
    
    /**********DATATYPE************/
    public static final String VARCHAR = "VARCHAR";
    public static final String INT = "INT";
    public static final String DATE = "DATE";
    public static final String TIME ="TIME";
    public static final String DATE_TIME = "DATETIME";
    public static final String CHAR = "CHAR";

    public static final Vector<String> DATA_TYPES  = new Vector<String>(
            Arrays.asList(new String[]{VARCHAR, INT, CHAR, DATE, TIME, DATE_TIME,
            "varchar", "int", "char", "date", "time", "datetime"}));

    public static final Vector<String> COMMANDS = new Vector<String>(
            Arrays.asList(new String[]{
                CREATE, TABLE, DATABASE, DROP, USE, USER, IDENTIFIED, BY,
                INSERT, INTO, VALUES, UPDATE, SET, SELECT, FROM, WHERE, DELETE,
                SHOW, DATABASES, AND, OR, NOT, NULL, AUTOINCREMENT, PRIMARY,
                KEY, DEFAULT,
                "create", "table", "database", "drop", "use", "user", "identified",
                "by", "insert", "into", "values", "update", "set", "select",
                "from", "where", "delete", "show", "databases", "and", "or",
                "not", "null", "autoincrement", "primary", "key", "default"
            }));
}
