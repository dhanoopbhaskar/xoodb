/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.xoodb.xml;

import com.sun.rowset.internal.XmlErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 *
 * @author dhanoopbhaskar
 */
public class XooDBXMLErrorHandler extends XmlErrorHandler {
    @Override
    public void warning(SAXParseException exception) throws SAXException {
        System.out.println(exception.getLineNumber() + " "
                + exception.getColumnNumber());
    }

    @Override
    public void error(SAXParseException exception) throws SAXException {
        System.out.println(exception.getLineNumber() + " "
                + exception.getColumnNumber());
    }

    @Override
    public void fatalError(SAXParseException exception) throws SAXException {
        System.out.println(exception.getLineNumber() + " "
                + exception.getColumnNumber());
    }
}
