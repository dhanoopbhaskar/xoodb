/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.xoodb.xml;

import com.xoodb.XooDBSQLException;
import com.xoodb.beans.CreateDatabaseBean;
import com.xoodb.beans.TableBean;
import com.xoodb.beans.TableColumnBean;
import com.xoodb.beans.UserBean;
import com.xoodb.constants.XooDBVariables;
import com.xoodb.main.XooDBServerMain;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 *
 * @author dhanoopbhaskar
 */
public class DatabaseSchemaXMLManager {
    XooDBXMLManager xooDBXMLManager = null;

    public DatabaseSchemaXMLManager() {
        xooDBXMLManager = new XooDBXMLManager();
    }

    public DatabaseSchemaXMLManager(UserBean userBean) throws
            ParserConfigurationException, SAXException, IOException {
        String xmlSchema = getXmlSchemeFileName(userBean.getSchemaFileName());
        xooDBXMLManager = new XooDBXMLManager(
                userBean.getSchemaFileName(), xmlSchema);
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
     * @param userBean
     * @param name
     * @throws XMLStreamException
     * @throws FileNotFoundException
     */
    public void createDatabaseSchema(UserBean userBean, String name) throws
            XMLStreamException, XooDBSQLException {
        File schemaFile = new File(userBean.getSchemaFileName());
        try {
            xooDBXMLManager.createXMLWriter(schemaFile);
        } catch (FileNotFoundException ex) {
            XooDBServerMain.appendExceptionLog(ex);
            XooDBSQLException xooDBSQLException = new XooDBSQLException();
            xooDBSQLException.setMessage("SQL Error: Database does not exist");
            throw xooDBSQLException;
        }
        /**
         * <?xml version="1.0" encoding="utf-8">
         */
        xooDBXMLManager.createStartDocument("utf-8","1.0");
        /**
         * <user-schema xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
         */
        xooDBXMLManager.createStartElement("user-schema");
        xooDBXMLManager.createAttribute("xmlns:xsi",
                "http://www.w3.org/2001/XMLSchema-instance");
        /**
         * <user-info>
	 *      <user-name>userName</user-name>
         * </user-info>
         */
        xooDBXMLManager.createStartElement("user-info");                        // <user-info>
        xooDBXMLManager.createStartElement("user-name");                        // <user-name>
        xooDBXMLManager.writeElementTextValue(userBean.getUsername());          // gets userName from the bean
        xooDBXMLManager.createCloseElement();                                   // </user-name>
        xooDBXMLManager.createCloseElement();                                   // </user-info>        
        xooDBXMLManager.createStartElement("database-info");                    // <database-info>
        xooDBXMLManager.createCloseElement();                                   // </database-info>
        xooDBXMLManager.createCloseElement();                                   // </user-schema>
        xooDBXMLManager.closeWriter();
    }

    /**
     * 
     * @param userBean
     * @return
     */
    public File createXmlSchemaForDbSchema(UserBean userBean) {
        File userDirectory = new File(XooDBVariables.dbDir
                + File.separator + userBean.getUsername()
                + File.separator + userBean.getUsername() + ".xsd");
        try {
            xooDBXMLManager.createXMLWriter(userDirectory);
            /**
             * <?xml version="1.0" encoding="utf-8"?>
             */
            xooDBXMLManager.createStartDocument("utf-8", "1.0");            
            /**
             * <xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema-instance">
             */
            xooDBXMLManager.createStartElement("xs:schema");
            xooDBXMLManager.createAttribute("xmlns:xs",
                "http://www.w3.org/2001/XMLSchema");
            /**
             * <xs:element name="user-schema">
             */
            xooDBXMLManager.createStartElement("xs:element");
            xooDBXMLManager.createAttribute("name", "user-schema");
            /**
             * <xs:complexType>
             * <xs:sequence>
             */
            xooDBXMLManager.createStartElement("xs:complexType");
            xooDBXMLManager.createStartElement("xs:sequence");
            /**
             * <xs:element name="user-info">
             */
            xooDBXMLManager.createStartElement("xs:element");
            xooDBXMLManager.createAttribute("name", "user-info");
            /**
             * <xs:complexType>
             * <xs:sequence>
             */
            xooDBXMLManager.createStartElement("xs:complexType");
            xooDBXMLManager.createStartElement("xs:sequence");
            /**
             * <xs:element name="user-name" type="xs:string"/>
             * note : empty element
             */
            xooDBXMLManager.createEmptyElement("xs:element");
            xooDBXMLManager.createAttribute("name", "user-name");
            xooDBXMLManager.createAttribute("type", "xs:string");
            
            xooDBXMLManager.createCloseElement();                               // </xs:sequence>
            xooDBXMLManager.createCloseElement();                               // </xs:complexType>
            xooDBXMLManager.createCloseElement();                               // </xs:element>
            /**
             * <xs:element name="database-info" maxOccurs="unbounded" minOccurs="0">
             */
            xooDBXMLManager.createStartElement("xs:element");
            xooDBXMLManager.createAttribute("name", "database-info");
            xooDBXMLManager.createAttribute("maxOccurs", "unbounded");
            xooDBXMLManager.createAttribute("minOccurs", "0");
            /**
             * <xs:complexType>
             * <xs:sequence>
             */
            xooDBXMLManager.createStartElement("xs:complexType");
            xooDBXMLManager.createStartElement("xs:sequence");
            /**
             * <xs:element name="database" maxOccurs="unbounded" minOccurs="0">
             */
            xooDBXMLManager.createStartElement("xs:element");
            xooDBXMLManager.createAttribute("name", "database");
            xooDBXMLManager.createAttribute("maxOccurs", "unbounded");
            xooDBXMLManager.createAttribute("minOccurs", "0");
            /**
             * <xs:complexType>
             * <xs:sequence>
             */
            xooDBXMLManager.createStartElement("xs:complexType");
            xooDBXMLManager.createStartElement("xs:sequence");
            /**
             * <xs:element name="table-info">
             */
            xooDBXMLManager.createStartElement("xs:element");
            xooDBXMLManager.createAttribute("name", "table-info");
            /**
             * <xs:complexType>
             * <xs:sequence>
             */
            xooDBXMLManager.createStartElement("xs:complexType");
            xooDBXMLManager.createStartElement("xs:sequence");
            /**
             * <xs:element name="table" maxOccurs="unbounded" minOccurs="0">
             */
            xooDBXMLManager.createStartElement("xs:element");
            xooDBXMLManager.createAttribute("name", "table");
            xooDBXMLManager.createAttribute("maxOccurs", "unbounded");
            xooDBXMLManager.createAttribute("minOccurs", "0");
            /**
             * <xs:complexType>
             * <xs:sequence>
             */
            xooDBXMLManager.createStartElement("xs:complexType");
            xooDBXMLManager.createStartElement("xs:sequence");
            /**
             * <xs:element name="column" maxOccurs="unbounded" minOccurs="0">
             */
            xooDBXMLManager.createStartElement("xs:element");
            xooDBXMLManager.createAttribute("name", "column");
            xooDBXMLManager.createAttribute("maxOccurs", "unbounded");
            xooDBXMLManager.createAttribute("minOccurs", "0");
            /**
             * <xs:complexType>
             */
            xooDBXMLManager.createStartElement("xs:complexType");
            /**
             * <xs:attribute name="column-name" type="xs:string" use="required"/>
             */
            xooDBXMLManager.createEmptyElement("xs:attribute");
            xooDBXMLManager.createAttribute("name", "column-name");
            xooDBXMLManager.createAttribute("type", "xs:string");
            xooDBXMLManager.createAttribute("use", "required");
            /**
             * <xs:attribute name="column-type" type="xs:string" use="required"/>
             */
            xooDBXMLManager.createEmptyElement("xs:attribute");
            xooDBXMLManager.createAttribute("name", "column-type");
            xooDBXMLManager.createAttribute("type", "xs:string");
            xooDBXMLManager.createAttribute("use", "required");
             /**
              * <xs:attribute name="column-size" type="xs:string" use="required"/>
              */
            xooDBXMLManager.createEmptyElement("xs:attribute");
            xooDBXMLManager.createAttribute("name", "column-size");
            xooDBXMLManager.createAttribute("type", "xs:string");
            xooDBXMLManager.createAttribute("use", "required");
            /**
             * <xs:attribute name="null-alwd" type="xs:boolean" use="required"/>
             */
            xooDBXMLManager.createEmptyElement("xs:attribute");
            xooDBXMLManager.createAttribute("name", "null-alwd");
            xooDBXMLManager.createAttribute("type", "xs:boolean");
            xooDBXMLManager.createAttribute("use", "required");
            /**
             * <xs:attribute name="primary-key" type="xs:boolean"/>
             */
            xooDBXMLManager.createEmptyElement("xs:attribute");
            xooDBXMLManager.createAttribute("name", "primary-key");
            xooDBXMLManager.createAttribute("type", "xs:boolean");
            /**
             * closing opened tags
             */
            xooDBXMLManager.createCloseElement(); //</xs:complexType>
            xooDBXMLManager.createCloseElement(); //</xs:element>
            xooDBXMLManager.createCloseElement(); //</xs:sequence>
            /**
             * <xs:attribute name="table-name" type="xs:string" use="required"/>
             */
            xooDBXMLManager.createEmptyElement("xs:attribute");
            xooDBXMLManager.createAttribute("name", "table-name");
            xooDBXMLManager.createAttribute("type", "xs:string");
            xooDBXMLManager.createAttribute("use", "required");
            /**
             * closing opened tags
             */
            xooDBXMLManager.createCloseElement(); //</xs:complexType>
            xooDBXMLManager.createCloseElement(); //</xs:element>
            xooDBXMLManager.createCloseElement(); //</xs:sequence>
            xooDBXMLManager.createCloseElement(); //</xs:complexType>
            xooDBXMLManager.createCloseElement(); //</xs:element>
            xooDBXMLManager.createCloseElement(); //</xs:sequence>
            /**
             * <xs:attribute name="db-name" type="xs:string"/>
             */
            xooDBXMLManager.createEmptyElement("xs:attribute");
            xooDBXMLManager.createAttribute("name", "db-name");
            xooDBXMLManager.createAttribute("type", "xs:string");
            /**
             * <xs:attribute name="owner" type="xs:string/>
             */
            xooDBXMLManager.createEmptyElement("xs:attribute");
            xooDBXMLManager.createAttribute("name", "owner");
            xooDBXMLManager.createAttribute("type", "xs:string");
            /**
             * closing opened tags
             */
            xooDBXMLManager.createCloseElement(); //</xs:complexType>
            xooDBXMLManager.createCloseElement(); //</xs:element>
            xooDBXMLManager.createCloseElement(); //</xs:sequence>
            xooDBXMLManager.createCloseElement(); //</xs:complexType>
            xooDBXMLManager.createCloseElement(); //</xs:element>
            xooDBXMLManager.createCloseElement(); //</xs:sequence>
            xooDBXMLManager.createCloseElement(); //</xs:complexType>
            xooDBXMLManager.createCloseElement(); //</xs:element>
            xooDBXMLManager.createCloseElement(); //</xs:schema>
            xooDBXMLManager.closeWriter();
            return userDirectory;
        } catch (XMLStreamException ex) {
            Logger.getLogger(DatabaseSchemaXMLManager.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ex);
            XooDBServerMain.appendExceptionLog(ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DatabaseSchemaXMLManager.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ex);
            XooDBServerMain.appendExceptionLog(ex);
        }

        return null;
    }

    /**
     * 
     * @param createDatabaseBean
     * @throws XooDBSQLException
     */
    public void addNewDatabase(CreateDatabaseBean createDatabaseBean)
            throws XooDBSQLException {
        /**
         * -checks whether the database does not exist
         * -checks for
         * <database db-name="databaseName">
         * in XML meta file of the user
         * userName.xml file
         *
         * if the node is null - does not exist
         */
        //Element dbInfoNode = (Element) xooDBXMLManager.createNode("database-info");
        if (xooDBXMLManager.getNode(
                "database", "db-name",
                (createDatabaseBean.getDatabaseName()).trim()) == null) {
            /**
             * adds
             * <database db-name="databaseName" owner="">
             * </database>
             */
            Node dbNodeInfo = xooDBXMLManager.getNode("database-info");

            Node dbNode = xooDBXMLManager.createNode("database");
            xooDBXMLManager.addAttribute(dbNode, "db-name",
                    createDatabaseBean.getDatabaseName());
            xooDBXMLManager.addAttribute(dbNode, "owner", "");
            xooDBXMLManager.addChildNode(dbNodeInfo, dbNode);
            /**
             * adds
             * <table-info>
             * </table-info>
             * to
             * <database>
             */
            Node tableInfo = xooDBXMLManager.createNode("table-info");
            xooDBXMLManager.addChildNode(dbNode, tableInfo);
            /**
             * adds
             * <database>
             * to
             * <database-info>
             */
           // Node dbNodeInfo = xooDBXMLManager.getNode("database-info");
           // xooDBXMLManager.addChildNode(dbNodeInfo, dbNode);
            /**
             *  <database-info>
             *      <database db-name="databaseName" owner="">
             *          <table-info>
             *          </table-info>
             *      </database>
             *  </database-info>
             */
            xooDBXMLManager.commitXML();
        } else {
            XooDBSQLException xooDBSQLException = new XooDBSQLException();
            xooDBSQLException.setMessage(createDatabaseBean.getDatabaseName()
                    + ": Database already exists");
            throw xooDBSQLException;
        }
    }

    /**
     *
     * Adding table information to the tag <table-info>
     *
     * @param createTableBean
     * @param databaseName
     * @throws XooDBSQLException
     */
    public void addTableInfoIntoSchema(TableBean createTableBean, 
            String databaseName) throws XooDBSQLException {
        Element element = (Element) xooDBXMLManager.getNode("database", "db-name", databaseName);
        if (element != null) {
            Node tableInfoNode = xooDBXMLManager.getNode(element, "table-info");
            System.out.println("Node Name: " + tableInfoNode.getNodeName());
            if (tableInfoNode != null && tableInfoNode.getNodeName().equals("table-info")) {
                /**
                 * checks whether the table already exists
                 * proceeds if does not exist
                 */
                if (xooDBXMLManager.getNode((Element) tableInfoNode, "table",
                        "table-name", createTableBean.getTableName()) == null) {
                    /**
                     *  <table table-name="tableName">
                     *  </table>
                     */
                    Node tableNode = xooDBXMLManager.createNode("table");
                    xooDBXMLManager.addAttribute(tableNode, "table-name", createTableBean.getTableName());
                    //Iterator columns = creatTableBean.getColumns().values().iterator();
                    TableColumnBean[] columns = createTableBean.getColumns();
                    for (int i = 0; i < columns.length; i++) {
                        TableColumnBean columnBean = columns[i];
                        /**
                         * adding column information within <table> tag
                         *
                         *  <table table-name="tableName">
                         *      <column column-name="columnName"
                         *              column-type="dataType"
                         *              column-size="size"
                         *              null-alwd="true/false"
                         *              primary-key="true"/>
                         *  </table>
                         */
                        Node columnInfo = xooDBXMLManager.createNode("column");
                        xooDBXMLManager.addAttribute(columnInfo,
                                "column-name", columnBean.getColumnName());
                        xooDBXMLManager.addAttribute(columnInfo,
                                "column-type", columnBean.getDataType());
                        xooDBXMLManager.addAttribute(columnInfo,
                                "column-size", "" + columnBean.getSize());
                        xooDBXMLManager.addAttribute(columnInfo,
                                "null-alwd", "" + columnBean.isNullAllowed());
                        if (columnBean.isPrivateKey()) {
                            xooDBXMLManager.addAttribute(
                                    columnInfo, "primary-key", "true");
                        }
                        xooDBXMLManager.addChildNode(tableNode, columnInfo);
                    }
                    /**
                     *  <database-info>
                     *      <database>
                     *          <table-info>
                     *              <table>
                     *                  <column/>
                     *              </table>
                     *          </table-info>
                     *      </database>
                     *  </database-info>
                     */
                    xooDBXMLManager.addChildNode(tableInfoNode, tableNode);
                    xooDBXMLManager.commitXML();
                } else {
                    XooDBSQLException xooDBSQLException = new XooDBSQLException();
                    xooDBSQLException.setMessage("Table: '"
                            + createTableBean.getTableName()
                            + "' already exists");
                    throw xooDBSQLException;
                }
            }
        }
    }

    /**
     *
     * Removing a table
     *
     * @param tableName
     * @param databaseName
     * @throws XooDBSQLException
     */
    public void removeTable(String tableName, String databaseName)
            throws XooDBSQLException {
        /**
         * checks for <database db-name="databaseName">
         */
        Element element = (Element) xooDBXMLManager.getNode("database",
                "db-name", databaseName);
        if (element != null) {
            /**
             * if database is found
             *
             * checks for <table-info> within the <database> selected
             * to find
             * <table table-name="tableName">
             */
            Node tableInfoNode = xooDBXMLManager.getNode(element, "table-info");
            System.out.println("Node Name: " + tableInfoNode.getNodeName());
            Node tableNode = xooDBXMLManager.getNode((Element) tableInfoNode,
                    "table", "table-name", tableName);
            /**
             * if table is found
             * -remove the node corresponding to the table selected
             * -commit the XML file
             */
            if (tableNode != null) {
                tableInfoNode.removeChild(tableNode);
                xooDBXMLManager.commitXML();
            } else {                                                            // EXCEPTION
                XooDBSQLException xooDBSQLException = new XooDBSQLException();
                xooDBSQLException.setMessage(("Table: '" + tableName + "' does not exist."));
                throw xooDBSQLException;

            }
        } else {
            XooDBSQLException xooDBSQLException = new XooDBSQLException();
            xooDBSQLException.setMessage(("Database: '" + databaseName + "' does not exist."));
            throw xooDBSQLException;
        }
    }
}
