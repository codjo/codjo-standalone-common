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
import java.util.List;
/**
 * Parametre pour une operation.
 *
 * @author $Author: blazart $
 * @version $Revision: 1.6 $
 *
 */
public class OperationSettings extends AbstractPersistent {
    private boolean automatic;
    private BehaviorLoader behaviorLoader;
    private Reference behaviorRef;
    private String commentry;
    private String deleteCriteria;
    private java.util.List portfolioGroupList = new java.util.ArrayList();
    private int priority;
    private String selectCriteria;
    private Reference tableDestRef;
    private Reference tableSourceRef;
    private long timestamp = Long.MAX_VALUE;

    /**
     * Constructor for the OperationSettings object
     *
     * @param selfRef Self Reference
     * @param p priority
     * @param tSRef Description of Parameter
     * @param tDRef Description of Parameter
     * @param co Commentaire.
     * @param auto Automatisable ?
     * @param bRef behavior Reference
     * @param pfList Liste de portfolio Group (Reference).
     * @param behaviorLoader Description of Parameter
     * @param selectCriteria Description of Parameter
     * @param deleteCriteria Description of Parameter
     *
     * @exception PersistenceException Si impossible de charger Table.
     * @throws IllegalArgumentException TODO
     *
     * @todo tester si pointeur null pour bRef
     */
    OperationSettings(Reference selfRef, int p, Reference tSRef, Reference tDRef,
        String co, boolean auto, Reference bRef, List pfList,
        BehaviorLoader behaviorLoader, String selectCriteria, String deleteCriteria)
            throws PersistenceException {
        super(selfRef);

        if (tSRef == null || pfList == null || tDRef == null) {
            throw new IllegalArgumentException();
        }

        tableSourceRef = tSRef;
        tableDestRef = tDRef;
        // On s'assure que les tables sont chargées
        tableSourceRef.getObject();
        tableDestRef.getObject();

        behaviorRef = bRef;
        setPriority(p);
        setCommentry(co);
        setAutomatic(auto);
        portfolioGroupList.addAll(pfList);
        setBehaviorLoader(behaviorLoader);
        this.selectCriteria = selectCriteria;
        this.deleteCriteria = deleteCriteria;
    }

    /**
     * Gets the Behavior attribute of the OperationSettings object
     *
     * @return The Behavior value
     *
     * @exception PersistenceException Description of Exception
     */
    public Behavior getBehavior() throws PersistenceException {
        return (Behavior)behaviorRef.getObject();
    }


    /**
     * Gets the Commentry attribute of the OperationSettings object
     *
     * @return The Commentry value
     */
    public String getCommentry() {
        return commentry;
    }


    /**
     * Récupère la table Destination de l'opération
     *
     * @return La table destination
     */
    public Table getDestTable() {
        if (behaviorRef.isLoaded()) {
            return getLoadedBehavior().getDestTable();
        }
        else {
            return (Table)tableDestRef.getLoadedObject();
        }
    }


    /**
     * Retourne le type de l'operation. Le type est une String tel que : "IMPORT"...
     *
     * @return le type de l'operation
     */
    public String getOperationType() {
        return behaviorLoader.getBehaviorLabel();
    }


    /**
     * Retourne la liste des groupes de portefeuille.
     *
     * @return Liste de Reference sur PortfolioGroup.
     */
    public java.util.List getPortfolioGroupList() {
        return portfolioGroupList;
    }


    /**
     * Gets the Priority attribute of the OperationSettings object
     *
     * @return The Priority value
     */
    public int getPriority() {
        return priority;
    }


    /**
     * Récupère la table Source de l'opération
     *
     * @return La table source
     */
    public Table getSourceTable() {
        if (behaviorRef.isLoaded() == false) {
            return (Table)tableSourceRef.getLoadedObject();
        }
        else if (getLoadedBehavior().getSourceTable() != null) {
            return getLoadedBehavior().getSourceTable();
        }
        else {
            return (Table)tableSourceRef.getLoadedObject();
        }
    }


    /**
     * Gets the Automatic attribute of the OperationSettings object
     *
     * @return The Automatic value
     */
    public boolean isAutomatic() {
        return automatic;
    }


    /**
     * Sets the Automatic attribute of the OperationSettings object
     *
     * @param newAutomatic The new Automatic value
     */
    public void setAutomatic(boolean newAutomatic) {
        automatic = newAutomatic;
        setSynchronized(false);
    }


    /**
     * Sets the Commentry attribute of the OperationSettings object
     *
     * @param newCommentry The new Commentry value
     */
    public void setCommentry(String newCommentry) {
        commentry = newCommentry;
        setSynchronized(false);
    }


    /**
     * Sets the Priority attribute of the OperationSettings object
     *
     * @param priority The new Priority value
     */
    public void setPriority(int priority) {
        this.priority = priority;
        setSynchronized(false);
    }


    /**
     * Sets the TableRef attribute of the OperationSettings object
     *
     * @param newTable The new TableDestRef value
     */
    public void setTableDestRef(Table newTable) {
        tableDestRef = newTable.getReference();
        setSynchronized(false);
    }


    /**
     * Sets the TableRef attribute of the OperationSettings object
     *
     * @param newTable The new TableSourceRef value
     */
    public void setTableSourceRef(Table newTable) {
        tableSourceRef = newTable.getReference();
        setSynchronized(false);
    }


    /**
     * Retourne l'Id du behavior attache ou null.
     *
     * @return The BehaviorId value
     */
    Object getBehaviorId() {
        if (behaviorRef != null) {
            return behaviorRef.getId();
        }
        else {
            return null;
        }
    }


    /**
     * Gets the BehaviorLoader attribute of the OperationSettings object
     *
     * @return The BehaviorLoader value
     */
    BehaviorLoader getBehaviorLoader() {
        return behaviorLoader;
    }


    /**
     * Retourne le critère spécifique de delete de l'operation. Ce critère est utilisé
     * dans la clause <code>where</code> pour effacer les lignes dans la table
     * destination.
     *
     * @return La valeur de deleteCriteria (format SQL)
     */
    public String getDeleteCriteria() {
        return deleteCriteria;
    }


    /**
     * Retourne le comportement charge en memoire (sinon <code>null</code> ).
     *
     * @return The Behavior value
     */
    Behavior getLoadedBehavior() {
        return (Behavior)behaviorRef.getLoadedObject();
    }


    /**
     * Retourne le critere spécifique de selection de l'operation. Ce critere est utilisé
     * dans la clause <code>where</code> pour selectionner les lignes dans la table
     * source.
     *
     * @return La valeur de selectCriteria (format SQL)
     */
    String getSelectCriteria() {
        return selectCriteria;
    }


    /**
     * Retourne l'attribut timestamp de OperationSettings. Retourne Long.MAX_VALUE si le
     * dechargement du behavior est locké.
     *
     * @return La valeur de timestamp
     */
    long getTimestamp() {
        return timestamp;
    }


    /**
     * Indique si le dechargement du behavior est locké.
     *
     * @return
     */
    boolean isUnloadBehaviorLocked() {
        return timestamp == Long.MAX_VALUE;
    }


    /**
     * Lock le dechargement du behavior.
     */
    void lockUnloadBehavior() {
        timestamp = Long.MAX_VALUE;
    }


    /**
     * Sets the BehaviorLoader attribute of the OperationSettings object
     *
     * @param newBehaviorLoader The new BehaviorLoader value
     */
    void setBehaviorLoader(BehaviorLoader newBehaviorLoader) {
        behaviorLoader = newBehaviorLoader;
        setSynchronized(false);
    }


    /**
                                                                                                 */
    void unloadBehavior() {
        behaviorRef.unload();
    }


    /**
     * Unlock le dechargement du behavior.
     */
    void unlockUnloadBehavior() {
        timestamp = System.currentTimeMillis();
    }
}
