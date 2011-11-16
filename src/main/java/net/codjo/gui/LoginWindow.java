/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.gui;
import net.codjo.gui.toolkit.util.ErrorDialog;
import net.codjo.utils.ConnectionManager;
import net.codjo.utils.GuiUtil;
import net.codjo.utils.JukeBox;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import org.apache.log4j.Logger;

/**
 * Fenetre de Login générique.
 *
 * @version $Revision: 1.5 $
 */
public abstract class LoginWindow extends javax.swing.JFrame {
    protected JTextField loginField = new javax.swing.JTextField();
    protected JPasswordField passwordField = new javax.swing.JPasswordField();
    protected JComboBox serverCombo = new JComboBox();
    BorderLayout borderLayout1 = new BorderLayout();
    FlowLayout flowLayout1 = new FlowLayout();
    GridBagLayout gridBagLayout1 = new GridBagLayout();
    JLabel imageLabel = new JLabel();
    JPanel jPanel2 = new JPanel();
    private JPanel jPanel1 = new JPanel();
    private JLabel loginLabel = new javax.swing.JLabel();
    private JButton okButton = new javax.swing.JButton();
    private JLabel passwordLabel = new javax.swing.JLabel();
    private JButton quitButton = new javax.swing.JButton();
    private String[][] servers;
    private String applicationName;
    private String driver;
    private JukeBox jukeBox;

    // Log
    private static final Logger APP = Logger.getLogger(LoginWindow.class);


    /**
     * Constructor. Chaque ligne de <code>servers</code> contient une configuration de serveur. Chaque ligne
     * contient : index=0 - label, index=1 - url, index=2 - catalog.
     *
     * @param applicationName Nom de l'application
     * @param servers         Parametres de configuration des serveurs dispo.
     * @param driver          "com.sybase.jdbc2.jdbc.SybDriver"
     * @param jukeBox         Description of Parameter
     * @param icon            Description of Parameter
     */
    protected LoginWindow(String applicationName, String[][] servers, String driver,
                          JukeBox jukeBox, Icon icon) {
        this.servers = servers;
        this.applicationName = applicationName;
        this.driver = driver;
        this.jukeBox = jukeBox;
        imageLabel.setIcon(icon);
        jbInit();

        for (int i = 0; i < servers.length; i++) {
            serverCombo.addItem(servers[i][0]);
        }
        serverCombo.setSelectedIndex(0);
        setNameForGuiTest();
    }


    private void setNameForGuiTest() {
        loginField.setName("loginField");
        passwordField.setName("passwordField");
        serverCombo.setName("serverCombo");
    }


    /**
     * Constructor a partir d'une <code>ApplicationData</code>.
     *
     * @param jukeBox pour la zic
     */
    protected LoginWindow(ApplicationData application, JukeBox jukeBox) {
        String[][] serversTable = new String[application.getServers().length][3];
        for (int i = 0; i < application.getServers().length; i++) {
            serversTable[i][0] = application.getServers()[i].getName();
            serversTable[i][1] = application.getServers()[i].getUrl();
            serversTable[i][2] = application.getServers()[i].getCatalog();
        }

        this.servers = serversTable;
        this.applicationName = application.getName() + " v-" + application.getVersion();
        this.driver = application.getDriver();
        this.jukeBox = jukeBox;
        imageLabel.setIcon(application.getIcon());
        jbInit();

        for (int i = 0; i < servers.length; i++) {
            serverCombo.addItem(servers[i][0]);
        }
        serverCombo.setSelectedIndex(0);

        if (servers.length > 1) {
            loginField.setText(application.getDefaultLogin());
            passwordField.setText(application.getDefaultPassword());
        }
        setNameForGuiTest();
    }


    /**
     * Call-Back appelle lorsque la connection a put etre faite avec le serveur.
     *
     * @param cm Un ConnectionManager valide
     */
    public abstract void requestLogin(ConnectionManager cm);


    /**
     * Description of the Method
     */
    public abstract void requestQuit();


    /**
     * Description of the Method
     *
     * @param e Description of the Parameter
     */
    void passwordFieldFocusGained(FocusEvent e) {
        passwordField.selectAll();
    }


    private boolean isEnvironnement(String env) {
        String environnement = servers[serverCombo.getSelectedIndex()][0];
        return env.equalsIgnoreCase(environnement);
    }


    /**
     * Init Gui
     */
    private void jbInit() {
        this.getContentPane().setLayout(borderLayout1);
        jPanel1.setBorder(BorderFactory.createEtchedBorder());
        jPanel1.setLayout(gridBagLayout1);
        passwordLabel.setText("Mot de passe");
        loginLabel.setText("Compte");
        okButton.setText("OK");
        okButton.setActionCommand("OK");
        quitButton.setText("Quitter");
        quitButton.setActionCommand("Quitter");
        loginField.setNextFocusableComponent(passwordField);
        passwordField.setNextFocusableComponent(okButton);
        passwordField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(FocusEvent e) {
                passwordFieldFocusGained(e);
            }
        });
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        jPanel2.setLayout(flowLayout1);
        flowLayout1.setAlignment(FlowLayout.RIGHT);
        this.getContentPane().add(imageLabel, BorderLayout.CENTER);
        this.getContentPane().add(jPanel1, BorderLayout.SOUTH);
        jPanel1.add(loginField,
                    new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                                           GridBagConstraints.HORIZONTAL, new Insets(15, 20, 0, 20), 0, 0));
        jPanel1.add(loginLabel,
                    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                                           GridBagConstraints.NONE, new Insets(15, 20, 0, 0), 0, 0));
        jPanel1.add(passwordLabel,
                    new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                                           GridBagConstraints.NONE, new Insets(15, 20, 0, 0), 0, 0));
        jPanel1.add(passwordField,
                    new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                                           GridBagConstraints.HORIZONTAL, new Insets(15, 20, 0, 20), 0, 0));
        jPanel1.add(serverCombo,
                    new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                                           GridBagConstraints.HORIZONTAL, new Insets(15, 20, 0, 20), 0, 0));
        jPanel1.add(jPanel2,
                    new GridBagConstraints(0, 3, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER,
                                           GridBagConstraints.HORIZONTAL, new Insets(10, 0, 5, 15), 0, 0));
        jPanel2.add(okButton, null);
        jPanel2.add(quitButton, null);

        setTitle("Connexion à " + applicationName);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        pack();
        GuiUtil.centerWindow(this);
        quitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent event) {
                quitButtonActionPerformed(event);
            }
        });
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent event) {
                okButtonActionPerformed(event);
            }
        });

        okButton.setMnemonic(KeyEvent.VK_O);
        quitButton.setMnemonic(KeyEvent.VK_Q);
        getRootPane().setDefaultButton(okButton);
    }


    /**
     * Construction d'un ConnectionManager a partir des info de l'IHM
     *
     * @return un ConnectionManager
     *
     * @throws ClassNotFoundException Driver inconnue
     */
    private ConnectionManager newConnectionManager()
          throws ClassNotFoundException {
        Properties props = new Properties();
        props.put("USER", loginField.getText());
        props.put("PASSWORD", new String(passwordField.getPassword()));
        props.put("HOSTNAME", System.getProperty("user.name"));
        props.put("APPLICATIONNAME", applicationName);

        int idx = serverCombo.getSelectedIndex();
        return new ConnectionManager(driver, servers[idx][1], servers[idx][2], props);
    }


    /**
     * DOCUMENT ME!
     *
     * @noinspection UNUSED_SYMBOL
     */
    private void okButtonActionPerformed(ActionEvent event) {
        okButton.setEnabled(false);
        try {
            ConnectionManager cm = newConnectionManager();

            testNbConnections(cm);

            cm.releaseConnection(cm.getConnection());
            jukeBox.playSuccessSound();
            requestLogin(cm);
        }
        catch (java.lang.Exception ex) {
            passwordField.setText("");
            repaint();
            jukeBox.playFailureSound();
            APP.error(ex);
            ErrorDialog.show(this, "Erreur de Login", "La connexion a échoué");
            okButton.setEnabled(true);
        }
    }


    private void testNbConnections(ConnectionManager cm) {
        Connection con = null;
        CallableStatement cstmt = null;
        String userName = loginField.getText();
        String appName =
              applicationName.substring(0, Math.min(applicationName.length(), 15)) + "%";
        String catalog = servers[serverCombo.getSelectedIndex()][2];
        int nbConnections = 0;
//        APP.debug("userName >" + userName);
//        APP.debug("appName >" + appName);
//        APP.debug("catalog >" + catalog);
        try {
            con = cm.getConnection();
            cstmt =
                  con.prepareCall(
                        "{call sp_COMMON_Nb_Connexion @USER_NAME=?, @APPLICATION_NAME=?, @CATALOG_NAME=?}");
            cstmt.setString(1, userName);
            cstmt.setString(2, appName);
            cstmt.setString(3, catalog);

            ResultSet rs = cstmt.executeQuery();
            if (rs.next()) {
                nbConnections = rs.getInt(1);
//                APP.debug("NB_CONNECTIONS >" + nbConnections + "<");
            }

            if ((isEnvironnement("production") || isEnvironnement("recette"))
                && nbConnections > 1) {
                ErrorDialog.show(this, "Utilisateur déjà connecté",
                                 "L'identifiant " + userName + " est déjà connecté à l'application "
                                 + applicationName + ".\nImpossible de continuer.");
                System.exit(-1);
            }
        }
        catch (Exception ex) {
            ErrorDialog.show(this, "Erreur", ex);
            System.exit(-1);
        }
        finally {
            try {
                cm.releaseConnection(con, cstmt);
            }
            catch (SQLException ex) {
                APP.error(ex);
            }
        }
    }


    /**
     * DOCUMENT ME!
     *
     * @noinspection UNUSED_SYMBOL
     */
    private void quitButtonActionPerformed(ActionEvent event) {
        requestQuit();
    }
}
