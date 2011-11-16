/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.gui;
import net.codjo.utils.sql.DbToolBar;
import net.codjo.utils.sql.GenericTable;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.TableModelListener;
/**
 * Affiche une fenêtre comportant une générique table (écran liste).
 *
 * @version $Revision: 1.3 $
 *
 *
 */
public class TableViewWindow extends JInternalFrame {
    private JPanel topPanel = new JPanel();
    private JLabel titleLabel = new JLabel();
    private JScrollPane tableScrollPane = new JScrollPane();
    private JDesktopPane gexPane;
    private GenericTable genericTable;
    private String whereClauseForFind;
    private ToolBarBuilder toolBarBuilder;

    /**
     * Constructeur.
     *
     * @param dp Le desktopPane.
     * @param gt La table qui "dirige" l'action.
     * @param whereClause La clause where par défaut pour le requêteur.
     * @param toolBar Le constructeur de la toolBar.
     */
    public TableViewWindow(JDesktopPane dp, GenericTable gt, String whereClause,
        ToolBarBuilder toolBar) {
        super("Table " + gt.getTable(), true, true, true, true);
        gexPane = dp;
        genericTable = gt;
        whereClauseForFind = whereClause;
        toolBarBuilder = toolBar;
        jbInit();
    }

    /**
     * Init GUI.
     */
    private void jbInit() {
        // Init Frame
        setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout(0, 0));
        setSize(700, 500);

        // Top
        topPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        titleLabel.setText("Visualisation des données : "
            + genericTable.getNumberOfFirstRow() + " à "
            + genericTable.getNumberOfLastRow() + " sur "
            + genericTable.getNumberOfRows() + " enregistrements");
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 12));
        topPanel.add(titleLabel);

        // Center
        genericTable.getModel().addTableModelListener(new TableModelListener() {
                /**
                 * DOCUMENT ME!
                 *
                 * @param evt Description of Parameter
                 */
                public void tableChanged(javax.swing.event.TableModelEvent evt) {
                    titleLabel.setText("Visualisation des données : "
                        + genericTable.getNumberOfFirstRow() + " à "
                        + genericTable.getNumberOfLastRow() + " sur "
                        + genericTable.getNumberOfRows() + " enregistrements");
                }
            });
        tableScrollPane.setBorder(BorderFactory.createEtchedBorder());
        tableScrollPane.getViewport().add(genericTable);

        // Bottom
        DbToolBar bottomToolBar =
            toolBarBuilder.getToolBar(gexPane, this, genericTable, whereClauseForFind,
                whereClauseForFind, true);

        // Assemblage
        getContentPane().add(topPanel, BorderLayout.NORTH);
        getContentPane().add(tableScrollPane, BorderLayout.CENTER);
        getContentPane().add(bottomToolBar, BorderLayout.SOUTH);
    }
}
