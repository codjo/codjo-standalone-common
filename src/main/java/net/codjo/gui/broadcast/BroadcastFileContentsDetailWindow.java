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
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import org.apache.log4j.Logger;
/**
 * TODO.
 *
 * @version $Revision: 1.2 $
 */
public class BroadcastFileContentsDetailWindow
      extends net.codjo.utils.sql.AbstractDetailWindow {
    private static final Logger LOG =
          Logger.getLogger(BroadcastFileContentsDetailWindow.class);
    public JCheckBox COLUMN_HEADER = new JCheckBox();
    public JTextField COLUMN_SEPARATOR = new JTextField();
    public ConnectionManager CONNECTION_MANAGER;
    public JTextField CONTENT_ID = new JTextField();
    public JTextField FILE_ID = new JTextField();
    public GuiPreferencesManager GUI_PREFERENCES_MANAGER;
    public JCheckBox SECTION_HEADER = new JCheckBox();
    public JTextArea SECTION_HEADER_TEXT = new JTextArea();
    public JComboBox SECTION_ID = new JComboBox();
    public NumberField SECTION_POSITION = new NumberField();
    public DetailButtonsPanel detailButtonsPanel = new DetailButtonsPanel();
    BorderLayout borderLayout1 = new BorderLayout();
    JPanel columnPanel = new JPanel();
    GridBagLayout gridBagLayout1 = new GridBagLayout();
    GridBagLayout gridBagLayout2 = new GridBagLayout();
    GridBagLayout gridBagLayout3 = new GridBagLayout();
    JScrollPane headerScrollPane = new JScrollPane();
    JPanel jPanel1 = new JPanel();
    JPanel jPanel2 = new JPanel();
    JLabel positionLabel = new JLabel();
    JLabel sectionNameLabel = new JLabel();
    JPanel sectionPanel = new JPanel();
    JTabbedPane sectionTabPanel = new JTabbedPane();
    JLabel separatorLabel = new JLabel();
    private JPanel optionPanel = null;


    public BroadcastFileContentsDetailWindow() throws Exception {
        jbInitGui();
    }


    @Override
    public void fillComponent(SQLFieldList columns, ResultSet rs)
          throws java.sql.SQLException {
        super.fillComponent(columns, rs);
        buildOptionPanel(rs.getInt("CONTENT_ID"));
    }


    @Override
    public void fillDefaultValues(HashMap defaultValues) {
        super.fillDefaultValues(defaultValues);
        ceckFileId();
        initSectionCombo();

        try {
            buildOptionPanel(-1);
        }
        catch (SQLException e) {
            LOG.error(e);
            ErrorDialog.show(this, "Erreur pendant la création du option panel", e);
        }

        initGuiFieldsProperties();
    }


    @Override
    public void saveLinks(Map pk, Connection con)
          throws Exception {
        super.saveLinks(pk, con);

        if (optionPanel != null) {
            GuiPreferences guiPref =
                  GUI_PREFERENCES_MANAGER.getGuiPreferences(getFamily());
            guiPref.saveContentOptionPanel(pk, con, optionPanel);
        }
    }


    private void buildOptionPanel(int contentId) throws SQLException {
        Connection con = CONNECTION_MANAGER.getConnection();

        try {
            GuiPreferences guiPref =
                  GUI_PREFERENCES_MANAGER.getGuiPreferences(getFamily());

            if (optionPanel != null) {
                sectionTabPanel.remove(optionPanel);
            }

            optionPanel = guiPref.buildContentOptionPanel(con, contentId);

            if (optionPanel != null) {
                String optionTitle = optionPanel.getName();

                if (optionTitle == null) {
                    optionTitle = "Options";
                }

                sectionTabPanel.add(optionPanel, optionTitle);
            }
        }
        finally {
            CONNECTION_MANAGER.releaseConnection(con);
        }
    }


    private void ceckFileId() {
        if ("".equals(FILE_ID.getText())) {
            JOptionPane.showMessageDialog(this,
                                          "Vous devez sélectionner un fichier avant d'ajouter une section.",
                                          "Erreur", JOptionPane.ERROR_MESSAGE);
            throw new IllegalArgumentException("Aucun fichier n'a ete selecionne");
        }
    }


    private String getFamily() throws SQLException {
        Connection con = CONNECTION_MANAGER.getConnection();
        Statement stmt = con.createStatement();

        try {
            ResultSet rs =
                  stmt.executeQuery("select FAMILY " + " from "
                                    + GUI_PREFERENCES_MANAGER.getSectionTableName()
                                    + " where SECTION_ID = " + SECTION_ID.getSelectedItem().toString());

            if (!rs.next()) {
                throw new SQLException("Section inconnue id "
                                       + SECTION_ID.getSelectedItem().toString());
            }

            return rs.getString(1);
        }
        finally {
            CONNECTION_MANAGER.releaseConnection(con, stmt);
        }
    }


    private void initGuiFieldsProperties() {
        GUI_PREFERENCES_MANAGER.setProperties(sectionNameLabel, SECTION_ID,
                                              GuiConstants.CONTENTS_SECTION_ID);
        GUI_PREFERENCES_MANAGER.setProperties(positionLabel, SECTION_POSITION,
                                              GuiConstants.CONTENTS_SECTION_POSITION);
        GUI_PREFERENCES_MANAGER.setProperties(SECTION_HEADER, SECTION_HEADER,
                                              GuiConstants.CONTENTS_SECTION_HEADER);
        GUI_PREFERENCES_MANAGER.setProperties(null, SECTION_HEADER_TEXT,
                                              GuiConstants.CONTENTS_SECTION_HEADER_TEXT);
        GUI_PREFERENCES_MANAGER.setProperties(separatorLabel, COLUMN_SEPARATOR,
                                              GuiConstants.CONTENTS_COLUMN_SEPARATOR);
        GUI_PREFERENCES_MANAGER.setProperties(COLUMN_HEADER, COLUMN_HEADER,
                                              GuiConstants.CONTENTS_COLUMN_HEADER);
    }


    private void initSectionCombo() {
        try {
            SectionNameRenderer renderer =
                  new SectionNameRenderer(CONNECTION_MANAGER,
                                          GUI_PREFERENCES_MANAGER.getSectionTableName(), SECTION_ID);
            SECTION_ID.setRenderer(renderer);
        }
        catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                                          "Impossible de charger la liste des sections.", "Erreur",
                                          JOptionPane.ERROR_MESSAGE);
            throw new IllegalArgumentException(
                  "Impossible de charger la liste des sections.");
        }
    }


    private void jbInitGui() throws Exception {
        this.setResizable(true);
        this.setTitle("Détail de la table");
        this.getContentPane().setBackground(UIManager.getColor("Panel.background"));
        this.setPreferredSize(new Dimension(430, 430));
        this.getContentPane().setLayout(gridBagLayout3);
        jPanel1.setLayout(borderLayout1);
        columnPanel.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(
              Color.white,
              new Color(142, 142, 142)), "Colonnes"));
        columnPanel.setLayout(gridBagLayout2);
        separatorLabel.setText("Séparateur");
        sectionPanel.setLayout(gridBagLayout1);
        SECTION_POSITION.setColumns(0);
        COLUMN_HEADER.setText("En tête");
        positionLabel.setText("Position");
        SECTION_HEADER.setText("Insérer l\'en tête de la section");
        sectionNameLabel.setText("Nom");
        headerScrollPane.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(
              Color.white,
              new Color(134, 134, 134)), "En-tête"));
        this.getContentPane().add(sectionTabPanel,
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
        sectionTabPanel.add(jPanel1, "Section");
        columnPanel.add(separatorLabel,
                        new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                                               GridBagConstraints.NONE, new Insets(0, 5, 5, 0), 0, 0));
        columnPanel.add(COLUMN_SEPARATOR,
                        new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
                                               GridBagConstraints.HORIZONTAL, new Insets(0, 5, 5, 0), 51, 0));
        columnPanel.add(COLUMN_HEADER,
                        new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                               GridBagConstraints.NONE, new Insets(0, 18, 5, 165), 0, 0));
        sectionPanel.add(sectionNameLabel,
                         new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                                                GridBagConstraints.NONE, new Insets(10, 5, 0, 12), 8, 0));
        sectionPanel.add(SECTION_ID,
                         new GridBagConstraints(1, 0, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER,
                                                GridBagConstraints.HORIZONTAL,
                                                new Insets(10, 5, 0, 10),
                                                181,
                                                0));
        sectionPanel.add(positionLabel,
                         new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                                                GridBagConstraints.NONE, new Insets(10, 5, 0, 0), 0, 0));
        sectionPanel.add(SECTION_POSITION,
                         new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
                                                GridBagConstraints.HORIZONTAL,
                                                new Insets(10, 5, 0, 0),
                                                30,
                                                0));
        sectionPanel.add(SECTION_HEADER,
                         new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                                                GridBagConstraints.NONE, new Insets(10, 92, 0, 10), 0, 0));
        sectionPanel.add(headerScrollPane,
                         new GridBagConstraints(0, 2, 3, 1, 1.0, 1.0, GridBagConstraints.CENTER,
                                                GridBagConstraints.BOTH, new Insets(10, 5, 5, 5), 0, 0));
        headerScrollPane.getViewport().add(SECTION_HEADER_TEXT, null);
        jPanel1.add(columnPanel, BorderLayout.SOUTH);
        jPanel1.add(sectionPanel, BorderLayout.CENTER);
    }
}
