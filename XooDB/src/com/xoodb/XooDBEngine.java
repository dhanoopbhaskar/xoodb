/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.xoodb;

import com.xoodb.beans.CreateDatabaseBean;
import com.xoodb.beans.DatabaseBean;
import com.xoodb.beans.DeleteStatementBean;
import com.xoodb.beans.InsertStatementBean;
import com.xoodb.beans.ResultBean;
import com.xoodb.beans.SQLBean;
import com.xoodb.beans.SelectQueryBean;
import com.xoodb.beans.ShowDatabase;
import com.xoodb.beans.TableBean;
import com.xoodb.beans.UpdateStatementBean;
import com.xoodb.beans.UserBean;
import com.xoodb.constants.XooDBConstants;
import com.xoodb.main.XooDBServerMain;
import com.xoodb.utilities.Utilities;
import com.xoodb.xml.DatabaseSchemaXMLManager;
import com.xoodb.xml.DatabaseXMLManager;
import com.xoodb.xml.TableXMLManager;
import com.xoodb.xml.UserXMLManager;
import java.io.File;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 *
 * @author dhanoopbhaskar
 */
public class XooDBEngine {
    private String userName = "";
    private boolean login = false;
    private UserBean userBean = null;
    private String databaseName = "";
    private Element databaseNode = null;
    private Utilities utilities = new Utilities();

    public XooDBEngine() {
    }

    /**
     * 
     * @param username
     * @param password
     * @return
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public boolean login(String userName, String password) throws
            ParserConfigurationException, SAXException, IOException, XooDBSQLException {
        UserXMLManager userXMLManager = new UserXMLManager();
        userBean = userXMLManager.getUserInfo(userName);
        if (userBean != null && userBean.getPassword().equals(password)) {
            this.userName = userName;
            return true;
        }
        return false;
    }

    /**
     * 
     * @param sqlStatement
     * @return
     * @throws XooDBSQLException
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     * @throws TransformerConfigurationException
     * @throws TransformerException
     * @throws XMLStreamException
     */
    public String executeUpdate(String sqlStatement) throws XooDBSQLException,
            ParserConfigurationException, IOException, SAXException,
            TransformerConfigurationException, TransformerException,
            XMLStreamException {
        String statementResult = XooDBConstants.FAIL;
        XooDBParser xooDBParser = new XooDBParser(this);
        SQLBean sqlBean = xooDBParser.parseSQLQuery(sqlStatement);
        if(sqlBean instanceof CreateDatabaseBean) {                             //CreateDatabaseBean
            /**
             * create an object of DatabaseXMLManager
             * call the method createDatabase(CreateDatabaseBean)
             * using that object
             */
            DatabaseXMLManager databaseXMLManager = new DatabaseXMLManager(this);
            databaseXMLManager.createDatabase((CreateDatabaseBean) sqlBean);
        } else if(sqlBean instanceof DatabaseBean) {                            //DatabaseBean
            /**
             * create an object of DatabaseXMLManager
             */
            DatabaseXMLManager databaseXMLManager = new DatabaseXMLManager(this);
            /**
             * if the queryType if "DROP"
             * -call the method removeDatabase(databaseName)
             * else
             * -call the method useDatabase(DatabaseBean)
             */
            if( ((DatabaseBean) sqlBean).getQueryType().equalsIgnoreCase(
                    XooDBConstants.DROP) ) {
                databaseXMLManager.removeDatabase(
                        ((DatabaseBean) sqlBean).getDatabaseName());
            } else {
                if(databaseXMLManager.useDatabase((DatabaseBean) sqlBean)) {                    
                    statementResult = XooDBConstants.SUCCESS;
                } else {                                                        //EXCEPTION
                    XooDBSQLException xooDBSQLException = new XooDBSQLException();
                    xooDBSQLException.setMessage("Database: "
                            + ((DatabaseBean) sqlBean).getDatabaseName()
                            + " does not exist");
                    throw xooDBSQLException;
                }
            }
        } else if(sqlBean instanceof UserBean) {                                //UserBean
            /**
             * create an object of UserXMLManager
             * call the method createUser(UserBean)
             */
            UserXMLManager userXMLManager = new UserXMLManager();
            userXMLManager.createUser((UserBean) sqlBean);
        } else if(sqlBean instanceof TableBean) {                               //TableBean
            /**
             * create an object of DatabaseSchemaXMLManager
             */
            DatabaseSchemaXMLManager databaseSchemaXMLManager =
                    new DatabaseSchemaXMLManager(userBean);
            if (((TableBean) sqlBean).getQueryType().equals(
                    XooDBConstants.DROP)) {                                     // Drop Table
                System.out.println("entered");
                /**
                 * if queryType is "DROP"
                 * -call the method removeTable(tableName, databaseName)
                 */
                databaseSchemaXMLManager.removeTable(
                        ((TableBean) sqlBean).getTableName(), databaseName);
                /**
                 * 
                 */
                File tableXmlFile = new File(userBean.getUserDir() + File.separator
                        + databaseName + File.separator
                        + ((TableBean) sqlBean).getTableName() + ".xml");
                tableXmlFile.deleteOnExit();
            } else {                                                            // Create Table
                /**
                 * add the table info into the schema using the method
                 * addTableInfoIntoSchema(TableBean, databaseName)
                 */
                databaseSchemaXMLManager.addTableInfoIntoSchema(
                        (TableBean) sqlBean, databaseName);
                String userDir = userBean.getUserDir();
                if (userDir != null && !userDir.equals("")) {
                    try {
                        File tableFile = utilities.createTableXmlFileDir(
                                new File(userDir + File.separator
                                + databaseName + File.separator
                                + ((TableBean) sqlBean).getTableName()
                                + ".xml"));
                        System.out.println(tableFile.createNewFile());
                        /**
                         * create an object of TableXMLManager
                         * call the method createTableXML(tableXmlPath)
                         */
                        TableXMLManager tableXMLManager = new TableXMLManager();
                        tableXMLManager.createTableXML(tableFile.getAbsolutePath());
                    } catch (XMLStreamException ex) {
                        System.out.println(ex);
                        XooDBServerMain.appendExceptionLog(ex);
                    }
                }
            }
        } else if(sqlBean instanceof InsertStatementBean) {                     //InsertStatementBean
            /**
             * create an object of DatabaseXMLManager
             */
            DatabaseXMLManager databaseXMLManager = new DatabaseXMLManager(this);
            /**
             * get the table meta data by calling the method
             * getTableMetaData(userName, tableName)
             */
            TableBean tableBean = databaseXMLManager.getTableMetaData(userName,
                    ((InsertStatementBean) sqlBean).getTableName());
            /**
             * create an object of TableXMLManager
             * UserBean
             * tableName
             * TableBean
             * xmlPath
             */
            String xmlPath = userBean.getUserDir()
                    + File.separator
                    + databaseName
                    + File.separator
                    + ((InsertStatementBean) sqlBean).getTableName()
                    + ".xml";
            TableXMLManager tableXMLManager = new TableXMLManager(userBean,
                    ((InsertStatementBean) sqlBean).getTableName(),
                    tableBean,
                    xmlPath);
            /**
             * insert a row using the values from the InsertStatementBean
             * calling the method
             * insertTableRow(InsertStatementBean)
             */
            tableXMLManager.insertTableRow((InsertStatementBean) sqlBean);
        } else if(sqlBean instanceof DeleteStatementBean) {                     //DeleteStatementBean
            /**
             * create an object of DatabaseXMLManager
             */
            DatabaseXMLManager databaseXMLManager = new DatabaseXMLManager(this);
            /**
             * get the table meta data by calling the method
             * getTableMetaData(userName, tableName)
             */
            TableBean tableBean = databaseXMLManager.getTableMetaData(userName,
                    ((DeleteStatementBean) sqlBean).getTableName());
            if (tableBean != null) {
                /**
                 * create an object of TableXMLManager
                 * UserBean
                 * tableName
                 * TableBean
                 * xmlPath
                 */
                String xmlPath = userBean.getUserDir()
                        + File.separator
                        + databaseName
                        + File.separator
                        + ((DeleteStatementBean) sqlBean).getTableName()
                        + ".xml";
                TableXMLManager tableXMLManager = new TableXMLManager(userBean,
                        ((DeleteStatementBean) sqlBean).getTableName(),
                        tableBean,
                        xmlPath);
                /**
                 * perform deletion using the method
                 * deleteTableRow(DeleteStatementBean, columnNames)
                 */
                return tableXMLManager.deleteTableRow((DeleteStatementBean) sqlBean,
                        utilities.getColumnNames(tableBean.getColumns()));
            } else {                                                            //EXCEPTION
                XooDBSQLException xooDBSQLException = new XooDBSQLException();
                xooDBSQLException.setMessage("Table: "
                        + ((DeleteStatementBean) sqlBean).getTableName()
                        + " does not exists.");
                throw xooDBSQLException;
            }
        } else if(sqlBean instanceof UpdateStatementBean) {                     //UpdateStatementBean
            /**
             * create an object of DatabaseXMLManager
             */
            DatabaseXMLManager databaseXMLManager = new DatabaseXMLManager(this);
            /**
             * get the table meta data by calling the method
             * getTableMetaData(userName, tableName)
             */
            TableBean tableBean = databaseXMLManager.getTableMetaData(userName,
                    ((UpdateStatementBean) sqlBean).getTableName());
            if (tableBean != null) {
                /**
                 * create an object of TableXMLManager
                 * UserBean
                 * tableName
                 * TableBean
                 * xmlPath
                 */
                String xmlPath = userBean.getUserDir()
                        + File.separator
                        + databaseName
                        + File.separator
                        + ((UpdateStatementBean) sqlBean).getTableName()
                        + ".xml";
                
                TableXMLManager tableXMLManager = new TableXMLManager(userBean,
                        ((UpdateStatementBean) sqlBean).getTableName(),
                        tableBean,
                        xmlPath);
                /**
                 * perform update operation using the method
                 * updateTable(UpdateStatementBean, TableColumnBean[])
                 */
                return tableXMLManager.updateTable((UpdateStatementBean) sqlBean,
                        tableBean.getColumns());
            } else {                                                            //EXCEPTION
                XooDBSQLException xooDBSQLException = new XooDBSQLException();
                xooDBSQLException.setMessage("Table: "
                        + ((UpdateStatementBean) sqlBean).getTableName()
                        + " does not exist");
                throw xooDBSQLException;
            }
        } else {                                                                //EXCEPTION
            XooDBSQLException xooDBSQLException = new XooDBSQLException();
            xooDBSQLException.setMessage("SQL Error: Invalid command");
            throw xooDBSQLException;
        }
        return statementResult;
    }

    /**
     * 
     * @param sqlQuery
     * @return
     * @throws XooDBSQLException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public ResultBean executeQuery(String sqlQuery) throws XooDBSQLException,
            ParserConfigurationException, SAXException, IOException {
        XooDBParser xooDBParser = new XooDBParser(this);
        SQLBean sqlBean = xooDBParser.parseSQLQuery(sqlQuery);
        if(sqlBean instanceof ShowDatabase) {                                   //SHOW DATABASES
            /**
             * create an object of DatabaseXMLManager
             * call the method getAllDatabases(userName)
             */
            DatabaseXMLManager databaseXMLManager = new DatabaseXMLManager(this);
            return databaseXMLManager.getAllDatabases(userName);
        } else if(sqlBean instanceof SelectQueryBean) {                         //SELECT            
            SelectQueryBean selectQueryBean = (SelectQueryBean) sqlBean;
            /**
             * create an object of DatabaseXMLManager
             */
            DatabaseXMLManager databaseXMLManager = new DatabaseXMLManager(this);
            /**
             * get the table meta data by calling the method
             * getTableMetaData(userName, tableName)
             */
            TableBean tableBean = databaseXMLManager.getTableMetaData(userName,
                    selectQueryBean.getTableName());
            if (tableBean != null) {
                /**
                 * retrieve the name of the file where table data is stored
                 * using the method
                 * getTableDataXMLFile(XooDBEngine, tableName)
                 */
                String tableDataFileName = utilities.getTableDataXMLFile(
                        this, selectQueryBean.getTableName());                
                /**
                 * create an object of TableXMLManager
                 * xmlPath
                 */
                TableXMLManager tableXMLManager = new TableXMLManager(
                        tableDataFileName);
                /**
                 * getTableData(SelectQueryBean, columnNames)
                 */
                return tableXMLManager.getTableData(
                        selectQueryBean, utilities.getColumnNames(
                        tableBean.getColumns()));
            } else {
                XooDBSQLException xooDBSQLException = new XooDBSQLException();
                xooDBSQLException.setMessage("SQL Error: Table '" 
                        + selectQueryBean.getTableName()
                        + "' does not exist");
                throw xooDBSQLException;
            }
        } else {                                                                //EXCEPTION
            XooDBSQLException xooDBSQLException = new XooDBSQLException();
            xooDBSQLException.setMessage("SQL Error: Invalid query");
            throw xooDBSQLException;
        }
        //return null;
    }

    /**
     * 
     * @param tableName
     * @param selectedRow
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     * @throws TransformerConfigurationException
     * @throws TransformerException
     */
    public void removeSelectedRow(String tableName, int selectedRow)
            throws ParserConfigurationException, SAXException, IOException,
            TransformerConfigurationException, TransformerException {
        String tableDataFileName = utilities.getTableDataXMLFile(this, tableName);
        TableXMLManager tableXMLManager = new TableXMLManager(tableDataFileName);
        tableXMLManager.getRemoveRow(selectedRow);
    }

    /**
     * 
     * @param tableName
     * @param selectedRow
     * @param selectedColumn
     * @param cellData
     * @param fieldName
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     * @throws TransformerConfigurationException
     * @throws TransformerException
     */
    public void updateCell(String tableName, int selectedRow,
            int selectedColumn, String cellData, String fieldName)
            throws ParserConfigurationException, SAXException, IOException,
            TransformerConfigurationException, TransformerException {
        String tableDataFileName = utilities.getTableDataXMLFile(this, tableName);
        TableXMLManager tableXMLManager = new TableXMLManager(tableDataFileName);
        tableXMLManager.updateCell(selectedRow, selectedColumn, cellData, fieldName);
    }
    

    /**
     * @param userName the userName to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param login the login to set
     */
    public void setLogin(boolean login) {
        this.login = login;
    }

    /**
     * @return login
     */
    public boolean isLogin() {
        return login;
    }

    /**
     * @param userBean the userBean to set
     */
    public void setUserBean(UserBean userBean) {
        this.userBean = userBean;
    }

    /**
     * @return the userBean
     */
    public UserBean getUserBean() {
        return userBean;
    }

    /**
     * @param databaseName the databaseName to set
     */
    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    /**
     * @return the databaseName
     */
    public String getDatabaseName() {
        return databaseName;
    }

    /**
     * @param databaseNode the databaseNode to set
     */
    public void setDatabaseNode(Element databaseNode) {
        this.databaseNode = databaseNode;
    }

    /**
     * @return the databaseNode
     */
    public Element getDatabaseNode() {
        return databaseNode;
    }
}
