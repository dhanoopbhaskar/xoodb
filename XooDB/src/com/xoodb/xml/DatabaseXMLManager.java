/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.xoodb.xml;

import com.xoodb.XooDBEngine;
import com.xoodb.XooDBSQLException;
import com.xoodb.beans.CreateDatabaseBean;
import com.xoodb.beans.DatabaseBean;
import com.xoodb.beans.ResultBean;
import com.xoodb.beans.TableBean;
import com.xoodb.beans.TableColumnBean;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author dhanoopbhaskar
 */
public class DatabaseXMLManager {
    XooDBEngine xooDBEngine = null;
    XooDBXMLManager xooDBXMLManager = null;

    public DatabaseXMLManager() {
    }

    public DatabaseXMLManager(XooDBEngine xooDBEngine) throws
            ParserConfigurationException, SAXException, IOException,
            NullPointerException {
        this.xooDBEngine = xooDBEngine;                
        String xmlSchema = getXmlSchemeFileName(
                this.xooDBEngine.getUserBean().getSchemaFileName());        
        xooDBXMLManager = new XooDBXMLManager(
                this.xooDBEngine.getUserBean().getSchemaFileName(), xmlSchema);        
    }

    /**
     * 
     * @param schemaFileName
     * @return
     */
    private String getXmlSchemeFileName(String schemaFileName) {
        if(schemaFileName.lastIndexOf(".") != -1) {
            return schemaFileName.substring(
                    0, schemaFileName.lastIndexOf(".")) + ".xsd";
        }
        return "";
    }

    /**
     *
     * @param createDatabaseBean
     * @return
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     * @throws XooDBSQLException
     */
    public boolean createDatabase(CreateDatabaseBean createDatabaseBean) 
            throws ParserConfigurationException, SAXException, IOException,
            XooDBSQLException {
        DatabaseSchemaXMLManager databaseSchemaXMLManager =
                new DatabaseSchemaXMLManager(xooDBEngine.getUserBean());
        databaseSchemaXMLManager.addNewDatabase(createDatabaseBean);
        return true;
    }

    /**
     * 
     * @param databaseBean
     * @return
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public boolean useDatabase(DatabaseBean databaseBean) 
            throws ParserConfigurationException, SAXException, IOException {
        DatabaseSchemaXMLManager databaseSchemaXMLManager =
                new DatabaseSchemaXMLManager(xooDBEngine.getUserBean());
        Element element = (Element) xooDBXMLManager.getNode(
                "database", "db-name", databaseBean.getDatabaseName());
        if(element != null) {
            xooDBEngine.setDatabaseName(databaseBean.getDatabaseName());
            xooDBEngine.setDatabaseNode(element);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 
     * @param userName
     * @return
     */
    public ResultBean getAllDatabases(String userName) {
        ResultBean resultBean = new ResultBean();
        resultBean.setType("SHOW DATABASES");
        NodeList databases = xooDBXMLManager.getElements("database");
        for (int i = 0; databases != null && i < databases.getLength(); i++) {
            Element database = (Element) databases.item(i);
            DatabaseBean databaseBean = new DatabaseBean();
            Attr dbName = database.getAttributeNode("db-name");
            if (dbName != null) {
                databaseBean.setDatabaseName(dbName.getValue());
            }
            NodeList tables = xooDBXMLManager.getElements(
                    (Element) xooDBXMLManager.getNode(database, "table-info"), "table");
            for (int j = 0; tables != null && j < tables.getLength(); j++) {
                Element table = (Element) tables.item(j);
                Attr tableName = table.getAttributeNode("table-name");
                if (tableName != null) {
                    TableBean tableBean = new TableBean();
                    tableBean.setTableName(tableName.getValue());
                    NodeList columnsNods = xooDBXMLManager.getElements(table, "column");
                    TableColumnBean[] columns = getColumnDetails(columnsNods);
                    tableBean.setColumns(columns);
                    databaseBean.getTables().add(tableBean);
                }
            }
            resultBean.getData().add(databaseBean);
        }
        return resultBean;
    }

    /**
     * 
     * @param columnNodes
     * @return
     */
    private TableColumnBean[] getColumnDetails(NodeList columnNodes) {
        TableColumnBean[] tableColumnBeans = new TableColumnBean[columnNodes.getLength()];
        for (int i = 0; i < columnNodes.getLength(); i++) {
            Element column = (Element) columnNodes.item(i);
            TableColumnBean tableColumnBean = new TableColumnBean();
            tableColumnBean.setColumnName(column.getAttribute("column-name"));
            tableColumnBean.setDataType(column.getAttribute("column-type"));
            tableColumnBean.setSize(Integer.parseInt(column.getAttribute("column-size")));
            tableColumnBean.setNullAllowed(((column.getAttribute("null-alwd") != null
                    && column.getAttribute("null-alwd").equals("true"))
                    ? true : false));
            tableColumnBean.setPrivateKey(((column.getAttribute("primary-key") != null
                    && column.getAttribute("primary-key").equals("true"))
                    ? true : false));
            tableColumnBeans[i] = tableColumnBean;
        }
        return tableColumnBeans;
    }

    /**
     * 
     * @param username
     * @param tableName
     * @return
     */
    public TableBean getTableMetaData(String username, String tableName) {
        NodeList databases = xooDBXMLManager.getElements("database");
        for (int i = 0; databases != null && i < databases.getLength(); i++) {
            Element database = (Element) databases.item(i);
            DatabaseBean databaseBean = new DatabaseBean();
            Attr dbNameAttr = database.getAttributeNode("db-name");
            //System.out.println(dbNameAttr.getValue());
            //System.out.println(xooDBEngine.getDatabaseName());
            if (dbNameAttr != null && dbNameAttr.getValue().equals(
                    xooDBEngine.getDatabaseName())) {
                databaseBean.setDatabaseName(dbNameAttr.getValue());
                NodeList tables = xooDBXMLManager.getElements(
                        (Element) xooDBXMLManager.getNode(database, "table-info"), "table");
                for (int j = 0; tables != null && j < tables.getLength(); j++) {
                    Element table = (Element) tables.item(j);
                    Attr tableNameAttr = table.getAttributeNode("table-name");
                    if (tableName != null && tableNameAttr.getValue().equals(tableName)) {
                        TableBean tableBean = new TableBean();
                        tableBean.setTableName(tableNameAttr.getValue());
                        NodeList columnsNods = xooDBXMLManager.getElements(table, "column");
                        TableColumnBean[] columns = getColumnDetails(columnsNods);
                        tableBean.setColumns(columns);
                        return tableBean;
                    }
                }
            }
        }
        return null;
    }

    /**
     * 
     * @param databaseName
     * @throws XooDBSQLException
     */
    public void removeDatabase(String databaseName) throws XooDBSQLException {
        Node dbInfoNode = xooDBXMLManager.getNode("database-info");
        Node database = xooDBXMLManager.getNode((Element) dbInfoNode, "database", "db-name", databaseName);
        if (database != null) {
            dbInfoNode.removeChild(database);
            xooDBXMLManager.commitXML();
        } else {
            XooDBSQLException xooDBSQLException = new XooDBSQLException();
            xooDBSQLException.setMessage(("Database: '" + databaseName + "' not exists."));
            throw xooDBSQLException;
        }
    }    
}
