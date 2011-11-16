/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.operation;

// Penelope
import net.codjo.persistent.Model;
import net.codjo.persistent.PersistenceException;
import net.codjo.persistent.Reference;
import net.codjo.utils.sql.event.DbChangeListener;

import java.sql.SQLException;
/**
 * Cette interface definie les méthodes afin de rajouter un comportement pour les
 * operations.
 *
 * @author $Author: blazart $
 * @version $Revision: 1.2 $
 *
 */
public interface BehaviorLoader {
    /**
     * Ajoute un ecouteur sur les modifications BD.
     *
     * @param l Le listener
     */
    public void addDbChangeListener(DbChangeListener l);


    /**
     * Retourne l'identifiant du comportement geree par ce Loader.
     *
     * @return L'identifiant du comportement (ex: "I")
     */
    public String getBehaviorID();


    /**
     * Retourne le label du comportement.
     *
     * @return Le label descriptif (ex: "IMPORT").
     */
    public String getBehaviorLabel();


    /**
     * Retourne le Home gerant le comportement.
     *
     * @return The Home value
     */
    public Model getHome();


    /**
     * Retourne une reference sur comportement qui correspond à l'id du settings
     * (SETTINGS_ID)
     *
     * @param setId L'identifiant du settings (SETTINGS_ID).
     *
     * @return Une reference sur un comportement.
     *
     * @exception PersistenceException Description of Exception
     */
    public Reference getReference(int setId) throws PersistenceException;


    /**
     * Retourne la liste de tous les SETTINGS_ID.
     *
     * @return les SETTINGS_ID
     *
     * @exception SQLException -
     */
    public Object[] getAllId() throws SQLException;
}
