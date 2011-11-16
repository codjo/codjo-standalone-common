/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.operation.imports;
import net.codjo.model.TableHome;
import net.codjo.operation.BehaviorLoader;
import net.codjo.persistent.Model;
import net.codjo.persistent.PersistenceException;
import net.codjo.persistent.Persistent;
import net.codjo.persistent.Reference;
import net.codjo.persistent.sql.SimpleHome;
import net.codjo.utils.ConnectionManager;
import net.codjo.utils.sql.event.DbChangeListener;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
/**
 * Class Home pour les objets ImportBehavior.
 * 
 * <p>
 * Cette classe utilise les tables PM_FIELD_IMPORT_SETTINGS et PM_IMPORT_SETTINGS.
 * </p>
 *
 * @author $Author: blazart $
 * @version $Revision: 1.3 $
 *
 */
public class ImportBehaviorHome extends SimpleHome implements BehaviorLoader {
    /** Description of the Field */
    public TableHome tableHome;
    private FieldImportHome fieldImportHome;
    private ConnectionManager connectionManager;

    /**
     * Constructor for the ImportBehaviorHome object
     *
     * @param con Une connnection
     * @param conMan Manager de connection (pour l'import)
     * @param tableHome Home table
     *
     * @exception SQLException En cas d'erreur lors de l'acces a la base
     */
    public ImportBehaviorHome(Connection con, ConnectionManager conMan,
        TableHome tableHome) throws SQLException {
        super(con, ResourceBundle.getBundle("ImportBehaviorHome"));
        this.tableHome = tableHome;
        fieldImportHome = new FieldImportHome(con);
        connectionManager = conMan;
    }

    /**
     * Retourne une reference avec ID.
     *
     * @param importID Description of Parameter
     *
     * @return The Reference value
     */
    public Reference getReference(int importID) {
        return getReference(new Integer(importID));
    }


    /**
     * Retourne un listener mettant a jours la couche de persistance au niveau de
     * TranslationBehaviorHome lors des changements directe en Base.
     *
     * @return The DbChangeListener value
     *
     * @see net.codjo.utils.SimpleHome#DefaultDbChangeListener
     */
    public DbChangeListener getDbChangeListener() {
        return new DefaultDbChangeListener();
    }


    /**
     * Retourne toutes les behaviors présents dans la base.
     *
     * @return Une liste de tous les behaviors
     *
     * @exception PersistenceException -
     */
    public List getAllBehavior() throws PersistenceException {
        return getAllObjects();
    }


    /**
     * Retourne l'ID du comportement d'import.
     *
     * @return "I"
     */
    public final String getBehaviorID() {
        return "I";
    }


    /**
     * Retourne le label du comportement d'import.
     *
     * @return "Import"
     */
    public final String getBehaviorLabel() {
        return "Import";
    }


    /**
     * Retourne le model gerant les comportements d'import.
     *
     * @return <code>this</code>
     */
    public final Model getHome() {
        return this;
    }


    /**
     * Retourne le home pour les FieldImport.
     *
     * @return The FieldImportHome value
     */
    public FieldImportHome getFieldImportHome() {
        return fieldImportHome;
    }


    /**
     * Retourne tous les <code>IMPORT_SETTINGS_ID</code> dans un tabeau d'objets.
     *
     * @return Tous les IMPORT_SETTINGS_ID
     *
     * @exception SQLException -
     */
    public Object[] getAllId() throws SQLException {
        Statement stmt = getConnection().createStatement();
        List listId = new ArrayList();
        try {
            ResultSet rs =
                stmt.executeQuery("select IMPORT_SETTINGS_ID"
                    + " from PM_IMPORT_SETTINGS");
            while (rs.next()) {
                listId.add(rs.getObject(1));
            }
        }
        finally {
            stmt.close();
        }
        return listId.toArray();
    }


    /**
     * Convertit (pour patcher) Le fieldSeparator. Cette methode est utilise a cause des
     * BCP car il convertit "\t" en "\\t".
     *
     * @param fieldSeparator Description of Parameter
     *
     * @return Description of the Returned Value
     *
     * @todo cette methode sera a vire lorsque les BCP ne seront plus utilise
     */
    public String convertFieldSeparator(String fieldSeparator) {
        if ("\\t".equals(fieldSeparator)) {
            fieldSeparator = "\t";
        }
        return fieldSeparator;
    }


    /**
     * Charge un objet.
     *
     * @param rs -
     * @param ref -
     *
     * @return -
     *
     * @exception SQLException -
     * @exception PersistenceException -
     */
    protected Persistent loadObject(ResultSet rs, Reference ref)
            throws SQLException, PersistenceException {
        ImportBehavior b = (ImportBehavior)super.loadObject(rs, ref);
        b.setConnectionManager(connectionManager);
        fieldImportHome.loadFieldImport(b);
        return b;
    }
}
