/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dbfront.xml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author dhanoopbhaskar
 */
public class XooDBXMLManager {
    private XMLOutputFactory xmlOutputFactory = null;
    private XMLStreamWriter xmlStreamWriter = null;
    private DocumentBuilderFactory documentBuilderFactory = null;
    private DocumentBuilder documentBuilder = null;
    private Document document = null;
    private String uri = "";
    private String xsdURI = "";

    public XooDBXMLManager() {
    }

    public XooDBXMLManager(String uri) throws ParserConfigurationException,
            SAXException, IOException {
        this.uri = uri;
        /**
         * The class DocumentBuilderFactory is responsible for creating
         * new DOM parsers
         */
        documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setValidating(false);
        /**
         * The instance of the class DocumentBuilder is used to create
         * a blank document
         */
        documentBuilder = documentBuilderFactory.newDocumentBuilder();
        documentBuilder.setErrorHandler(new XooDBXMLErrorHandler());
        /**
         * The parse function is used to parse existing XML document
         */
        document = documentBuilder.parse(uri);
    }

    public XooDBXMLManager(String uri, String xsdURI) throws ParserConfigurationException,
            SAXException, IOException {
        this.uri = uri;
        this.xsdURI = xsdURI;
        /**
         * The class DocumentBuilderFactory is responsible for creating
         * new DOM parsers
         */
        documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setValidating(false);
        /**
         * The instance of the class DocumentBuilder is used to create
         * a blank document
         */
        documentBuilder = documentBuilderFactory.newDocumentBuilder();
        documentBuilder.setErrorHandler(new XooDBXMLErrorHandler());
        /**
         * The parse function is used to parse existing XML document
         */
        document = documentBuilder.parse(uri);
    }

    public NodeList getElements(String tagName) {
        /**
         * After parsing the XML document you get the node element using
         * getDocumentElement() method
         */
        Element element = document.getDocumentElement();
        /**
         * The method getElementsByTagName() creates a NodeList
         */
        NodeList nodeList = element.getElementsByTagName(tagName);
        return nodeList;
    }

    public NodeList getElements(Element element, String tagName) {
        /**
         * The method getElementsByTagName() creates a NodeList
         */
        NodeList nodeList = element.getElementsByTagName(tagName);
        return nodeList;
    }

    public String getElementValue(Element element, String tagName) {
        String elementValue = null;
        /**
         * The method getElementsByTagName() creates a NodeList
         */
        NodeList nodeList = element.getElementsByTagName(tagName.trim());
        if(nodeList != null && nodeList.getLength() > 0) {
            Element element1 = (Element) nodeList.item(0);
            elementValue = element1.getFirstChild().getNodeValue();
        }
        return elementValue;
    }

    public Node getNode(String nodeName) {
        Element rootElement = (Element) document.getFirstChild();
        /**
         * The method getElementsByTagName() creates a NodeList
         */
        NodeList nodeList = rootElement.getElementsByTagName(nodeName);
        if(nodeList != null && nodeList.getLength() > 0) {
            Element element = (Element) nodeList.item(0);
            return element;
        }
        return null;
    }

    public Node getNode(Element rootElement, String nodeName) {
        /**
         * The method getElementsByTagName() creates a NodeList
         */
        NodeList nodeList = rootElement.getElementsByTagName(nodeName);
        if(nodeList != null && nodeList.getLength() > 0) {
            Element element = (Element) nodeList.item(0);
            return element;
        }
        return null;
    }

    public Node getNode(String nodeName, String attrName, String attrValue) {
        Element rootElement = (Element) document.getFirstChild();
        /**
         * The method getElementsByTagName() creates a NodeList
         */
        NodeList nodeList = rootElement.getElementsByTagName(nodeName);
        for(int i = 0 ; nodeList != null && i < nodeList.getLength() ; i++) {
            Element element = (Element) nodeList.item(i);
            Attr attr = element.getAttributeNode(attrName);            
            if(attr != null && attr.getValue().equalsIgnoreCase(attrValue)) {
                return element;
            }
        }
        return null;
    }

    public Node getNode(Element rootElement, String nodeName, String attrName, String attrValue) {
        /**
         * The method getElementsByTagName() creates a NodeList
         */
        NodeList nodeList = rootElement.getElementsByTagName(nodeName);
        for(int i = 0 ; nodeList != null && i < nodeList.getLength() ; i++) {
            Element element = (Element) nodeList.item(i);
            Attr attr = element.getAttributeNode(attrName);            
            if(attr != null && attr.getValue().equals(attrValue)) {
                return element;
            }
        }
        return null;
    }

    public Node createNode(String nodeName) {
        return document.createElement(nodeName);
    }

    public Node addAttribute(Node node, String attrName, String attrValue) {
        ((Element) node).setAttribute(attrName, attrValue);
        return node;
    }

    public Node addNewElementToRoot(String elementName) {
        Node rootNode = document.getFirstChild();
        Node newNode = document.createElement(elementName);
        return rootNode.appendChild(newNode);
    }

    public Node addChildNode(Node parentNode, String nodeName) {
        Node newNode = document.createElement(nodeName);
        return parentNode.appendChild(newNode);
    }

    public Node addChildNode(Node parentNode, Node childNode) {
        return parentNode.appendChild(childNode);
    }

    public Node addChildNode(Node parentNode, String nodeName, String nodeValue) {
        Node newNode = document.createElement(nodeName);
        newNode.setTextContent(nodeValue);
        return parentNode.appendChild(newNode);
    }

    /**
     * 
     * @param validate
     */
    public void commitXML(boolean validate) {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            Source source = new DOMSource(document);
            if(validate) {
                validateXML(source, xsdURI);
            }
            Result destination = new StreamResult(new File(uri));
            transformer.transform(source, destination);
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(XooDBXMLManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            Logger.getLogger(XooDBXMLManager.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }

    /**
     * 
     */
    public void commitXML() {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            Source source = new DOMSource(document);
            validateXML(source, xsdURI);
            Result destination = new StreamResult(new File(uri));
            transformer.transform(source, destination);
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(XooDBXMLManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            Logger.getLogger(XooDBXMLManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * 
     * @param source
     * @param xsdURI
     */
    public void validateXML(Source source, String xsdURI) {
        try {
            /**
             * public static final String W3C_XML_SCHEMA_NS_URI =
             *                  "http://www.w3.org/2001/XMLSchema";
             *
             * SchemaFactory creates Schema objects.
             * Its the entry-point to the validation API.
             *
             * create a SchemaFactory capable of understanding WXS schemas
             */
            SchemaFactory schemaFactory = SchemaFactory.newInstance(
                    XMLConstants.W3C_XML_SCHEMA_NS_URI);
            /**
             * load a WXS schema, represented by a Schema instance
             * create schema by reading it from an XSD file
             */
            Schema schema = schemaFactory.newSchema(new StreamSource(xsdURI));
            /**
             * create a Validator instance, which can be used to validate an
             * instance document
             */
            Validator validator = schema.newValidator();
            /**
             * validate the DOM tree - DOMSource source
             */
            validator.validate(source);
        } catch (IOException ex) {
            Logger.getLogger(XooDBXMLManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(XooDBXMLManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     * @param xmlURI
     * @param xsdURI
     */
    public void validateXML(String xmlURI, String xsdURI) {
        try {
            Source source = new StreamSource(xmlURI);
            /**
             * public static final String W3C_XML_SCHEMA_NS_URI =
             *                  "http://www.w3.org/2001/XMLSchema";
             *
             * SchemaFactory creates Schema objects.
             * Its the entry-point to the validation API.
             *
             * create a SchemaFactory capable of understanding WXS schemas
             */
            SchemaFactory schemaFactory = SchemaFactory.newInstance(
                    XMLConstants.W3C_XML_SCHEMA_NS_URI);
            /**
             * load a WXS schema, represented by a Schema instance
             * create schema by reading it from an XSD file
             */
            Schema schema = schemaFactory.newSchema(new StreamSource(xsdURI));
            /**
             * create a Validator instance, which can be used to validate an
             * instance document
             */
            Validator validator = schema.newValidator();
            /**
             * validate the DOM tree - StreamSource source
             */
            validator.validate(source);
        } catch (IOException ex) {
            Logger.getLogger(XooDBXMLManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(XooDBXMLManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     * @param userDirectory
     * @throws XMLStreamException
     * @throws FileNotFoundException
     */
    public void createXMLWriter(File userDirectory) throws
            XMLStreamException, FileNotFoundException {
        String xmlFileName = userDirectory.getAbsolutePath();
        if(xmlFileName.lastIndexOf(File.separator) != -1) {
            String xmlDirectoryPath = xmlFileName.substring(
                    0, xmlFileName.lastIndexOf(File.separator));
            File xmlDirectory = new File(xmlDirectoryPath);
            if(!xmlDirectory.exists()) {
                xmlDirectory.mkdirs();
            }
        }
        xmlOutputFactory = XMLOutputFactory.newInstance();
        xmlStreamWriter = xmlOutputFactory.createXMLStreamWriter(
                new FileOutputStream(userDirectory.getAbsoluteFile()));
    }

    /**
     *
     * @param encoding to set value of encoding in XML document
     * @param version to set value of version in XML document
     * @return a boolean value
     * @throws XMLStreamException
     */
    public boolean createStartDocument(String encoding, String version)
            throws XMLStreamException {
        xmlStreamWriter.writeStartDocument(encoding, version);
        return true;
    }

    /**
     *
     * creates <elementName>
     *
     * @param elementName to create a start element in the XML document
     * @return a boolean value
     * @throws XMLStreamException
     */
    public boolean createStartElement(String elementName)
            throws XMLStreamException {
        xmlStreamWriter.writeStartElement(elementName);
        return true;
    }

    /**
     *
     * creates </elementName>
     *
     * @return a boolean value
     * @throws XMLStreamException
     */
    public boolean createCloseElement() throws XMLStreamException {
        xmlStreamWriter.writeEndElement();
        return true;
    }

    /**
     *
     * @param attrName to set attribute name
     * @param attrValue to set attribute value
     * @return a boolean value
     * @throws XMLStreamException
     */
    public boolean createAttribute(String attrName, String attrValue)
            throws XMLStreamException {
        xmlStreamWriter.writeAttribute(attrName, attrValue);
        return true;
    }

    /**
     *
     * creates <elementName/>
     *
     * @param elementName to set element name
     * @return a boolean value
     * @throws XMLStreamException
     */
    public boolean createEmptyElement(String elementName)
            throws XMLStreamException {
        xmlStreamWriter.writeEmptyElement(elementName);
        return true;
    }

    /**
     *
     * @param data to create CDATA section
     * @return a boolean value
     * @throws XMLStreamException
     */
    public boolean writeCData(String data) throws XMLStreamException {
        xmlStreamWriter.writeCData(data);
        return true;
    }

    /**
     *
     * @param characters to write
     * @return a boolean value
     * @throws XMLStreamException
     */
    public boolean writeElementTextValue(String characters)
            throws XMLStreamException {
        xmlStreamWriter.writeCharacters(characters);
        return true;
    }

    /**
     *
     * closes the XMLStreamWriter
     *
     * @throws XMLStreamException
     */
    public void closeWriter() throws XMLStreamException {
        xmlStreamWriter.close();
        xmlOutputFactory = null;
        xmlStreamWriter = null;
    }
}
