/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.gui.model;
import net.codjo.model.TableRecordingMode;
import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
/**
 * Renderer permettant d'afficher le libellé du mode d'archivage d'une table au lieu de
 * son ID
 *
 * @author $Author: acharif $
 * @version $Revision: 1.3 $
 *
 *
 */
public class TableRecordingModeRenderer implements ListCellRenderer, TableCellRenderer {
    private DefaultListCellRenderer listCellRenderer = new DefaultListCellRenderer();
    private DefaultTableCellRenderer tableCellRenderer = new DefaultTableCellRenderer();
    private Map traductTable;

    /**
     * Constructor for the TableRecordingModeRenderer object
     */
    public TableRecordingModeRenderer() {
        fillTraductTable();
        tableCellRenderer.setHorizontalAlignment(JLabel.CENTER);
        //listCellRenderer.setHorizontalAlignment(JLabel.CENTER);
    }

    /**
     * Gets the ListCellRendererComponent attribute of the TableRecordingModeRenderer
     * object
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
     * Gets the TableCellRendererComponent attribute of the TableRecordingModeRenderer
     * object
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
     * Gets the AllRecordingMode attribute of the TableRecordingModeRenderer object
     *
     * @return The AllRecordingMode value
     */
    public List getAllRecordingMode() {
        List theList = new ArrayList();
        theList.add(new Integer(TableRecordingMode.NONE));
        theList.add(new Integer(TableRecordingMode.BY_PERIOD));
        theList.add(new Integer(TableRecordingMode.BY_PERIOD_AND_PORTFOLIOGROUP));
        theList.add(new Integer(TableRecordingMode.BY_PORTFOLIOGROUP));
        return theList;
    }


    /**
     * Traduit la valeur
     *
     * @param value La nouvelle valeur choisie
     *
     * @return La valeur à afficher
     */
    public String translateValue(Object value) {
        if (value == null) {
            return "";
        }
        else if (traductTable.containsKey(value)) {
            return (String)traductTable.get(value);
        }
        else {
            return "? " + value + " ?";
        }
    }


    /**
     * Remplissage de la table de traduction
     */
    private void fillTraductTable() {
        traductTable = new HashMap();
        traductTable.put(new Integer(TableRecordingMode.NONE), "Aucune");
        traductTable.put(new Integer(TableRecordingMode.BY_PERIOD), "Période");
        traductTable.put(new Integer(TableRecordingMode.BY_PERIOD_AND_PORTFOLIOGROUP),
            "Période et ptf");
        traductTable.put(new Integer(TableRecordingMode.BY_PORTFOLIOGROUP), "Ptf");
    }
}
