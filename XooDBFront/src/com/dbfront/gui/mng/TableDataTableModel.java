/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dbfront.gui.mng;

import javax.swing.table.DefaultTableModel;

/**
 * 
 * @author dhanoopbhaskar
 */
public class TableDataTableModel extends DefaultTableModel {

    public TableDataTableModel(Object[] columns) {
        super(columns, 0);
    }

    public TableDataTableModel(Object[][] data, Object[] columns) {
        super(data, columns);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return true;
    }
}
