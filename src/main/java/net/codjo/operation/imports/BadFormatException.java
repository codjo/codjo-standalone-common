/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.operation.imports;
/**
 * Cette exception est lancée par une opération FieldImport afin d'indiquer que le champ
 * n'est pas au bon format.
 * 
 * <p>
 * Exemple: Type attendu est une date, type recu est un reel.
 * </p>
 *
 * @version $Revision: 1.3 $
 *
 */
public class BadFormatException extends Exception {
    /**
     * Constructor for the BadFormatException object
     *
     * @param fi Le FieldImport ayant genere l'erreur
     * @param msg
     */
    public BadFormatException(FieldImport fi, String msg) {
        super("Mauvais format de [" + fi.getDBDestFieldName() + "] : " + msg);
    }


    /**
     * Constructor for the BadFormatException object
     *
     * @param msg Description of Parameter
     */
    public BadFormatException(String msg) {
        super(msg);
    }
}
