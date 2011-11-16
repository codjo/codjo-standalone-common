/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.utils.sql;
import net.codjo.gui.operation.ExportProgress;
import net.codjo.gui.toolkit.fileChooser.FileChooserManager;
import net.codjo.gui.toolkit.util.ErrorDialog;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.UIManager;
/**
 * Permet d'exporter les données de la GenericTable sous forme de fichier texte.
 *
 * @author $Author: marcona $
 * @version $Revision: 1.7 $
 */
public class ExportAction extends AbstractAction {
    private GenericTable genericTable;
    private boolean reloadAtEnd;


    /**
     * Constructeur.
     *
     * @param gt La GenericTable.
     */
    public ExportAction(GenericTable gt) {
        this(gt, true);
    }


    public ExportAction(GenericTable gt, boolean doReloadAtEnd) {
        genericTable = gt;
        reloadAtEnd = doReloadAtEnd;
        putValue(NAME, "Export");
        putValue(SHORT_DESCRIPTION, "Export des données");
        putValue(SMALL_ICON, UIManager.getIcon("ListTable.bcpOut"));
    }


    /**
     * Lance l'export des données.
     *
     * @param evt Event
     */
    public void actionPerformed(ActionEvent evt) {
        String fileName =
              FileChooserManager.showChooserForExport(genericTable.getTable().getTableName()
                                                      + ".txt", "Export de la sélection courante");
        if (fileName == null) {
            return;
        }
        ExportProgress export = new ExportProgress(genericTable, fileName, reloadAtEnd);
        export.go(new Runnable() {
            /**
             */
            public void run() {
                try {
                    genericTable.reloadData();
                }
                catch (java.sql.SQLException ex) {
                    ex.printStackTrace();
                    ErrorDialog.show(genericTable,
                                     "Impossible de réafficher les données !", ex);
                }
            }
        });
    }
}
