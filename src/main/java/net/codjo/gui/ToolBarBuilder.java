/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.gui;
import net.codjo.utils.sql.DbToolBar;
import net.codjo.utils.sql.GenericTable;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
/**
 * Interface permettant de récupérer la DbToolBar construite dans chaque application.
 * Elle permet de définir une toolBar spécifique à une table.
 *
 *
 */
public interface ToolBarBuilder {
    /**
     * Retourne la DbToolBar de l'application.
     *
     * @param dp Le desktopPane
     * @param frame La frame de l'écran liste
     * @param genericTable La genericTable de la table à afficher
     * @param whereClauseForFind La clause where par défaut pour la recherche
     * @param whereClauseForShowAll La clause where par défaut pour tout afficher
     * @param closeButton Affiche ou non le bouton "Fermer"
     *
     * @return La DbToolBar.
     */
    public DbToolBar getToolBar(JDesktopPane dp, JInternalFrame frame,
        GenericTable genericTable, String whereClauseForFind,
        String whereClauseForShowAll, boolean closeButton);
}
