/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.gui.imports;
import net.codjo.gui.ListInputError;
import net.codjo.gui.renderer.FieldNameRenderer;
import net.codjo.model.Table;
import net.codjo.operation.imports.FieldImport;
import net.codjo.operation.imports.FieldImportHome;
import net.codjo.operation.imports.ImportBehaviorHome;
import net.codjo.persistent.PersistenceException;
import net.codjo.utils.CloseEditorListener;
import net.codjo.utils.ConnectionManager;
import net.codjo.utils.sql.GenericTable;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Map;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
/**
 * Visualisation de la structure d'un import
 *
 * @author $Author: marcona $
 * @version $Revision: 1.4 $
 */
class ImportStructurePanel extends JPanel {
    JButton addButton = new JButton();
    BorderLayout borderLayout1 = new BorderLayout();
    JPanel bottomPanel = new JPanel();
    JButton deleteButton = new JButton();
    FlowLayout flowLayout1 = new FlowLayout();
    GenericTable structureTable;
    JScrollPane tableScrollPane = new JScrollPane();
    private ConnectionManager connectionManager;
    private ImportSettingsDetailWindow detailWindow;
    private ListInputError listInputError;

    /**
     * Constructor for the ImportStructurePanel object
     *
     * @param w Description of Parameter
     *
     * @exception Exception Oups.
     */
    ImportStructurePanel(ImportSettingsDetailWindow w)
            throws Exception {
        detailWindow = w;

        Table tableFieldImport =
            net.codjo.gui.Dependency.getTableHome().getTable("PM_FIELD_IMPORT_SETTINGS");

        connectionManager = net.codjo.gui.Dependency.getConnectionManager();

        structureTable =
            new GenericTable(tableFieldImport, false, "where IMPORT_SETTINGS_ID=-1");

        structureTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        structureTable.getModel().addTableModelListener(new CloseEditorListener(
                structureTable));

        jbInit();
        initDetailWindowListener();
        initCombo();
        listInputError = new ListInputError(structureTable);
    }

    /**
     * Sauvegarde la structure d'un import
     *
     * @param pk La clef primaire
     * @param ih Description of the Parameter
     *
     * @exception PersistenceException
     * @exception SQLException Description of Exception
     */
    public void saveStructurePanel(Map pk, ImportBehaviorHome ih)
            throws PersistenceException, SQLException {
        FieldImportHome fieldImportHome = ih.getFieldImportHome();
        Integer id = (Integer)pk.get("IMPORT_SETTINGS_ID");

        fieldImportHome.deleteFieldImport(id.intValue());

        TableModel model = structureTable.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            String dbName = (String)structureTable.getValueAt(i, 1);
            Integer position = (Integer)structureTable.getValueAt(i, 2);
            Integer length = (Integer)structureTable.getValueAt(i, 3);
            String fieldType = (String)structureTable.getValueAt(i, 4);
            Integer inputDateFormat = (Integer)structureTable.getValueAt(i, 5);
            Boolean removeLeftZeros = (Boolean)structureTable.getValueAt(i, 6);
            String decimalSeparator = (String)structureTable.getValueAt(i, 7);

            try {
                if (position == null
                        || length == null
                        || fieldType == null
                        || removeLeftZeros == null) {
                    throw new IllegalArgumentException("Ligne invalide");
                }
                FieldImport fi =
                    fieldImportHome.newFieldImport(id.intValue(), dbName,
                        position.intValue(), length.intValue(), fieldType.charAt(0),
                        decimalSeparator,
                        (inputDateFormat == null) ? 0 : inputDateFormat.intValue(),
                        removeLeftZeros.booleanValue());

                fi.save();
            }
            catch (PersistenceException ex) {
                listInputError.setErrorLine(i);
                throw ex;
            }
            catch (RuntimeException ex) {
                listInputError.setErrorLine(i);
                throw ex;
            }
            listInputError.setNoError();
        }
    }


    /**
     * Action executée lors de l'appui sur le bouton Ajout
     *
     * @param event The feature to be added to the Button_actionPerformed attribute
     */
    void addButton_actionPerformed(ActionEvent event) {
        this.tableScrollPane.getVerticalScrollBar().setValue(0);
        structureTable.addNewLine();
        structureTable.getModel().setValueAt(new Integer(
                detailWindow.getImportSettingsId()), 0, 0);
    }


    /**
     * Action executée lors de l'appui sur le bouton Supprimer
     *
     * @param event Description of Parameter
     */
    void deleteButton_actionPerformed(ActionEvent event) {
        int realIndex =
            ((Integer)structureTable.getModel().getValueAt(structureTable.getSelectedRow(),
                -1)).intValue();
        structureTable.deleteLine(realIndex);
    }


    /**
     * Action executée lors du changement de selection sur la table
     *
     * @param enable Description of Parameter
     */
    void deleteButton_selectionChange(boolean enable) {
        deleteButton.setEnabled(enable);
    }


    /**
     * Initialisation des comboBox de saisie et des renderers
     *
     * @exception SQLException Description of Exception
     */
    private void initCombo() throws SQLException {
        JComboBox fieldTypeComboBox = new JComboBox();
        JComboBox separatorComboBox = new JComboBox();
        JComboBox dateFormatComboBox = new JComboBox();

        DateFormatRenderer dateFormatRenderer = new DateFormatRenderer();
        FieldTypeRenderer fieldTypeRenderer = new FieldTypeRenderer();
        SeparatorRenderer separatorRenderer = new SeparatorRenderer();

        fieldTypeComboBox.addItem("S");
        fieldTypeComboBox.addItem("N");
        fieldTypeComboBox.addItem("D");
        fieldTypeComboBox.addItem("B");
        fieldTypeComboBox.setRenderer(fieldTypeRenderer);

        separatorComboBox.addItem("");
        separatorComboBox.addItem(".");
        separatorComboBox.addItem(",");
        separatorComboBox.setRenderer(separatorRenderer);

        dateFormatComboBox.addItem(new Integer(0));
        dateFormatComboBox.addItem(new Integer(1));
        dateFormatComboBox.addItem(new Integer(2));
        dateFormatComboBox.addItem(new Integer(3));
        dateFormatComboBox.addItem(new Integer(4));
        dateFormatComboBox.addItem(new Integer(5));
        dateFormatComboBox.addItem(new Integer(7));
        dateFormatComboBox.addItem(new Integer(8));
        dateFormatComboBox.setRenderer(dateFormatRenderer);

        structureTable.getColumnByDbField("DESTINATION_FIELD_TYPE").setCellRenderer(fieldTypeRenderer);
        structureTable.getColumnByDbField("DESTINATION_FIELD_TYPE").setCellEditor(new DefaultCellEditor(
                fieldTypeComboBox));
        structureTable.getColumnByDbField("DECIMAL_SEPARATOR").setCellRenderer(separatorRenderer);
        structureTable.getColumnByDbField("DECIMAL_SEPARATOR").setCellEditor(new DefaultCellEditor(
                separatorComboBox));
        structureTable.getColumnByDbField("INPUT_DATE_FORMAT").setCellRenderer(dateFormatRenderer);
        structureTable.getColumnByDbField("INPUT_DATE_FORMAT").setCellEditor(new DefaultCellEditor(
                dateFormatComboBox));
    }


    /**
     * Constructor for the initDestCombo object
     */
    private void initDestCombo() {
        try {
            JComboBox fieldNameComboBox = new JComboBox();
            Table tableDest = detailWindow.DEST_TABLE_ID.getSelectedTable();
            FieldNameRenderer fieldNameRenderer =
                new FieldNameRenderer(connectionManager, tableDest.getDBTableName(),
                    fieldNameComboBox);
            fieldNameComboBox.setRenderer(fieldNameRenderer);
            structureTable.getColumnByDbField("DB_DESTINATION_FIELD_NAME")
                          .setCellRenderer(fieldNameRenderer);
            structureTable.getColumnByDbField("DB_DESTINATION_FIELD_NAME").setCellEditor(new DefaultCellEditor(
                    fieldNameComboBox));
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
    }


    /**
     * Construction des listeners sur la fenetre de details pour mise-a-jour automatique.
     */
    private void initDetailWindowListener() {
        detailWindow.DEST_TABLE_ID.addActionListener(new ActionListener() {
                /**
                 * DOCUMENT ME!
                 *
                 * @param ev Description of Parameter
                 */
                public void actionPerformed(ActionEvent ev) {
                    initDestCombo();
                }
            });
        detailWindow.IMPORT_SETTINGS_ID.getDocument().addDocumentListener(new DocumentListener() {
                /**
                 * DOCUMENT ME!
                 *
                 * @param e Description of Parameter
                 */
                public void changedUpdate(DocumentEvent e) {
                    reloadStructure();
                }


                /**
                 * DOCUMENT ME!
                 *
                 * @param ev Description of Parameter
                 */
                public void insertUpdate(DocumentEvent ev) {
                    reloadStructure();
                }


                /**
                 * DOCUMENT ME!
                 *
                 * @param e Description of Parameter
                 */
                public void removeUpdate(DocumentEvent e) {
                    reloadStructure();
                }
            });
    }


    /**
     * Init IHM
     *
     * @exception Exception Oups
     */
    private void jbInit() throws Exception {
        this.setLayout(borderLayout1);
        deleteButton.setForeground(Color.red);
        deleteButton.setMaximumSize(new Dimension(93, 25));
        deleteButton.setMinimumSize(new Dimension(93, 25));
        deleteButton.setPreferredSize(new Dimension(93, 25));
        deleteButton.setActionCommand("delete");
        deleteButton.setText("Supprimer");
        bottomPanel.setLayout(flowLayout1);
        addButton.setMaximumSize(new Dimension(73, 25));
        addButton.setMinimumSize(new Dimension(73, 25));
        addButton.setPreferredSize(new Dimension(73, 25));
        addButton.setActionCommand("add");
        addButton.setText("Ajouter");
        tableScrollPane.setAutoscrolls(true);
        this.add(bottomPanel, BorderLayout.SOUTH);
        bottomPanel.add(addButton, null);
        bottomPanel.add(deleteButton, null);
        this.add(tableScrollPane, BorderLayout.CENTER);
        tableScrollPane.getViewport().add(structureTable);
        ImportStructureWindowAction actions = new ImportStructureWindowAction();
        addButton.addActionListener(actions);
        deleteButton.addActionListener(actions);
        ImportStructureSelectionListener actions_selection =
            new ImportStructureSelectionListener();
        structureTable.getSelectionModel().addListSelectionListener(actions_selection);
        deleteButton.setEnabled(false);
    }


    /**
     * Overview.
     *
     * @todo L'erreur SQL n'est pas geree !
     */
    private void reloadStructure() {
        try {
            structureTable.reloadData("from PM_FIELD_IMPORT_SETTINGS "
                + "where IMPORT_SETTINGS_ID=" + detailWindow.getImportSettingsId());
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Lancement des actions des boutons
     *
     * @author $Author: marcona $
     * @version $Revision: 1.4 $
     */
    class ImportStructureWindowAction implements java.awt.event.ActionListener {
        /**
         * DOCUMENT ME!
         *
         * @param event Description of Parameter
         */
        public void actionPerformed(java.awt.event.ActionEvent event) {
            String cmd = event.getActionCommand();
            if ("add".equals(cmd)) {
                addButton_actionPerformed(event);
            }
            else if ("delete".equals(cmd)) {
                deleteButton_actionPerformed(event);
            }
        }
    }


    /**
     * Renderer pour le format de date pour la combo et la liste
     *
     * @author $Author: marcona $
     * @version $Revision: 1.4 $
     */
    private class DateFormatRenderer implements ListCellRenderer, TableCellRenderer {
        private DefaultListCellRenderer listCellRenderer = new DefaultListCellRenderer();
        private DefaultTableCellRenderer tableCellRenderer =
            new DefaultTableCellRenderer();

        /**
         * Constructor for the DateFormatRenderer object
         */
        DateFormatRenderer() {
            tableCellRenderer.setHorizontalAlignment(JLabel.CENTER);
            listCellRenderer.setHorizontalAlignment(JLabel.CENTER);
        }

        /**
         * Gets the ListCellRendererComponent attribute of the DateFormatRenderer object
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
            return listCellRenderer.getListCellRendererComponent(list,
                translateValue(value), index, isSelected, cellHasFocus);
        }


        /**
         * Gets the TableCellRendererComponent attribute of the DateFormatRenderer object
         *
         * @param table Description of Parameter
         * @param value Description of Parameter
         * @param isSelected Description of Parameter
         * @param hasFocus Description of Parameter
         * @param row Description of Parameter
         * @param column Description of Parameter
         *
         * @return The TableCellRendererComponent value
         */
        public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
            return tableCellRenderer.getTableCellRendererComponent(table,
                translateValue(value), isSelected, hasFocus, row, column);
        }


        /**
         * Traduit la value contenue dans la combo en valeur à stocker en BD
         *
         * @param value La nouvelle valeur choisie
         *
         * @return La valeur telle qu'on doit la stocker
         */
        public String translateValue(Object value) {
            if (value == null) {
                return null;
            }
            int typeDate = ((Integer)value).intValue();
            switch (typeDate) {
                case (0):
                    return " ";
                case (1):
                    return "AAAAMMJJ";
                case (2):
                    return "AAAA-MM-JJ";
                case (3):
                    return "AAAA/MM/JJ";
                case (4):
                    return "JJ-MM-AA";
                case (5):
                    return "JJ-MM-AAAA";
                case (6):
                    return "JJMMAAAA";
                case (7):
                    return "JJ/MM/AAAA";
                case (8):
                    return "JJ/MM/AA";
                default:
                    return "? " + Integer.toString(typeDate) + " ?";
            }
        }
    }


    /**
     * Renderer pour le type de champ pour la combo et la liste
     *
     * @author $Author: marcona $
     * @version $Revision: 1.4 $
     */
    private class FieldTypeRenderer implements ListCellRenderer, TableCellRenderer {
        private DefaultListCellRenderer listCellRenderer = new DefaultListCellRenderer();
        private DefaultTableCellRenderer tableCellRenderer =
            new DefaultTableCellRenderer();

        /**
         * Constructor for the FieldTypeRenderer object
         */
        FieldTypeRenderer() {
            tableCellRenderer.setHorizontalAlignment(JLabel.CENTER);
            listCellRenderer.setHorizontalAlignment(JLabel.CENTER);
        }

        /**
         * Gets the ListCellRendererComponent attribute of the FieldTypeRenderer object
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
            return listCellRenderer.getListCellRendererComponent(list,
                translateValue(value), index, isSelected, cellHasFocus);
        }


        /**
         * Gets the TableCellRendererComponent attribute of the FieldTypeRenderer object
         *
         * @param table Description of Parameter
         * @param value Description of Parameter
         * @param isSelected Description of Parameter
         * @param hasFocus Description of Parameter
         * @param row Description of Parameter
         * @param column Description of Parameter
         *
         * @return The TableCellRendererComponent value
         */
        public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
            return tableCellRenderer.getTableCellRendererComponent(table,
                translateValue(value), isSelected, hasFocus, row, column);
        }


        /**
         * Traduit la value contenue dans la combo en valeur à stocker en BD
         *
         * @param value La nouvelle valeur choisie
         *
         * @return La valeur telle qu'on doit la stocker
         */
        public String translateValue(Object value) {
            String str = (String)value;
            if ("S".equals(str)) {
                return "Chaîne";
            }
            else if ("N".equals(str)) {
                return "Nombre";
            }
            else if ("D".equals(str)) {
                return "Date";
            }
            else if ("B".equals(str)) {
                return "Booléen";
            }
            else {
                return str;
            }
        }
    }


    /**
     * Ecouteur de selection
     *
     * @author $Author: marcona $
     * @version $Revision: 1.4 $
     */
    private class ImportStructureSelectionListener implements ListSelectionListener {
        /**
         * DOCUMENT ME!
         *
         * @param e Description of Parameter
         */
        public void valueChanged(ListSelectionEvent e) {
            if (e.getValueIsAdjusting()) {
                return;
            }

            ListSelectionModel lsm = (ListSelectionModel)e.getSource();
            if (lsm.isSelectionEmpty()) {
                deleteButton_selectionChange(false);
            }
            else {
                deleteButton_selectionChange(true);
            }
        }
    }


    /**
     * Renderer pour le separateur décimal pour la combo et la liste
     *
     * @author $Author: marcona $
     * @version $Revision: 1.4 $
     */
    private class SeparatorRenderer implements ListCellRenderer, TableCellRenderer {
        private DefaultListCellRenderer listCellRenderer = new DefaultListCellRenderer();
        private DefaultTableCellRenderer tableCellRenderer =
            new DefaultTableCellRenderer();

        /**
         * Constructor for the SeparatorRenderer object
         */
        SeparatorRenderer() {
            tableCellRenderer.setHorizontalAlignment(JLabel.CENTER);
            listCellRenderer.setHorizontalAlignment(JLabel.CENTER);
        }

        /**
         * Gets the ListCellRendererComponent attribute of the SeparatorRenderer object
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
            return listCellRenderer.getListCellRendererComponent(list,
                translateValue(value), index, isSelected, cellHasFocus);
        }


        /**
         * Gets the TableCellRendererComponent attribute of the SeparatorRenderer object
         *
         * @param table Description of Parameter
         * @param value Description of Parameter
         * @param isSelected Description of Parameter
         * @param hasFocus Description of Parameter
         * @param row Description of Parameter
         * @param column Description of Parameter
         *
         * @return The TableCellRendererComponent value
         */
        public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
            return tableCellRenderer.getTableCellRendererComponent(table,
                translateValue(value), isSelected, hasFocus, row, column);
        }


        /**
         * Traduit la value contenue dans la combo en valeur à stocker en BD
         *
         * @param value La nouvelle valeur choisie
         *
         * @return La valeur telle qu'on doit la stocker
         */
        public String translateValue(Object value) {
            String str = (String)value;
            if ("".equals(str)) {
                return " ";
            }
            else if (".".equals(str)) {
                return "Point";
            }
            else if (",".equals(str)) {
                return "Virgule";
            }
            else {
                return str;
            }
        }
    }
}
