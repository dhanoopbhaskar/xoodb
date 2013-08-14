/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.xoodb.xml;

import com.xoodb.XooDBSQLException;
import com.xoodb.beans.UserBean;
import com.xoodb.constants.XooDBVariables;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Vector;
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
public class UserXMLManager {
    XooDBXMLManager xooDBXMLManager = null;

    public UserXMLManager() throws ParserConfigurationException,
            SAXException, IOException {
        xooDBXMLManager = new XooDBXMLManager("db/XoodbMeta.xml", "db/XoodbMeta.xsd");
    }

    /**
     * 
     * @param userBean
     * @return
     * @throws XooDBSQLException
     * @throws TransformerConfigurationException
     * @throws TransformerException
     * @throws XMLStreamException
     * @throws FileNotFoundException
     */
    public boolean createUser(UserBean userBean) throws XooDBSQLException,
            TransformerConfigurationException,
            TransformerException,
            XMLStreamException,
            FileNotFoundException {
        /**
         * get the details of user with userName (received from the UserBean)
         *
         * if its null
         * -user does not exist
         * else
         * -user already exists and Exception is thrown
         */
        if (getUserInfo(userBean.getUsername()) != null) {
            XooDBSQLException xooDBSQLException = new XooDBSQLException();
            xooDBSQLException.setMessage("User '" + userBean.getUsername()
                    + "' Already exists");
            throw xooDBSQLException;
        }
        /**
         * Setting schema file path for the user
         */
        String schemaFilePath = XooDBVariables.dbDir + File.separator
                + userBean.getUsername() + File.separator + userBean.getUsername() + ".xml";
        /**
         * Adding new node (<user>) for the user to <users>
         *
         * <user>
         * <user-name>userName</user-name>
         * <password>password</password>
         * <schema-file-name>schemaFilePath</schema-file-name>
         * </user>
         */
        Node newNode = xooDBXMLManager.addNewElementToRoot("user");
        xooDBXMLManager.addChildNode(newNode, "user-name", userBean.getUsername());
        xooDBXMLManager.addChildNode(newNode, "password", userBean.getPassword());
        xooDBXMLManager.addChildNode(newNode, "schema-file-name", schemaFilePath);
        /**
         * setting the schema file name to the UserBean
         */
        userBean.setSchemaFileName(schemaFilePath);
        xooDBXMLManager.commitXML();
        /**
         * creating database schema for the user - may not be required
         */
        DatabaseSchemaXMLManager dbSchemaXMLManager = new DatabaseSchemaXMLManager();
        File xmlSchemaFile = dbSchemaXMLManager.createXmlSchemaForDbSchema(userBean);
        dbSchemaXMLManager.createDatabaseSchema(userBean, xmlSchemaFile.getName());
        return true;
    }

    /**
     *
     * @param username
     * @param password
     * @return
     */
    public boolean loginUser(String userName, String password) throws XooDBSQLException {
        UserBean userBean = getUserInfo(userName);
        if (userBean != null && userBean.getUsername().equals(userName)
                && userBean.getPassword().equals(password)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     *
     * @param username
     * @return
     */
    public UserBean getUserInfo(String userName) throws XooDBSQLException {
        /**
         * retrieve all the <user> nodes into a NodeList
         */
        NodeList nodeList = xooDBXMLManager.getElements("user");
        /**
         * returns details of user with user-name userName as UserBean
         */
        for (int i = 0; nodeList != null && i < nodeList.getLength(); i++) {
            UserBean userBean = getUser((Element) nodeList.item(i));
            if (userBean.getUsername().equals(userName)) {
                return userBean;
            }
        }
        return null;
    }

    /**
     *
     * @return
     */
    public Vector getAllUsers() throws XooDBSQLException {
        Vector users = new Vector();
        NodeList nodeList = xooDBXMLManager.getElements("user");
        for (int i = 0; nodeList != null && i < nodeList.getLength(); i++) {
            users.addElement(getUser((Element) nodeList.item(i)));
        }
        return users;
    }

    /**
     * 
     * @param userElement
     * @return
     */
    public UserBean getUser(Element userElement) throws XooDBSQLException {
        UserBean userBean = new UserBean();
        /**
         * extracts the values of <user-name>, <password> and <schema-file-name>
         * from the
         * <user> node
         * and create a UserBean with the values extracted
         */
        try {
            userBean.setUsername(xooDBXMLManager.getElementValue(userElement, "user-name"));
            userBean.setPassword(xooDBXMLManager.getElementValue(userElement, "password"));
            //userBean.setSchemaFileName(xooDBXMLManager.getElementValue(userElement, "schema-file-name"));
            userBean.setSchemaFileName(getRelativePathOfUserSchemaFile(userBean.getUsername()));
        } catch(NullPointerException nullExp) {
            XooDBSQLException xooDBSQLException = new XooDBSQLException();
            xooDBSQLException.setMessage("User '" + userBean.getUsername()
                    + "' does not exist");
            throw xooDBSQLException;
        }
        return userBean;
    }

    private String getRelativePathOfUserSchemaFile(String userName) {
        return XooDBVariables.dbDir +
                File.separator +
                userName +
                File.separator +
                userName +".xml";
    }
}
