/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.operation.imports;
import net.codjo.persistent.Reference;
import org.apache.log4j.Logger;
/**
 * Classe permettant d'importer un booleen.
 *
 * @author $Author: rivierv $
 * @version $Revision: 1.3 $
 */
class BooleanFieldImport extends FieldImport {
    // Log
    private static final Logger APP = Logger.getLogger(BooleanFieldImport.class);

    /**
     * Constructor for the BooleanFieldImport object.
     *
     * @param ref Self Reference
     * @param dbName Nom du champ DB de destination.
     */
    public BooleanFieldImport(Reference ref, String dbName) {
        super(ref, dbName);
    }


    /**
     * Constructor for Test.
     *
     * @param dbName Description of Parameter
     */
    BooleanFieldImport(String dbName) {
        super(dbName);
    }

    /**
     * Retourne le type SQL de l'objet produit par convertFieldToSQL.
     *
     * @return java.sql.Types.BIT.
     */
    public int getSQLType() {
        return java.sql.Types.BIT;
    }


    /**
     * Traduction du champ en objet Boolean.
     * 
     * <p>
     * Conversion du booleen d'entrée (VRAI/FAUX) en booleen de sortie.
     * </p>
     *
     * @param field Champ à traduire.
     *
     * @return Le champ en format SQL.
     *
     * @exception BadFormatException Description of Exception
     */
    public Object translateField(String field) throws BadFormatException {
        if (field == null) {
            return Boolean.FALSE;
        }
        if ("".equals(field)) {
            return Boolean.FALSE;
        }
        if ("VRAI".equals(field)) {
            return Boolean.TRUE;
        }
        if ("FAUX".equals(field)) {
            return Boolean.FALSE;
        }
        APP.debug("Valeur de champ booleen non prévue : '" + field + "'");
        throw new BadFormatException(this, field + " n'est pas un booleen");
    }
}
