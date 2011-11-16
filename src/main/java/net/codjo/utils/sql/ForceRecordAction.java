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
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
/**
 * Action permettant de forcer le champ RECORD_ACCRESS à 0 de l'enregistrement sélectionné de la table
 * courante (BO_STOCK, BO_TRANSACTION et BO_SECURITY)
 */
public class ForceRecordAction extends AbstractDbAction {
    private String confirmMsg =
          "Etes-vous sûr de vouloir restaurer les enregistrements ?";


    /**
     * Constructor for the ForceRecordAction object
     */
    public ForceRecordAction() {
        putValue(NAME, "Restaurer");
        putValue(SHORT_DESCRIPTION, "Restaurer l'enregitrement");
        putValue(SMALL_ICON, UIManager.getIcon("ListTable.force"));
    }


    /**
     * Constructor for the ForceRecordAction object
     *
     * @param gt Description of the Parameter
     *
     * @throws IllegalArgumentException TODO
     */
    public ForceRecordAction(GenericTable gt) {
        super(null, null, gt);
        if (gt == null) {
            throw new IllegalArgumentException();
        }
        putValue(NAME, "Restaurer");
        putValue(SHORT_DESCRIPTION, "Restaurer l'enregitrement");
        putValue(SMALL_ICON, UIManager.getIcon("ListTable.force"));
        setEnabled(false);

        ListSelectionModel rowSM = getGenericTable().getSelectionModel();
        rowSM.addListSelectionListener(new SelectionListener());
    }


    /**
     * Sets the confirmMsg attribute of the ForceRecordAction object
     *
     * @param msg The new confirmMsg value
     */
    public void setConfirmMsg(String msg) {
        confirmMsg = msg;
    }


    /**
     * Description of the Method
     *
     * @param evt Description of Parameter
     */
    public void actionPerformed(ActionEvent evt) {
        fireActionEvent(evt);
        try {
            int result =
                  JOptionPane.showConfirmDialog(getDesktopPane(), confirmMsg,
                                                "Confirmer la restauration ", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.CANCEL_OPTION) {
                return;
            }
            execute();
        }
        catch (SQLException ex) {
            ex.printStackTrace();
            ErrorDialog.show(getGenericTable(),
                             "Impossible de forcer le champ 'RECORD_ACCESS' de l'enregistrement",
                             ex.getLocalizedMessage());
        }
    }


    /**
     * Gets the activatable attribute of the ForceRecordAction object
     *
     * @return The activatable value
     */
    private boolean isActivatable() {
        ListSelectionModel lsm = getGenericTable().getSelectionModel();
        return !lsm.isSelectionEmpty();
    }


    /**
     * Execute la requête de forçage
     *
     * @throws SQLException -
     */
    private void execute() throws SQLException {
        Connection con = getConnection();
        try {
            for (Iterator iter = getPrimaryKeys().iterator(); iter.hasNext();) {
                Map keyMap = (Map)iter.next();
                QueryHelper queryHelper = buildQueryHelper(con, keyMap);
                queryHelper.doForce();
            }
            fireDbChange(DbChangeEvent.FORCE_EVENT, getGenericTablePK());
            getGenericTable().refreshData();
        }
        finally {
            releaseConnection(con);
        }
    }


    /**
     * Construit la requête de forçage.
     *
     * @param con    une connection valide
     * @param keyMap Description of Parameter
     *
     * @return Description of the Returned Value
     *
     * @throws SQLException Erreur SQL
     */
    private QueryHelper buildQueryHelper(Connection con, Map keyMap)
          throws SQLException {
        SQLFieldList selector = new SQLFieldList();
        SQLFieldList allcols = new SQLFieldList();

        Map pk = keyMap;
        Map allColumns = getGenericTable().getTable().getAllColumns();

        for (Iterator iter = allColumns.keySet().iterator(); iter.hasNext();) {
            String dbField = (String)iter.next();
            Integer sqlType = (Integer)allColumns.get(dbField);
            allcols.addField(dbField, sqlType.intValue());
        }

        for (Iterator iter = pk.keySet().iterator(); iter.hasNext();) {
            String dbField = (String)iter.next();
            Integer sqlType = (Integer)allColumns.get(dbField);
            selector.addField(dbField, sqlType.intValue());
            selector.setFieldValue(dbField, pk.get(dbField));
        }

        QueryHelper queryHelper =
              new QueryHelper(getDbTableName(), con, allcols, selector);

        return queryHelper;
    }


    /**
     * Active / Desactive l'action en fonction de la selection sur la table
     *
     * @author $Author: marcona $
     * @version $Revision: 1.4 $
     */
    private class SelectionListener implements ListSelectionListener {
        /**
         * DOCUMENT ME!
         *
         * @param e Description of Parameter
         */
        public void valueChanged(ListSelectionEvent e) {
            if (e.getValueIsAdjusting()) {
                return;
            }
            setEnabled(isActivatable());
        }
    }
}
