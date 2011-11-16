/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.gui;
import java.awt.Color;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
/**
 * DOCUMENT ME!
 *
 * @version $Revision: 1.3 $
 *
 *
 */
public class ListInputError implements ListSelectionListener {
    private int errorLine = -2;
    private Color selectionColor;
    private JTable table;

    /**
     * Constructor for the ListInputError object
     *
     * @param t Description of Parameter
     */
    public ListInputError(JTable t) {
        table = t;
        selectionColor = table.getSelectionBackground();
        table.getSelectionModel().addListSelectionListener(this);
    }

    /**
     * DOCUMENT ME!
     *
     * @param l The new ErrorLine value
     */
    public void setErrorLine(int l) {
        errorLine = l;
        table.addRowSelectionInterval(errorLine, errorLine);
    }


    /**
     * Sets the NoError attribute of the ListInputError object
     */
    public void setNoError() {
        errorLine = -2;
    }


    /**
     * DOCUMENT ME!
     *
     * @param e Description of Parameter
     */
    public void valueChanged(ListSelectionEvent e) {
//			if (e.getValueIsAdjusting()) {
//				return;
//			}
        if (table.getSelectedRow() == errorLine) {
            table.setSelectionBackground(Color.blue);
//                table.repaint();
        }
        else {
            table.setSelectionBackground(selectionColor);
//                table.repaint();
        }
    }
}
