/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.utils.sql;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComponent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
/**
 * Ecouteur d'évenements pour les modifications des éléments
 *
 * @author $Author: marcona $
 * @version $Revision: 1.7 $
 *
 */
public class ModificationListener implements ActionListener, DocumentListener {
    private JComponent component;

    /**
     * Constructor for the ModificationListener object
     *
     * @param comp
     */
    public ModificationListener(JComponent comp) {
        this.component = comp;
        clear();
    }

    /**
     * DOCUMENT ME!
     *
     * @param evt
     */
    public void actionPerformed(ActionEvent evt) {
        changeColor();
    }


    /**
     * DOCUMENT ME!
     *
     * @param e
     */
    public void changedUpdate(DocumentEvent e) {
        changeColor();
    }


    /**
     * DOCUMENT ME!
     */
    public void clear() {
        component.setForeground(Color.black);
    }


    /**
     * DOCUMENT ME!
     *
     * @param e
     */
    public void insertUpdate(DocumentEvent e) {
        changeColor();
    }


    /**
     * DOCUMENT ME!
     *
     * @param e
     */
    public void removeUpdate(DocumentEvent e) {
        changeColor();
    }


    /**
                                                             */
    private void changeColor() {
        component.setForeground(Color.blue);
    }
}
