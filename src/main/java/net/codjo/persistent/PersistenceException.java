/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.persistent;
/**
 * Exception indiquant une erreur dans la couche de persistance.
 *
 * @author $Author: blazart $
 * @version $Revision: 1.3 $
 *
 */
public class PersistenceException extends Exception {
    private Exception exception;

    /**
     * Constructor for the PersistenceException object
     */
    public PersistenceException() {
        this(null, null);
    }


    /**
     * Constructor for the PersistenceException object
     *
     * @param ex Description of Parameter
     */
    public PersistenceException(Exception ex) {
        this(ex, ex.getMessage());
    }


    /**
     * Constructor for the PersistenceException object
     *
     * @param s Detail message
     */
    public PersistenceException(String s) {
        super(s);
    }


    /**
     * Constructor for the PersistenceException object
     *
     * @param e Description of Parameter
     * @param s Detail message
     */
    public PersistenceException(Exception e, String s) {
        super(s);
        setException(e);
    }

    /**
     * Sets the Exception attribute of the PersistenceException object
     *
     * @param exception The new Exception value
     */
    public void setException(java.lang.Exception exception) {
        this.exception = exception;
    }


    /**
     * Gets the Exception attribute of the PersistenceException object
     *
     * @return The Exception value
     */
    public Exception getException() {
        return exception;
    }
}
