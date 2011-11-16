/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.gui.model;
import net.codjo.model.TableHome;
import net.codjo.utils.ConnectionManager;
/**
 * Trop cool
 *
 * @author $Author: blazart $
 * @version $Revision: 1.3 $
 *
 *
 */
public class Dependency {
    private static TableHome tableHome = null;
    private static ConnectionManager connectionManager = null;

    /**
     * Constructeur
     */
    public Dependency() {}

    /**
     * Positionne l attribut tableHome de l class Dependency
     *
     * @param th La nouvelle valeur de tableHome
     */
    public static void setTableHome(TableHome th) {
        tableHome = th;
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
}
