/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.gui;
import javax.swing.JComponent;
import javax.swing.JLabel;
/**
 * Interface permettant d'ajouter des filtres d'affichage sur l'explorateur des données.
 *
 * @version $Revision: 1.2 $
 *
 *
 */
public interface ExplorerFilter {
    /**
     * Retourne le label du filtre.
     *
     * @return Le JLabel.
     */
    public JLabel getLabel();


    /**
     * Retourne le composant du filtre (ex : un combo).
     *
     * @return Le JComponent.
     */
    public JComponent getComponent();


    /**
     * Retourne la clause where à utiliser pour le filtrage.
     *
     * @return La String de la clause where.
     */
    public String getWhereClause();


    /**
     * Retourne le nom DB de la colonne sur laquelle porte le filtre.
     *
     * @return La String du nom DB de la colonne.
     */
    public String getFilterColumnName();
}
