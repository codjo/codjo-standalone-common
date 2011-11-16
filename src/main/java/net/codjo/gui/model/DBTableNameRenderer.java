/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.gui.model;
import net.codjo.model.Table;
import net.codjo.model.TableHome;
import net.codjo.persistent.PersistenceException;
import net.codjo.persistent.Reference;
import java.awt.Component;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
/**
 * Renderer permettant d'afficher le libellé des tables de <code>PM_TABLE </code>au lieu
 * de son DB_TABLE_NAME.
 * 
 * <p></p>
 *
 * @author $Author: blazart $
 * @version $Revision: 1.3 $
 *
 *
 */
public class DBTableNameRenderer implements ListCellRenderer, TableCellRenderer {
    // Renderer
    private DefaultListCellRenderer listCellRenderer = new DefaultListCellRenderer();
    private DefaultTableCellRenderer tableCellRenderer = new DefaultTableCellRenderer();
    private TableHome tableHome;

    // Data
    private Map traductTable = new HashMap();

    /**
     * DOCUMENT ME!
     *
     * @param tableHome Description of Parameter
     *
     * @exception PersistenceException Description of Exception
     * @throws IllegalArgumentException TODO
     */
    public DBTableNameRenderer(TableHome tableHome)
            throws PersistenceException {
        if (tableHome == null) {
            throw new IllegalArgumentException();
        }
        this.tableHome = tableHome;
        fillTraductTable();
    }

    /**
     * DOCUMENT ME!
     *
     * @param list
     * @param value
     * @param index
     * @param isSelected
     * @param cellHasFocus
     *
     * @return La valeur de listCellRendererComponent
     */
    public Component getListCellRendererComponent(JList list, Object value, int index,
        boolean isSelected, boolean cellHasFocus) {
        return listCellRenderer.getListCellRendererComponent(list, translateValue(value),
            index, isSelected, cellHasFocus);
    }


    /**
     * DOCUMENT ME!
     *
     * @param table
     * @param value
     * @param isSelected
     * @param hasFocus
     * @param row
     * @param column
     *
     * @return La valeur de tableCellRendererComponent
     */
    public Component getTableCellRendererComponent(JTable table, Object value,
        boolean isSelected, boolean hasFocus, int row, int column) {
        return tableCellRenderer.getTableCellRendererComponent(table,
            translateValue(value), isSelected, hasFocus, row, column);
    }


    /**
     * Traduit la DB_TABLE_NAME en libellé de la table
     *
     * @param value La nouvelle valeur choisie
     *
     * @return La valeur telle qu'on doit la stocker
     */
    public String translateValue(Object value) {
        if (traductTable.containsKey(value)) {
            Table t = (Table)((Reference)traductTable.get(value)).getLoadedObject();
            return t.getTableName();
        }
        else {
            return "? " + value + " ?";
        }
    }


    /**
     * Rempli la HashMap qui fait le mapping entre le <code>DB_TABLE_NAME_ID </code>et
     * <code>TABLE_NAME</code> de la table <code>PM_TABLE</code> .
     *
     * @exception PersistenceException Description of Exception
     */
    private void fillTraductTable() throws PersistenceException {
        java.util.List list = tableHome.getAllObjects();
        for (Iterator iter = list.iterator(); iter.hasNext();) {
            Table table = (Table)((Reference)iter.next()).getObject();
            traductTable.put(table.getDBTableName(), table.getReference());
        }
    }
}
