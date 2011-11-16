/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.operation;
import net.codjo.model.Table;
import net.codjo.persistent.AbstractPersistent;
import net.codjo.persistent.PersistenceException;
import net.codjo.persistent.Reference;
import net.codjo.utils.ConnectionManager;
/**
 * Classe de base pour tous les comportements.
 *
 * @author $Author: blazart $
 * @version $Revision: 1.2 $
 *
 */
public abstract class Behavior extends AbstractPersistent {
    private transient int lengthOfTask = 0;
    private transient int currentOfTask = 0;
    private transient ConnectionManager connectionManager;
    private Reference destTableRef;
    private Reference sourceTableRef;

    /**
     * Constructor for the Behavior object.
     *
     * @param selfRef La reference de ce comportement
     * @param sourceTable
     * @param destTable
     */
    protected Behavior(Reference selfRef, Table sourceTable, Table destTable) {
        super(selfRef);
        initTable(sourceTable, destTable);
    }


    /**
     * Constructor pour les tests.
     *
     * @param sourceTable
     * @param destTable
     */
    protected Behavior(Table sourceTable, Table destTable) {
        initTable(sourceTable, destTable);
    }

    /**
     * Positionne le manager de connection de la tache.
     *
     * @param newConnectionManager The new ConnectionManager value
     *
     * @throws IllegalArgumentException TODO
     */
    public final void setConnectionManager(ConnectionManager newConnectionManager) {
        if (newConnectionManager == null) {
            throw new IllegalArgumentException();
        }
        connectionManager = newConnectionManager;
    }


    /**
     * Retourne la table de destination
     *
     * @return The DestTable value
     */
    public final Table getDestTable() {
        if (destTableRef != null) {
            return (Table)destTableRef.getLoadedObject();
        }
        else {
            return null;
        }
    }


    /**
     * Retourne la table source.
     *
     * @return The SourceTable value
     */
    public final Table getSourceTable() {
        if (sourceTableRef != null) {
            return (Table)sourceTableRef.getLoadedObject();
        }
        else {
            return null;
        }
    }


    /**
     * Retourne La taille totale de la tache. Cette valeur est initialise par la methode
     * determineLengthOfTask.
     *
     * @return The LengthOfTask value
     *
     * @see #determineLengthOfTask
     */
    public int getLengthOfTask() {
        return lengthOfTask;
    }


    /**
     * Retourne le compteur courant de la tache.
     *
     * @return The CurrentOfTask value
     */
    public int getCurrentOfTask() {
        return currentOfTask;
    }


    /**
     * Prepare l'execution de l'operation du comportement. Cette methode doit s'assurer
     * que tout les elements dont a besoins le comportement sont en memoire.
     *
     * @exception PersistenceException
     */
    public abstract void prepareProceed() throws PersistenceException;


    /**
     * Determine la longueur de l'opération à effectuer (ex : Nb de lignes du fichier à
     * importer).
     * 
     * <p>
     * <b>Attention</b> : La methode met a jour l'attribut lengthOfTask.
     * </p>
     *
     * @param ope Description of Parameter
     *
     * @exception Exception Description of Exception
     */
    public abstract void determineLengthOfTask(Operation ope)
            throws Exception;


    /**
     * Lance le traitement attache a l'operation.
     * 
     * <p>
     * Il est fortement deconseille d'appeler cette methode directement. Pour effectuer
     * le traitement il est preferable de passer par l'Operation.
     * </p>
     *
     * @param ope L'operation qui lance le comportement
     *
     * @exception Exception -
     *
     * @see net.codjo.operation.Operation#proceed
     */
    public abstract void proceed(Operation ope) throws Exception;


    /**
     * Positionne la taille totale de la tache.
     *
     * @param n The new LengthOfTask value
     */
    public void setLengthOfTask(int n) {
        lengthOfTask = n;
    }


    /**
     * Positionne la taille totale de la tache.
     *
     * @param newCurrentOfTask The new CurrentOfTask value
     */
    protected void setCurrentOfTask(int newCurrentOfTask) {
        currentOfTask = newCurrentOfTask;
    }


    /**
     * Retourne le ConnectionManager de ce traitement
     *
     * @return The ConnectionManager value
     */
    protected final ConnectionManager getConnectionManager() {
        return connectionManager;
    }


    /**
     * Incremente le compteur courant de la tache.
     */
    public final void incrementCurrentOfTask() {
        currentOfTask++;
    }


    /**
     * Init.
     *
     * @param sourceTable
     * @param destTable
     */
    private void initTable(Table sourceTable, Table destTable) {
        if (destTable != null) {
            this.destTableRef = destTable.getReference();
        }
        if (sourceTable != null) {
            this.sourceTableRef = sourceTable.getReference();
        }
    }
}
