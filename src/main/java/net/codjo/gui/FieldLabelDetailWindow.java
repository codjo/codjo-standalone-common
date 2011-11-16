/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.gui;
import net.codjo.gui.model.DBTableNameRenderer;
import net.codjo.gui.model.TableReferenceComparator;
import net.codjo.gui.toolkit.util.ErrorDialog;
import net.codjo.model.Table;
import net.codjo.persistent.PersistenceException;
import net.codjo.persistent.Reference;
import net.codjo.utils.ConnectionManager;
import net.codjo.utils.SQLFieldList;
import net.codjo.utils.sql.AbstractDetailWindow;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
/**
 * Ecran de détail de la table PM_FIELD_LABEL.
 *
 * @version $Revision: 1.9 $
 */
public class FieldLabelDetailWindow extends AbstractDetailWindow {
    /**
     */
    public JComboBox DB_FIELD_NAME = new JComboBox();
    /**
     */
    public JComboBox DB_TABLE_NAME = new JComboBox();
    /**
     */
    public JTextField FIELD_LABEL = new JTextField();
    /**
     */
    public DetailButtonsPanel detailButtonsPanel = new DetailButtonsPanel();
    GridBagLayout gridBagLayout1 = new GridBagLayout();
    JCheckBox tableCheck = new JCheckBox();
    private BorderLayout borderLayout1 = new BorderLayout();
    private JLabel label = new JLabel();
    private ItemListener listener;
    private JPanel mainPanel = new JPanel();
    private JLabel nomLogique = new JLabel();
    private JLabel nomTable = new JLabel();


    /**
     * Constructeur
     *
     * @throws java.sql.SQLException Description of Exception
     */
    public FieldLabelDetailWindow() throws java.sql.SQLException {
        try {
            listener =
                  new ItemListener() {
                      public void itemStateChanged(ItemEvent ie) {
                          if (ie.getStateChange() == ie.SELECTED) {
                              DB_TABLE_NAME.removeItemListener(listener);
                              fillTableComboBox();
                              fillFieldComboBox();
                              DB_TABLE_NAME.addItemListener(listener);
                          }
                      }
                  };
            DB_TABLE_NAME.addItemListener(listener);
            jbInit();
            tableCheck.setSelected(false);
            tableCheck_actionPerformed(null);
            pack();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void fillComponent(SQLFieldList columns, ResultSet rs)
          throws java.sql.SQLException {
        super.fillComponent(columns, rs);

        if (rs.getString("DB_TABLE_NAME").startsWith("#")) {
            tableCheck.setSelected(true);
        }
        else {
            tableCheck.setSelected(false);
        }
        tableCheck_actionPerformed(null);

        tableCheck.setEnabled(false);
        DB_FIELD_NAME.setEnabled(false);
        DB_TABLE_NAME.setEnabled(false);

        DB_TABLE_NAME.removeItemListener(listener);
        DB_FIELD_NAME.removeAllItems();
        DB_TABLE_NAME.removeAllItems();
        DB_FIELD_NAME.addItem(rs.getString("DB_FIELD_NAME"));
        DB_TABLE_NAME.addItem(rs.getString("DB_TABLE_NAME"));
        DB_TABLE_NAME.setSelectedIndex(DB_TABLE_NAME.getItemCount() - 1);
        DB_FIELD_NAME.setSelectedIndex(DB_FIELD_NAME.getItemCount() - 1);
    }


    void tableCheck_actionPerformed(ActionEvent e) {
        DB_TABLE_NAME.removeItemListener(listener);

        if (tableCheck.isSelected()) {
            DB_TABLE_NAME.removeAllItems();
            DB_FIELD_NAME.removeAllItems();
            DB_TABLE_NAME.setEditable(true);
            DB_FIELD_NAME.setEditable(true);
        }
        else {
            DB_TABLE_NAME.setEditable(false);
            DB_FIELD_NAME.setEditable(false);
            fillTableComboBox();
            fillFieldComboBox();
            DB_TABLE_NAME.addItemListener(listener);
        }
    }


    /**
     * Remplissage de la comboBox des noms physique des champs
     */
    private void fillFieldComboBox() {
        String selectedField = (String)DB_FIELD_NAME.getSelectedItem();
        DB_FIELD_NAME.removeAllItems();
        if (DB_TABLE_NAME.getSelectedItem() == null) {
            return;
        }

        ConnectionManager conMan = null;
        Connection con = null;
        try {
            conMan = Dependency.getConnectionManager();
            con = conMan.getConnection();
            SQLFieldList fieldList =
                  new SQLFieldList(DB_TABLE_NAME.getSelectedItem().toString(), con);
            for (Iterator iter = fieldList.getSortedDBFieldNameList().iterator();
                 iter.hasNext();) {
                String dbFieldName = (String)iter.next();
                DB_FIELD_NAME.addItem(dbFieldName);
            }
            DB_FIELD_NAME.setSelectedItem(selectedField);
        }
        catch (SQLException sqle) {
            sqle.printStackTrace();
            ErrorDialog.show(this, "Erreur SQL", sqle);
        }
        finally {
            try {
                conMan.releaseConnection(con);
            }
            catch (SQLException sqle1) {
                sqle1.printStackTrace();
                ErrorDialog.show(this, "Erreur de libération de la connection", sqle1);
            }
        }
    }


    /**
     * Remplissage de la comboBox des noms de table
     */
    private void fillTableComboBox() {
        String selectedTableName = (String)DB_TABLE_NAME.getSelectedItem();
        DB_TABLE_NAME.removeAllItems();

        java.util.List listeAllTable = new ArrayList();
        try {
            listeAllTable = Dependency.getTableHome().getAllObjects();
            Collections.sort(listeAllTable, new TableReferenceComparator(1));
            int cpt = 0;
            do {
                Table table =
                      (Table)((Reference)listeAllTable.get(cpt)).getLoadedObject();
                if ("".equals(Dependency.getApplication())) {
                    DB_TABLE_NAME.addItem(table.getDBTableName());
                }
                else {
                    if (table.getApplication().equals(Dependency.getApplication())) {
                        DB_TABLE_NAME.addItem(table.getDBTableName());
                    }
                }
                cpt++;
            }
            while (cpt < listeAllTable.size());
            DB_TABLE_NAME.setSelectedItem(selectedTableName);
            DB_TABLE_NAME.setRenderer(new DBTableNameRenderer(Dependency.getTableHome()));
        }
        catch (PersistenceException pe) {
            pe.printStackTrace();
            ErrorDialog.show(this, "Erreur de la couche de persistence", pe);
        }
    }


    /**
     * DOCUMENT ME!
     *
     * @throws Exception Description of Exception
     */
    private void jbInit() throws Exception {
        this.setClosable(true);
        this.setSelected(true);
        this.setIconifiable(true);
        this.setResizable(true);
        this.setTitle("Détail de la table");
        this.getContentPane().setBackground(Color.lightGray);
        this.getContentPane().setLayout(borderLayout1);
        nomTable.setText("Nom logique de la table");
        nomLogique.setText("Nom physique du champ");
        label.setText("Libellé du champ");
        mainPanel.setLayout(gridBagLayout1);
        tableCheck.setActionCommand("tableCheck");
        tableCheck.setHorizontalTextPosition(SwingConstants.LEFT);
        tableCheck.setText("table temporaire");
        tableCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent event) {
                tableCheck_actionPerformed(event);
            }
        });
        this.getContentPane().add(detailButtonsPanel, BorderLayout.SOUTH);
        this.getContentPane().add(mainPanel, BorderLayout.CENTER);
        mainPanel.add(DB_TABLE_NAME,
                      new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER,
                                             GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 10), 0, 0));
        mainPanel.add(nomTable,
                      new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                                             GridBagConstraints.NONE, new Insets(0, 5, 0, 0), 0, 0));
        mainPanel.add(nomLogique,
                      new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                                             GridBagConstraints.NONE, new Insets(0, 5, 0, 0), 0, 0));
        mainPanel.add(label,
                      new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
                                             GridBagConstraints.NONE, new Insets(7, 5, 0, 0), 0, 0));
        mainPanel.add(FIELD_LABEL,
                      new GridBagConstraints(1, 3, 1, 1, 1.0, 1.0, GridBagConstraints.NORTH,
                                             GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 10), 0, 0));
        mainPanel.add(DB_FIELD_NAME,
                      new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER,
                                             GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 10), 0, 0));
        mainPanel.add(tableCheck,
                      new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                                             GridBagConstraints.NONE, new Insets(0, 5, 0, 0), 0, 0));
        this.setSize(420, 200);
    }
}
