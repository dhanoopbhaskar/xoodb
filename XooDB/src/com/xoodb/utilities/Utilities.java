/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.xoodb.utilities;

import com.xoodb.XooDBEngine;
import com.xoodb.XooDBSQLException;
import com.xoodb.beans.TableColumnBean;
import com.xoodb.beans.UserBean;
import com.xoodb.constants.XooDBConstants;
import com.xoodb.xml.UserXMLManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Properties;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 *
 * @author dhanoopbhaskar
 */
public class Utilities {

    /**
     * 
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public int getPortNumber() throws FileNotFoundException, IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(
                new File(XooDBConstants.DB_CONF_FILE)));
        return Integer.parseInt(properties.getProperty("db.server.port"));
    }

    /**
     * 
     * @param msg
     */
    public void printDebug(String msg) {
        if (XooDBConstants.DEBUG) {
            System.out.println(msg);
        }
    }

    /**
     * 
     * @param value
     * @return
     */
    public boolean isNumber(String value) {
        char[] chars = value.replaceAll("'", "").toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (Character.isDigit(chars[i])) {
                continue;
            } else {
                return false;
            }
        }
        return true;
    }

    /**
     * 
     * @param value
     * @return
     */
    public boolean isDate(String value) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                XooDBConstants.DATE_PATTERN);
        try {
            dateFormat.parse(value);
            return true;
        } catch (ParseException ex) {
            return false;
        }
    }

    /**
     * 
     * @param value
     * @return
     */
    public boolean isDateTime(String value) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                XooDBConstants.DATETIME_PATTERN);
        try {
            dateFormat.parse(value);
            return true;
        } catch (ParseException ex) {
            return false;
        }
    }

    /**
     * 
     * @param file
     * @return
     */
    public File createTableXmlFileDir(File file) {
        File dir = file.getParentFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return file;
    }

    /**
     * 
     * @param columnBeans
     * @return
     */
    public String[] getColumnNames(TableColumnBean[] columnBeans) {
        String[] columNames = new String[columnBeans.length];
        for (int i = 0; i < columnBeans.length; i++) {
            columNames[i] = columnBeans[i].getColumnName();
        }
        return columNames;
    }

    /**
     * 
     * @param xooDBEngine
     * @param tableName
     * @return
     */
    public String getTableDataXMLFile(XooDBEngine xooDBEngine,
            String tableName) {
        return (xooDBEngine.getUserBean().getUserDir() + File.separator
                + xooDBEngine.getDatabaseName() + File.separator
                + tableName + ".xml");
    }

    /**
     * 
     * @param columnBeans
     * @param updateColumName
     * @return
     */
    public TableColumnBean getColumnBean(TableColumnBean[] columnBeans,
            String updateColumnName) {
        for (TableColumnBean columnBean : columnBeans) {
            if (columnBean.getColumnName().equalsIgnoreCase(updateColumnName)) {
                return columnBean;
            }
        }
        return null;
    }

    /**
     * 
     * @param userName
     * @return
     * @throws ParserConfigurationException
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    public UserBean getUserBean(String userName) throws
            ParserConfigurationException, ParserConfigurationException,
            IOException, SAXException, XooDBSQLException {
        UserBean userBean = new UserXMLManager().getUserInfo(userName);
        return userBean;
    }
}
