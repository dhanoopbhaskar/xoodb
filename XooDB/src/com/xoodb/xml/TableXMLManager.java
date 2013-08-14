/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.xoodb.xml;

import com.xoodb.XooDBSQLException;
import com.xoodb.beans.ConditionBean;
import com.xoodb.beans.DeleteStatementBean;
import com.xoodb.beans.InsertStatementBean;
import com.xoodb.beans.ResultBean;
import com.xoodb.beans.SelectQueryBean;
import com.xoodb.beans.TableBean;
import com.xoodb.beans.TableColumnBean;
import com.xoodb.beans.UpdateStatementBean;
import com.xoodb.beans.UserBean;
import com.xoodb.constants.XooDBConstants;
import com.xoodb.utilities.Utilities;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author dhanoopbhaskar
 */
public class TableXMLManager {
    private UserBean userBean = null;
    private XooDBXMLManager xooDBXMLManager = null;
    private String tableName = null;
    private String xmlPath = null;
    private TableBean tableBean = null;
    private Utilities utilities = new Utilities();

    public TableXMLManager() {
        xooDBXMLManager = new XooDBXMLManager();
    }

    public TableXMLManager(UserBean userBean) {
        this.userBean = userBean;
        String xmlSchema = getXmlSchemaFileName(userBean.getSchemaFileName());
    }

    /**
     * 
     * @param schemaFileName
     * @return
     */
    private String getXmlSchemaFileName(String schemaFileName) {
        if(schemaFileName.lastIndexOf(".") != -1) {
            return schemaFileName.substring(
                    0, schemaFileName.lastIndexOf(".")) + ".xsd";
        }
        return "";
    }

    public TableXMLManager(UserBean userBean, String tableName, 
            TableBean tableBean, String xmlPath) throws
            ParserConfigurationException, SAXException, IOException {
        this.userBean = userBean;
        this.tableName = tableName;
        this.tableBean = tableBean;
        this.xmlPath = xmlPath;
        xooDBXMLManager = new XooDBXMLManager(xmlPath);
    }

    public TableXMLManager(String xmlPath) throws
            ParserConfigurationException, SAXException, IOException {
        this.xmlPath = xmlPath;
        xooDBXMLManager = new XooDBXMLManager(xmlPath);
    }

    /**
     * 
     * @param tableXmlPath
     * @throws XMLStreamException
     * @throws FileNotFoundException
     */
    public void createTableXML(String tableXmlPath) throws XMLStreamException, FileNotFoundException {
        xooDBXMLManager.createXMLWriter(new File(tableXmlPath));
        /**
         * <?xml version="1.0" encoding="utf-8" standalone="no"?>
         */
        xooDBXMLManager.createStartDocument("utf-8", "1.0");
        /**
         * <user-schema xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
         */
        xooDBXMLManager.createStartElement("user-schema");
        xooDBXMLManager.createAttribute("xmlns:xsi",
                XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);
        /**
         * <table-info>
         */
        xooDBXMLManager.createStartElement("table-info");
        /**
         * <table-name>
         */
        xooDBXMLManager.createStartElement("table-name");
        /**
         * adds tableName as data content
         */
        xooDBXMLManager.writeElementTextValue(tableName);
        /**
         * </table-name>
         */
        xooDBXMLManager.createCloseElement();
        /**
         * </table-info>
         */
        xooDBXMLManager.createCloseElement();
        /**
         * <data>
         */
        xooDBXMLManager.createStartElement("data");
        /**
         * </data>
         */
        xooDBXMLManager.createCloseElement();
        /**
         * </user-schema>
         */
        xooDBXMLManager.createCloseElement();
        xooDBXMLManager.closeWriter();
    }

    /**
     * 
     * @param insertStatementBean
     * @throws XooDBSQLException
     */
    public void insertTableRow(InsertStatementBean insertStatementBean) 
            throws XooDBSQLException {
        String[] values = insertStatementBean.getValues();
        
        if(tableBean == null) {
            XooDBSQLException xooDBSQLException = new XooDBSQLException();
            xooDBSQLException.setMessage("Table does not exist");
            throw xooDBSQLException;
        }

        TableColumnBean[] tableColumnBeans = tableBean.getColumns();
        
        if(tableColumnBeans.length != values.length) {
            XooDBSQLException xooDBSQLException = new XooDBSQLException();
            xooDBSQLException.setMessage("SQL Error: Number of field "
                    + "values doesnot match");
            throw xooDBSQLException;
        }
        /**
         *  <data>
         *      <row>
         *          values for each column
         *      </row>
         *      <row>
         *
         *      </row>
         *  </data>
         */
        Node dataNode = xooDBXMLManager.getNode("data");
        Node rowNode = xooDBXMLManager.addChildNode(dataNode, "row");
        for(int i = 0 ; i < tableColumnBeans.length ; i++) {
            TableColumnBean columnBean = tableColumnBeans[i];

            boolean okPrimaryKey = false;
            boolean okNullCheck = false;

            /**
             * check for primary key
             */
            if(columnBean.isPrivateKey()) {
                okPrimaryKey = checkPrimaryKey(rowNode, columnBean, values[i]);
            } else {
                okPrimaryKey = true;
            }

            /**
             * check for null allowed
             */
             if(columnBean.isNullAllowed()) {
                okNullCheck = true;
             } else {
                okNullCheck = checkNullAllowed(rowNode, columnBean, values[i]);
             }

            checkForSize(columnBean, values[i]);
            checkForDataTypeMisMatch(columnBean, values[i]);

            if(checkDataType(columnBean.getDataType(), values[i].trim()) && okPrimaryKey && okNullCheck) {
                xooDBXMLManager.addChildNode(rowNode,
                        columnBean.getColumnName(), values[i]);
            } else {
                /**
                 * handling error prone situation
                 */
                if(!okPrimaryKey) {
                    XooDBSQLException xooDBSQLException = new XooDBSQLException();
                    xooDBSQLException.setMessage("SQL Error: "
                        + "Duplicate value for primary key");
                    throw xooDBSQLException;
                } else if(!okNullCheck) {
                    XooDBSQLException xooDBSQLException = new XooDBSQLException();
                    xooDBSQLException.setMessage("SQL Error: "
                        + "Insert operation failed as null is not allowed in "
                        + "one of the columns and you tried to insert a null value");
                    throw xooDBSQLException;
                } else {
                    XooDBSQLException xooDBSQLException = new XooDBSQLException();
                    xooDBSQLException.setMessage(
                            "SQL Error: Incompatible type '" + values[i] + "'");
                    throw xooDBSQLException;
                }
            }
        }
        xooDBXMLManager.commitXML(false);
    }

    /**
     * 
     * @param dataType
     * @param trim
     * @return
     */
    private boolean checkDataType(String dataType, String trim) {
        if(dataType.equalsIgnoreCase(XooDBConstants.VARCHAR)) {
            return true;
        } else if(dataType.equalsIgnoreCase(XooDBConstants.INT)) {
            return true;
        } else if(dataType.equalsIgnoreCase(XooDBConstants.CHAR)) {
            return true;
        } else if(dataType.equalsIgnoreCase(XooDBConstants.DATE)) {
            return true;
        } else if(dataType.equalsIgnoreCase(XooDBConstants.DATE_TIME)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 
     * @param selectQueryBean
     * @param allColumnNames
     * @return
     * @throws XooDBSQLException
     */
    public ResultBean getTableData(SelectQueryBean selectQueryBean,
            String[] allColumnNames) throws XooDBSQLException {
        int[] rowNos;
        int r = 0;
        ResultBean resultBean = new ResultBean();
        resultBean.setType("TABLE");
        /**
         * extracting <row> nodes with <data> as the parent node
         */
        Node dataNode = xooDBXMLManager.getNode("data");
        NodeList rows = xooDBXMLManager.getElements((Element) dataNode, "row");
        String[] columnNames = selectQueryBean.getColumnNames();
        /**
         *  2D array for table data
         */
        String[][] tableData2 = new String[rows.getLength()][columnNames.length];
        
        rowNos = new int[rows.getLength()];
        for(int i = 0 ; i < rows.getLength() ; i++) {
            Element row = (Element) rows.item(i);
            if(selectQueryBean.getConditions().size() == 0 ||
                    checkConditions(
                    row, selectQueryBean.getConditions(), allColumnNames)) {
                rowNos[r++] = i;
                for(int j = 0 ; j < columnNames.length ; j++) {
                    /**
                     * extracting the column values from the tags represented by
                     * column names that is within <row> tag
                     */
                    String cellData = xooDBXMLManager.getElementValue(row, columnNames[j]);
                    if(cellData != null && !cellData.equals("")) {
                        tableData2[i][j] = cellData;                        
                    } else {                                                    //EXCEPTION
                        XooDBSQLException xooDBSQLException =
                                new XooDBSQLException();
                        xooDBSQLException.setMessage(
                                "SQL Error: Field '" + columnNames[j]
                                + "' does not exist");
                        throw xooDBSQLException;
                    }
                }
            }
        }
        resultBean.setTableColumnNames(columnNames);
        String[][] tableData = new String[r][columnNames.length];
        for (int i = 0 ; i < r ; i++) {
            tableData[i] = tableData2[rowNos[i]];
        }
        resultBean.setTableData(tableData);
        return resultBean;
    }

    /**
     * 
     * @param rowNode
     * @param conditions
     * @param columnNames
     * @return
     */
    private boolean checkConditions(Node rowNode, LinkedList conditions, 
            String[] columnNames) throws XooDBSQLException {
        boolean result = false;
        /**
         * if no condition is specified true should be returned
         */
        if(conditions == null || conditions.size() == 0) {
            return true;
        }
        
        for(int i = 0 ; i < conditions.size() ; i+=2) { //for            
            /**
             * condition linked list may also contain keywords like "and" & "or"
             *
             * so checks whether the entry represents a condition
             * in ConditionBean
             */
            if(conditions.get(i) instanceof ConditionBean)  {//if 1
                ConditionBean conditionBean = (ConditionBean) conditions.get(i);                
                /**
                 * checks whether the collection of columnNames contains the
                 * column name corresponding to the conditionBean
                 * -using binary search
                 */

//                for(int x = 0 ; x < columnNames.length ; x++) {
//                    System.out.println("*" + columnNames[x]);
//                }
//                System.out.println("check for: " + conditionBean.getColumnName().trim());
//                System.out.println(Arrays.binarySearch(columnNames, conditionBean.getColumnName().trim()));
                //if(Arrays.binarySearch(columnNames, conditionBean.getColumnName().trim()) >= 0) {//if 2
                
                if(searchInFor(columnNames, conditionBean.getColumnName().trim())) {// if 2                    
                    /**
                     * retrieve the value corresponding to the columnName
                     * within the rowNode
                     */
                    String cellValue = xooDBXMLManager.getElementValue((Element) rowNode, conditionBean.getColumnName());
                    boolean tempResult = check(cellValue.replace("'", ""), conditionBean.getValue().trim(), conditionBean.getOperator());
                    if(i == 0) {//if 3
                        result = tempResult;
                    } else {
                        /**
                         * checks whether the previous item in the linked list
                         * is a string
                         *
                         * if yes
                         * checks whether its "and" or "or"
                         *
                         * if "and"
                         * logical AND the previous result (in variable result)
                         * and current one (in variable tempResult)
                         *
                         * if "or"
                         * logical OR the previous result (in variable result)
                         * and current one (in variable tempResult)
                         */
                        if(conditions.get(i - 1) instanceof String) {//if 4
                            String concatKeyword = (String) conditions.get(i - 1);
                            if(concatKeyword.equalsIgnoreCase("and")) {//if 5
                                result = result & tempResult;
                            } else if(concatKeyword.equalsIgnoreCase("or")) {
                                result = result | tempResult;
                            }//if 5
                        }//if 4
                    }//if 3
                } else {//if 2
                    XooDBSQLException xooDBSQLException = new XooDBSQLException();
                    xooDBSQLException.setMessage("SQL Error: The column name "
                            + "you specified in the condition cannot be found");
                    throw xooDBSQLException;
                }
            }//if 1
        }//for
        return result;
    }

    /**
     * 
     * @param cellValue
     * @param value
     * @param operator
     * @return
     */
    private boolean check(String cellValue, String value, String operator) {
        
//        System.out.println("cellValue: " + cellValue);
//        System.out.println("value: " + value);
//        System.out.println("operator: " + operator);

        if (operator.trim().equals("=")) {
            return cellValue.compareTo(value) == 0 ? true : false;
        } else if (operator.trim().equals("!=")) {
            return cellValue.compareTo(value) != 0 ? true : false;
        } else if (operator.trim().equals("<")) {
            return cellValue.compareTo(value) < 0 ? true : false;
        } else if (operator.trim().equals(">")) {
            return cellValue.compareTo(value) > 0 ? true : false;
        } else {
            return false;
        }
    }

    /**
     * 
     * @param selectedRow
     * @throws TransformerConfigurationException
     * @throws TransformerException
     */
    public void getRemoveRow(int selectedRow) throws
            TransformerConfigurationException, TransformerException {
        /**
         * retrieve all the nodes with tag <row> within parent node <data>         
         */
        Node dataNode = xooDBXMLManager.getNode("data");
        NodeList rowNodeList = xooDBXMLManager.getElements(
                (Element) dataNode, "row");
        /**
         * traverse the nodes to see whether it represents the row selected
         *
         * if yes
         * delete the node that represents the row selected
         */
        for(int i = 0 ; i < rowNodeList.getLength() ; i++) {
            Node rowNode = rowNodeList.item(i);            
            if(i == selectedRow) {
                dataNode.removeChild(rowNode);
            }
        }
        xooDBXMLManager.commitXML(false);
    }

    /**
     * 
     * @param selectedRow
     * @param selectedColumn
     * @param cellData
     * @param fieldName
     * @throws TransformerConfigurationException
     * @throws TransformerException
     */
    public void updateCell(int selectedRow, int selectedColumn,
            String cellData, String fieldName)
            throws TransformerConfigurationException, TransformerException {
        /**
         * retrieve all the nodes with tag <row> within parent node <data>
         */
        Node dataNode = xooDBXMLManager.getNode("data");
        NodeList rowNodeList = xooDBXMLManager.getElements(
                (Element) dataNode, "row");
        /**
         * traverse the nodes to see whether it represents the row selected
         *
         * if yes
         * retrieve the child nodes that represents each column of the row
         * that is selected
         *
         * traverse each such child nodes to see whether it represents
         * the fieldName
         *
         * if yes
         * set cellData as its text content
         */
        for(int i = 0 ; i < rowNodeList.getLength() ; i++) {
            Node rowNode = rowNodeList.item(i);
            NodeList cellNodeList = rowNode.getChildNodes();
            if(i == selectedRow) {
                for(int j = 0 ; j < cellNodeList.getLength() ; j++) {
                    Node cellNode = cellNodeList.item(j);
                    if(cellNode.getNodeName().equals(fieldName)) {
                        cellNode.setTextContent(cellData);
                    }
                }
            }
        }
        xooDBXMLManager.commitXML(false);
    }

    /**
     * 
     * @param deleteStatementBean
     * @param columnNames
     * @return
     * @throws TransformerConfigurationException
     * @throws TransformerException
     */
    public String deleteTableRow(DeleteStatementBean deleteStatementBean,
            String[] columnNames)
            throws TransformerConfigurationException, TransformerException, XooDBSQLException {
        int noOfRowsUpdated = 0;
        /**
         * retrieve all the nodes with tag <row> within parent node <data>
         */
        Node dataNode = xooDBXMLManager.getNode("data");
        NodeList rowNodeList = xooDBXMLManager.getElements(
                (Element) dataNode, "row");
        /**
         * retrieve all the conditions from deleteStatementBean
         */
        LinkedList conditionList = deleteStatementBean.getConditions();
        if(conditionList.size() == 0) {
            /**
             * if no condition is specified
             * delete all the rows (nodes)
             */
            while(rowNodeList.getLength() > 0) {
                dataNode.removeChild(rowNodeList.item(0));
                noOfRowsUpdated++;
            }
        } else {
            /**
             * if some conditions are there
             * -retrieve each row
             * -check whether it satisfies the conditions
             *      using method checkConditions()
             * -if yes
             *      then delete the node representing that row
             * -if no
             *      continue with remaining rows (nodes)
             */
            for(int i = 0 ; i < rowNodeList.getLength() ; i++) {
                Node rowNode = rowNodeList.item(i);
                if(checkConditions(rowNode, conditionList, columnNames)) {
                    dataNode.removeChild(rowNode);
                    noOfRowsUpdated++;
                    i = 0;
                } else {
                    continue;
                }
            }
        }
        xooDBXMLManager.commitXML(false);
        return (noOfRowsUpdated + " rows removed");
    }

    /**
     * 
     * @param updateStatementBean
     * @param columnBeans
     * @return
     * @throws XooDBSQLException
     * @throws TransformerConfigurationException
     * @throws TransformerException
     */
    public String updateTable(UpdateStatementBean updateStatementBean, 
            TableColumnBean[] columnBeans)
            throws XooDBSQLException, TransformerConfigurationException,
            TransformerException {
        int noOfRowsUpdated = 0;
        String[] allColumnNames = utilities.getColumnNames(columnBeans);
        /**
         * retrieve all the nodes with tag <row> within parent node <data>
         */
        Node dataNode = xooDBXMLManager.getNode("data");
        NodeList rowNodeList = xooDBXMLManager.getElements((Element) dataNode, "row");
        /**
         * retrieve all the conditions from deleteStatementBean
         */
        LinkedList conditionList = updateStatementBean.getConditions();
        for (int i = 0; i < rowNodeList.getLength(); i++) {
            Node rowNode = rowNodeList.item(i);
            if (checkConditions(rowNode, conditionList, allColumnNames)) {
                Iterator updateColumns = updateStatementBean.getColumnValues().keySet().iterator();
                while (updateColumns.hasNext()) {
                    String updateColumName = (String) updateColumns.next();
                    String value = updateStatementBean.getColumnValues().get(updateColumName).replaceAll("'", "").trim();
                    TableColumnBean columnBean = utilities.getColumnBean(columnBeans, updateColumName);
                    if (columnBean != null) {
                        boolean okPrimaryKey = false;
                        boolean okNullCheck = false;
                        /**
                         * check for primary key
                         */
                        if(columnBean.isPrivateKey()) {
                            okPrimaryKey = checkPrimaryKey(rowNode, columnBean, value);
                        } else {
                            okPrimaryKey = true;
                        }

                        /**
                         * check for null allowed
                         */
                         if(columnBean.isNullAllowed()) {
                            okNullCheck = true;
                         } else {
                            okNullCheck = checkNullAllowed(rowNode, columnBean, value);
                         }

                        checkForSize(columnBean, value);
                        checkForDataTypeMisMatch(columnBean, value);

                        if (checkDataType(columnBean.getDataType(), value) && okPrimaryKey && okNullCheck) {
                            Node cell = xooDBXMLManager.getNode((Element) rowNode, columnBean.getColumnName());
                            //System.out.println("node value: " + cell.getNodeValue());
                            //System.out.println("node name: " + cell.getNodeName());
                            cell.setTextContent(value);
                        } else {
                            if(!okPrimaryKey) {
                                XooDBSQLException xooDBSQLException = new XooDBSQLException();
                                xooDBSQLException.setMessage("SQL Error: "
                                    + "Duplicate value for primary key");
                                throw xooDBSQLException;
                            } else if(!okNullCheck) {
                                XooDBSQLException xooDBSQLException = new XooDBSQLException();
                                xooDBSQLException.setMessage("SQL Error: "
                                    + "Update operation failed as null is not allowed in "
                                    + "the column and you tried to update with a null value");
                                throw xooDBSQLException;
                            } else {
                                XooDBSQLException xooDBSQLException = new XooDBSQLException();
                                xooDBSQLException.setMessage("SQL Error: "
                                        + "Type miss match");
                                throw xooDBSQLException;
                            }
                        }
                    } else {
                        XooDBSQLException xooDBSQLException = new XooDBSQLException();
                        xooDBSQLException.setMessage("SQL Error: Field '" +
                                updateColumName + "' does not exist");
                        throw xooDBSQLException;
                    }
                }
                noOfRowsUpdated++;
            } else {
                continue;
            }
        }
        xooDBXMLManager.commitXML(false);        
        return (noOfRowsUpdated + " rows updated");
    }

    /**
     * check for primary key
     *
     * @param parentNode
     * @param columnBean
     * @param newValue
     * @return
     */
    private boolean checkPrimaryKey(Node parentNode, TableColumnBean columnBean, String newValue) {
        boolean noDuplicatePrimaryKey = true;

        //System.out.println("new value: " + newValue);

        String columnName = columnBean.getColumnName();
        Node parentCell = xooDBXMLManager.getNode(
                (Element) parentNode, columnBean.getColumnName());
        /**
         * traverse all the nodes
         */
        Node dataNode = xooDBXMLManager.getNode("data");
        NodeList rowNodeList = xooDBXMLManager.getElements(
                (Element) dataNode, "row");
        
        for (int i = 0; i < rowNodeList.getLength(); i++) {
            Node rowNode = rowNodeList.item(i);
            Node cell = xooDBXMLManager.getNode((Element) rowNode, columnBean.getColumnName());
            if(cell != parentCell) {
                //System.out.println("cell: " + cell.getTextContent());
                if(cell.getTextContent().equals(newValue)) {
                    noDuplicatePrimaryKey = false;
                    return noDuplicatePrimaryKey;
                }
            }
        }

        return noDuplicatePrimaryKey;
    }

    /**
     * check for null allowed
     *
     * @param parentNode
     * @param columnBean
     * @param newValue
     * @return
     */
    private boolean checkNullAllowed(Node parentNode, TableColumnBean columnBean, String newValue) {
        boolean isOkNullCheck = false;
        //displayColumnProps(columnBean);
        //System.out.println("value: " + newValue);
        if(columnBean.isNullAllowed()) {
            isOkNullCheck = true;
        } else {
            if(newValue.equals("") || newValue == null ||
                    newValue.equalsIgnoreCase("null")) {
                isOkNullCheck = false;
            } else {
                isOkNullCheck = true;
            }
        }
        return isOkNullCheck;
    }

    private void displayColumnProps(TableColumnBean columnBean) {
        System.out.println(columnBean.getColumnName());
        System.out.println(columnBean.getDataType());
        System.out.println(columnBean.isPrivateKey());
        System.out.println(columnBean.isNullAllowed());
        System.out.println();
    }

    private boolean searchInFor(String[] columnNames, String columnName) {
        boolean result = false;
        for(int i = 0 ; i < columnNames.length ; i++) {
            if(columnNames[i].equals(columnName)) {
                return true;
            }
        }
        return result;
    }

    private void checkForSize(TableColumnBean columnBean, String value) throws XooDBSQLException {
        int originalSize = columnBean.getSize();
        int currentSize = getCharCount(value);

        if(currentSize <= originalSize) {

        } else {
            XooDBSQLException xooDBSQLException = new XooDBSQLException();
            xooDBSQLException.setMessage("SQL Error: The value for '" +
                    columnBean.getColumnName() + "' exceeds the size limit");
            throw xooDBSQLException;
        }
    }

    private int getCharCount(String value) {
        int size = 0;
        //System.out.println("value: " + value);
        char[] characters = value.toCharArray();

        for(char ch : characters) {
            size++;
        }

        //System.out.println("char count: " + size);

        return size;
    }

    private void checkForDataTypeMisMatch(TableColumnBean columnBean, String value)
            throws XooDBSQLException {
        String dataType = columnBean.getDataType();

        //System.out.println("datatype: " + dataType);

        if(dataType.equalsIgnoreCase(XooDBConstants.INT)) {
            try {
                int i = Integer.parseInt(value);
            } catch(NumberFormatException numExp) {
                XooDBSQLException xooDBSQLException = new XooDBSQLException();
                xooDBSQLException.setMessage("SQL Error: Datatype mismatch "
                        + "for field '" + columnBean.getColumnName() + "'");
                throw xooDBSQLException;
            }
        }
    }
}
