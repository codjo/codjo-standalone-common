/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.gui.renderer;
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
 * Renderer permettant d'afficher le libellé du champ au lieu de son nom DB
 *
 * @version : $Revision: 1.4 $
 */
public class FieldNameRenderer implements ListCellRenderer, TableCellRenderer {
    private DefaultListCellRenderer listCellRenderer = new DefaultListCellRenderer();
    private DefaultTableCellRenderer tableCellRenderer = new DefaultTableCellRenderer();
    private Map traductTable = new HashMap();

    /**
     * Constructor for the FieldNameRenderer object
     *
     * @param con Description of Parameter
     * @param tableDest Description of Parameter
     * @param combo Description of Parameter
     *
     * @exception SQLException Description of Exception
     * @throws IllegalArgumentException TODO
     */
    public FieldNameRenderer(Connection con, String tableDest, JComboBox combo)
            throws SQLException {
        if (con == null) {
            throw new IllegalArgumentException("FieldNameRenderer : Paramètre invalide");
        }
        traductTable = FieldNameRenderer.loadTraducTable(con, tableDest, combo);

        //fillTraductTable(conMan,tableDest,combo);
    }


    /**
     * Constructor for the FieldNameRenderer object
     *
     * @param con Description of Parameter
     * @param tableName Description of Parameter
     *
     * @exception SQLException Description of Exception
     */
    public FieldNameRenderer(Connection con, String tableName)
            throws SQLException {
        this(con, tableName, null);
    }


    /**
     * Constructor for the FieldNameRenderer object
     *
     * @param conMan Connection manager
     * @param tableDest la table dest
     * @param combo heuu la combo ?
     *
     * @exception SQLException oups
     * @throws IllegalArgumentException TODO
     */
    public FieldNameRenderer(ConnectionManager conMan, String tableDest, JComboBox combo)
            throws SQLException {
        if (conMan == null) {
            throw new IllegalArgumentException("FieldNameRenderer : Paramètre invalide");
        }
        traductTable = FieldNameRenderer.loadTraducTable(conMan, tableDest, combo);
        //fillTraductTable(conMan,tableDest,combo);
    }


    /**
     * Constructor for the FieldNameRenderer object
     *
     * @param conMan Connection manager
     * @param tableName la table
     *
     * @exception SQLException oups
     */
    public FieldNameRenderer(ConnectionManager conMan, String tableName)
            throws SQLException {
        this(conMan, tableName, null);
    }

    /**
     * Overview.
     *
     * @param conMan Description of Parameter
     * @param tableDest Description of Parameter
     * @param combo Description of Parameter
     *
     * @return Description of the Returned Value
     *
     * @exception SQLException Description of Exception
     */
    public static HashMap loadTraducTable(ConnectionManager conMan, String tableDest,
        JComboBox combo) throws SQLException {
        Connection con = conMan.getConnection();
        try {
            return FieldNameRenderer.loadTraducTable(con, tableDest, combo);
        }
        finally {
            conMan.releaseConnection(con);
        }
    }


    /**
     * Description of the Method
     *
     * @param conMan Description of Parameter
     * @param tableDest Description of Parameter
     *
     * @return Description of the Returned Value
     *
     * @exception SQLException Description of Exception
     */
    public static HashMap loadTraducTable(ConnectionManager conMan, String tableDest)
            throws SQLException {
        return FieldNameRenderer.loadTraducTable(conMan, tableDest, null);
    }


    /**
     * Overview.
     *
     * @param con Description of Parameter
     * @param tableDest Description of Parameter
     * @param combo Description of Parameter
     *
     * @return Description of the Returned Value
     *
     * @exception SQLException Description of Exception
     */
    public static HashMap loadTraducTable(Connection con, String tableDest,
        JComboBox combo) throws SQLException {
        HashMap traduction = new HashMap();
        Statement stmt = con.createStatement();
        try {
            ResultSet rs =
                stmt.executeQuery("select DB_FIELD_NAME, FIELD_LABEL from PM_FIELD_LABEL"
                    + " where DB_TABLE_NAME='" + tableDest + "' ORDER BY FIELD_LABEL");
            while (rs.next()) {
                if (combo != null) {
                    combo.addItem(rs.getString("DB_FIELD_NAME"));
                }
                traduction.put(rs.getString("DB_FIELD_NAME"), rs.getString("FIELD_LABEL"));
            }
            if (combo != null) {
                combo.addItem("");
            }

//            traduction.put("", "???");
        }
        finally {
            stmt.close();
        }
        return traduction;
    }


    /**
     * Gets the ListCellRendererComponent attribute of the FieldNameRenderer object
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
        return listCellRenderer.getListCellRendererComponent(list,
            translateValue(value, listCellRenderer), index, isSelected, cellHasFocus);
    }


    /**
     * Gets the TableCellRendererComponent attribute of the FieldNameRenderer object
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
            translateValue(value, tableCellRenderer), isSelected, hasFocus, row, column);
    }


    /**
     * Retourne l attribut traductTable de l object FieldNameRenderer
     *
     * @return La valeur de traductTable
     */
    public Map getTranslationsMap() {
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
    private String translateValue(Object value, JLabel label) {
        if (traductTable.containsKey(value)) {
            label.setForeground(Color.black);
            return (String)traductTable.get(value);
        }
        else {
            label.setForeground(Color.red);
            return (String)value;
        }
    }
}
