/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.xoodb.server;

import com.xoodb.main.XooDBServerMain;
import com.xoodb.utilities.Utilities;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dhanoopbhaskar
 */
public class DBServer implements Runnable {
    private ServerSocket serverSocket = null;
    private Utilities utilities = null;
    private boolean running = true;
    public static Hashtable clientSessions = null;

    public DBServer() {
        try {
            utilities = new Utilities();
            clientSessions = new Hashtable();
            serverSocket = new ServerSocket(utilities.getPortNumber());
        } catch (IOException ex) {
            Logger.getLogger(DBServer.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ex);
            XooDBServerMain.appendExceptionLog(ex);
        }
    }

    public void run() {
        while (running) {
            try {
                utilities.printDebug("Server Waiting for Connection");

                Socket clientSocket = serverSocket.accept();

                utilities.printDebug(clientSocket.getInetAddress().getHostAddress() + " Connected");
                utilities.printDebug("---------------------------------------------------------\n");

                ClientSession clientSession = new ClientSession(clientSocket);
                clientSessions.put(clientSocket.getInetAddress().getHostAddress(), clientSession);
                Thread sessionManager = new Thread(clientSession,
                        clientSocket.getInetAddress().getHostAddress());
                sessionManager.start();
            } catch(SocketException ex) {
                System.out.println(ex);
                XooDBServerMain.appendExceptionLog(ex);
                break;
            } catch (IOException ex) {
                Logger.getLogger(DBServer.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println(ex);
                XooDBServerMain.appendExceptionLog(ex);
                break;
            }

        }
    }

    public void stop() {
        running = false;
    }
}
