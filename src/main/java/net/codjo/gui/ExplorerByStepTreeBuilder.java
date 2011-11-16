/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.gui;
import net.codjo.gui.model.TableReferenceComparator;
import net.codjo.gui.toolkit.util.ErrorDialog;
import net.codjo.model.Table;
import net.codjo.persistent.PersistenceException;
import net.codjo.persistent.Reference;
import net.codjo.profile.User;
import java.awt.Component;
import java.util.Collections;
import java.util.List;
import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
/**
 * Cette classe permet de construire un JTree propre à Penelope pour l'explorateur des données.
 *
 * @version $Revision: 1.5 $
 */
public class ExplorerByStepTreeBuilder implements ExplorerTreeBuilder {
    private JTree tree;
    private User user = null;


    /**
     * Constructeur.
     */
    public ExplorerByStepTreeBuilder() {
        initTree();
    }


    /**
     * Retourne le JTree.
     *
     * @return Le JTree.
     */
    public JTree getTree() {
        initTree();
        return tree;
    }


    /**
     * Retourne le user courant
     *
     * @return The user value
     */
    public net.codjo.profile.User getUser() {
        return user;
    }


    /**
     * Affecte user à newCurrentUser
     *
     * @param newCurrentUser The new currentUser value
     */
    public void setCurrentUser(net.codjo.profile.User newCurrentUser) {
        user = newCurrentUser;
    }


    /**
     * Initialisation du JTree.
     */
    private void initTree() {
        tree = new JTree(createNodes());
        tree.setCellRenderer(new ExplorerRenderer());
    }


    /**
     * Création du JTree.
     *
     * @return L'arborescence des Tables.
     *
     * @noinspection CallToPrintStackTrace
     */
    private DefaultMutableTreeNode createNodes() {
        DefaultMutableTreeNode top = new DefaultMutableTreeNode("Liste des tables");
        List listTable;
        try {
            listTable = Dependency.getTableHome().getAllObjects();

            Collections.sort(listTable,
                             new TableReferenceComparator(
                                   TableReferenceComparator.COMPARE_BY_TABLE_NAME));

            DefaultMutableTreeNode importTable =
                  new DefaultMutableTreeNode("Tables importées");
            DefaultMutableTreeNode translatTable =
                  new DefaultMutableTreeNode("Tables transcodées");
            DefaultMutableTreeNode treatTable =
                  new DefaultMutableTreeNode("Tables traitées");
            DefaultMutableTreeNode refTable = new DefaultMutableTreeNode("Référentiels");
            DefaultMutableTreeNode otherTable = new DefaultMutableTreeNode("Autres");

            int cpt = 0;
            do {
                Table table = (Table)((Reference)listTable.get(cpt)).getLoadedObject();
                if ("IMPORTEE".equals(table.getTableStep())) {
                    importTable.add(new DefaultMutableTreeNode(table));
                }
                else if ("TRANSCODEE".equals(table.getTableStep())) {
                    translatTable.add(new DefaultMutableTreeNode(table));
                }
                else if ("TRAITEE".equals(table.getTableStep())) {
                    treatTable.add(new DefaultMutableTreeNode(table));
                }
                else if ("REFERENTIEL".equals(table.getTableStep())) {
                    refTable.add(new DefaultMutableTreeNode(table));
                }
                else {
                    otherTable.add(new DefaultMutableTreeNode(table));
                }
                cpt++;
            }
            while (cpt < listTable.size());

            if (!importTable.children().equals(DefaultMutableTreeNode.EMPTY_ENUMERATION)) {
                top.add(importTable);
            }
            if (!translatTable.children().equals(DefaultMutableTreeNode.EMPTY_ENUMERATION)) {
                top.add(translatTable);
            }
            if (!treatTable.children().equals(DefaultMutableTreeNode.EMPTY_ENUMERATION)) {
                top.add(treatTable);
            }
            if (!refTable.children().equals(DefaultMutableTreeNode.EMPTY_ENUMERATION)) {
                top.add(refTable);
            }
            if (!otherTable.children().equals(DefaultMutableTreeNode.EMPTY_ENUMERATION)) {
                top.add(otherTable);
            }
        }
        catch (PersistenceException ex) {
            ErrorDialog.show(tree, "Impossible d'obtenir la liste des tables", ex);
            ex.printStackTrace();
        }
        return top;
    }


    /**
     * Renderer pour les tables de l'arbre (feuille).
     *
     * @author $Author: marcona $
     * @version $Revision: 1.5 $
     */
    private static class ExplorerRenderer extends DefaultTreeCellRenderer {
        Icon workTable;
        Icon finalTable;


        /**
         * Constructeur.
         */
        ExplorerRenderer() {
            workTable = UIManager.getIcon("dbExplorer.WorkTable");
            finalTable = UIManager.getIcon("dbExplorer.FinalTable");
        }


        /**
         * Retourne le renderer.
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
            // Lorsque des familles de tables sont vide, la valeur leaf
            //          est vrai. Donc pour pour faire afficher le bon icon
            //          on positionne leaf a false.
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
            if (node.getUserObject().getClass() != Table.class) {
                leaf = false;
            }

            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row,
                                               hasFocus);

            if (leaf) {
                if (isFinalTable(value)) {
                    setIcon(finalTable);
                }
                else {
                    setIcon(workTable);
                }
            }
            return this;
        }


        /**
         * Indique si la table est finale.
         *
         * @param value Tree node
         *
         * @return -
         */
        protected boolean isFinalTable(Object value) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
            Table nodeInfo = (Table)(node.getUserObject());
            return "INFOCENTRE".equals(nodeInfo.getApplication());
        }
    }
}
