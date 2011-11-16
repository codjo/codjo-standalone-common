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
public class UnknownIdException extends PersistenceException {
    /**
     * Constructor for the PersistenceException object
     */
    public UnknownIdException() {
        super(null, null);
    }


    /**
     * Constructor for the PersistenceException object
     *
     * @param ex Description of Parameter
     */
    public UnknownIdException(Exception ex) {
        super(ex, ex.getMessage());
    }


    /**
     * Constructor for the PersistenceException object
     *
     * @param s Detail message
     */
    public UnknownIdException(String s) {
        super(s);
    }


    /**
     * Constructor for the PersistenceException object
     *
     * @param e Description of Parameter
     * @param s Detail message
     */
    public UnknownIdException(Exception e, String s) {
        super(e, s);
    }
}
