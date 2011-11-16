/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.operation.treatment;
import java.sql.ResultSet;
import java.sql.SQLException;
/**
 * Permet la detection d'une rupture dans un jeux d'enregistrements.
 * 
 * <p>
 * Note: un enregistrement unique dans le jeux ou le dernier appel ne retourne pas une
 * rupture.
 * </p>
 *
 * @author $Author: marcona $
 * @version $Revision: 1.4 $
 *
 */
public class BreakDetector {
    private String oldLine = null;
    private String[] listPk = null;
    private boolean isAggregate = false;

    /**
     * Constructeur pour un detecteur de rupture en mode ligne a ligne.
     */
    public BreakDetector() {
        this(null);
    }


    /**
     * Constructeur pour un detecteur de rupture en mode agregation.
     *
     * @param pk La liste des champs cle sur lesquelles la methode
     *        <code>isBreakPoint</code> detecte une rupture.
     */
    public BreakDetector(String[] pk) {
        if (pk == null) {
            isAggregate = false;
        }
        else {
            listPk = pk;
            isAggregate = true;
        }
    }

    /**
     * Appele a chaque ligne d'un jeux d'enregistrements, permet la detection d'une
     * rupture en fonction du mode de rupture.
     *
     * @param rs Le ResultSet pour la detection de la rupture.
     *
     * @return True si rupture
     *
     * @exception SQLException -
     */
    public boolean isBreakPoint(ResultSet rs) throws SQLException {
        if (isAggregate) {
            boolean returnBreakPoint = false;
            String newLine = "";

            for (int i = 0; i < listPk.length; i++) {
                newLine = newLine + rs.getObject(listPk[i]);
            }

            if (oldLine == null) {
                oldLine = newLine;
            }

            returnBreakPoint = !newLine.equals(oldLine);
            oldLine = newLine;
            return returnBreakPoint;
        }
        else {
            if (oldLine == null) {
                oldLine = "";
                return false;
            }
            else {
                return true;
            }
        }
    }


    /**
     * Reinitialise le pointeur de rupture.
     */
    public void clear() {
        oldLine = null;
    }


    /**
     * DOCUMENT ME!
     *
     * @return
     */
    public String toString() {
        return "BreakDetector(aggregation = " + isAggregate + " , breakKeys = "
        + ((listPk != null) ? java.util.Arrays.asList(listPk).toString() : "None");
    }
}