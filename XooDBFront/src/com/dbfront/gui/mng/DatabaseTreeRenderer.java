/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dbfront.gui.mng;

import com.xoodb.beans.DatabaseBean;
import com.xoodb.beans.TableBean;
import java.awt.Component;
import java.awt.Font;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * 
 * @author dhanoopbhaskar
 */
public class DatabaseTreeRenderer extends DefaultTreeCellRenderer {

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value,
            boolean sel, boolean expanded, boolean leaf,
            int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        Font font = UIManager.getFont("Tree.font");
        if (value instanceof DefaultMutableTreeNode) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            Object usrObj = node.getUserObject();
            if (node.isRoot()) {
                setIcon(new ImageIcon(this.getClass().getResource("/icon/user.png")));
            } else if (usrObj instanceof DatabaseBean) {
                setText(((DatabaseBean) usrObj).getDatabaseName());
                setToolTipText(((DatabaseBean) usrObj).getDatabaseName());
                setIcon(new ImageIcon(this.getClass().getResource("/icon/db.png")));
            } else if (usrObj instanceof TableBean) {
                setText(((TableBean) usrObj).getTableName());
                setToolTipText(((TableBean) usrObj).getTableName());
                setIcon(new ImageIcon(this.getClass().getResource("/icon/table.png")));
            } else if (usrObj instanceof String) {                
                setIcon(new ImageIcon(this.getClass().getResource("/icon/column.png")));
            }
        }
        return this;
    }
}
