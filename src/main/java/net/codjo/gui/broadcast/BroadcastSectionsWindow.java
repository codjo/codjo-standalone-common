/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.gui.broadcast;
import net.codjo.gui.toolkit.util.ErrorDialog;
import net.codjo.model.TableHome;
import net.codjo.persistent.PersistenceException;
import net.codjo.utils.ConnectionManager;
import net.codjo.utils.sql.DbToolBar;
import net.codjo.utils.sql.GenericTable;
import net.codjo.utils.sql.PersistentToolBar;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.apache.log4j.Logger;
/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.4 $
 */
public class BroadcastSectionsWindow extends JInternalFrame {
    private static final Logger LOG = Logger.getLogger(BroadcastSectionsWindow.class);
    private BorderLayout columnsBorderLayout = new BorderLayout();
    private JLabel columnsLabel = new JLabel();
    private JPanel columnsPanel = new JPanel();
    private JScrollPane columnsScrollPane = new JScrollPane();
    private GenericTable columnsTable;
    private DbToolBar columnsToolBar;
    private BorderLayout sectionBorderLayout = new BorderLayout();
    private JLabel sectionLabel = new JLabel();
    private JPanel sectionPanel = new JPanel();
    private JScrollPane sectionScrollPane = new JScrollPane();
    private GenericTable sectionTable;
    private DbToolBar sectionToolBar;
    private GridBagLayout thisGridBagLayout = new GridBagLayout();


    /**
     * Constructor
     *
     * @param connectionManager     Description of the Parameter
     * @param tableHome             Description of the Parameter
     * @param guiPreferencesManager Description of the Parameter
     *
     * @throws NullPointerException TODO
     */
    public BroadcastSectionsWindow(JDesktopPane desktop,
                                   ConnectionManager connectionManager, TableHome tableHome,
                                   GuiPreferencesManager guiPreferencesManager)
          throws SQLException, PersistenceException {
        super("Paramétrage des sections distribuées et de leurs colonnes", true, true,
              false, true);

        if ((desktop == null)
            || (connectionManager == null)
            || (tableHome == null)
            || (guiPreferencesManager == null)) {
            throw new NullPointerException();
        }

        sectionTable =
              new GenericTable(tableHome.getTable(
                    guiPreferencesManager.getSectionTableName()), true);

        columnsTable =
              new GenericTable(tableHome.getTable(
                    guiPreferencesManager.getColumnsTableName()), true,
                               "where COLUMNS_ID = -1");

        sectionToolBar =
              new PersistentToolBar(desktop, sectionTable, this,
                                    "net.codjo.gui.broadcast");
        sectionToolBar.putDefaultValueForDetail("GUI_PREFERENCES_MANAGER",
                                                guiPreferencesManager);
        sectionToolBar.putDefaultValueForDetail("CONNECTION_MANAGER", connectionManager);

        columnsToolBar =
              new PersistentToolBar(desktop, columnsTable, this,
                                    "net.codjo.gui.broadcast");
        columnsToolBar.putDefaultValueForDetail("CONNECTION_MANAGER", connectionManager);
        columnsToolBar.putDefaultValueForDetail("TABLE_HOME", tableHome);
        columnsToolBar.putDefaultValueForDetail("GUI_PREFERENCES_MANAGER",
                                                guiPreferencesManager);

        jbInit();
    }


    /**
     * Description of the Method
     */
    private void jbInit() {
        sectionLabel.setFont(new java.awt.Font("Dialog", 1, 12));
        sectionLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 0));
        sectionLabel.setText("Sections");

        columnsLabel.setFont(new java.awt.Font("Dialog", 1, 12));
        columnsLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 0));
        columnsLabel.setText("Colonnes");

        // Add components to Frame
        this.getContentPane().setLayout(thisGridBagLayout);
        this.setMinimumSize(new Dimension(100, 100));
        this.getContentPane().setBackground(Color.lightGray);

        sectionPanel.setLayout(sectionBorderLayout);
        sectionPanel.add(sectionLabel, BorderLayout.NORTH);
        sectionPanel.add(sectionScrollPane, BorderLayout.CENTER);
        sectionPanel.add(sectionToolBar, BorderLayout.SOUTH);

        columnsPanel.setLayout(columnsBorderLayout);
        columnsPanel.add(columnsLabel, BorderLayout.NORTH);
        columnsPanel.add(columnsScrollPane, BorderLayout.CENTER);
        columnsPanel.add(columnsToolBar, BorderLayout.SOUTH);

        sectionScrollPane.getViewport().add(sectionTable);
        columnsScrollPane.getViewport().add(columnsTable);

        this.getContentPane().add(sectionPanel,
                                  new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER,
                                                         GridBagConstraints.BOTH,
                                                         new Insets(0, 5, 0, 4),
                                                         -144,
                                                         -309));
        this.getContentPane().add(columnsPanel,
                                  new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER,
                                                         GridBagConstraints.BOTH,
                                                         new Insets(0, 5, 5, 4),
                                                         -144,
                                                         -309));

        // Listener securityCodeTable
        SectionSelectionListener actionsSelection = new SectionSelectionListener();
        sectionTable.getSelectionModel().addListSelectionListener(actionsSelection);

        setSize(750, 550);
    }


    private class SectionSelectionListener implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent e) {
            if (e.getValueIsAdjusting()) {
                return;
            }

            ListSelectionModel lsm = (ListSelectionModel)e.getSource();

            String sqlFromClause = "";
            try {
                if (lsm.isSelectionEmpty()) {
                    sqlFromClause =
                          "From " + columnsTable.getTable().getDBTableName()
                          + " where COLUMNS_ID = -1";
                    columnsTable.reloadData(sqlFromClause);
                    columnsToolBar.putDefaultValueForDetail("SECTION_ID", null);
                }
                else {
                    Map pk = sectionTable.getKey(sectionTable.getSelectedRow());
                    Object id = pk.get("SECTION_ID");
                    columnsToolBar.putDefaultValueForDetail("SECTION_ID", id);

                    sqlFromClause =
                          "From " + columnsTable.getTable().getDBTableName()
                          + " where SECTION_ID = " + id;
                    columnsTable.reloadData(sqlFromClause, true);
                }
            }
            catch (SQLException exc) {
                LOG.error(exc);
                ErrorDialog.show(BroadcastSectionsWindow.this,
                                 "Erreur pendant le chargement : " + sqlFromClause, exc);
            }
        }
    }
}
