/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.utils;
import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellEditor;
/**
 * Listener pour fermer les editeurs en cas de changement du data
 *
 * @author $Author: blazart $
 * @version $Revision: 1.3 $
 *
 */
public class CloseEditorListener implements TableModelListener {
    private JTable table;

    /**
     * Constructeur de CloseEditorListener
     *
     * @param tableToCloseEditors La table recevant le listener
     */
    public CloseEditorListener(JTable tableToCloseEditors) {
        table = tableToCloseEditors;
    }

    /**
     * Le model a changé, on ferme les listeners présents
     *
     * @param evt L'évènement
     */
    public void tableChanged(TableModelEvent evt) {
        for (int i = 0; i < table.getColumnCount(); i++) {
            TableCellEditor tce = table.getColumn(table.getColumnName(i)).getCellEditor();
            if (tce != null) {
                tce.cancelCellEditing();
            }
        }
    }
}
