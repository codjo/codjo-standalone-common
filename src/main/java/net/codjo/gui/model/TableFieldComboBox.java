/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.gui.model;
import net.codjo.gui.renderer.FieldLabelComparator;
import net.codjo.gui.renderer.FieldNameRenderer;
import net.codjo.model.Table;
import net.codjo.utils.ConnectionManager;
import java.sql.SQLException;
import java.util.Arrays;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
/**
 * Composant graphique affichant les nom de colonnes d'une table.
 *
 * @author $Author: blazart $
 * @version $Revision: 1.6 $
 *
 *
 */
public class TableFieldComboBox extends JComboBox {
    /**
                                                                                                 */
    public TableFieldComboBox() {}

    /**
     * Reconstruit le contenu de la comboBox.
     *
     * @param table Table
     * @param conMan Le ConnectionManager
     *
     * @exception SQLException Description of Exception
     */
    public void init(Table table, ConnectionManager conMan)
            throws SQLException {
        init(table.getDBTableName(), table.getAllColumns().keySet().toArray(), conMan);
    }


    /**
     * Description of the Method
     *
     * @param tableName Description of the Parameter
     * @param fields Description of the Parameter
     * @param conMan Description of the Parameter
     *
     * @exception SQLException Description of the Exception
     */
    public void init(String tableName, Object[] fields, ConnectionManager conMan)
            throws SQLException {
        Object selectedItem = getSelectedItem();
        FieldNameRenderer fd = new FieldNameRenderer(conMan, tableName);

        Arrays.sort(fields, new FieldLabelComparator(fd, tableName));
        setModel(new DefaultComboBoxModel(fields));

        this.setRenderer(fd);
        if (selectedItem != null) {
            setSelectedItem(selectedItem);
        }
    }
}
