/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.operation.imports;
import net.codjo.persistent.AbstractPersistent;
import net.codjo.persistent.Reference;

import java.util.StringTokenizer;
/**
 * Classe de base pour les filtes d'import.
 * 
 * <p>
 * Cette classe permet d'extraire d'une String un champs Et de le transformer dans la
 * syntaxe SQL Et contient le champs cible.
 * </p>
 *
 * @version $Revision: 1.5 $
 *
 */
public abstract class FieldImport extends AbstractPersistent {
    private String _dbDestFieldName;
    private boolean _fixedLength = true;
    private boolean _headerLine = false;
    private int _length = 0;
    private int _position = 0;
    private boolean _removeLeftZeros = false;
    private String _separator = "\t";

    /**
     * Constructor pour des FieldImport non <code>Persistent</code>.
     *
     * @param dbName Nom du champ DB de destination.
     */
    protected FieldImport(String dbName) {
        _dbDestFieldName = dbName;
    }


    /**
     * Constructor for the FieldImport object
     *
     * @param ref Description of Parameter
     * @param dbName Description of Parameter
     */
    FieldImport(Reference ref, String dbName) {
        super(ref);
        _dbDestFieldName = dbName;
    }

    /**
     * Convertion du champs en "String SQL".
     * 
     * <p>
     * Cette methode extrait le champs de "ligneFichier" et le convertit en un Object
     * utilisable dans une requete SQL.
     * </p>
     *
     * @param ligneFichier Une ligne de la table a importer
     *
     * @return Le champs convertit.
     *
     * @exception FieldNotFoundException Si la ligne est trop courte.
     * @exception BadFormatException Si le format est incorrecte.
     */
    public final Object convertFieldToSQL(String ligneFichier)
            throws FieldNotFoundException, BadFormatException {
        return translateField(extractField(ligneFichier));
    }


    /**
     * Access method for the _dbDestFieldName.
     *
     * @return the current value of the _dbDestFieldName
     */
    public String getDBDestFieldName() {
        return _dbDestFieldName;
    }


    /**
     * Access method for the _length property.
     *
     * @return the current value of the _length property
     */
    public boolean getFixedLength() {
        return _fixedLength;
    }


    /**
     * Access method for the _length property.
     *
     * @return the current value of the _length property
     */
    public int getLength() {
        return _length;
    }


    /**
     * Access method for the _position property.
     *
     * @return the current value of the _position property
     */
    public int getPosition() {
        return _position;
    }


    /**
     * Access method for the _removeLeftZero property.
     *
     * @return the current value of the _removeLeftZero property
     */
    public boolean getRemoveLeftZeros() {
        return _removeLeftZeros;
    }


    /**
     * Retourne le type SQL de l'objet produit par convertFieldToSQL.
     *
     * @return Le type SQL définit dans java.sql.Types.
     */
    public abstract int getSQLType();


    /**
     * Access method for the _separator property.
     *
     * @return the current value of the _label property
     */
    public String getSeparator() {
        return _separator;
    }


    /**
     * Sets the value of the _fixedLength property.
     *
     * @param fixedLength the new value of the _fixedLength property
     */
    public void setFixedLength(boolean fixedLength) {
        _fixedLength = fixedLength;
    }


    /**
     * Sets the value of the _headerLine property.
     *
     * @param headerLine the new value of the _headerLine property
     */
    public void setHeaderLine(boolean headerLine) {
        _headerLine = headerLine;
    }


    /**
     * Sets the value of the _length property.
     *
     * @param length the new value of the _length property
     */
    public void setLength(int length) {
        _length = length;
    }


    /**
     * Sets the value of the _position property.
     *
     * @param position the new value of the _position property
     */
    public void setPosition(int position) {
        _position = position;
    }


    /**
     * Sets the value of the _removeLeftZero property.
     *
     * @param removeLeftZeros The new RemoveLeftZeros value
     */
    public void setRemoveLeftZeros(boolean removeLeftZeros) {
        _removeLeftZeros = removeLeftZeros;
    }


    /**
     * Sets the value of the _separator property.
     *
     * @param separator the new value of the _separator property
     */
    public void setSeparator(String separator) {
        _separator = separator;
    }


    /**
     * toString
     *
     * @return _dbDestFieldName + _position
     */
    public String toString() {
        return "(" + _dbDestFieldName + "," + _position + ")";
    }


    /**
     * Traduction en "Object SQL".
     * 
     * <p>
     * Cette methode traduit le champs extrait en "String SQL".
     * </p>
     *
     * @param field Le champs extrait et nettoyé (pre-formaté).
     *
     * @return Le champs traduit.
     *
     * @exception BadFormatException Si le format est incorrecte.
     */
    public abstract Object translateField(String field)
            throws BadFormatException;


    /**
     * Extraction du champs de la ligne texte.
     *
     * @param line Ligne de la table
     *
     * @return Retourne le champs extrait.
     *
     * @exception FieldNotFoundException Si la ligne est trop courte.
     */
    public String extractField(String line) throws FieldNotFoundException {
        String sourceField = null;

        if (_fixedLength == true) {
            try {
                sourceField =
                    line.substring(getPosition() - 1, getPosition() + getLength() - 1);
            }
            catch (Exception ex) {
                throw new FieldNotFoundException(ex.getMessage() + " ["
                    + getDBDestFieldName() + "]");
            }
        }
        else {
            String sep = getSeparator();
            StringTokenizer st = new StringTokenizer(line, sep, true);
            boolean previousWasTab = false;
            int idx = 1;
            while ((st.hasMoreTokens()) && (idx <= getPosition() * 2 - 1)) {
                sourceField = st.nextToken();
                if (sep.equals(sourceField)) {
                    if (previousWasTab) {
                        idx++;
                    }
                    else if (idx == 1) {
                        idx++;
                    }
                    previousWasTab = true;
                    sourceField = "";
                }
                else {
                    previousWasTab = false;
                }
                idx++;
            }
            if (sep != null && sep.equals(sourceField)) {
                sourceField = "";
            }

            if (idx <= getPosition()) {
                System.err.println("Le champ [" + getDBDestFieldName() + "]"
                    + " à la position " + getPosition() + " n'est pas dans la ligne >"
                    + line + "<");
                throw new FieldNotFoundException("Pas assez de champ dans le fichier ["
                    + getDBDestFieldName() + "]");
            }
        }

        StringBuffer field = new StringBuffer(sourceField.trim());

        if (getRemoveLeftZeros() == true) {
            while (field.length() > 1 && field.charAt(0) == '0') {
                field.deleteCharAt(0);
            }
        }

        return field.toString();
    }
}
