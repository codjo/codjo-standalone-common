/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.gui.model;
import net.codjo.model.Table;
import net.codjo.persistent.Reference;
import java.util.Comparator;
/**
 * Comparateur servant à trier le nom des tables. Cette classe peut etre utiliser avec
 * <code>Collections.sort(list, comparator)</code> .
 *
 * @author $Author: blazart $
 * @version $Revision: 1.2 $
 *
 *
 */
public class TableReferenceComparator implements Comparator {
    /** Comparaison par nom physique de table. */
    public static final int COMPARE_BY_DB_TABLE_NAME = 0;
    /** Comparaison par nom logique de table. */
    public static final int COMPARE_BY_TABLE_NAME = 1;
    private int compareMode;

    /**
     * Constructor for the TableReferenceComparator object
     *
     * @param compareMode Description of Parameter
     *
     * @throws IllegalArgumentException TODO
     */
    public TableReferenceComparator(int compareMode) {
        if (compareMode < 0 && compareMode > 1) {
            throw new IllegalArgumentException();
        }
        this.compareMode = compareMode;
    }

    /**
     * DOCUMENT ME!
     *
     * @param o1 -
     * @param o2 -
     *
     * @return -
     *
     * @throws IllegalArgumentException TODO
     */
    public int compare(Object o1, Object o2) {
        Table t1 = (Table)((Reference)o1).getLoadedObject();
        Table t2 = (Table)((Reference)o2).getLoadedObject();

        switch (compareMode) {
            case COMPARE_BY_DB_TABLE_NAME:
                return t1.getDBTableName().compareTo(t2.getDBTableName());
            case COMPARE_BY_TABLE_NAME:
                return t1.getTableName().compareTo(t2.getTableName());
            default:
                throw new IllegalArgumentException("Unknown Compare Mode");
        }
    }


    /**
     * DOCUMENT ME!
     *
     * @param obj -
     *
     * @return -
     */
    public boolean equals(Object obj) {
        return this == obj;
    }
}
