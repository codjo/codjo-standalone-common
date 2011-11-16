/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.operation.imports;
import net.codjo.persistent.Persistent;
import net.codjo.persistent.Reference;
import net.codjo.persistent.sql.AbstractHome;
import net.codjo.utils.QueryHelper;
import net.codjo.utils.SQLFieldList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

/**
 * Classe Home pour les objets FieldImport.
 *
 * @author $Author: marcona $
 * @version $Revision: 1.4 $
 *
 */
public class FieldImportHome extends AbstractHome {
    /** Format de type = "20011230" */
    public static final int YYYYMMDD = 1;
    /** Format de type = "2001-12-30" */
    public static final int YYYY_MM_DD_HYPHEN = 2;
    /** Format de type = "2001/12/30" */
    public static final int YYYY_MM_DD_SLASH = 3;
    /** Format de type = "30-12-01" */
    public static final int DD_MM_YY_HYPHEN = 4;
    /** Format de type = "30-12-2001" */
    public static final int DD_MM_YYYY_HYPHEN = 5;
    /** Format de type = "30122001" */
    public static final int DDMMYYYY = 6;
    /** Format de type = "30/12/2001" */
    public static final int DD_MM_YYYY_SLASH = 7;
    /** Format de type = "30/12/01" */
    public static final int DD_MM_YY_SLASH = 8;
    /** Formats des champs destination */
    public static final char STRING_FIELD = 'S';
    /** Description of the Field */
    public static final char BOOLEAN_FIELD = 'B';
    /** Description of the Field */
    public static final char NUMERIC_FIELD = 'N';
    /** Description of the Field */
    public static final char DATE_FIELD = 'D';
    private static Map dateFormats = null;
    private PreparedStatement selectAllByImportID;

    /**
     * Constructor for the FieldImportHome object
     *
     * @param con Une connection
     *
     * @exception SQLException Impossible de creer les PreparedStatement
     */
    FieldImportHome(Connection con) throws SQLException {
        super(con);

        selectAllByImportID =
            con.prepareStatement("select * " + "from PM_FIELD_IMPORT_SETTINGS "
                                 + "where IMPORT_SETTINGS_ID=?");

        SQLFieldList selectById = new SQLFieldList();
        selectById.addIntegerField("IMPORT_SETTINGS_ID");
        selectById.addStringField("DB_DESTINATION_FIELD_NAME");

        SQLFieldList tableFields = new SQLFieldList("PM_FIELD_IMPORT_SETTINGS", con);

        queryHelper =
            new QueryHelper("PM_FIELD_IMPORT_SETTINGS", con, tableFields, selectById);

        setBufferOn(false);
    }

    /**
     * Retourne une reference avec ID.
     *
     * @param id IMPORT_SETTINGS_ID
     * @param dbFieldName DB_DESTINATION_FIELD_NAME
     *
     * @return The Reference value
     */
    public Reference getReference(int id, String dbFieldName) {
        return getReference(new FieldImportPK(id, dbFieldName));
    }


    /**
     * Charge les FieldImport associé au comportement d'import.
     *
     * @param behavior -
     *
     * @exception SQLException -
     */
    public void loadFieldImport(ImportBehavior behavior)
            throws SQLException {
        int importId = ((Integer)behavior.getId()).intValue();

        selectAllByImportID.setInt(1, importId);
        ResultSet rs = selectAllByImportID.executeQuery();

        while (rs.next()) {
            FieldImport fi =
                (FieldImport)loadObject(rs, getReference(importId, rs.getString(5)));
            behavior.addFieldImport(fi);
        }
    }


    /**
     * Efface tous les FieldImport attaches au ImportBehavior.
     *
     * @param importId IMPORT_SETTINGS_ID
     *
     * @exception SQLException
     */
    public void deleteFieldImport(int importId) throws SQLException {
        Statement stmt = getConnection().createStatement();
        try {
            stmt.executeUpdate("delete from PM_FIELD_IMPORT_SETTINGS "
                               + "where IMPORT_SETTINGS_ID=" + importId);
        }
        finally {
            stmt.close();
        }
    }


    /**
     * Creation d'un nouveau FieldImport.
     *
     * @param importId IMPORT_SETTINGS_ID
     * @param dbName DB_DESTINATION_FIELD_NAME
     * @param position POSITION
     * @param length LENGTH
     * @param fieldType DESTINATION_FIELD_TYPE
     * @param decimalSeparator DECIMAL_SEPARATOR
     * @param inputDateFormat INPUT_DATE_FORMAT
     * @param removeLeftZeros REMOVE_LEFT_ZEROS
     *
     * @return un nouveau FieldImport (non enregistre en Base)
     */
    public FieldImport newFieldImport(int importId, String dbName, int position,
                                      int length, char fieldType, String decimalSeparator, int inputDateFormat,
                                      boolean removeLeftZeros) {
        Reference ref = new Reference(this, new FieldImportPK(importId, dbName));
        addReference(ref);
        return buildFieldImport(ref, dbName, position, length, fieldType,
                                decimalSeparator, inputDateFormat, removeLeftZeros);
    }


    /**
     * Construction d'une reference a partir d'un ResultSet.
     *
     * @param rs Le ResultSet a utilise pour la construction.
     *
     * @return Une instance non null.
     *
     * @exception SQLException En cas d'erreur d'acces a la base
     */
    protected Reference loadReference(ResultSet rs)
            throws SQLException {
        return getReference(rs.getInt("IMPORT_SETTINGS_ID"),
                            rs.getString("DB_DESTINATION_FIELD_NAME"));
    }


    /**
     * Methode utilitaire qui remplit la clause where du QueryHelper.
     *
     * @param ref La reference utilisee pour remplir la clause.
     */
    protected void fillQueryHelperSelector(Reference ref) {
        FieldImportPK pk = (FieldImportPK)ref.getId();
        queryHelper.setSelectorValue("IMPORT_SETTINGS_ID", pk.importSettingsID);
        queryHelper.setSelectorValue("DB_DESTINATION_FIELD_NAME", pk.dbDestFieldName);
    }


    /**
     * Methode utilitaire qui remplit la requete pour une insertion. L'objet reference
     * doit etre deja charge.
     *
     * @param ref La reference de l'objet a inserer.
     *
     * @throws Error TODO
     */
    protected void fillQueryHelperForInsert(Reference ref) {
        FieldImport obj = (FieldImport)ref.getLoadedObject();
        FieldImportPK pk = (FieldImportPK)ref.getId();

        // Generic
        queryHelper.setInsertValue("IMPORT_SETTINGS_ID", pk.importSettingsID);
        queryHelper.setInsertValue("POSITION", obj.getPosition());
        queryHelper.setInsertValue("LENGTH", obj.getLength());
        queryHelper.setInsertValue("DB_DESTINATION_FIELD_NAME", obj.getDBDestFieldName());
        queryHelper.setInsertValue("REMOVE_LEFT_ZEROS", obj.getRemoveLeftZeros());

        // Specific
        if (obj.getClass() == NumberFieldImport.class) {
            queryHelper.setInsertValue("DECIMAL_SEPARATOR",
                                       ((NumberFieldImport)obj).getDecimalSeparator());
            queryHelper.setInsertValue("DESTINATION_FIELD_TYPE", "N");
        }
        else if (obj.getClass() == DateFieldImport.class) {
            queryHelper.setInsertValue("INPUT_DATE_FORMAT",
                                       ((DateFieldImport)obj).getInputDateFormat());
            queryHelper.setInsertValue("DESTINATION_FIELD_TYPE", "D");
        }
        else if (obj.getClass() == StringFieldImport.class) {
            queryHelper.setInsertValue("DESTINATION_FIELD_TYPE", "S");
        }
        else if (obj.getClass() == BooleanFieldImport.class) {
            queryHelper.setInsertValue("DESTINATION_FIELD_TYPE", "B");
        }
        else {
            throw new Error("Type de FieldImport non supporte : " + obj.getClass());
        }
    }


    /**
     * Construction d'un FieldImport.
     * 
     * <p>
     * L'objet est instancié en fonction des information contenu dans le ResultSet
     * (DESTINATION_FIELD_TYPE).
     * </p>
     *
     * @param rs -
     * @param ref Reference sur le field import a construire.
     *
     * @return Une instance non null.
     *
     * @exception SQLException -
     */
    protected Persistent loadObject(ResultSet rs, Reference ref)
            throws SQLException {
        if (ref.isLoaded()) {
            return ref.getLoadedObject();
        }

        return buildFieldImport(ref, rs.getString("DB_DESTINATION_FIELD_NAME"),
                                rs.getInt("POSITION"), rs.getInt("LENGTH"),
                                rs.getString("DESTINATION_FIELD_TYPE").charAt(0),
                                rs.getString("DECIMAL_SEPARATOR"), rs.getInt("INPUT_DATE_FORMAT"),
                                rs.getBoolean("REMOVE_LEFT_ZEROS"));
    }


    /**
     * Construction d'un field import.
     *
     * @param ref La reference de l'objet a construire
     * @param dbName DB_DESTINATION_FIELD_NAME
     * @param position POSITION
     * @param length LENGTH
     * @param fieldType DESTINATION_FIELD_TYPE
     * @param decimalSeparator DECIMAL_SEPARATOR
     * @param inputDateFormat INPUT_DATE_FORMAT
     * @param removeLeftZeros REMOVE_LEFT_ZEROS
     *
     * @return un nouveau FieldImport
     *
     * @throws Error TODO
     */
    private FieldImport buildFieldImport(Reference ref, String dbName, int position,
                                         int length, char fieldType, String decimalSeparator, int inputDateFormat,
                                         boolean removeLeftZeros) {
        FieldImport fieldImport;

        switch (fieldType) {
            case STRING_FIELD:
                fieldImport = new StringFieldImport(ref, dbName);
                break;
            case BOOLEAN_FIELD:
                fieldImport = new BooleanFieldImport(ref, dbName);
                break;
            case NUMERIC_FIELD:
                if (decimalSeparator == null) {
                    fieldImport = new NumberFieldImport(ref, dbName);
                }
                else {
                    fieldImport =
                        new NumberFieldImport(ref, dbName, decimalSeparator.charAt(0));
                }
                break;
            case DATE_FIELD:
                fieldImport = new DateFieldImport(ref, dbName, inputDateFormat);
                break;
            default:
                throw new Error("Type de champ inconnu : '" + fieldType + "'");
        }

        fieldImport.setPosition(position);
        fieldImport.setLength(length);
        fieldImport.setRemoveLeftZeros(removeLeftZeros);
        return fieldImport;
    }


    /**
     * Creation d'un nouveau FieldImport.
     *
     * @param destFieldName Description of the Parameter
     * @param destFieldType Description of the Parameter
     * @param decimalSeparator DECIMAL_SEPARATOR
     * @param inputDateFormat INPUT_DATE_FORMAT
     *
     * @return un nouveau FieldImport
     *
     * @throws Error TODO
     */
    public static FieldImport newFieldImport(String destFieldName, int destFieldType,
                                             String decimalSeparator, String inputDateFormat) {
        FieldImport fieldImport;

        switch (destFieldType) {
            case Types.CHAR:
            case Types.VARCHAR:
                fieldImport = new StringFieldImport(destFieldName);
                break;
            case Types.BIT:
                fieldImport = new BooleanFieldImport(destFieldName);
                break;
            case Types.INTEGER:
                fieldImport = new NumberFieldImport(destFieldName);
                break;
            case Types.NUMERIC:
                if (decimalSeparator == null) {
                    fieldImport = new NumberFieldImport(destFieldName);
                }
                else {
                    fieldImport =
                        new NumberFieldImport(destFieldName, decimalSeparator.charAt(0));
                }
                break;
            case Types.DATE:
                if (dateFormats == null) {
                    initDateFormats();
                }
                fieldImport =
                    new DateFieldImport(destFieldName, getInputDateFormat(inputDateFormat));
                break;
            default:
                throw new Error("Type de champ inconnu : '" + destFieldType + "'");
        }

        return fieldImport;
    }


    public static String getDateFormat(int code) {
        switch (code) {
            case 0:
                return null;
            case 1:
                return "yyyyMMdd";
            case 2:
                return "yyyy-MM-dd";
            case 3:
                return "yyyy/MM/dd";
            case 4:
                return "dd-MM-yy";
            case 5:
                return "dd-MM-yyyy";
            case 6:
                return "ddMMyyyy";
            case 7:
                return "dd/MM/yyyy";
            case 8:
                return "dd/MM/yy";
            default:
                throw new IllegalArgumentException("code de format de date non valide : "
                                                   + code);
        }
    }


    /**
     * Initialisation du tableau des format dates.
     */
    private static void initDateFormats() {
        dateFormats = new HashMap();
        dateFormats.put("yyyymmdd", "1");
        dateFormats.put("yyyy-mm-dd", "2");
        dateFormats.put("yyyy/mm/dd", "3");
        dateFormats.put("dd-mm-yy", "4");
        dateFormats.put("dd-mm-yyyy", "5");
        dateFormats.put("ddmmyyyy", "6");
        dateFormats.put("dd/mm/yyyy", "7");
        dateFormats.put("dd/mm/yy", "8");
    }


    /**
     * Retourne l'identifiant du format <code>inputDateFormat</code>
     *
     * @param inputDateFormat Description of the Parameter
     *
     * @return La valeur de inputDateFormat
     *
     * @throws IllegalArgumentException TODO
     */
    private static int getInputDateFormat(String inputDateFormat) {
        String dateft = (String)dateFormats.get(inputDateFormat);
        if (dateft == null) {
            throw new IllegalArgumentException("Le format : " + inputDateFormat
                                               + " n'est pas valide.");
        }
        return Integer.parseInt(dateft);
    }

    /**
     * Clef primaire pour un field Import.
     * 
     * <p>
     * Une clef primaire est constituee du IMPORT_SETTINGS_ID et de
     * DB_DESTINATION_FIELD_NAME
     * </p>
     *
     * @author $Author: marcona $
     * @version $Revision: 1.4 $
     */
    private static class FieldImportPK {
        Integer importSettingsID;
        String dbDestFieldName;

        /**
         * Constructor for the FieldImportPK object
         *
         * @param id -
         * @param dbFieldName -
         */
        FieldImportPK(int id, String dbFieldName) {
            importSettingsID = new Integer(id);
            dbDestFieldName = dbFieldName;
        }

        /**
         * DOCUMENT ME!
         *
         * @return Description of the Returned Value
         */
        public String toString() {
            return "(" + importSettingsID + "," + dbDestFieldName + ")";
        }
    }
}
