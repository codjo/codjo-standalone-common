/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.utils;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
/**
 * Overview.
 *
 * @author $Author: blazart $
 * @version $Revision: 1.3 $
 *
 */
public class TableMap extends AbstractTableModel implements TableModelListener {
    /** Description of the Field */
    protected TableModel model;

    /**
     * Sets the Model attribute of the TableMap object
     *
     * @param model The new Model value
     */
    public void setModel(TableModel model) {
        this.model = model;
        model.addTableModelListener(this);
    }


    /**
     * Sets the ValueAt attribute of the TableMap object
     *
     * @param aValue The new ValueAt value
     * @param aRow The new ValueAt value
     * @param aColumn The new ValueAt value
     */
    public void setValueAt(Object aValue, int aRow, int aColumn) {
        model.setValueAt(aValue, aRow, aColumn);
    }


    /**
     * Gets the Model attribute of the TableMap object
     *
     * @return The Model value
     */
    public TableModel getModel() {
        return model;
    }


    // By default, Implement TableModel by forwarding all messages
    // to the model.
    /**
     * Gets the ValueAt attribute of the TableMap object
     *
     * @param aRow Description of Parameter
     * @param aColumn Description of Parameter
     *
     * @return The ValueAt value
     */
    public Object getValueAt(int aRow, int aColumn) {
        return model.getValueAt(aRow, aColumn);
    }


    /**
     * Gets the RowCount attribute of the TableMap object
     *
     * @return The RowCount value
     */
    public int getRowCount() {
        return (model == null) ? 0 : model.getRowCount();
    }


    /**
     * Gets the ColumnCount attribute of the TableMap object
     *
     * @return The ColumnCount value
     */
    public int getColumnCount() {
        return (model == null) ? 0 : model.getColumnCount();
    }


    /**
     * Gets the ColumnName attribute of the TableMap object
     *
     * @param aColumn Description of Parameter
     *
     * @return The ColumnName value
     */
    public String getColumnName(int aColumn) {
        return model.getColumnName(aColumn);
    }


    /**
     * Gets the ColumnClass attribute of the TableMap object
     *
     * @param aColumn Description of Parameter
     *
     * @return The ColumnClass value
     */
    public Class getColumnClass(int aColumn) {
        return model.getColumnClass(aColumn);
    }


    /**
     * Gets the CellEditable attribute of the TableMap object
     *
     * @param row Description of Parameter
     * @param column Description of Parameter
     *
     * @return The CellEditable value
     */
    public boolean isCellEditable(int row, int column) {
        return model.isCellEditable(row, column);
    }


    //
    // Implementation of the TableModelListener interface,
    //
    // By default forward all events to all the listeners.
    /**
     * Overview.
     *
     * @param evt Description of Parameter
     */
    public void tableChanged(TableModelEvent evt) {
        fireTableChanged(evt);
    }
}
