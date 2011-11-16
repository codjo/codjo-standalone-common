/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.gui;
import net.codjo.gui.toolkit.util.ErrorDialog;
import net.codjo.model.Table;
import net.codjo.utils.GuiUtil;
import net.codjo.utils.sql.GenericTable;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JDesktopPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
/**
 * Explorateur de tables BD.
 *
 * @version $Revision: 1.4 $
 */
public class ExplorerDataWindow extends javax.swing.JInternalFrame {
    private static final String EMPTY_STRING = "";
    JPanel filterPanel = new JPanel();
    Border border1;
    TitledBorder titledBorder1;
    JPanel explorerPanel = new JPanel();
    BorderLayout borderLayout1 = new BorderLayout();
    GridBagLayout gridBagLayout1 = new GridBagLayout();
    BorderLayout borderLayout2 = new BorderLayout();
    private JDesktopPane gexPane;
    private List filters;
    private ExplorerTreeBuilder explorer;
    private ExplorerRecordAccessFilter recordAccessFilter;
    private ToolBarBuilder toolBarBuilder;


    /**
     * Constructeur.
     *
     * @param dp           Le destopPane.
     * @param filters      Liste des filtres d'affichage.
     * @param exp          L'explorateur permettant de récupérer le JTree spécifique à l'application.
     * @param recordFilter Filtre sur la visibilité des enregistrements des tables partagées entre plusieurs
     *                     applications (si non renseigné on prend par défaut le DefaultExplorerRecordAccessFilter
     *                     qui renvoie une chaine vide comme clause where obligatoire).
     * @param toolBar      Le constructeur de la toolBar.
     *
     * @throws IllegalArgumentException TODO
     */
    public ExplorerDataWindow(JDesktopPane dp, List filters, ExplorerTreeBuilder exp,
                              ExplorerRecordAccessFilter recordFilter, ToolBarBuilder toolBar) {
        super("Explorateur des données", true, true, false, true);
        if (dp == null || filters == null || toolBar == null) {
            throw new IllegalArgumentException("Un parametre n'est pas renseigné !");
        }
        this.gexPane = dp;
        this.filters = filters;
        toolBarBuilder = toolBar;
        explorer = exp;

        if (recordFilter != null) {
            recordAccessFilter = recordFilter;
        }
        else {
            recordAccessFilter = new DefaultExplorerRecordAccessFilter();
        }

        initFilters();
        jbInit();
        pack();
    }


    /**
     * Affiche la table choisie par un double-click en tenant compte des filtres d'affichage.
     *
     * @param table Table à afficher.
     */
    public void execute(Table table) {
        try {
            GenericTable geneTable =
                  new GenericTable(table, true, buildWhereClause(table));
            TableViewWindow tvw =
                  new TableViewWindow(gexPane, geneTable, buildWhereClause(table),
                                      toolBarBuilder);
            gexPane.add(tvw);
            GuiUtil.centerWindow(tvw);
            tvw.setVisible(true);
            try {
                tvw.setSelected(true);
            }
            catch (java.beans.PropertyVetoException ex) {
                ex.printStackTrace();
            }
        }
        catch (Exception es) {
            ErrorDialog.show(ExplorerDataWindow.this, "Impossible d'afficher la table", es);
            es.printStackTrace();
        }
    }


    /**
     * Construit la clause where pour le filtrage des données de la table.
     *
     * @param table La Table à afficher.
     *
     * @return La String de la clause where.
     */
    private String buildWhereClause(Table table) {
        String whereClause = EMPTY_STRING;
        if (filters != null) {
            String filterClause;
            for (int i = 0; i < filters.size(); i++) {
                if (table.containsColumn(
                      ((ExplorerFilter)filters.get(i)).getFilterColumnName())) {
                    filterClause = ((ExplorerFilter)filters.get(i)).getWhereClause();
                    if (!filterClause.equals(EMPTY_STRING)) {
                        if (whereClause.equals(EMPTY_STRING)) {
                            whereClause += " where " + table.getDBTableName() + "."
                                           + filterClause;
                        }
                        else {
                            whereClause += " and " + table.getDBTableName() + "."
                                           + filterClause;
                        }
                    }
                }
            }
        }
        whereClause += buildMandatoryWhereClause(whereClause, table);
        return whereClause;
    }


    /**
     * Initialise les filtres d'affichage des tables. Cette méthode ajoute les filtres (Jlabel + JComponent)
     * dans le filterPanel.
     */
    private void initFilters() {
        filterPanel.setLayout(gridBagLayout1);
        int gridy;
        gridy = 0;
        for (int i = 0; i < filters.size(); i++) {
            filterPanel.add(((ExplorerFilter)filters.get(i)).getLabel(),
                            new GridBagConstraints(0, gridy, 2, 1, 0.0, 0.0, GridBagConstraints.WEST,
                                                   GridBagConstraints.NONE, new Insets(0, 5, 5, 5), 0, 0));
            filterPanel.add(((ExplorerFilter)filters.get(i)).getComponent(),
                            new GridBagConstraints(1, gridy, 2, 1, 1.0, 0.0, GridBagConstraints.EAST,
                                                   GridBagConstraints.HORIZONTAL,
                                                   new Insets(0, 80, 5, 5),
                                                   0,
                                                   0));
            gridy++;
        }
    }


    /**
     * Init GUI.
     */
    private void jbInit() {
        final JTree tree = explorer.getTree();
        border1 = BorderFactory.createEtchedBorder(Color.white, new Color(134, 134, 134));
        titledBorder1 = new TitledBorder(border1, "Filtres d'affichage");
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.putClientProperty("JTree.lineStyle", "Angled");
        tree.setShowsRootHandles(true);

        //Enable tool tips.
        ToolTipManager.sharedInstance().registerComponent(tree);

        //Create the scroll pane and add the tree to it.
        setFrameIcon(UIManager.getIcon("DataExplorer.open"));
        this.setPreferredSize(new Dimension(325, 600));
        this.getContentPane().setBackground(Color.lightGray);
        this.getContentPane().setLayout(borderLayout2);

        MouseListener ml =
              new java.awt.event.MouseAdapter() {
                  /**
                   * Determine la table sélectionnée par un double click.
                   *
                   * @param evt Evenement de la souris.
                   */
                  public void mousePressed(MouseEvent evt) {
                      DefaultMutableTreeNode nodeInfo =
                            (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
                      int selRow = tree.getRowForLocation(evt.getX(), evt.getY());
                      if (selRow != -1) {
                          if (evt.getClickCount() == 2) {
                              Table table = (Table)nodeInfo.getUserObject();
                              execute(table);
                          }
                      }
                  }
              };
        tree.addMouseListener(ml);
        filterPanel.setBorder(titledBorder1);
        explorerPanel.setLayout(borderLayout1);
        JScrollPane treeView = new JScrollPane(tree);
        this.getContentPane().add(explorerPanel, BorderLayout.CENTER);
        explorerPanel.add(treeView, BorderLayout.CENTER);
        this.getContentPane().add(filterPanel, BorderLayout.NORTH);
    }


    /**
     * Construit (si elle existe) la clause where obligatoire pour le filtrage des données des tables
     * partagées entre plusieurs applcations.
     *
     * @param whereClause La clause where initiale (filtres de l'explorateur).
     * @param table       La table.
     *
     * @return La clause where initiale + la clause where obligatoire.
     */
    private String buildMandatoryWhereClause(String whereClause, Table table) {
        String mandatoryWhereClause = EMPTY_STRING;
        if (recordAccessFilter != null) {
            String clause = recordAccessFilter.getMandatoryWhereClause(table);
            if (!clause.equals(EMPTY_STRING)) {
                if (whereClause.equals(EMPTY_STRING)) {
                    mandatoryWhereClause = " where " + clause;
                }
                else {
                    mandatoryWhereClause = " and " + clause;
                }
            }
        }
        return mandatoryWhereClause;
    }
}
