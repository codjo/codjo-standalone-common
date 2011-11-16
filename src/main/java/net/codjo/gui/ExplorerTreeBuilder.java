/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.gui;
/**
 * Interface permettant de récupérer le JTree spécifique à chaque application. Elle est
 * utilisée pour l'affichage de l'explorateur des données.
 *
 * @version $Revision: 1.3 $
 *
 *
 */
public interface ExplorerTreeBuilder {
    /**
     * Retourne le JTree de l'application.
     *
     * @return Le JTree.
     */
    public javax.swing.JTree getTree();


    /**
     * Retourne le user courant
     *
     * @return The user value
     */
    public net.codjo.profile.User getUser();


    /**
     * Affecte user à newCurrentUser
     *
     * @param newCurrentUser The new currentUser value
     */
    public void setCurrentUser(net.codjo.profile.User newCurrentUser);
}
