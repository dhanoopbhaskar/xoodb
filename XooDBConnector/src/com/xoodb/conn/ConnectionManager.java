/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xoodb.conn;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dhanoopbhaskar
 */

public class ConnectionManager implements Runnable{
    private Socket socket = null;
    private ResponseListener responseListener = null;
    private ObjectInputStream readFromServer = null;
    private ObjectOutputStream writeToServer = null;

    public ConnectionManager() {
    }

    public boolean connectToServer(String ipAddress, int portNo)
            throws UnknownHostException, IOException {
        socket = new Socket(ipAddress, portNo);
        return true;
    }

    public void setResponseListener(ResponseListener responseListener) {
        this.responseListener = responseListener;
    }

    public void run() {
        while(true){
            try {
                readFromServer = new ObjectInputStream(socket.getInputStream());
                Object readData = readFromServer.readObject();
                if(responseListener != null) {
                    responseListener.response(readData);
                }
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(ConnectionManager.class.getName()).log(Level.SEVERE, null, ex);                
            } catch (IOException ex) {
                Logger.getLogger(ConnectionManager.class.getName()).log(Level.SEVERE, null, ex);
                break;
            }
        }
    }
    
    public void writeToServer(Object data) {
        try {
            writeToServer = new ObjectOutputStream(socket.getOutputStream());
            writeToServer.writeObject(data);
            writeToServer.flush();
        } catch (IOException ex) {
            Logger.getLogger(ConnectionManager.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
