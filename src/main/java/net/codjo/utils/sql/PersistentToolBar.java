/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.utils.sql;
import net.codjo.model.Table;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
/**
 * Toolbar contenant les actions attachées à une table persistente.
 * 
 * <p>
 * Cette Toolbar contient les actions suivantes : Ajouter, Modifier, Supprimer,
 * Rechercher, Tout afficher, Quitter
 * </p>
 * 
 * <p>
 * Cette ToolBar rajoute un popup sur la table avec les actions : Modifier, Supprimer.
 * </p>
 *
 * @author $Author: marcona $
 * @version $Revision: 1.5 $
 *
 */
public class PersistentToolBar extends DbToolBar {
    private static final int[] PERSISTENT_ACTIONS =
        {
            PREVIOUS, NEXT, SEPARATOR, EXPORT, PRINT, SEPARATOR, ADD, MODIFY, DELETE,
            SEPARATOR, FIND, SHOW, SEPARATOR, CLOSE
        };
    private static final int[] PERSISTENT_ACTIONS_NOCLOSE =
        {
            PREVIOUS, NEXT, SEPARATOR, EXPORT, PRINT, SEPARATOR, ADD, MODIFY, DELETE,
            SEPARATOR, FIND, SHOW
        };

    /**
     * Constructor for Designer
     */
    public PersistentToolBar() {}


    /**
     * Constructor for the PersistentToolBar object
     *
     * @param dp Description of Parameter
     * @param gt Description of Parameter
     * @param jf Description of Parameter
     * @param packName Description of Parameter
     */
    public PersistentToolBar(JDesktopPane dp, GenericTable gt, JInternalFrame jf,
        String packName) {
        this(dp, gt, jf, packName, true);
    }


    /**
     * Constructor for the PersistentToolBar object
     *
     * @param dp Le DesktopPane
     * @param gt La GenericTable source
     * @param jf L'InternelFrame sur laquelle on ajoute cette ToolBar
     * @param packName Nom du package dans lequel se trouve la classe de l'écran détail
     * @param closeButton Description of Parameter
     */
    public PersistentToolBar(JDesktopPane dp, GenericTable gt, JInternalFrame jf,
        String packName, boolean closeButton) {
        super(dp, gt, jf, packName,
            ((closeButton) ? PERSISTENT_ACTIONS : PERSISTENT_ACTIONS_NOCLOSE));
        setConnection(Dependency.getHomeConnection());
//        Penelope.getInstance().getHomeConnection());
        Table table = genericTable.getTable();
        if (table.getPkNames().size() == 1
                && table.getAllColumns().get(table.getPkNames().get(0)).equals(new Integer(
                        java.sql.Types.INTEGER))) {
            addAction.setPkType(AddAction.PK_AUTOMATIC);
        }
        else {
            addAction.setPkType(AddAction.PK_MANUAL);
        }
    }

    /**
     * Sets the PkManual attribute of the PersistentToolBar object
     */
    public void setPkManual() {
        addAction.setPkType(AddAction.PK_MANUAL);
    }
}
