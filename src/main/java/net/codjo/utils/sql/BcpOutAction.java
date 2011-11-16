/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.utils.sql;
import net.codjo.gui.toolkit.fileChooser.FileChooserManager;
import net.codjo.gui.toolkit.util.ErrorDialog;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.apache.log4j.Logger;

/**
 * Action declanchant un "BCP out" de la table. Dès que l'action est initialisée, elle est disponible sur
 * toutes les DbToolBar crées.
 *
 * @author $Author: acharif $
 * @version $Revision: 1.5 $
 */
public class BcpOutAction extends AbstractAction {
    private static String user;
    private static String pwd;
    private static String server;
    private static String catalog;
    private String dbTableName;
    // Log
    private static final Logger APP = Logger.getLogger(BcpOutAction.class);


    /**
     * Constructeur
     *
     * @param dbTableName Nom physique de la table.
     */
    public BcpOutAction(String dbTableName) {
        this.dbTableName = dbTableName;
        putValue(NAME, "BCP");
        putValue(SHORT_DESCRIPTION, "Execute un BCP out de la table " + dbTableName);
    }


    /**
     * Accesseur user
     *
     * @return The User value
     */
    public String getUser() {
        return this.user;
    }


    /**
     * Accesseur pwd
     *
     * @return The Pwd value
     */
    public String getPwd() {
        return this.pwd;
    }


    /**
     * Accesseur server
     *
     * @return The Server value
     */
    public String getServer() {
        return this.server;
    }


    /**
     * Execute l'action.
     *
     * @param evt Event
     */
    public void actionPerformed(ActionEvent evt) {
        String fileName =
              FileChooserManager.showChooserForExport("..\\" + dbTableName + ".txt",
                                                      "Fichier du bcp out");
        if (fileName == null) {
            return;
        }

        String cmd =
              "bcp " + catalog + ".." + dbTableName + " out " + fileName + " -U" + user
              + " -P" + pwd + " -S" + server + " -c";
        execute(cmd);
    }


    /**
     * Execute le bcp out.
     *
     * @param cmd La commande de BCP out
     */
    private void execute(String cmd) {
        APP.debug("Commande de BCP out : " + cmd);
        try {
            Process proc = Runtime.getRuntime().exec(cmd);
            int r = proc.waitFor();
            if (r != 0) {
                APP.error("Le bcp out a echoue : " + dbTableName);
                ErrorDialog.show(null, "Le bcp out a echoue", "code erreur=" + r);
            }
        }
        catch (Exception ex) {
            ErrorDialog.show(null, "Le bcp out a echoue", ex);
        }
    }


    /**
     * Initialise l'action. Dès que l'action est initialisée, elle est disponible sur toutes les DbToolBar
     * crées.
     *
     * @param user    Utilisateur
     * @param pwd     Password
     * @param server  Nom du serveur
     * @param catalog Le catalogue
     */
    public static void initAction(String user, String pwd, String server, String catalog) {
        APP.debug("InitBCP");
        BcpOutAction.user = user;
        BcpOutAction.pwd = pwd;
        BcpOutAction.server = server;
        BcpOutAction.catalog = catalog;
    }


    /**
     * Indique si l'action est initialisé.
     *
     * @return 'true' si initialisé
     */
    static boolean isInited() {
        return user != null;
    }
}
