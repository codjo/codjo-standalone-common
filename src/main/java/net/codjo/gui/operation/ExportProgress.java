/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.gui.operation;

//Lib
import net.codjo.utils.sql.GenericTable;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;
import javax.swing.Timer;
/**
 * Classe permettant d'executer l'export dans un thread séparé avec une progress barre.
 *
 * @author $Author: blazart $
 * @version $Revision: 1.2 $
 *
 *
 */
public class ExportProgress extends JFrame {
    private static final int ONE_SECOND = 1000;
    private ProgressMonitor progressMonitor;
    private ExportManager manager;
    private Timer timer;
    private GenericTable genericTable;
    private String filename;
    private boolean doReload;

    /**
     * Constructeur.
     *
     * @param g La GenericTable sur laquelle on récupère les données à exporter.
     * @param filename Le nom du fichier d'export.
     * @param reloadAtEnd TODO
     *
     * @throws IllegalArgumentException TODO
     */
    public ExportProgress(GenericTable g, String filename, boolean reloadAtEnd) {
        super("Opération en cours...");
        if (g == null || filename == null) {
            throw new IllegalArgumentException();
        }
        genericTable = g;
        this.filename = filename;
        doReload = reloadAtEnd;
    }

    /**
     * Lance l'opération d'export.
     *
     * @param endOperationListener Runner dont la méthode run sera appelée en fin de
     *        tâche (dans le thread Event).
     */
    public void go(Runnable endOperationListener) {
        manager = new ExportManager(genericTable, filename, doReload);
        timer = new Timer(ONE_SECOND, new TimerListener());
        progressMonitor =
            new ProgressMonitor(ExportProgress.this,
                "Export de la table " + genericTable.getTable().getTableName()
                + " en cours...", "", 0, manager.getLengthOfTask());
        progressMonitor.setProgress(0);
        progressMonitor.setMillisToDecideToPopup(ONE_SECOND / 10);
        manager.go(endOperationListener);
        timer.start();
    }

    /**
     * Rafraichit le ProgressMonitor en fonction de l'état de l'export.
     *
     * @author $Author: blazart $
     * @version $Revision: 1.2 $
     */
    class TimerListener implements ActionListener {
        /**
         * DOCUMENT ME!
         *
         * @param evt Description of Parameter
         */
        public void actionPerformed(ActionEvent evt) {
            if (manager.done()) {
                progressMonitor.close();
                manager.stop();
                Toolkit.getDefaultToolkit().beep();
            }
            else if (progressMonitor.isCanceled()) {
                timer.stop();
                Object[] choix = {"Non je continue", "Oui je veux arrêter"};
                int answer = -1;
                while (answer == -1) {
                    answer =
                        JOptionPane.showOptionDialog(null,
                            "Etes-vous sûr(e) de vouloir annuler l'export en cours ?",
                            "Arrêt de l'export", JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE, null, choix, null);
                }

                if (answer == 1) {
                    manager.getTaskWorker().interrupt();
                    manager.stop();
                    Toolkit.getDefaultToolkit().beep();
                }
                else {
                    progressMonitor =
                        new ProgressMonitor(ExportProgress.this,
                            "Export de la table "
                            + genericTable.getTable().getTableName() + " en cours...",
                            "", 0, manager.getLengthOfTask());
                    timer.start();
                }
            }
            else {
                progressMonitor.setNote(manager.getMessage());
                progressMonitor.setProgress(manager.getCurrent());
            }
        }
    }
}
