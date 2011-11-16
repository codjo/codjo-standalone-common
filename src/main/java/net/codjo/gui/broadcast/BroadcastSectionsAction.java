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
public class BroadcastSectionsAction extends AbstractAction {
    private static final Logger LOG = Logger.getLogger(BroadcastSectionsAction.class);
    private ConnectionManager connectionManager;
    private javax.swing.JDesktopPane desktop;
    private GuiPreferencesManager preferencesGuiManager;
    private JInternalFrame securityFileSectionWindow;
    private TableHome tableHome;


    /**
     * Constructor for the SecurityFileSectionAction object
     *
     * @param preferencesGuiManager Description of the Parameter
     *
     * @throws IllegalArgumentException TODO
     */
    public BroadcastSectionsAction(JDesktopPane desktopPane,
                                   ConnectionManager connectionManager, TableHome tableHome,
                                   GuiPreferencesManager preferencesGuiManager) {
        if ((desktopPane == null)
            || (connectionManager == null)
            || (tableHome == null)
            || (preferencesGuiManager == null)) {
            throw new IllegalArgumentException();
        }

        putValue(NAME, "Sections / Colonnes");
        putValue(SHORT_DESCRIPTION,
                 "Paramétrage des sections distribuées et de leurs colonnes");

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
            displaySecurityFileSectionWindow();
            securityFileSectionWindow.setVisible(true);
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
    private void createSecurityFileSectionWindow()
          throws SQLException, PersistenceException {
        securityFileSectionWindow =
              new BroadcastSectionsWindow(desktop, connectionManager, tableHome,
                                          preferencesGuiManager);

        desktop.add(securityFileSectionWindow);
        GuiUtil.centerWindow(securityFileSectionWindow);

        securityFileSectionWindow.addInternalFrameListener(new InternalFrameAdapter() {
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
                securityFileSectionWindow.dispose();
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
    private void displaySecurityFileSectionWindow()
          throws SQLException, PersistenceException {
        createSecurityFileSectionWindow();
        securityFileSectionWindow.setVisible(true);

        try {
            securityFileSectionWindow.setSelected(true);
        }
        catch (java.beans.PropertyVetoException e) {
            LOG.error(e);
            ErrorDialog.show(desktop, "Erreur pendant la selection des fichiers", e);
        }
    }
}
