/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.utils;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
/**
 * Liste permettant a un utilisateur de selectionner des filtres sur un tableau. Cette
 * JListe est connecte a un <code>TableFilter</code> (responsable du filtre). La liste
 * se rempli avec les informations contenu dans le model.
 *
 * @author $Author: marcona $
 * @version $Revision: 1.4 $
 *
 * @see TableFilter
 */
public class TableMultiFilterGui extends JTable {
    private InnerModel model = new InnerModel();
    private TableFilterListener tableListener = new TableFilterListener();

    /**
     * Constructor
     */
    public TableMultiFilterGui() {
        setModel(model);
        getColumn(getModel().getColumnName(0)).setMinWidth(25);
        getColumn(getModel().getColumnName(0)).setMaxWidth(25);
    }

    /**
     * DOCUMENT ME!
     *
     * @param filterModel The new TableFilter value
     * @param column
     */
    public void setTableFilter(TableFilter filterModel, int column) {
        if (model.tableFilterModel != null) {
            model.tableFilterModel.removePropertyChangeListener(model.filteredColumn,
                tableListener);
            model.tableFilterModel.getModel().removeTableModelListener(tableListener);
        }
        model.init(filterModel, column);
        filterModel.addPropertyChangeListener(column, tableListener);
        filterModel.getModel().addTableModelListener(tableListener);
    }


    /**
     * Retourne la colonne filtrée.
     *
     * @return The FilteredColumn value
     */
    public int getFilteredColumn() {
        return model.filteredColumn;
    }

    /**
     * Ecoute les changements de Filtre, et de contenu.
     *
     * @author $Author: marcona $
     * @version $Revision: 1.4 $
     */
    private class TableFilterListener implements PropertyChangeListener,
        TableModelListener {
        /**
         * DOCUMENT ME!
         *
         * @param evt Description of Parameter
         */
        public void tableChanged(TableModelEvent evt) {
            model.fillTable();
        }


        /**
         * DOCUMENT ME!
         *
         * @param evt
         */
        public void propertyChange(PropertyChangeEvent evt) {
            repaint();
        }
    }


    /**
     * DOCUMENT ME!
     *
     * @author $Author: marcona $
     * @version $Revision: 1.4 $
     */
    private static class InnerModel extends AbstractTableModel {
        static final String[] columnNames = {"Sel", "Valeur"};
        static final Class[] columnClass = {Boolean.class, Object.class};
        /** Description of the Field */
        public TableFilter tableFilterModel = null;
        /** Description of the Field */
        public int filteredColumn = 0;
        private java.util.List valueList = new java.util.ArrayList();

        /**
         * Constructeur de HashMapTableModel
         */
        InnerModel() {}

        /**
         * Sets the valueAt attribute of the HashMapTableModel object
         *
         * @param aValue The new valueAt value
         * @param rowIndex The new valueAt value
         * @param columnIndex The new valueAt value
         */
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            if (columnIndex == 0) {
                if (tableFilterModel.containsFilterValue(filteredColumn,
                            valueList.get(rowIndex))) {
                    tableFilterModel.removeFilter(filteredColumn, valueList.get(rowIndex));
                }
                else {
                    tableFilterModel.addFilter(filteredColumn, valueList.get(rowIndex));
                }
                this.fireTableCellUpdated(rowIndex, columnIndex);
            }
        }


        /**
         * Gets the cellEditable attribute of the HashMapTableModel object
         *
         * @param rowIndex Description of Parameter
         * @param columnIndex Description of Parameter
         *
         * @return The cellEditable value
         */
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex == 0;
        }


        /**
         * Gets the RowCount attribute of the HashMapTableModel object
         *
         * @return The RowCount value
         */
        public int getRowCount() {
            return valueList.size();
        }


        /**
         * Gets the ColumnCount attribute of the HashMapTableModel object
         *
         * @return The ColumnCount value
         */
        public int getColumnCount() {
            return columnNames.length;
        }


        /**
         * Gets the ColumnName attribute of the HashMapTableModel object
         *
         * @param columnIndex Description of Parameter
         *
         * @return The ColumnName value
         */
        public String getColumnName(int columnIndex) {
            return columnNames[columnIndex];
        }


        /**
         * Gets the Class attribute of the HashMapTableModel object
         *
         * @param columnIndex Description of Parameter
         *
         * @return The ColumnName value
         */
        public Class getColumnClass(int columnIndex) {
            return columnClass[columnIndex];
        }


        /**
         * Gets the ValueAt attribute of the HashMapTableModel object
         *
         * @param row Description of Parameter
         * @param column Description of Parameter
         *
         * @return The ValueAt value
         */
        public Object getValueAt(int row, int column) {
            Object value = valueList.get(row);
            switch (column) {
                case 0:
                    if (tableFilterModel.containsFilterValue(filteredColumn, value)) {
                        return Boolean.TRUE;
                    }
                    else {
                        return Boolean.FALSE;
                    }
                case 1:
                    return value;
            }
            return null;
        }


        /**
         * Sets the value attribute of the HashMapTableModel object
         *
         * @param tfm Description of Parameter
         * @param filteredColumn Description of Parameter
         */
        public void init(TableFilter tfm, int filteredColumn) {
            this.filteredColumn = filteredColumn;
            this.tableFilterModel = tfm;
            fillTable();
        }


        /**
         * Overview.
         * 
         * <p>
         * Description
         * </p>
         */
        public void fillTable() {
            boolean hasNullValue = false;
            Set set = new TreeSet();
            TableModel model = tableFilterModel.getModel();
            for (int i = 0; i < model.getRowCount(); i++) {
                if (model.getValueAt(i, filteredColumn) != null) {
                    set.add(model.getValueAt(i, filteredColumn));
                }
                else {
                    hasNullValue = true;
                }
            }
            valueList.clear();
            if (hasNullValue) {
                valueList.add(null);
            }
            valueList.addAll(set);
            fireTableDataChanged();
        }
    }
}
