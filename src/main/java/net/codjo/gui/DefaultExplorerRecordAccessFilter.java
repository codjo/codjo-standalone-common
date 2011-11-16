/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.gui;
import net.codjo.model.Table;
/**
 * Classe permettant de restreindre la visibilité des enregistrements sur les tables
 * partagées. Il s'agit d'une classe par défaut qui renvoie toujours une chaine vide
 * comme clause where obligatoire.
 *
 * @version $Revision: 1.3 $
 */
public class DefaultExplorerRecordAccessFilter implements ExplorerRecordAccessFilter {
    /**
     * Constructeur.
     */
    public DefaultExplorerRecordAccessFilter() {}

    /**
     * Retourne la clause where obligatoire à utiliser pour le filtrage des données
     * (toujours une chaine vide).
     *
     * @param table La table à afficher.
     *
     * @return La String de la clause where obligatoire.
     */
    public String getMandatoryWhereClause(Table table) {
        return "";
    }
}
