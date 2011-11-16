/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.persistent;
/**
 * Un Model est responsable de l'interfacage entre les objets persistants et la base de
 * données.
 * 
 * <p>
 * Son but est d'assurer l'unicité des références pointant sur un même objet et la
 * sauvegarde cohérente des références qu'il contient. Un Model est monothread et ne
 * possède donc qu'une seule connection.
 * </p>
 *
 * @author $Author: blazart $
 * @version $Revision: 1.3 $
 *
 */
public interface Model {
    /**
     * Retourne une référence unique possédant la clef primaire passée en paramètre. La
     * référence se verra automatiquement ajoutée au Model.
     *
     * @param pk La clef Primaire.
     *
     * @return Une référence.
     */
    public Reference getReference(Object pk);


    /**
     * Enregistre ref dans la base.
     *
     * @param ref Une référence.
     *
     * @exception PersistenceException Description of Exception
     */
    public void save(Reference ref) throws PersistenceException;


    /**
     * The object deletes itself from the datastore <b>without issuing any commit </b> .
     *
     * @param ref Une référence.
     *
     * @exception PersistenceException Description of Exception
     */
    public void delete(Reference ref) throws PersistenceException;


    /**
     * Charge l'objet pointé par la référence.
     *
     * @param ref Une référence.
     *
     * @exception PersistenceException Description of Exception
     */
    public void load(Reference ref) throws PersistenceException;
}
