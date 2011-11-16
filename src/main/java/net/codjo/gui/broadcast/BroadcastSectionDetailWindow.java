/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.gui.broadcast;
import net.codjo.gui.DetailButtonsPanel;
import net.codjo.gui.toolkit.number.NumberField;
import net.codjo.gui.toolkit.util.ErrorDialog;
import net.codjo.utils.ConnectionManager;
import net.codjo.utils.SQLFieldList;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import org.apache.log4j.Logger;
/**
 * TODO.
 *
 * @version $Revision: 1.2 $
 */
public class BroadcastSectionDetailWindow extends net.codjo.utils.sql.AbstractDetailWindow {
    private static final Logger LOG =
          Logger.getLogger(BroadcastSectionDetailWindow.class);
    /**
     * CONNECTION_MANAGER initialise par putDefault...
     */
    public ConnectionManager CONNECTION_MANAGER;
    public JTextField DECIMAL_SEPARATOR = new JTextField();
    public JComboBox FAMILY = new JComboBox();
    public JTextField FILE_ID = new JTextField();
    public JCheckBox FIXED_LENGTH = new JCheckBox();
    /**
     * Le manager des preferences de diffusion, initialise par putDefault...
     */
    public GuiPreferencesManager GUI_PREFERENCES_MANAGER;
    public NumberField RECORD_LENGTH = new NumberField();
    public JTextField SECTION_ID = new JTextField();
    public JTextField SECTION_NAME = new JTextField();
    public JComboBox SELECTION_ID = new JComboBox();
    public DetailButtonsPanel detailButtonsPanel = new DetailButtonsPanel();
    public String fileTableName;
    JPanel columnPanel = new JPanel();
    JLabel decimalSeparatorLabel = new JLabel();
    JLabel familyLabel = new JLabel();
    GridBagLayout gridBagLayout1 = new GridBagLayout();
    GridBagLayout gridBagLayout2 = new GridBagLayout();
    GridBagLayout gridBagLayout3 = new GridBagLayout();
    GridBagLayout gridBagLayout4 = new GridBagLayout();
    JLabel lengthLabel = new JLabel();
    JPanel mainPanel = new JPanel();
    JLabel sectionNameLabel = new JLabel();
    JPanel sectionPanel = new JPanel();
    JLabel selectionNameLabel = new JLabel();
    JTabbedPane tabbedPane = new JTabbedPane();
    private GuiPreferences guiPrefs = null;
    private JPanel optionPanel = null;


    public BroadcastSectionDetailWindow() throws Exception {
        jbInitGui();
    }


    @Override
    public void fillComponent(SQLFieldList columns, ResultSet rs)
          throws SQLException {
        super.fillComponent(columns, rs);
        FAMILY.setEnabled(false);
        buildOptionPanel(rs.getInt("SECTION_ID"));
    }


    @Override
    public void fillDefaultValues(HashMap defaultValues) {
        super.fillDefaultValues(defaultValues);
        initFamily();
        initGuiPref();
        fillSelection();
        initGuiFieldsProperties();

        try {
            buildOptionPanel(-1);
        }
        catch (SQLException e) {
            LOG.error(e);
            ErrorDialog.show(this, "Erreur pendant la création du option panel", e);
        }
    }


    @Override
    public void saveLinks(Map pk, Connection con)
          throws java.lang.Exception {
        super.saveLinks(pk, con);

        if (optionPanel != null) {
            GuiPreferences guiPref =
                  GUI_PREFERENCES_MANAGER.getGuiPreferences(FAMILY.getSelectedItem()
                        .toString());
            guiPref.saveSectionOptionPanel(pk, con, optionPanel);
        }
    }


    private void familyActionPerformed() {
        initGuiPref();
        fillSelection();
    }


    private void buildOptionPanel(int sectionId) throws SQLException {
        Connection con = CONNECTION_MANAGER.getConnection();

        try {
            GuiPreferences guiPref =
                  GUI_PREFERENCES_MANAGER.getGuiPreferences(FAMILY.getSelectedItem()
                        .toString());

            if (optionPanel != null) {
                tabbedPane.remove(optionPanel);
            }

            optionPanel = guiPref.buildSectionOptionPanel(con, sectionId);

            if (optionPanel != null) {
                String optionTitle = optionPanel.getName();

                if (optionTitle == null) {
                    optionTitle = "Options";
                }

                tabbedPane.add(optionPanel, optionTitle);
            }
        }
        finally {
            CONNECTION_MANAGER.releaseConnection(con);
        }
    }


    private void fillFamily() {
        for (String s : GUI_PREFERENCES_MANAGER.getAllGuiPreferences().keySet()) {
            FAMILY.addItem(s);
        }
    }


    private void fillSelection() {
        try {
            initSelectionComboBox();
        }
        catch (Exception ex) {
            LOG.error(ex);
            ErrorDialog.show(this, "Erreur pendant le chargement des selections", ex);
        }
    }


    private void initFamily() {
        fillFamily();
        FAMILY.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                familyActionPerformed();
            }
        });
    }


    private void initGuiFieldsProperties() {
        GUI_PREFERENCES_MANAGER.setProperties(sectionNameLabel, SECTION_NAME,
                                              GuiConstants.SECTION_SECTION_NAME);
        GUI_PREFERENCES_MANAGER.setProperties(familyLabel, FAMILY,
                                              GuiConstants.SECTION_FAMILY);
        GUI_PREFERENCES_MANAGER.setProperties(selectionNameLabel, SELECTION_ID,
                                              GuiConstants.SECTION_SELECTION_ID);
        GUI_PREFERENCES_MANAGER.setProperties(FIXED_LENGTH, FIXED_LENGTH,
                                              GuiConstants.SECTION_FIXED_LENGTH);
        GUI_PREFERENCES_MANAGER.setProperties(lengthLabel, null,
                                              GuiConstants.SECTION_RECORD_LENGTH);
        GUI_PREFERENCES_MANAGER.setProperties(decimalSeparatorLabel, DECIMAL_SEPARATOR,
                                              GuiConstants.SECTION_DECIMAL_SEPARATOR);
    }


    private void initGuiPref() {
        this.guiPrefs =
              GUI_PREFERENCES_MANAGER.getGuiPreferences(FAMILY.getSelectedItem().toString());
    }


    private void initSelectionComboBox() throws Exception {
        Connection con = CONNECTION_MANAGER.getConnection();

        try {
            JComboBox combo = guiPrefs.buildSelectionComboBox(con);
            SELECTION_ID.setModel(combo.getModel());
            SELECTION_ID.setRenderer(combo.getRenderer());
        }
        finally {
            CONNECTION_MANAGER.releaseConnection(con);
        }
    }


    private void jbInitGui() throws Exception {
        this.setResizable(true);
        this.setTitle("Détail de la table");
        this.getContentPane().setBackground(UIManager.getColor("Panel.background"));
        this.setPreferredSize(new Dimension(400, 320));
        this.getContentPane().setLayout(gridBagLayout4);
        columnPanel.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(
              Color.white,
              new Color(142, 142, 142)), "Colonnes"));
        columnPanel.setLayout(gridBagLayout1);
        mainPanel.setLayout(gridBagLayout3);
        RECORD_LENGTH.setBackground(UIManager.getColor("Panel.background"));
        RECORD_LENGTH.setEnabled(false);
        RECORD_LENGTH.setColumns(0);
        FIXED_LENGTH.setText("Longueur fixe");
        FIXED_LENGTH.setHorizontalTextPosition(SwingConstants.LEFT);
        FIXED_LENGTH.addItemListener(new ActionListenerForFixedLength());
        sectionPanel.setLayout(gridBagLayout2);
        decimalSeparatorLabel.setText("Séparateur décimal");
        lengthLabel.setText("Longueur");
        selectionNameLabel.setText("Sélection");
        familyLabel.setText("Famille");
        sectionNameLabel.setText("Nom");
        DECIMAL_SEPARATOR.setColumns(2);
        sectionPanel.add(sectionNameLabel,
                         new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                                                GridBagConstraints.NONE, new Insets(0, 5, 0, 0), 8, 0));
        sectionPanel.add(SECTION_NAME,
                         new GridBagConstraints(1, 0, 3, 1, 1.0, 0.0, GridBagConstraints.WEST,
                                                GridBagConstraints.HORIZONTAL,
                                                new Insets(0, 5, 0, 5),
                                                310,
                                                0));
        sectionPanel.add(SELECTION_ID,
                         new GridBagConstraints(1, 2, 3, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
                                                GridBagConstraints.HORIZONTAL,
                                                new Insets(10, 5, 0, 5),
                                                0,
                                                0));
        sectionPanel.add(selectionNameLabel,
                         new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                                                GridBagConstraints.NONE, new Insets(10, 5, 0, 0), 0, 0));
        sectionPanel.add(FIXED_LENGTH,
                         new GridBagConstraints(0, 3, 2, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
                                                GridBagConstraints.NONE, new Insets(6, 5, 5, 0), 0, 0));
        sectionPanel.add(lengthLabel,
                         new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
                                                GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
        sectionPanel.add(RECORD_LENGTH,
                         new GridBagConstraints(3, 3, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
                                                GridBagConstraints.NONE, new Insets(10, 5, 0, 0), 30, 0));
        sectionPanel.add(FAMILY,
                         new GridBagConstraints(1, 1, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
                                                GridBagConstraints.HORIZONTAL,
                                                new Insets(10, 5, 0, 5),
                                                0,
                                                0));
        sectionPanel.add(familyLabel,
                         new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                                                GridBagConstraints.NONE, new Insets(10, 5, 0, 0), 0, 0));
        mainPanel.add(columnPanel,
                      new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.NORTH,
                                             GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        mainPanel.add(sectionPanel,
                      new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                             GridBagConstraints.HORIZONTAL, new Insets(15, 0, 0, 0), 0, 0));
        columnPanel.add(decimalSeparatorLabel,
                        new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
                                               GridBagConstraints.NONE, new Insets(0, 5, 0, 0), 0, 0));
        columnPanel.add(DECIMAL_SEPARATOR,
                        new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
                                               GridBagConstraints.NONE, new Insets(0, 5, 5, 0), 0, 0));
        this.getContentPane().add(tabbedPane,
                                  new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.NORTH,
                                                         GridBagConstraints.BOTH,
                                                         new Insets(10, 5, 0, 5),
                                                         0,
                                                         0));
        this.getContentPane().add(detailButtonsPanel,
                                  new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTH,
                                                         GridBagConstraints.HORIZONTAL,
                                                         new Insets(10, 5, 5, 5),
                                                         0,
                                                         0));
        tabbedPane.add(mainPanel, "Section");
    }


    private class ActionListenerForFixedLength implements java.awt.event.ItemListener {
        public void itemStateChanged(java.awt.event.ItemEvent evt) {
            if (FIXED_LENGTH.isSelected()) {
                RECORD_LENGTH.setEnabled(true);
                RECORD_LENGTH.setBackground(UIManager.getColor("TextField.background"));
            }
            else {
                RECORD_LENGTH.setText(null);
                RECORD_LENGTH.setEnabled(false);
                RECORD_LENGTH.setBackground(UIManager.getColor("Panel.background"));
            }
        }
    }
}
