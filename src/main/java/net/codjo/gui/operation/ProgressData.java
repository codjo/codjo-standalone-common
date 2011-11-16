/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.gui.operation;
import net.codjo.model.Table;
import net.codjo.operation.OperationFailureException;
import java.util.ArrayList;
/**
 * Interface permettant de redéfinir des méthodes de la classe Operation. Cela est nécessaire pour lancer une
 * operation de traitement en tenant compte des spécificités de l'application d'appel (Penelope / Alis /
 * Paris). Elle est utilisée dans le constructeur de la classe OperationProgress.
 *
 * @version $Revision: 1.2 $
 */
public interface ProgressData {
    /**
     * Prepare l'execution de l'opération.
     *
     * @param firstLaunch Premier lancement
     *
     * @throws OperationFailureException Erreur lors de l'opération
     */
    public void prepareProceed(boolean firstLaunch)
          throws OperationFailureException;


    /**
     * Execute l'opération.
     *
     * @throws OperationFailureException Erreur lors de l'opération
     */
    public void proceed() throws OperationFailureException;


    /**
     * Retourne la table source de l'opération
     *
     * @return La table source de l'opération
     */
    public Table getSourceTable();


    /**
     * Retourne le message à afficher dans la barre de progression du traitement
     *
     * @return Le message à afficher dans la barre de progression du traitement
     */
    public String getProgressMessage();


    /**
     * Retourne la table destination de l'opération
     *
     * @return La table destination de l'opération
     */
    public Table getDestTable();


    /**
     * Retourne le type de l'operation.
     *
     * @return Le type de l'operation
     */
    public String getOperationType();


    /**
     * Determine la longueur de l'opération à effectuer.
     *
     * @throws OperationFailureException Si la détermination échoue
     */
    public void determineLengthOfTask() throws OperationFailureException;


    /**
     * Fixe la restriction sur les codes portefeuilles.
     *
     * @param ptfRestrictList La liste
     */
    public void setPtfRestrictionList(ArrayList ptfRestrictList);


    /**
     * Appel la methode save du model.
     *
     * @throws net.codjo.persistent.PersistenceException
     *          -
     * @see net.codjo.persistent.Model#save
     */
    public void save() throws net.codjo.persistent.PersistenceException;


    /**
     * Retourne le compteur courant de la tache.
     *
     * @return Le compteur courant de la tache
     */
    public int getCurrentOfTask();


    /**
     * Retourne la taille totale de la tache. Cette valeur est initialise par la methode
     * determineLengthOfTask.
     *
     * @return La taille totale de la tache
     *
     * @see #determineLengthOfTask
     */
    public int getLengthOfTask();
}
