/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.utils.sql;
import net.codjo.utils.ConnectionManager;

import java.sql.Connection;
/**
 * Trop cool
 *
 * @author $Author: blazart $
 * @version $Revision: 1.2 $
 *
 */
public class Dependency {
    private static ConnectionManager connectionManager = null;
    private static Connection homeConnection = null;

    /**
     * Constructeur
     */
    public Dependency() {}

    /**
     * Attribue le gestionnaire de connections
     *
     * @param conMan The new ConnectionManager value
     */
    public static void setConnectionManager(ConnectionManager conMan) {
        connectionManager = conMan;
    }


    /**
     * Attribue la conenction partagée par tous les Homes
     *
     * @param hc The new HomeConnection value
     */
    public static void setHomeConnection(Connection hc) {
        homeConnection = hc;
    }


    /**
     * Récupère le gestionnaire de connections
     *
     * @return The ConnectionManager value
     *
     * @throws NullPointerException TODO
     */
    public static ConnectionManager getConnectionManager() {
        if (connectionManager == null) {
            throw new NullPointerException("sql.Dependency n'est pas initialise");
        }
        return connectionManager;
    }


    /**
     * Récupère la connection utilisée par tous les Homes
     *
     * @return The HomeConnection value
     *
     * @throws NullPointerException TODO
     */
    public static Connection getHomeConnection() {
        if (homeConnection == null) {
            throw new NullPointerException("sql.Dependency n'est pas initialise");
        }
        return homeConnection;
    }
}
