/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.utils.sql;
import net.codjo.gui.toolkit.util.ErrorDialog;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import javax.swing.UIManager;
import org.apache.log4j.Logger;

/**
 * Action permettant d'afficher toutes les données d'une table.
 *
 * @author $Author: spinae $
 * @version $Revision: 1.2 $
 */
class ShowAllAction extends AbstractDbAction {
    String defaultValue = "";
    // Log
    private static final Logger APP = Logger.getLogger(ShowAllAction.class);


    /**
     * Constructor for the ShowAllAction object
     */
    ShowAllAction() {
        putValue(NAME, "Tout afficher");
        putValue(SHORT_DESCRIPTION, "Affiche toutes les données");
        putValue(SMALL_ICON, UIManager.getIcon("ListTable.showAll"));
    }


    /**
     * Constructor for the ShowAllAction object
     *
     * @param gt La table qui "dirige" l'action.
     *
     * @throws IllegalArgumentException TODO
     */
    ShowAllAction(GenericTable gt) {
        super(null, null, gt);
        if (gt == null) {
            throw new IllegalArgumentException();
        }
        putValue(NAME, "Tout afficher");
        putValue(SHORT_DESCRIPTION, "Affiche toutes les données");
        putValue(SMALL_ICON, UIManager.getIcon("ListTable.showAll"));
    }


    /**
     * Affichage de toutes les données de la table.
     *
     * @param evt evenement declenchant l'affichage
     */
    public void actionPerformed(ActionEvent evt) {
        fireActionEvent(evt);
        try {
            if (defaultValue == null || "".equals(defaultValue)) {
                getGenericTable().displayAll();
            }
            else {
                getGenericTable().reloadData(defaultValue);
            }
        }
        catch (SQLException ex) {
            APP.debug("defaultValue  >" + defaultValue + "<");
            ex.printStackTrace();
            ErrorDialog.show(getGenericTable(), "Impossible de raffraichir la fenêtre",
                             ex.getLocalizedMessage());
        }
    }


    /**
     * Fixe la clause par défaut (valeurs des filtres de l'explorateur) pour cette action.
     *
     * @param clause The new DefaultValue value
     */
    public void setDefaultValue(String clause) {
        defaultValue = clause;
    }
}
