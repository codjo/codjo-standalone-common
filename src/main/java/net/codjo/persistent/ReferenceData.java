/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.persistent;
/**
 * Données internes d'une Reference.
 * 
 * <p>
 * Contient au moins :
 * 
 * <ul>
 * <li>
 * Un identifiant
 * </li>
 * <li>
 * Un model
 * </li>
 * <li>
 * Une classe
 * </li>
 * </ul>
 * </p>
 *
 * @author $Author: gonnot $
 * @version $Revision: 1.1.1.1 $
 */
class ReferenceData implements Cloneable {
    private Object id;
    private Model model;
    private Class objectClass;

    /**
     * Constructor pour une référence non chargée
     *
     * @param m Le modèle contenant la référence
     * @param id identifiant de l'objet
     * @param objectClass Description of Parameter
     *
     * @throws IllegalArgumentException TODO
     */
    public ReferenceData(Model m, Object id, Class objectClass) {
        if (m == null || id == null || objectClass == null) {
            throw new IllegalArgumentException();
        }

        setId(id);
        setObjectClass(objectClass);
        setModel(m);
    }


    /**
     * Constructor pour une référence non chargée
     *
     * @param m Le modèle contenant la référence
     * @param id identifiant de l'objet
     *
     * @throws IllegalArgumentException TODO
     */
    public ReferenceData(Model m, Object id) {
        if (m == null || id == null) {
            throw new IllegalArgumentException();
        }

        setId(id);
        setModel(m);
    }


    /**
     * Constructor pour une référence non chargée
     *
     * @param m Le modèle contenant la référence
     *
     * @throws IllegalArgumentException TODO
     */
    public ReferenceData(Model m) {
        if (m == null) {
            throw new IllegalArgumentException();
        }

        setId(id);
        setObjectClass(objectClass);
        setModel(m);
    }

    /**
     * Sets the Id attribute of the ReferenceData object
     *
     * @param id The new Id value
     */
    public void setId(Object id) {
        this.id = id;
    }


    /**
     * Sets the ObjectClass attribute of the ReferenceData object
     *
     * @param objectClass The new ObjectClass value
     */
    public void setObjectClass(Class objectClass) {
        this.objectClass = objectClass;
    }


    /**
     * Sets the Model attribute of the ReferenceData object
     *
     * @param model The new Model value
     */
    public void setModel(Model model) {
        this.model = model;
    }


    /**
     * Gets the Model attribute of the ReferenceData object
     *
     * @return The Model value
     */
    public Model getModel() {
        return model;
    }


    /**
     * Gets the Id attribute of the ReferenceData object
     *
     * @return The Id value
     */
    public Object getId() {
        return id;
    }


    /**
     * Gets the Class attribute of the Reference object
     *
     * @return The Class value
     */
    public Class getObjectClass() {
        return objectClass;
    }


    /**
     * Clone les data.
     *
     * @return une copie.
     */
    public ReferenceData duplicate() {
        try {
            return (ReferenceData)clone();
        }
        catch (CloneNotSupportedException ex) {
            // impossible
        }
        return null;
    }
}
