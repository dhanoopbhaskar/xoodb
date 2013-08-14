/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.xoodb.exp;

import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * @author dhanoopbhaskar
 */
public class TextAreaOutputStream extends OutputStream {
    StringBuilder stringBuilder = new StringBuilder();
    ExceptionFrame exceptionFrame = null;
    
    public TextAreaOutputStream(ExceptionFrame exceptionFrame) {
        this.exceptionFrame = exceptionFrame;
    }

    @Override
    public void write(int b) throws IOException {
        if(b == '\r') {
            return;
        }

        if(b == '\n') {
            exceptionFrame.appendExceptionText(stringBuilder.toString());
            stringBuilder.setLength(0);
        }

        stringBuilder.append((char) b);
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() {
    }

}
