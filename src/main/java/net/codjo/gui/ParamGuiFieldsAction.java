/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.gui;
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
 * Action qui lance l'affichage de la liste des paramètrages de l'affichage
 *
 * @version $Revision: 1.3 $
 */
public class ParamGuiFieldsAction extends AbstractAction {
    private javax.swing.JDesktopPane gexPane;
    private JInternalFrame guiFieldsWindow;
    private ConnectionManager connectionManager;
    private TableHome tableHome;


    /**
     * Constructor for the ParamGuiFieldsAction object
     *
     * @param dp     le desktopPane dans lequel sera affichee la fenetre.
     * @param th     Description of Parameter
     * @param conMan Description of Parameter
     *
     * @throws IllegalArgumentException TODO
     */
    public ParamGuiFieldsAction(javax.swing.JDesktopPane dp, TableHome th,
                                ConnectionManager conMan) {
        if (dp == null) {
            throw new IllegalArgumentException();
        }
        putValue(NAME, "Affichage en liste");
        putValue(SHORT_DESCRIPTION, "Liste des paramètrages de l'affichage");
        gexPane = dp;
        connectionManager = conMan;
        tableHome = th;
    }


    /**
     * Affichage de la fenetre
     *
     * @param parm1 evenement declenchant l'affichage
     */
    public void actionPerformed(ActionEvent parm1) {
        try {
            displayGuiFieldsWindow();
            guiFieldsWindow.setVisible(true);
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
    private void createGuiFieldsWindow() throws SQLException, PersistenceException {
        guiFieldsWindow = new GuiFieldsWindow(gexPane, tableHome, connectionManager);
        gexPane.add(guiFieldsWindow);
        GuiUtil.centerWindow(guiFieldsWindow);

        guiFieldsWindow.addInternalFrameListener(new InternalFrameAdapter() {
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
                guiFieldsWindow.dispose();
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
    private void displayGuiFieldsWindow() throws SQLException, PersistenceException {
        createGuiFieldsWindow();
        guiFieldsWindow.setVisible(true);
        try {
            guiFieldsWindow.setSelected(true);
        }
        catch (java.beans.PropertyVetoException ex) {
            ex.printStackTrace();
        }
    }
}
