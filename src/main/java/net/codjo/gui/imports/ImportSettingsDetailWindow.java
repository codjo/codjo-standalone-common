/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.gui.imports;
import net.codjo.gui.model.TableComboBox;
import net.codjo.model.PeriodHome;
import net.codjo.operation.imports.BadFormatException;
import net.codjo.operation.imports.ImportBehavior;
import net.codjo.operation.imports.ImportBehaviorHome;
import net.codjo.persistent.PersistenceException;
import net.codjo.utils.IntegerField;
import net.codjo.utils.sql.AbstractDetailWindow;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
/**
 * Affiche et Edite un comportement d'import.
 *
 * @author $Author: acharif $
 * @version $Revision: 1.4 $
 *
 *
 */
public class ImportSettingsDetailWindow extends AbstractDetailWindow {
    /** Description of the Field */
    public JTextArea COMMENTRY = new JTextArea();
    /** Description of the Field */
    public TableComboBox DEST_TABLE_ID;
    /** Description of the Field */
    public JComboBox FIELD_SEPARATOR = new JComboBox();
    /** Description of the Field */
    public JTextField FILE_TYPE = new JTextField();
    /** Description of the Field */
    public JCheckBox FIXED_LENGTH = new JCheckBox();
    /** Description of the Field */
    public JCheckBox HEADER_LINE = new JCheckBox();
    /** Description of the Field */
    public IntegerField IMPORT_SETTINGS_ID = new IntegerField();
    /** Description of the Field */
    public JTextField IN_BOX = new JTextField();
    /** Description of the Field */
    public JTextField LOCATION = new JTextField();
    /** Description of the Field */
    public JTextField OUT_BOX = new JTextField();
    /** Description of the Field */
    public IntegerField RECORD_LENGTH = new IntegerField();
    /** Description of the Field */
    public JTextField STANDARD_FILE_NAME = new JTextField();
    /** Description of the Field */
    public JButton cancelButton = new JButton();
    /** Description of the Field */
    public ImportBehaviorHome importHome = null;
    /** Description of the Field */
    public JButton okButton = new JButton();
    /** Description of the Field */
    public PeriodHome periodHome = null;
    BorderLayout borderLayout1 = new BorderLayout();
    FlowLayout bottomFlowLayout = new FlowLayout();
    JPanel bottomPanel = new JPanel();
    JLabel destTableLabel = new JLabel();
    JPanel detailPanel = new JPanel();
    JLabel fieldSeparatorLabel = new JLabel();
    JTextField fileNameField = new JTextField();
    JLabel fileNameLabel = new JLabel();
    JLabel fileTypeLabel = new JLabel();
    GridBagLayout gridBagLayout1 = new GridBagLayout();
    JLabel inBoxLabel = new JLabel();
    JScrollPane jScrollPane1 = new JScrollPane();
    JLabel locationLabel = new JLabel();
    BorderLayout mainBorderLayout = new BorderLayout();
    JTabbedPane mainTabbedPane = new JTabbedPane();
    JLabel numeroLabel = new JLabel();
    JLabel outBoxLabel = new JLabel();
    JLabel recordLengthLabel = new JLabel();
    JLabel remarkLabel = new JLabel();
    JLabel standardFileNameLabel = new JLabel();
    ImportStructurePanel structurePanel;
    FlowLayout topFlowLayout = new FlowLayout();
    JLabel topLabel = new JLabel();
    JPanel topPanel = new JPanel();

    /**
     * Constructor for the ImportSettingsDetailWindow object
     *
     * @exception Exception Description of Exception
     */
    public ImportSettingsDetailWindow() throws Exception {
        FIELD_SEPARATOR.setModel(new DefaultComboBoxModel(getStdSeparator()));
        FIELD_SEPARATOR.setRenderer(new FieldSeparatorListCellRenderer());
        FIELD_SEPARATOR.insertItemAt(NULL_VALUE_COMBO, 0);
        FIELD_SEPARATOR.setSelectedIndex(-1);
        DEST_TABLE_ID =
            new TableComboBox(net.codjo.gui.Dependency.getTableHome(), "IMPORTEE");
        structurePanel = new ImportStructurePanel(this);
        jbInit();
        initUpdateListener();
    }

    /**
     * Enregistre la structure
     *
     * @param pk
     * @param con
     *
     * @exception SQLException Description of Exception
     * @exception PersistenceException Description of Exception
     */
    public void saveLinks(Map pk, Connection con)
            throws SQLException, PersistenceException {
        structurePanel.saveStructurePanel(pk, importHome);
    }


    public JPanel getDetailPanel() {
        return detailPanel;
    }


    public JLabel getInBoxLabel() {
        return inBoxLabel;
    }


    public JLabel getOutBoxLabel() {
        return outBoxLabel;
    }


    /**
     * DOCUMENT ME!
     *
     * @param evt Description of Parameter
     */
    void FIELD_SEPARATOR_actionPerformed(ActionEvent evt) {
        if ("Autre".equals(FIELD_SEPARATOR.getSelectedItem())) {
            JOptionPane newFieldSeparator = new JOptionPane();
            String inputValue =
                newFieldSeparator.showInputDialog(this,
                    "Tapez le nouveau séparateur (Un caractère):", "Séparateur",
                    newFieldSeparator.DEFAULT_OPTION);
            if (inputValue != null) {
                if (inputValue.length() == 1) {
                    FIELD_SEPARATOR.addItem(inputValue);
                    FIELD_SEPARATOR.setSelectedItem(inputValue);
                }
                else {
                    FIELD_SEPARATOR.setSelectedIndex(-1);
                    newFieldSeparator.showMessageDialog(this,
                        "Veuillez saisir un caractère.", "Alert",
                        newFieldSeparator.ERROR_MESSAGE);
                }
            }
            else {
                FIELD_SEPARATOR.setSelectedIndex(-1);
            }
        }
    }


    /**
     * Gets the ImportSettingsId attribute of the ImportSettingsDetailWindow object
     *
     * @return The ImportSettingsId value
     */
    int getImportSettingsId() {
        try {
            return IMPORT_SETTINGS_ID.getIntegerValue().intValue();
        }
        catch (NullPointerException ex) {
            return -1;
        }
    }


    /**
     * Initialise la liste des separateurs de champ Ajout le separateur trouve dans la
     * base si ce dernier ne fait pas partie de la liste.
     *
     * @return objet contenant la liste des separateurs
     */
    private Object[] getStdSeparator() {
        List list = new ArrayList();
        list.add("\\t");
        list.add(" ");
        list.add(";");
        list.add(":");
        list.add("Autre");
        return list.toArray();
    }


    /**
     * Construction des listeners sur la fenetre de details pour mise-a-jour automatique.
     */
    private void initUpdateListener() {
        STANDARD_FILE_NAME.getDocument().addDocumentListener(new DocumentListener() {
                /**
                 * DOCUMENT ME!
                 *
                 * @param e Description of Parameter
                 */
                public void changedUpdate(DocumentEvent e) {
                    updateRealFileName();
                }


                /**
                 * DOCUMENT ME!
                 *
                 * @param ev Description of Parameter
                 */
                public void insertUpdate(DocumentEvent ev) {
                    updateRealFileName();
                }


                /**
                 * DOCUMENT ME!
                 *
                 * @param e Description of Parameter
                 */
                public void removeUpdate(DocumentEvent e) {
                    updateRealFileName();
                }
            });
    }


    /**
     * Init de l'IHM.
     */
    private void jbInit() {
        this.getContentPane().setLayout(borderLayout1);
        topLabel.setFont(new java.awt.Font("Dialog", 1, 12));
        topLabel.setToolTipText("");
        topLabel.setText("Détail paramétrage import");
        topPanel.setLayout(topFlowLayout);
        topFlowLayout.setAlignment(FlowLayout.LEFT);
        cancelButton.setText("Annuler");
        okButton.setText("Valider");
        bottomPanel.setLayout(bottomFlowLayout);
        bottomFlowLayout.setAlignment(FlowLayout.RIGHT);
        detailPanel.setLayout(gridBagLayout1);
        fieldSeparatorLabel.setText("Séparateur");
        fileNameField.setEnabled(false);
        FIXED_LENGTH.setText("Longueur fixe");
        HEADER_LINE.setText("Ligne d\'entête");
        standardFileNameLabel.setText("Num-type du fichier");
        inBoxLabel.setText("IN BOX (TIFS)");
        recordLengthLabel.setText("Longueur");
        fileNameLabel.setText("Nom du fichier");
        COMMENTRY.setBorder(null);
        locationLabel.setText("Localisation en manuel");
        outBoxLabel.setText("OUT BOX (TIFS)");
        fileTypeLabel.setText("Type du fichier");
        jScrollPane1.setBorder(BorderFactory.createLoweredBevelBorder());
        mainTabbedPane.setOpaque(true);
        remarkLabel.setBounds(new Rectangle(14, 245, 97, 21));
        remarkLabel.setText("Commentaires");
        IMPORT_SETTINGS_ID.setEnabled(false);
        IMPORT_SETTINGS_ID.setEditable(false);
        IMPORT_SETTINGS_ID.setText("integerField1");
        numeroLabel.setText("Numéro");
        destTableLabel.setText("Table destination");
        this.setResizable(true);
        this.getContentPane().setBackground(UIManager.getColor("Panel.background"));
        this.getContentPane().add(topPanel, BorderLayout.NORTH);
        topPanel.add(topLabel, null);
        this.getContentPane().add(bottomPanel, BorderLayout.SOUTH);
        bottomPanel.add(okButton, null);
        bottomPanel.add(cancelButton, null);
        this.getContentPane().add(mainTabbedPane, BorderLayout.CENTER);
        mainTabbedPane.add(detailPanel, "Détail");
        FIELD_SEPARATOR.addActionListener(new ImportSettingsDetailWindowFieldSeparatorActionAdapter(
                this));
        detailPanel.add(FIELD_SEPARATOR,
            new GridBagConstraints(3, 7, 3, 1, 1.0, 0.0, GridBagConstraints.CENTER,
                GridBagConstraints.HORIZONTAL, new Insets(13, 0, 0, 0), 20, 0));
        detailPanel.add(jScrollPane1,
            new GridBagConstraints(1, 8, 6, 1, 1.0, 1.0, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(9, 6, 17, 19), 421, 40));
        detailPanel.add(inBoxLabel,
            new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, new Insets(13, 9, 0, 38), 20, 4));
        detailPanel.add(outBoxLabel,
            new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, new Insets(13, 9, 0, 36), 9, 4));
        detailPanel.add(fileNameLabel,
            new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, new Insets(13, 9, 0, 37), 16, 4));
        detailPanel.add(standardFileNameLabel,
            new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, new Insets(14, 9, 0, 9), 18, 4));
        detailPanel.add(recordLengthLabel,
            new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, new Insets(13, 9, 0, 30), 50, 4));
        detailPanel.add(fieldSeparatorLabel,
            new GridBagConstraints(2, 7, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, new Insets(13, 12, 0, 0), 4, 4));
        detailPanel.add(HEADER_LINE,
            new GridBagConstraints(6, 6, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                GridBagConstraints.NONE, new Insets(14, 16, 0, 19), 1, -4));
        detailPanel.add(FIXED_LENGTH,
            new GridBagConstraints(6, 7, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                GridBagConstraints.NONE, new Insets(13, 16, 0, 19), 4, -4));
        detailPanel.add(remarkLabel,
            new GridBagConstraints(0, 8, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, new Insets(9, 9, 57, 30), 20, 4));
        detailPanel.add(fileTypeLabel,
            new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, new Insets(13, 9, 0, 49), 5, 4));
        detailPanel.add(locationLabel,
            new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, new Insets(14, 9, 0, 0), 4, 4));
        detailPanel.add(numeroLabel,
            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, new Insets(17, 9, 0, 22), 66, 4));
        detailPanel.add(DEST_TABLE_ID,
            new GridBagConstraints(5, 0, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER,
                GridBagConstraints.HORIZONTAL, new Insets(17, 0, 0, 19), 3, 0));
        detailPanel.add(destTableLabel,
            new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, new Insets(17, 18, 0, 0), 6, 4));
        detailPanel.add(RECORD_LENGTH,
            new GridBagConstraints(1, 7, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, new Insets(13, 6, 0, 0), -26, 0));
        detailPanel.add(STANDARD_FILE_NAME,
            new GridBagConstraints(1, 6, 5, 1, 1.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, new Insets(14, 6, 0, 0), 302, 0));
        detailPanel.add(fileNameField,
            new GridBagConstraints(1, 5, 6, 1, 1.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, new Insets(13, 6, 0, 19), 421, 0));
        detailPanel.add(OUT_BOX,
            new GridBagConstraints(1, 4, 6, 1, 1.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, new Insets(13, 6, 0, 19), 421, 0));
        detailPanel.add(IN_BOX,
            new GridBagConstraints(1, 3, 6, 1, 1.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, new Insets(13, 6, 0, 19), 421, 0));
        detailPanel.add(LOCATION,
            new GridBagConstraints(1, 2, 6, 1, 1.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, new Insets(14, 6, 0, 19), 421, 0));
        detailPanel.add(FILE_TYPE,
            new GridBagConstraints(1, 1, 6, 1, 1.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, new Insets(13, 6, 0, 19), 421, 0));
        detailPanel.add(IMPORT_SETTINGS_ID,
            new GridBagConstraints(1, 0, 3, 1, 1.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, new Insets(17, 6, 0, 0), 68, 0));
        mainTabbedPane.add(structurePanel, "Structure");
        jScrollPane1.getViewport().add(COMMENTRY, null);
    }


    /**
     * Formatage du nom du fichier selon periode
     */
    private void updateRealFileName() {
        // TEMP
        if (periodHome == null) {
            return;
        }

        // END TEMP
        String fileName = STANDARD_FILE_NAME.getText();
        File f = new File(fileName);
        try {
            f = ImportBehavior.findRealFileName(f, periodHome.getCurrentPeriod());
            fileNameField.setText(f.getName());
        }
        catch (BadFormatException ex) {
            fileNameField.setText("[Mauvais Format]");
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @author $Author: acharif $
     * @version $Revision: 1.4 $
     */
    private class FieldSeparatorListCellRenderer extends JLabel
        implements ListCellRenderer {
        /**
         * Constructor for the FieldSeparatorListCellRenderer object
         */
        FieldSeparatorListCellRenderer() {
            setOpaque(true);
        }

        /**
         * Gets the ListCellRendererComponent attribute of the
         * FieldSeparatorListCellRenderer object
         *
         * @param list Description of Parameter
         * @param value Description of Parameter
         * @param index Description of Parameter
         * @param isSelected Description of Parameter
         * @param cellHasFocus Description of Parameter
         *
         * @return The ListCellRendererComponent value
         */
        public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            }
            else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }

            String str = "";
            if (value != null) {
                str = value.toString();
            }

            if ("\\t".equals(str)) {
                str = "Tabulation";
            }
            else if (" ".equals(str)) {
                str = "Espace";
            }
            else if (";".equals(str)) {
                str = "Point Virgule";
            }
            else if (":".equals(str)) {
                str = "Deux Points";
            }
            else if ("Autre".equals(str)) {
                str = "Autre...";
            }
            setText(str);
            return this;
        }
    }

/**
 * DOCUMENT ME!
 *
 * @author $Author: acharif $
 * @version $Revision: 1.4 $
 */
private class ImportSettingsDetailWindowFieldSeparatorActionAdapter
    implements java.awt.event.ActionListener {
    ImportSettingsDetailWindow adaptee;

    /**
     * Constructor for the ImportSettingsDetailWindowFieldSeparatorActionAdapter
     * object
     *
     * @param adaptee Description of Parameter
     */
    ImportSettingsDetailWindowFieldSeparatorActionAdapter(
        ImportSettingsDetailWindow adaptee) {
        this.adaptee = adaptee;
    }

    /**
     * DOCUMENT ME!
     *
     * @param evt Description of Parameter
     */
    public void actionPerformed(ActionEvent evt) {
        adaptee.FIELD_SEPARATOR_actionPerformed(evt);
    }
}
}

