/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.gui.renderer;

// Utils
import net.codjo.utils.ConnectionManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.Map;
/**
 * Comparateur servant à trier les champs en fonction de leur traduction définie dans la
 * table PM_FIELD_LABEL
 *
 * @version $Revision: 1.3 $
 *
 *
 */
public class FieldLabelComparator implements Comparator {
    Map traductTable;

    /**
     * Constructor for the FieldLabelComparator object
     *
     * @param conMan Connection Manager
     * @param table Table
     *
     * @exception SQLException erreur base
     * @throws IllegalArgumentException TODO
     */
    public FieldLabelComparator(ConnectionManager conMan, String table)
            throws SQLException {
        if ((conMan == null) || (table == null)) {
            throw new IllegalArgumentException();
        }
        traductTable = FieldNameRenderer.loadTraducTable(conMan, table);
    }


    /**
     * Constructor for the FieldLabelComparator object
     *
     * @param con Description of the Parameter
     * @param table Description of the Parameter
     *
     * @exception SQLException Description of the Exception
     * @throws IllegalArgumentException TODO
     */
    public FieldLabelComparator(Connection con, String table)
            throws SQLException {
        if ((con == null) || (table == null)) {
            throw new IllegalArgumentException();
        }
        traductTable = FieldNameRenderer.loadTraducTable(con, table, null);
    }


    /**
     * Constructor for the FieldLabelComparator object
     *
     * @param renderer Description of the Parameter
     * @param table Description of the Parameter
     *
     * @exception SQLException Description of the Exception
     * @throws IllegalArgumentException TODO
     */
    public FieldLabelComparator(FieldNameRenderer renderer, String table)
            throws SQLException {
        if ((renderer == null) || (table == null)) {
            throw new IllegalArgumentException();
        }
        traductTable = renderer.getTranslationsMap();
    }

    /**
     * Compare deux champs
     *
     * @param o1 Le nom physique du premier champ
     * @param o2 Le nom physique du deuxième champ
     *
     * @return La comparaison / libellés des deux champs (s'ils existent)
     */
    public int compare(Object o1, Object o2) {
        String field1;
        String field2;
        String lib1;
        String lib2;
        field1 = (String)o1;
        field2 = (String)o2;
        if (traductTable.containsKey(field1)) {
            lib1 = (String)traductTable.get(field1);
        }
        else {
            lib1 = field1;
        }

        if (traductTable.containsKey(field2)) {
            lib2 = (String)traductTable.get(field2);
        }
        else {
            lib2 = field2;
        }

        return lib1.compareTo(lib2);
    }


    /**
     * Teste l'égalité d'un obj avec le comparateur MAIS A QUOI CA SERT ???
     *
     * @param obj Un obj
     *
     * @return Egalité ?
     */
    public boolean equals(Object obj) {
        return this == obj;
    }
}
