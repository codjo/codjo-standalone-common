/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.operation.imports;
import net.codjo.persistent.Reference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * Classe permettant d'importer une Date.
 * 
 * <p>
 * Le format de date en entrée peut être de 6 types différents.
 * </p>
 *
 * @author $Author: blazart $
 * @version $Revision: 1.3 $
 */
class DateFieldImport extends FieldImport {
    /** Type de format de date en entrée */
    private DateFormat formatIN;
    private int inputDateFormat;
    /** Date NULL. ex: pour le format YYYY_MM_DD_SLASH la nullDate est "0000/00/00" */
    private String nullDate;

    /**
     * Constructor for the DateFieldImport object
     *
     * @param ref Description of Parameter
     * @param dbName Nom du champ DB de destination.
     * @param dateFormat Format de date en entrée.
     */
    public DateFieldImport(Reference ref, String dbName, int dateFormat) {
        super(ref, dbName);
        init(dateFormat);
    }


    /**
     * Constructor for test.
     *
     * @param dbName Nom du champ DB de destination.
     * @param dateFormat Format de date en entrée.
     */
    DateFieldImport(String dbName, int dateFormat) {
        super(dbName);
        init(dateFormat);
    }

    /**
     * Retourne le Format de Date en entree
     *
     * @return Le INPUT_DATE_FORMAT.
     */
    public int getInputDateFormat() {
        return inputDateFormat;
    }


    /**
     * Retourne le type SQL de l'objet produit par convertFieldToSQL.
     *
     * @return java.sql.Types.DATE.
     */
    public int getSQLType() {
        return java.sql.Types.DATE;
    }


    /**
     * Traduction du champ en objet Date SQL.
     * 
     * <p>
     * La date retournée est de type java.sql.Date.
     * </p>
     *
     * @param field Champ à traduire.
     *
     * @return Le champ en format SQL.
     *
     * @exception BadFormatException Mauvais format de date
     */
    public Object translateField(String field) throws BadFormatException {
        if (field == null) {
            return null;
        }
        if ("".equals(field)) {
            return null;
        }
        if (field.equals(nullDate)) {
            return null;
        }

        try {
            Date date = formatIN.parse(field);
            detectUndectedError(field);
            return new java.sql.Date(date.getTime());
        }
        catch (java.text.ParseException ex) {
            throw new BadFormatException(this, ex.getMessage());
        }
    }


    /**
     * Detecte quelques erreurs non vue par le formater.
     *
     * @param field
     *
     * @exception BadFormatException
     *
     * @see DateFieldImportTest#test_translateField_ErrorFormatYear()
     * @see DateFieldImportTest#test_translateField_ErrorALaCon()
     */
    private void detectUndectedError(String field)
            throws BadFormatException {
        if (nullDate.length() != field.length()) {
            throw new BadFormatException(this,
                "Mauvais format de date " + "(format " + nullDate + " mais " + field
                + " )");
        }
        for (int i = 0; i < field.length(); i++) {
            if ((nullDate.charAt(i) != '0' && nullDate.charAt(i) != field.charAt(i))
                    || field.charAt(i) == ' ') {
                throw new BadFormatException(this,
                    "Mauvais format de date " + "(format " + nullDate + " mais " + field
                    + " )");
            }
        }
    }


    /**
     * Init.
     *
     * @param dateFormat format de date.
     *
     * @throws IllegalArgumentException TODO
     */
    private void init(int dateFormat) {
        switch (dateFormat) {
            case FieldImportHome.YYYYMMDD:
                formatIN = new SimpleDateFormat("yyyyMMdd");
                nullDate = "00000000";
                break;
            case FieldImportHome.YYYY_MM_DD_HYPHEN:
                formatIN = new SimpleDateFormat("yyyy-MM-dd");
                nullDate = "0000-00-00";
                break;
            case FieldImportHome.YYYY_MM_DD_SLASH:
                formatIN = new SimpleDateFormat("yyyy/MM/dd");
                nullDate = "0000/00/00";
                break;
            case FieldImportHome.DD_MM_YY_HYPHEN:
                formatIN = new SimpleDateFormat("dd-MM-yy");
                nullDate = "00-00-00";
                break;
            case FieldImportHome.DD_MM_YYYY_HYPHEN:
                formatIN = new SimpleDateFormat("dd-MM-yyyy");
                nullDate = "00-00-0000";
                break;
            case FieldImportHome.DDMMYYYY:
                formatIN = new SimpleDateFormat("ddMMyyyy");
                nullDate = "00000000";
                break;
            case FieldImportHome.DD_MM_YYYY_SLASH:
                formatIN = new SimpleDateFormat("dd/MM/yyyy");
                nullDate = "00/00/0000";
                break;
            case FieldImportHome.DD_MM_YY_SLASH:
                formatIN = new SimpleDateFormat("dd/MM/yy");
                nullDate = "00/00/00";
                break;
            default:
                throw new IllegalArgumentException("Mauvais format de date");
        }
        inputDateFormat = dateFormat;
        formatIN.setLenient(false);
    }
}
