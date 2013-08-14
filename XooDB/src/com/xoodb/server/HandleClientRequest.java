/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.xoodb.server;

import com.xoodb.XooDBEngine;
import com.xoodb.XooDBSQLException;
import com.xoodb.beans.QueryBean;
import com.xoodb.constants.XooDBConstants;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.xml.sax.SAXException;

/**
 *
 * @author dhanoopbhaskar
 */
public class HandleClientRequest {
    XooDBEngine xooDBEngine = null;

    public HandleClientRequest() {
        xooDBEngine = new XooDBEngine();
    }

    /**
     * 
     * @param request
     * @return
     * @throws TransformerConfigurationException
     * @throws TransformerException
     */
    public Object processClientRequest(Object request)
            throws TransformerConfigurationException, TransformerException, XMLStreamException {
        if (request instanceof String) {
            String rqstStr = (String) request;
            if (rqstStr.startsWith("LOGIN")) {
                try {
                    String rqstCont = rqstStr.substring(rqstStr.indexOf(":") + 1);
                    String userName = rqstCont.substring(0, rqstCont.indexOf("@"));
                    String password = rqstCont.substring(rqstCont.indexOf("@") + 1);
                    if (xooDBEngine.login(userName, password)) {
                        xooDBEngine.setLogin(true);
                        xooDBEngine.setUserName(userName);
                        return xooDBEngine.executeQuery("SHOW DATABASES");
                    } else {
                        return XooDBConstants.FAIL;
                    }
                } catch (XooDBSQLException ex) {
                    return XooDBConstants.FAIL;
                } catch (ParserConfigurationException ex) {
                    return XooDBConstants.FAIL;
                } catch (SAXException ex) {
                    return XooDBConstants.FAIL;
                } catch (IOException ex) {
                    return XooDBConstants.FAIL;
                }
            } else {
                if (xooDBEngine.isLogin()) {
                    try {
                        xooDBEngine.executeUpdate(rqstStr);
                        return xooDBEngine.executeQuery("SHOW DATABASES");
                    } catch (XooDBSQLException ex) {
                        return ex.getMessage();
                    } catch (ParserConfigurationException ex) {
                        return ex.getMessage();
                    } catch (IOException ex) {
                        return ex.getMessage();
                    } catch (SAXException ex) {
                        return ex.getMessage();
                    } catch (TransformerConfigurationException ex) {
                        return ex.getMessage();
                    } catch (TransformerException ex) {
                        return ex.getMessage();
                    }
                }
            }
        } else if (request instanceof QueryBean) {
            QueryBean query = (QueryBean) request;
            if (xooDBEngine.isLogin()) {
                try {
                    if (query.getQueryType().equalsIgnoreCase("SELECT")) {
                        xooDBEngine.setDatabaseName(query.getDatabaseName());
                        return xooDBEngine.executeQuery(query.getQuery());
                    } else if (query.getQueryType().equalsIgnoreCase("DELETE ROW")) {
                        xooDBEngine.setDatabaseName(query.getDatabaseName());
                        xooDBEngine.removeSelectedRow(query.getTableName(),
                                query.getSelectedRow());
                        return xooDBEngine.executeQuery(("SELECT * FROM "
                                + query.getTableName()));
                    } else if (query.getQueryType().equalsIgnoreCase("UPDATE CELL")) {
                        xooDBEngine.setDatabaseName(query.getDatabaseName());
                        xooDBEngine.updateCell(query.getTableName(),
                                query.getSelectedRow(), query.getSelectedColumn(),
                                query.getCellData(), query.getFieldName());
                        return xooDBEngine.executeQuery(("SELECT * FROM "
                                + query.getTableName()));
                    } else if (query.getQueryType().equalsIgnoreCase("SHOW DATABASES")) {
                        return xooDBEngine.executeQuery("SHOW DATABASES");
                    } else {
                        xooDBEngine.setDatabaseName(query.getDatabaseName());
                        xooDBEngine.executeUpdate(query.getQuery());
                        return xooDBEngine.executeQuery("SHOW DATABASES");
                    }
                } catch (XooDBSQLException ex) {
                    return ex.getMessage();
                } catch (ParserConfigurationException ex) {
                    return ex.getMessage();
                } catch (IOException ex) {
                    return ex.getMessage();
                } catch (SAXException ex) {
                    return ex.getMessage();
                } catch (TransformerConfigurationException ex) {
                    return ex.getMessage();
                } catch (TransformerException ex) {
                    return ex.getMessage();
                }
            }
        }
        return null;
    }
}
