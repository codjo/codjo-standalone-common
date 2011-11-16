/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.utils;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;
/**
 * Manager of SQL connection.
 *
 * <p> Spool database connection in order to speed up process. A ConnectionManager is specific for one
 * database and one user. The Pool contains 2 kinds of list: one for all connections built by this manager,
 * and another for unused connection. </p>
 *
 * <p> This class is multi-thread safe. </p>
 *
 * @author $Author: marcona $
 * @version $Revision: 1.5 $
 */
public class ConnectionManager {
    private List allConnections;
    private String catalog;
    private String classDriver;
    private long closeDelay = 5000;
    private Properties dbProps;
    private String dbUrl;
    private Timer timer;
    private long timestamp = System.currentTimeMillis();
    private Stack unusedConnections;


    public ConnectionManager() {
    }


    /**
     * Constructor for the ConnectionManager object.
     *
     * @param classDriver Class driver name (ex: com.sybase.jdbc2.jdbc.SybDriver)
     * @param url         Url of the Database
     * @param catalog     Catalog de la base (optionnal)
     * @param props       Propriete du driver
     *
     * @throws ClassNotFoundException Si le driver n'est pas trouve.
     */
    public ConnectionManager(String classDriver, String url, String catalog,
                             Properties props) throws ClassNotFoundException {
        this(classDriver, url, catalog, props, false);
    }


    /**
     * Constructeur de ConnectionManager
     *
     * @param classDriver              Description of the Parameter
     * @param url                      Description of the Parameter
     * @param catalog                  Description of the Parameter
     * @param props                    Description of the Parameter
     * @param numericTruncationWarning
     *
     * @throws ClassNotFoundException Description of the Exception
     */
    public ConnectionManager(String classDriver, String url, String catalog,
                             Properties props, boolean numericTruncationWarning)
          throws ClassNotFoundException {
        Class.forName(classDriver);
        this.classDriver = classDriver;
        this.dbUrl = url;
        this.dbProps = props;
        this.catalog = catalog;
        if (numericTruncationWarning) {
            dbProps.put("SQLINITSTRING", "set arithabort numeric_truncation on");
        }
        else {
            dbProps.put("SQLINITSTRING", "set arithabort numeric_truncation off");
        }
        unusedConnections = new Stack();
        allConnections = new java.util.LinkedList();
        initTimer();
    }


    /**
     * Ferme toutes les connections du pool.
     */
    public synchronized void closeAllConnections() {
        List copy = new ArrayList(allConnections);
        for (Iterator iter = copy.iterator(); iter.hasNext();) {
            Connection con = ((Connection)iter.next());
            closeConnection(con);
        }
    }


    /**
     * Retourne Le catalogue utilisé par ce ConnectionManager.
     *
     * @return nom de catalog
     */
    public String getCatalog() {
        return catalog;
    }


    /**
     * Retourne Le driver JDBC utilisé par ce ConnectionManager.
     *
     * @return nom de classe
     */
    public String getClassDriver() {
        return classDriver;
    }


    /**
     * Gets one valid connection to the database.
     *
     * @return a connection object to the database.
     *
     * @throws SQLException Description of Exception
     */
    public synchronized Connection getConnection()
          throws SQLException {
        if (unusedConnections.isEmpty()) {
            addNewConnection();
        }
        Connection co = (Connection)unusedConnections.pop();
        return co;
    }


    /**
     * Retourne Les propriétés utilisé par ce ConnectionManager pour créer des connections.
     *
     * @return properties
     */
    public Properties getDbProps() {
        return dbProps;
    }


    /**
     * Retourne L'url de la BD utilisé par ce ConnectionManager.
     *
     * @return addresse de la BD
     */
    public String getDbUrl() {
        return dbUrl;
    }


    /**
     * Release a connection (previously given by this manager), and close the given statement.
     *
     * @param con  a connection
     * @param stmt a statement
     *
     * @throws SQLException DB access error
     */
    public synchronized void releaseConnection(Connection con, Statement stmt)
          throws SQLException {
        try {
            if (stmt != null) {
                stmt.close();
            }
        }
        finally {
            releaseConnection(con);
        }
    }


    /**
     * Release a connection (previously given by this manager).
     *
     * @param con a connection
     *
     * @throws SQLException DB access error
     */
    public synchronized void releaseConnection(Connection con)
          throws SQLException {
        if ((con != null)
            && (con.isClosed() == false)
            && (unusedConnections.contains(con) == false)) {
            if (con.getAutoCommit() == false) {
                con.rollback();
                con.setAutoCommit(true);
            }
            timestamp = System.currentTimeMillis();
            if (unusedConnections.size() == 0) {
                unusedConnections.push(con);
            }
            else {
                closeConnection(con);
            }
        }
    }


    /**
     * Description of the Method
     *
     * @return Description of the Return Value
     */
    public String toString() {
        return "ConnectionManager(" + "total=" + allConnections.size() + ", unused="
               + unusedConnections.size() + ")";
    }


    /**
     * Retourne l'attribut allConnectionsSize de ConnectionManager
     *
     * @return La valeur de allConnectionsSize
     */
    public int getAllConnectionsSize() {
        return allConnections.size();
    }


    /**
     * Ferme toutes les connexions et arrête le timer de fermeture des connexions
     */
    public void shutdown() {
        closeAllConnections();
        timer.cancel();
    }


    /**
     * Adds a feature to the NewConnection attribute of the ConnectionManager object
     *
     * @throws SQLException         Description of Exception
     * @throws NullPointerException TODO
     */
    void addNewConnection() throws SQLException {
        Connection con = DriverManager.getConnection(dbUrl, dbProps);

        if (con == null) {
            throw new NullPointerException("DriverManager retourne une connection null");
        }

        if (catalog != null) {
            con.setCatalog(catalog);
        }

        allConnections.add(con);
        unusedConnections.push(con);
    }


    /**
     * Renseigne un nouveau délai d'attente avant de fermer une connection non utilisée.
     *
     * @param delay Le délai (en millisecondes).
     */
    void setCloseDelay(long delay) {
        closeDelay = delay;
        timer.cancel();
        initTimer();
    }


    /**
     * Ferme la connection.
     *
     * @param con Une connection
     */
    private void closeConnection(Connection con) {
        try {
            if (con.isClosed() == false) {
                con.close();
            }
        }
        catch (SQLException ex) {
            System.err.println("Unable to close a connection : " + ex.toString());
            ex.printStackTrace();
        }
        allConnections.remove(con);
        unusedConnections.remove(con);
    }


    /**
     * Ferme la connection non utilisée après le closeDelay.
     */
    private synchronized void closeOldConnection() {
        if (unusedConnections.size() != 0) {
            if (System.currentTimeMillis() >= timestamp + closeDelay) {
                closeConnection((Connection)unusedConnections.get(0));
            }
        }
    }


    /**
     * Initialise le Timer permettant de fermer une connection non utilisée au bout du closeDelay.
     */
    private void initTimer() {
        timer = new Timer(true);
        timer.schedule(new TimerTask() {
            public void run() {
                closeOldConnection();
            }
        }, 1, closeDelay);
    }
}
