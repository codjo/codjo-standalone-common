/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.gui.model;
import net.codjo.gui.toolkit.util.ErrorDialog;
import net.codjo.model.TableHome;
import net.codjo.persistent.PersistenceException;
import net.codjo.utils.ConnectionManager;
import net.codjo.utils.GuiUtil;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import javax.swing.AbstractAction;
import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
/**
 * Action qui lance l'affichage de la liste des tables
 *
 * @author $Author: blazart $
 * @version $Revision: 1.3 $
 */
public class ParamTableAction extends AbstractAction {
    private javax.swing.JDesktopPane gexPane;
    private JInternalFrame tableWindow;
    private TableHome tableHome;
    private ConnectionManager connectionManager;
    private String packageOfDetailWindow;
    private String whereClause;


    /**
     * Constructeur
     *
     * @param dp          Le desktopPane dans lequel sera affichee la fenetre.
     * @param th          Le TableHome.
     * @param conMan      Le ConnectionManager.
     * @param packageName Le nom du package où se trouve l'écran de détail.
     * @param whereClause La clause where pour l'affichage des tables propres à l'application.
     *
     * @throws IllegalArgumentException TODO
     */
    public ParamTableAction(javax.swing.JDesktopPane dp, TableHome th,
                            ConnectionManager conMan, String packageName, String whereClause) {
        if ((dp == null) || (th == null) || (conMan == null) || (packageName == null)) {
            throw new IllegalArgumentException();
        }
        putValue(NAME, "Tables");
        putValue(SHORT_DESCRIPTION, "Liste des tables");
        gexPane = dp;
        tableHome = th;
        connectionManager = conMan;
        packageOfDetailWindow = packageName;
        this.whereClause = whereClause;
    }


    /**
     * Constructeur sans clause where.
     *
     * @param dp          Le desktopPane dans lequel sera affichee la fenetre.
     * @param th          Le TableHome.
     * @param conMan      Le ConnectionManager.
     * @param packageName Le nom du package où se trouve l'écran de détail.
     */
    public ParamTableAction(javax.swing.JDesktopPane dp, TableHome th,
                            ConnectionManager conMan, String packageName) {
        this(dp, th, conMan, packageName, "");
        putValue(NAME, "Tables");
        putValue(SHORT_DESCRIPTION, "Liste des tables");
    }


    /**
     * Affichage de la fenetre
     *
     * @param parm1 evenement declenchant l'affichage
     */
    public void actionPerformed(ActionEvent parm1) {
        try {
            displayTableWindow();
            tableWindow.setVisible(true);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            ErrorDialog.show(gexPane, "Impossible d'afficher la fenêtre: ", ex);
        }
    }


    /**
     * Creation de la fenetre
     *
     * @throws SQLException         Description of Exception
     * @throws PersistenceException Description of Exception
     */
    private void createTableWindow() throws SQLException, PersistenceException {
        tableWindow =
              new TableWindow(gexPane, tableHome, connectionManager, packageOfDetailWindow,
                              whereClause);
        gexPane.add(tableWindow);
        GuiUtil.centerWindow(tableWindow);

        tableWindow.addInternalFrameListener(new InternalFrameAdapter() {
            /**
             * Desactive l'action lors de l'ouverture de la fenetre.
             *
             * @param evt -
             */
            public void internalFrameActivated(InternalFrameEvent evt) {
                setEnabled(false);
            }


            /**
             * Active l'action à la fermeture de la fenetre.
             *
             * @param evt -
             */
            public void internalFrameClosing(InternalFrameEvent evt) {
                setEnabled(true);
                tableWindow.dispose();
            }
        });
    }


    /**
     * Affiche la fenetre.
     *
     * <p> Si la fenetre n'a pas deja ete cree, la methode en créé une. </p>
     *
     * @throws SQLException         Description of Exception
     * @throws PersistenceException Description of Exception
     */
    private void displayTableWindow() throws SQLException, PersistenceException {
        createTableWindow();
        tableWindow.setVisible(true);
        try {
            tableWindow.setSelected(true);
        }
        catch (java.beans.PropertyVetoException ex) {
            ex.printStackTrace();
        }
    }
}
