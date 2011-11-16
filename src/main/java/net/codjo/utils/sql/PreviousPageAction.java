/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.utils.sql;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.UIManager;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
/**
 * Action qui permet de passer à la page précédente
 *
 * @author $Author: marcona $
 * @version $Revision: 1.4 $
 *
 */
public class PreviousPageAction extends AbstractAction implements TableModelListener {
    GenericTable genericTable;
    NextPageAction nextPageAction;

    /**
     * Constructor for the PreviousPageAction object
     */
    public PreviousPageAction() {
        putValue(NAME, "Page précédente");
        putValue(SHORT_DESCRIPTION, "Retour à la page précédente");
        putValue(SMALL_ICON, UIManager.getIcon("ListTable.previous"));
    }


    /**
     * Constructor for the PreviousPageAction object
     *
     * @param gt Description of Parameter
     */
    public PreviousPageAction(GenericTable gt) {
        genericTable = gt;
        setEnabled(isActivated());
        putValue(NAME, "Page précédente");
        putValue(SHORT_DESCRIPTION, "Retour à la page précédente");
        putValue(SMALL_ICON, UIManager.getIcon("ListTable.previous"));
        genericTable.getTableModel().addTableModelListener(this);
    }

    /**
     * Garde un lien entre cette action et le bouton Next Page
     *
     * @param npa L'autre action
     */
    public void setNextPageAction(NextPageAction npa) {
        nextPageAction = npa;
    }


    /**
     * Le model à changé on met à jour l'état du bouton
     *
     * @param evt Description of Parameter
     */
    public void tableChanged(TableModelEvent evt) {
        setEnabled(isActivated());
    }


    /**
     * Page précédente
     *
     * @param parm1 Description of Parameter
     */
    public void actionPerformed(ActionEvent parm1) {
        genericTable.previousPage();
    }


    /**
     * Retourne Oui si l'action doit être activée
     *
     * @return The Activated value
     */
    private boolean isActivated() {
        return (genericTable.getPageNumber() != 0);
    }
}
