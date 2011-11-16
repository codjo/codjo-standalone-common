/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.utils.sql;
import net.codjo.model.Table;
import net.codjo.utils.ConnectionManager;
import net.codjo.utils.sql.event.DbChangeEvent;
import net.codjo.utils.sql.event.DbChangeListener;
import java.awt.AWTEventMulticaster;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TooManyListenersException;
import javax.swing.AbstractAction;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
/**
 * Classe abstraite pour les actions BD.
 *
 * <p> Une action est connectee avec sa table source. </p>
 *
 * <p> Une action gere deux types d'evenements : (1) ActionEvent, cet evenement est declenche avant que
 * l'action soit execute, (2) DbChangeEvent, cet evenement est declenche quand l'action s'est termine
 * correctement. </p>
 *
 * @author $Author: spinae $
 * @version $Revision: 1.2 $
 */
abstract class AbstractDbAction extends AbstractAction {
    private GenericTable genericTable;
    private JInternalFrame windowTable;
    private JDesktopPane gexPane;
    private DbChangeListener dbChangeListener;
    private Map genericTablePK;

    // Connection optionnelle : precisee pour faire des Transactions
    private Connection connection;
    private ActionListener actionListenerManager = null;
    private boolean forcedDisabled = false;


    /**
     * Constructor for the AbstractDbAction object
     */
    AbstractDbAction() {
        setEnabled(false);
    }


    /**
     * Constructor for the AbstractDbAction object
     *
     * @param dp  DesktopPane
     * @param frm JInternalFrame contenant la table générique source
     * @param gt  Table générique source
     */
    AbstractDbAction(JDesktopPane dp, JInternalFrame frm, GenericTable gt) {
        gexPane = dp;
        genericTable = gt;
        windowTable = frm;
    }


    /**
     * Sets the Connection attribute of the AbstractDbAction object
     *
     * @param con The new Connection value
     */
    public void setConnection(Connection con) {
        connection = con;
    }


    /**
     * Ajoute un listener qui ecoute le declenchement de l'action.
     *
     * @param l
     */
    public synchronized void addActionListener(ActionListener l) {
        actionListenerManager = AWTEventMulticaster.add(actionListenerManager, l);
    }


    /**
     * Enleve un listener.
     *
     * @param l
     */
    public synchronized void removeActionListener(ActionListener l) {
        actionListenerManager = AWTEventMulticaster.remove(actionListenerManager, l);
    }


    /**
     * Ajoute un ecouteur sur les modifications BD.
     *
     * @param l Le listener
     *
     * @throws TooManyListenersException Si il existe deja un ecouteur
     */
    public void addDbChangeListener(DbChangeListener l)
          throws TooManyListenersException {
        if (dbChangeListener != null) {
            throw new TooManyListenersException();
        }
        dbChangeListener = l;
    }


    /**
     * Enleve un ecouteur sur les modifications BD.
     *
     * @param l Le listener
     */
    public void removeDbChangeListener(DbChangeListener l) {
        if (dbChangeListener == l) {
            dbChangeListener = null;
        }
    }


    /**
     * Retourne le desktopPane
     *
     * @return Le JDesktopPane
     */
    protected JDesktopPane getDesktopPane() {
        return gexPane;
    }


    /**
     * Retourne le nom physique de la table
     *
     * @return The DbTableName value
     */
    protected String getDbTableName() {
        return genericTable.getTable().getDBTableName();
    }


    /**
     * Retourne l'objet Table
     *
     * @return La Table
     */
    protected Table getTable() {
        return genericTable.getTable();
    }


    /**
     * Retourne la table générique source.
     *
     * @return La générique table
     */
    protected GenericTable getGenericTable() {
        return genericTable;
    }


    /**
     * Retourne la fenetre contenant la genericTable.
     *
     * @return Une JInternalFrame
     */
    protected JInternalFrame getWindowTable() {
        return windowTable;
    }


    /**
     * Retourne une connection
     *
     * @return The Connection value
     *
     * @throws SQLException Description of Exception
     */
    protected Connection getConnection() throws SQLException {
        if (connection != null) {
            return connection;
        }
        else {
            return getConnectionManager().getConnection();
        }
    }


    /**
     * Retourne la liste des clés primaires definie dans la table source.
     *
     * @return Map de nom de colonne / valeur
     */
    protected Map getGenericTablePK() {
        return genericTablePK;
    }


    /**
     * Retourne la liste de Maps (DbKeyName, KeyValue) correspondant aux lignes sélectionnées sur la
     * GenericTable.
     *
     * @return La List
     */
    protected List getPrimaryKeys() {
        List listKeys = new ArrayList();
        int[] lineNumbers = genericTable.getSelectedRows();
        for (int i = 0; i < lineNumbers.length; i++) {
            listKeys.add(genericTable.getKey(lineNumbers[i]));
        }
        return listKeys;
    }


    /**
     * Declenche un evenement ActionEvent. Cette methode doit etre appele avant d'executer l'action.
     *
     * @param evt Description of Parameter
     */
    protected final void fireActionEvent(ActionEvent evt) {
        if (actionListenerManager != null) {
            actionListenerManager.actionPerformed(evt);
        }

        if (genericTable.getSelectedRow() >= 0) {
            setGenericTablePK(genericTable.getKey(genericTable.getSelectedRow()));
        }
        else {
            setGenericTablePK(null);
        }
    }


    /**
     * Remets la connection dans le Pool.
     *
     * @param con
     *
     * @throws SQLException
     */
    protected void releaseConnection(Connection con)
          throws SQLException {
        if (con != connection) {
            getConnectionManager().releaseConnection(con);
        }
    }


    /**
     * Averti le Listener d'un changement en BD. La clef primaire sous forme de Map (nom de colonne / valeur)
     *
     * @param evtType Description of Parameter
     * @param pk      Description of Parameter
     *
     * @todo a finir (evt construit fixe >
     */
    protected void fireDbChange(int evtType, Map pk) {
        synchronized (this) {
            if (dbChangeListener != null) {
                DbChangeEvent evt = new DbChangeEvent(this, evtType, pk);
                dbChangeListener.succeededChange(evt);
            }
        }
    }


    /**
     * Positionne la liste des clés primaires.
     *
     * @param pk The new GenericTablePK value
     */
    void setGenericTablePK(Map pk) {
        genericTablePK = pk;
    }


    /**
     * Retourne le connection manager
     *
     * @return -
     */
    private ConnectionManager getConnectionManager() {
        return Dependency.getConnectionManager();
    }


    public void setForcedDisabled(boolean forcedDisabled) {
        this.forcedDisabled = forcedDisabled;
        setEnabled(isEnabled());
    }


    @Override
    public void setEnabled(boolean newValue) {
        if (forcedDisabled) {
            super.setEnabled(false);
        }
        else {
            super.setEnabled(newValue);
        }
    }
}
