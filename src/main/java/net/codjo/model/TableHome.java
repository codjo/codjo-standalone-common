/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.model;
import net.codjo.persistent.PersistenceException;
import net.codjo.persistent.Reference;
import net.codjo.persistent.sql.SimpleHome;
import net.codjo.utils.ConnectionManager;
import net.codjo.utils.SQLFieldList;
import net.codjo.utils.sql.event.DbChangeEvent;
import net.codjo.utils.sql.event.DbChangeListener;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import org.apache.log4j.Logger;
/**
 * Classe Model des objets Table.
 * 
 * <p>
 * Pour obtenir une reference sur une table : <code>getReference(new Integer(5),
 * Table.class))</code> .
 * </p>
 *
 * @version $Revision: 1.5 $
 *
 */
public class TableHome extends SimpleHome {
    // Log
    private static final Logger APP = Logger.getLogger(TableHome.class);
    private ConnectionManager connectionManager;

    /**
     * Constructeur de TableHome
     *
     * @param con Une connexion
     * @param cM Le connectionManager.
     *
     * @exception SQLException Impossible de creer les PreparedStatement
     */
    public TableHome(Connection con, ConnectionManager cM)
            throws SQLException {
        super(con, ResourceBundle.getBundle("TableHome"));
        connectionManager = cM;
    }


    /**
     * Constructeur de TableHome en précisant un ResourceBundle (nom du fichier
     * properties).
     *
     * @param con Une connexion
     * @param cM Le connectionManager.
     * @param resource Le nom du fichier properties.
     *
     * @exception SQLException Impossible de creer les PreparedStatement
     */
    public TableHome(Connection con, ConnectionManager cM, String resource)
            throws SQLException {
        super(con, ResourceBundle.getBundle(resource));
        connectionManager = cM;
    }

    /**
     * Retourne une reference sur une Table.
     *
     * @param id DB_TABLE_NAME_ID
     *
     * @return Une reference.
     */
    public Reference getReference(int id) {
        return getReference(new Integer(id));
    }


    /**
     * Retourne la table ayant cet id.
     *
     * @param id DB_TABLE_NAME_ID
     *
     * @return The Table value
     *
     * @exception PersistenceException
     */
    public Table getTable(int id) throws PersistenceException {
        return (Table)getReference(id).getObject();
    }


    /**
     * Récupère la définiton d'une table.
     *
     * @param dbTableName Nom physique de la table.
     *
     * @return Une table ou null si inconnue.
     *
     * @exception PersistenceException -
     */
    public Table getTable(String dbTableName) throws PersistenceException {
        try {
            return getTableSQL(dbTableName);
        }
        catch (SQLException ex) {
            ex.printStackTrace();
            throw new PersistenceException(ex);
        }
    }


    /**
     * Retourne un listener mettant a jours la couche de persistance au niveau de
     * PortfolioGroupHome lors des changements directe en Base.
     *
     * @return The DbChangeListener value
     *
     * @see net.codjo.persistent.sql.SimpleHome.DefaultDbChangeListener
     */
    public DbChangeListener getDbChangeListener() {
        return new TableDbChangeListener();
    }


    /**
     * Determine les colonnes cles primaires. La determination se fait grace à l'API
     * JDBC.
     *
     * @param dbTableName Le nom physique de la table.
     *
     * @return Description of the Returned Value
     */
    public List determinePkNames(String dbTableName) {
        try {
            List keyDBNameList = new ArrayList();
            Connection con = connectionManager.getConnection();
            try {
                DatabaseMetaData dbmd = con.getMetaData();
                if (dbTableName.startsWith("#")) {
                    ResultSet rs =
                        dbmd.getColumns("tempdb", null,
                            dbTableName.substring(0, Math.min(13, dbTableName.length()))
                            + "%", null);
                    rs.next();
                    keyDBNameList.add(rs.getString("COLUMN_NAME"));
                    rs.close();
                }
                else {
                    ResultSet rs = dbmd.getPrimaryKeys(null, null, dbTableName);
                    while (rs.next()) {
                        keyDBNameList.add(rs.getString("COLUMN_NAME"));
                    }
                    rs.close();
                }
            }
            catch (SQLException ex) {
                APP.error("Impossible de determiner les PK : " + dbTableName);
                throw ex;
            }
            finally {
                connectionManager.releaseConnection(con);
            }
            return keyDBNameList;
        }
        catch (SQLException ex) {
            return null;
        }
    }


    /**
     * Determine les colonnes de la table (nom + type) .La determination se fait grace à
     * l'API JDBC.
     *
     * @param dbTableName Le nom physique de la table.
     *
     * @return Map (clef = nom de la colonne) (valeur = type SQL)
     *
     * @see java.sql.Types
     */
    public Map determineAllColumns(String dbTableName) {
        // @bug : bizzarerie Sybase, des requetes MetaData sur une connection
        //        en autocommit=false, si on ne le fait pas en premier (haha),
        //        lance des exceptions.
        //        cf : exception JZ0R2
        //   http://ness:34500/dynaweb/jcg0400e/jconigrb/@Generic__BookView?DwebQuery=JZ0R2
        try {
            SQLFieldList fieldList;
            Connection con = connectionManager.getConnection();
            try {
                if (dbTableName.startsWith("#")) {
                    fieldList =
                        new SQLFieldList(dbTableName.substring(0,
                                Math.min(13, dbTableName.length())) + "%", con, "tempdb");
                }
                else {
                    fieldList = new SQLFieldList(dbTableName, con);
                }

                Map allColumns = new HashMap();
                for (Iterator iter = fieldList.fieldNames(); iter.hasNext();) {
                    String dbName = (String)iter.next();
                    allColumns.put(dbName, new Integer(fieldList.getFieldType(dbName)));
                }
                return allColumns;
            }
            finally {
                connectionManager.releaseConnection(con);
            }
        }
        catch (SQLException ex) {
            return null;
        }
    }


    /**
     * Récupère la définiton d'une table à partir de son nom.
     *
     * @param dbTableName Nom physique de la table.
     *
     * @return Une table ou null si inconnue.
     *
     * @exception SQLException
     * @exception PersistenceException
     */
    private Table getTableSQL(String dbTableName)
            throws SQLException, PersistenceException {
        Statement stmt = null;
        try {
            stmt = getConnection().createStatement();
            ResultSet rs =
                stmt.executeQuery("select * from PM_TABLE where DB_TABLE_NAME='"
                    + dbTableName + "'");

            if (rs.next()) {
                Reference ref = loadReference(rs);
                return (Table)loadObject(rs, ref);
            }
            else {
                // Cas particulier d'une table non definie dans PM_TABLE
                Map columns = determineAllColumns(dbTableName);
                if (columns.size() == 0) {
                    return null;
                }
                Reference ref = new Reference(this);
                return new Table(ref, dbTableName, columns, determinePkNames(dbTableName));
            }
        }
        finally {
            if (stmt != null) {
                stmt.close();
            }
        }
    }

    /**
     * Classe offrant un comportement par defaut pour la mise a jours de ce Home, lors de
     * modification en directe de la BD.
     * 
     * <p>
     * Ce listener recharge automatiquement la table modifie.
     * </p>
     *
     * @author $Author: marcona $
     * @version $Revision: 1.5 $
     */
    /**
     * DOCUMENT ME!
     *
     */
    public class TableDbChangeListener extends DefaultDbChangeListener {
        /**
         * DOCUMENT ME!
         *
         * @param evt Description of Parameter
         */
        public void succeededChange(DbChangeEvent evt) {
            super.succeededChange(evt);
            if (evt.getEventType() == DbChangeEvent.MODIFY_EVENT) {
                try {
                    getReference(evt.getPrimaryKey()).getObject();
                }
                catch (PersistenceException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
