/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.utils;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;
/**
 * Overview.
 * 
 * <p>
 * Description
 * </p>
 *
 * @author $Author: blazart $
 * @version $Revision: 1.2 $
 *
 */
public class SqlTypeConverter {
    // Constante
    private static final Integer ZERO_INTEGER = new Integer(0);
    private static final BigDecimal ZERO_NUMERIC = new BigDecimal(0);
    private static final Float ZERO_FLOAT = new Float(0);
    private static final Double ZERO_DOUBLE = new Double(0);
    private static final Long ZERO_BIGINT = new Long(0);

//	private final static java.sql.Timestamp ZERO_TIMESTAMP = new java.sql.Timestamp(0);
//	private final static java.sql.Date ZERO_DATE = new java.sql.Date(0);
    private static Map javaToSql = new HashMap();

    static {
        javaToSql.put(char.class, new Integer(Types.CHAR));
        javaToSql.put(String.class, new Integer(Types.VARCHAR));
        javaToSql.put(int.class, new Integer(Types.INTEGER));
        javaToSql.put(long.class, new Integer(Types.BIGINT));
        javaToSql.put(float.class, new Integer(Types.FLOAT));
        javaToSql.put(double.class, new Integer(Types.DOUBLE));
        javaToSql.put(boolean.class, new Integer(Types.BIT));
        javaToSql.put(Timestamp.class, new Integer(Types.TIMESTAMP));
        javaToSql.put(Time.class, new Integer(Types.TIME));
        javaToSql.put(java.sql.Date.class, new Integer(Types.DATE));
        javaToSql.put(java.util.Date.class, new Integer(Types.DATE));
        javaToSql.put(BigDecimal.class, new Integer(Types.NUMERIC));
    }

    /**
     * Indique si le type sql correspond a un scalaire.
     *
     * @param sqlType devine !
     *
     * @return idem !
     */
    public static boolean isScalar(Integer sqlType) {
        switch (sqlType.intValue()) {
            case Types.SMALLINT:
            case Types.INTEGER:
            case Types.FLOAT:
            case Types.DOUBLE:
            case Types.BIT:
            case Types.BIGINT:
                return true;
            default:
                return false;
        }
    }


    /**
     * Indique si le type sql correspond a un reel.
     *
     * @param sqlType devine !
     *
     * @return idem !
     */
    public static boolean isDouble(Integer sqlType) {
        switch (sqlType.intValue()) {
            case Types.FLOAT:
            case Types.DOUBLE:
                return true;
            default:
                return false;
        }
    }


    /**
     * Indique si le type sql correspond a une date.
     *
     * @param sqlType devine !
     *
     * @return idem !
     */
    public static boolean isDate(Integer sqlType) {
        switch (sqlType.intValue()) {
            case Types.DATE:
            case Types.TIMESTAMP:
                return true;
            default:
                return false;
        }
    }


    /**
     * Indique si le type sql correspond a un entier.
     *
     * @param sqlType devine !
     *
     * @return idem !
     */
    public static boolean isInteger(Integer sqlType) {
        switch (sqlType.intValue()) {
            case Types.SMALLINT:
            case Types.INTEGER:
                return true;
            default:
                return false;
        }
    }


    /**
     * Indique si le type sql correspond a une String.
     *
     * @param sqlType devine !
     *
     * @return idem !
     */
    public static boolean isString(Integer sqlType) {
        switch (sqlType.intValue()) {
            case Types.CHAR:
            case Types.VARCHAR:
            case Types.LONGVARCHAR:
                return true;
            default:
                return false;
        }
    }


    /**
     * Retourne la valeur par defaut attache a un type SQL.
     *
     * @param sqlType
     *
     * @return La valeur zero / ou null
     *
     * @throws IllegalArgumentException TODO
     */
    public static Object getDefaultSqlValue(int sqlType) {
        switch (sqlType) {
            case Types.CHAR:
            case Types.VARCHAR:
            case Types.LONGVARCHAR:
                return "";
            case Types.SMALLINT:
            case Types.INTEGER:
                return ZERO_INTEGER;
            case Types.FLOAT:
                return ZERO_FLOAT;
            case Types.DOUBLE:
                return ZERO_DOUBLE;
            case Types.NUMERIC:
                return ZERO_NUMERIC;
            case Types.BIGINT:
                return ZERO_BIGINT;
            case Types.TIMESTAMP:
//                return ZERO_TIMESTAMP;
            case Types.DATE:
                return null;

//				return ZERO_DATE;
            case Types.BIT:
                return Boolean.FALSE;
            default:
                throw new IllegalArgumentException("Type SQL" + " est non supporte : "
                    + sqlType);
        }
    }


    /**
     * Indique si le type SQL est de format numerique.
     *
     * @param sqlType type sql.
     *
     * @return <code>true</code> si oui.
     */
    public static boolean isNumeric(int sqlType) {
        if (sqlType == Types.INTEGER
                || sqlType == Types.NUMERIC
                || sqlType == Types.FLOAT
                || sqlType == Types.DOUBLE
                || sqlType == Types.DECIMAL
                || sqlType == Types.SMALLINT
                || sqlType == Types.BIGINT) {
            return true;
        }
        else {
            return false;
        }
    }


    /**
     * DOCUMENT ME!
     *
     * @param cl Description of Parameter
     *
     * @return Description of the Returned Value
     *
     * @throws IllegalArgumentException TODO
     */
    public static Integer toSqlType(Class cl) {
        Integer sqlType = (Integer)javaToSql.get(cl);
        if (sqlType == null) {
            throw new IllegalArgumentException("Type JAVA " + " non supporte : " + cl);
        }
        return sqlType;
    }


    /**
     * Retourne le type Java associe a un type SQL.
     *
     * @param sqlType
     *
     * @return Une String contenant le type Java (ex : "String")
     *
     * @throws IllegalArgumentException TODO
     */
    public static Class toJavaType(int sqlType) {
        switch (sqlType) {
            case Types.CHAR:
            case Types.VARCHAR:
            case Types.LONGVARCHAR:
                return String.class;
            case Types.SMALLINT:
            case Types.INTEGER:
                return int.class;
            case Types.FLOAT:
                return float.class;
            case Types.DOUBLE:
                return double.class;
            case Types.BIGINT:
                return long.class;
            case Types.NUMERIC:
                return java.math.BigDecimal.class;
            case Types.TIMESTAMP:
                return Timestamp.class;
            case Types.TIME:
                return Time.class;
            case Types.DATE:
                return java.sql.Date.class;
            case Types.BIT:
                return boolean.class;
            default:
                throw new IllegalArgumentException("Type SQL" + " non supporte : "
                    + sqlType);
        }
    }
}
