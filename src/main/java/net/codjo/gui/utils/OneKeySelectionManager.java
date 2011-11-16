/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.gui.utils;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
/**
 * This key selection manager will handle selections based on multiple keys. Gestion de
 * la recherche d'une valeur dans une combo box en fonction des caracteres tapés au
 * clavier
 *
 * @version $Revision: 1.4 $
 */
public class OneKeySelectionManager implements JComboBox.KeySelectionManager {
    long lastKeyTime = 0;
    String pattern = "";

    /**
     * Description of the Method
     *
     * @param aKey Description of Parameter
     * @param model Description of Parameter
     *
     * @return Description of the Returned Value
     */
    public int selectionForKey(char aKey, ComboBoxModel model) {
        // Find index of selected item
        int selIx = 1;
        Object sel = model.getSelectedItem();

        if (sel != null) {
            for (int i = 0; i < model.getSize(); i++) {
                if (sel.equals(model.getElementAt(i))) {
                    selIx = i;
                    break;
                }
            }
        }

        // Get the current time
        long curTime = System.currentTimeMillis();

        // If last key was typed less than 1000 ms ago, append to current pattern
        if (curTime - lastKeyTime < 1000) {
            pattern += ("" + aKey).toLowerCase();
        }
        else {
            pattern = ("" + aKey).toLowerCase();
        }

        // Save current time
        lastKeyTime = curTime;

        // Search forward from current selection
        for (int i = selIx + 1; i < model.getSize(); i++) {
            String item = model.getElementAt(i).toString().toLowerCase();
            if (item.startsWith(pattern)) {
                return i;
            }
        }

        // Search from top to current selection
        for (int i = 0; i < selIx; i++) {
            if (model.getElementAt(i) != null) {
                String item = model.getElementAt(i).toString().toLowerCase();
                if (item.startsWith(pattern)) {
                    return i;
                }
            }
        }
        return -1;
    }
}
