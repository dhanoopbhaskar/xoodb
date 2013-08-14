/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.xoodb.server;

import com.xoodb.main.XooDBServerMain;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

/**
 *
 * @author dhanoopbhaskar
 */
public class ClientSession implements Runnable {
    private Socket clientSocket = null;
    private ObjectInputStream readFromClient = null;
    private ObjectOutputStream writeToClient = null;
    private HandleClientRequest clientRequest = null;

    public ClientSession(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        clientRequest = new HandleClientRequest();
    }

    public void run() {
        try {
            while (true) {
                readFromClient = new ObjectInputStream(clientSocket.getInputStream());
                Object readData = readFromClient.readObject();
                Object response = clientRequest.processClientRequest(readData);
                if (response != null) {
                    writeToClient(response);
                }
            }
        } catch (XMLStreamException ex) {
            System.out.println(ex);
            XooDBServerMain.appendExceptionLog(ex);
        } catch (TransformerConfigurationException ex) {
            System.out.println(ex);
            XooDBServerMain.appendExceptionLog(ex);
        } catch (TransformerException ex) {
            System.out.println(ex);
            XooDBServerMain.appendExceptionLog(ex);
        } catch(SocketException ex) {
            System.out.println(ex);
            XooDBServerMain.appendExceptionLog(ex);
            if(ex.getMessage().equalsIgnoreCase("Connection reset")) {
                try {
                    clientSocket.close();                    
                } catch (IOException ex1) {
                    Logger.getLogger(ClientSession.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
        } catch (IOException ex) {
            System.out.println(ex);            
            XooDBServerMain.appendExceptionLog(ex);
        } catch (ClassNotFoundException ex) {
            System.out.println(ex);
            XooDBServerMain.appendExceptionLog(ex);
        }

    }

    /**
     *
     * @param data
     */
    private void writeToClient(Object data) {
        try {
            writeToClient = new ObjectOutputStream(clientSocket.getOutputStream());
            writeToClient.writeObject(data);
            writeToClient.flush();
        } catch (IOException ex) {
            System.out.println(ex);
            XooDBServerMain.appendExceptionLog(ex);
        }
    }
}
