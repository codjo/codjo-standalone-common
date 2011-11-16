/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.utils.sql;
import java.awt.event.ActionEvent;
import javax.swing.JInternalFrame;

// Java
import javax.swing.UIManager;
/**
 * Action permettant de fermer une fenêtre.
 *
 * @author $Author: gonnot $
 * @version $Revision: 1.1.1.1 $
 */
class CloseAction extends AbstractDbAction {
    /**
     * Constructor for the CloseAction object
     */
    public CloseAction() {
        putValue(NAME, "Fermer");
        putValue(SHORT_DESCRIPTION, "Ferme la fenêtre");
        putValue(SMALL_ICON, UIManager.getIcon("ListTable.close"));
    }


    /**
     * Constructor for the CloseAction object
     *
     * @param f Fenêtre d'affichage de la table (fenêtre mère).
     *
     * @throws IllegalArgumentException TODO
     */
    public CloseAction(JInternalFrame f) {
        super(null, f, null);
        if (f == null) {
            throw new IllegalArgumentException();
        }
        putValue(NAME, "Fermer");
        putValue(SHORT_DESCRIPTION, "Ferme la fenêtre");
        putValue(SMALL_ICON, UIManager.getIcon("ListTable.close"));
    }

    /**
     * Ferme la fenêtre courante.
     *
     * @param parm1 evenement declenchant la fermeture
     */
    public void actionPerformed(ActionEvent parm1) {
        try {
            getWindowTable().setClosed(true);
        }
        catch (java.beans.PropertyVetoException ex) {}
    }
}
