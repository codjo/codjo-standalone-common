/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.persistent;
/**
 * Pointeur polymorphique intelligent sur un objet Persistent.
 * 
 * <p>
 * Pointe sur la mémoire ou sur la base.
 * </p>
 *
 * @author $Author: blazart $
 * @version $Revision: 1.3 $
 *
 */
public class Reference {
    private ReferenceData data;
    private Persistent object;

    /**
     * Constructeur de Reference
     *
     * @param m Description of Parameter
     * @param id Description of Parameter
     */
    public Reference(Model m, Object id) {
        data = new ReferenceData(m, id);
    }


    /**
     * Constructeur de Reference
     *
     * @param m Description of Parameter
     */
    public Reference(Model m) {
        data = new ReferenceData(m);
    }

    /**
     * retourne la clef primaire de l'objet null si celle-ci n'est pas définie
     *
     * @return The Id value
     */
    public Object getId() {
        return data.getId();
    }


    /**
     * Retourne l'objet référencé. Renvoie pointeur null, si l'objet n'est pas charge.
     *
     * @return L'Object (ou null si non charge)
     */
    public Persistent getLoadedObject() {
        return object;
    }


    /**
     * Gets the Model attribute of the Reference object
     *
     * @return The Model value
     */
    public Model getModel() {
        return data.getModel();
    }


    /**
     * Retourne l'objet référencé. Le charge si nécessaire.
     *
     * @return The Object value
     *
     * @exception PersistenceException Impossible de charger l'objet.
     */
    public Persistent getObject() throws PersistenceException {
        if (!isLoaded()) {
            data.getModel().load(this);
        }
        return object;
    }


    /**
     * Indique si l'objet est en mémoire.
     *
     * @return true si en mémoire
     */
    public boolean isLoaded() {
        return object != null;
    }


    /**
     * Recharge l'objet depuis la base
     *
     * @exception PersistenceException Description of Exception
     */
    public void reload() throws PersistenceException {
        unload();
        data.getModel().load(this);
    }


    /**
     * affecte la clef primaire de l'objet
     *
     * @param id The new Id value
     *
     * @throws IllegalArgumentException TODO
     */
    public void setId(Object id) {
        if (id == null || data.getId() != null) {
            throw new IllegalArgumentException();
        }

        data.setId(id);
    }


    /**
     * Sets the Object attribute of the Reference object
     *
     * @param o The new Object value
     */
    public void setObject(Persistent o) {
        this.object = o;
    }


    /**
     * DOCUMENT ME!
     *
     * @return Description of the Returned Value
     */
    public String toString() {
        return "Ref(" + getId() + " , " + getLoadedObject() + ")";
    }


    /**
     * Decharge l'objet de la memoire
     */
    public void unload() {
        object = null;
    }
}
