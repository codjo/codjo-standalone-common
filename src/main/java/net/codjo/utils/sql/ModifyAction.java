/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.utils.sql;
import net.codjo.gui.toolkit.util.ErrorDialog;
import net.codjo.utils.QueryHelper;
import net.codjo.utils.SQLFieldList;
import net.codjo.utils.sql.event.DbChangeEvent;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
/**
 * Affiche l'ecran page correspondant à l'enregistrement sélectionné sur la table source afin de le modifier.
 *
 * @author $Author: spinae $
 * @version $Revision: 1.2 $
 */
public class ModifyAction extends AbstractDetailAction implements ListSelectionListener {
    private SQLFieldList editableFields;


    /**
     * Constructor for the AddAction object
     */
    public ModifyAction() {
        actionType = "Modify";
        putValue(NAME, "Modifier");
        putValue(SHORT_DESCRIPTION, "Modification d'un enregistrement");
        putValue(SMALL_ICON, UIManager.getIcon("ListTable.edit"));
    }


    /**
     * Constructor for the AddAction object
     *
     * @param dp       DesktopPane
     * @param frm      Fenêtre source (écran liste)
     * @param gt       La table source
     * @param packName Nom du package dans lequel se trouve la classe de l'écran détail
     *
     * @throws IllegalArgumentException TODO
     */
    public ModifyAction(JDesktopPane dp, JInternalFrame frm, GenericTable gt,
                        String packName) {
        super(dp, frm, gt, packName);
        actionType = "Modify";
        if (dp == null || gt == null || frm == null || packName == null) {
            throw new IllegalArgumentException();
        }
        putValue(NAME, "Editer");
        putValue(SHORT_DESCRIPTION, "Modification d'un enregistrement");
        putValue(SMALL_ICON, UIManager.getIcon("ListTable.edit"));

        setEnabled(false);
        ListSelectionModel rowSM = getGenericTable().getSelectionModel();
        rowSM.addListSelectionListener(this);
    }


    /**
     * Affiche la fenêtre detail correspondant à l'enregistrement sélectionné. (Appelée par le bouton Modifier
     * ou par le menu contextuel).
     *
     * @param event -
     */
    public void actionPerformed(ActionEvent event) {
        super.actionPerformed(event);
        loadData();
    }


    /**
     * Active / Desactive l'action en fonction de la selection sur la table
     *
     * @param e Description of Parameter
     */
    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) {
            return;
        }
        setEnabled(isActivatable());
    }


    /**
     * Description of the Method
     *
     * @param ev Description of the Parameter
     */
    protected void previousButton_actionPerformed(ActionEvent ev) {
        super.previousButton_actionPerformed(ev);
        loadData();
    }


    /**
     * Description of the Method
     *
     * @param ev Description of the Parameter
     */
    protected void nextButton_actionPerformed(ActionEvent ev) {
        super.nextButton_actionPerformed(ev);
        loadData();
    }


    /**
     * Applique les modifications et recharge l'ecran.
     *
     * @throws Exception Description of Exception
     */
    protected void executeApply() throws Exception {
        super.executeApply();
        loadData();
    }


    /**
     * Insere les valeurs dans la table
     *
     * @throws Exception Impossible d'inserer la nouvelle ligne
     */
    protected void executeAction() throws Exception {
        Connection con = getConnection();
        try {
            QueryHelper queryHelper = buildQueryHelper(con);

            con.setAutoCommit(false);

            detailWindow.fillQueryHelper(editableFields, queryHelper);
            queryHelper.doUpdate();

            detailWindow.saveLinks(getGenericTablePK(), con);

            con.commit();
        }
        catch (Exception ex) {
            con.rollback();
            throw ex;
        }
        finally {
            con.setAutoCommit(true);
            releaseConnection(con);
        }
        fireDbChange(DbChangeEvent.MODIFY_EVENT, getGenericTablePK());
    }


    /**
     * Charge les data de la base vers la fenetre.
     */
    void loadData() {
        Connection con = null;
        try {
            con = getConnection();
            QueryHelper queryHelper = buildQueryHelper(con);
            ResultSet rs = queryHelper.doSelect();
            detailWindow.fillComponent(editableFields, rs);
        }
        catch (SQLException ex) {
            ex.printStackTrace();
            ErrorDialog.show(getGenericTable(), "Edition impossible", ex);
        }
        finally {
            try {
                releaseConnection(con);
            }
            catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }


    /**
     * Indique si l'action est potentiellement activable.
     *
     * @return 'true' si elle peut etre activee
     */
    private boolean isActivatable() {
        ListSelectionModel lsm = getGenericTable().getSelectionModel();
        if (lsm.isSelectionEmpty() == false
            && getGenericTable().getSelectedRowCount() == 1
            && getWindowClass() != null) {
            return true;
        }
        return false;
    }


    /**
     * Construit la requête de sauvegarde suite aux modifications apportées à l'enregistrement.
     *
     * @param con une connection valide
     *
     * @return Description of the Returned Value
     *
     * @throws SQLException Erreur SQL
     */
    private QueryHelper buildQueryHelper(Connection con)
          throws SQLException {
        SQLFieldList selector = new SQLFieldList();
        Map pk = getGenericTablePK();
        Map allColumns = getGenericTable().getTable().getAllColumns();

        for (Iterator iter = pk.keySet().iterator(); iter.hasNext();) {
            String dbField = (String)iter.next();
            Integer sqlType = (Integer)allColumns.get(dbField);
            selector.addField(dbField, sqlType.intValue());
            selector.setFieldValue(dbField, pk.get(dbField));
        }

        editableFields = getEditableFields(detailWindow.getListOfComponents());

        return new QueryHelper(getDbTableName(), con, editableFields, selector);
    }
}
