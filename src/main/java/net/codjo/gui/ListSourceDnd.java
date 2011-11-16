/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.gui;
import java.awt.datatransfer.StringSelection;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import javax.swing.JList;
/**
 * Description of the Class
 *
 *
 */
public class ListSourceDnd implements DragGestureListener, DragSourceListener {
    private JList list;
    private DragSource dragSource;

    /**
     * Constructor for the DefaultDragGestureListener object
     *
     * @param ds Description of Parameter
     * @param list Description of Parameter
     */
    public ListSourceDnd(DragSource ds, JList list) {
        dragSource = ds;
        this.list = list;
    }

    /**
     * Description of the Method
     *
     * @param event Description of Parameter
     */
    public void dragGestureRecognized(DragGestureEvent event) {
        Object selected = list.getSelectedValue();
        if (selected != null) {
            StringSelection text = new StringSelection(selected.toString());

            // as the name suggests, starts the dragging
            dragSource.startDrag(event, DragSource.DefaultCopyNoDrop, text, this);
        }
    }


    /**
     * Description of the Method
     *
     * @param dsde Description of Parameter
     */
    public void dragEnter(DragSourceDragEvent dsde) {}


    /**
     * Description of the Method
     *
     * @param dsde Description of Parameter
     */
    public void dragOver(DragSourceDragEvent dsde) {}


    /**
     * Description of the Method
     *
     * @param dsde Description of Parameter
     */
    public void dropActionChanged(DragSourceDragEvent dsde) {}


    /**
     * Description of the Method
     *
     * @param dse Description of Parameter
     */
    public void dragExit(DragSourceEvent dse) {}


    /**
     * Description of the Method
     *
     * @param dsde Description of Parameter
     */
    public void dragDropEnd(DragSourceDropEvent dsde) {}
}
