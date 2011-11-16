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
 * Action qui permet de passer à la page suivante
 *
 * @author $Author: marcona $
 * @version $Revision: 1.4 $
 *
 */
public class NextPageAction extends AbstractAction implements TableModelListener {
    GenericTable genericTable;
    PreviousPageAction previousPageAction;

    /**
     * Constructor for the NextPageAction object
     */
    public NextPageAction() {
        putValue(NAME, "Page suivante");
        putValue(SHORT_DESCRIPTION, "Passage à la page suivante");
        putValue(SMALL_ICON, UIManager.getIcon("ListTable.next"));
    }


    /**
     * Constructor for the NextPageAction object
     *
     * @param gt Description of Parameter
     */
    public NextPageAction(GenericTable gt) {
        genericTable = gt;
        setEnabled(isActivated());
        putValue(NAME, "Page suivante");
        putValue(SHORT_DESCRIPTION, "Passage à la page suivante");
        putValue(SMALL_ICON, UIManager.getIcon("ListTable.next"));
        genericTable.getTableModel().addTableModelListener(this);
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
     * Page suivante
     *
     * @param parm1 Description of Parameter
     */
    public void actionPerformed(ActionEvent parm1) {
        genericTable.nextPage();
    }


    /**
     * Retourne Oui si l'action doit être activée
     *
     * @return The Activated value
     */
    private boolean isActivated() {
        return genericTable.hasMoreData();
    }
}
