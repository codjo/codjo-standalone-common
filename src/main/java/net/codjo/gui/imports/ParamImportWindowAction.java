/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.gui.imports;
import net.codjo.gui.toolkit.util.ErrorDialog;
import net.codjo.model.PeriodHome;
import net.codjo.operation.imports.ImportBehaviorHome;
import net.codjo.persistent.PersistenceException;
import net.codjo.utils.GuiUtil;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import javax.swing.AbstractAction;
import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
/**
 * Action permettant d'afficher la fenetre liste des imports paramètrés
 *
 * @author $Author: blazart $
 * @version $Revision: 1.3 $
 */
public class ParamImportWindowAction extends AbstractAction {
    private javax.swing.JDesktopPane gexPane;
    private ImportBehaviorHome importHome;
    private JInternalFrame paramImportWindow;
    private PeriodHome periodHome;


    /**
     * Constructor for the ParamImportWindowAction object
     *
     * @param dp         le desktopPane dans lequel sera affichee la fenetre.
     * @param periodHome Description of the Parameter
     * @param ibh        Description of the Parameter
     *
     * @throws IllegalArgumentException TODO
     */
    public ParamImportWindowAction(javax.swing.JDesktopPane dp, PeriodHome periodHome,
                                   ImportBehaviorHome ibh) {
        if (dp == null) {
            throw new IllegalArgumentException();
        }
        putValue(NAME, "Import");
        putValue(SHORT_DESCRIPTION, "Import");
        gexPane = dp;
        this.importHome = ibh;
        this.periodHome = periodHome;
    }


    /**
     * Affichage de la fenetre de paramètrage des imports
     *
     * @param parm1 evenement declenchant l'affichage
     */
    public void actionPerformed(ActionEvent parm1) {
        try {
            displayParamImportWindow();
            paramImportWindow.setVisible(true);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            ErrorDialog.show(gexPane, "Impossible d'afficher la fenêtre: ", ex);
        }
    }


    /**
     * Creation de la fenetre de paramètrage des imports (ParamImportWindow).
     *
     * @throws SQLException         Description of Exception
     * @throws PersistenceException Description of Exception
     */
    private void createParamImportWindow() throws SQLException, PersistenceException {
        paramImportWindow = new ParamImportWindow(gexPane, periodHome, importHome);
        gexPane.add(paramImportWindow);
        GuiUtil.centerWindow(paramImportWindow);

        paramImportWindow.addInternalFrameListener(new InternalFrameAdapter() {
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
                paramImportWindow.dispose();
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
    private void displayParamImportWindow() throws SQLException, PersistenceException {
        createParamImportWindow();
        paramImportWindow.setVisible(true);
        try {
            paramImportWindow.setSelected(true);
        }
        catch (java.beans.PropertyVetoException ex) {
            ex.printStackTrace();
        }
    }
}
