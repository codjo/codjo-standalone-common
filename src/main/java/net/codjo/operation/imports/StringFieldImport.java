/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.operation.imports;
import net.codjo.persistent.Reference;
/**
 * Classe permettant d'importer une chaîne de caractère.
 *
 * @author $Author: spinae $
 * @version $Revision: 1.2 $
 */
class StringFieldImport extends FieldImport {
    /**
     * Constructor for the StringFieldImport object
     *
     * @param ref Reference.
     * @param dbName Nom du champ DB de destination.
     */
    public StringFieldImport(Reference ref, String dbName) {
        super(ref, dbName);
    }


    /**
     * Constructor for Test.
     *
     * @param dbName Nom du champ DB de destination.
     */
    StringFieldImport(String dbName) {
        super(dbName);
    }

    /**
     * Retourne le type SQL de l'objet produit par convertFieldToSQL.
     *
     * @return java.sql.Types.VARCHAR.
     */
    public int getSQLType() {
        return java.sql.Types.VARCHAR;
    }


    /**
     * Traduction du champ en objet String.
     * 
     * <p></p>
     *
     * @param field Champ à traduire.
     *
     * @return Le champ en format SQL.
     *
     * @exception BadFormatException Description of Exception
     */
    public Object translateField(String field) throws BadFormatException {
        if ((field != null) && ("".equals(field) == false)) {
            return field;
        }
        return null;
    }
}
