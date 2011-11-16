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
 * Action qui lance l'affichage de la liste des noms des champs
 *
 * @version $Revision: 1.3 $
 */
public class ParamFieldLabelAction extends AbstractAction {
    private javax.swing.JDesktopPane gexPane;
    private JInternalFrame fieldLabelWindow;
    private TableHome tableHome;
    private ConnectionManager connectionManager;


    /**
     * Constructor for the ParamFieldLabelAction object
     *
     * @param dp     le desktopPane dans lequel sera affichee la fenetre.
     * @param th     Description of Parameter
     * @param conMan Description of Parameter
     *
     * @throws IllegalArgumentException TODO
     */
    public ParamFieldLabelAction(javax.swing.JDesktopPane dp, TableHome th,
                                 ConnectionManager conMan) {
        if (dp == null) {
            throw new IllegalArgumentException();
        }
        putValue(NAME, "Noms des champs");
        putValue(SHORT_DESCRIPTION, "Liste des noms des champs");
        gexPane = dp;
        tableHome = th;
        connectionManager = conMan;
    }


    /**
     * Affichage de la fenetre
     *
     * @param parm1 evenement declenchant l'affichage
     */
    public void actionPerformed(ActionEvent parm1) {
        try {
            displayFieldLabelWindow();
            fieldLabelWindow.setVisible(true);
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
    private void createFieldLabelWindow() throws SQLException, PersistenceException {
        fieldLabelWindow = new FieldLabelWindow(gexPane, tableHome, connectionManager);
        gexPane.add(fieldLabelWindow);
        GuiUtil.centerWindow(fieldLabelWindow);

        fieldLabelWindow.addInternalFrameListener(new InternalFrameAdapter() {
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
                fieldLabelWindow.dispose();
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
    private void displayFieldLabelWindow() throws SQLException, PersistenceException {
        createFieldLabelWindow();
        fieldLabelWindow.setVisible(true);
        try {
            fieldLabelWindow.setSelected(true);
        }
        catch (java.beans.PropertyVetoException ex) {
            ex.printStackTrace();
        }
    }
}
