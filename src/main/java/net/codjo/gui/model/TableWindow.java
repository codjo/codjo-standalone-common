/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.gui.model;
import net.codjo.model.TableHome;
import net.codjo.persistent.PersistenceException;
import net.codjo.utils.ConnectionManager;
import net.codjo.utils.sql.GenericTable;
import net.codjo.utils.sql.PersistentToolBar;
import net.codjo.utils.sql.event.DbChangeListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JDesktopPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelListener;
/**
 * Affiche la fenêtre comportant la générique table (écran liste).
 *
 * @author $Author: blazart $
 * @version $Revision: 1.3 $
 *
 *
 */
public class TableWindow extends javax.swing.JInternalFrame {
    Border borderLabel;

    // GUI
    BorderLayout borderLayout1 = new BorderLayout();
    BorderLayout borderLayout2 = new BorderLayout();
    JPanel bottomPanel = new JPanel();
    JDesktopPane gexPane;
    GridBagLayout gridBagLayout1 = new GridBagLayout();
    JScrollPane linkTableScrollPane = new JScrollPane();
    PersistentToolBar linkToolBar;
    GenericTable pmLinkTable;
    GenericTable pmTable;
    JScrollPane tableScrollPane = new JScrollPane();
    PersistentToolBar tableToolBar;
    JLabel titleLinkTable = new JLabel();
    JLabel titleTable = new JLabel();
    JPanel topPanel = new JPanel();

    // Actions DB
    private ConnectionManager connectionManager;
    private TableHome tableHome;
    private TableNameRenderer tableNameRenderer;

    /**
     * Constructeur
     *
     * @param dp Le desktopPane dans lequel sera affichee la fenetre.
     * @param th Le TableHome.
     * @param conMan Le ConnectionManager.
     * @param packageOfTableDetailWindow Le nom du package où se trouve l'écran de
     *        détail.
     * @param whereClause La clause where pour l'affichage des tables propres à
     *        l'application.
     *
     * @exception SQLException -
     * @exception PersistenceException -
     */
    public TableWindow(JDesktopPane dp, TableHome th, ConnectionManager conMan,
        String packageOfTableDetailWindow, String whereClause)
            throws SQLException, PersistenceException {
        super("Liste des tables", true, true, false, true);

        init(dp, th, conMan, packageOfTableDetailWindow, whereClause);

        linkToolBar =
            new PersistentToolBar(gexPane, pmLinkTable, this,
                TableWindow.class.getPackage().getName());
        linkToolBar.setDefaultValueforFindAction(" where DB_TABLE_NAME_ID = -1");

        jbInit();
    }

    /**
     * Init
     *
     * @param dp Le desktopPane dans lequel sera affichee la fenetre.
     * @param th Le TableHome.
     * @param conMan Le ConnectionManager.
     * @param packageOfTableDetailWindow Le nom du package où se trouve l'écran de
     *        détail.
     * @param whereClause La clause where pour l'affichage des tables propres à
     *        l'application.
     *
     * @exception SQLException -
     * @exception PersistenceException -
     */
    private void init(JDesktopPane dp, TableHome th, ConnectionManager conMan,
        String packageOfTableDetailWindow, String whereClause)
            throws SQLException, PersistenceException {
        gexPane = dp;
        connectionManager = conMan;
        tableHome = th;

        pmTable =
            new GenericTable(tableHome.getTable("PM_TABLE"), true, whereClause,
                "ORDER BY DB_TABLE_NAME");
        tableToolBar =
            new PersistentToolBar(gexPane, pmTable, this, packageOfTableDetailWindow,
                false);

        pmLinkTable =
            new GenericTable(tableHome.getTable("PM_LINK_TABLE"), true,
                "WHERE DB_TABLE_NAME_ID = -1");

        tableNameRenderer = new TableNameRenderer(tableHome);
        pmLinkTable.getColumnByDbField("LINK_DB_TABLE_NAME_ID").setCellRenderer(tableNameRenderer);

        DbChangeListener l = tableHome.getDbChangeListener();
        try {
            tableToolBar.add(l);
        }
        catch (java.util.TooManyListenersException ex) {
            // Cas impossible
        }
    }


    /**
     * Init GUI.
     *
     * @exception SQLException Description of Exception
     * @exception PersistenceException Description of Exception
     */
    private void jbInit() throws SQLException, PersistenceException {
        // Init Frame
        borderLabel = BorderFactory.createEmptyBorder(5, 5, 5, 0);
        setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
        this.getContentPane().setBackground(Color.lightGray);
        getContentPane().setLayout(gridBagLayout1);

        // Top
        titleLinkTable.setText("Visualisation des données : "
            + pmLinkTable.getNumberOfFirstRow() + " à "
            + pmLinkTable.getNumberOfLastRow() + " sur " + pmLinkTable.getNumberOfRows()
            + " enregistrements");
        titleLinkTable.setFont(new Font("Dialog", Font.BOLD, 12));
        titleLinkTable.setBorder(borderLabel);
        pmLinkTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        linkTableScrollPane.setBorder(BorderFactory.createEtchedBorder());

        // Center
        tableScrollPane.setBorder(BorderFactory.createEtchedBorder());
        pmTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        TableRecordingModeRenderer tableRecordingRenderer =
            new TableRecordingModeRenderer();
        pmTable.getColumnByDbField("RECORDING_MODE").setCellRenderer(tableRecordingRenderer);

        // Bottom
        // Assemblage
        bottomPanel.setBorder(BorderFactory.createEtchedBorder());
        bottomPanel.setLayout(borderLayout1);
        linkToolBar.setBorder(BorderFactory.createEtchedBorder());
        topPanel.setBorder(BorderFactory.createEtchedBorder());
        topPanel.setLayout(borderLayout2);
        titleTable.setText("Visualisation des données : " + pmTable.getNumberOfFirstRow()
            + " à " + pmTable.getNumberOfLastRow() + " sur " + pmTable.getNumberOfRows()
            + " enregistrements");
        titleTable.setFont(new Font("Dialog", Font.BOLD, 12));
        titleTable.setBorder(borderLabel);
        this.getContentPane().add(topPanel,
            new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
        topPanel.add(tableScrollPane, BorderLayout.CENTER);
        topPanel.add(titleTable, BorderLayout.NORTH);
        topPanel.add(tableToolBar, BorderLayout.SOUTH);
        this.getContentPane().add(bottomPanel,
            new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
        bottomPanel.add(linkTableScrollPane, BorderLayout.CENTER);
        bottomPanel.add(linkToolBar, BorderLayout.SOUTH);
        bottomPanel.add(titleLinkTable, BorderLayout.NORTH);
        linkTableScrollPane.getViewport().add(pmLinkTable);
        tableScrollPane.getViewport().add(pmTable);

        // Listener Table
        TableSelectionListener actions_selection = new TableSelectionListener();
        pmTable.getSelectionModel().addListSelectionListener(actions_selection);

        //Listener sur TableModel pmTable
        pmTable.getModel().addTableModelListener(new TableModelListener() {
                /**
                 * DOCUMENT ME!
                 *
                 * @param evt Description of Parameter
                 */
                public void tableChanged(javax.swing.event.TableModelEvent evt) {
                    titleTable.setText("Visualisation des données : "
                        + pmTable.getNumberOfFirstRow() + " à "
                        + pmTable.getNumberOfLastRow() + " sur "
                        + pmTable.getNumberOfRows() + " enregistrements");
                }
            });

        //Listener sur TableModel pmLinkTable
        pmLinkTable.getModel().addTableModelListener(new TableModelListener() {
                /**
                 * DOCUMENT ME!
                 *
                 * @param evt Description of Parameter
                 */
                public void tableChanged(javax.swing.event.TableModelEvent evt) {
                    titleLinkTable.setText("Visualisation des données : "
                        + pmLinkTable.getNumberOfFirstRow() + " à "
                        + pmLinkTable.getNumberOfLastRow() + " sur "
                        + pmLinkTable.getNumberOfRows() + " enregistrements");
                }
            });
        setSize(700, 500);
    }

    /**
     * Overview.
     * 
     * <p>
     * Description
     * </p>
     *
     * @author $Author: blazart $
     * @version $Revision: 1.3 $
     */
    private class TableSelectionListener implements ListSelectionListener {
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
                try {
                    pmLinkTable.reloadData("From PM_LINK_TABLE Where "
                        + "DB_TABLE_NAME_ID = -1");
                    linkToolBar.setDefaultValueforFindAction(
                        " where DB_TABLE_NAME_ID = -1");
                }
                catch (Exception exc) {
                    exc.printStackTrace();
                }
            }
            else {
                Map pk = pmTable.getKey(pmTable.getSelectedRow());
                Object id = pk.get("DB_TABLE_NAME_ID");
                linkToolBar.putDefaultValueForDetail("DB_TABLE_NAME_ID", id);
                try {
                    pmLinkTable.reloadData("From PM_LINK_TABLE Where "
                        + "DB_TABLE_NAME_ID = " + id, true);
                    linkToolBar.setDefaultValueforFindAction(" where DB_TABLE_NAME_ID = "
                        + id);
                }
                catch (Exception exc) {
                    exc.printStackTrace();
                }
            }
        }
    }
}
