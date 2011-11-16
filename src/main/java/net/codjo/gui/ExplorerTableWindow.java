/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.gui;
import net.codjo.gui.model.TableReferenceComparator;
import net.codjo.gui.renderer.DBFieldNameRenderer;
import net.codjo.gui.renderer.FieldNameRenderer;
import net.codjo.gui.toolkit.util.ErrorDialog;
import net.codjo.model.Table;
import net.codjo.persistent.Reference;
import net.codjo.utils.ConnectionManager;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeSelectionModel;
/**
 * Explorateur de tables BD.
 *
 * @version $Revision: 1.8 $
 */
public class ExplorerTableWindow extends javax.swing.JInternalFrame {
    private static List listTable;
    private static JTextArea textArea = new JTextArea();
    private InternalListener listener = new InternalListener();
    private JPanel choix = new JPanel();
    private JComboBox comboChoice = new JComboBox();
    private String displayFilter = "physique";
    private TableReferenceComparator comparator =
          new TableReferenceComparator(TableReferenceComparator.COMPARE_BY_DB_TABLE_NAME);
    BorderLayout borderLayout1 = new BorderLayout();
    private JTree tree = null;


    /**
     * Constructor for the AdministrationWindow object
     *
     * @throws Exception Description of Exception
     */
    public ExplorerTableWindow() throws Exception {
        super("Explorateur des tables", true, true, false, true);

        listTable = Dependency.getTableHome().getAllObjects();
        Collections.sort(listTable, comparator);

        tree = new JTree(createNodes(displayFilter));
        jbInit();
        comboChoice.addActionListener(listener);
        pack();
    }


    /**
     * Init GUI. construit le tree avec la valeur "physique" par defaut
     *
     * @throws Exception Description of Exception
     */
    private void jbInit() throws Exception {
        comboChoice.addItem("Par Nom Physique");
        comboChoice.addItem("Par Nom Logique");
        treeConstructor("physique");
    }


    /**
     * Construit le tree de navigation avec un parametre d'affichage
     *
     * @param choixAffiche Description of the Parameter
     *
     * @throws Exception Description of the Exception
     */
    private void treeConstructor(String choixAffiche)
          throws Exception {
        final String variable = choixAffiche;
        Border border1 = BorderFactory.createEtchedBorder(Color.white, new Color(134, 134, 134));
        TitledBorder titledBorder1 = new TitledBorder(border1, "Type d'affichage");
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.putClientProperty("JTree.lineStyle", "Angled");
        tree.setShowsRootHandles(true);
        tree.setCellRenderer(new ExplorerTableRenderer());

        //Enable tool tips.
        ToolTipManager.sharedInstance().registerComponent(tree);

        setFrameIcon(UIManager.getIcon("dbExplorer.FreeTable"));
        this.setEnabled(true);
        this.setPreferredSize(new Dimension(340, 630));
        //Create the scroll pane and add the tree to it.
        JScrollPane scrollPane = new JScrollPane(tree);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        //Create a text field.
        textArea.setEditable(false);
        //Add to a split pane.
        JSplitPane splitPane =
              new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollPane, textArea);
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerSize(10);
        splitPane.setDividerLocation(510);

        JPanel panel = new JPanel(new BorderLayout());
        choix.setBorder(titledBorder1);
        choix.setLayout(borderLayout1);
        panel.add(splitPane, BorderLayout.CENTER);
        panel.add(choix, BorderLayout.NORTH);
        choix.add(comboChoice, BorderLayout.CENTER);

        this.getContentPane().add(panel, null);
        setContentPane(panel);

        tree.addMouseListener(getMouseListener(variable));
// rafraichit l'affichage!
        this.setSize(this.getSize());
    }


    private MouseListener getMouseListener(final String variable) {
        return new java.awt.event.MouseAdapter() {
            /**
             * Affiche les caracteristiques du champ ou de la table sélectionné par
             * un click.
             *
             * @param evt Evenement de la souris.
             */
            public void mousePressed(MouseEvent evt) {
                DefaultMutableTreeNode nodeInfo =
                      (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
                int selRow = tree.getRowForLocation(evt.getX(), evt.getY());
                if (selRow != -1) {
                    if (evt.getClickCount() == 1) {
                        if (nodeInfo.isLeaf()) {
                            try {
                                if ("logique".equals(variable)) {
                                    textArea.setText("Nom Logique : "
                                                     + nodeInfo.getUserObject().toString() + '\n'
                                                     + "Nom Physique : "
                                                     + findDBFieldName(findDBTableName(
                                          nodeInfo.getParent().toString()),
                                                                       (String)nodeInfo.getUserObject()
                                                                             .toString()));
                                }
                                else {
                                    textArea.setText("Nom physique : "
                                                     + nodeInfo.getUserObject().toString() + '\n'
                                                     + "Nom logique : "
                                                     + findFieldName(
                                          nodeInfo.getParent().toString(),
                                          nodeInfo.getUserObject().toString()));
                                }
                            }
                            catch (Exception es) {
                                ErrorDialog.show(ExplorerTableWindow.this,
                                                 "Erreur SQL", es);
                            }
                        }
                        else {
                            try {
                                if ("logique".equals(variable)) {
                                    textArea.setText("Nom Logique : "
                                                     + nodeInfo.getUserObject().toString() + '\n'
                                                     + "Nom Physique : "
                                                     + findDBTableName(
                                          nodeInfo.getUserObject().toString()));
                                }
                                else {
                                    textArea.setText("Nom physique : "
                                                     + nodeInfo.getUserObject().toString() + '\n'
                                                     + "Nom logique : "
                                                     + findTableName(
                                          nodeInfo.getUserObject().toString()));
                                }
                            }
                            catch (Exception pe) {
                                ErrorDialog.show(ExplorerTableWindow.this,
                                                 "Erreur de Persistence", pe);
                            }
                        }
                    }
                }
            }
        };
    }


    /**
     * Description of the Method
     *
     * @param tableDisplayFilter Description of the Parameter
     *
     * @return Description of the Return Value
     *
     * @throws Exception Description of the Exception
     */
    private DefaultMutableTreeNode createNodes(String tableDisplayFilter)
          throws Exception {
        DefaultMutableTreeNode top = new DefaultMutableTreeNode("Liste des tables");
        Connection con = null;
        ConnectionManager connectManager = null;
        String dBTableName;
        String dBFieldName = "";
        Map labelMap = null;
        try {
            connectManager = Dependency.getConnectionManager();
            con = connectManager.getConnection();
            //Boucle sur les tables

            for (int cptTable = 0; cptTable < listTable.size(); cptTable++) {
                Table table = (Table)((Reference)listTable.get(cptTable)).getObject();
                // pour ne pas prendre en compte les tables tempo
                if (!table.getDBTableName().startsWith("#")) {

                    if ("logique".equals(tableDisplayFilter)) {
                        dBTableName = table.getTableName();
                        labelMap = findFieldLabel(table.getDBTableName(), con);
                    }
                    else {
                        dBTableName = table.getDBTableName();
                    }

                    DefaultMutableTreeNode tableNode =
                          new DefaultMutableTreeNode(dBTableName);
                    top.add(tableNode);

                    List fieldList = new ArrayList(table.getAllColumns().keySet());
                    Collections.sort(fieldList);

                    for (int cptField = 0; cptField < fieldList.size(); cptField++) {
                        String dbname = (String)fieldList.get(cptField);
                        if ("logique".equals(tableDisplayFilter)) {
                            try {
//                        dBFieldName = findFieldLabel(table.getDBTableName(), (String) fieldList.get(cptField));
                                if (labelMap.containsKey(dbname)) {
                                    dBFieldName = (String)labelMap.get(dbname);
                                }
                                else {
                                    dBFieldName = dbname;
                                }
                            }
                            catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                        else {
                            dBFieldName = dbname;
                        }
                        tableNode.add(new DefaultMutableTreeNode(dBFieldName));
                    }
                }
            }
        }
        catch (Exception ex) {
            //Pb de parametrage de l'affichage des tables
            ex.printStackTrace();
        }
        finally {
            connectManager.releaseConnection(con);
        }
        return top;
    }


    /**
     * Retourne le nom logique (label) d'une table.
     *
     * @param dbTableName Nom physique de la table.
     *
     * @return Le nom logique de la table.
     *
     * @throws Exception Description of the Exception
     */
    private static String findTableName(String dbTableName)
          throws Exception {
        String tableName = "";
        String dbTabName;

        int cpt = 0;
        do {
            dbTabName =
                  ((Table)((Reference)listTable.get(cpt)).getObject()).getDBTableName();
            if (dbTabName.equals(dbTableName)) {
                tableName =
                      ((Table)((Reference)listTable.get(cpt)).getObject()).getTableName();
            }
            cpt++;
        }
        while ((!dbTabName.equals(dbTableName)) && (cpt < listTable.size()));

        return tableName;
    }


    /**
     * Cherche le nom physique d'une Table
     *
     * @param tableName Description of the Parameter
     *
     * @return Description of the Return Value
     *
     * @throws Exception Description of the Exception
     */
    private static String findDBTableName(String tableName)
          throws Exception {
        String dbtableName = "";
        String tabName;

        int cpt = 0;
        do {
            tabName = ((Table)((Reference)listTable.get(cpt)).getObject()).getTableName();
            if (tabName.equals(tableName)) {
                dbtableName =
                      ((Table)((Reference)listTable.get(cpt)).getObject()).getDBTableName();
            }
            cpt++;
        }
        while ((!tabName.equals(tableName)) && (cpt < listTable.size()));

        return dbtableName;
    }


    /**
     * Cherche le nom logique d'un champ
     *
     * @param dbTableName Description of the Parameter
     * @param dbFieldName Description of the Parameter
     *
     * @return Description of the Return Value
     *
     * @throws SQLException Description of the Exception
     */
    private String findFieldName(String dbTableName, String dbFieldName)
          throws SQLException {
        Connection con = null;
        String fieldName = "";
        Statement stmt = null;
        Map traductTable =
              FieldNameRenderer.loadTraducTable(Dependency.getConnectionManager(),
                                                dbTableName);

        try {
            if (traductTable.containsKey(dbFieldName)) {
                fieldName = (String)traductTable.get(dbFieldName);
            }
            else {
                fieldName = dbFieldName;
            }
        }
        finally {
            Dependency.getConnectionManager().releaseConnection(con, stmt);
        }
        return fieldName;
    }


    /**
     * Cherche le nom physique d'un champ
     *
     * @param dbTableName Description of the Parameter
     * @param fieldName   Description of the Parameter
     *
     * @return Description of the Return Value
     *
     * @throws SQLException Description of the Exception
     */
    private String findDBFieldName(String dbTableName, String fieldName)
          throws SQLException {
        Connection con = null;
        String dbfieldName = "";
        Statement stmt = null;
        Map traductTable =
              DBFieldNameRenderer.loadDBName(Dependency.getConnectionManager(), dbTableName);
        try {
            if (traductTable.containsKey(fieldName)) {
                dbfieldName = (String)traductTable.get(fieldName);
            }
            else {
                dbfieldName = fieldName;
            }
        }
        finally {
            Dependency.getConnectionManager().releaseConnection(con, stmt);
        }
        return dbfieldName;
    }


    /**
     * Retourne les noms logiques pour la construction de l'arbre
     *
     * @param dbTableName Description of the Parameter
     * @param con         Description of the Parameter
     *
     * @return Description of the Return Value
     *
     * @throws SQLException Description of the Exception
     */
    private Map findFieldLabel(String dbTableName, Connection con)
          throws SQLException {
        Statement stmt = null;
        Map fieldMap = new HashMap();
        stmt = con.createStatement();
        String query =
              "select DB_FIELD_NAME,FIELD_LABEL from PM_FIELD_LABEL "
              + " where DB_TABLE_NAME='" + dbTableName + "'";
        ResultSet rs = stmt.executeQuery(query);
        while (rs.next()) {
            fieldMap.put(rs.getString("DB_FIELD_NAME"), rs.getString("FIELD_LABEL"));
        }
        return fieldMap;
    }


    /**
     * Renderer pour les tables de l'arbre (feuille).
     *
     * @author $Author: acharif $
     * @version $Revision: 1.8 $
     */
    private static class ExplorerTableRenderer extends DefaultTreeCellRenderer {
        private Icon freeTable;


        /**
         * Constructor for the MyRenderer object
         */
        ExplorerTableRenderer() {
            freeTable = UIManager.getIcon("dbExplorer.FreeTable");
        }


        /**
         * Gets the TreeCellRendererComponent attribute of the MyRenderer object
         *
         * @param tree     -
         * @param value    -
         * @param sel      -
         * @param expanded -
         * @param leaf     -
         * @param row      -
         * @param hasFocus -
         *
         * @return The TreeCellRendererComponent value
         */
        public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                      boolean sel,
                                                      boolean expanded,
                                                      boolean leaf,
                                                      int row,
                                                      boolean hasFocus) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row,
                                               hasFocus);

            if (leaf) {
                try {
                    setIcon(freeTable);
                    setToolTipText(node.getUserObject().toString());
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            else {
                try {
                    setToolTipText(findTableName(node.getUserObject().toString()));
                }
                catch (Exception pe) {
                    ErrorDialog.show(null, "Erreur de Persistence", pe);
                }
            }
            return this;
        }
    }

    /**
     * Listner sur le comboBox qui affecte la variable displayFilter à logique ou physique et reconstruit le
     * tree.
     *
     * @author BCHIR
     */
    class InternalListener implements ActionListener {
        /**
         * Description of the Method
         *
         * @param parm1 Description of the Parameter
         */
        public void actionPerformed(ActionEvent parm1) {
            String comboValue = (String)comboChoice.getSelectedItem();
            try {
                if ("Par Nom Logique".equals(comboValue)) {
                    Collections.sort(listTable,
                                     new TableReferenceComparator(
                                           TableReferenceComparator.COMPARE_BY_TABLE_NAME));

                    tree = new JTree(createNodes("logique"));

                    treeConstructor("logique");
                }
                else {
                    Collections.sort(listTable,
                                     new TableReferenceComparator(
                                           TableReferenceComparator.COMPARE_BY_DB_TABLE_NAME));
                    tree = new JTree(createNodes("physique"));

                    treeConstructor("physique");
                }
            }
            catch (Exception pe) {
                ErrorDialog.show(null, "Erreur de Persistence", pe);
            }
        }
    }
}
