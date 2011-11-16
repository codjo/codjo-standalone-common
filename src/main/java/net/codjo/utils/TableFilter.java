/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.utils;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableModel;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Arrays;
import java.util.List;
/**
 * Model pour filtrer un tableau.
 *
 * @author $Author: blazart $
 * @version $Revision: 1.3 $
 *
 */
public class TableFilter extends TableMap {
    private int[] indexes;
    private int rowCount = 0;
    private Object[][] filters;
    private transient PropertyChangeSupport propertyChangeListeners =
        new PropertyChangeSupport(this);

    /**
     * Constructor
     *
     * @param model Model original filtrer
     */
    public TableFilter(TableModel model) {
        setModel(model);
    }

    /**
     * Positionne le model a filtrer.
     *
     * @param model Un nouveau Model
     */
    public void setModel(TableModel model) {
        super.setModel(model);
        reallocateInternalData();
    }


    /**
     * Positionne un filtre sur une colonne.
     *
     * @param column La colonne filtree
     * @param value La valeur du filtre (null = pas de filtre)
     */
    public void setFilter(int column, Object value) {
        Object oldValue = filters[column];
        filters[column] = new Object[] {value};
        applyFilter();
        propertyChangeListeners.firePropertyChange(Integer.toString(column), oldValue,
            filters[column]);
    }


    /**
     * DOCUMENT ME!
     *
     * @param aValue
     * @param aRow
     * @param aColumn
     *
     * @throws IllegalArgumentException TODO
     */
    public void setValueAt(Object aValue, int aRow, int aColumn) {
        checkModel();
        if (aRow > rowCount) {
            throw new IllegalArgumentException();
        }
        model.setValueAt(aValue, indexes[aRow], aColumn);
        applyFilter();
    }


    /**
     * Retourne les valeurs de tous les filtres de la colonne.
     *
     * @param column La colonne
     *
     * @return Tableau de filtre (ou null)
     */
    public Object[] getFilterValueList(int column) {
        return filters[column];
    }


    /**
     * Indique si la colonne est filtrée.
     *
     * @param column La colonne
     *
     * @return 'true' si la colonne est filtrée
     */
    public boolean hasFilter(int column) {
        return filters[column] != null;
    }


    /**
     * Retourne la premiere valeur du filtre de la colonne.
     *
     * @param column La colonne
     *
     * @return Le premier filtre (ou null)
     */
    public Object getFilterValue(int column) {
        Object[] columnFilter = getFilterValueList(column);
        if (columnFilter == null || columnFilter.length == 0) {
            return null;
        }
        return columnFilter[0];
    }


    /**
     * DOCUMENT ME!
     *
     * @param aRow
     * @param aColumn
     *
     * @return
     */
    public Object getValueAt(int aRow, int aColumn) {
        checkModel();
        if (aRow > rowCount) {
            return null;
        }
        else {
            return model.getValueAt(indexes[aRow], aColumn);
        }
    }


    /**
     * DOCUMENT ME!
     *
     * @return The RowCount value
     */
    public int getRowCount() {
        return rowCount;
    }


    /**
     * Adds a feature to the Filter attribute of the TableFilter object
     *
     * @param column The feature to be added to the Filter attribute
     * @param value The feature to be added to the Filter attribute
     */
    public void addFilter(int column, Object value) {
        Object oldValue = filters[column];
        Object[] a = (filters[column] != null) ? filters[column] : new Object[0];
        List columnFilters = new java.util.ArrayList(Arrays.asList(a));
        columnFilters.add(value);
        filters[column] = columnFilters.toArray();
        applyFilter();
        propertyChangeListeners.firePropertyChange(Integer.toString(column), oldValue,
            filters[column]);
    }


    /**
     * Enleve le filtre de la colonne.
     *
     * @param column La colonne
     */
    public void clearFilter(int column) {
        Object oldValue = filters[column];
        filters[column] = null;
        applyFilter();
        propertyChangeListeners.firePropertyChange(Integer.toString(column), oldValue,
            filters[column]);
    }


    /**
     * Overview.
     * 
     * <p>
     * Description
     * </p>
     */
    public void clearAllColumnFilter() {
        for (int i = 0; i < getColumnCount(); i++) {
            clearFilter(i);
        }
    }


    /**
     * Overview.
     * 
     * <p>
     * Description
     * </p>
     *
     * @param column Description of Parameter
     * @param value Description of Parameter
     */
    public void removeFilter(int column, Object value) {
        Object[] oldValue = filters[column];
        if (oldValue == null) {
            return;
        }
        List columnFilters = new java.util.ArrayList(Arrays.asList(oldValue));
        if (columnFilters.contains(value)) {
            if (columnFilters.size() == 1) {
                clearFilter(column);
            }
            else {
                columnFilters.remove(value);
                filters[column] = columnFilters.toArray();
                applyFilter();
                propertyChangeListeners.firePropertyChange(Integer.toString(column),
                    oldValue, filters[column]);
            }
        }
        else {
            // pas de filtre value dans la column !
        }
    }


    /**
     * Overview.
     * 
     * <p>
     * Description
     * </p>
     *
     * @param column Description of Parameter
     * @param value Description of Parameter
     *
     * @return Description of the Returned Value
     */
    public boolean containsFilterValue(int column, Object value) {
        Object[] columnFilters = filters[column];
        if (columnFilters == null) {
            return false;
        }

        for (int i = 0; i < filters[column].length; i++) {
            if (filters[column][i] == value) {
                return true;
            }
            else if (filters[column][i] != null && filters[column][i].equals(value)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Modification du model sous jacent.
     *
     * @param evt
     */
    public void tableChanged(TableModelEvent evt) {
        reallocateInternalData();
        applyFilter();
    }


    /**
     * DOCUMENT ME!
     *
     * @param column Description of Parameter
     * @param l
     */
    public synchronized void removePropertyChangeListener(int column,
        PropertyChangeListener l) {
        propertyChangeListeners.removePropertyChangeListener(Integer.toString(column), l);
    }


    /**
     * Ajoute un PropertyChangeListener pour les Filtres
     *
     * @param column
     * @param l
     */
    public synchronized void addPropertyChangeListener(int column,
        PropertyChangeListener l) {
        propertyChangeListeners.addPropertyChangeListener(Integer.toString(column), l);
    }


    /**
     * Indique si la ligne verifie les filtres.
     *
     * @param aRow La ligne du model a verifier.
     *
     * @return <code>true</code> la ligne respecte les filtre (sera affichee)
     */
    private boolean isDisplayed(int aRow) {
        boolean isDisplayed = true;

        for (int ci = 0; ci < getColumnCount(); ci++) {
            if (checkColumnFilter(ci, getModel().getValueAt(aRow, ci)) == false) {
                return false;
            }
        }

        return true;
    }


    /**
     * Verifie que la valeur "value" correspond au moins a un des filtres.
     *
     * @param column La colonne
     * @param value La valeur
     *
     * @return 'true' si la valeur est contenu dans les filtres, 'false' sinon.
     */
    private boolean checkColumnFilter(int column, Object value) {
        if (filters[column] == null) {
            return true;
        }
        return containsFilterValue(column, value);
    }


    /**
     * Applique les filtre. Cette methode lance un evenenment de modification.
     */
    private void applyFilter() {
        rowCount = 0;

        for (int i = 0; i < getModel().getRowCount(); i++) {
            if (isDisplayed(i)) {
                indexes[rowCount] = i;
                rowCount++;
            }
        }

        super.tableChanged(new TableModelEvent(this));
    }


    /**
     * Reallocation des donnees interne.
     */
    private void reallocateInternalData() {
        Object[][] oldFilters = filters;
        filters = new Object[getModel().getColumnCount()][];
        if (oldFilters != null) {
            for (int i = 0; i < oldFilters.length && i < filters.length; i++) {
                filters[i] = oldFilters[i];
            }
        }

        rowCount = getModel().getRowCount();
        indexes = new int[rowCount];
        for (int row = 0; row < rowCount; row++) {
            indexes[row] = row;
        }

        checkModel();
    }


    /**
     * Verification d'un changement.
     *
     * @todo a virer ???
     */
    private void checkModel() {
        if (indexes.length != model.getRowCount()) {
            System.err.println("Filter not informed of a change in model.");
        }
    }
}
