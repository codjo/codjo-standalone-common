/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.utils;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
/**
 * ComboBox permettant a un utilisateur de selectionner un filtre sur un tableau. Ce
 * JComboBox est connecte a un <code>TableFilter</code> (responsable du filtre). Le
 * ComboBox se rempli avec les informations contenu dans le model.
 *
 * @author $Author: marcona $
 * @version $Revision: 1.4 $
 *
 * @see TableFilter
 */
public class TableFilterCombo extends JComboBox {
    public static final Object NO_FILTER = makeEnum("Tout");
    public static final Object NULL_FILTER = makeEnum("Vide");
    private ComboBoxListener comboListener = new ComboBoxListener();
    private TableFilterListener tableListener = new TableFilterListener();
    private java.util.Comparator comparator = null;
    private int column;
    private TableFilter tableFilterModel;

    /**
     * Constructor
     */
    public TableFilterCombo() {
        addActionListener(comboListener);
    }


    /**
     * Constructor
     *
     * @param filterModel
     * @param column
     */
    public TableFilterCombo(TableFilter filterModel, int column) {
        this();
        setTableFilter(filterModel, column);
    }

    public void setComparator(java.util.Comparator comparator) {
        this.comparator = comparator;
        fillComboBox();
    }


    /**
     * DOCUMENT ME!
     *
     * @param filterModel La nouvelle valeur de tableFilter
     * @param column
     */
    public void setTableFilter(TableFilter filterModel, int column) {
        this.column = column;
        tableFilterModel = filterModel;
        fillComboBox();
        tableFilterModel.addPropertyChangeListener(column, tableListener);
        tableFilterModel.getModel().addTableModelListener(tableListener);
    }


    public java.util.Comparator getComparator() {
        return comparator;
    }


    /**
     * Construction d'un objet pour faire Enum.
     *
     * @param item
     *
     * @return Un objet Enum
     */
    private static Object makeEnum(final String item) {
        return new Object() {
                public String toString() {
                    return item;
                }
            };
    }


    private DefaultComboBoxModel buildSortedModel(TableModel model) {
        Set set = null;
        if (getComparator() != null) {
            set = new TreeSet(getComparator());
        }
        else {
            set = new TreeSet();
        }

        boolean modelContainsNullValue = false;
        for (int i = 0; i < model.getRowCount(); i++) {
            Object obj = model.getValueAt(i, column);
            if (obj == null) {
                modelContainsNullValue = true;
            }
            else {
                set.add(obj);
            }
        }
        DefaultComboBoxModel comboModel = new DefaultComboBoxModel(set.toArray());
        if (modelContainsNullValue) {
            comboModel.insertElementAt(NULL_FILTER, 0);
        }
        return comboModel;
    }


    private DefaultComboBoxModel buildUnsortedModel(TableModel model) {
        Set set = new HashSet();
        boolean modelContainsNullValue = false;
        for (int i = 0; i < model.getRowCount(); i++) {
            Object obj = model.getValueAt(i, column);
            if (obj == null) {
                modelContainsNullValue = true;
            }
            else {
                set.add(obj);
            }
        }
        DefaultComboBoxModel comboModel = new DefaultComboBoxModel(set.toArray());
        if (modelContainsNullValue) {
            comboModel.insertElementAt(NULL_FILTER, 0);
        }
        return comboModel;
    }


    /**
     * Rempli le contenu du ComboBox avec les elements distincts de la colonne a trier.
     * Par défaut le contenu est triée, si il y a echec aucun trie n'est fait.
     */
    private void fillComboBox() {
        if (tableFilterModel == null) {
            return;
        }
        removeActionListener(comboListener);
        TableModel model = tableFilterModel.getModel();

        DefaultComboBoxModel comboModel = null;

        try {
            comboModel = buildSortedModel(model);
        }
        catch (RuntimeException ex) {
            // En cas d'echec on ne trie pas.
            comboModel = buildUnsortedModel(model);
        }

        setModel(comboModel);

        insertItemAt(NO_FILTER, 0);

        if (tableFilterModel.getFilterValue(column) == null) {
            setSelectedIndex(0);
        }
        else {
            setSelectedItem(tableFilterModel.getFilterValue(column));
        }
        addActionListener(comboListener);
    }

    /**
     * Ecoute les changements du ComboBox (action utilisateur).
     *
     * @author $Author: marcona $
     * @version $Revision: 1.4 $
     */
    private class ComboBoxListener implements ActionListener {
        /**
         * DOCUMENT ME!
         *
         * @param parm1
         */
        public void actionPerformed(ActionEvent parm1) {
            Object selectedItem = getSelectedItem();

            if (selectedItem != NO_FILTER) {
                tableFilterModel.setFilter(column, selectedItem);
            }
            else {
                tableFilterModel.clearFilter(column);
            }
        }
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
         * @param evt
         */
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getNewValue() != null) {
                setSelectedItem(tableFilterModel.getFilterValue(column));
            }
            else {
                setSelectedItem(NO_FILTER);
            }
        }


        /**
         * DOCUMENT ME!
         *
         * @param evt Description of Parameter
         */
        public void tableChanged(TableModelEvent evt) {
            fillComboBox();
        }
    }
}
