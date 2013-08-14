/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * CreateTableJDialog.java
 *
 * Created on Nov 18, 2010, 5:30:14 PM
 */
package com.dbfront.gui;

import com.dbfront.conn.ResponseListener;
import com.dbfront.gui.mng.CreateTableModel;
import com.dbfront.utilities.Utility;
import com.xoodb.beans.QueryBean;
import com.xoodb.beans.ResultBean;
import java.util.ArrayList;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 * 
 * @author dhanoopbhaskar
 */
public class CreateTableJDialog extends javax.swing.JDialog implements ResponseListener {

    /** Creates new form CreateTableJDialog */
    XooDBFrontMainFrame mainFrame = null;

    public CreateTableJDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        mainFrame = (XooDBFrontMainFrame) parent;
        createTableJTable.setModel(new CreateTableModel());
        JComboBox comboBox = new JComboBox(new Object[]{
            "VARCHAR", "INT", "CHAR", "DATE", "DATE TIME"});
        createTableJTable.getColumnModel().getColumn(1).setCellEditor(
                new DefaultCellEditor(comboBox));
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cancelButton = new javax.swing.JButton();
        createTableButton = new javax.swing.JButton();
        addFieldButton = new javax.swing.JButton();
        removeFieldButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        createTableJTable = new javax.swing.JTable();
        tableNameTextField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        createTableButton.setText("Create Table");
        createTableButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createTableButtonActionPerformed(evt);
            }
        });

        addFieldButton.setText("Add Field");
        addFieldButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addFieldButtonActionPerformed(evt);
            }
        });

        removeFieldButton.setText("Remove Field");
        removeFieldButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeFieldButtonActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setText("Table Name:");

        createTableJTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Field Name", "Type", "Size", "Null", "Primary Key"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Boolean.class, java.lang.Boolean.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane1.setViewportView(createTableJTable);

        jLabel2.setText("Fields");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 567, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tableNameTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 503, Short.MAX_VALUE))
                    .addComponent(jLabel2))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(tableNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(removeFieldButton)
                        .addGap(18, 18, 18)
                        .addComponent(addFieldButton)
                        .addGap(18, 18, 18)
                        .addComponent(createTableButton)
                        .addGap(18, 18, 18)
                        .addComponent(cancelButton))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton)
                    .addComponent(createTableButton)
                    .addComponent(addFieldButton)
                    .addComponent(removeFieldButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void addFieldButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addFieldButtonActionPerformed
        /**
         * Get the table model
         */
        DefaultTableModel tableModel = (DefaultTableModel) createTableJTable.getModel();
        /**
         * Add a new row to the table model using the method addRow()
         *
         * the table header is
         * "Field Name", "Type", "Size", "Null", "Primary Key"
         */
        tableModel.addRow(new Object[]{"", "", new Integer("0"), false, false});
        /**
         * update the user interface to reflect the changes
         */
        createTableJTable.updateUI();
    }//GEN-LAST:event_addFieldButtonActionPerformed

    private void removeFieldButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeFieldButtonActionPerformed
        /**
         * check whether any rows are selected
         *
         * if yes
         * -get the table model
         * -remove the selected row using the method removeRow(index)
         * -update the user interface
         *
         * else
         * -display warning message
         */
        int selectedRow = createTableJTable.getSelectedRow();
        if (selectedRow != -1) {
            DefaultTableModel tableModel = (DefaultTableModel) createTableJTable.getModel();
            tableModel.removeRow(selectedRow);
            createTableJTable.updateUI();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Please select field you want to delete",
                    "Remove Field", JOptionPane.WARNING_MESSAGE);
        }

    }//GEN-LAST:event_removeFieldButtonActionPerformed

    private void createTableButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createTableButtonActionPerformed
        String tableName = tableNameTextField.getText();
        Utility utility = new Utility();
        /**
         * using
         * createTableCreationStatement(Vector , tableName)
         * construct a CREATE TABLE command using the values retrieved from the
         * table
         */
        if(tableName==null || tableName.equals("")) {
            JOptionPane.showMessageDialog(this, "Enter a name for the table",
                    "Create Table", JOptionPane.WARNING_MESSAGE);
        } else {

            String stmnt = utility.createTableCreationStatement(
                    ((DefaultTableModel) createTableJTable.getModel()).getDataVector(),
                    tableName);
            /**
             * if statement is not null
             * create an instance of QueryBean with the sufficient data and send it
             * to the XooDB server
             */
            if (stmnt != null) {
                System.out.println("stmnt: " + stmnt);

                QueryBean query = new QueryBean();
                query.setQuery(stmnt);
                query.setDatabaseName(XooDBFrontMainFrame.selectedDatabase);
                query.setQueryType("");
                XooDBFrontMainFrame.connectionManager.setResponseListener(this);
                XooDBFrontMainFrame.connectionManager.writeToServer(query);
            }
        }
    }//GEN-LAST:event_createTableButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addFieldButton;
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton createTableButton;
    private javax.swing.JTable createTableJTable;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton removeFieldButton;
    private javax.swing.JTextField tableNameTextField;
    // End of variables declaration//GEN-END:variables

    public void response(Object response) {
        if (response instanceof String) {
            JOptionPane.showMessageDialog(this, response, "Create Table", JOptionPane.WARNING_MESSAGE);
        } else if (response instanceof ResultBean) {
            ArrayList data = ((ResultBean) response).getData();
            mainFrame.reloadDatabaseTree(data);
            this.dispose();
        }
    }
}
