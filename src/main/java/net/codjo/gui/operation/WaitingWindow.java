/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.gui.operation;
import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
/**
 * Construit une fenêtre d'attente lors de l'execution d'un traitement.
 *
 * @author $Author: blazart $
 * @version $Revision: 1.3 $
 *
 *
 */
public class WaitingWindow extends JInternalFrame {
    JLabel labelMessage = new JLabel();
    BorderLayout borderLayout1 = new BorderLayout();
    private String waitingMessage;

    /**
     * Constructeur.
     *
     * @param waitingMessage TODO
     */
    WaitingWindow(String waitingMessage) {
        super("Veuillez patienter...", false, false, false, false);
        this.waitingMessage = waitingMessage;
        try {
            jbInit();
            pack();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Init de l'IHM.
     *
     * @exception Exception Pb ???
     */
    private void jbInit() throws Exception {
        labelMessage.setFont(new java.awt.Font("Dialog", 1, 14));
        labelMessage.setHorizontalAlignment(SwingConstants.CENTER);
        labelMessage.setText(waitingMessage);
        this.getContentPane().setLayout(borderLayout1);
        this.getContentPane().setBackground(Color.lightGray);
        this.getContentPane().add(labelMessage, BorderLayout.CENTER);
    }
}
