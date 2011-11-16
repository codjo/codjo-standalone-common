/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.utils.sql.event;
import java.util.EventListener;
/**
 * Ecouteur sur les modifications apportees sur la base BD.
 *
 * @author $Author: blazart $
 * @version $Revision: 1.2 $
 *
 */
public interface DbChangeListener extends EventListener {
    /**
     * La modification a reussi.
     *
     * @param evt Description of Parameter
     */
    public void succeededChange(DbChangeEvent evt);
}
