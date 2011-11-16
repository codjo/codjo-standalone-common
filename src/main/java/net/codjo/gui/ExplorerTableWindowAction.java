/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.gui;
import net.codjo.gui.toolkit.util.ErrorDialog;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JInternalFrame;
import javax.swing.UIManager;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
/**
 * Action qui lance un explorateur des tables.
 *
 * @version $Revision: 1.3 $
 */
public class ExplorerTableWindowAction extends AbstractAction {
    private javax.swing.JDesktopPane gexPane;
    private JInternalFrame explorerTableWindow;


    /**
     * Constructor for the ExplorerTableWindowAction object
     *
     * @param dp DesktopPane principal
     *
     * @throws IllegalArgumentException TODO
     */
    public ExplorerTableWindowAction(javax.swing.JDesktopPane dp) {
        if (dp == null) {
            throw new IllegalArgumentException();
        }
        putValue(NAME, "Explorateur des tables");
        putValue(SHORT_DESCRIPTION, "Explorateur des tables");
        putValue(SMALL_ICON, UIManager.getIcon("TableExplorer.open"));
        gexPane = dp;
    }


    /**
     * Ouverture de la fenetre.
     *
     * @param parm1 event
     */
    public void actionPerformed(ActionEvent parm1) {
        try {
            displayExplorerTableWindow();
            explorerTableWindow.setVisible(true);
        }
        catch (Exception error) {
            error.printStackTrace();

            ErrorDialog.show(gexPane, "Impossible d'afficher la fenêtre: ",
                             error.getLocalizedMessage());
        }
    }


    /**
     * Creation de la fenetre.
     *
     * @throws Exception Description of Exception
     */
    private void createExplorerTableWindow() throws Exception {
        explorerTableWindow = new ExplorerTableWindow();
        gexPane.add(explorerTableWindow);

        explorerTableWindow.addInternalFrameListener(new InternalFrameAdapter() {
            /**
             * Overview.
             *
             * @param evt Description of Parameter
             */
            public void internalFrameOpened(InternalFrameEvent evt) {
                setEnabled(false);
            }


            /**
             * Overview.
             *
             * @param evt Description of Parameter
             */
            public void internalFrameClosing(InternalFrameEvent evt) {
                setEnabled(true);
                explorerTableWindow.dispose();
            }
        });
    }


    /**
     * Affiche la fenetre.
     *
     * @throws Exception Description of Exception
     */
    private void displayExplorerTableWindow() throws Exception {
        createExplorerTableWindow();
        explorerTableWindow.setVisible(true);
        try {
            explorerTableWindow.setSelected(true);
        }
        catch (java.beans.PropertyVetoException ex) {
            ex.printStackTrace();
        }
    }
}
