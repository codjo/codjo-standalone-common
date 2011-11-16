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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.List;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
/**
 * Renderer permettant d'afficher le libellé des tables de <code>PM_TABLE </code>au lieu
 * de son ID.
 * 
 * <p>
 * Cette classe propose un deuxieme constructeur qui prend en parametre deux combo pour
 * l'edition des table source et destination.
 * </p>
 *
 * @author $Author: acharif $
 * @version $Revision: 1.3 $
 *
 *
 */
public class TableNameRenderer implements ListCellRenderer, TableCellRenderer {
    // Renderer
    private DefaultListCellRenderer listCellRenderer = new DefaultListCellRenderer();
    private DefaultTableCellRenderer tableCellRenderer = new DefaultTableCellRenderer();

    // Data
    private Map traductTable = new HashMap();
    private TableHome tableHome;

    /**
     * Mapping entre le <code>DB_TABLE_NAME_ID</code> et <code>TABLE_NAME</code> de la
     * table <code>PM_TABLE.</code>
     *
     * @param tableHome Description of Parameter
     *
     * @exception PersistenceException Description of Exception
     * @throws IllegalArgumentException TODO
     */
    public TableNameRenderer(TableHome tableHome)
            throws PersistenceException {
        if (tableHome == null) {
            throw new IllegalArgumentException();
        }
        this.tableHome = tableHome;
        fillTraductTable();
    }

    /**
     * Gets the ListCellRendererComponent attribute of the TableNameRenderer object
     *
     * @param list Description of Parameter
     * @param value Description of Parameter
     * @param index Description of Parameter
     * @param isSelected Description of Parameter
     * @param cellHasFocus Description of Parameter
     *
     * @return The ListCellRendererComponent value
     */
    public Component getListCellRendererComponent(JList list, Object value, int index,
        boolean isSelected, boolean cellHasFocus) {
        return listCellRenderer.getListCellRendererComponent(list, translateValue(value),
            index, isSelected, cellHasFocus);
    }


    /**
     * Gets the TableCellRendererComponent attribute of the TableNameRenderer object
     *
     * @param table Description of Parameter
     * @param value Description of Parameter
     * @param isSelected Description of Parameter
     * @param hasFocus Description of Parameter
     * @param row Description of Parameter
     * @param column Description of Parameter
     *
     * @return The TableCellRendererComponent value
     */
    public Component getTableCellRendererComponent(JTable table, Object value,
        boolean isSelected, boolean hasFocus, int row, int column) {
        return tableCellRenderer.getTableCellRendererComponent(table,
            translateValue(value), isSelected, hasFocus, row, column);
    }


    /**
     * Gets the TableIdList attribute of the TableNameRenderer object
     *
     * @param step Description of Parameter
     * @param extraTableRef Description of Parameter
     *
     * @return The TableIdList value
     */
    public Object[] getTableIdList(java.util.List step, Reference extraTableRef) {
        Table extraTable = null;
        if (extraTableRef != null) {
            extraTable = (Table)extraTableRef.getLoadedObject();
        }
        Object[] tableList = traductTable.values().toArray();
        Arrays.sort(tableList,
            new TableReferenceComparator(TableReferenceComparator.COMPARE_BY_TABLE_NAME));

        List listId = new ArrayList();
        for (int i = 0; i < tableList.length; i++) {
            Table table = (Table)((Reference)tableList[i]).getLoadedObject();
            if ((step != null && step.contains(table.getTableStep()))
                    || (extraTable != null && table.equals(extraTable))) {
                listId.add(table.getId());
            }
            else if (step == null) {
                listId.add(table.getId());
            }
        }

        return listId.toArray();
    }


    /**
     * Traduit la value contenue dans la combo en valeur à stocker en BD
     *
     * @param value La nouvelle valeur choisie
     *
     * @return La valeur telle qu'on doit la stocker
     *
     * @todo cette methode ne devrait pas plutot renvoyer un ID ? A VERIFIER
     */
    public String translateValue(Object value) {
        if (traductTable.containsKey(value)) {
            Table t = (Table)((Reference)traductTable.get(value)).getLoadedObject();
            return t.getTableName();
        }
        else {
            return "???????";
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
            traductTable.put(table.getId(), table.getReference());
        }
    }
}
