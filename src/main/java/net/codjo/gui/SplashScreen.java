/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.gui;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
/**
 * Affiche une mire de démarrage.
 *
 * @version $Revision: 1.3 $
 *
 *
 */
public class SplashScreen extends JWindow {
    /**
     * Constructor for the SplashScreen object
     *
     * @param filename Répertoire du fichier image
     * @param frm Une Frame
     * @param waitTime Temps d'affichage
     */
    public SplashScreen(String filename, Frame frm, int waitTime) {
        this(new ImageIcon(filename), frm, waitTime);
    }


    /**
     * Constructeur de SplashScreen
     *
     * @param icon Description of Parameter
     * @param frm Description of Parameter
     * @param waitTime Description of Parameter
     */
    public SplashScreen(Icon icon, Frame frm, int waitTime) {
        super(frm);
        JLabel l = new JLabel(icon);
        getContentPane().add(l, BorderLayout.CENTER);
        pack();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension labelSize = l.getPreferredSize();
        setLocation(screenSize.width / 2 - (labelSize.width / 2),
            screenSize.height / 2 - (labelSize.height / 2));
        addMouseListener(new MouseAdapter() {
                /**
                 * DOCUMENT ME!
                 *
                 * @param e Description of Parameter
                 */
                public void mousePressed(MouseEvent e) {
                    setVisible(false);
                    dispose();
                }
            });

        final int pause = waitTime;
        final Runnable closerRunner =
            new Runnable() {
                /**
                 * Main processing method for the SplashScreen object
                 */
                public void run() {
                    setVisible(false);
                    dispose();
                }
            };
        Runnable waitRunner =
            new Runnable() {
                /**
                 * Main processing method for the SplashScreen object
                 */
                public void run() {
                    try {
                        Thread.sleep(pause);
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                        // can catch InvocationTargetException
                        // can catch InterruptedException
                    }finally{
                        try {
                            SwingUtilities.invokeAndWait(closerRunner);
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
        setVisible(true);
        Thread splashThread = new Thread(waitRunner, "SplashThread");
        splashThread.start();
    }
}
