/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.utils.sql.event;
import java.util.EventObject;
import java.util.Map;
/**
 * Evenement de changement dans la base.
 *
 * @author $Author: blazart $
 * @version $Revision: 1.5 $
 *
 */
public class DbChangeEvent extends EventObject {
    public static final int ADD_EVENT = 0;
    public static final int MODIFY_EVENT = 1;
    public static final int DELETE_EVENT = 2;
    public static final int DUPLICATE_EVENT = 3;
    public static final int FORCE_EVENT = 4;
    private int eventType;
    private java.util.Map primaryKey;

    /**
     * Constructor for the DbChangeEvent object
     *
     * @param source
     * @param eventType
     * @param key Map (column Name / value)
     *
     * @throws IllegalArgumentException TODO
     */
    public DbChangeEvent(Object source, int eventType, Map key) {
        super(source);
        if (eventType < 0 || eventType > 4) {
            throw new IllegalArgumentException();
        }
        this.eventType = eventType;
        this.primaryKey = key;
    }

    /**
     * Retourne le type de l'evenement.
     *
     * @return
     */
    public int getEventType() {
        return eventType;
    }


    /**
     * Retourne la clef primaire de l'objet impacte par l'evenement.
     *
     * @return Map (column Name / value)
     */
    public java.util.Map getPrimaryKey() {
        return primaryKey;
    }


    /**
     * DOCUMENT ME!
     *
     * @return
     */
    public String toString() {
        return "DbChangeEvent(" + typeToString(getEventType()) + "," + getPrimaryKey()
        + ")";
    }


    /**
     * DOCUMENT ME!
     *
     * @param eventType
     *
     * @return
     */
    private static final String typeToString(int eventType) {
        switch (eventType) {
            case DbChangeEvent.ADD_EVENT:
                return "ADD_EVENT";
            case DbChangeEvent.DUPLICATE_EVENT:
                return "DUPLICATE_EVENT";
            case DbChangeEvent.DELETE_EVENT:
                return "DELETE_EVENT";
            case DbChangeEvent.MODIFY_EVENT:
                return "MODIFY_EVENT";
            case DbChangeEvent.FORCE_EVENT:
                return "FORCE_EVENT";
            default:
                return "UNKNOWN";
        }
    }
}
