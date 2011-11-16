/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.gui.broadcast;
import net.codjo.gui.DetailButtonsPanel;
import net.codjo.gui.model.TableFieldComboBox;
import net.codjo.gui.toolkit.number.NumberField;
import net.codjo.model.Table;
import net.codjo.model.TableHome;
import net.codjo.utils.ConnectionManager;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import org.apache.log4j.Logger;
/**
 * DOCUMENT ME!
 */
public class BroadcastColumnsDetailWindow extends net.codjo.utils.sql.AbstractDetailWindow {
    private static final Logger LOG =
          Logger.getLogger(BroadcastColumnsDetailWindow.class);
    public JTextField COLUMN_DATE_FORMAT = new JTextField();
    public NumberField COLUMN_LENGTH = new NumberField();
    public JTextField COLUMN_NAME = new JTextField();
    public NumberField COLUMN_NUMBER = new NumberField();
    public JTextField COLUMN_NUMBER_FORMAT = new JTextField();
    public JTextField EXPRESSION = new JTextField();
    /**
     * CONNECTION_MANAGER initialise par putDefault...
     */
    public ConnectionManager CONNECTION_MANAGER;
    public TableFieldComboBox DB_FIELD_NAME = new TableFieldComboBox();
    public JComboBox DB_TABLE_NAME = new JComboBox();
    /**
     * Le manager des preferences de diffusion, initialise par putDefault...
     */
    public GuiPreferencesManager GUI_PREFERENCES_MANAGER;
    public JTextField PADDING_CARACTER = new JTextField();
    public JCheckBox RIGHT_COLUMN_PADDING = new JCheckBox();
    public JTextField SECTION_ID = new JTextField();
    /**
     * TableHome initialise par putDefault...
     */
    public TableHome TABLE_HOME;
    public DetailButtonsPanel detailButtonsPanel = new DetailButtonsPanel();
    private JLabel columnDateFormatLabel = new JLabel();
    private GridBagLayout columnGridBagLayout = new GridBagLayout();
    private JLabel columnLengthLabel = new JLabel();
    private JLabel columnNamberLabel = new JLabel();
    private JLabel columnNameLabel = new JLabel();
    private JPanel columnPanel = new JPanel();
    private JLabel fieldNameLabel = new JLabel();
    private JLabel expressionLabel = new JLabel();
    private GuiPreferences guiPrefs;
    private JLabel numberFormatLabel = new JLabel();
    private JLabel paddingCaracterLabel = new JLabel();
    private GridBagLayout tableGridBagLayout = new GridBagLayout();
    private JLabel tableNameLabel = new JLabel();
    private ActionListener tableNameListener;
    private JPanel tablePanel = new JPanel();
    private GridBagLayout thisGridBagLayout = new GridBagLayout();


    /**
     * Constructeur de SharePriceQuarantineDetailWindow
     */
    public BroadcastColumnsDetailWindow() {
        jbInitGui();
        initTableNameListener();
    }


    /**
     * Description of the Method
     *
     * @param defaultValues Description of the Parameter
     *
     * @throws IllegalArgumentException TODO
     */
    @Override
    public void fillDefaultValues(HashMap defaultValues) {
        super.fillDefaultValues(defaultValues);

        if ("".equals(SECTION_ID.getText())) {
            JOptionPane.showMessageDialog(this,
                                          "Vous devez sélectionner une section avant d'ajouter une colonne.",
                                          "Erreur", JOptionPane.ERROR_MESSAGE);
            throw new IllegalArgumentException("Aucune section n'a ete selecionne");
        }

        try {
            initGuiPrefs();
        }
        catch (Exception ex) {
            LOG.error(ex);
            throw new IllegalArgumentException(ex.getLocalizedMessage());
        }

        initFieldName();
        initTableName();
        initGuiFieldsProperties();
    }


    private void dbTableNameActionPerformed() {
        try {
            if (GUI_PREFERENCES_MANAGER.getComputedTableName(guiPrefs.getFamily()).equals(DB_TABLE_NAME
                  .getSelectedItem())) {
                DB_FIELD_NAME.init(GUI_PREFERENCES_MANAGER.getComputedTableName(
                      guiPrefs.getFamily()),
                                   GUI_PREFERENCES_MANAGER.getComputedFieldNames(guiPrefs.getFamily()),
                                   CONNECTION_MANAGER);
            }
            else {
                Table table =
                      TABLE_HOME.getTable((String)DB_TABLE_NAME.getSelectedItem());
                DB_FIELD_NAME.init(table, CONNECTION_MANAGER);
            }
        }
        catch (Exception e) {
            LOG.error(e);
            DB_FIELD_NAME.setModel(new DefaultComboBoxModel());
        }
    }


    private String getFamily(String sectionId) throws SQLException {
        Connection con = CONNECTION_MANAGER.getConnection();
        Statement stmt = con.createStatement();

        try {
            ResultSet rs =
                  stmt.executeQuery("select FAMILY " + " from "
                                    + GUI_PREFERENCES_MANAGER.getSectionTableName()
                                    + " where SECTION_ID = " + sectionId);

            if (!rs.next()) {
                throw new IllegalArgumentException("Section inconnue id " + sectionId);
            }

            return rs.getString(1);
        }
        finally {
            CONNECTION_MANAGER.releaseConnection(con, stmt);
        }
    }


    private void initFieldName() {
        EventListener[] el = DB_TABLE_NAME.getListeners(ActionListener.class);

        for (EventListener anEl : el) {
            if (anEl.getClass().isInstance(tableNameListener)) {
                return;
            }
        }

        DB_TABLE_NAME.addActionListener(tableNameListener);
    }


    private void initGuiFieldsProperties() {
        GUI_PREFERENCES_MANAGER.setProperties(tableNameLabel, DB_TABLE_NAME,
                                              GuiConstants.COLUMNS_DB_TABLE_NAME);
        GUI_PREFERENCES_MANAGER.setProperties(fieldNameLabel, DB_FIELD_NAME,
                                              GuiConstants.COLUMNS_DB_FIELD_NAME);
        GUI_PREFERENCES_MANAGER.setProperties(columnNameLabel, COLUMN_NAME,
                                              GuiConstants.COLUMNS_COLUMN_NAME);
        GUI_PREFERENCES_MANAGER.setProperties(columnNamberLabel, COLUMN_NUMBER,
                                              GuiConstants.COLUMNS_COLUMN_NUMBER);
        GUI_PREFERENCES_MANAGER.setProperties(columnLengthLabel, COLUMN_LENGTH,
                                              GuiConstants.COLUMNS_COLUMN_LENGTH);
        GUI_PREFERENCES_MANAGER.setProperties(columnDateFormatLabel, COLUMN_DATE_FORMAT,
                                              GuiConstants.COLUMNS_COLUMN_DATE_FORMAT);
        GUI_PREFERENCES_MANAGER.setProperties(numberFormatLabel, COLUMN_NUMBER_FORMAT,
                                              GuiConstants.COLUMNS_COLUMN_NUMBER_FORMAT);
        GUI_PREFERENCES_MANAGER.setProperties(paddingCaracterLabel, PADDING_CARACTER,
                                              GuiConstants.COLUMNS_PADDING_CARACTER);
        GUI_PREFERENCES_MANAGER.setProperties(RIGHT_COLUMN_PADDING, RIGHT_COLUMN_PADDING,
                                              GuiConstants.COLUMNS_RIGHT_COLUMN_PADDING);
        GUI_PREFERENCES_MANAGER.setProperties(expressionLabel, EXPRESSION,
                                              GuiConstants.COLUMNS_EXPRESSION);
    }


    private void initGuiPrefs() throws SQLException {
        String family = getFamily(SECTION_ID.getText());
        guiPrefs = GUI_PREFERENCES_MANAGER.getGuiPreferences(family);
    }


    private void initTableName() {
        Collection<String> tableList = GUI_PREFERENCES_MANAGER.getTableList(guiPrefs.getFamily());

        tableList.remove(GUI_PREFERENCES_MANAGER.getSelectionTableName(
              guiPrefs.getFamily()));

        DefaultComboBoxModel model = new DefaultComboBoxModel(tableList.toArray());
        DB_TABLE_NAME.setModel(model);

        DB_TABLE_NAME.setRenderer(new TableNameRenderer(tableList));

        // Dans le cas Ajout de ligne, on positionne la DB_TABLE_NAME
        if ("Add".equals(getActionType())) {
            DB_TABLE_NAME.setSelectedIndex(0);
        }
    }


    private void initTableNameListener() {
        tableNameListener =
              new ActionListener() {
                  public void actionPerformed(ActionEvent evt) {
                      dbTableNameActionPerformed();
                  }
              };
    }


    private void jbInitGui() {
        this.setResizable(true);
        this.setTitle("Détail de la table");
        this.getContentPane().setBackground(UIManager.getColor("Panel.background"));
        this.setMinimumSize(new Dimension(500, 410));
        this.setPreferredSize(new Dimension(500, 410));
        this.getContentPane().setLayout(thisGridBagLayout);
        RIGHT_COLUMN_PADDING.setHorizontalTextPosition(SwingConstants.LEFT);
        RIGHT_COLUMN_PADDING.setText("Remplissage droit ");
        tableNameLabel.setText("Nom");
        fieldNameLabel.setText("Champ");
        columnNameLabel.setText("Nom");
        tablePanel.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(
              Color.white,
              new Color(142, 142, 142)), "Table source"));
        tablePanel.setLayout(tableGridBagLayout);
        columnPanel.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(
              Color.white,
              new Color(142, 142, 142)), "Colonne destination"));
        columnPanel.setLayout(columnGridBagLayout);
        columnNamberLabel.setText("Index");
        columnLengthLabel.setText("Longueur");
        paddingCaracterLabel.setText("Remplissage");
        columnDateFormatLabel.setText("Format date");
        expressionLabel.setText("Expression");
        PADDING_CARACTER.setSelectionEnd(1);
        PADDING_CARACTER.setColumns(2);
        numberFormatLabel.setText("Format nombre");
        COLUMN_LENGTH.setColumns(3);
        COLUMN_NUMBER.setColumns(3);
        COLUMN_DATE_FORMAT.setColumns(10);
        COLUMN_NUMBER_FORMAT.setColumns(10);
        this.getContentPane().add(tablePanel,
                                  new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.NORTH,
                                                         GridBagConstraints.HORIZONTAL,
                                                         new Insets(5, 5, 0, 5), 350, 0));
        tablePanel.add(tableNameLabel,
                       new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
                                              GridBagConstraints.NONE, new Insets(5, 5, 0, 0), 0, 0));
        tablePanel.add(fieldNameLabel,
                       new GridBagConstraints(0, 1, 1, 1, 0.0, 1.0, GridBagConstraints.NORTHWEST,
                                              GridBagConstraints.NONE, new Insets(10, 5, 10, 0), 0, 0));
        tablePanel.add(DB_TABLE_NAME,
                       new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST,
                                              GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0));
        tablePanel.add(DB_FIELD_NAME,
                       new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST,
                                              GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 5), 0, 0));
        this.getContentPane().add(columnPanel,
                                  new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTH,
                                                         GridBagConstraints.HORIZONTAL,
                                                         new Insets(10, 5, 0, 5), 0, 0));
        columnPanel.add(columnNamberLabel,
                        new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
                                               GridBagConstraints.NONE, new Insets(10, 5, 0, 0), 0, 0));
        columnPanel.add(COLUMN_NUMBER,
                        new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
                                               GridBagConstraints.NONE, new Insets(10, 5, 0, 0), 0, 0));
        columnPanel.add(columnLengthLabel,
                        new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
                                               GridBagConstraints.NONE, new Insets(10, 5, 0, 0), 0, 0));
        columnPanel.add(columnDateFormatLabel,
                        new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
                                               GridBagConstraints.NONE, new Insets(10, 5, 0, 0), 0, 0));
        columnPanel.add(paddingCaracterLabel,
                        new GridBagConstraints(0, 4, 1, 1, 0.0, 1.0, GridBagConstraints.NORTHWEST,
                                               GridBagConstraints.NONE, new Insets(10, 5, 10, 0), 0, 0));
        columnPanel.add(expressionLabel,
                        new GridBagConstraints(0, 5, 1, 1, 0.0, 2.0, GridBagConstraints.NORTHWEST,
                                               GridBagConstraints.NONE, new Insets(12, 7, 12, 2), 0, 0));

        columnPanel.add(PADDING_CARACTER,
                        new GridBagConstraints(1, 4, 3, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
                                               GridBagConstraints.NONE, new Insets(10, 5, 0, 5), 0, 0));
        columnPanel.add(columnNameLabel,
                        new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
                                               GridBagConstraints.NONE, new Insets(5, 5, 0, 0), 0, 0));
        columnPanel.add(COLUMN_DATE_FORMAT,
                        new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
                                               GridBagConstraints.NONE, new Insets(10, 5, 0, 0), 0, 0));
        columnPanel.add(RIGHT_COLUMN_PADDING,
                        new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
                                               GridBagConstraints.NONE, new Insets(8, 140, 0, 0), 0, 0));
        columnPanel.add(EXPRESSION,
                        new GridBagConstraints(1, 5, 3, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
                                               GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0));

        columnPanel.add(COLUMN_NAME,
                        new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST,
                                               GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0));
        columnPanel.add(COLUMN_LENGTH,
                        new GridBagConstraints(1, 2, 3, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
                                               GridBagConstraints.NONE, new Insets(10, 5, 0, 50), 0, 0));
        columnPanel.add(COLUMN_NUMBER_FORMAT,
                        new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
                                               GridBagConstraints.NONE, new Insets(10, 249, 0, 5), 0, 0));
        columnPanel.add(numberFormatLabel,
                        new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                                               GridBagConstraints.NONE, new Insets(10, 140, 0, 5), 0, 0));
        this.getContentPane().add(detailButtonsPanel,
                                  new GridBagConstraints(0, 2, 1, 1, 0.0, 1.0, GridBagConstraints.NORTH,
                                                         GridBagConstraints.HORIZONTAL,
                                                         new Insets(5, 7, 5, 7), 0, 0));
    }


    private class TableNameRenderer extends DefaultListCellRenderer {
        final Map<String, String> translateTable = new HashMap<String, String>();


        /**
         * Constructeur de TableNameRenderer
         *
         * @param tableList Description of the Parameter
         */
        TableNameRenderer(Collection<String> tableList) {
            for (String tableName : tableList) {
                if (GUI_PREFERENCES_MANAGER.getComputedTableName(guiPrefs.getFamily())
                      .equals(tableName)) {
                    translateTable.put(tableName, "Champs Calculés");
                }
                else {
                    try {
                        translateTable.put(tableName,
                                           TABLE_HOME.getTable(tableName).getTableName());
                    }
                    catch (Exception e) {
                        LOG.error(e);
                        translateTable.put(tableName, tableName);
                    }
                }
            }
        }


        @Override
        public Component getListCellRendererComponent(JList list, Object value,
                                                      int index, boolean isSelected, boolean cellHasFocus) {
            return super.getListCellRendererComponent(list, translateTable.get(value),
                                                      index, isSelected, cellHasFocus);
        }
    }
}
