/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.gui.broadcast;
import net.codjo.utils.ConnectionManager;
import java.awt.Color;
import java.awt.Component;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.4 $
 */
public class SectionNameRenderer implements ListCellRenderer, TableCellRenderer {
    private DefaultListCellRenderer listCellRenderer = new DefaultListCellRenderer();
    private DefaultTableCellRenderer tableCellRenderer = new DefaultTableCellRenderer();
    private Map<Integer, String> traductTable = new HashMap<Integer, String>();


    /**
     * Constructor for the SectionNameRenderer object
     *
     * @param conMan                    Connection manager
     * @param broadcastSectionTableName Description of the Parameter
     * @param combo                     Description of the Parameter
     *
     * @throws SQLException             oups
     * @throws IllegalArgumentException DOCUMENT ME!
     */
    public SectionNameRenderer(ConnectionManager conMan,
                               String broadcastSectionTableName, JComboBox combo)
          throws SQLException {
        if (conMan == null) {
            throw new IllegalArgumentException("SectionNameRenderer : Paramètre invalide");
        }

        traductTable = loadTraducTable(conMan, broadcastSectionTableName, combo);
    }


    /**
     * Overview.
     *
     * @param conMan                    Description of Parameter
     * @param broadcastSectionTableName Description of the Parameter
     * @param combo                     Description of the Parameter
     *
     * @return Description of the Returned Value
     *
     * @throws SQLException Description of Exception
     */
    public static Map<Integer, String> loadTraducTable(ConnectionManager conMan,
                                                       String broadcastSectionTableName, JComboBox combo)
          throws SQLException {
        Map<Integer, String> traduction = new HashMap<Integer, String>();
        Connection con = conMan.getConnection();
        Statement stmt = null;

        Object object = "";

        if (combo.getSelectedItem() != null) {
            object = combo.getSelectedItem();
        }

        try {
            stmt = con.createStatement();

            ResultSet rs =
                  stmt.executeQuery("select SECTION_ID, SECTION_NAME from "
                                    + broadcastSectionTableName);

            while (rs.next()) {
                Integer sectionId = rs.getInt("SECTION_ID");

                if (!object.equals(sectionId)) {
                    combo.addItem(sectionId);
                }

                traduction.put(sectionId, rs.getString("SECTION_NAME"));
            }
        }
        finally {
            conMan.releaseConnection(con, stmt);
        }

        return traduction;
    }


    /**
     * Gets the ListCellRendererComponent attribute of the SectionNameRenderer object
     *
     * @param list         Description of Parameter
     * @param value        Description of Parameter
     * @param index        Description of Parameter
     * @param isSelected   Description of Parameter
     * @param cellHasFocus Description of Parameter
     *
     * @return The ListCellRendererComponent value
     */
    public Component getListCellRendererComponent(JList list, Object value, int index,
                                                  boolean isSelected, boolean cellHasFocus) {
        return listCellRenderer.getListCellRendererComponent(list,
                                                             translateValue(value, listCellRenderer),
                                                             index,
                                                             isSelected,
                                                             cellHasFocus);
    }


    /**
     * Gets the TableCellRendererComponent attribute of the SectionNameRenderer object
     *
     * @param table      Description of Parameter
     * @param value      Description of Parameter
     * @param isSelected Description of Parameter
     * @param hasFocus   Description of Parameter
     * @param row        Description of Parameter
     * @param column     Description of Parameter
     *
     * @return The TableCellRendererComponent value
     */
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected,
                                                   boolean hasFocus,
                                                   int row,
                                                   int column) {
        return tableCellRenderer.getTableCellRendererComponent(table,
                                                               translateValue(value, tableCellRenderer),
                                                               isSelected,
                                                               hasFocus,
                                                               row,
                                                               column);
    }


    /**
     * Retourne l attribut traductTable de l object SectionNameRenderer
     *
     * @return La valeur de traductTable
     */
    public Map<Integer, String> getTranslationsMap() {
        return traductTable;
    }


    /**
     * Traduit la value contenue dans la combo en valeur à stocker en BD
     *
     * @param value La nouvelle valeur choisie
     * @param label Description of Parameter
     *
     * @return La valeur telle qu'on doit la stocker
     */
    private Object translateValue(Object value, JLabel label) {
        if (traductTable.containsKey(value)) {
            label.setForeground(Color.black);

            return traductTable.get(value);
        }
        else {
            label.setForeground(Color.red);

            return value;
        }
    }
}
