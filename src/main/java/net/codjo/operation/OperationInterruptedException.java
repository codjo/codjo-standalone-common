/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.operation;
/**
 * Exception qui indique qu'une opération a été interrompue
 *
 * @author $Author: blazart $
 * @version $Revision: 1.2 $
 *
 */
public class OperationInterruptedException extends OperationFailureException {
    /**
     * Constructeur avec une opération.
     *
     * @param s le message d'erreur.
     * @param ope L'opération.
     */
    public OperationInterruptedException(String s, Operation ope) {
        super(s, ope);
    }


    /**
     * Constructeur sans opération.
     *
     * @param s le message d'erreur.
     */
    public OperationInterruptedException(String s) {
        this(s, null);
    }
}
