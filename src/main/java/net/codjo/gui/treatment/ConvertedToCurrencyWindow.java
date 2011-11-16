/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.gui.treatment;
import net.codjo.gui.model.PeriodComboBox;
import net.codjo.gui.operation.WaitingWindowManager;
import net.codjo.gui.toolkit.util.ErrorDialog;
import net.codjo.model.Period;
import net.codjo.model.PeriodHome;
import net.codjo.utils.ConnectionManager;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.UIManager;
import javax.swing.border.Border;
/**
 * Fenêtre de lancement de la contre-valorisation
 *
 * @version $Revision: 1.4 $
 */
public class ConvertedToCurrencyWindow extends JInternalFrame {
    //Synchronisation de la comboBox avec celle de la Toolbar de PenelopeWindow
    JComboBox periodComboBox = new PeriodComboBox();
    private JLabel jLabel1 = new JLabel();
    private JButton launchButton = new JButton();
    private JButton quitButton = new JButton();
    private JRadioButton radioButtonStock = new JRadioButton();
    private JRadioButton radioButtonTransaction = new JRadioButton();
    private JRadioButton radioButtonBoth = new JRadioButton();
    private ButtonGroup buttonGroupe = new ButtonGroup();
    private ConnectionManager connectionManager;
    private JDesktopPane gexPane;
    private WaitingWindowManager waitingWindowManager;
    JPanel paneltop = new JPanel();
    Border border1;
    JPanel jPanel2 = new JPanel();
    Border border2;
    FlowLayout flowLayout1 = new FlowLayout();
    GridBagLayout gridBagLayout1 = new GridBagLayout();
    BorderLayout borderLayout1 = new BorderLayout();


    /**
     * Constructeur de ConvertedToCurrencyWindow
     *
     * @param dp         Le desktopPane
     * @param conMan     Le connectionManager
     * @param periodHome Le periodHome
     *
     * @throws IllegalArgumentException TODO
     */
    public ConvertedToCurrencyWindow(JDesktopPane dp, ConnectionManager conMan,
                                     PeriodHome periodHome) {
        super("Contre-valorisation", false, true, false, false);
        if ((conMan == null) || (periodHome == null) || (dp == null)) {
            throw new IllegalArgumentException();
        }
        gexPane = dp;
        connectionManager = conMan;

        try {
            jbInit();
            pack();
            //init de la Combo
            ((PeriodComboBox)periodComboBox).setPeriodHome(periodHome);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /**
     * Constructeur2 For Paris
     *
     * @param dp     Le desktopPane
     * @param conMan Le connectionManager
     *
     * @throws IllegalArgumentException TODO
     */
    public ConvertedToCurrencyWindow(JDesktopPane dp, ConnectionManager conMan) {
        super("Contre-valorisation", false, true, false, false);
        if ((conMan == null) || (dp == null)) {
            throw new IllegalArgumentException();
        }

        gexPane = dp;
        connectionManager = conMan;
        try {
            jbInit();
            pack();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /**
     * Retourne la connectionManager pour l'objet ConvertedToCurrencyForParisWindow
     *
     * @return The connectionManager value
     */
    protected ConnectionManager getConnectionManager() {
        return connectionManager;
    }


    /**
     * Description of the Method
     *
     * @param newComboBox Description of the Parameter
     */
    protected void assignNewComboBox(JComboBox newComboBox) {
        this.paneltop.remove(periodComboBox);
        this.periodComboBox = newComboBox;
        this.paneltop.add(periodComboBox,
                          new GridBagConstraints(1, 0, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER,
                                                 GridBagConstraints.HORIZONTAL,
                                                 new Insets(16, 10, 10, 60),
                                                 28,
                                                 2));
    }


    /**
     * Ferme la fenêtre
     */
    protected void quitButton_actionPerformed() {
        setVisible(false);
        dispose();
    }


    /**
     * Lancement de la contre-valorisation
     */
    protected void launchButton_actionPerformed() {
        waitingWindowManager =
              new WaitingWindowManager(gexPane, "Contre-valorisation en cours...", 1);
        waitingWindowManager.showWaitingWindow();

        final javax.swing.Timer timer =
              new javax.swing.Timer(100,
                                    new ActionListener() {
                                        public void actionPerformed(ActionEvent evt) {
                                            doConvertData();
                                        }
                                    });
        timer.setRepeats(false);
        timer.start();
    }


    /**
     * Retourne la période (string) de l'objet Period, sélectionné dans la combo
     *
     * @return Description of the Returned Value
     */
    protected String determinePeriod() {
        return ((Period)periodComboBox.getSelectedItem()).getPeriod();
    }


    /**
     * Contre-valorise les données.
     */
    private void doConvertData() {
        Connection connection = null;
        CallableStatement stmt = null;
        String period = determinePeriod();
        try {
            connection = connectionManager.getConnection();
            if ((radioButtonStock.isSelected()) || (radioButtonBoth.isSelected())) {
                stmt =
                      connection.prepareCall("{call sp_INF_Stock_Eur @current_period=?}");
                try {
                    stmt.setString(1, period);
                    stmt.executeUpdate();
                }
                catch (SQLException sqle1) {
                    sqle1.printStackTrace();
                    ErrorDialog.show(this,
                                     "Erreur pendant la contre-valorisation des stocks",
                                     sqle1.getLocalizedMessage());
                }
            }

            if ((radioButtonTransaction.isSelected()) || (radioButtonBoth.isSelected())) {
                stmt =
                      connection.prepareCall(
                            "{call sp_INF_Transaction_Eur @current_period=?}");
                try {
                    stmt.setString(1, period);
                    stmt.executeUpdate();
                }
                catch (SQLException sqle2) {
                    sqle2.printStackTrace();
                    ErrorDialog.show(this,
                                     "Erreur pendant la contre-valorisation des mouvements",
                                     sqle2.getLocalizedMessage());
                }
            }
        }
        catch (SQLException sqle3) {
            sqle3.printStackTrace();
        }
        finally {
            try {
                waitingWindowManager.disposeWaitingWindow();
                try {
                    this.setSelected(true);
                }
                catch (java.beans.PropertyVetoException g) {
                }
                connectionManager.releaseConnection(connection, stmt);
            }
            catch (SQLException sql4) {
                sql4.printStackTrace();
            }
        }
    }


    /**
     * Description of the Method
     *
     * @throws Exception -
     */
    private void jbInit() throws Exception {
        border1 = BorderFactory.createEtchedBorder(Color.white, new Color(134, 134, 134));
        border2 = BorderFactory.createEtchedBorder(Color.white, new Color(134, 134, 134));
        jLabel1.setText("Période");
        this.getContentPane().setLayout(borderLayout1);
        this.getContentPane().setBackground(UIManager.getColor("Panel.background"));
        launchButton.setText("Lancer");
        launchButton.addActionListener(new java.awt.event.ActionListener() {
            /**
             * DOCUMENT ME!
             *
             * @param evt Description of Parameter
             */
            public void actionPerformed(ActionEvent evt) {
                launchButton_actionPerformed();
            }
        });
        quitButton.setText("Quitter");
        quitButton.addActionListener(new java.awt.event.ActionListener() {
            /**
             * DOCUMENT ME!
             *
             * @param evt Description of Parameter
             */
            public void actionPerformed(ActionEvent evt) {
                quitButton_actionPerformed();
            }
        });
        radioButtonStock.setText("Stock");
        radioButtonTransaction.setText("Mouvement");
        radioButtonBoth.setSelected(true);
        radioButtonBoth.setText("Stock & Mouvement");
        paneltop.setBorder(border1);
        paneltop.setLayout(gridBagLayout1);
        jPanel2.setBorder(border2);
        jPanel2.setLayout(flowLayout1);
        this.getContentPane().add(paneltop, BorderLayout.CENTER);
        paneltop.add(jLabel1,
                     new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                                            GridBagConstraints.NONE, new Insets(10, 20, 10, 0), 11, 0));
        paneltop.add(radioButtonStock,
                     new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                            GridBagConstraints.HORIZONTAL, new Insets(10, 40, 5, 20), 0, 0));
        paneltop.add(radioButtonBoth,
                     new GridBagConstraints(0, 3, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                            GridBagConstraints.HORIZONTAL, new Insets(10, 40, 30, 20), 0, 0));
        paneltop.add(radioButtonTransaction,
                     new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                            GridBagConstraints.HORIZONTAL, new Insets(10, 40, 5, 20), 0, 0));
        paneltop.add(periodComboBox,
                     new GridBagConstraints(1, 0, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER,
                                            GridBagConstraints.HORIZONTAL,
                                            new Insets(16, 10, 10, 60),
                                            28,
                                            2));

        this.getContentPane().add(jPanel2, BorderLayout.SOUTH);
        jPanel2.add(launchButton, null);
        jPanel2.add(quitButton, null);
        buttonGroupe.add(radioButtonStock);
        buttonGroupe.add(radioButtonTransaction);
        buttonGroupe.add(radioButtonBoth);
    }
}
