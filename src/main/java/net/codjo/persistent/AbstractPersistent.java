/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.persistent;
/**
 * Une implémentation abstraite de Persistent.
 * 
 * <p>
 * Service fournie :
 * 
 * <ul>
 * <li>
 * Implantation de l'état du persistant.
 * </li>
 * <li>
 * Contient une self reference.
 * </li>
 * <li>
 * Offre une implantation par défaut de copy et duplicate.
 * </li>
 * </ul>
 * </p>
 *
 * @author $Author: blazart $
 * @version $Revision: 1.3 $
 *
 */
public abstract class AbstractPersistent implements Persistent {
    private static final int synchMask = 0xFFFE;
    private static final int synchOffset = 0;
    private static final int livelyhoodMask = 0xFFF9;
    private static final int livelyhoodOffset = 1;
    private static final int locationMask = 0xFFE7;
    private static final int locationOffset = 3;
    private Reference selfId;
    private int state;

    /**
     * Constructor for the AbstractPersistent object. Pour les objets non en base.
     */
    protected AbstractPersistent() {
        setAlive();
        setSynchronized(false);
    }


    /**
     * Constructor for the AbstractPersistent object
     *
     * @param ref Reference sur "this"
     */
    protected AbstractPersistent(Reference ref) {
        init(ref);
    }

    /**
     * Sets the Synchronised attribute of the AbstractPersistent object
     *
     * @param sync The new Synchronised value
     */
    public void setSynchronized(boolean sync) {
        if (sync) {
            state = state & synchMask;
        }
        else {
            state = (state & synchMask) | (1 << synchOffset);
        }
    }


    /**
     * Sets the Dead attribute of the AbstractPersistent object
     */
    public void setDead() {
        state = (state & livelyhoodMask) | (1 << livelyhoodOffset);
    }


    /**
     * Sets the Stored attribute of the AbstractPersistent object
     */
    public void setStored() {
        state = (state & locationMask) | (1 << locationOffset);
    }


    /**
     * Sets the Alive attribute of the AbstractPersistent object
     */
    private void setAlive() {
        state = state & livelyhoodMask;
    }


    /**
     * Retourne l'identifiant (clef primaire) de l'objet
     *
     * @return The Id value
     */
    public Object getId() {
        return selfId.getId();
    }


    /**
     * Gets the Synchronised attribute of the AbstractPersistent object
     *
     * @return The Synchronised value
     */
    public boolean isSynchronized() {
        return !((state & ~synchMask) == (1 << synchOffset));
    }


    /**
     * Gets the Stored attribute of the AbstractPersistent object
     *
     * @return The Stored value
     */
    public boolean isStored() {
        return (state & ~locationMask) == (1 << locationOffset);
    }


    /**
     * Gets the Dead attribute of the AbstractPersistent object
     *
     * @return The Dead value
     */
    public boolean isDead() {
        return (state & ~livelyhoodMask) == (1 << livelyhoodOffset);
    }


    /**
     * Retourne la référence sur "this"
     *
     * @return The Reference value
     */
    public Reference getReference() {
        return selfId;
    }


    /**
     * Gets the Alive attribute of the AbstractPersistent object
     *
     * @return The Alive value
     */
    public boolean isAlive() {
        return (state & ~livelyhoodMask) == 0;
    }


    /**
     * Appel la methode delete du model.
     *
     * @exception net.codjo.persistent.PersistenceException -
     *
     * @see net.codjo.persistent.Model#delete
     */
    public void delete() throws net.codjo.persistent.PersistenceException {
        getReference().getModel().delete(getReference());
    }


    /**
     * Appel la methode save du model.
     *
     * @exception net.codjo.persistent.PersistenceException -
     *
     * @see net.codjo.persistent.Model#save
     */
    public void save() throws net.codjo.persistent.PersistenceException {
        getReference().getModel().save(getReference());
    }


    /**
     * Sets the Newborn attribute of the AbstractPersistent object
     */
    private void setNewborn() {
        state = state & locationMask;
    }


    /**
     * Gets the Newborn attribute of the AbstractPersistent object
     *
     * @return The Newborn value
     */
    private boolean isNewborn() {
        return (state & ~locationMask) == 0;
    }


    /**
     * méthode commune aux constructeurs.
     *
     * @param ref Description of Parameter
     *
     * @throws IllegalArgumentException TODO
     */
    private void init(Reference ref) {
        if (ref == null) {
            throw new IllegalArgumentException();
        }
        selfId = ref;
        setAlive();
        setSynchronized(false);
        ref.setObject(this);
    }
}
