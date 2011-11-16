/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.gui.operation;
import java.util.HashMap;
import org.apache.log4j.Logger;

/**
 * Helper qui contient les messages d'erreurs des traitements lancés. Utilisé par la
 * piste d'audit de IRIS.
 *
 *
 */
public class OperationFailureHelper {
    private static OperationFailureHelper instance = null;
    private HashMap map;
    // Log
    private static final Logger APP = Logger.getLogger(OperationFailureHelper.class);

    private OperationFailureHelper() {
        map = new HashMap();
    }

    public static OperationFailureHelper getInstance() {
        if (instance == null) {
            instance = new OperationFailureHelper();
        }
        return instance;
    }


    public HashMap getErrorMap() {
        return map;
    }


    public void clearMap() {
        instance = new OperationFailureHelper();
    }


    public void addError(Integer operationId, String errorMsg) {
        map.put(operationId, errorMsg);
        APP.debug("codjo-commoun : ajout dans la map opID : " + operationId
            + " msgerror : " + errorMsg);
    }
}
