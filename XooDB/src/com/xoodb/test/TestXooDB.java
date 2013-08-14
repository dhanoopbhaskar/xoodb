/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.xoodb.test;

import com.xoodb.XooDBEngine;
import com.xoodb.XooDBSQLException;
import com.xoodb.beans.DatabaseBean;
import com.xoodb.beans.ResultBean;
import com.xoodb.beans.TableBean;
import com.xoodb.beans.TableColumnBean;
import com.xoodb.beans.UserBean;
import com.xoodb.constants.XooDBVariables;
import com.xoodb.main.XooDBServerMain;
import com.xoodb.utilities.Utilities;
import com.xoodb.xml.UserXMLManager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.xml.sax.SAXException;

/**
 *
 * @author dhanoopbhaskar
 */
public class TestXooDB {
    public static void main(String[] args) throws XMLStreamException {
        String[] abc = null;
        String abc1 = null;
        try {
            //        try {
            //            XooDbParser dbParser = new XooDbParser();
            //            InsertStatementBean statementBean =
            //                    (InsertStatementBean) dbParser.parseSQLQuery(
            //                    "INSERT INTO table_name VALUES(value1,value2,value3)");
            //            System.out.println("==============================================");
            //            System.out.println("Table Name: " + statementBean.getTableName());
            //            System.out.println("------ Columns -------");
            //            for (int i = 0; statementBean.getColumnNames() != null
            //                    && i < statementBean.getColumnNames().length; i++) {
            //                System.out.println((i + 1) + ":" + statementBean.getColumnNames()[i]);
            //            }
            //            System.out.println("------ Values -------");
            //            for (int i = 0; statementBean.getValues() != null
            //                    && i < statementBean.getValues().length; i++) {
            //                System.out.println((i + 1) + ":" + statementBean.getValues()[i]);
            //            }
            //            // Test Create Table
            //            CreatTableBean creatTableBean =
            //                    (CreatTableBean) dbParser.parseSQLQuery(
            //                    "CREATE TABLE tableName (column1 int(10) NOT NULL PRIMARY KEY," +
            //                    "column2 VARCHAR(50) NOT NULL, column3 CHAR NULL," +
            //                    "column4 VARCHAR(100)");
            //        } catch (XooDBSQLException ex) {
            //            Logger.getLogger(TestXooDB.class.getName()).log(Level.SEVERE, null, ex);
            //        }
            XooDBEngine xooDBEngine = new XooDBEngine();
            //xooDBEngine.executeUpdate("CREATE USER 'dhanoop'@'%' IDENTIFIED BY 'xoodb'");
            if(xooDBEngine.login("dhanoop","xoodb")){
                
                /*xooDBEngine.setUserBean((UserBean) new UserXMLManager().getAllUsers().elementAt(1));
                xooDBEngine.getUserBean().setSchemaFileName(
                        "K:\\NETBEANS\\PROJECTS\\XooDB\\db\\"
                        + "dhanoop\\my_database\\tableName2.xml");*/
                //xooDBEngine.executeUpdate("CREATE DATABASE my_database");

//                ResultBean resultBean = xooDBEngine.executeQuery("SHOW DATABASES");
//                ArrayList resultArrayList = resultBean.getData();
//                for(int i = 0 ; i < resultArrayList.size() ; i++) {
//                    DatabaseBean databaseBean = (DatabaseBean) resultArrayList.get(i);
//                    System.out.println("Database #" + (i+1) + " " + databaseBean.getDatabaseName());
//                    ArrayList tableArrayList = databaseBean.getTables();
//                    for(int j = 0 ; j < tableArrayList.size() ; j++) {
//                        TableBean tableBean = (TableBean) tableArrayList.get(j);
//                        System.out.println("Table #" + (j+1) + " " + tableBean.getTableName());
//                        TableColumnBean[] tableColumnBeans = tableBean.getColumns();
//                        for(TableColumnBean tableColumnBean : tableColumnBeans) {
//                            System.out.println("- " + tableColumnBean.getColumnName());
//                        }
//                    }
//                }
                
                xooDBEngine.executeUpdate("USE my_database");
                /*xooDBEngine.executeUpdate("CREATE TABLE tableName2 (column1 int(10) NOT NULL PRIMARY KEY," +
                                "column2 VARCHAR(50) NOT NULL, column3 CHAR NULL," +
                                "column4 VARCHAR(100))");*/
                //xooDBEngine.executeUpdate("update tableName set column2=def where column1=1");
                //xooDBEngine.executeUpdate("insert into tableName2 values(2,'xyz','b','pqr')");
                /*xooDBEngine.executeUpdate("INSERT INTO tableName (column1, " +
                        "column2, column3, column4) VALUES (4,'abc', 'a', 'cdf')");*/
                //xooDBEngine.executeUpdate("delete from tableName2 where column1=2");
                //xooDBEngine.executeUpdate("drop table tableName");


                ResultBean resultBean = xooDBEngine.executeQuery("select * from tableName2");
                String[] columnNames = resultBean.getTableColumnNames();
                String[][] tableData = resultBean.getTableData();
                for(int i = 0 ; i < columnNames.length ; i++) {
                    System.out.print(columnNames[i] + " ");
                }
                System.out.print("\n");
                for(int i = 0 ; i < tableData.length ; i++) {
                    for(int j = 0 ; j < columnNames.length ; j++) {
                        System.out.print(tableData[i][j] + "       ");
                    }
                    System.out.print("\n");
                }
            }


//            UserXMLManager userXMLManager = new UserXMLManager();
//            Vector users = userXMLManager.getAllUsers();
//
//            System.out.println("\n");
//            for (int i = 0; i < users.size(); i++) {
//                UserBean userBean = (UserBean) users.elementAt(i);
//                System.out.println("User Name: "+ userBean.getUsername());
//                System.out.println("Password: "+ userBean.getPassword());
//                System.out.println("Schema File Name: "+ userBean.getSchemaFileName());
//                System.out.println("");
//            }

            
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(TestXooDB.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ex);
            XooDBServerMain.appendExceptionLog(ex);
        } catch (TransformerException ex) {
            Logger.getLogger(TestXooDB.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ex);
            XooDBServerMain.appendExceptionLog(ex);
        } catch (XooDBSQLException ex) {
            Logger.getLogger(TestXooDB.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ex);
            XooDBServerMain.appendExceptionLog(ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(TestXooDB.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ex);
            XooDBServerMain.appendExceptionLog(ex);
        } catch (SAXException ex) {
            Logger.getLogger(TestXooDB.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ex);
            XooDBServerMain.appendExceptionLog(ex);
        } catch (IOException ex) {
            Logger.getLogger(TestXooDB.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ex);
            XooDBServerMain.appendExceptionLog(ex);
        }
    }
}
