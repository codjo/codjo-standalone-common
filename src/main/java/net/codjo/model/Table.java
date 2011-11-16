/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.model;
// Persistence
import net.codjo.persistent.AbstractPersistent;
import net.codjo.persistent.Reference;

import java.util.List;
import java.util.Map;
/**
 * Cette classe fournit des informations sur la définition d'une table de la base.
 *
 * @version $Revision: 1.3 $
 *
 */
public class Table extends AbstractPersistent implements TableRecordingMode {
    private String tableName;
    private String dbTableName;
    private String step;
    private String source;
    private int recordingMode;
    private Map allColumns = null;
    private List pkNames = null;
    private String application;

    /**
     * Constructeur.
     *
     * @param ref La reference de la table.
     * @param dbTableName Le nom physique de la table.
     * @param tableName Le nom fonctionel de la table.
     * @param step Le niveau de formatage de la table.
     * @param source Le systeme d'origine de la table (ex: "GPI")
     * @param recordingMode Le mode d'historisation
     * @param application L'application à laquelle appartient la table (ex :
     *        "INFOCENTRE").
     *
     * @see net.codjo.model.TableRecordingMode
     */
    public Table(Reference ref, String dbTableName, String tableName, String step,
        String source, int recordingMode, String application) {
        super(ref);
        init(dbTableName, tableName, step, source, recordingMode, application);
    }


    /**
     * Constructor for the Table object
     *
     * @param ref La reference de la table
     * @param dbTableName Le nom physique de la table.
     * @param allColumns Toutes les colonnes de la table
     * @param pkNames Liste des cles primaires
     */
    Table(Reference ref, String dbTableName, Map allColumns, List pkNames) {
        super(ref);
        init(dbTableName, null, null, null, 0, null);
        setAllColumns(allColumns);
        setPkNames(pkNames);
    }

    /**
     * Positionne le nom fonctionnel de la table.
     *
     * @param newTableName The new TableName value
     */
    public void setTableName(String newTableName) {
        tableName = newTableName;
        setSynchronized(false);
    }


    /**
     * Sets the AllColumns attribute of the Table object
     *
     * @param newAllColumns The new AllColumns value
     *
     * @throws IllegalArgumentException TODO
     */
    public void setAllColumns(Map newAllColumns) {
        if (allColumns != null) {
            throw new IllegalArgumentException("Colonnes deja positionnees");
        }
        if (newAllColumns != null && newAllColumns.size() == 0) {
            newAllColumns = null;
        }
        allColumns = newAllColumns;
    }


    /**
     * Sets the PkNames attribute of the Table object
     *
     * @param newPkNames The new PkNames value
     *
     * @throws IllegalArgumentException TODO
     */
    public void setPkNames(List newPkNames) {
        if (pkNames != null) {
            throw new IllegalArgumentException();
        }
        pkNames = newPkNames;
    }


    /**
     * Retourne l'identifiant de cette table
     *
     * @return DB_TABLE_NAME_ID
     */
    public Integer getTableId() {
        return (Integer)getId();
    }


    /**
     * Retourne le nom physique de la table.
     *
     * @return Le nom physique de la table.
     */
    public String getDBTableName() {
        return dbTableName;
    }


    /**
     * Retourne le nom fonctionnel de la table. Ce nom peut etre non renseigne.
     *
     * @return Le nom fonctionel de la table (ou null).
     */
    public String getTableName() {
        if (tableName == null) {
            return getDBTableName();
        }
        else {
            return tableName;
        }
    }


    /**
     * Retourne le type SQL de la colonne. Attention : Si la colonne est inexistante une
     * exception NullPointerException sera lance.
     *
     * @param columnName Le nom physique de la colonne.
     *
     * @return Le type SQL
     *
     * @see java.sql.Types
     */
    public int getColumnSqlType(String columnName) {
        return ((Integer)getAllColumns().get(columnName)).intValue();
    }


    /**
     * Retourne le niveau de formatage de la table. Cette information est optionnelle.
     *
     * @return Le niveau de formatage de la table (ou null).
     */
    public String getTableStep() {
        return step;
    }


    /**
     * Retourne le nombre de colonne de la table.
     *
     * @return Le nombre de colonnes de la table
     */
    public int getNumberOfCol() {
        return getAllColumns().size();
    }


    /**
     * Retourne le systeme d'origine de la table (ex: "GPI"). Cette information est
     * optionnelle.
     *
     * @return The Source value
     */
    public String getSource() {
        return source;
    }


    /**
     * Retourne toutes les colonnes de la table.
     *
     * @return Map (clef = nom de la colonne) (valeur = type SQL)
     */
    public Map getAllColumns() {
        if (allColumns == null) {
            setAllColumns(getTableHome().determineAllColumns(getDBTableName()));
        }
        return allColumns;
    }


    /**
     * Retourne le nom de l'application à laquelle appartient la Table.
     *
     * @return Le nom de l'application.
     */
    public String getApplication() {
        return application;
    }


    /**
     * Retourne le mode d'historisation de la table.
     *
     * @return Le mode d'historisation.
     *
     * @see net.codjo.model.TableRecordingMode
     */
    public int getRecordingMode() {
        return recordingMode;
    }


    /**
     * Gets the PkNames attribute of the Table object
     *
     * @return The PkNames value
     */
    public List getPkNames() {
        if (pkNames == null) {
            pkNames = getTableHome().determinePkNames(getDBTableName());
        }
        return pkNames;
    }


    /**
     * Indique si la colonne fait partie de la table.
     *
     * @param dbColumnName Nom physique de la colonne
     *
     * @return <code>true</code> si appartient a la table
     */
    public boolean containsColumn(String dbColumnName) {
        return getAllColumns().containsKey(dbColumnName);
    }


    /**
     * DOCUMENT ME!
     *
     * @return -
     */
    public String toString() {
        if (tableName == null) {
            return dbTableName;
        }
        return tableName;
    }


    /**
     * Permet de tester l'égalité entre des objets de ce type
     *
     * @param obj L'objet à tester
     *
     * @return Egalité VRAI/FAUX
     */
    public boolean equals(Object obj) {
        if (obj instanceof Table) {
            return dbTableName.equals(((Table)obj).dbTableName);
        }
        return false;
    }


    /**
     * Gets the TableHome attribute of the Table object
     *
     * @return The TableHome value
     */
    private TableHome getTableHome() {
        return (TableHome)getReference().getModel();
    }


    /**
     * Initialisation de l'objet
     *
     * @param dbTableName -
     * @param tableName -
     * @param step -
     * @param source -
     * @param recordingMode -
     * @param application -
     *
     * @throws IllegalArgumentException TODO
     */
    private void init(String dbTableName, String tableName, String step, String source,
        int recordingMode, String application) {
        if (dbTableName == null) {
            throw new IllegalArgumentException("Nom physique de la table incorrect");
        }
        if (tableName != null && "".equals(tableName)) {
            tableName = null;
        }
        this.tableName = tableName;
        this.dbTableName = dbTableName;
        this.step = step;
        this.source = source;
        this.recordingMode = recordingMode;
        this.application = application;
    }
}
