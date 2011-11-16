/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.gui.operation;

// Operation stuff
import net.codjo.operation.OperationState;
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.table.DefaultTableCellRenderer;
/**
 * Renderer pour les operation State.
 *
 * @author $Author: blazart $
 * @version $Revision: 1.2 $
 *
 *
 */
public class OperationStateRenderer extends DefaultTableCellRenderer {
    /**
     * Constructor for the OperationStateRenderer object
     */
    public OperationStateRenderer() {
        setHorizontalAlignment(JLabel.CENTER);
    }

    /**
     * DOCUMENT ME!
     *
     * @param value The new Value value
     */
    public void setValue(Object value) {
        int stateCode = ((Integer)value).intValue();

        switch (stateCode) {
            case OperationState.TO_DO:
                setText("A faire");
                setBackground(Color.red);
                break;
            case OperationState.DONE:
                setText("Fait");
                setBackground(Color.green);
                break;
            case OperationState.FAILED:
                setText("Echec");
                setBackground(Color.orange);
                break;
            default:
                setText("N/A");
                setBackground(Color.cyan);
        }
    }
}
