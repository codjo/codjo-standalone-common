/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.gui;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.Timer;
/**
 * Description of the Class
 *
 * @version $Revision: 1.6 $
 *
 *
 */
public class MemoryPanel extends JPanel {
    private static final int REFRESH_DELAY = 1000;
    JButton forceGCButton = new JButton();
    JLabel jLabel1 = new JLabel();
    JTextField memoryLabel = new JTextField();
    Timer timer;

    /**
     * Constructeur de MemoryPanel
     */
    public MemoryPanel() {
        jbInit();
        updateMemoryLabel();
        timer =
            new Timer(REFRESH_DELAY,
                new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                        updateMemoryLabel();
                    }
                });
        timer.start();
    }

    /**
     * Force le lancement du garbage collector.
     *
     * @param evt
     */
    void forceGCButton_actionPerformed(ActionEvent evt) {
        System.gc();
        System.runFinalization();
    }


    /**
                                                                                                 */
    void jbInit() {
        forceGCButton.setText("Forcer GC");
        forceGCButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    forceGCButton_actionPerformed(evt);
                }
            });
        memoryLabel.setToolTipText("");
        memoryLabel.setEditable(false);
        memoryLabel.setMargin(new Insets(0, 10, 0, 10));
        memoryLabel.setText("xx / xx");
        memoryLabel.setColumns(15);
        memoryLabel.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel1.setText("Mémoire : ");
        this.add(jLabel1, null);
        this.add(memoryLabel, null);
        this.add(forceGCButton, null);
    }


    /**
                                                                                                 */
    private void updateMemoryLabel() {
        memoryLabel.setText(((Runtime.getRuntime().totalMemory()
            - Runtime.getRuntime().freeMemory()) / 1000) + " / "
            + (Runtime.getRuntime().totalMemory() / 1000) + " ko");
    }
}
