/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dbfront.utilities;

import com.xoodb.beans.TableColumnBean;
import java.util.Vector;

/**
 * 
 * @author dhanoopbhaskar
 */
public class Utility {
    /**
     * 
     * @param dataVector
     * @param tableName
     * @return
     */
    public String createTableCreationStatement(Vector dataVector, String tableName) {
        if (dataVector != null && dataVector.size() > 0) {
            /**
             * we have to build a CREATE TABLE statement from the info received
             * from the XooDBFront
             * StringBuilder is used for that
             */
            StringBuilder stmntStrBuilder = new StringBuilder();
            stmntStrBuilder.append(("CREATE TABLE " + tableName + " ("));
            /**
             * Each instance of Vector contains info about a single column
             */
            for (int i = 0; i < dataVector.size(); i++) {
                Vector row = (Vector) dataVector.get(i);
                /**
                 * At index 0 - columnName
                 * At index 1 - columnDataType
                 * At index 2 - columnSize
                 * At index 3 - null or not null
                 * At index 4 - primary key
                 */
                stmntStrBuilder.append((row.get(0) + " "));           
                stmntStrBuilder.append((row.get(1) + ""));            
                stmntStrBuilder.append(("(" + row.get(2) + ") "));    

                Boolean nullAllowd = (Boolean) row.get(3);
                if (nullAllowd) {
                    stmntStrBuilder.append("NULL ");                  
                } else {
                    stmntStrBuilder.append("NOT NULL ");              
                }

                Boolean primaryKey = (Boolean) row.get(4);
                if (primaryKey) {
                    stmntStrBuilder.append("PRIMARY KEY");            
                }
                /**
                 * if not the last column
                 * -append a comma
                 */
                if (i != (dataVector.size() - 1)) {
                    stmntStrBuilder.append(",");
                }
            }
            stmntStrBuilder.append(")");
            return stmntStrBuilder.toString();
        } else {
            return null;
        }
    }

    /**
     * 
     * @param rowData
     * @param tableName
     * @return
     */
    public String createInsertTableStatement(Vector rowData, String tableName) {
        if (rowData != null && rowData.size() > 0 && tableName != null) {
            /**
             * we have to build a CREATE TABLE statement from the info received
             * from the XooDBFront
             * StringBuilder is used for that
             */
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(("INSERT INTO " + tableName + " VALUES("));
            for (int i = 0; i < rowData.size(); i++) {
                stringBuilder.append(("'" + rowData.get(i) + "'"));
                /**
                 * if not the last column value
                 * -append a comma
                 */
                if (i != rowData.size() - 1) {
                    stringBuilder.append(",");
                }
            }
            stringBuilder.append(")");
            return stringBuilder.toString();
        } else {
            return null;
        }
    }

    /**
     * 
     * @param columns
     * @return
     */
    public Object[] getColumnNames(TableColumnBean[] columnBean) {
        String[] columanNames = new String[columnBean.length];
        /**
         * Traverse each instance of TableColumnBean from the array
         * 
         * Use the method getColumnName() to get the columnName from the
         * instance of TableColumnBean
         */
        for (int i = 0; i < columnBean.length; i++) {
            columanNames[i] = columnBean[i].getColumnName();
        }
        return columanNames;
    }
}
