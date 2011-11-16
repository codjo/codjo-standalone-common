/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.utils;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
/**
 * Liste de champs SQL.
 *
 * @author $Author: marcona $
 * @version $Revision: 1.4 $
 *
 * @see SQLField
 */
public class SQLFieldList {
    private Map hashTable = new java.util.HashMap();

    /**
     * Constructor par défaut.
     */
    public SQLFieldList() {}


    /**
     * Construction d'une liste de champs a partir de la definition de la table.
     *
     * @param dbTableName Le nom physique de la table.
     * @param con La connection.
     *
     * @exception SQLException Erreur d'access a la base.
     */
    public SQLFieldList(String dbTableName, Connection con)
            throws SQLException {
        this(dbTableName, con, null);
    }


    /**
     * Construction d'une liste de champs a partir de la definition de la table pour un
     * catalogue donné.
     *
     * @param dbTableName Le nom physique de la table.
     * @param con La connection.
     * @param catalog Le catalogue de la table.
     *
     * @exception SQLException Erreur d'access a la base.
     */
    public SQLFieldList(String dbTableName, Connection con, String catalog)
            throws SQLException {
        DatabaseMetaData md = con.getMetaData();
        ResultSet rs = md.getColumns(catalog, null, dbTableName, null);

        while (rs.next()) {
            String dbFieldName = rs.getString(4);
            int sqlType = rs.getInt(5);
            addField(dbFieldName, sqlType);
        }
    }

    /**
     * Fusionne les éléments de list dans cette SQLFieldList.
     *
     * @param list La liste à ajouter
     */
    public void addAll(SQLFieldList list) {
        hashTable.putAll(list.hashTable);
    }


    /**
     * Ajoute un SQLField de type Bit.
     *
     * @param dbFieldName nom physique du champs
     */
    public void addBitField(String dbFieldName) {
        hashTable.put(dbFieldName, new SQLField(Types.BIT, dbFieldName));
    }


    /**
     * Ajoute un SQLField de type specifie.
     *
     * @param dbFieldName nom physique du champs
     * @param sqlType Le type SQL du champs.
     *
     * @see java.sql.Types
     */
    public void addField(String dbFieldName, int sqlType) {
        if (sqlType == java.sql.Types.DATE
                || sqlType == java.sql.Types.TIME
                || sqlType == java.sql.Types.TIMESTAMP) {
            hashTable.put(dbFieldName, new SQLDateField(sqlType, dbFieldName));
        }
        else {
            hashTable.put(dbFieldName, new SQLField(sqlType, dbFieldName));
        }
    }


    /**
     * Ajoute un SQLField de type Float.
     *
     * @param dbFieldName nom physique du champs
     */
    public void addFloatField(String dbFieldName) {
        hashTable.put(dbFieldName, new SQLField(Types.FLOAT, dbFieldName));
    }


    /**
     * Ajoute un SQLField de type Integer.
     *
     * @param dbFieldName nom physique du champs
     */
    public void addIntegerField(String dbFieldName) {
        hashTable.put(dbFieldName, new SQLField(Types.INTEGER, dbFieldName));
    }


    /**
     * Ajoute un SQLField de type String.
     *
     * @param dbFieldName nom physique du champs
     */
    public void addStringField(String dbFieldName) {
        hashTable.put(dbFieldName, new SQLField(Types.VARCHAR, dbFieldName));
    }


    /**
     * Efface toutes les valeurs contenue dans les SQLField.
     */
    public void clearValues() {
        for (Iterator iter = hashTable.values().iterator(); iter.hasNext();) {
            SQLField field = (SQLField)iter.next();
            field.setValue(null);
        }
    }


    /**
     * Retourne un iterator sur les noms de champs.
     *
     * @return Description of the Returned Value
     */
    public Iterator fieldNames() {
        return hashTable.keySet().iterator();
    }


    /**
     * Retourne un ensemble contenant les noms de champs.
     *
     * @return Description of the Returned Value
     */
    public Set fieldNamesSet() {
        return hashTable.keySet();
    }


    /**
     * Gets the FieldType attribute of the SQLFieldList object
     *
     * @param dbFieldName nom physique du champs
     *
     * @return Le type SQL du champs (tel que definie dans Types)
     *
     * @see java.sql.Types
     */
    public int getFieldType(String dbFieldName) {
        return getField(dbFieldName).getSQLType();
    }


    /**
     * Gets the FieldValue attribute of the SQLFieldList object
     *
     * @param dbFieldName nom physique du champs
     *
     * @return La valeur du champs
     */
    public Object getFieldValue(String dbFieldName) {
        return getField(dbFieldName).getValue();
    }


    /**
     * Gets the SortedDBFieldNameList attribute of the SQLFieldList object
     *
     * @return La liste triée des noms physiques de la table courante.
     */
    public List getSortedDBFieldNameList() {
        List listField = new ArrayList(hashTable.keySet());
        Collections.sort(listField);
        return listField;
    }


    /**
     * Enleve un SQLField de la liste. Si la colonne n'existe pas, la liste n'est pas
     * modifiee.
     *
     * @param dbFieldName nom physique du champs
     */
    public void removeField(String dbFieldName) {
        hashTable.remove(dbFieldName);
    }


    /**
     * Sets the FieldValue attribute of the SQLFieldList object
     *
     * @param dbFieldName nom physique du champs
     * @param v La nouvelle valeur du champs.
     */
    public void setFieldValue(String dbFieldName, Object v) {
        getField(dbFieldName).setValue(v);
    }


    /**
     * Sets the FieldValue attribute of the SQLFieldList object (for Date value).
     * Converts a java.util.Date into java.sql.Date .
     *
     * @param dbFieldName nom physique du champs
     * @param d La nouvelle date du champs.
     */
    public void setFieldValue(String dbFieldName, java.util.Date d) {
        getField(dbFieldName).setValue(d);
    }


    /**
     * Retourne le nombre de champs.
     *
     * @return Description of the Returned Value
     */
    public int size() {
        return hashTable.size();
    }


    /**
     * DOCUMENT ME!
     *
     * @return
     */
    public String toString() {
        StringBuffer buffer = new StringBuffer("SQLFieldList(");

        for (Iterator iter = fieldNames(); iter.hasNext();) {
            String obj = (String)iter.next();
            buffer.append(obj);
            if (iter.hasNext()) {
                buffer.append(", ");
            }
        }

        return buffer.append(")").toString();
    }


    /**
     * Retourne un iterator sur les champs (SQLField).
     *
     * @return Description of the Returned Value
     */
    Iterator sqlFields() {
        return hashTable.values().iterator();
    }


    /**
     * Retourne le SQLField ayant ce nom physique.
     *
     * @param dbFieldName nom physique du champs
     *
     * @return Le SQLField (non null).
     *
     * @throws java.util.NoSuchElementException TODO
     */
    private SQLField getField(String dbFieldName) {
        SQLField field = (SQLField)hashTable.get(dbFieldName);
        if (field == null) {
            throw new java.util.NoSuchElementException(dbFieldName);
        }
        return field;
    }
}
