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
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
/**
 * Composant graphique synchronisé avec la période courante de l'application.
 *
 * @author $Author: blazart $
 * @version $Revision: 1.3 $
 *
 *
 */
public class TableComboBox extends JComboBox {
    private Reference extraTableRef = null;
    private Object[] listId;
    private DefaultComboBoxModel model;
    private TableNameRenderer renderer;
    private List step;
    private TableHome tableHome;

    /**
     * Constructor for the TableComboBox object
     */
    public TableComboBox() {}


    /**
     * Constructor for the TableComboBox object
     *
     * @param tableHome Description of Parameter
     */
    public TableComboBox(TableHome tableHome) {
        this(tableHome, (String)null);
    }


    /**
     * Constructor for the TableComboBox object
     *
     * @param tableHome Description of Parameter
     * @param step Description of Parameter
     */
    public TableComboBox(TableHome tableHome, String step) {
        this(tableHome, step, null);
    }


    /**
     * Constructor for the TableComboBox object
     *
     * @param tableHome Description of Parameter
     * @param step Description of Parameter
     * @param extraTableRef Description of Parameter
     */
    public TableComboBox(TableHome tableHome, String step, Reference extraTableRef) {
        this.extraTableRef = extraTableRef;
        this.tableHome = tableHome;
        if (step != null) {
            this.step = new ArrayList();
            this.step.add(step);
        }
        init();
    }


    /**
     * Constructor for the TableComboBox object
     *
     * @param tableHome Description of Parameter
     * @param step Description of Parameter
     */
    public TableComboBox(TableHome tableHome, List step) {
        this.tableHome = tableHome;
        if (step != null) {
            this.step = step;
        }
        init();
    }


    /**
     * Constructor for the TableComboBox object
     *
     * @param tableHome Description of Parameter
     * @param listId Description of Parameter
     */
    public TableComboBox(TableHome tableHome, Object[] listId) {
        this.tableHome = tableHome;
        if (listId != null) {
            this.listId = listId;
        }
        init();
    }

    /**
     * Gets the SelectedTable attribute of the TableComboBox object
     *
     * @return The SelectedTable value
     */
    public Table getSelectedTable() {
        Integer id = (Integer)super.getSelectedItem();
        if (id == null) {
            return null;
        }
        try {
            return tableHome.getTable(id.intValue());
        }
        catch (PersistenceException ex) {
            // Cas impossible : Le model contient obligatoirement des Id valide
            ex.printStackTrace();
            return null;
        }
    }


    /**
     * Retourne l'attribut tableHome de TableComboBox
     *
     * @return La valeur de tableHome
     */
    protected TableHome getTableHome() {
        return tableHome;
    }


    /**
     * Overview.
     *
     * @param tableId Description of Parameter
     *
     * @return Description of the Returned Value
     */
    boolean contains(int tableId) {
        return model.getIndexOf(new Integer(tableId)) >= 0;
    }


    /**
     * Reconstruit le contenu de la comboBox.
     */
    private void init() {
        try {
            renderer = new TableNameRenderer(tableHome);
            if (listId != null) {
                model = new DefaultComboBoxModel(listId);
            }
            else {
                Object[] idList = renderer.getTableIdList(step, extraTableRef);
                model = new DefaultComboBoxModel(idList);
            }
            setRenderer(renderer);
            setModel(model);
        }
        catch (PersistenceException ex) {
            ex.printStackTrace();
        }
    }
}
