/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.persistent;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
/**
 * Une implémentation abstraite de Model.
 * 
 * <p>
 * Ce model garde dans un buffer la liste des References valide de ce model. Une
 * Reference est valide : si elle possede un id.
 * </p>
 * 
 * <p>
 * Le buffer peut etre desactivee par <code>setBufferOn(false)</code> .
 * </p>
 *
 * @author $Author: marcona $
 * @version $Revision: 1.4 $
 *
 */
public abstract class AbstractModel implements Model {
    private boolean bufferOn = true;
    private Connection connection;
    private Map refWithId = Collections.synchronizedMap(new HashMap());

    /**
     * Constructor for the AbstractModel object
     *
     * @param con Connection de ce model
     *
     * @throws IllegalArgumentException TODO
     */
    protected AbstractModel(Connection con) {
        if (con == null) {
            throw new IllegalArgumentException();
        }
        connection = con;
    }

    /**
     * The object deletes itself from the datastore <b>without issuing any commit </b> .
     * La reference est retire de ce Model.
     *
     * @param ref Une référence.
     *
     * @exception PersistenceException Delete impossible
     */
    public final void delete(Reference ref) throws PersistenceException {
        try {
            if (ref.getObject().isStored()) {
                deleteSQL(ref);
            }
            ref.getObject().setDead();
            ref.getObject().setSynchronized(true);
            removeReference(ref);
        }
        catch (PersistenceException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw new PersistenceException(ex);
        }
    }


    /**
     * Charge une Reference de la base. La Reference est mis-a-jours.
     *
     * @param ref Une référence.
     *
     * @exception PersistenceException -
     */
    public final void load(Reference ref) throws PersistenceException {
        if (ref.isLoaded()) {
            return;
        }

        try {
            loadSQL(ref);
            ref.getObject().setStored();
            ref.getObject().setSynchronized(true);
            addReference(ref);
        }
        catch (PersistenceException ex) {
            throw ex;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            throw new PersistenceException(ex);
        }
    }


    /**
     * Enregistre ref dans la base.
     *
     * @param ref Une référence.
     *
     * @exception PersistenceException
     */
    public final void save(Reference ref) throws PersistenceException {
        if (!ref.isLoaded()
                || ref.getObject().isSynchronized()
                || ref.getObject().isDead()) {
            return;
        }

        try {
            saveSQL(ref);
            ref.getObject().setStored();
            ref.getObject().setSynchronized(true);
            addReference(ref);
        }
        catch (PersistenceException ex) {
            throw ex;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            throw new PersistenceException(ex);
        }
    }


    /**
     * Retourne la connection du Model.
     *
     * @return The Connection value
     */
    public Connection getConnection() {
        return connection;
    }


    /**
     * Retourne une référence unique possédant cette id
     *
     * @param id Identifiant (ou Pk de l'objet)
     *
     * @return La référence
     */
    public Reference getReference(Object id) {
        Reference ref = null;
        if (isBufferOn()) {
            ref = (Reference)refWithId.get(id);
        }

        if (ref == null) {
            ref = new Reference(this, id);
            addReference(ref);
        }
        return ref;
    }


    /**
     * Retire explicitement une référence du Model. Si la reference n'existe pas, la
     * methode echoue silencieusement.
     *
     * @param ref La reference
     */
    public void removeReference(Reference ref) {
        if (isBufferOn() == false || ref.getId() == null) {
            return;
        }
        refWithId.remove(ref.getId());
    }


    /**
     * Ajoute explicitement une référence dans le Model.
     *
     * @param ref Reference a ajouter.
     */
    protected void addReference(Reference ref) {
        if (!isBufferOn()) {
            return;
        }
        if (ref.isLoaded() && ref.getLoadedObject().isDead()) {
            return;
        }
        if (ref.getId() == null || refWithId.containsKey(ref.getId())) {
            return;
        }
        refWithId.put(ref.getId(), ref);
    }


    /**
     * Execute la requete d'effacement SQL.
     *
     * @param ref La reference
     *
     * @exception Exception En cas d'erreur :)
     */
    protected abstract void deleteSQL(Reference ref)
            throws Exception;


    /**
     * Retourne toutes les References se trouvant dans le buffer.
     *
     * @return Collection de Reference.
     */
    protected Collection getReferences() {
        synchronized (refWithId) {
            return new ArrayList(refWithId.values());
        }
    }


    /**
     * Gets the BufferOn attribute of the AbstractModel object
     *
     * @return The BufferOn value
     */
    protected boolean isBufferOn() {
        return bufferOn;
    }


    /**
     * Execute la requete d'enregistrement SQL.
     *
     * @param ref La reference
     *
     * @exception Exception En cas d'erreur :)
     */
    protected abstract void loadSQL(Reference ref)
            throws Exception;


    /**
     * Execute la requete d'enregistrement SQL.
     *
     * @param ref La reference
     *
     * @exception Exception En cas d'erreur :)
     */
    protected abstract void saveSQL(Reference ref)
            throws Exception;


    /**
     * Sets the BufferOn attribute of the AbstractModel object
     *
     * @param newBufferOn The new BufferOn value
     */
    protected void setBufferOn(boolean newBufferOn) {
        bufferOn = newBufferOn;
        if (!isBufferOn()) {
            refWithId.clear();
        }
    }
}
