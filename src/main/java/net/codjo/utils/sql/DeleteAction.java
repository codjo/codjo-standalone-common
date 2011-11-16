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
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
/**
 * Permet de supprimer l'enregistrement sélectionné de la table courante
 *
 * @author $Author: blazart $
 * @version $Revision: 1.3 $
 */
public class DeleteAction extends AbstractDbAction {
    private String confirmMsg = "Etes-vous sûr de vouloir supprimer ?";


    /**
     * Constructor for the DeleteAction object
     */
    public DeleteAction(JOptionPane optionPane) {
        putValue(NAME, "Supprimer");
        putValue(SHORT_DESCRIPTION, "Suppression de l'enregistrement");
        putValue(SMALL_ICON, UIManager.getIcon("ListTable.delete"));
    }


    /**
     * Constructor for the DeleteAction object
     *
     * @param gt Table générique source
     */
    public DeleteAction(GenericTable gt) {
        super(null, null, gt);
        if (gt == null) {
            throw new IllegalArgumentException();
        }
        putValue(NAME, "Supprimer");
        putValue(SHORT_DESCRIPTION, "Suppression de l'enregistrement");
        putValue(SMALL_ICON, UIManager.getIcon("ListTable.delete"));
        setEnabled(false);

        ListSelectionModel rowSM = getGenericTable().getSelectionModel();
        rowSM.addListSelectionListener(new SelectionListener());
    }


    /**
     * Definit un nouveau message de confirmation pour la suppression.
     *
     * @param msg Le message
     */
    public void setConfirmMsg(String msg) {
        confirmMsg = msg;
    }


    /**
     * Lance la suppression de l'enregistrement sélectionné.
     *
     * @param evt L'événement de l'action
     */
    public void actionPerformed(ActionEvent evt) {
        fireActionEvent(evt);
        try {
            int result =
                  JOptionPane.showConfirmDialog(getDesktopPane(), confirmMsg,
                                                "Confirmer la suppression", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.CANCEL_OPTION) {
                return;
            }
            execute();
        }
        catch (SQLException ex) {
            ex.printStackTrace();
            ErrorDialog.show(getGenericTable(),
                             "Impossible de supprimer l'enregistrement", ex.getLocalizedMessage());
        }
    }


    /**
     * Indique si l'action est potentiellement activable.
     *
     * @return 'true' si elle peut etre activee
     */
    private boolean isActivatable() {
        ListSelectionModel lsm = getGenericTable().getSelectionModel();
        return !lsm.isSelectionEmpty();
    }


    /**
     * Execute la requête de suppression
     *
     * @throws SQLException -
     */
    protected void execute() throws SQLException {
        Connection con = getConnection();
        try {
            for (Object o : getPrimaryKeys()) {
                Map keyMap = (Map)o;
                QueryHelper queryHelper = buildQueryHelper(con, keyMap);
                queryHelper.doDelete();
            }
            fireDbChange(DbChangeEvent.DELETE_EVENT, getGenericTablePK());
            getGenericTable().refreshData();
        }
        finally {
            releaseConnection(con);
        }
    }


    /**
     * Construit la requête de delete.
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

        Map allColumns = getGenericTable().getTable().getAllColumns();

        for (Object o : allColumns.keySet()) {
            String dbField = (String)o;
            Integer sqlType = (Integer)allColumns.get(dbField);
            allcols.addField(dbField, sqlType);
        }

        for (Object o : keyMap.keySet()) {
            String dbField = (String)o;
            Integer sqlType = (Integer)allColumns.get(dbField);
            selector.addField(dbField, sqlType);
            selector.setFieldValue(dbField, keyMap.get(dbField));
        }

        return new QueryHelper(getDbTableName(), con, allcols, selector);
    }


    /**
     * Active / Desactive l'action en fonction de la selection sur la table
     *
     * @author $Author: blazart $
     * @version $Revision: 1.3 $
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
