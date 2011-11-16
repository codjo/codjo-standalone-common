/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.gui.model;
import net.codjo.model.PeriodHome;
import net.codjo.utils.GuiUtil;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
/**
 * Fenetre permettant de selectionner la periode courante. Cette ihm est utilisee comme
 * fenetre d'acceuil.
 *
 * @author $Author: blazart $
 * @version $Revision: 1.2 $
 *
 *
 */
public class PeriodSelectWindow extends javax.swing.JFrame {
    JPanel periodsPanel = new JPanel();
    JPanel buttonsPanel = new JPanel();
    JLabel penelopeLabel = new javax.swing.JLabel("Penelope");
    TitledBorder periodTitle = new TitledBorder("Période de travail");
    PeriodComboBox periodComboBox = new PeriodComboBox();
    PeriodHome periodHome;
    JButton okButton = new JButton("OK");
    BorderLayout borderLayout1 = new BorderLayout();

    /**
     * Constructor for the WelcomeWindow object
     *
     * @param ph Description of Parameter
     * @param Title Description of Parameter
     *
     * @exception java.sql.SQLException Description of Exception
     */
    public PeriodSelectWindow(PeriodHome ph, String Title)
            throws java.sql.SQLException {
        jbInit();

        SymAction lSymAction = new SymAction();
        okButton.addActionListener(lSymAction);

        penelopeLabel.setText(Title);
        periodHome = ph;
        periodComboBox.setPeriodHome(ph);
        periodComboBox.setSelectedIndex(periodComboBox.getItemCount() - 1);
    }

    /**
     * Action lors d'un click sur OK.
     *
     * @param event -
     */
    void OkButton_actionPerformed(java.awt.event.ActionEvent event) {
        dispose();
    }


    /**
     * Init GUI
     *
     * @exception java.sql.SQLException -
     */
    private void jbInit() throws java.sql.SQLException {
        this.getContentPane().setLayout(borderLayout1);
        setSize(340, 240);
        setTitle("Accueil");
        setDefaultCloseOperation(javax.swing.JFrame.DO_NOTHING_ON_CLOSE);

        periodsPanel.setBorder(periodTitle);
        periodsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        periodsPanel.add(periodComboBox);

        penelopeLabel.setFont(new Font("Dialog", Font.BOLD, 21));

        buttonsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        okButton.setActionCommand("OK");
        okButton.setSelected(true);
        okButton.setMnemonic((int)'O');
        buttonsPanel.add(okButton);

        this.getContentPane().add(penelopeLabel, BorderLayout.NORTH);
        this.getContentPane().add(periodsPanel, BorderLayout.CENTER);
        this.getContentPane().add(buttonsPanel, BorderLayout.SOUTH);
        GuiUtil.centerWindow(this);
    }


    public PeriodComboBox getPeriodComboBox() {
        return periodComboBox;
    }

    /**
     * Listener
     *
     * @author $Author: blazart $
     * @version $Revision: 1.2 $
     */
    class SymAction implements java.awt.event.ActionListener {
        /**
         * Overview.
         *
         * @param event Description of Parameter
         */
        public void actionPerformed(java.awt.event.ActionEvent event) {
            Object object = event.getSource();
            if (object == okButton) {
                OkButton_actionPerformed(event);
            }
        }
    }
}
