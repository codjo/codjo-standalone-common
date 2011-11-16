/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.gui.operation;
// Librairies AGF
import net.codjo.model.Table;
import net.codjo.operation.Operation;
import net.codjo.operation.OperationFailureException;
import java.util.ArrayList;
/**
 * Implementation de l'interface ProgressData pour les applications Penelope/Alis.
 *
 * @version $Revision: 1.3 $
 */
public class OperationProgressData implements ProgressData {
    private Operation operation;


    /**
     * Constructeur.
     *
     * @param operation L'operation.
     */
    public OperationProgressData(Operation operation) {
        if (operation == null) {
            throw new IllegalArgumentException();
        }
        this.operation = operation;
    }


    /**
     * Fixe la restriction sur les codes portefeuilles.
     *
     * @param ptfRestrictList La liste
     */
    public void setPtfRestrictionList(ArrayList ptfRestrictList) {
        operation.setPtfRestrictionList(ptfRestrictList);
    }


    /**
     * Retourne le message à afficher dans la barre de progression du traitement
     *
     * @return Le message à afficher dans la barre de progression du traitement
     */
    public String getProgressMessage() {
        String srcTable = "N/A";
        if (getSourceTable() != null) {
            srcTable = getSourceTable().getTableName();
        }

        String destTable = "N/A";
        if (getDestTable() != null) {
            destTable = getDestTable().getTableName();
        }

        return getOperationType() + " de " + srcTable + " vers " + destTable
               + " en cours ...";
    }


    /**
     * Retourne la table source de l'opération
     *
     * @return La table source de l'opération
     */
    public Table getSourceTable() {
        return operation.getSourceTable();
    }


    /**
     * retourne l'ID de l'opération
     */
    public Integer getOperationId() {
        return new Integer(operation.getOperationSettings().getId().toString());
    }


    /**
     * Retourne la table destination de l'opération
     *
     * @return La table destination de l'opération
     */
    public Table getDestTable() {
        return operation.getDestTable();
    }


    /**
     * Retourne le type de l'operation.
     *
     * @return Le type de l'operation
     */
    public String getOperationType() {
        return operation.getOperationType();
    }


    /**
     * Retourne le compteur courant de la tache.
     *
     * @return Le compteur courant de la tache
     */
    public int getCurrentOfTask() {
        return operation.getLoadedBehavior().getCurrentOfTask();
    }


    public String getCompany() {
        return operation.getCompany();
    }


    public void setCompany(String company) {
        operation.setCompany(company);
    }


    /**
     * Retourne la taille totale de la tache. Cette valeur est initialise par la methode
     * determineLengthOfTask.
     *
     * @return La taille totale de la tache
     *
     * @see #determineLengthOfTask
     */
    public int getLengthOfTask() {
        return operation.getLoadedBehavior().getLengthOfTask();
    }


    /**
     * Prepare l'execution de l'opération.
     *
     * @param firstLaunch Premier lancement
     *
     * @throws OperationFailureException Erreur lors de l'opération
     */
    public void prepareProceed(boolean firstLaunch)
          throws OperationFailureException {
        operation.prepareProceed(firstLaunch);
    }


    /**
     * Execute l'opération.
     *
     * @throws OperationFailureException Erreur lors de l'opération
     */
    public void proceed() throws OperationFailureException {
        operation.proceed();
    }


    /**
     * Determine la longueur de l'opération à effectuer.
     *
     * @throws OperationFailureException Si la détermination échoue
     */
    public void determineLengthOfTask() throws OperationFailureException {
        operation.determineLengthOfTask();
    }


    /**
     * Appel la methode save du model.
     *
     * @see net.codjo.persistent.Model#save
     */
    public void save() throws net.codjo.persistent.PersistenceException {
        operation.save();
    }
}
