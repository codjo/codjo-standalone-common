/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.gui.treatment;
import net.codjo.gui.toolkit.util.ErrorDialog;
import net.codjo.model.PeriodHome;
import net.codjo.utils.ConnectionManager;
import net.codjo.utils.GuiUtil;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import javax.swing.AbstractAction;
import javax.swing.JInternalFrame;
import javax.swing.UIManager;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
/**
 * Action permettant d'afficher la fenêtre de lancement de la contre-valorisation.
 *
 * @author $Author: marcona $
 * @version $Revision: 1.4 $
 */
public class ConvertedToCurrencyWindowAction extends AbstractAction {
    private javax.swing.JDesktopPane gexPane;
    private JInternalFrame convertedToCurrencyWindow;
    private PeriodHome periodHome;
    private ConnectionManager connectionManager;


    /**
     * Constructeur.
     *
     * @param dp     DesktopPane principal
     * @param ph     Le PeriodHome
     * @param conMan Le ConnectionManager
     *
     * @throws IllegalArgumentException TODO
     */
    public ConvertedToCurrencyWindowAction(javax.swing.JDesktopPane dp, PeriodHome ph,
                                           ConnectionManager conMan) {
        if ((dp == null) || (ph == null) || (conMan == null)) {
            throw new IllegalArgumentException();
        }
        putValue(NAME, "Contre-valorisation");
        putValue(SHORT_DESCRIPTION, "Contre-valorisation");
        putValue(SMALL_ICON, UIManager.getIcon("Conversion.open"));
        gexPane = dp;
        periodHome = ph;
        connectionManager = conMan;
    }


    /**
     * Ouverture de la fenetre.
     *
     * @param parm1 event
     */
    public void actionPerformed(ActionEvent parm1) {
        try {
            displayConvertedToCurrencyWindow();
            convertedToCurrencyWindow.setVisible(true);
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
     * @throws SQLException Description of Exception
     */
    private void createConvertedToCurrencyWindow()
          throws SQLException {
        convertedToCurrencyWindow =
              new ConvertedToCurrencyWindow(gexPane, connectionManager, periodHome);
        gexPane.add(convertedToCurrencyWindow);

        convertedToCurrencyWindow.addInternalFrameListener(new InternalFrameAdapter() {
            /**
             * DOCUMENT ME!
             *
             * @param evt Description of the Parameter
             */
            public void internalFrameOpened(InternalFrameEvent evt) {
                setEnabled(false);
                GuiUtil.centerWindow(convertedToCurrencyWindow);
            }


            /**
             * DOCUMENT ME!
             *
             * @param evt Description of the Parameter
             */
            public void internalFrameClosed(InternalFrameEvent evt) {
                setEnabled(true);
            }
        });
    }


    /**
     * Affiche la fenetre.
     *
     * @throws SQLException Description of Exception
     */
    private void displayConvertedToCurrencyWindow()
          throws SQLException {
        createConvertedToCurrencyWindow();
        convertedToCurrencyWindow.setVisible(true);
        try {
            convertedToCurrencyWindow.setSelected(true);
        }
        catch (java.beans.PropertyVetoException ex) {
            ex.printStackTrace();
        }
    }
}
