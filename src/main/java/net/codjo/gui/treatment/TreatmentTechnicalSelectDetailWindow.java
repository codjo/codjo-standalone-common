/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.gui.treatment;
import net.codjo.utils.IntegerField;
import net.codjo.utils.sql.AbstractDetailWindow;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
/**
 * Detail de saisi du critère libre
 *
 * @author $Author: marcona $
 * @version $Revision: 1.5 $
 */
public class TreatmentTechnicalSelectDetailWindow extends AbstractDetailWindow {
    /** Description of the Field */
    public JTextField TREATMENT_UNIT_SETTINGS_ID = new IntegerField();
    /** Description of the Field */
    public JTextField FREE_CRITERIA = new JTextField();
    /** Description of the Field */
    public JButton cancelButton = new JButton();
    /** Description of the Field */
    public JButton okButton = new JButton();
    JPanel bottomPanel = new JPanel();
    FlowLayout flowLayout1 = new FlowLayout(FlowLayout.RIGHT);
    JPanel topPanel2 = new JPanel();
    JLabel lotLabel = new JLabel();
    JPanel jPanel1 = new JPanel();
    JLabel freeCriteriaLabel = new JLabel();
    GridBagLayout gridBagLayout2 = new GridBagLayout();
    GridBagLayout gridBagLayout3 = new GridBagLayout();
    GridBagLayout gridBagLayout1 = new GridBagLayout();

    /**
     * Constructor for the TreatmentTechnicalSelectDetailWindow object
     */
    public TreatmentTechnicalSelectDetailWindow() {
        super("Sélection de la source");
        try {
            jbInit();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Overview.
     *
     * <p>
     * Description
     * </p>
     *
     * @exception Exception Description of Exception
     */
    private void jbInit() throws Exception {
        bottomPanel.setLayout(flowLayout1);
        this.getContentPane().setLayout(gridBagLayout3);
        cancelButton.setText("Annuler");
        okButton.setText("Valider");
        topPanel2.setLayout(gridBagLayout1);
        TREATMENT_UNIT_SETTINGS_ID.setEnabled(false);
        TREATMENT_UNIT_SETTINGS_ID.setBorder(BorderFactory.createEtchedBorder());
        lotLabel.setText("Lot N°");
        jPanel1.setLayout(gridBagLayout2);
        freeCriteriaLabel.setToolTipText("");
        freeCriteriaLabel.setText("Critère libre");
        this.getContentPane().add(topPanel2,
            new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), -147, 0));
        topPanel2.add(lotLabel,
            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, new Insets(5, 10, 10, 0), 0, 0));
        topPanel2.add(TREATMENT_UNIT_SETTINGS_ID,
            new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, new Insets(5, 10, 10, 276), -46, 0));
        this.getContentPane().add(jPanel1,
            new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        jPanel1.add(freeCriteriaLabel,
            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, new Insets(0, 9, 0, 0), 9, 4));
        jPanel1.add(FREE_CRITERIA,
            new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, new Insets(0, 6, 0, 7), 295, 0));
        this.getContentPane().add(bottomPanel,
            new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 200, 0));
        bottomPanel.add(okButton, null);
        bottomPanel.add(cancelButton, null);
    }
}
