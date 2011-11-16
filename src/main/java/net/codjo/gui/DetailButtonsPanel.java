/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.gui;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
/**
 * Panel des boutons Précédent-Suivant-Appliquer-Valider-Annuler pour les écrans détail
 *
 * @version $Revision: 1.5 $
 */
public class DetailButtonsPanel extends JPanel {
    private JButton previousButton = new JButton();
    private JButton nextButton = new JButton();

//    private JButton applyButton = new JButton();
    private JButton okButton = new JButton();
    private JButton cancelButton = new JButton();
    private JPanel leftPanel = new JPanel();
    private JPanel rightPanel = new JPanel();
    private BorderLayout borderLayout1 = new BorderLayout();
    private FlowLayout flowLayout1 = new FlowLayout();
    private FlowLayout flowLayout2 = new FlowLayout();

    /**
     * Constructeur
     */
    public DetailButtonsPanel() {
        jbInit();
    }

    /**
     * Retourne le bouton OK
     *
     * @return Le bouton
     */
    public JButton getOkButton() {
        return okButton;
    }


    /**
     * Retourne le bouton Annuler
     *
     * @return Le bouton
     */
    public JButton getCancelButton() {
        return cancelButton;
    }


    /**
     * Retourne le bouton Appliquer
     *
     * @return Le bouton
     */

//    public JButton getApplyButton() {
//        return applyButton;
//    }
    /**
     * Retourne le bouton Précédent
     *
     * @return Le bouton
     */
    public JButton getPreviousButton() {
        return previousButton;
    }


    /**
     * Retourne le bouton Suivant
     *
     * @return Le bouton
     */
    public JButton getNextButton() {
        return nextButton;
    }


    /**
     * Description of the Method
     */
    private void jbInit() {
        this.setBorder(BorderFactory.createEtchedBorder());
        this.setPreferredSize(new Dimension(300, 40));
        this.setLayout(borderLayout1);
        leftPanel.setMaximumSize(new Dimension(250, 41));
        leftPanel.setPreferredSize(new Dimension(153, 41));
        leftPanel.setLayout(flowLayout1);
        rightPanel.setMaximumSize(new Dimension(250, 41));
        rightPanel.setPreferredSize(new Dimension(153, 41));
        rightPanel.setLayout(flowLayout2);
        previousButton.setMaximumSize(new Dimension(67, 27));
        previousButton.setMinimumSize(new Dimension(67, 27));
        previousButton.setPreferredSize(new Dimension(67, 27));
        previousButton.setMargin(new Insets(2, 2, 2, 2));
        previousButton.setText("Précédent");
        nextButton.setMaximumSize(new Dimension(67, 27));
        nextButton.setMinimumSize(new Dimension(67, 27));
        nextButton.setPreferredSize(new Dimension(67, 27));
        nextButton.setMargin(new Insets(2, 2, 2, 2));
        nextButton.setText("Suivant");
        okButton.setMaximumSize(new Dimension(67, 27));
        okButton.setMinimumSize(new Dimension(67, 27));
        okButton.setPreferredSize(new Dimension(67, 27));
        okButton.setMargin(new Insets(2, 2, 2, 2));
        okButton.setText("Valider");
        cancelButton.setMaximumSize(new Dimension(67, 27));
        cancelButton.setMinimumSize(new Dimension(67, 27));
        cancelButton.setPreferredSize(new Dimension(67, 27));
        cancelButton.setMargin(new Insets(2, 2, 2, 2));
        cancelButton.setText("Annuler");
        borderLayout1.setHgap(10);
        borderLayout1.setVgap(10);
        flowLayout1.setAlignment(FlowLayout.LEFT);
        flowLayout2.setAlignment(FlowLayout.RIGHT);
        this.add(leftPanel, BorderLayout.WEST);
        leftPanel.add(previousButton, null);
        leftPanel.add(nextButton, null);
        this.add(rightPanel, BorderLayout.EAST);
        rightPanel.add(okButton, null);
        rightPanel.add(cancelButton, null);
    }
}
