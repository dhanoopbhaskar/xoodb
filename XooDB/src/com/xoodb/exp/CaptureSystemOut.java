/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.xoodb.exp;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import javax.swing.SwingUtilities;

/**
 *
 * @author dhanoopbhaskar
 */
public class CaptureSystemOut implements Runnable {
    PipedOutputStream pipedOutputStream = null;
    BufferedReader bufferedReader = null;
    ExceptionFrame exceptionFrame = null;
    PrintStream printStream = null;
    String line = null;

    public CaptureSystemOut(ExceptionFrame exceptionFrame) {
        try {
            this.exceptionFrame = exceptionFrame;
            pipedOutputStream = new PipedOutputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(
                    new PipedInputStream(pipedOutputStream)));

            printStream = new PrintStream(new File("xyz.txt"));
            System.setOut(printStream);
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    public void run() {
        try {            
            while ((line = bufferedReader.readLine()) != null) {
                try {
                    SwingUtilities.invokeAndWait(new Runnable() {

                        public void run() {
                            exceptionFrame.appendExceptionText(line);
                        }
                    });
                } catch (InterruptedException ex) {
                    System.out.println(ex);
                } catch (InvocationTargetException ex) {
                    System.out.println(ex);
                }
            }
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

}
