/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.operation;
import java.util.Date;
/**
 * Cette classe représente l'évolution de l'opération au cours du temps.
 * 
 * <p>
 * Elle n'existe que pour les opérations attachées à une tâche. Elle n'existe pas pour
 * les opérations de la table de paramétrage.
 * </p>
 *
 * @author $Author: blazart $
 * @version $Revision: 1.2 $
 *
 */
public class OperationState {
    /** Etat d'une tache a faire */
    public static final int TO_DO = 1;
    /** Etat d'une tache faite (tout est OK) */
    public static final int DONE = 2;
    /** Etat d'une tache ayant echoué */
    public static final int FAILED = 3;
    private int _state = OperationState.TO_DO;
    private Date _date = null;

    /**
     * Constructeur par défaut.
     * 
     * <p></p>
     */
    public OperationState() {}


    /**
     * Constructeur avec initialisation.
     * 
     * <p></p>
     *
     * @param date La date du changement d'etat
     * @param state Le nouvel etat
     */
    public OperationState(Date date, int state) {
        setState(date, state);
    }

    /**
     * Retourne l'etat.
     *
     * @return The State value
     */
    public int getState() {
        return _state;
    }


    /**
     * Retourne la date du changement d'etat.
     *
     * @return The Date value
     */
    public Date getDate() {
        return _date;
    }


    /**
     * Change l'etat.
     *
     * @param date La date du changement d'etat
     * @param state Le nouvel etat
     *
     * @throws IllegalArgumentException TODO
     */
    private void setState(Date date, int state) {
        // Precondition
        switch (state) {
            case TO_DO:
                if (date != null) {
                    throw new IllegalArgumentException(
                        "Bad Date (etat TO_DO doit avoir une date null)");
                }
                break;
            case DONE:
            case FAILED:
                if (date == null) {
                    throw new IllegalArgumentException(
                        "L'état doit avoir une date non null");
                }
                break;
            default:
                throw new IllegalArgumentException("Mauvais Identifiant d'etat");
        }

        // Init
        _state = state;
        _date = date;
    }
}
