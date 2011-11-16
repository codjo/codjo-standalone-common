/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.operation.imports;
import net.codjo.persistent.Reference;
import net.codjo.util.string.StringUtil;
/**
 * Classe permettant d'importer un nombre.
 *
 * @author $Author: spinae $
 * @version $Revision: 1.2 $
 */
class NumberFieldImport extends FieldImport {
    private String decimalSeparator = null;
    private boolean isInteger = false;


    /**
     * Constructeur dédié à l'import d'entiers.
     *
     * @param ref    Description of Parameter
     * @param dbName Nom du champ DB de destination.
     */
    NumberFieldImport(Reference ref, String dbName) {
        super(ref, dbName);
    }


    /**
     * Constructeur dédié à l'import de rééls.
     *
     * @param ref       Description of Parameter
     * @param dbName    Nom du champ DB de destination.
     * @param separator Séparateur de décimal.
     */
    NumberFieldImport(Reference ref, String dbName, char separator) {
        super(ref, dbName);
        init(separator);
    }


    /**
     * Constructeur dédié à l'import d'entiers (pour test).
     *
     * @param dbName Nom du champ DB de destination.
     */
    NumberFieldImport(String dbName) {
        super(dbName);
        isInteger = true;
    }


    /**
     * Constructeur dédié à l'import de rééls (pour test).
     *
     * @param dbName    Nom du champ DB de destination.
     * @param separator Séparateur de décimal.
     */
    NumberFieldImport(String dbName, char separator) {
        super(dbName);
        init(separator);
    }


    /**
     * Retourne le separateur de decimal
     *
     * @return "." ou ","
     */
    public String getDecimalSeparator() {
        return decimalSeparator;
    }


    /**
     * Retourne le type SQL de l'objet produit par convertFieldToSQL.
     *
     * @return java.sql.Types.INTEGER ou FLOAT.
     */
    @Override
    public int getSQLType() {
        if (isInteger) {
            return java.sql.Types.INTEGER;
        }
        else {
            return java.sql.Types.NUMERIC;
        }
    }


    /**
     * Traduction du champ en objet Integer ou Float.
     *
     * @param field Champ à traduire.
     *
     * @return Le champ en format SQL.
     *
     * @throws BadFormatException Mauvais format de nombre
     */
    @Override
    public Object translateField(String field) throws BadFormatException {
        if (field == null) {
            return null;
        }

        field = StringUtil.removeAllCharOccurrence(field, ' ');
        if ("".equals(field)) {
            return null;
        }

        try {
            if (isInteger) {
                return new Integer(field);
            }
            else {
                return parseDecimal(field);
            }
        }
        catch (java.lang.NumberFormatException ex) {
            throw new BadFormatException(this, ex.getMessage());
        }
    }


    /**
     * Inialise.
     *
     * @param separator separator de decimal.
     *
     * @throws IllegalArgumentException TODO
     */
    private void init(char separator) {
        if (separator != '.' && separator != ',') {
            throw new IllegalArgumentException("Separateur de décimal "
                                               + "non supporté (" + separator + ")");
        }

        decimalSeparator = "" + separator;

        isInteger = false;
    }


    /**
     * DOCUMENT ME!
     */
    private Number parseDecimal(String field) throws BadFormatException {
        if (",".equals(decimalSeparator)) {
            if (field.contains(".")) {
                throw new BadFormatException(this, field);
            }
            field = field.replace(',', '.');
        }
        return new java.math.BigDecimal(field);
    }
}
