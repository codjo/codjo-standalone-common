/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.utils;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.UIManager;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import org.apache.log4j.Logger;

/**
 * Overview.
 *
 * @author $Author: acharif $
 * @version $Revision: 1.8 $
 *
 */
public class BasicUndoManager {
    private UndoManager undo = new UndoManager();
    private UndoAction undoAction = new UndoAction();
    private RedoAction redoAction = new RedoAction();
    private ExpressionUndoableEditListener listener =
        new ExpressionUndoableEditListener();

    // Log
    private static final Logger APP = Logger.getLogger(BasicUndoManager.class);


    /**
     * Constructor for the BasicUndoManager object
     */
    public BasicUndoManager() {}

    /**
     * Gets the UndoableEditListener attribute of the BasicUndoManager object
     *
     * @return The UndoableEditListener value
     */
    public UndoableEditListener getUndoableEditListener() {
        return listener;
    }


    /**
     * Gets the UndoAction attribute of the BasicUndoManager object
     *
     * @return The UndoAction value
     */
    public Action getUndoAction() {
        return undoAction;
    }


    /**
     * Gets the RedoAction attribute of the BasicUndoManager object
     *
     * @return The RedoAction value
     */
    public Action getRedoAction() {
        return redoAction;
    }

    /**
     * Overview.
     *
     * @author $Author: acharif $
     * @version $Revision: 1.8 $
     */
    private class ExpressionUndoableEditListener implements UndoableEditListener {
        /**
         * DOCUMENT ME!
         *
         * @param e Description of Parameter
         */
        public void undoableEditHappened(UndoableEditEvent e) {
            undo.addEdit(e.getEdit());
            undoAction.updateUndoState();
            redoAction.updateRedoState();
        }
    }


    /**
     * Action Undo.
     *
     * @author $Author: acharif $
     * @version $Revision: 1.8 $
     */
    private class UndoAction extends AbstractAction {
        /**
                                                                                                                                                                         */
        public UndoAction() {
            putValue(SMALL_ICON, UIManager.getIcon("textAction.undo"));
            setEnabled(false);
        }

        /**
         * Overview.
         */
        public void updateUndoState() {
            setEnabled(undo.canUndo());
        }


        /**
         * DOCUMENT ME!
         *
         * @param arg0 Description of Parameter
         */
        public void actionPerformed(java.awt.event.ActionEvent arg0) {
            try {
                undo.undo();
            }
            catch (CannotUndoException ex) {}
            updateUndoState();
            redoAction.updateRedoState();
        }
    }


    /**
     * Action Undo.
     *
     * @author $Author: acharif $
     * @version $Revision: 1.8 $
     */
    private class RedoAction extends AbstractAction {
        /**
                                                                                                                                                                         */
        public RedoAction() {
            putValue(SMALL_ICON, UIManager.getIcon("textAction.redo"));
            setEnabled(false);
        }

        /**
         * Overview.
         */
        public void updateRedoState() {
            setEnabled(undo.canRedo());
        }


        /**
         * DOCUMENT ME!
         *
         * @param arg0 Description of Parameter
         */
        public void actionPerformed(java.awt.event.ActionEvent arg0) {
            try {
                undo.redo();
            }
            catch (CannotRedoException ex) {
                APP.error("Unable to redo: " + ex);
                ex.printStackTrace();
            }
            updateRedoState();
            undoAction.updateUndoState();
        }
    }
}
