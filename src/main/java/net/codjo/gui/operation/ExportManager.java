/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.gui.operation;
import net.codjo.gui.toolkit.swing.SwingWorker;
import net.codjo.gui.toolkit.util.ErrorDialog;
import net.codjo.operation.OperationInterruptedException;
import net.codjo.utils.sql.DataFormater;
import net.codjo.utils.sql.GenericTable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
/**
 * Classe gérant l'export et sa progression.
 *
 * @author $Author: blazart $
 * @version $Revision: 1.3 $
 */
public class ExportManager {
    private int lengthOfTask;
    private int current = 0;
    private GenericTable genericTable;
    private String filename;
    private SwingWorker taskWorker;
    private DataFormater dataFormater;
    private boolean doReloadAtEnd;


    /**
     * Constructeur.
     *
     * @param g        La GenericTable.
     * @param filename Le nom du fichier d'export.
     * @param doReload TODO
     *
     * @throws IllegalArgumentException TODO
     */
    ExportManager(GenericTable g, String filename, boolean doReload) {
        if (g == null || filename == null) {
            throw new IllegalArgumentException();
        }
        genericTable = g;
        this.filename = filename;
        doReloadAtEnd = doReload;
        lengthOfTask = genericTable.getNumberOfRows();
    }


    /**
     * Retourne le SwingWorker.
     *
     * @return Le SwingWorker.
     */
    SwingWorker getTaskWorker() {
        return taskWorker;
    }


    /**
     * Retourne le nombre de lignes à exporter.
     *
     * @return Le nombre de lignes à exporter.
     */
    int getLengthOfTask() {
        return lengthOfTask;
    }


    /**
     * Retourne le numéro de la ligne exportée.
     *
     * @return Le numéro de la ligne exportée.
     */
    int getCurrent() {
        return dataFormater.getCurrentLine();
    }


    /**
     * Retourne le message à afficher sur le ProgressMonitor.
     *
     * @return Le message à afficher sur le ProgressMonitor.
     */
    String getMessage() {
        return "Enregistrement " + getCurrent() + " / " + getLengthOfTask() + ".";
    }


    /**
     * Lance l'export.
     *
     * @param endOperationListener Runner dont la méthode run sera appelée en fin de tâche (dans le thread
     *                             Event).
     */
    void go(Runnable endOperationListener) {
        current = 0;
        try {
            File outputFile = new File(filename);
            FileWriter out = new FileWriter(outputFile);
            dataFormater = new DataFormater(genericTable);
            taskWorker =
                  new TaskWorker(out, dataFormater, endOperationListener,
                                 doReloadAtEnd);
            taskWorker.start();
        }
        catch (IOException ex) {
            ex.printStackTrace();
            ErrorDialog.show(genericTable, "L'export a échoué !", ex);
        }
    }


    /**
     * Arrête le ProgressMonitor.
     */
    void stop() {
        current = lengthOfTask;
    }


    /**
     * Indique si l'export est terminé.
     *
     * @return true si terminé false sinon.
     */
    boolean done() {
        return current >= lengthOfTask;
    }


    /**
     * Execute l'export dans un thread de type SwingWorker.
     *
     * @author $Author: blazart $
     * @version $Revision: 1.3 $
     */
    private class TaskWorker extends SwingWorker {
        private FileWriter out;
        private DataFormater dataFormater;
        private Runnable endOperationListener;
        private boolean doReloadAtEnd;


        /**
         * Constructeur.
         *
         * @param out                  Le FileWriter utilisé pour l'écriture.
         * @param dataFormater         L'objet qui gère l'écriture.
         * @param endOperationListener Runner dont la méthode run sera appelée en fin de tâche (dans le thread
         *                             Event).
         * @param doReload             TODO
         */
        private TaskWorker(FileWriter out,
                           DataFormater dataFormater,
                           Runnable endOperationListener,
                           boolean doReload) {
            this.out = out;
            this.dataFormater = dataFormater;
            this.endOperationListener = endOperationListener;
            doReloadAtEnd = doReload;
        }


        /**
         * Execute l'export.
         *
         * @return -
         */
        @Override
        public Object construct() {
            try {
                dataFormater.buildDataForExport(out);
                out.close();
            }
            catch (IOException ex) {
                ex.printStackTrace();
                ErrorDialog.show(genericTable, "L'export a échoué !", ex);
            }
            catch (OperationInterruptedException ex) {
                ex.printStackTrace();
                ErrorDialog.show(genericTable, "L'export a échoué !", ex);
            }
            return null;
        }


        /**
         * Previent l'IHM et recharge les données (on revient sur la 1ère page).
         */
        @Override
        public void finished() {
            if (doReloadAtEnd) {
                endOperationListener.run();
            }
        }
    }
}
