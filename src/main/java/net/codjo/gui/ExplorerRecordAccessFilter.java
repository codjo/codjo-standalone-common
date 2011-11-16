/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.gui;
import net.codjo.model.Table;
/**
 * Interface permettant de gérer la visibilité des enregistrements sur les tables
 * partagées entre les différentes applications.
 *
 * @version $Revision: 1.2 $
 *
 *
 */
public interface ExplorerRecordAccessFilter {
    /**
     * Retourne la clause where obligatoire à utiliser pour le filtrage des données.
     *
     * @param table La table à afficher.
     *
     * @return La String de la clause where obligatoire.
     */
    public String getMandatoryWhereClause(Table table);
}
