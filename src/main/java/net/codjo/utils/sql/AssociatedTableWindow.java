/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.utils.sql;
import net.codjo.model.Table;
import net.codjo.persistent.PersistenceException;
import net.codjo.utils.ConnectionManager;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JDesktopPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
/**
 * Fenêtre générique de visualisation des 2 tables Mère / Fille
 *
 * @author BCHIR
 * @version $Revision: 1.5 $
 *
 */
public class AssociatedTableWindow extends javax.swing.JInternalFrame {
    JPanel jPanelfille = new JPanel();
    JPanel jPanelmere = new JPanel();
    JScrollPane jScrollPanemanager = new JScrollPane();
    JScrollPane jScrollPanedetails = new JScrollPane();
    private PersistentToolBar dbToolBarmanager;
    private PersistentToolBar dbToolBardetails;
    GridBagLayout gridBagLayout1 = new GridBagLayout();
    GridBagLayout gridBagLayout2 = new GridBagLayout();
    Border border2;
    TitledBorder titledBorder2;
    Border border1;
    TitledBorder titledBorder1;
    JPanel jPanelprincipal = new JPanel();
    Border border3;
    GridBagLayout gridBagLayout3 = new GridBagLayout();
    BorderLayout borderLayout1 = new BorderLayout();
    private JDesktopPane gexPane;
    private ConnectionManager conMan;
    private GenericTable managerTable;
    private GenericTable detailsTable;
    private Table topTable;
    private Table bottomTable;
    private String tableKey;
    private String packName;
    private String initWhere = "";
    private int sqlKeyType;
    private String windowTitle;
    private Connection con = null;
    private boolean pkAutomaticType = false;

    /**
     * Constructor for the AssociatedTableWindow object
     *
     * @param windowTitle Description of the Parameter
     * @param dp desktop
     * @param conMan connectionManager
     * @param topTable table mère (en haut)
     * @param bottomTable table fille (en bas)
     * @param tableKey clée de jointure
     * @param packName package des tables détails ( ! il doit etre le meme pour les 2
     *        tables)
     * @param pkAutomaticType Description of the Parameter
     *
     * @exception SQLException Description of the Exception
     * @exception PersistenceException Description of the Exception
     * @throws IllegalArgumentException TODO
     */
    public AssociatedTableWindow(String windowTitle, JDesktopPane dp,
        ConnectionManager conMan, Table topTable, Table bottomTable, String tableKey,
        String packName, boolean pkAutomaticType)
            throws SQLException, PersistenceException {
        super("Gestion des deux tables  ", true, true, false, true);

        if (dp == null || conMan == null) {
            throw new IllegalArgumentException("Un paramètre n'est pas renseigné!");
        }
        gexPane = dp;
        this.conMan = conMan;
        this.topTable = topTable;
        this.bottomTable = bottomTable;
        this.tableKey = tableKey;
        this.packName = packName;
        this.windowTitle = windowTitle;
        this.pkAutomaticType = pkAutomaticType;
        jbInit();
        pack();
    }


    public AssociatedTableWindow(String windowTitle, JDesktopPane dp,
        ConnectionManager conMan, Table topTable, Table bottomTable, String tableKey,
        String packName) throws SQLException, PersistenceException {
        this(windowTitle, dp, conMan, topTable, bottomTable, tableKey, packName, false);
    }

    private void setPkTypeForTopTable(boolean automatic) {
        if (!automatic) {
            dbToolBarmanager.setPkManual();
        }

        //sinon mode automatic
    }


    void this_internalFrameClosed(InternalFrameEvent evt) {
        try {
            conMan.releaseConnection(con);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /**
     * rempli la valeur par défaut pour l'écran de détail de la liste fille
     *
     * @param values Les n° des lignes sélectionnées
     */
    private void setDeaultValueForDetail(int[] values) {
        if (values.length == 1) {
            Map pk = managerTable.getKey(managerTable.getSelectedRow());
            dbToolBardetails.putDefaultValueForDetail(tableKey, pk.get(tableKey));
        }
        else {
            dbToolBardetails.putDefaultValueForDetail(tableKey, null);
        }
    }


    /**
     * Construction des elements la fenetre
     *
     * @exception PersistenceException Description of the Exception
     * @exception SQLException Description of the Exception
     */
    private void jbInit() throws PersistenceException, SQLException {
        setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
        this.setTitle(windowTitle);
        this.setPreferredSize(new Dimension(940, 640));
        this.addInternalFrameListener(new javax.swing.event.InternalFrameAdapter() {
                public void internalFrameClosed(InternalFrameEvent evt) {
                    this_internalFrameClosed(evt);
                }
            });
        getContentPane().setLayout(new BorderLayout(0, 0));
        border2 = BorderFactory.createEtchedBorder(Color.white, new Color(134, 134, 134));
        titledBorder2 = new TitledBorder(border2, "Table " + topTable.getTableName());
        border1 = BorderFactory.createEtchedBorder(Color.white, new Color(134, 134, 134));
        titledBorder1 = new TitledBorder(border1, "Table " + bottomTable.getTableName());
        border3 = BorderFactory.createEmptyBorder();
        this.getContentPane().setLayout(borderLayout1);
        jPanelfille.setLayout(gridBagLayout2);
        jPanelfille.setBorder(titledBorder1);
        jPanelmere.setBorder(titledBorder2);
        jPanelmere.setLayout(gridBagLayout1);
        sqlKeyType = bottomTable.getColumnSqlType(tableKey);
        createIdTable(sqlKeyType);
        // table fille
        detailsTable =
            new GenericTable(con, bottomTable, false, "where " + tableKey + initWhere);

        dbToolBardetails =
            new PersistentToolBar(gexPane, detailsTable, this, packName, true);
        dbToolBardetails.putDefaultValueForDetail("connectionManager", conMan);

        //table mere
        managerTable = new GenericTable(con, topTable, false);
        dbToolBarmanager =
            new PersistentToolBar(gexPane, managerTable, this, packName, false);
        dbToolBarmanager.putDefaultValueForDetail("connectionManager", conMan);
        setPkTypeForTopTable(pkAutomaticType);

        jScrollPanemanager.setBorder(BorderFactory.createLoweredBevelBorder());
        jPanelprincipal.setBorder(border3);
        jPanelprincipal.setLayout(gridBagLayout3);
        detailsTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        managerTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        this.getContentPane().add(jPanelprincipal, BorderLayout.CENTER);
        jPanelprincipal.add(jPanelmere,
            new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(5, 3, 0, 0), -459, -392));
        jPanelmere.add(jScrollPanemanager,
            new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.NORTH,
                GridBagConstraints.BOTH, new Insets(0, 1, 0, 0), 571, 208));
        jPanelmere.add(dbToolBarmanager,
            new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER,
                GridBagConstraints.HORIZONTAL, new Insets(0, 1, 0, 0), 261, 5));
        jPanelprincipal.add(jPanelfille,
            new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(0, 3, 0, 0), -459, -396));
        jPanelfille.add(jScrollPanedetails,
            new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(1, 0, 0, 0), 572, 202));
        jPanelfille.add(dbToolBardetails,
            new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER,
                GridBagConstraints.HORIZONTAL, new Insets(6, 0, 0, 0), 262, 7));

        jScrollPanemanager.getViewport().add(managerTable);
        jScrollPanedetails.getViewport().add(detailsTable);

        // Listener managertable
        SelectionListener actions_selection = new SelectionListener();
        managerTable.getSelectionModel().addListSelectionListener(actions_selection);
    }


    /**
     * Drope la table des ID
     *
     * @param con connection
     *
     * @exception SQLException Description of the Exception
     */
    private void dropTable(Connection con) throws SQLException {
        Statement stmt = con.createStatement();
        try {
            stmt.executeUpdate("drop table  #ID_TABLE");
        }
        catch (SQLException ex) {
            // on n'affiche pas l'exception SQL
        }
    }


    /**
     * construit la table temporaire des ID en fonction du type de l'ID en base
     *
     * @param sqlType Description of the Parameter
     *
     * @exception SQLException Description of the Exception
     */
    private void createIdTable(int sqlType) throws SQLException {
        try {
            con = conMan.getConnection();
            dropTable(con);
            Statement stmt = con.createStatement();
            if (sqlKeyType == Types.VARCHAR || sqlKeyType == Types.CHAR) {
                stmt.executeUpdate("create table  #ID_TABLE" + "("
                    + "ID_NAME       varchar(50)    null," + ")");
                stmt.close();
                initWhere = " = 'vide'";
            }
            else if (sqlKeyType == Types.NUMERIC) {
                stmt.executeUpdate("create table  #ID_TABLE" + "("
                    + "ID_NAME       numeric(18)    null," + ")");
                stmt.close();
                initWhere = " = 0";
            }
            else if (sqlKeyType == Types.INTEGER) {
                stmt.executeUpdate("create table  #ID_TABLE" + "("
                    + "ID_NAME       int    null," + ")");
                stmt.close();
                initWhere = " = 0";
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /**
     * Description of the Method
     *
     * @exception SQLException Description of the Exception
     */
    private void delTempTable() throws SQLException {
        try {
            Statement stmt = con.createStatement();
            stmt.executeUpdate("truncate table #ID_TABLE ");
            stmt.close();
        }
        catch (SQLException exc) {
            exc.printStackTrace();
        }
    }


    /**
     * rempli la table des clées de la selection
     *
     * @param values Les n° des lignes sélectionnées
     *
     * @exception SQLException Description of the Exception
     */
    private void fillIdTable(int[] values) throws SQLException {
        java.util.List idList = new java.util.ArrayList();
        for (int i = 0; i < values.length; i++) {
            idList.add(i, managerTable.getKey(values[i]).get(tableKey));
        }

        java.util.Iterator it = idList.iterator();
        CallableStatement cstmt = null;
        try {
            delTempTable();
            cstmt = con.prepareCall("insert into  #ID_TABLE (ID_NAME) values (?)");
            while (it.hasNext()) {
                Object id = (Object)it.next();
                cstmt.setObject(1, id);
                cstmt.executeUpdate();
            }
        }
        finally {
            try {
                cstmt.close();
            }
            catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Listener sur la generictable managerTable qui affiche le detail des lignes
     * selectionnées
     *
     * @author BCHIR
     * @version $Revision: 1.5 $
     */
    private class SelectionListener implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent e) {
            if (e.getValueIsAdjusting()) {
                return;
            }

            ListSelectionModel lsm = (ListSelectionModel)e.getSource();
            if (lsm.isSelectionEmpty()) {
                try {
                    detailsTable.reloadData("From " + bottomTable.getDBTableName()
                        + " Where " + tableKey + initWhere, true);
                    dbToolBardetails.setDefaultValueforFindAction(" where " + tableKey
                        + initWhere);
                    dbToolBardetails.putDefaultValueForDetail(tableKey, null);
                }
                catch (Exception exc) {
                    exc.printStackTrace();
                }
            }
            else {
                try {
                    int[] values = managerTable.getSelectedRows();
                    fillIdTable(values);
                    setDeaultValueForDetail(values);
                    String rQuery =
                        "From " + bottomTable.getDBTableName()
                        + " Inner join #ID_TABLE on " + "("
                        + bottomTable.getDBTableName() + "." + tableKey
                        + " = #ID_TABLE.ID_NAME )";
                    try {
                        detailsTable.reloadData(rQuery, true);
                        dbToolBardetails.setDefaultValueforFindAction(
                            " Inner join #ID_TABLE on " + "("
                            + bottomTable.getDBTableName() + "." + tableKey
                            + " = #ID_TABLE.ID_NAME )");
                    }
                    catch (Exception exc) {
                        exc.printStackTrace();
                    }
                }
                catch (Exception exce) {
                    exce.printStackTrace();
                }
            }
        }
    }
}
