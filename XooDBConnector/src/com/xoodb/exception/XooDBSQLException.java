/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.xoodb.exception;

/**
 *
 * @author dhanoopbhaskar
 */
public class XooDBSQLException extends Exception {
    private String message = null;

    public XooDBSQLException() {
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "XooDBSQLException: " + message;
    }
}
