/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.gui.imports;
import net.codjo.model.PeriodHome;
import net.codjo.operation.imports.ImportBehaviorHome;
import net.codjo.persistent.PersistenceException;
import net.codjo.utils.sql.DbToolBar;
import net.codjo.utils.sql.GenericTable;
import net.codjo.utils.sql.PersistentToolBar;
import net.codjo.utils.sql.event.DbChangeListener;

// Java
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
/**
 * Affichage en Liste des imports.
 *
 * @author $Author: spinae $
 * @version $Revision: 1.2 $
 */
class ParamImportWindow extends javax.swing.JInternalFrame {
    JButton addButton = new JButton();
    JToolBar bottomToolBar = new JToolBar();
    JButton closeButton = new JButton();
    DbToolBar dbToolBar;
    JButton deleteButton = new JButton();
    JDesktopPane gexPane;
    GenericTable importTable;
    JPopupMenu popupMenu = new JPopupMenu();
    JScrollPane tableScrollPane = new JScrollPane();
    JLabel titleLabel = new JLabel();

    // GUI
    JPanel topPanel = new JPanel();

    /**
     * Constructor for the ParamImportWindow object
     *
     * @param dp Description of Parameter
     * @param periodHome Description of the Parameter
     * @param ibh Description of the Parameter
     *
     * @exception SQLException -
     * @exception PersistenceException -
     */
    ParamImportWindow(JDesktopPane dp, PeriodHome periodHome,
        ImportBehaviorHome ibh) throws SQLException, PersistenceException {
        super("Liste des imports définis", true, true, false, true);
        gexPane = dp;
        jbInit();

        dbToolBar.putDefaultValueForDetail("periodHome", periodHome);
        dbToolBar.putDefaultValueForDetail("importHome", ibh);
        DbChangeListener l = ibh.getDbChangeListener();
        try {
            dbToolBar.add(l);
        }
        catch (java.util.TooManyListenersException ex) {
            // Cas impossible
        }
    }

    /**
     * Init GUI.
     *
     * @exception PersistenceException Description of Exception
     * @exception SQLException Description of Exception
     */
    private void jbInit() throws PersistenceException, SQLException {
        importTable =
            new GenericTable(net.codjo.gui.Dependency.getTableHome().getTable("PM_IMPORT_SETTINGS"),
                true);
        // Init Frame
        setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout(0, 0));

        // Top
        topPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        titleLabel.setText("Visualisation des données : "
            + importTable.getNumberOfFirstRow() + " à "
            + importTable.getNumberOfLastRow() + " sur " + importTable.getNumberOfRows()
            + " enregistrements");
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 12));
        topPanel.add(titleLabel);

        // Center
        tableScrollPane.setBorder(BorderFactory.createEtchedBorder());
        importTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        tableScrollPane.getViewport().add(importTable);
        dbToolBar =
            new PersistentToolBar(gexPane, importTable, this, "net.codjo.gui.imports");
        dbToolBar.setConfirmMsg("Cette action supprimera toutes les associations\n"
            + "champ à champ liées.\n" + "Etes-vous sûr ?");

        // Assemblage
        getContentPane().add(topPanel, BorderLayout.NORTH);
        getContentPane().add(tableScrollPane, BorderLayout.CENTER);
        getContentPane().add(dbToolBar, BorderLayout.SOUTH);

        setSize(800, 500);
    }
}
