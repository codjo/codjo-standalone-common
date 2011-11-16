/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.gui;
import net.codjo.model.TableHome;
import net.codjo.persistent.PersistenceException;
import net.codjo.utils.ConnectionManager;
import net.codjo.utils.sql.GenericTable;
import net.codjo.utils.sql.PersistentToolBar;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.JDesktopPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelListener;
/**
 * Affiche la fenêtre comportant la générique table (écran liste).
 *
 * @version $Revision: 1.3 $
 *
 *
 */
public class FieldLabelWindow extends javax.swing.JInternalFrame {
    // GUI
    JPanel topPanel = new JPanel();
    JLabel titleLabel = new JLabel();
    JScrollPane tableScrollPane = new JScrollPane();
    GenericTable fieldLabelTable;
    JDesktopPane gexPane;
    ConnectionManager connectionManager;
    TableHome tableHome;

    // Actions DB
    PersistentToolBar bottomToolBar;

    /**
     * Constructor for the FieldLabelWindow object
     *
     * @param dp Description of Parameter
     * @param th Description of Parameter
     * @param conMan Description of Parameter
     *
     * @exception SQLException -
     * @exception PersistenceException -
     */
    public FieldLabelWindow(JDesktopPane dp, TableHome th, ConnectionManager conMan)
            throws SQLException, PersistenceException {
        super("Liste des noms des champs", true, true, false, true);
        gexPane = dp;
        tableHome = th;
        connectionManager = conMan;
        jbInit();
    }

    /**
     * Init GUI.
     *
     * @exception SQLException Description of Exception
     * @exception PersistenceException Description of Exception
     */
    private void jbInit() throws SQLException, PersistenceException {
        fieldLabelTable = new GenericTable(tableHome.getTable("PM_FIELD_LABEL"), true);
        // Init Frame
        setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout(0, 0));

        // Top
        topPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        titleLabel.setText("Visualisation des données : "
            + fieldLabelTable.getNumberOfFirstRow() + " à "
            + fieldLabelTable.getNumberOfLastRow() + " sur "
            + fieldLabelTable.getNumberOfRows() + " enregistrements");
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 12));
        topPanel.add(titleLabel);

        // Center
        fieldLabelTable.getModel().addTableModelListener(new TableModelListener() {
                /**
                 * DOCUMENT ME!
                 *
                 * @param evt Description of Parameter
                 */
                public void tableChanged(javax.swing.event.TableModelEvent evt) {
                    titleLabel.setText("Visualisation des données : "
                        + fieldLabelTable.getNumberOfFirstRow() + " à "
                        + fieldLabelTable.getNumberOfLastRow() + " sur "
                        + fieldLabelTable.getNumberOfRows() + " enregistrements");
                }
            });
        tableScrollPane.setBorder(BorderFactory.createEtchedBorder());
        fieldLabelTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        tableScrollPane.getViewport().add(fieldLabelTable);

        // Bottom
        bottomToolBar =
            new PersistentToolBar(gexPane, fieldLabelTable, this, "net.codjo.gui");

        // Assemblage
        getContentPane().add(topPanel, BorderLayout.NORTH);
        getContentPane().add(tableScrollPane, BorderLayout.CENTER);
        getContentPane().add(bottomToolBar, BorderLayout.SOUTH);

        setSize(726, 550);
    }
}
