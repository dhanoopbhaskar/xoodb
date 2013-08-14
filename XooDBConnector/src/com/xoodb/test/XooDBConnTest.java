/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.xoodb.test;

import com.xoodb.connector.XooDBConnector;
import com.xoodb.exception.XooDBSQLException;
import com.xoodb.query.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dhanoopbhaskar
 */
public class XooDBConnTest {
    XooDBConnector xooDBConnector = null;

    public XooDBConnTest() throws XooDBSQLException {
        xooDBConnector = new XooDBConnector("127.0.0.1", 9999, "db4", "root", "root");
        xooDBConnector.connectToXooDB();
        test();
        System.exit(0);
    }    

    private void test() {
        String query = "select * from tab3";
        ResultSet resultSet = null;
        try {
            resultSet = xooDBConnector.executeQuery(query);
        } catch (XooDBSQLException ex) {
            Logger.getLogger(XooDBConnTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        while(resultSet.next()) {
            for(int i = 0 ; i < resultSet.getColumnCount() ; i++) {
                System.out.println(resultSet.getString(i));
            }
        }

        //query = "create table tab1(rollno int(10) primary key, name varchar(20))";
        query = "insert into tab1 values(1,rtr)";
        try {
            xooDBConnector.executeUpdate(query);
            //        query = "select * from tab2";
            //        resultSet = xooDBConnector.executeQuery(query);
            //
            //        while(resultSet.next()) {
            //            for(int i = 0 ; i < resultSet.getColumnCount() ; i++) {
            //                System.out.println(resultSet.getString(i));
            //            }
            //        }
            //        while(resultSet.next()) {
            ////            for(int i = 0 ; i < resultSet.getColumnCount() ; i++) {
            //                System.out.println(resultSet.getString("rollno"));
            ////            }
            //        }
        } catch (XooDBSQLException ex) {
            Logger.getLogger(XooDBConnTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
