/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dbfront.db;

import com.dbfront.gui.MigrateDatabaseJDialog;
import com.dbfront.xml.XooDBXMLManager;
import com.xoodb.exception.XooDBSQLException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.jar.JarFile;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * 
 * @author dhanoopbhaskar
 */
public class MigrateDatabase {
    private JarFile jarFile = null;
    private String pathOfMappingFile = null;
    private DatabaseManager srsDbMgr = null;
    private DatabaseManager destDbMgr = null;
    private ArrayList createdTables = new ArrayList();
    private XooDBXMLManager xooDBXMLManager = null;
    private int primaryKeyCount = 0;

    public MigrateDatabase(DatabaseManager srsDbMgr, DatabaseManager destDbMgr)
            throws ParserConfigurationException, SAXException, IOException {
        this.srsDbMgr = srsDbMgr;
        this.destDbMgr = destDbMgr;
        /**
         * XooDBXMLManager is used for handling the datatype mapping info
         * which is saved as an XML file
         */       

        if(AppConstants.isJarFile) {
            findPathFromJar();
        } else {
            findPathFromDir();
        }        
        
        xooDBXMLManager = new XooDBXMLManager(pathOfMappingFile);
    }

    /**
     * for the case when the program is run from a jar file
     */
    private void findPathFromJar() {
        try {
            jarFile = new JarFile(new File("XooDBFront.jar"));

            InputStream inputStream = jarFile.getInputStream(
                    jarFile.getEntry("com/dbfront/rsrs/datatype_mapping.xml"));

            OutputStream outputStream = new FileOutputStream("datatype_mapping.xml");
            
            int c;
            while ((c = inputStream.read()) != -1) {
                outputStream.write(c);
            }
            inputStream.close();
            outputStream.close();
            jarFile.close();
            pathOfMappingFile = "datatype_mapping.xml";
        } catch (IOException ex) {
            findPathFromDir();
        }
    }

    /**
     * for the case when program is run in NetBeans or in command prompt
     * without jar file
     */
    private void findPathFromDir() {
        pathOfMappingFile = this.getClass().getResource(
                "/com/dbfront/rsrs/datatype_mapping.xml").getPath();

        if(pathOfMappingFile.indexOf("!") != -1) {
            pathOfMappingFile = pathOfMappingFile.replace("!", "");
        }
    }

    /**
     * 
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws XooDBSQLException
     */
    public boolean migrate() throws ClassNotFoundException, SQLException, XooDBSQLException {
        DatabaseInfo databaseInfo = srsDbMgr.getDatabaseInfo();
        createdTables.clear();
        primaryKeyCount = 0;
        /**
         * retrieves the key values in the LinkedHashMap where database info
         * is saved
         * LinkedHashMap<String, TableInfo>
         * and
         * iterates
         */
        Iterator iterator = databaseInfo.getTables().keySet().iterator();
        while (iterator.hasNext()) {
            String tableName = (String) iterator.next();
            /**
             * if the table is not already created - not in created table list
             * and tableName does not start with BIN
             * -call the method createTable to create table with name tableName
             * (string value from LinkedHashMap)
             * 
             * databaseInfo.getTables().get(tableName) -> gets the TableInfo
             * corresponding to tableName from the LinkedHashMap
             */
            if (!createdTables.contains(tableName) && !tableName.startsWith("BIN")) {
                try {
                    createTable(databaseInfo.getTables().get(tableName), databaseInfo.getTables());
                } catch(XooDBSQLException exp) {
                    MigrateDatabaseJDialog.status = false;
                }
            }
        }
        return false;
    }

    /**
     * 
     * @param tableInfo
     * @param tables
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    private void createTable(TableInfo tableInfo, LinkedHashMap tables) throws ClassNotFoundException, SQLException, XooDBSQLException {
        if (tableInfo != null) {
            /**
             * to ensure that the primary tables are created before (foreign)
             * derived tables
             */
            Iterator foreignKeyNames = tableInfo.getForeignKeys().keySet().iterator();
            while (foreignKeyNames.hasNext()) {
                ForeignKey foreignKey = tableInfo.getForeignKeys().get(foreignKeyNames.next());
                if (!createdTables.contains(foreignKey.getParantTableName())) {
                    createTable((TableInfo) tables.get(foreignKey.getParantTableName()), tables);
                } else {
                    continue;
                }
            }
            
            String query = "";
            if (destDbMgr.getConnection().getDbType().equals(DatabaseConnection.ORACLE)) {
                query = createOracleQuery(tableInfo);
            } else if (destDbMgr.getConnection().getDbType().equals(DatabaseConnection.MYSQL)) {
                query = createMySQLQuery(tableInfo);
            } else if (destDbMgr.getConnection().getDbType().equals(DatabaseConnection.MSSQL)) {
                query = createMSSQLQuery(tableInfo);
            } else if (destDbMgr.getConnection().getDbType().equals(DatabaseConnection.XOODB)) {
                query = createXooDBSQLQuery(tableInfo);
            }
            System.out.println(query);
            if (destDbMgr.createTable(query)) {
                createdTables.add(tableInfo.getTableName());
            }
        }
    }

    /**
     * method for creating MS SQL Query
     *
     * @param tableInfo
     * @return
     */
    private String createMSSQLQuery(TableInfo tableInfo) {
        StringBuilder queryBuilder = new StringBuilder("CREATE TABLE ");
        queryBuilder.append((tableInfo.getTableName() + "("));
        Iterator fieldNames = tableInfo.getFields().keySet().iterator();
        while (fieldNames.hasNext()) {
            FieldInfo fieldInfo = tableInfo.getFields().get(fieldNames.next());
            String fieldName = fieldInfo.getFieldName();
            /**
             * dataType
             * sourceDatabase
             * destinationDatabase
             */
            String dataType = getDataType(fieldInfo.getDataType(),
                    srsDbMgr.getConnection().getDbType(), DatabaseConnection.MSSQL);
            
            if (dataType == null) {
                //JOptionPane.
            }

            queryBuilder.append((fieldName + " " + dataType));
            
            if (!fieldInfo.getSize().equals("") && isSizeAllowable(dataType, DatabaseConnection.MSSQL)) {
                queryBuilder.append(("(" + fieldInfo.getSize() + ")"));
            }

            if (!fieldInfo.isNullable()) {
                queryBuilder.append(" NOT NULL");
            }

            if (fieldInfo.getDefaultValue() != null
                    && !fieldInfo.getDefaultValue().equals("")) {
                queryBuilder.append((" DEFAULT '"
                        + fieldInfo.getDefaultValue().replaceAll("'", "").trim() + "'"));
            }
            
            queryBuilder.append(", ");
        }

        queryBuilder.deleteCharAt(queryBuilder.lastIndexOf(","));

        if (tableInfo.getPrimaryKeys().size() > 0) {
            queryBuilder.append(", CONSTRAINT primary_key" + primaryKeyCount + " PRIMARY KEY (");

            for (int i = 0; i < tableInfo.getPrimaryKeys().size(); i++) {
                queryBuilder.append(tableInfo.getPrimaryKeys().get(i) + ",");
            }
            
            queryBuilder.deleteCharAt(queryBuilder.length() - 1);
            queryBuilder.append(")");
            primaryKeyCount++;
        }

        if (tableInfo.getForeignKeys().size() > 0) {
            Iterator foreignKeyNames = tableInfo.getForeignKeys().keySet().iterator();
            while (foreignKeyNames.hasNext()) {
                ForeignKey foriegnKey = tableInfo.getForeignKeys().get(foreignKeyNames.next());
                queryBuilder.append((", CONSTRAINT " + foriegnKey.getForeignKeyName()
                        + " FOREIGN KEY (" + foriegnKey.getFkColumnName()
                        + ") REFERENCES " + foriegnKey.getParantTableName()
                        + "(" + foriegnKey.getParantPKColumName() + ")"));
            }
        }

        if (queryBuilder.toString().trim().endsWith(",")) {
            queryBuilder.deleteCharAt(queryBuilder.lastIndexOf(","));
            queryBuilder.append(")");
        } else {            
            queryBuilder.append(")");
        }
        
        return queryBuilder.toString();
    }

    /**
     * method for creating My SQL Query
     *
     * @param tableInfo
     * @return
     */
    private String createMySQLQuery(TableInfo tableInfo) {
        StringBuilder queryBuilder = new StringBuilder("CREATE TABLE ");
        queryBuilder.append((tableInfo.getTableName() + "("));
        Iterator fieldNames = tableInfo.getFields().keySet().iterator();

        while (fieldNames.hasNext()) {
            FieldInfo fieldInfo = tableInfo.getFields().get(fieldNames.next());
            String fieldName = fieldInfo.getFieldName();
            String dataType = getDataType(fieldInfo.getDataType(),
                    srsDbMgr.getConnection().getDbType(), DatabaseConnection.MYSQL);

            if (dataType == null) {
                //JOptionPane.
            }

            queryBuilder.append((fieldName + " " + dataType));

            if (!fieldInfo.getSize().equals("") && isSizeAllowable(dataType, DatabaseConnection.MYSQL)) {
                queryBuilder.append(("(" + fieldInfo.getSize() + ")"));
            }

            if (!fieldInfo.isNullable()) {
                queryBuilder.append(" NOT NULL");
            }

            if (fieldInfo.getDefaultValue() != null
                    && !fieldInfo.getDefaultValue().equals("")) {
                queryBuilder.append((" DEFAULT '"
                        + fieldInfo.getDefaultValue().replaceAll("'", "").trim() + "'"));
            }
            
            queryBuilder.append(", ");
        }

        if (tableInfo.getPrimaryKeys().size() > 0) {
            queryBuilder.append(" PRIMARY KEY (");
            for (int i = 0; i < tableInfo.getPrimaryKeys().size(); i++) {
                queryBuilder.append(tableInfo.getPrimaryKeys().get(i) + ",");
            }
            queryBuilder.deleteCharAt(queryBuilder.length() - 1);
            queryBuilder.append(")");
        }
        
        if (tableInfo.getForeignKeys().size() > 0) {
            Iterator foreignKeyNames = tableInfo.getForeignKeys().keySet().iterator();
            while (foreignKeyNames.hasNext()) {
                ForeignKey foriegnKey = tableInfo.getForeignKeys().get(foreignKeyNames.next());
                queryBuilder.append((", CONSTRAINT " + foriegnKey.getForeignKeyName()
                        + " FOREIGN KEY (" + foriegnKey.getFkColumnName()
                        + ") REFERENCES " + foriegnKey.getParantTableName()
                        + "(" + foriegnKey.getParantPKColumName() + ")"));
            }
        }

        if (queryBuilder.toString().trim().endsWith(",")) {
            queryBuilder.deleteCharAt(queryBuilder.lastIndexOf(","));
            queryBuilder.append(")");
        } else {
            //queryBuilder.deleteCharAt(queryBuilder.lastIndexOf(","));
            queryBuilder.append(")");
        }
        
        return queryBuilder.toString();
    }

    /**
     * method for creating Oracle Query
     *
     * @param tableInfo
     * @return
     */
    private String createOracleQuery(TableInfo tableInfo) {
        StringBuilder queryBuilder = new StringBuilder("CREATE TABLE ");
        queryBuilder.append((tableInfo.getTableName() + "("));
        Iterator fieldNames = tableInfo.getFields().keySet().iterator();

        while (fieldNames.hasNext()) {
            FieldInfo fieldInfo = tableInfo.getFields().get(fieldNames.next());
            String fieldName = fieldInfo.getFieldName();
            String dataType = getDataType(fieldInfo.getDataType(),
                    srsDbMgr.getConnection().getDbType(), DatabaseConnection.ORACLE);
            
            if (dataType == null) {
                //JOptionPane.
            }

            queryBuilder.append((fieldName + " " + dataType));

            if (!fieldInfo.getSize().equals("") && isSizeAllowable(dataType, DatabaseConnection.ORACLE)) {
                queryBuilder.append(("(" + fieldInfo.getSize() + ")"));
            }

            if (fieldInfo.getDefaultValue() != null
                    && !fieldInfo.getDefaultValue().equals("")) {
                queryBuilder.append(" DEFAULT '" + fieldInfo.getDefaultValue() + "'");
//                if(isSinglrQuotesNeeded(dataType, DatabaseConnection.ORACLE))  {
//                    queryBuilder.append("'" + fieldInfo.getDefaultValue() + "'");
//                }else{
//                    queryBuilder.append(fieldInfo.getDefaultValue());
//                }
            }

            if (!fieldInfo.isNullable()) {
                queryBuilder.append(" NOT NULL");
            }

            Iterator foreignKeyNames = tableInfo.getForeignKeys().keySet().iterator();

            while (foreignKeyNames.hasNext()) {
                ForeignKey foriegnKey = tableInfo.getForeignKeys().get(
                        foreignKeyNames.next());

                if (foriegnKey.getFkColumnName().equals(fieldInfo.getFieldName())) {
                    queryBuilder.append((" references "
                            + foriegnKey.getParantTableName() + "("
                            + foriegnKey.getParantPKColumName() + ")"));
                }
                
            }
            
            queryBuilder.append(", ");
        }

        if (tableInfo.getPrimaryKeys().size() > 0) {
            queryBuilder.append("CONSTRAINT primary_key" + primaryKeyCount + " PRIMARY KEY (");
            for (int i = 0; i < tableInfo.getPrimaryKeys().size(); i++) {
                queryBuilder.append((tableInfo.getPrimaryKeys().get(i) + ","));
            }
            queryBuilder.deleteCharAt(queryBuilder.length() - 1);
            queryBuilder.append(")");
            primaryKeyCount++;
        } else {
            queryBuilder.delete(queryBuilder.length() - 2, queryBuilder.length());
        }

        queryBuilder.append(")");
        
        return queryBuilder.toString();
    }

    /**
     *
     * @param dataType
     * @param srcDbType
     * @param destDbType
     * @return
     */
    private String getDataType(String dataType, String srcDbType, String destDbType) {
        String outputDataType = "";
        /**
         * gets the node corresponding to the source database type
         * -oracle
         * -mysql
         * -mssql
         */
        //System.out.println(srcDbType.toLowerCase().replace(" ", ""));
        Node databaseNode = xooDBXMLManager.getNode(srcDbType.toLowerCase().replace(" ", ""));
        /**
         * get the node <mapping> within the databaseNode
         */
        Node mapping = xooDBXMLManager.getNode((Element) databaseNode, "mapping");
        /**
         * gets the neutral datatype under <mapping> and enclosed within
         * <dataType>
         */
        String neutralDatatype = xooDBXMLManager.getElementValue((Element) mapping, dataType.toLowerCase());
        /**
         * gets the node corresponding to the destination database type
         * -oracle
         * -mysql
         * -mssql
         */
        Node destDbNode = xooDBXMLManager.getNode(destDbType.toLowerCase().replace(" ", ""));
        /**
         * get the node <neutral> within the databaseNode
         */
        Node neutralDatatypeNode = xooDBXMLManager.getNode((Element) destDbNode, "neutral");
        /**
         * gets the destination datatype under <neutral> and enclosed within
         * <dataType>
         */
        outputDataType = xooDBXMLManager.getElementValue((Element) neutralDatatypeNode, neutralDatatype);
        return outputDataType;
    }

    /**
     * 
     * @param dataType
     * @param dbType
     * @return
     */
    private boolean isSizeAllowable(String dataType, String dbType) {
        if (dbType.equals(DatabaseConnection.ORACLE)) {
            if (dataType.equals("int")) {
                return false;
            } else {
                return true;
            }
        } else if (dbType.equals(DatabaseConnection.MYSQL)) {
            if (dataType.equals("date")) {
                return false;
            } else if (dataType.equals("datetime")) {
                return false;
            } else {
                return true;
            }
        } else if (dbType.equals(DatabaseConnection.MSSQL)) {
            if (dataType.equals("date")) {
                return false;
            } else if (dataType.equals("datetime")) {
                return false;
            } else if (dataType.equals("int")) {
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    /**
     * 
     * @param dataType
     * @param dbType
     * @return
     */
    private boolean isSingleQuotesNeeded(String dataType, String dbType) {
        if (dbType.equals(DatabaseConnection.ORACLE)) {
            if (dataType.equals("int")) {
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    private String createXooDBSQLQuery(TableInfo tableInfo) {
        StringBuilder queryBuilder = new StringBuilder("CREATE TABLE ");
        queryBuilder.append((tableInfo.getTableName() + "("));
        Iterator fieldNames = tableInfo.getFields().keySet().iterator();

        while (fieldNames.hasNext()) {
            FieldInfo fieldInfo = tableInfo.getFields().get(fieldNames.next());
            String fieldName = fieldInfo.getFieldName();
            String dataType = getDataType(fieldInfo.getDataType(),
                    srsDbMgr.getConnection().getDbType(), DatabaseConnection.XOODB);

            if (dataType == null) {
                //JOptionPane.
            }

            queryBuilder.append((fieldName + " " + dataType));

            if (!fieldInfo.getSize().equals("") && isSizeAllowable(dataType, DatabaseConnection.XOODB)) {
                queryBuilder.append(("(" + fieldInfo.getSize() + ")"));
            }

            if(fieldInfo.isPrimaryKey()) {
                queryBuilder.append(" PRIMARY KEY");
            }

            if (!fieldInfo.isNullable()) {
                queryBuilder.append(" NOT NULL");
            }

            if (fieldInfo.getDefaultValue() != null
                    && !fieldInfo.getDefaultValue().equals("")) {
                queryBuilder.append((" DEFAULT '"
                        + fieldInfo.getDefaultValue().replaceAll("'", "").trim() + "'"));
            }

            queryBuilder.append(", ");
        }
             

        if (queryBuilder.toString().trim().endsWith(",")) {
            queryBuilder.deleteCharAt(queryBuilder.lastIndexOf(","));
            queryBuilder.append(")");
        } else {
            //queryBuilder.deleteCharAt(queryBuilder.lastIndexOf(","));
            queryBuilder.append(")");
        }

        return queryBuilder.toString();
    }
}
