/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.gui.renderer;
import java.text.DateFormat;
import javax.swing.JLabel;
import javax.swing.table.DefaultTableCellRenderer;
/**
 * Renderer pour des objets Date affichant L'heure et La date au format francais.
 *
 * @version $Revision: 1.3 $
 *
 * @deprecated utiliser la version codjo-gui-toolkit :  <code>new
 *             DateRenderer(DateFormat.getDateTimeInstance(DateFormat.SHORT,
 *             DateFormat.SHORT));</code>
 */
@Deprecated
public class DateTimeRenderer extends DefaultTableCellRenderer {
    private static final DateFormat formatter =
        DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);

    /**
     * Constructor for the DateTimeRenderer object
     */
    public DateTimeRenderer() {
        setHorizontalAlignment(JLabel.CENTER);
    }

    /**
     * Sets the Value attribute of the ManagerWindow object
     *
     * @param value The new Value value
     */
    @Override
    public void setValue(Object value) {
        if (value == null) {
            setText("");
        }
        else {
            setText(formatter.format(value));
        }
    }
}
