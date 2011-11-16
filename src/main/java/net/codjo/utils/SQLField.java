/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.utils;
/**
 * SQL Field. Un champs SQL est definie par un nom physique de colonne, un type SQL et
 * eventuellement une valeur.
 *
 * @author $Author: gonnot $
 * @version $Revision: 1.1.1.1 $
 */
class SQLField {
    private String name;
    private Object value;
    private int sqlType;

    /**
     * Constructor for the SQLField object
     *
     * @param sqlType Le type SQL du champs
     * @param n Le nom physique du champs
     */
    SQLField(int sqlType, String n) {
        this.sqlType = sqlType;
        name = n;
    }


    /**
     * Constructor for the SQLField object
     *
     * @param sqlType Le type SQL du champs
     * @param n Le nom physique du champs
     * @param v la valeur du champs
     */
    SQLField(int sqlType, String n, Object v) {
        this.sqlType = sqlType;
        name = n;
        setValue(v);
    }

    /**
     * Positionne la valeur du champs.
     *
     * @param v The new value
     */
    public void setValue(Object v) {
        value = v;
    }


    /**
     * Retourne le nom physique du champs (colonne).
     *
     * @return The Name value
     */
    public String getName() {
        return name;
    }


    /**
     * Retourne la valeur courante du champs.
     *
     * @return The Value value
     */
    public Object getValue() {
        return value;
    }


    /**
     * Retourne le type SQL du champs.
     *
     * @return The SQLType value
     */
    public int getSQLType() {
        return sqlType;
    }
}



/**
 * SQL Field specific pour le type SQL date et assimile (TIME, TIMESTAMP,...).
 *
 * @author $Author: gonnot $
 * @version $Revision: 1.1.1.1 $
 */
class SQLDateField extends SQLField {
    /**
     * Constructor for the SQLDateField object
     *
     * @param sqlDateType Type sql Date ( DATE, TIMESTAMP, TIME)
     * @param n Nom physique du champs
     */
    SQLDateField(int sqlDateType, String n) {
        super(sqlDateType, n);
    }

    /**
     * Positionne la valeur. Si la valeur est de type <code>java.util.Date</code> elle
     * est convertit en <code>java.sql.Date</code>
     *
     * @param v The new Value value
     */
    public void setValue(Object v) {
        if (v != null && v.getClass() == java.util.Date.class) {
            super.setValue(new java.sql.Timestamp(((java.util.Date)v).getTime()));
        }
        else {
            super.setValue(v);
        }
    }
}
