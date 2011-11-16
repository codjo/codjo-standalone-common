/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.gui;
import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.MenuComponent;
import java.awt.MenuContainer;
import javax.swing.SwingUtilities;
/**
 * Gestion de l'affichage du curseur 'Sablier'
 *
 * @version $Revision: 1.3 $
 *
 *
 */
public class WaitCursorEventQueue extends EventQueue {
    private int delay;
    private WaitCursorTimer waitTimer;

    /**
     * Constructor for the WaitCursorEventQueue object
     *
     * @param delay Délai (millisecondes) avant affichage du sablier
     */
    public WaitCursorEventQueue(int delay) {
        this.delay = delay;
        waitTimer = new WaitCursorTimer();
        waitTimer.setDaemon(true);
        waitTimer.start();
    }

    /**
     * Description of the Method
     *
     * @param event Description of the Parameter
     */
    protected void dispatchEvent(AWTEvent event) {
        waitTimer.startTimer(event.getSource());
        try {
            super.dispatchEvent(event);
        }
        finally {
            waitTimer.stopTimer();
        }
    }

    private class WaitCursorTimer extends Thread {
        private Object source;
        private Component parent;

        /**
         * Main processing method for the WaitCursorTimer object
         */
        public synchronized void run() {
            while (true) {
                try {
                    wait();
                    wait(delay);

                    if (source instanceof Component) {
                        parent = SwingUtilities.getRoot((Component)source);
                    }
                    else if (source instanceof MenuComponent) {
                        MenuContainer mParent = ((MenuComponent)source).getParent();
                        if (mParent instanceof Component) {
                            parent = SwingUtilities.getRoot((Component)mParent);
                        }
                    }

                    if (parent != null && parent.isShowing()) {
                        parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    }
                }
                catch (InterruptedException ie) {}
            }
        }


        synchronized void startTimer(Object source) {
            this.source = source;
            notify();
        }


        synchronized void stopTimer() {
            if (parent == null) {
                interrupt();
            }
            else {
                parent.setCursor(null);
                parent = null;
            }
            parent = null;
            source = null;
        }
    }
}
