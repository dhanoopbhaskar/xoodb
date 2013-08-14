/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dbfront.db;

import com.dbfront.gui.MigrateDatabaseJDialog;
import com.xoodb.beans.DatabaseBean;
import com.xoodb.beans.ResultBean;
import com.xoodb.beans.TableBean;
import com.xoodb.beans.TableColumnBean;
import com.xoodb.exception.XooDBSQLException;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;

/**
 * 
 * @author dhanoopbhaskar
 */
public class DatabaseManager {

    private DatabaseConnection connection = null;
    private DatabaseMetaData metaData = null;

    public DatabaseManager(DatabaseConnection connection) throws ClassNotFoundException, SQLException {
        this.connection = connection;
        if(!connection.getDbType().equals(DatabaseConnection.XOODB)) {
            metaData = connection.getConnection().getMetaData();
        }
    }

    public DatabaseInfo getDatabaseInfo() throws ClassNotFoundException, SQLException {
        DatabaseInfo databaseInfo = new DatabaseInfo();
        databaseInfo.setDbName(connection.getDbName());
        databaseInfo.setTables(getTables());
        return databaseInfo;
    }

    private LinkedHashMap<String, TableInfo> getTables()
            throws ClassNotFoundException, SQLException {
        LinkedHashMap<String, TableInfo> tables = new LinkedHashMap<String, TableInfo>();
        //Statement stmnt = connection.getConnection().createStatement();
        //ResultSet rs = stmnt.executeQuery("SHOW TABLES");
        ResultSet rs = null;
        if (connection.getDbType().equals(DatabaseConnection.ORACLE)) {
            rs = metaData.getTables(null, "SCOTT", null, new String[]{"TABLE"});
        } else if(connection.getDbType().equals(DatabaseConnection.XOODB)) {
            ResultBean resultBean = getXooDBTables();
            ArrayList dataBases = resultBean.getData();
            tables = getTables(dataBases);
            return tables;
        } else {
            rs = metaData.getTables(null, null, null, new String[]{"TABLE"});
        }

        while (rs.next()) {
            String tableName = rs.getString("TABLE_NAME");
            if(!tableName.startsWith("BIN")) {
                tables.put(tableName, getTableInfo(tableName));
            }
        }
        return tables;
    }

    private TableInfo getTableInfo(String tableName)
            throws ClassNotFoundException, SQLException {
        TableInfo tableInfo = new TableInfo();
        tableInfo.setTableName(tableName);
        DatabaseMetaData metaData = connection.getConnection().getMetaData();
        ResultSet rs = metaData.getColumns(null, null, tableName, null);
        while (rs.next()) {
            FieldInfo fieldInfo = new FieldInfo();
            fieldInfo.setFieldName(rs.getString("COLUMN_NAME"));
            fieldInfo.setDataType(rs.getString("TYPE_NAME"));
            fieldInfo.setSize(rs.getString("COLUMN_SIZE"));
            fieldInfo.setNullable(
                    rs.getInt("NULLABLE")
                    == DatabaseMetaData.columnNullable ? true : false);
            fieldInfo.setDefaultValue(rs.getString("COLUMN_DEF"));

            if(checkPrimaryKey(tableName, fieldInfo.getFieldName())) {
                fieldInfo.setAsPrimaryKey(true);
            }
            
            tableInfo.getFields().put(fieldInfo.getFieldName(), fieldInfo);
        }
        tableInfo.setPrimaryKeys(getPrimaryKeys(tableName));
        tableInfo.setForeignKeys(getForeignKeys(tableName));
        return tableInfo;
    }

    private LinkedList getPrimaryKeys(String tableName) throws SQLException {
        LinkedList primaryKeys = new LinkedList();
        ResultSet prmKeysRs = metaData.getPrimaryKeys(null, null, tableName);
        while (prmKeysRs.next()) {
            primaryKeys.add(prmKeysRs.getString("COLUMN_NAME"));
        }
        return primaryKeys;
    }

    private LinkedHashMap<String, ForeignKey> getForeignKeys(String tableName) throws SQLException {
        LinkedHashMap foreignKeys = new LinkedHashMap();
        ResultSet expKeysRs = metaData.getImportedKeys(null, null, tableName);
        while (expKeysRs.next()) {
            ForeignKey foriegnKey = new ForeignKey();
            foriegnKey.setForeignKeyName(expKeysRs.getString("FK_NAME"));
            foriegnKey.setFkColumnName(expKeysRs.getString("FKCOLUMN_NAME"));
            foriegnKey.setParantTableName(expKeysRs.getString("PKTABLE_NAME"));
            foriegnKey.setParantPKColumName(expKeysRs.getString("PKCOLUMN_NAME"));
            foreignKeys.put(expKeysRs.getString("FK_NAME"), foriegnKey);
        }
        return foreignKeys;
    }

    public DatabaseConnection getConnection() {
        return connection;
    }

    public void setConnection(DatabaseConnection connection) {
        this.connection = connection;
    }

    public boolean createTable(String query) throws ClassNotFoundException, SQLException, XooDBSQLException {
        int rowUpdated = -1;
        if(connection.getDbType().equals(DatabaseConnection.XOODB)) {
            try {
                connection.getXooDBConnector().executeUpdate(query);
            } catch(XooDBSQLException exp) {
                MigrateDatabaseJDialog.status = false;
            }
        } else {
            rowUpdated = connection.getConnection().createStatement().executeUpdate(query);
        }
        if(rowUpdated >= 0) {
            return true;
        } else {
            return false;
        }
    }

    private ResultBean getXooDBTables() {
        //String query = "SHOW DATABASES";
        //connection.getXooDBConnector().executeQuery(query);
        ResultBean resultBean = null;
        System.out.println("*waiting for result*");
        
        while(true) {
	    System.out.print("");
            if(connection.getXooDBConnector().getIsResultBean()) {
                resultBean = connection.getXooDBConnector().getResultBean();
                break;
            }
        }
        
        System.out.println("*result received*");
        return resultBean;
    }

    private LinkedHashMap<String, TableInfo> getTables(ArrayList databases) {
        LinkedHashMap tableLinkedHashMap = new LinkedHashMap();
        TableInfo tableInfo = null;
        FieldInfo fieldInfo = null;
        for (int i = 0; i < databases.size(); i++) {
            DatabaseBean databaseBean = (DatabaseBean) databases.get(i);
            if(databaseBean.getDatabaseName().equals(connection.getXooDBConnector().databaseName)) {
                ArrayList tables = databaseBean.getTables();
                for (int j = 0; j < tables.size(); j++) {
                    TableBean tableBean = (TableBean) tables.get(j);
                    tableInfo = new TableInfo();
                    tableInfo.setTableName(tableBean.getTableName());
                    for (TableColumnBean columnBean : tableBean.getColumns()) {
                        fieldInfo = new FieldInfo();
                        fieldInfo.setDataType(columnBean.getDataType());
                        fieldInfo.setDefaultValue(columnBean.getDefaultValue());
                        fieldInfo.setFieldName(columnBean.getColumnName());
                        fieldInfo.setSize(columnBean.getSize() + "");
                        fieldInfo.setNullable(columnBean.isNullAllowed());
                        tableInfo.getFields().put(columnBean.getColumnName(), fieldInfo);
                    }
                    tableLinkedHashMap.put(tableBean.getTableName(), tableInfo);
                }
                
            }
        }

        return tableLinkedHashMap;
    }

    
    private boolean checkPrimaryKey(String tableName, String fieldName) throws SQLException {
        LinkedList primaryKeys = new LinkedList();
        boolean status = false;
        ResultSet prmKeysRs = metaData.getPrimaryKeys(null, null, tableName);

        while (prmKeysRs.next()) {
            if(prmKeysRs.getString("COLUMN_NAME").equals(fieldName)) {
                status = true;
                return status;
            }
        }
        return status;
    }
    
}
