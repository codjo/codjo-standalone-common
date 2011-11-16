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
public class BroadcastFilesWindow extends JInternalFrame {
    private static final Logger LOG = Logger.getLogger(BroadcastFilesWindow.class);
    private GenericTable contentsTable;
    private DbToolBar contentsToolBar;
    private BorderLayout fileBorderLayout = new BorderLayout();
    private JPanel filePanel = new JPanel();
    private JScrollPane fileScrollPane = new JScrollPane();
    private GenericTable fileTable;
    private DbToolBar fileToolBar;
    private JLabel filesLabel = new JLabel();
    private BorderLayout sectionBorderLayout = new BorderLayout();
    private JLabel sectionLabel = new JLabel();
    private JPanel sectionPanel = new JPanel();
    private JScrollPane sectionScrollPane = new JScrollPane();
    private GridBagLayout thisGridBagLayout = new GridBagLayout();


    /**
     * Constructor
     *
     * @param guiPreferencesManager Description of the Parameter
     * @param tableHome             Description of the Parameter
     */
    public BroadcastFilesWindow(JDesktopPane desktop,
                                ConnectionManager connectionManager,
                                GuiPreferencesManager guiPreferencesManager,
                                TableHome tableHome) throws SQLException, PersistenceException {
        super("Paramétrage des fichier distribués et de leurs sections", true, true,
              false, true);

        fileTable =
              new GenericTable(tableHome.getTable(guiPreferencesManager.getFileTableName()),
                               true);

        fileToolBar =
              new PersistentToolBar(desktop, fileTable, this, "net.codjo.gui.broadcast",
                                    false);
        fileToolBar.putDefaultValueForDetail("GUI_PREFERENCES_MANAGER",
                                             guiPreferencesManager);

        contentsTable =
              new GenericTable(tableHome.getTable(
                    guiPreferencesManager.getFileContentsTableName()), true,
                               "where FILE_ID = -1");

        contentsToolBar =
              new PersistentToolBar(desktop, contentsTable, this,
                                    "net.codjo.gui.broadcast");
        contentsToolBar.putDefaultValueForDetail("CONNECTION_MANAGER", connectionManager);
        contentsToolBar.putDefaultValueForDetail("GUI_PREFERENCES_MANAGER",
                                                 guiPreferencesManager);

        jbInit();
    }


    /**
     * Description of the Method
     */
    private void jbInit() {
        filesLabel.setFont(new java.awt.Font("Dialog", 1, 12));
        filesLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 0));
        filesLabel.setText("Fichiers distribues");

        sectionLabel.setFont(new java.awt.Font("Dialog", 1, 12));
        sectionLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 0));
        sectionLabel.setText("Sections");

        // Add components to Frame
        this.getContentPane().setLayout(thisGridBagLayout);
        this.setMinimumSize(new Dimension(100, 100));
        this.getContentPane().setBackground(Color.lightGray);

        filePanel.setLayout(fileBorderLayout);
        filePanel.add(filesLabel, BorderLayout.NORTH);
        filePanel.add(fileScrollPane, BorderLayout.CENTER);
        filePanel.add(fileToolBar, BorderLayout.SOUTH);

        sectionPanel.setLayout(sectionBorderLayout);
        sectionPanel.add(sectionLabel, BorderLayout.NORTH);
        sectionPanel.add(sectionScrollPane, BorderLayout.CENTER);
        sectionPanel.add(contentsToolBar, BorderLayout.SOUTH);

        fileScrollPane.getViewport().add(fileTable);
        sectionScrollPane.getViewport().add(contentsTable);

        this.getContentPane().add(filePanel,
                                  new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER,
                                                         GridBagConstraints.BOTH,
                                                         new Insets(0, 5, 0, 4),
                                                         -144,
                                                         -309));
        this.getContentPane().add(sectionPanel,
                                  new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER,
                                                         GridBagConstraints.BOTH,
                                                         new Insets(0, 5, 5, 4),
                                                         -144,
                                                         -309));

        // Listener securityCodeTable
        SectionSelectionListener actionsSelection = new SectionSelectionListener();
        fileTable.getSelectionModel().addListSelectionListener(actionsSelection);

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
                          "From " + contentsTable.getTable().getDBTableName() + " Where "
                          + "FILE_ID = -1";
                    contentsTable.reloadData(sqlFromClause);
                    contentsToolBar.putDefaultValueForDetail("FILE_ID", null);
                }
                else {
                    Map pk = fileTable.getKey(fileTable.getSelectedRow());
                    Object id = pk.get("FILE_ID");
                    contentsToolBar.putDefaultValueForDetail("FILE_ID", id);

                    sqlFromClause =
                          "From " + contentsTable.getTable().getDBTableName()
                          + " where FILE_ID = " + id;
                    contentsTable.reloadData(sqlFromClause, true);
                }
            }
            catch (SQLException exc) {
                LOG.error(exc);
                ErrorDialog.show(BroadcastFilesWindow.this,
                                 "Erreur pendant le chargement : " + sqlFromClause, exc);
            }
        }
    }
}
