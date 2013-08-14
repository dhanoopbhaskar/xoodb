/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dbfront.xml;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * 
 * @author dhanoopbhaskar
 */
public class XooDBXMLErrorHandler implements ErrorHandler {

    public void warning(SAXParseException exception) throws SAXException {
        System.out.println(exception.getLineNumber() + " "
                + exception.getColumnNumber());
    }

    public void error(SAXParseException exception) throws SAXException {
        System.out.println(exception.getLineNumber() + " "
                + exception.getColumnNumber());
    }

    public void fatalError(SAXParseException exception) throws SAXException {
        System.out.println(exception.getLineNumber() + " "
                + exception.getColumnNumber());
    }
}
