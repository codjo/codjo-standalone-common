/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.gui;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import javax.swing.JTextArea;
/**
 * Description of the Class
 *
 *
 */
public class TextAreaTargetDnd implements DropTargetListener {
    JTextArea text;

    /**
     * Constructor for the TextAreaTargetDnd object
     *
     * @param textComponent Description of Parameter
     */
    public TextAreaTargetDnd(JTextArea textComponent) {
        this.text = textComponent;
    }

    /**
     * Description of the Method
     *
     * @param event Description of Parameter
     */
    public void dragEnter(DropTargetDragEvent event) {
        event.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE);
    }


    /**
     * Description of the Method
     *
     * @param event Description of Parameter
     */
    public void dragOver(DropTargetDragEvent event) {}


    /**
     * Description of the Method
     *
     * @param dtde Description of Parameter
     */
    public void dropActionChanged(DropTargetDragEvent dtde) {}


    /**
     * Description of the Method
     *
     * @param dte Description of Parameter
     */
    public void dragExit(DropTargetEvent dte) {}


    /**
     * Description of the Method
     *
     * @param event Description of Parameter
     */
    public void drop(DropTargetDropEvent event) {
        try {
            Transferable transferable = event.getTransferable();

            // we accept only Strings
            if (transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                event.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                String s = (String)transferable.getTransferData(DataFlavor.stringFlavor);
                text.insert(s, text.getText().length());
                event.getDropTargetContext().dropComplete(true);
            }
            else {
                event.rejectDrop();
            }
        }
        catch (java.io.IOException exception) {
            exception.printStackTrace();
            System.err.println("Exception" + exception.getMessage());
            event.rejectDrop();
        }
        catch (UnsupportedFlavorException ufException) {
            ufException.printStackTrace();
            System.err.println("Exception" + ufException.getMessage());
            event.rejectDrop();
        }
    }
}
