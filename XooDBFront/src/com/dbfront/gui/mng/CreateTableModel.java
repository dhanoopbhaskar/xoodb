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
public class CreateTableModel extends DefaultTableModel {

    public CreateTableModel() {
        super(new String[]{"Field Name", "Type", "Size", "Null", "Primary Key"}, 0);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return true;
    }

    @Override
    public Class<?> getColumnClass(int column) {
        return getValueAt(0, column).getClass();
    }

}
