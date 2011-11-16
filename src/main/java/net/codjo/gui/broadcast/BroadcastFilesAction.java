/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.gui.broadcast;
import net.codjo.gui.toolkit.util.ErrorDialog;
import net.codjo.gui.toolkit.util.GuiUtil;
import net.codjo.model.TableHome;
import net.codjo.persistent.PersistenceException;
import net.codjo.utils.ConnectionManager;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import javax.swing.AbstractAction;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import org.apache.log4j.Logger;
/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.4 $
 */
public class BroadcastFilesAction extends AbstractAction {
    private static final Logger LOG = Logger.getLogger(BroadcastFilesAction.class);
    private ConnectionManager connectionManager;
    private javax.swing.JDesktopPane desktop;
    private JInternalFrame distributedFileWindow;
    private GuiPreferencesManager preferencesGuiManager;
    private TableHome tableHome;


    /**
     * Constructor for the DistributedFileAction object
     *
     * @param desktopPane           Description of the Parameter
     * @param tableHome             Description of the Parameter
     * @param preferencesGuiManager Description of the Parameter
     *
     * @throws IllegalArgumentException DOCUMENT ME!
     */
    public BroadcastFilesAction(JDesktopPane desktopPane,
                                ConnectionManager connectionManager, TableHome tableHome,
                                GuiPreferencesManager preferencesGuiManager) {
        if ((desktopPane == null)
            || (connectionManager == null)
            || (tableHome == null)
            || (preferencesGuiManager == null)) {
            throw new IllegalArgumentException();
        }

        putValue(NAME, "Fichiers / Sections");
        putValue(SHORT_DESCRIPTION,
                 "Paramétrage des fichier distribués et de leurs sections");

        this.desktop = desktopPane;
        this.connectionManager = connectionManager;
        this.tableHome = tableHome;
        this.preferencesGuiManager = preferencesGuiManager;
    }


    /**
     * Affichage de la fenetre
     *
     * @param parm1 evenement declenchant l'affichage
     */
    public void actionPerformed(ActionEvent parm1) {
        try {
            displayDistributedFileWindow();
            distributedFileWindow.setVisible(true);
        }
        catch (Exception e) {
            LOG.error(e);
            ErrorDialog.show(desktop, "Impossible d'afficher la fenêtre: ", e);
        }
    }


    /**
     * Creation de la fenetre
     *
     * @throws SQLException         -
     * @throws PersistenceException -
     */
    private void createDistributedFileWindow() throws SQLException, PersistenceException {
        distributedFileWindow =
              new BroadcastFilesWindow(desktop, connectionManager, preferencesGuiManager,
                                       tableHome);
        desktop.add(distributedFileWindow);
        GuiUtil.centerWindow(distributedFileWindow);

        distributedFileWindow.addInternalFrameListener(new InternalFrameAdapter() {
            /**
             * Desactive l'action lors de l'ouverture de la fenetre.
             *
             * @param e -
             */
            @Override
            public void internalFrameActivated(InternalFrameEvent e) {
                setEnabled(false);
            }


            /**
             * Active l'action à la fermeture de la fenetre.
             *
             * @param e -
             */
            @Override
            public void internalFrameClosing(InternalFrameEvent e) {
                setEnabled(true);
                distributedFileWindow.dispose();
            }
        });
    }


    /**
     * Affiche la fenetre.
     *
     * <p> Si la fenetre n'a pas deja ete cree, la methode en créé une. </p>
     *
     * @throws SQLException         -
     * @throws PersistenceException -
     */
    private void displayDistributedFileWindow() throws SQLException, PersistenceException {
        createDistributedFileWindow();
        distributedFileWindow.setVisible(true);

        try {
            distributedFileWindow.setSelected(true);
        }
        catch (java.beans.PropertyVetoException e) {
            // Erreur possible
        }
    }
}
