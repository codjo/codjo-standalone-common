/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.operation;
/**
 * Exception qui indique qu'une opération a échoué.
 *
 * @author $Author: blazart $
 * @version $Revision: 1.2 $
 *
 */
public class OperationFailureException extends Exception {
    private Operation operation;

    /**
     * Constructeur.
     *
     * @param s Message de l'exception
     * @param ope L'opération
     */
    public OperationFailureException(String s, Operation ope) {
        super(s);
        operation = ope;
    }


    /**
     * Constructeur.
     *
     * @param s Message de l'exception
     */
    public OperationFailureException(String s) {
        super(s);
    }

    /**
     * Retourne l'operation ayant echoue
     *
     * @return The Operation
     */
    public Operation getOperation() {
        return operation;
    }
}
