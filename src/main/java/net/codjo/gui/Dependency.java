/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.gui;
import net.codjo.model.TableHome;
import net.codjo.utils.ConnectionManager;
/**
 * DOCUMENT ME!
 *
 * @version $Revision: 1.4 $
 */
public class Dependency {
    private static ConnectionManager connectionManager = null;
    private static TableHome tableHome = null;
    private static String application = "";

    /**
     * Constructeur
     */
    public Dependency() {}

    /**
     * Positionne l'attribut application de Dependency
     *
     * @param appli La nouvelle valeur de application
     */
    public static void setApplication(String appli) {
        application = appli;
    }


    /**
     * Retourne l'attribut application de Dependency
     *
     * @return La valeur de application
     */
    public static String getApplication() {
        return application;
    }


    /**
     * Attribue le gestionnaire de connections
     *
     * @param conMan The new ConnectionManager value
     */
    public static void setConnectionManager(ConnectionManager conMan) {
        connectionManager = conMan;
    }


    /**
     * Récupère le tableHome
     *
     * @return The TableHome value
     */
    public static TableHome getTableHome() {
        return tableHome;
    }


    /**
     * Positionne l'attribut tableHome de Dependency
     *
     * @param th La nouvelle valeur de tableHome
     */
    public static void setTableHome(TableHome th) {
        tableHome = th;
    }


    /**
     * Récupère le gestionnaire de connections
     *
     * @return The ConnectionManager value
     */
    public static ConnectionManager getConnectionManager() {
        return connectionManager;
    }
}
