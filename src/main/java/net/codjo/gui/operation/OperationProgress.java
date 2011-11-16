/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.gui.operation;
import net.codjo.gui.toolkit.swing.SwingWorker;
import net.codjo.gui.toolkit.util.ErrorDialog;
import net.codjo.gui.treatment.SleeveAuditHelper;
import net.codjo.operation.OperationFailureException;
import net.codjo.persistent.PersistenceException;
import net.codjo.utils.JukeBox;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.sql.SQLException;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ProgressMonitor;
import javax.swing.Timer;
import org.apache.log4j.Logger;

/**
 * Effectue une operation, avec affichage de sa progression.
 *
 * @author $Author: acharif $
 * @version $Revision: 1.7 $
 */
public class OperationProgress extends MouseAdapter implements Runnable {
    private static final int ONE_SECOND = 1000;
    private boolean calculatingLengthOfTask = true;
    private JPanel glassPanel;
    private JukeBox jukeBox;
    private JInternalFrame managerWindow;
    private ProgressData operation;
    private boolean operationDone;
    private ProgressMonitor progressMonitor;
    private Thread thread;
    private Timer timer;
    private ModalVetoableChangeListener vcl = new ModalVetoableChangeListener();
    private SwingWorker worker;
    private WaitingWindowManager waitingWindowManager;
    // Log
    private static final Logger APP = Logger.getLogger(OperationProgress.class);


    /**
     * Constructeur.
     *
     * @param op                   L'operation a effectuer (interface permettant d'adapter le code en fonction
     *                             de l'application).
     * @param jb                   Le JukeBox des sons
     * @param waitingWindowManager Le gestionnaire d'affichage de la waitingWindow
     */
    public OperationProgress(ProgressData op, JukeBox jb,
                             WaitingWindowManager waitingWindowManager) {
        operation = op;
        jukeBox = jb;
        this.waitingWindowManager = waitingWindowManager;
    }


    /**
     * Lance l'operation. Le composant <code>gui</code> sera <code>repaint</code> a la fin du traitement.
     *
     * @param firstLaunch          Traitement complet (sinon Reprise du traitement)
     * @param gui                  Le composant lancant l'operation.
     * @param endOperationListener Runner dont la methode run sera appelee en fin de tache (dans le thread
     *                             Event)
     *
     * @throws PersistenceException      Impossible de charger le behavior
     * @throws OperationFailureException Impossible de determiner la longueur de la tache.
     * @throws SQLException              Erreur d'accès base
     * @throws IllegalArgumentException  TODO
     */
    public void go(boolean firstLaunch, JInternalFrame gui, Runnable endOperationListener)
          throws PersistenceException, OperationFailureException, SQLException {
        if (gui == null || endOperationListener == null) {
            throw new IllegalArgumentException();
        }

        managerWindow = gui;

        operation.prepareProceed(firstLaunch);

        thread = new Thread(this);
        thread.start();

        progressMonitor =
              new ProgressMonitor(gui, operation.getProgressMessage(), "", 0, 100);

        timer = new Timer(ONE_SECOND, new OperationListener());

        worker = new OperationWorker(gui, endOperationListener, waitingWindowManager);

        operationDone = false;
        addModalRestriction();
        try {
            worker.start();
            timer.start();
        }
        catch (RuntimeException ex) {
            removeModalRestriction();
            throw ex;
        }
        catch (Error er) {
            removeModalRestriction();
            throw er;
        }
    }


    /**
     * Overview.
     *
     * <p> Description </p>
     *
     * @param e Description of Parameter
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        Toolkit.getDefaultToolkit().beep();
    }


    /**
     * Main processing method for the OperationProgress object
     */
    public void run() {
        try {
            operation.determineLengthOfTask();
        }
        catch (OperationFailureException ex) {
            ex.printStackTrace();
        }
        this.calculatingLengthOfTask = false;
    }


    /**
     * DOCUMENT ME!
     *
     * @return The CurrentOfTask value
     */
    private int getCurrentOfTask() {
        return operation.getCurrentOfTask() * 100;
    }


    /**
     * DOCUMENT ME!
     *
     * @return The LengthOfTask value
     */
    private int getLengthOfTask() {
        int length = operation.getLengthOfTask();
        if (length == 0) {
            length = 1;
        }
        return length * 100;
    }


    /**
     * Gets the Message attribute of the OperationProgress object
     *
     * @return The Message value
     */
    private String getStateMessage() {
        return "Enregistrement " + getCurrentOfTask() / 100 + " / "
               + getLengthOfTask() / 100 + ".";
    }


    /**
     * Empeche la selection de la fenêtre managerWindow.
     */
    private void addModalRestriction() {
        glassPanel = new JPanel();
        glassPanel.setOpaque(false);
        glassPanel.addMouseListener(this);
        managerWindow.setGlassPane(glassPanel);
        managerWindow.addVetoableChangeListener(vcl);
        try {
            managerWindow.setSelected(false);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /**
     * Redonne la possibilite de selectionner la fenêtre managerWindow.
     */
    private void removeModalRestriction() {
        managerWindow.removeVetoableChangeListener(vcl);
        managerWindow.remove(glassPanel);
        try {
            managerWindow.setSelected(true);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /**
     * Evite la selection de la fenêtre managerWindow.
     *
     * @author $Author: acharif $
     * @version $Revision: 1.7 $
     */
    static class ModalVetoableChangeListener implements VetoableChangeListener {
        /**
         * DOCUMENT ME!
         *
         * @param evt Changement de propriété de la fenêtre source.
         *
         * @throws PropertyVetoException Exception sur le changement.
         */
        public void vetoableChange(PropertyChangeEvent evt)
              throws PropertyVetoException {
            if ("selected".equals(evt.getPropertyName())
                && Boolean.TRUE.equals(evt.getNewValue())) {
                throw new PropertyVetoException("En cours d'edition", evt);
            }
        }
    }

    /**
     * Rafraichit le ProgressMonitor en fonction de l'etat de l'operation.
     *
     * @author $Author: acharif $
     * @version $Revision: 1.7 $
     */
    private class OperationListener implements ActionListener {
        /**
         * DOCUMENT ME!
         *
         * @param evt Description of Parameter
         */
        public void actionPerformed(ActionEvent evt) {
            if (operationDone) {
                Toolkit.getDefaultToolkit().beep();
                timer.stop();
                progressMonitor.close();
                thread.interrupt();
                removeModalRestriction();
            }
            else if ((progressMonitor.isCanceled()) && (!thread.isInterrupted())) {
                timer.stop();
                Object[] choix = {"Non je continue", "Oui je veux arrêter"};
                int answer = -1;
                while (answer == -1) {
                    answer =
                          JOptionPane.showOptionDialog(null,
                                                       "Etes-vous sûr(e) de vouloir annuler l'opération en cours ?",
                                                       "Arrêt de l'opération",
                                                       JOptionPane.YES_NO_OPTION,
                                                       JOptionPane.QUESTION_MESSAGE,
                                                       null,
                                                       choix,
                                                       null);
                }

                if (answer == 1) {
                    worker.interrupt();
                    thread.interrupt();
                    removeModalRestriction();
                }
                else {
                    progressMonitor =
                          new ProgressMonitor(managerWindow,
                                              operation.getOperationType() + " de "
                                              + operation.getSourceTable().getTableName() + " vers "
                                              + operation.getDestTable().getTableName() + " en cours ...",
                                              "", 0, 100);
                    timer.start();
                }
            }
            else {
//                APP.debug("--> Memory : " + Runtime.getRuntime().totalMemory());
//                APP.debug("--> Time   : " + System.currentTimeMillis() / 1000);
                if (calculatingLengthOfTask) {
                    progressMonitor.setNote("Enregistrement " + getCurrentOfTask() / 100
                                            + " / ...calcul en cours");
                    progressMonitor.setProgress(1);
                }
                else {
                    double progress = getCurrentOfTask() / (getLengthOfTask() / 100);
                    progressMonitor.setNote(getStateMessage());
                    progressMonitor.setProgress((int)progress);
                }
            }
        }
    }

    /**
     * Execute l'operation dans un thread de type SwingWorker.
     *
     * @author $Author: acharif $
     * @version $Revision: 1.7 $
     */
    private class OperationWorker extends SwingWorker {
        private Runnable endOperationListener;
        private Component gui;
        private WaitingWindowManager waitingWindowManager;


        /**
         * Constructor for the OperationWorker object
         *
         * @param endOperationListener Runner appele en fin de tache
         * @param waitingWindowManager Le gestionnaire d'affichage de la waitingWindow
         */
        private OperationWorker(Component gui, Runnable endOperationListener,
                                WaitingWindowManager waitingWindowManager) {
            this.gui = gui;
            this.endOperationListener = endOperationListener;
            this.waitingWindowManager = waitingWindowManager;
        }


        /**
         * Execute l'operation.
         *
         * @return l'operation executee.
         */
        @Override
        public Object construct() {
            try {
                operation.proceed();
                operationDone = true;
                jukeBox.playSuccessSound();
            }
            catch (OperationFailureException ex) {
                if (operation instanceof OperationProgressData) {
                    OperationFailureHelper.getInstance().addError(((OperationProgressData)operation)
                          .getOperationId(), ex.getLocalizedMessage());
                }
                else {
                    SleeveAuditHelper.getInstance().setErrorMsg(ex.getLocalizedMessage());
                }
                ex.printStackTrace();
                operationDone = true;
                jukeBox.playFailureSound();
                ErrorDialog.show(gui, "Erreur pendant l'opération!",
                                 ex.getLocalizedMessage());
                waitingWindowManager.disposeWaitingWindow();
            }
            return operation;
        }


        /**
         * Enregistre l'operation et previent l'IHM.
         */
        @Override
        public void finished() {
            waitingWindowManager.disposeWaitingWindow();
            endOperationListener.run();
        }
    }
}
