/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.xoodb;

import com.xoodb.beans.ConditionBean;
import com.xoodb.beans.CreateDatabaseBean;
import com.xoodb.beans.DatabaseBean;
import com.xoodb.beans.DeleteStatementBean;
import com.xoodb.beans.InsertStatementBean;
import com.xoodb.beans.SQLBean;
import com.xoodb.beans.SelectQueryBean;
import com.xoodb.beans.ShowDatabase;
import com.xoodb.beans.TableBean;
import com.xoodb.beans.TableColumnBean;
import com.xoodb.beans.UpdateStatementBean;
import com.xoodb.beans.UserBean;
import com.xoodb.constants.XooDBConstants;
import com.xoodb.main.XooDBServerMain;
import com.xoodb.utilities.Utilities;
import com.xoodb.xml.DatabaseXMLManager;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 *
 * @author dhanoopbhaskar
 */
public class XooDBParser {
    private XooDBEngine xooDBEngine = null;
    private Utilities utilities = new Utilities();
    //private boolean isCreateStmnt = false;
    private boolean isFirstLeftParanthesis = true;
    private int indexOfFirstParanthesis = -1;

    public XooDBParser(){
    }

    public XooDBParser(XooDBEngine xooDBEngine) {
        this.xooDBEngine = xooDBEngine;
    }

    /**
     * 
     * @param query
     * @return
     * @throws XooDBSQLException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public SQLBean parseSQLQuery(String query) throws 
            XooDBSQLException, ParserConfigurationException, SAXException,
            IOException {
        isFirstLeftParanthesis = true;
        SQLBean sqlBean = null;
        StringBuffer sqlQueryBuffer = new StringBuffer(query);

        if(sqlQueryBuffer.indexOf("(") != -1) {
            /**
             * adding a whitespace before the first occurrence of '('
             * to make to act as a delimiter for splitting tokens
             */
            indexOfFirstParanthesis = sqlQueryBuffer.indexOf("(");
            sqlQueryBuffer.insert(indexOfFirstParanthesis, " ");
        }
        System.out.println(sqlQueryBuffer);
        String command = nextToken(sqlQueryBuffer);
        if(command.equalsIgnoreCase(XooDBConstants.CREATE)) {                   //CREATE
            String nextToCreate = nextToken(sqlQueryBuffer);
            if(nextToCreate.equalsIgnoreCase(XooDBConstants.DATABASE)) {        //CREATE->DATABASE
                sqlBean = parseCreateDatabaseCommand(sqlQueryBuffer);
            } else if(nextToCreate.equalsIgnoreCase(XooDBConstants.TABLE)) {    //CREATE->TABLE
                //isCreateStmnt = true;
                sqlBean = parseCreateTableCommand(sqlQueryBuffer);
                //isCreateStmnt = false;
            } else if(nextToCreate.equalsIgnoreCase(XooDBConstants.USER)) {     //CREATE->USER
                sqlBean = parseCreateUserCommand(sqlQueryBuffer);
            } else {                                                            //CREATE->EXCEPTION
                XooDBSQLException xooDBSQLException = new XooDBSQLException();
                xooDBSQLException.setMessage("SQL Syntax Error : "
                        + "Invalid CREATE command; "
                        + "expected 'USER', TABLE' or 'DATABASE'");
                throw xooDBSQLException;
            }
        } else if(command.equalsIgnoreCase(XooDBConstants.USE)) {               //USE DATABASE
            sqlBean = parseUseDatabaseCommand(sqlQueryBuffer);
        } else if (command.equalsIgnoreCase(XooDBConstants.INSERT)) {           //INSERT
            sqlBean = parseInsertCommand(sqlQueryBuffer);
        } else if(command.equalsIgnoreCase(XooDBConstants.UPDATE)) {            //UPDATE
            sqlBean = parseUpdateCommand(sqlQueryBuffer);
        } else if(command.equalsIgnoreCase(XooDBConstants.DELETE)) {            //DELETE
            sqlBean = parseDeleteCommand(sqlQueryBuffer);
        } else if(command.equalsIgnoreCase(XooDBConstants.DROP)) {              //DROP
            String nextToDrop = nextToken(sqlQueryBuffer);
            if(nextToDrop.equalsIgnoreCase(XooDBConstants.DATABASE)) {          //DROP->DATABASE
                String databaseName = nextToken(sqlQueryBuffer);
                if(databaseName != null && !databaseName.equals("")) {
                    DatabaseBean databaseBean = new DatabaseBean();
                    databaseBean.setDatabaseName(databaseName);
                    databaseBean.setQueryType(XooDBConstants.DROP);
                    return databaseBean;
                } else {                                                        //DROP->DATABASE->EXCEPTION
                    XooDBSQLException xooDBSQLException = new XooDBSQLException();
                    xooDBSQLException.setMessage("SQL Syntax Error : Invalid DROP command");
                    throw xooDBSQLException;
                }
            } else if(nextToDrop.equalsIgnoreCase(XooDBConstants.TABLE)) {      //DROP->TABLE
                String tableName = nextToken(sqlQueryBuffer);
                if(tableName != null && !tableName.equals("")) {
                    TableBean tableBean = new TableBean();
                    tableBean.setTableName(tableName);
                    tableBean.setQueryType(XooDBConstants.DROP);
                    return tableBean;
                } else {                                                        //DROP->TABLE->EXCEPTION
                    XooDBSQLException xooDBSQLException = new XooDBSQLException();
                    xooDBSQLException.setMessage("SQL Syntax Error: expected <tablename>");
                    throw xooDBSQLException;
                }
            } else {
                XooDBSQLException xooDBSQLException = new XooDBSQLException();
                xooDBSQLException.setMessage("SQL Syntax Error: "
                        + "Invalid DROP command; "
                        + "expected 'TABLE' or 'DATABASE'");
                throw xooDBSQLException;
            }
        } else if(command.equalsIgnoreCase(XooDBConstants.SELECT)) {            //SELECT
            sqlBean = parseSelectQuery(sqlQueryBuffer);
        } else if(command.equalsIgnoreCase(XooDBConstants.SHOW)) {              //SHOW
            String nextToShow = nextToken(sqlQueryBuffer);
            if(nextToShow.equalsIgnoreCase(XooDBConstants.DATABASES)) {         //SHOW->DATABASES
                return new ShowDatabase();
            }
        } else {                                                                //EXCEPTION
            XooDBSQLException xooDBSQLException = new XooDBSQLException();
            xooDBSQLException.setMessage("SQL Error : Command \"" +
                    command + "\" is not valid");
            throw xooDBSQLException;
        }
        
        return sqlBean;
    }

    /**
     * @return the next token
     */
    private String nextToken(StringBuffer sqlQueryBuffer) {
        removeBeginningSpace(sqlQueryBuffer);
        String token = "";

        if (sqlQueryBuffer.indexOf(" ") != -1) {    //if not the last token
            token = sqlQueryBuffer.substring(0, sqlQueryBuffer.indexOf(" "));            
        } else {                                    //in case of last token
            token = sqlQueryBuffer.toString();
        }
        sqlQueryBuffer.delete(0, token.length()); //removes the current token from the string
        removeBeginningSpace(sqlQueryBuffer);

        System.out.println("token: " + token);
        if(XooDBConstants.COMMANDS.contains(token)) {
            return token.toUpperCase();
        } else {
            return token;
        }
    }

    /**
     *  removes the space at the beginning of the string
     *
     *  repeatedly checks whether there is white space at the beginning
     *  of the string and deletes the first character (i.e., the space)
     */
    private void removeBeginningSpace(StringBuffer sqlQueryBuffer) {
        while (sqlQueryBuffer.toString().startsWith(" ")) {
            sqlQueryBuffer = sqlQueryBuffer.deleteCharAt(0);
        }
    }

    /**
     *  removes the enclosing single quotes
     *
     *  checks whether the token is enclosed in single quotes
     *  and find the substring excluding the single quotes
     */
    private String removeSingleQuote(String token) throws XooDBSQLException {
        token = token.trim();
        if(token.startsWith("'")) {
            if(token.endsWith("'")) {
                StringBuffer tokenBuffer = new StringBuffer(token);
                tokenBuffer.deleteCharAt(0);
                tokenBuffer.deleteCharAt(tokenBuffer.length() - 1);
                token = tokenBuffer.toString();
            } else {
                XooDBSQLException xooDBSQLException = new XooDBSQLException();
                xooDBSQLException.setMessage("SQL Syntax Error");
                throw xooDBSQLException;
            }
        }
        return token;
    }

    /**
     * @return the instance of CreateDatabaseBean
     *
     * handles the CREATE DATABASE command
     */
    private CreateDatabaseBean parseCreateDatabaseCommand(StringBuffer sqlQueryBuffer) {
        CreateDatabaseBean createDatabaseBean = new CreateDatabaseBean();
        String databaseName = sqlQueryBuffer.toString().trim();
        createDatabaseBean.setDatabaseName(databaseName);
        return createDatabaseBean;
    }

    /**
     * @return the instance of TableBean
     *
     * handles the CREATE TABLE command
     */
    private TableBean parseCreateTableCommand(StringBuffer sqlQueryBuffer) throws XooDBSQLException {
        TableBean createTableBean = new TableBean();
        createTableBean.setTableName(nextToken(sqlQueryBuffer));
        if(sqlQueryBuffer.toString().startsWith("(")
                && sqlQueryBuffer.toString().endsWith(")")) {                   //checks for "(" and ")"
            sqlQueryBuffer.deleteCharAt(0);                                     //deletes "("
            sqlQueryBuffer.deleteCharAt(sqlQueryBuffer.length() - 1);           //deletes ")"
            String[] columnInfo = sqlQueryBuffer.toString().split(",");
            TableColumnBean[] tableColumnBeans = new TableColumnBean[columnInfo.length];
            for(int i = 0 ; i < columnInfo.length ; i++) {
                /**
                 * Each column-info constitutes a TableColumnBean
                 */
                tableColumnBeans[i] = getTableColumn(columnInfo[i]);
            }
            createTableBean.setColumns(tableColumnBeans);
        } else {                                                                //EXCEPTION
            XooDBSQLException xooDBSQLException = new XooDBSQLException();
            xooDBSQLException.setMessage("SQL Syntax Error : Expected column "
                    + "details enclosed in parantheses");
            throw xooDBSQLException;
        }
        return createTableBean;
    }

    /**
     * @return the instance of TableColumnBean
     *
     * that constitute info about a single column in the table
     */
    private TableColumnBean getTableColumn(String columnInfo) throws XooDBSQLException {
        TableColumnBean tableColumnBean = new TableColumnBean();
        StringBuffer columnInfoBuffer = new StringBuffer(columnInfo);
        removeBeginningSpace(columnInfoBuffer);
        String columnName = nextToken(columnInfoBuffer);                        //retrieving columnName
        tableColumnBean.setColumnName(columnName);
        removeBeginningSpace(columnInfoBuffer);
        String dataType = nextToken(columnInfoBuffer);
                                                                                //retrieving field length
        if(dataType.indexOf("(") != -1) {
            if(dataType.endsWith(")")) {
                /**
                 * field length is in between "(" and ")"
                 */
                try {
                    int fieldLength = Integer.parseInt(
                            dataType.substring(
                            dataType.indexOf("(") + 1 , dataType.indexOf(")")));
                    tableColumnBean.setSize(fieldLength);
                    /**
                     * extracting data type only excluding "(field-length)"
                     */
                    dataType = dataType.substring(0 , dataType.indexOf("("));
                } catch (NumberFormatException numExp) {                        //NumberFormatException
                    XooDBServerMain.appendExceptionLog(numExp);
                }
            } else {                                                            //EXCEPTION- missing ")"
                XooDBSQLException xooDBSQLException = new XooDBSQLException();
                xooDBSQLException.setMessage("SQL Syntax Error: Missing \")\"");
                throw xooDBSQLException;
            }
        }
                                                                                //validity of datatype
        if(XooDBConstants.DATA_TYPES.contains(dataType)) {
            tableColumnBean.setDataType(dataType);
        } else {                                                                //EXCEPTION-invalid datatype
            XooDBSQLException xooDBSQLException = new XooDBSQLException();
            xooDBSQLException.setMessage("SQL Error: Invalid datatype '" +
                    dataType + "' is found");
            throw xooDBSQLException;
        }
                                                                                //finding column constraints
        /**
         * checking whether the columnInfoBuffer is not empty
         *
         * each token is checked for finding constraints
         *
         * variables in tableColumnBean are set accordingly
         */
        while(!columnInfoBuffer.toString().equals("")) {
            removeBeginningSpace(columnInfoBuffer);
            String constraint = nextToken(columnInfoBuffer);
            if(constraint.equalsIgnoreCase(XooDBConstants.NOT) &&
                    nextToken(columnInfoBuffer).equalsIgnoreCase(
                    XooDBConstants.NULL)) {                                     //NOT NULL
                tableColumnBean.setNullAllowed(false);
            } else if(constraint.equalsIgnoreCase(XooDBConstants.NULL)) {       //NULL
                tableColumnBean.setNullAllowed(true);
            } else if(constraint.equalsIgnoreCase(
                    XooDBConstants.AUTOINCREMENT)) {                            //AUTOINCREMENT
                tableColumnBean.setAutoIncriment(true);
            } else if(constraint.equalsIgnoreCase(XooDBConstants.PRIMARY) &&
                    nextToken(columnInfoBuffer).equalsIgnoreCase(
                    XooDBConstants.KEY)) {                                      //PRIMARY KEY
                tableColumnBean.setPrivateKey(true);
            } else if(constraint.equalsIgnoreCase(XooDBConstants.DEFAULT)) {    //DEFAULT
                tableColumnBean.setDefaultValue(nextToken(columnInfoBuffer));
            } else {                                                            //EXCEPTION
                XooDBSQLException xooDBSQLException = new XooDBSQLException();
                xooDBSQLException.setMessage("SQL Error: Invalid constraint, "
                        + constraint + ", is found");
                throw xooDBSQLException;
            }
        }
        
        return tableColumnBean;
    }
    
    /**
     * @return the instance of UserBean
     *
     * handles the CREATE USER command
     */
    private UserBean parseCreateUserCommand(StringBuffer sqlQueryBuffer) throws XooDBSQLException {
        UserBean userBean = new UserBean();
        String userNameAndAccess = nextToken(sqlQueryBuffer);
        if(userNameAndAccess.indexOf("@") != -1) {                              //check for "@" symbol
            //user name before @ symbol
            String userName = userNameAndAccess.substring(
                    0 , userNameAndAccess.indexOf("@"));
            userName = removeSingleQuote(userName);
            userBean.setUsername(userName);
            //host name after @ symbol
            String hostName = removeSingleQuote(
                    userNameAndAccess.substring(
                    userNameAndAccess.indexOf("@") + 1));
            /**
             * checking whether remote access is permitted
             */
            if(hostName.equals("") || hostName == null) {
                XooDBSQLException xooDBSQLException = new XooDBSQLException();
                xooDBSQLException.setMessage("SQL Syntax Error: "
                        + "Expected '%' symbol or an IP address");
                throw xooDBSQLException;
            } else if(hostName.equals("%")) {
               userBean.setRemoteAccess(true);
            } else {
               userBean.setRemoteAccess(false);
            }
        } else {
            XooDBSQLException xooDBSQLException = new XooDBSQLException();
            xooDBSQLException.setMessage("SQL Syntax Error: Expected '@' symbol");
            throw xooDBSQLException;
        }
        /**
         * checking whether a password is provided in create user command
         */
        if(!sqlQueryBuffer.toString().equals("")) {
            
            if(nextToken(sqlQueryBuffer).equalsIgnoreCase(XooDBConstants.IDENTIFIED)) {//IDENTIFIED
                /**
                 * check for keyword 'IDENTIFIED'
                 */
                if(nextToken(sqlQueryBuffer).equalsIgnoreCase(XooDBConstants.BY)) { //BY
                    String password = removeSingleQuote(nextToken(sqlQueryBuffer)); //password
                    userBean.setPassword(password);
                } else {
                    XooDBSQLException xooDBSQLException = new XooDBSQLException();
                    xooDBSQLException.setMessage("SQL Syntax Error: "
                            + "Expected keyword 'BY'");
                    throw xooDBSQLException;
                }
            } else {                                                            //EXCEPTION
                XooDBSQLException xooDBSQLException = new XooDBSQLException();
                xooDBSQLException.setMessage("SQL Syntax Error: "
                        + "Expected keyword 'IDENTIFIED'");
                throw xooDBSQLException;
            }
        }
        return userBean;
    }

    /**
     * @return the instance of InsertStatementBean
     *
     * handles the INSERT command
     */
    private InsertStatementBean parseInsertCommand(StringBuffer sqlQueryBuffer) throws XooDBSQLException {
        InsertStatementBean insertStatementBean = new InsertStatementBean();
        String tableName = "";
        removeBeginningSpace(sqlQueryBuffer);
        String nextToInsert = nextToken(sqlQueryBuffer);
        if(nextToInsert.equalsIgnoreCase(XooDBConstants.INTO)) {                //INSERT->INTO
            Pattern valuesPattern = Pattern.compile(XooDBConstants.VALUES, Pattern.CASE_INSENSITIVE);
            Matcher valuesMatcher = valuesPattern.matcher(
                    sqlQueryBuffer.toString());
                                                                                //retrieving tableName
            if(valuesMatcher.find()) {                                          //matches "VALUES"
                /**
                 * tableName is just before the keyword "VALUES"
                 */
                tableName = sqlQueryBuffer.substring(
                        0, valuesMatcher.start()).trim();

                if(tableName.indexOf("(") != -1) {                    
                    String columnPart = tableName.substring(tableName.indexOf("("));
                    tableName = tableName.substring(0, tableName.indexOf("(")).trim();
                    checkColumnsInInsert(columnPart);
                }

                /**
                 * deleting the traversed parts
                 * including the keyword "VALUES"
                 */
                sqlQueryBuffer.delete(0, valuesMatcher.end());
                removeBeginningSpace(sqlQueryBuffer);
            } else {                                                            //EXCEPTION in matcher
                XooDBSQLException xooDBSQLException = new XooDBSQLException();
                xooDBSQLException.setMessage("SQL Syntax Error : Expected "
                        + "keyword 'VALUES'");
                throw xooDBSQLException;
            }
            insertStatementBean.setTableName(tableName);            
            if(sqlQueryBuffer.toString().startsWith("(") &&                     //retrieving values
                    sqlQueryBuffer.toString().endsWith(")")) {
                sqlQueryBuffer.deleteCharAt(0);                                 //deletes "("
                sqlQueryBuffer.deleteCharAt(sqlQueryBuffer.length() - 1);       //deletes ")"
                String[] colValues = sqlQueryBuffer.toString().
                        replaceAll("'", "").split(",");
                for(int i = 0 ; i < colValues.length ; i++) {
                    if(colValues[i].equals("") || colValues[i] == null) {       //checking for null
                        XooDBSQLException xooDBSQLException = new XooDBSQLException();
                        xooDBSQLException.setMessage("One or more values given is null");
                        throw xooDBSQLException;
                    }
                    colValues[i] = colValues[i].trim();
                }
                insertStatementBean.setValues(colValues);
            } else {                                                            //EXCEPTION while retrieving values
                XooDBSQLException xooDBSQLException = new XooDBSQLException();
                xooDBSQLException.setMessage("SQL Syntax Error : Expected values"
                        + " enclosed in parantheses");
                throw xooDBSQLException;
            }
        } else {                                                                //INSERT-EXCEPTION
            XooDBSQLException xooDBSQLException = new XooDBSQLException();
            xooDBSQLException.setMessage("SQL Syntax Error :"
                    + " Expected keyword \"INTO\"");
            throw xooDBSQLException;
        }
        return insertStatementBean;
    }

    /**
     * @return the instance of UpdateStementBean
     *
     * handles the UPDATE command
     */
    private UpdateStatementBean parseUpdateCommand(StringBuffer sqlQueryBuffer) throws XooDBSQLException {
        UpdateStatementBean updateStatementBean = new UpdateStatementBean();
        removeBeginningSpace(sqlQueryBuffer);
        String tableName = nextToken(sqlQueryBuffer);
        updateStatementBean.setTableName(tableName);
        removeBeginningSpace(sqlQueryBuffer);
        String nextToTableName = nextToken(sqlQueryBuffer);
        if(nextToTableName.equalsIgnoreCase(XooDBConstants.SET)) {
            removeBeginningSpace(sqlQueryBuffer);
            Pattern wherePattern = Pattern.compile(XooDBConstants.WHERE, Pattern.CASE_INSENSITIVE);       //pattern for WHERE
            String[] queryPart = wherePattern.split(sqlQueryBuffer.toString()); //splitting with WHERE            
            /**
             * if there is keyword "WHERE" we get two parts
             *
             * -> part describing what to be SET
             * -> WHERE condition part
             *
             * otherwise only the first part
             */
            if(queryPart.length == 2 && !queryPart[0].equals("")
                    && !queryPart[1].equals("")) {                              
                updateStatementBean.setColumnValues(
                        getColumnAndValues(queryPart[0]));
                updateStatementBean.setConditions(
                        getConditions(queryPart[1]));
            } else if(queryPart.length == 1 && !queryPart[0].equals("")) {
                updateStatementBean.setColumnValues(
                        getColumnAndValues(queryPart[0]));
            } else {                                                            //EXCEPTION
                XooDBSQLException xooDBSQLException = new XooDBSQLException();
                xooDBSQLException.setMessage("SQL Syntax Error");
                throw xooDBSQLException;
            }
        } else {
            XooDBSQLException xooDBSQLException = new XooDBSQLException();
            xooDBSQLException.setMessage("SQL Syntax Error : Expected "
                    + "keyword \"SET\"");
            throw xooDBSQLException;
        }
        return updateStatementBean;
    }

    /**
     * @return the instance of LinkedHashMap containing
     * each pair of
     * -> columnName
     * -> value
     *
     * handles the SET part of UPDATE statement
     */
    private LinkedHashMap getColumnAndValues(String setPartOfQuery) throws XooDBSQLException {
        LinkedHashMap columnAndValues = new LinkedHashMap();
        /*splitting each colunmName=value*/
        String[] columns = setPartOfQuery.trim().split(",");
        for(String column : columns) {
            /*splitting columnName and value*/
            String[] columnValues = column.trim().split("=");
            //System.out.println(columnValues[0] + "," + columnValues[1]);
            if(columnValues.length == 2) {
                /**
                 * LinkedHashMap with
                 * key part   - columnName
                 * value part - value
                 */
                columnAndValues.put(columnValues[0].trim(), columnValues[1].trim());
            } else {                                                            //EXCEPTION
                XooDBSQLException xooDBSQLException = new XooDBSQLException();
                xooDBSQLException.setMessage("SQL Syntax Error");
                throw xooDBSQLException;
            }
        }
        return columnAndValues;
    }

    /**
     * @return the instance of DeleteStatementBean
     *
     * handles the DELETE command
     */
    private DeleteStatementBean parseDeleteCommand(StringBuffer sqlQueryBuffer) throws XooDBSQLException {
        DeleteStatementBean deleteStatementBean = new DeleteStatementBean();
        String nextToDelete = nextToken(sqlQueryBuffer);
        if(nextToDelete.equalsIgnoreCase(XooDBConstants.FROM)) {                //DELETE->FROM
            removeBeginningSpace(sqlQueryBuffer);
            String tableName = nextToken(sqlQueryBuffer);
            deleteStatementBean.setTableName(tableName);
            removeBeginningSpace(sqlQueryBuffer);
            String token = nextToken(sqlQueryBuffer);
            if(token.equalsIgnoreCase(XooDBConstants.WHERE)) {                  //WHERE
                removeBeginningSpace(sqlQueryBuffer);
                deleteStatementBean.setConditions(
                        getConditions(sqlQueryBuffer.toString()));
            } else if(!token.equals("")){                                       //EXCEPTION
                XooDBSQLException xooDBSQLException = new XooDBSQLException();
                xooDBSQLException.setMessage("SQL Syntax Error : Unexpected "
                        + "token found");
                throw xooDBSQLException;
            }
        } else {                                                                //EXCEPTION
            XooDBSQLException xooDBSQLException = new XooDBSQLException();
            xooDBSQLException.setMessage("SQL Syntax Error : Expected "
                    + "keyword \"FROM\"");
            throw xooDBSQLException;
        }
        return deleteStatementBean;
    }

    /**
     * @return the instance of LinkedList
     *
     * handles the condition part of commands
     */
    private LinkedList getConditions(String queryConditionPart)
            throws XooDBSQLException {
        LinkedList conditionsList = new LinkedList();
        Pattern andPattern = Pattern.compile("and"); // pattern "and"
        Pattern orPattern = Pattern.compile("or"); // pattern "or"
        /**
         * splitting conditions as per the occurrence of keyword AND
         * in the condition part of query
         */
        String[] andConditions = andPattern.split(queryConditionPart);
        for(int i = 0 ; i < andConditions.length ; i++) {
            /**
             * splitting conditions as per the occurrence of keyword OR
             * with in the previously splitted part of query
             */
            String[] orConditions = orPattern.split(andConditions[i]);
            for(int j = 0 ; j < orConditions.length ; j++) {
                conditionsList.add(orConditions[j]);
                if( (j+1) < orConditions.length ) {
                    /**
                     * if there is remaining conditions
                     * 
                     * adding "or" between each pair of conditions
                     */
                    conditionsList.add("or");
                }
            }
            if( (i+1) < andConditions.length ) {
                /**
                 * if there is remaining conditions
                 *
                 * adding "and" between each pair of conditions
                 */
                conditionsList.add("and");
            }
        }

        for(int i = 0 ; i < conditionsList.size() ; i++) {
            parseCondition(conditionsList, i);
            i++; //skipping keywords "and" and "or"
        }

        return conditionsList;
    }

    /**
     * splits the condition into
     * ->columnName
     * ->operator
     * ->value
     * in ConditionBean
     */
    private void parseCondition(LinkedList conditionsList, int index) throws XooDBSQLException {
        String condition = (String) conditionsList.get(index);
        ConditionBean conditionBean = new ConditionBean();
        if(condition.indexOf("!=") != -1) {                                     //!=
            conditionBean.setColumnName(
                    condition.substring(0, condition.indexOf("!=")).trim());
            conditionBean.setOperator("!=");
            conditionBean.setValue(
                    condition.substring(condition.indexOf("!=") + 1).
                    replaceAll("'", "").
                    trim());
        } else if(condition.indexOf("=") != -1) {                               //=
            conditionBean.setColumnName(
                    condition.substring(0, condition.indexOf("=")).trim());
            conditionBean.setOperator("=");
            conditionBean.setValue(
                    condition.substring(condition.indexOf("=") + 1).
                    replaceAll("'", "").
                    trim());
        } else if(condition.indexOf("<") != -1) {                               //<
            conditionBean.setColumnName(
                    condition.substring(0, condition.indexOf("<")).trim());
            conditionBean.setOperator("<");
            conditionBean.setValue(
                    condition.substring(condition.indexOf("<") + 1).
                    replaceAll("'", "").
                    trim());
        } else if(condition.indexOf(">") != -1) {                               //>
            conditionBean.setColumnName(
                    condition.substring(0, condition.indexOf(">")).trim());
            conditionBean.setOperator(">");
            conditionBean.setValue(
                    condition.substring(condition.indexOf(">") + 1).
                    replaceAll("'", "").
                    trim());
        } else if(condition.indexOf("<=") != -1) {                              //<=
            conditionBean.setColumnName(
                    condition.substring(0, condition.indexOf("<=")).trim());
            conditionBean.setOperator("<=");
            conditionBean.setValue(
                    condition.substring(condition.indexOf("<=") + 1).
                    replaceAll("'", "").
                    trim());
        } else if(condition.indexOf(">=") != -1) {                              //>=
            conditionBean.setColumnName(
                    condition.substring(0, condition.indexOf(">=")).trim());
            conditionBean.setOperator(">=");
            conditionBean.setValue(
                    condition.substring(condition.indexOf(">=") + 1).
                    replaceAll("'", "").
                    trim());
        } else {                                                                //EXCEPTION
            XooDBSQLException xooDBSQLException = new XooDBSQLException();
            xooDBSQLException.setMessage("SQL Syntax Error : "
                    + "Invalid operator found");
            throw xooDBSQLException;
        }
        
        /**
         * replace the condition part by conditionBean
         */
        conditionsList.set(index, conditionBean);
    }
    
    /**
     * @return the instance of DatabaseBean
     *
     * handles the USE databaseName command
     */
    private DatabaseBean parseUseDatabaseCommand(StringBuffer sqlQueryBuffer) {
        DatabaseBean databaseBean = new DatabaseBean();
        String databaseName = sqlQueryBuffer.toString().trim();
        databaseBean.setDatabaseName(databaseName);
        return databaseBean;
    }

    /**
     * @return the instance of SelectQueryBean
     *
     * handles the SELECT command
     */
    private SelectQueryBean parseSelectQuery(StringBuffer sqlQueryBuffer) throws XooDBSQLException, ParserConfigurationException, SAXException, IOException {
        SelectQueryBean selectQueryBean = new SelectQueryBean();
        sqlQueryBuffer = new StringBuffer(
                sqlQueryBuffer.toString().replaceFirst("from", "FROM"));
        if(sqlQueryBuffer.indexOf("FROM") != -1) {                              //FROM
            removeBeginningSpace(sqlQueryBuffer);
            /**
             * extracting the column names
             * between keywords SELECT and FROM
             *
             * SELECT already removed in previous step
             * hence start index is given as 0 rather than indexOf("SELECT" + 6)
             */
            String fieldNamesString = sqlQueryBuffer.substring(
                    0, sqlQueryBuffer.indexOf("FROM"));
            /**
             * deleting the beginning portion including the keyword "FROM"
             */
            sqlQueryBuffer.delete(0, sqlQueryBuffer.indexOf("FROM") + 4);
            removeBeginningSpace(sqlQueryBuffer);
            String tableName = nextToken(sqlQueryBuffer);
            selectQueryBean.setTableName(tableName);
            if(fieldNamesString.trim().equalsIgnoreCase("*")) {
                DatabaseXMLManager dbXMLManager = new DatabaseXMLManager(
                        xooDBEngine);                
                TableBean tableBean = dbXMLManager.getTableMetaData(
                        xooDBEngine.getUserName(), tableName);
                if (tableBean != null) {
                    selectQueryBean.setColumnNames(
                            utilities.getColumnNames(tableBean.getColumns()));
                } else {
                    XooDBSQLException xooDBSQLException = new XooDBSQLException();
                    xooDBSQLException.setMessage(
                            "SQL Error: Table '"
                            + tableName
                            + "' does not exist");
                    throw xooDBSQLException;
                }
            } else {
                String[] columnNames = null;
                /**
                 * splitting the fieldNamesString into columnNames
                 * as per the occurrence of comma (",")
                 */
                if(fieldNamesString.indexOf(",") != -1) {
                    columnNames = fieldNamesString.split(",");
                } else {
                    columnNames = new String[] {fieldNamesString};
                }

                selectQueryBean.setColumnNames(columnNames);
            }
            removeBeginningSpace(sqlQueryBuffer);
            String nextToTableName = nextToken(sqlQueryBuffer);
            if(nextToTableName.equalsIgnoreCase(XooDBConstants.WHERE)) {        //WHERE
                removeBeginningSpace(sqlQueryBuffer);
                selectQueryBean.setConditions(getConditions(sqlQueryBuffer.toString()));
            } else if(!nextToTableName.equals("")) {                            //WHERE->EXCEPTION
                XooDBSQLException xooDBSQLException = new XooDBSQLException();
                xooDBSQLException.setMessage("SQL Syntax Error : Unexpected "
                        + "token found ");
                throw xooDBSQLException;
            }
            return selectQueryBean;
        } else {                                                                //SELECT-EXCEPTION
            XooDBSQLException xooDBSQLException = new XooDBSQLException();
            xooDBSQLException.setMessage("SQL Syntax Error : Expected "
                    + "keyword \"FROM\"");
            throw xooDBSQLException;
        }        
    }

    private void checkColumnsInInsert(String columnPart) {
        
    }
}
