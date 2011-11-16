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
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
/**
 * Duplique l'enregistrement sélectionné sur la table source.
 *
 * @author $Author: spinae $
 * @version $Revision: 1.2 $
 */
class DuplicateAction extends AbstractDbAction {
    SQLFieldList allcols = new SQLFieldList();


    /**
     * Constructor for the DuplicateAction object
     */
    public DuplicateAction() {
        putValue(NAME, "Dupliquer");
        putValue(SHORT_DESCRIPTION, "Duplication de l'enregistrement");
        putValue(SMALL_ICON, UIManager.getIcon("ListTable.duplicate"));
    }


    /**
     * Constructor for the DuplicateAction object
     *
     * @param gt GUI Table source.
     *
     * @throws IllegalArgumentException TODO
     */
    public DuplicateAction(GenericTable gt) {
        super(null, null, gt);
        if (gt == null) {
            throw new IllegalArgumentException();
        }
        putValue(NAME, "Dupliquer");
        putValue(SHORT_DESCRIPTION, "Duplication de l'enregistrement");
        putValue(SMALL_ICON, UIManager.getIcon("ListTable.duplicate"));

        setEnabled(false);
        ListSelectionModel rowSM = getGenericTable().getSelectionModel();
        rowSM.addListSelectionListener(new SelectionListener());
    }


    /**
     * Lance la duplication de l'enregistrement sélectionné.
     *
     * @param evt L'événement de l'action
     */
    public void actionPerformed(ActionEvent evt) {
        fireActionEvent(evt);
        try {
            execute();
        }
        catch (SQLException ex) {
            ex.printStackTrace();
            ErrorDialog.show(getGenericTable(),
                             "Impossible de dupliquer l'enregistrement", ex.getLocalizedMessage());
        }
    }


    /**
     * Indique si l'action est potentiellement activable.
     *
     * @return 'true' si elle peut etre activee
     */
    private boolean isActivatable() {
        ListSelectionModel lsm = getGenericTable().getSelectionModel();
        if (lsm.isSelectionEmpty() == false) {
            return true;
        }
        return false;
    }


    /**
     * Execute les requêtes de duplication (select des valeurs à supprimer + sauvegarde dans un nouvel
     * enregistrement).
     *
     * @throws SQLException -
     */
    private void execute() throws SQLException {
        Connection con = getConnection();
        try {
            for (Iterator iter = getPrimaryKeys().iterator(); iter.hasNext();) {
                Map keyMap = (Map)iter.next();
                QueryHelper queryHelper = buildQueryHelper(con, keyMap);

                ResultSet rs = queryHelper.doSelect();

                fillQueryHelper(allcols, rs, queryHelper);

                queryHelper.doInsert();
            }
            fireDbChange(DbChangeEvent.DUPLICATE_EVENT, null);

            getGenericTable().refreshData();
        }
        finally {
            releaseConnection(con);
        }
    }


    /**
     * Construit la requête d'insert.
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
        Map pk = keyMap;
        Map allColumns = getTable().getAllColumns();

        for (Iterator iter = allColumns.keySet().iterator(); iter.hasNext();) {
            String fieldName = (String)iter.next();
            if (pk.containsKey(fieldName) == false) {
                Integer sqlType = (Integer)allColumns.get(fieldName);
                allcols.addField(fieldName, sqlType.intValue());
            }
        }

        SQLFieldList selector = new SQLFieldList();
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
     * Remplit le QueryHelper avec les valeurs des champs.
     *
     * @param columns Liste de colonnes a remplir dans le query helper.
     * @param res     Le ResultSet de la requête de selection.
     * @param qh      Le QueryHelper a remplir.
     *
     * @throws SQLException             Description of Exception
     * @throws IllegalArgumentException TODO
     */
    private void fillQueryHelper(SQLFieldList columns, ResultSet res, QueryHelper qh)
          throws SQLException {
        Iterator iter = columns.fieldNames();

        if (res.next() == false) {
            throw new IllegalArgumentException("Manque une ligne");
        }

        while (iter.hasNext()) {
            String columnName = (String)iter.next();
            Object value = res.getObject(columnName);
            qh.setInsertValue(columnName, value);
        }
    }


    /**
     * Active / Desactive l'action en fonction de la selection sur la table
     *
     * @author $Author: spinae $
     * @version $Revision: 1.2 $
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
