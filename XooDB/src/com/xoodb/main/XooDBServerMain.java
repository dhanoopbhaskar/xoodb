/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.xoodb.main;

import com.xoodb.about.AboutFrame;
import com.xoodb.about.ContactFrame;
import com.xoodb.exp.ExceptionFrame;
import com.xoodb.server.DBServer;
import java.awt.AWTException;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JFrame;

/**
 *
 * @author dhanoopbhaskar
 */
public class XooDBServerMain implements ActionListener {
    private MenuItem startMenuItem = null;
    private MenuItem stopMenuItem = null;
    private MenuItem exceptionMenuItem = null;
    private TrayIcon trayIcon = null;
    private DBServer dBServer = null;
    private static ExceptionFrame exceptionFrame = new ExceptionFrame();

    public XooDBServerMain() {
        dBServer = new DBServer();
        initComponents();
    }

    private void initComponents() {
        try {
            PopupMenu trayPopup = new PopupMenu();

//            MenuItem viewMainFrame = new MenuItem("IMB Agent...");
//            trayPopup.add(viewMainFrame);
//            viewMainFrame.addActionListener(new ActionListener() {
//
//                public void actionPerformed(ActionEvent e) {
//                    if (LOGIN) {
//                        agentMainFrame.setVisible(true);
//                        agentMainFrame.setState(Frame.NORMAL);
//                    } else {
//                        loginFrame.setVisible(true);
//                    }
//                }
//            });
//
//            trayPopup.addSeparator();

            MenuItem aboutMenuItem = new MenuItem("About...");
            trayPopup.add(aboutMenuItem);
            aboutMenuItem.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    AboutFrame aboutFrame = new AboutFrame();
                    aboutFrame.setVisible(true);
                    aboutFrame.setLocationRelativeTo(new JFrame());
                }
            });

            MenuItem contactMenuItme = new MenuItem("Contact...");
            trayPopup.add(contactMenuItme);
            contactMenuItme.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    ContactFrame contactFrame = new ContactFrame();
                    contactFrame.setVisible(true);
                    contactFrame.setLocationRelativeTo(new JFrame());
                }
            });
            trayPopup.addSeparator();

            startMenuItem = new MenuItem("Start Server");
            trayPopup.setActionCommand("Start Server");
            startMenuItem.addActionListener(this);
            trayPopup.add(startMenuItem);

            stopMenuItem = new MenuItem("Stop Server");
            stopMenuItem.setEnabled(false);
            stopMenuItem.setActionCommand("Stop Server");
            stopMenuItem.addActionListener(this);
            trayPopup.add(stopMenuItem);


            trayPopup.addSeparator();

            exceptionMenuItem = new MenuItem("View Error Log");            
            exceptionMenuItem.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    exceptionFrame.setVisible(true);
                }
            });
            trayPopup.add(exceptionMenuItem);

            trayPopup.addSeparator();

            MenuItem exitMenuItem = new MenuItem("Exit");
            trayPopup.add(exitMenuItem);
            exitMenuItem.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
            });



            trayIcon = new TrayIcon(
                    Toolkit.getDefaultToolkit().createImage(
                    this.getClass().getResource("/icon/db_stopped.png")), "XooDB Server", trayPopup);
            trayIcon.setImageAutoSize(true);

            trayIcon.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseClicked(MouseEvent event) {
                    if (event.getButton() == MouseEvent.BUTTON1) {
                    }
                }
            });

            SystemTray systemTray = SystemTray.getSystemTray();
            systemTray.add(trayIcon);
        } catch (AWTException ex) {
            XooDBServerMain.appendExceptionLog(ex);
        }
    }

    public void actionPerformed(ActionEvent e) {
        Object srcComp = e.getSource();
        if (srcComp instanceof MenuItem) {
            MenuItem menuItem = (MenuItem) srcComp;
            if (menuItem.getActionCommand().equals("Start Server")) {
                Thread dbServerThread = new Thread(dBServer, "Db Server Thread");
                dbServerThread.start();
                startMenuItem.setEnabled(false);
                stopMenuItem.setEnabled(true);
                trayIcon.setImage(Toolkit.getDefaultToolkit().createImage(
                        this.getClass().getResource("/icon/db_started.png")));
            } else if (menuItem.getActionCommand().equals("Stop Server")) {
                dBServer.stop();
                startMenuItem.setEnabled(true);
                stopMenuItem.setEnabled(false);
                trayIcon.setImage(Toolkit.getDefaultToolkit().createImage(
                        this.getClass().getResource("/icon/db_stopped.png")));
            }
        }
    }

    public static void main(String[] args) {
        new XooDBServerMain();
    }

    public static void appendExceptionLog(Exception exp) {
        exceptionFrame.appendExceptionText(exp);
    }

//    public static void appendExceptionLog(String text) {
//
//    }
}
