package net.codjo.operation;
/**
 * <p>Listener for save() on Operation instances.</p>
 *
 */
public interface OperationSaveListener {
    /** Triggered before the Operation save method is effectively called.*/
    public void onBeforeSave(Operation operation);
    
    /** Triggered after the Operation save method is effectively called.*/
    public void onAfterSave(Operation operation);
}
