/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.gui;
import net.codjo.gui.toolkit.util.ErrorDialog;
import net.codjo.persistent.PersistenceException;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.UIManager;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
/**
 * Action qui lance un explorateur des tables.
 *
 * @version $Revision: 1.4 $
 */
public class ExplorerDataWindowAction extends AbstractAction {
    private javax.swing.JDesktopPane gexPane;
    private JInternalFrame explorerDataWindow;
    private List filters;
    private ExplorerTreeBuilder explorer;
    private ExplorerRecordAccessFilter recordAccessFilter;
    private ToolBarBuilder toolBarBuilder;
    private final static String DATA_EXPLORER_LABEL = "Explorateur des données";


    /**
     * Constructeur.
     *
     * @param dp           DesktopPane principal.
     * @param filters      Liste des filtres d'affichage.
     * @param exp          L'explorateur permettant de récupérer le JTree spécifique à l'application.
     * @param recordFilter Filtre sur la visibilité des enregistrements des tables partagées entre plusieurs
     *                     applications.
     * @param toolBar      Le constructeur de la toolBar.
     *
     * @throws IllegalArgumentException TODO
     */
    public ExplorerDataWindowAction(JDesktopPane dp, List filters,
                                    ExplorerTreeBuilder exp, ExplorerRecordAccessFilter recordFilter,
                                    ToolBarBuilder toolBar) {
        if (dp == null || filters == null || exp == null || toolBar == null) {
            throw new IllegalArgumentException("Un parametre n'est pas renseigné !");
        }
        putValue(NAME, DATA_EXPLORER_LABEL);
        putValue(SHORT_DESCRIPTION, DATA_EXPLORER_LABEL);
        putValue(SMALL_ICON, UIManager.getIcon("DataExplorer.open"));
        gexPane = dp;
        this.filters = filters;
        explorer = exp;
        recordAccessFilter = recordFilter;
        toolBarBuilder = toolBar;
    }


    /**
     * Constructeur allégé (sans filtre sur la visibilité des enregistrements des tables partagées entre
     * plusieurs applications).
     *
     * @param dp      DesktopPane principal.
     * @param filters Liste des filtres d'affichage.
     * @param exp     L'explorateur permettant de récupérer le JTree spécifique à l'application.
     * @param toolBar Description of the Parameter
     */
    public ExplorerDataWindowAction(JDesktopPane dp, List filters,
                                    ExplorerTreeBuilder exp, ToolBarBuilder toolBar) {
        this(dp, filters, exp, null, toolBar);
        putValue(NAME, DATA_EXPLORER_LABEL);
        putValue(SHORT_DESCRIPTION, DATA_EXPLORER_LABEL);
        putValue(SMALL_ICON, UIManager.getIcon("DataExplorer.open"));
    }


    /**
     * Ouverture de la fenetre.
     *
     * @param parm1 event
     */
    public void actionPerformed(ActionEvent parm1) {
        try {
            displayExplorerDataWindow();
            explorerDataWindow.setVisible(true);
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
     * @throws PersistenceException -
     */
    private void createExplorerDataWindow() throws PersistenceException {
        explorerDataWindow =
              new ExplorerDataWindow(gexPane, filters, explorer, recordAccessFilter,
                                     toolBarBuilder);
        gexPane.add(explorerDataWindow);

        explorerDataWindow.addInternalFrameListener(new InternalFrameAdapter() {
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
                explorerDataWindow.dispose();
            }
        });
    }


    /**
     * Affiche la fenetre.
     *
     * @throws PersistenceException -
     */
    private void displayExplorerDataWindow() throws PersistenceException {
        createExplorerDataWindow();
        explorerDataWindow.setVisible(true);
        try {
            explorerDataWindow.setSelected(true);
        }
        catch (java.beans.PropertyVetoException ex) {
            ex.printStackTrace();
        }
    }
}
