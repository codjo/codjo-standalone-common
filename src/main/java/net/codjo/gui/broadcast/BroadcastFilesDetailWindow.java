/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.gui.broadcast;
import net.codjo.gui.DetailButtonsPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.HashMap;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
/**
 * TODO.
 *
 * @version $Revision: 1.2 $
 */
public class BroadcastFilesDetailWindow extends net.codjo.utils.sql.AbstractDetailWindow {
    public JCheckBox AUTO_DISTRIBUTION = new JCheckBox();
    public JComboBox CFT_BATCH_FILE = new JComboBox();
    public JTextField DESTINATION_SYSTEM = new JTextField();
    public JComboBox DISTRIBUTION_METHOD = new JComboBox();
    public JComboBox FILE_DESTINATION_LOCATION = new JComboBox();
    public JCheckBox FILE_HEADER = new JCheckBox();
    public JTextArea FILE_HEADER_TEXT = new JTextArea();
    public JTextField FILE_NAME = new JTextField();
    public GuiPreferencesManager GUI_PREFERENCES_MANAGER = null;
    public JCheckBox HISTORISE_FILE = new JCheckBox();
    public JCheckBox SECTION_SEPARATOR = new JCheckBox();
    public DetailButtonsPanel detailButtonsPanel = new DetailButtonsPanel();
    GridBagLayout gridBagLayout1 = new GridBagLayout();
    JScrollPane scrollPane = new JScrollPane();
    private JLabel batchFileLabel = new JLabel();
    private JLabel destinationSystemLabel = new JLabel();
    private JLabel distributionMethodLabel = new JLabel();
    private JLabel fileNameLabel = new JLabel();
    private JLabel locationLabel = new JLabel();


    /**
     * Constructeur de SharePriceQuarantineDetailWindow
     */
    public BroadcastFilesDetailWindow() {
        jbInitGui();
    }


    @Override
    public void fillDefaultValues(HashMap defaultValues) {
        super.fillDefaultValues(defaultValues);
        fillDiffuserCode();
        fillBatchFile();
        fillDestinationLocation();
        initGuiFieldsProperties();
    }


    private void fillBatchFile() {
        CFT_BATCH_FILE.setModel(new DefaultComboBoxModel(
              GUI_PREFERENCES_MANAGER.getVtomBatchFilesNames()));
    }


    private void fillDestinationLocation() {
        FILE_DESTINATION_LOCATION.setModel(new DefaultComboBoxModel(
              GUI_PREFERENCES_MANAGER.getBroadcastLocations()));
    }


    private void fillDiffuserCode() {
        DISTRIBUTION_METHOD.setModel(new DefaultComboBoxModel(
              GUI_PREFERENCES_MANAGER.getDiffuserCode()));
    }


    private void initGuiFieldsProperties() {
        GUI_PREFERENCES_MANAGER.setProperties(fileNameLabel, FILE_NAME,
                                              GuiConstants.FILE_FILE_NAME);
        GUI_PREFERENCES_MANAGER.setProperties(destinationSystemLabel, DESTINATION_SYSTEM,
                                              GuiConstants.FILE_DESTINATION_SYSTEM);
        GUI_PREFERENCES_MANAGER.setProperties(locationLabel, FILE_DESTINATION_LOCATION,
                                              GuiConstants.FILE_FILE_DESTINATION_LOCATION);
        GUI_PREFERENCES_MANAGER.setProperties(distributionMethodLabel,
                                              DISTRIBUTION_METHOD, GuiConstants.FILE_DISTRIBUTION_METHOD);
        GUI_PREFERENCES_MANAGER.setProperties(batchFileLabel, CFT_BATCH_FILE,
                                              GuiConstants.FILE_CFT_BATCH_FILE);
        GUI_PREFERENCES_MANAGER.setProperties(SECTION_SEPARATOR, SECTION_SEPARATOR,
                                              GuiConstants.FILE_SECTION_SEPARATOR);
        GUI_PREFERENCES_MANAGER.setProperties(HISTORISE_FILE, HISTORISE_FILE,
                                              GuiConstants.FILE_HISTORISE_FILE);
        GUI_PREFERENCES_MANAGER.setProperties(AUTO_DISTRIBUTION, AUTO_DISTRIBUTION,
                                              GuiConstants.FILE_AUTO_DISTRIBUTION);
        GUI_PREFERENCES_MANAGER.setProperties(FILE_HEADER, FILE_HEADER,
                                              GuiConstants.FILE_FILE_HEADER);
        GUI_PREFERENCES_MANAGER.setProperties(null, FILE_HEADER_TEXT,
                                              GuiConstants.FILE_FILE_HEADER_TEXT);
    }


    private void jbInitGui() {
        this.setResizable(true);
        this.setTitle("Détail de la table");
        this.getContentPane().setBackground(UIManager.getColor("Panel.background"));
        this.setPreferredSize(new Dimension(470, 470));
        this.getContentPane().setLayout(gridBagLayout1);
        fileNameLabel.setText("Nom du fichier");
        destinationSystemLabel.setText("Système destination");
        locationLabel.setText("Localisation");
        FILE_HEADER.setText("Ajouter l\'en-tête de fichier");
        AUTO_DISTRIBUTION.setText("Déclenchement auto");
        HISTORISE_FILE.setText("Historisation quotidienne");
        distributionMethodLabel.setText("Moyen de diffusion");
        batchFileLabel.setText("Fichier batch");
        FILE_NAME.setColumns(30);
        SECTION_SEPARATOR.setText("Séparateur de section");
        scrollPane.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(
              Color.white,
              new Color(134, 134, 134)), "En-tête"));
        this.getContentPane().add(fileNameLabel,
                                  new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                                                         GridBagConstraints.NONE,
                                                         new Insets(10, 10, 0, 41),
                                                         0,
                                                         0));
        this.getContentPane().add(FILE_NAME,
                                  new GridBagConstraints(1, 0, 3, 1, 1.0, 0.0, GridBagConstraints.WEST,
                                                         GridBagConstraints.HORIZONTAL,
                                                         new Insets(10, 5, 0, 10),
                                                         13,
                                                         0));
        this.getContentPane().add(locationLabel,
                                  new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                                                         GridBagConstraints.NONE,
                                                         new Insets(10, 10, 0, 54),
                                                         0,
                                                         0));
        this.getContentPane().add(destinationSystemLabel,
                                  new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                                                         GridBagConstraints.NONE,
                                                         new Insets(10, 10, 0, 9),
                                                         0,
                                                         0));
        this.getContentPane().add(FILE_DESTINATION_LOCATION,
                                  new GridBagConstraints(1, 2, 3, 1, 1.0, 0.0, GridBagConstraints.CENTER,
                                                         GridBagConstraints.HORIZONTAL,
                                                         new Insets(10, 5, 0, 10),
                                                         218,
                                                         0));
        this.getContentPane().add(distributionMethodLabel,
                                  new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                                                         GridBagConstraints.NONE,
                                                         new Insets(10, 10, 0, 19),
                                                         0,
                                                         0));
        this.getContentPane().add(DISTRIBUTION_METHOD,
                                  new GridBagConstraints(1, 3, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER,
                                                         GridBagConstraints.HORIZONTAL,
                                                         new Insets(10, 5, 0, 22),
                                                         56,
                                                         0));
        this.getContentPane().add(batchFileLabel,
                                  new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                                                         GridBagConstraints.NONE,
                                                         new Insets(10, 10, 0, 51),
                                                         0,
                                                         0));
        this.getContentPane().add(CFT_BATCH_FILE,
                                  new GridBagConstraints(1, 4, 3, 1, 1.0, 0.0, GridBagConstraints.CENTER,
                                                         GridBagConstraints.HORIZONTAL,
                                                         new Insets(10, 5, 0, 10),
                                                         218,
                                                         0));
        this.getContentPane().add(SECTION_SEPARATOR,
                                  new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                         GridBagConstraints.HORIZONTAL,
                                                         new Insets(10, 10, 0, 0),
                                                         0,
                                                         0));

        this.getContentPane().add(HISTORISE_FILE,
                                  new GridBagConstraints(2, 5, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                         GridBagConstraints.NONE,
                                                         new Insets(10, 0, 0, 0),
                                                         0,
                                                         0));
        this.getContentPane().add(AUTO_DISTRIBUTION,
                                  new GridBagConstraints(3, 5, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                         GridBagConstraints.NONE,
                                                         new Insets(10, 7, 0, 20),
                                                         -3,
                                                         0));
        this.getContentPane().add(scrollPane,
                                  new GridBagConstraints(0, 7, 4, 1, 1.0, 1.0, GridBagConstraints.CENTER,
                                                         GridBagConstraints.BOTH,
                                                         new Insets(0, 10, 0, 10),
                                                         0,
                                                         0));
        this.getContentPane().add(detailButtonsPanel,
                                  new GridBagConstraints(0, 8, 4, 1, 0.0, 0.0, GridBagConstraints.SOUTH,
                                                         GridBagConstraints.HORIZONTAL,
                                                         new Insets(10, 10, 10, 10),
                                                         0,
                                                         0));
        this.getContentPane().add(FILE_HEADER,
                                  new GridBagConstraints(0, 6, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                         GridBagConstraints.NONE,
                                                         new Insets(5, 10, 5, 0),
                                                         0,
                                                         0));
        this.getContentPane().add(DESTINATION_SYSTEM,
                                  new GridBagConstraints(1, 1, 3, 1, 1.0, 0.0, GridBagConstraints.CENTER,
                                                         GridBagConstraints.HORIZONTAL,
                                                         new Insets(10, 5, 0, 10),
                                                         0,
                                                         0));
        scrollPane.getViewport().add(FILE_HEADER_TEXT, null);
    }
}
