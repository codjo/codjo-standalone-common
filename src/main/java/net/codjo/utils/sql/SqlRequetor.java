/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.utils.sql;
import net.codjo.gui.model.TableComboBox;
import net.codjo.gui.operation.WaitingWindowManager;
import net.codjo.gui.renderer.FieldNameRenderer;
import net.codjo.gui.toolkit.fileChooser.FileChooserManager;
import net.codjo.gui.toolkit.util.ErrorDialog;
import net.codjo.model.Table;
import net.codjo.model.TableHome;
import net.codjo.utils.ConnectionManager;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.apache.log4j.Logger;
//
/**
 * Fenêtre de saisie de requêtes SQL. Permet d'executer un select sur une table donnée.
 *
 * @author $Author: marcona $
 * @version $Revision: 1.8 $
 */
public class SqlRequetor extends JInternalFrame {
    // Log
    private static final Logger APP = Logger.getLogger(SqlRequetor.class);
    JPanel panelRequest = new JPanel();
    JPanel panelButton = new JPanel();
    JPanel panelShowRequest = new JPanel();
    JPanel panelConstructRequest = new JPanel();
    JButton validateButton = new JButton();
    JButton cancelButton = new JButton();
    JButton buttonAdd = new JButton();
    JButton buttonDelete = new JButton();
    JButton buttonDeleteAll = new JButton();
    JButton buttonAnd = new JButton();
    JButton buttonOr = new JButton();
    JLabel labelFields = new JLabel();
    JLabel labelOperators = new JLabel();
    JLabel labelValue = new JLabel();
    JLabel labelLinkTables = new JLabel();
    JList listSqlRequest = new JList();
    JList listCurrentFields = new JList();
    JList listOperators = new JList();
    JList listLinkFields = new JList();
    JScrollPane linkFieldsScrollPane = new JScrollPane();
    JScrollPane operatorsScrollPane = new JScrollPane();
    JScrollPane currentFieldsScrollPane = new JScrollPane();
    JScrollPane sqlRequestScrollPane = new JScrollPane();
    DefaultListModel sqlListModel = new DefaultListModel();
    DefaultListModel currentListFieldsModel = new DefaultListModel();
    DefaultListModel linkListFieldsModel = new DefaultListModel();
    Border border1;
    Border border2;
    Border border3;
    TitledBorder titledBorder1;
    TitledBorder titledBorder2;
    String defaultClause;
    String mandatoryClause = "";
    Map srcLinkTableFieldsName;
    Map destLinkTableFieldsName;
    Map innerJoins;
    JTextField textFieldValue = new JTextField();
    TableComboBox linkTablesComboBox;
    JInternalFrame windowTable;
    GenericTableInterface table;
    TableHome th;
    ConnectionManager connectionManager;
    SqlRequetorRequest req;
    JCheckBox findInSelectionCheckBox = new JCheckBox();
    FindAction findAction;
    JDesktopPane gexPane;
    boolean findInSelection = true;
    WaitingWindowManager waitingWindowManager;
    JButton buttonSave = new JButton();
    JButton buttonOpen = new JButton();


    /**
     * Constructor for the SqlRequetor object
     *
     * @param dp            Le desktopPane.
     * @param frm           Fenêtre d'affichage de la table (fenêtre mère).
     * @param fa            La FindAction
     * @param gti           La GenericTable.
     * @param defaultClause Clause par défaut de la requête SQL.
     *
     * @throws IllegalArgumentException TODO
     */
    public SqlRequetor(JDesktopPane dp, JInternalFrame frm, FindAction fa,
                       GenericTableInterface gti, String defaultClause) {
        super("Recherche sur la table " + gti.getTable(), false, true, false, true);
        if (dp == null) {
            throw new IllegalArgumentException();
        }

        gexPane = dp;
        this.defaultClause = defaultClause;
        table = gti;
        findAction = fa;
        windowTable = frm;
        connectionManager = Dependency.getConnectionManager();
        req = new SqlRequetorRequest();
        addOldRequest();
        th = (TableHome)table.getTable().getReference().getModel();
        waitingWindowManager =
              new WaitingWindowManager(gexPane, "Recherche en cours...", 1);
        try {
            buildListOpeartors();
            initLinkTablesAndFieldsMap();
            fillLinkTablesComboBox();
            linkTablesComboBox.setSelectedItem(table.getTable().getId());
            initInnerJoinsMap();
            buildListFields(table.getTable().getDBTableName(), currentListFieldsModel,
                            listCurrentFields);
            linkFieldsScrollPane.setVisible(false);
            jbInit();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        setNameForGuiTest();
    }


    private void setNameForGuiTest() {
        textFieldValue.setName("textFieldValue");
        listSqlRequest.setName("listSqlRequest");
        buttonAdd.setName("buttonAdd");
        linkTablesComboBox.setName("linkTablesComboBox");
        validateButton.setName("validateButton");
        cancelButton.setName("cancelButton");
        listLinkFields.setName("listLinkFields");
        listOperators.setName("listOperators");
        listCurrentFields.setName("listCurrentFields");
    }


    /**
     * Fixe une clause de recherche obligatoire
     *
     * @param clause The new MandatoryClause value
     */
    public void setMandatoryClause(String clause) {
        mandatoryClause = clause;
    }


    /**
     * Description of the Method
     *
     * @param evt Description of the Parameter
     */
    void buttonValidateActionPerformed(ActionEvent evt) {
        if (sqlListModel.isEmpty()) {
            Toolkit.getDefaultToolkit().beep();
            return;
        }

        waitingWindowManager.showWaitingWindow();
        final javax.swing.Timer timer =
              new javax.swing.Timer(100,
                                    new ActionListener() {
                                        public void actionPerformed(ActionEvent evt) {
                                            doReloadData();
                                        }
                                    });
        timer.setRepeats(false);
        timer.start();
    }


    /**
     * Description of the Method
     *
     * @param evt Description of the Parameter
     */
    void buttonCancelActionPerformed(ActionEvent evt) {
        if (evt.getSource() == cancelButton) {
            dispose();
            try {
                windowTable.setSelected(true);
            }
            catch (java.beans.PropertyVetoException g) {
            }
        }
    }


    /**
     * Description of the Method
     *
     * @param evt Description of the Parameter
     */
    void buttonDeleteActionPerformed(ActionEvent evt) {
        if (sqlListModel.isEmpty() || listSqlRequest.isSelectionEmpty()) {
            return;
        }
        if (sqlListModel.getSize() > 1 && getIndex() == 0) {
            req.setLogicalOper("", getIndex() + 1);
        }
        textFieldValue.setText("");
        req.removeElements(getIndex());
        sqlListModel.removeElementAt(getIndex());
        updateSqlRequest();
    }


    /**
     * Description of the Method
     *
     * @param evt Description of the Parameter
     */
    void buttonAddActionPerformed(ActionEvent evt) {
        if (sqlListModel.isEmpty()) {
            return;
        }
        addNewLine();
    }


    /**
     * Description of the Method
     *
     * @param evt Description of the Parameter
     */
    void buttonAndActionPerformed(ActionEvent evt) {
        if (getIndex() == 0) {
            return;
        }
        req.setLogicalOper("and ", getIndex());
        updateSqlRequest();
    }


    /**
     * Description of the Method
     *
     * @param evt Description of the Parameter
     */
    void buttonOrActionPerformed(ActionEvent evt) {
        if (getIndex() == 0) {
            return;
        }
        req.setLogicalOper("or ", getIndex());
        updateSqlRequest();
    }


    /**
     * Description of the Method
     *
     * @param e Description of the Parameter
     */
    void textFieldValueKeyReleased(KeyEvent e) {
        if (!sqlListModel.isEmpty()) {
            req.setValue(textFieldValue.getText(), getIndex());
            updateSqlRequest();
        }
    }


    /**
     * Description of the Method
     *
     * @param evt Description of the Parameter
     */
    void buttonDeleteAllActionPerformed(ActionEvent evt) {
        sqlListModel.removeAllElements();
        req.removeAllElements();
    }


    /**
     * Description of the Method
     *
     * @param evt Description of the Parameter
     */
    void linkTablesComboBoxActionPerformed(ActionEvent evt) {
        String sourceTable = table.getTable().getDBTableName();
        String linkTable = linkTablesComboBox.getSelectedTable().getDBTableName();

        if (!linkTable.equals(sourceTable)) {
            linkListFieldsModel.removeAllElements();
            buildListFields(linkTable, linkListFieldsModel, listLinkFields);
            linkFieldsScrollPane.setVisible(true);
        }
        else {
            linkFieldsScrollPane.setVisible(false);
        }
    }


    /**
     * Description of the Method
     *
     * @param evt Description of the Parameter
     */
    void findInSelectionCheckBoxActionPerformed(ActionEvent evt) {
        if (findInSelectionCheckBox.isSelected()) {
            addOldRequest();
        }
        else {
            removeOldRequest();
        }
    }


    /**
     * Action permettant de sauvegarder la requête courante dans un fichier texte.
     *
     * @param evt Description of the Parameter
     */
    void buttonSaveActionPerformed(ActionEvent evt) {
        String fileName =
              FileChooserManager.showChooserForExport("requête.txt",
                                                      "Sauvegarde de la requête");
        if (fileName == null) {
            return;
        }

        try {
            saveRequest(fileName);
        }
        catch (IOException ex) {
            APP.error(ex);
            ErrorDialog.show(this, "Erreur lors de la sauvegarde de la requête", ex);
        }
    }


    /**
     * Action permettant d'ouvrir une requête suvegardée dans un fichier texte.
     *
     * @param evt Description of the Parameter
     */
    void buttonOpenActionPerformed(ActionEvent evt) {
        String fileName =
              FileChooserManager.showChooserForOpen("requête.txt", "Ouverture de la requête");
        if (fileName == null) {
            return;
        }

        try {
            loadRequest(fileName);
        }
        catch (Exception ex) {
            APP.error(ex);
            ErrorDialog.show(this, "Erreur lors de l'ouverture de la requête", ex);
        }
    }


    /**
     * Retourne l'index de l'élément courant de la liste des requêtes.
     *
     * @return L'index.
     */
    private int getIndex() {
        int idx;
        if (sqlListModel.isEmpty()) {
            idx = 0;
        }
        else {
            if (listSqlRequest.isSelectionEmpty()) {
                idx = sqlListModel.size() - 1;
            }
            else {
                idx = listSqlRequest.getSelectedIndex();
            }
        }
        return idx;
    }


    /**
     * Recharge les données à partir de la recherche spécifiée.
     */
    private void doReloadData() {
        try {
            findAction.setPreviousRequest(req);
            table.reloadData(buildQuery());
            dispose();
            firePropertyChange("reload", 0, 0);
            waitingWindowManager.disposeWaitingWindow();
            try {
                windowTable.setSelected(true);
            }
            catch (java.beans.PropertyVetoException g) {
            }
        }
        catch (SQLException es) {
            waitingWindowManager.disposeWaitingWindow();
            APP.error(es);
            ErrorDialog.show(windowTable, "Requête SQL incorrecte",
                             es.getLocalizedMessage());
        }
    }


    /**
     * Construit la requête (from ...) à envoyer à la GenericTable.
     *
     * @return La requête.
     *
     * @throws SQLException Description of the Exception
     */
    private String buildQuery() throws SQLException {
        try {
            StringBuffer query = new StringBuffer();
            query.append("from ").append(table.getTable().getDBTableName()).append(buildInnerJoinKeys());

            if (!"".equals(defaultClause) && findInSelection) {
                query.append(defaultClause).append(" and (");
            }
            else {
                query.append(" where (");
            }

            if (!"".equals(mandatoryClause)) {
                query.append(mandatoryClause + " and ");
            }

            for (int i = 0; i < sqlListModel.size(); i++) {
                query.append((sqlListModel.get(i)).toString());
            }
            query.append(")");
            APP.debug("Requete envoyée :" + query.toString());
            return query.toString();
        }
        catch (NullPointerException ex) {
            throw new SQLException();
        }
    }


    /**
     * Retire les quotes de la valeur du champ sur lequel on désire faire une recherche.
     *
     * @param value La valeur du champ.
     *
     * @return La valeur du champ sans quote.
     */
    private String removeQuote(String value) {
        StringBuffer tmp = new StringBuffer(value);
        char quote = '\'';
        int index = 0;
        while (index < tmp.length()) {
            if (tmp.charAt(index) == quote) {
                tmp.deleteCharAt(index);
                index++;
            }
            index++;
        }
        return tmp.toString();
    }


    /**
     * Retourne le type SQL du champ sélectioné.
     *
     * @return Le type SQL.
     */
    private int findSqlType() {
        int sqlType = 0;
        sqlType = req.getTable(getIndex()).getColumnSqlType(req.getField(getIndex()));
        return sqlType;
    }


    /**
     * Rempli la liste des champs de la table désirée.
     *
     * @param tableName Le nom de la table.
     * @param model     Le model de la liste.
     * @param list      La liste.
     */
    private void buildListFields(String tableName, DefaultListModel model, JList list) {
        try {
            Table tab = th.getTable(tableName);
            FieldNameRenderer fieldNameRenderer =
                  new FieldNameRenderer(connectionManager, tab.getDBTableName());
            Map m = tab.getAllColumns();
            Object[] o = m.keySet().toArray();
            Arrays.sort(o,
                        new net.codjo.gui.renderer.FieldLabelComparator(connectionManager,
                                                                      tab.getDBTableName()));
            for (int i = 0; i < o.length; i++) {
                model.add(i, o[i]);
            }
            list.setCellRenderer(fieldNameRenderer);
        }
        catch (Exception ex) {
            APP.error(ex);
            ErrorDialog.show(this, "Impossible de charger la liste des champs : ",
                             ex.getLocalizedMessage());
        }
    }


    /**
     * Met à jour la liste des requêtes.
     */
    private void updateSqlRequest() {
        for (int i = 0; i < sqlListModel.size(); i++) {
            sqlListModel.setElementAt(req.getRequest(i), i);
        }
    }


    /**
     * Rempli la liste des opérateurs de comparaison.
     */
    private void buildListOpeartors() {
        List oper = new ArrayList();
        oper.add("Egal");
        oper.add("Supérieur");
        oper.add("Supérieur ou égal");
        oper.add("Inférieur");
        oper.add("Inférieur ou égal");
        oper.add("Différent");
        oper.add("Commence par");
        oper.add("Finit par");
        oper.add("Contient");
        oper.add("Ne contient pas");
        oper.add("Est null");
        oper.add("Est pas null");
        listOperators = new JList(oper.toArray());
    }


    /**
     * Initialisation du gui
     */
    private void jbInit() {
        border2 = BorderFactory.createEtchedBorder(Color.white, new Color(134, 134, 134));
        titledBorder1 = new TitledBorder(border2, "Texte de la requête");

        border3 = BorderFactory.createEtchedBorder(Color.white, new Color(134, 134, 134));
        titledBorder2 = new TitledBorder(border3, "Saisie de la requête");
        validateButton.setText("Valider");
        validateButton.addActionListener(new java.awt.event.ActionListener() {
            /**
             * DOCUMENT ME!
             *
             * @param evt Description of Parameter
             */
            public void actionPerformed(ActionEvent evt) {
                buttonValidateActionPerformed(evt);
            }
        });
        cancelButton.setText("Annuler");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            /**
             * DOCUMENT ME!
             *
             * @param evt Description of Parameter
             */
            public void actionPerformed(ActionEvent evt) {
                buttonCancelActionPerformed(evt);
            }
        });
        this.getContentPane().setBackground(UIManager.getColor("Panel.background"));
        this.setFont(new java.awt.Font("Dialog", 0, 10));
        this.getContentPane().setLayout(null);
        panelRequest.setLayout(null);
        panelButton.setBounds(new Rectangle(486, 439, 181, 36));
        panelRequest.setBounds(new Rectangle(10, 1, 657, 430));
        textFieldValue.setBounds(new Rectangle(16, 269, 610, 24));
        textFieldValue.addKeyListener(new java.awt.event.KeyAdapter() {
            /**
             * DOCUMENT ME!
             *
             * @param evt Description of Parameter
             */
            public void keyReleased(KeyEvent evt) {
                textFieldValueKeyReleased(evt);
            }
        });
        panelShowRequest.setBorder(titledBorder1);
        panelShowRequest.setBounds(new Rectangle(2, 320, 643, 112));
        panelShowRequest.setLayout(null);
        listSqlRequest.setModel(sqlListModel);
        listSqlRequest.setBounds(new Rectangle(31, 223, 434, 47));
        sqlRequestScrollPane.setBorder(BorderFactory.createEtchedBorder());
        sqlRequestScrollPane.setBounds(new Rectangle(17, 22, 613, 75));
        buttonAdd.setText("Ajouter");
        buttonAdd.setBounds(new Rectangle(19, 205, 78, 22));
        buttonAdd.addActionListener(new java.awt.event.ActionListener() {
            /**
             * DOCUMENT ME!
             *
             * @param evt Description of Parameter
             */
            public void actionPerformed(ActionEvent evt) {
                buttonAddActionPerformed(evt);
            }
        });
        panelConstructRequest.setBorder(titledBorder2);
        panelConstructRequest.setBounds(new Rectangle(2, 8, 642, 310));
        panelConstructRequest.setLayout(null);
        labelFields.setHorizontalAlignment(SwingConstants.CENTER);
        labelFields.setText("Liste des champs");
        labelFields.setBounds(new Rectangle(46, 21, 131, 15));
        listCurrentFields.setModel(currentListFieldsModel);
        listCurrentFields.setBounds(new Rectangle(29, 27, 183, 142));
        labelOperators.setText("Liste des opérateurs");
        labelOperators.setBounds(new Rectangle(463, 21, 131, 15));
        labelOperators.setHorizontalAlignment(SwingConstants.CENTER);
        buttonDelete.addActionListener(new java.awt.event.ActionListener() {
            /**
             * DOCUMENT ME!
             *
             * @param evt Description of Parameter
             */
            public void actionPerformed(ActionEvent evt) {
                buttonDeleteActionPerformed(evt);
            }
        });
        buttonDelete.setText("Supprimer");
        buttonDelete.setBounds(new Rectangle(112, 205, 92, 22));
        linkFieldsScrollPane.setBorder(BorderFactory.createEtchedBorder());
        linkFieldsScrollPane.setBounds(new Rectangle(227, 76, 188, 112));
        operatorsScrollPane.setBorder(BorderFactory.createEtchedBorder());
        operatorsScrollPane.setBounds(new Rectangle(438, 44, 188, 144));
        listLinkFields.setBounds(new Rectangle(29, 27, 183, 142));
        listLinkFields.setModel(linkListFieldsModel);
        currentFieldsScrollPane.setBorder(BorderFactory.createEtchedBorder());
        currentFieldsScrollPane.setBounds(new Rectangle(16, 44, 188, 144));
        labelValue.setHorizontalAlignment(SwingConstants.CENTER);
        labelValue.setText("Valeur");
        labelValue.setBounds(new Rectangle(285, 249, 72, 17));
        labelLinkTables.setHorizontalAlignment(SwingConstants.CENTER);
        labelLinkTables.setText("Tables liées");
        labelLinkTables.setBounds(new Rectangle(256, 21, 131, 15));
        linkTablesComboBox.addActionListener(new java.awt.event.ActionListener() {
            /**
             * DOCUMENT ME!
             *
             * @param evt Description of Parameter
             */
            public void actionPerformed(ActionEvent evt) {
                linkTablesComboBoxActionPerformed(evt);
            }
        });
        linkTablesComboBox.setBounds(new Rectangle(228, 44, 186, 21));
        buttonDeleteAll.setText("Tout supprimer");
        buttonDeleteAll.setBounds(new Rectangle(229, 205, 185, 22));
        buttonDeleteAll.addActionListener(new java.awt.event.ActionListener() {
            /**
             * DOCUMENT ME!
             *
             * @param evt Description of Parameter
             */
            public void actionPerformed(ActionEvent evt) {
                buttonDeleteAllActionPerformed(evt);
            }
        });
        buttonAnd.addActionListener(new java.awt.event.ActionListener() {
            /**
             * DOCUMENT ME!
             *
             * @param evt Description of Parameter
             */
            public void actionPerformed(ActionEvent evt) {
                buttonAndActionPerformed(evt);
            }
        });
        buttonAnd.setText("Et");
        buttonAnd.setBounds(new Rectangle(438, 205, 48, 22));
        buttonOr.setText("Ou");
        buttonOr.setBounds(new Rectangle(577, 205, 50, 22));
        buttonOr.addActionListener(new java.awt.event.ActionListener() {
            /**
             * DOCUMENT ME!
             *
             * @param evt Description of Parameter
             */
            public void actionPerformed(ActionEvent evt) {
                buttonOrActionPerformed(evt);
            }
        });
        findInSelectionCheckBox.setSelected(true);
        findInSelectionCheckBox.setText("Conserver les critères précédents");
        findInSelectionCheckBox.setBounds(new Rectangle(265, 448, 211, 18));
        findInSelectionCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                findInSelectionCheckBoxActionPerformed(evt);
            }
        });
        buttonSave.addActionListener(new java.awt.event.ActionListener() {
            /**
             * DOCUMENT ME!
             *
             * @param evt Description of Parameter
             */
            public void actionPerformed(ActionEvent evt) {
                buttonSaveActionPerformed(evt);
            }
        });
        buttonSave.setBounds(new Rectangle(15, 444, 114, 26));
        buttonSave.setText("Sauvegarder...");
        buttonOpen.setText("Ouvrir...");
        buttonOpen.setBounds(new Rectangle(134, 444, 107, 26));
        buttonOpen.addActionListener(new java.awt.event.ActionListener() {
            /**
             * DOCUMENT ME!
             *
             * @param evt Description of Parameter
             */
            public void actionPerformed(ActionEvent evt) {
                buttonOpenActionPerformed(evt);
            }
        });
        this.getContentPane().add(panelRequest, null);
        panelRequest.add(panelConstructRequest, null);
        panelConstructRequest.add(buttonDeleteAll, null);
        panelConstructRequest.add(labelFields, null);
        panelConstructRequest.add(labelLinkTables, null);
        panelConstructRequest.add(textFieldValue, null);
        panelConstructRequest.add(currentFieldsScrollPane, null);
        panelConstructRequest.add(buttonDelete, null);
        panelConstructRequest.add(labelOperators, null);
        panelConstructRequest.add(linkTablesComboBox, null);
        panelConstructRequest.add(buttonOr, null);
        panelConstructRequest.add(labelValue, null);
        panelConstructRequest.add(operatorsScrollPane, null);
        panelConstructRequest.add(buttonAnd, null);
        panelConstructRequest.add(buttonAdd, null);
        panelConstructRequest.add(linkFieldsScrollPane, null);
        linkFieldsScrollPane.getViewport().add(listLinkFields);
        operatorsScrollPane.getViewport().add(listOperators);
        currentFieldsScrollPane.getViewport().add(listCurrentFields);
        panelRequest.add(panelShowRequest, null);
        panelShowRequest.add(sqlRequestScrollPane, null);
        this.getContentPane().add(panelButton, null);
        panelButton.add(validateButton, null);
        panelButton.add(cancelButton, null);
        this.getContentPane().add(findInSelectionCheckBox, null);
        this.getContentPane().add(buttonOpen, null);
        this.getContentPane().add(buttonSave, null);
        sqlRequestScrollPane.getViewport().add(listSqlRequest);
        setSize(673, 505);

        // List Listeners
        CurrentFieldSelectionListener currentFieldSelection =
              new CurrentFieldSelectionListener();
        listCurrentFields.getSelectionModel().addListSelectionListener(currentFieldSelection);

        LinkFieldSelectionListener linkFieldSelection = new LinkFieldSelectionListener();
        listLinkFields.getSelectionModel().addListSelectionListener(linkFieldSelection);

        OperatorSelectionListener operatorSelection = new OperatorSelectionListener();
        listOperators.getSelectionModel().addListSelectionListener(operatorSelection);

        SqlRequestSelectionListener sqlRequestSelection =
              new SqlRequestSelectionListener();
        listSqlRequest.getSelectionModel().addListSelectionListener(sqlRequestSelection);
    }


    /**
     * Rempli les Maps des tables liées avec les champs de jointure (clé : Id de la table liée, valeur : la
     * liste des champs de jointure (source pour la 1ère Map, dest pour la 2ème)).
     */
    private void initLinkTablesAndFieldsMap() {
        srcLinkTableFieldsName = new HashMap();
        destLinkTableFieldsName = new HashMap();
        List srcFieldName = new ArrayList();
        List destFieldName = new ArrayList();
        Connection con = null;
        try {
            con = connectionManager.getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs =
                  stmt.executeQuery(
                        "select LINK_DB_TABLE_NAME_ID,SOURCE_DB_FIELD_NAME,DEST_DB_FIELD_NAME "
                        + "from PM_TABLE, PM_LINK_TABLE where PM_TABLE.DB_TABLE_NAME_ID="
                        + "PM_LINK_TABLE.DB_TABLE_NAME_ID and PM_TABLE.DB_TABLE_NAME_ID = "
                        + table.getTable().getTableId() + " order by LINK_DB_TABLE_NAME_ID");

            int linkTableId = -1;
            while (rs.next()) {
                if (linkTableId != -1 && linkTableId != rs.getInt(1)) {
                    srcLinkTableFieldsName.put(new Integer(linkTableId), srcFieldName);
                    destLinkTableFieldsName.put(new Integer(linkTableId), destFieldName);
                    srcFieldName = new ArrayList();
                    destFieldName = new ArrayList();
                }
                srcFieldName.add(rs.getString(2));
                destFieldName.add(rs.getString(3));
                linkTableId = rs.getInt(1);
            }

            if (linkTableId != -1) {
                srcLinkTableFieldsName.put(new Integer(linkTableId), srcFieldName);
                destLinkTableFieldsName.put(new Integer(linkTableId), destFieldName);
            }
        }
        catch (SQLException ex) {
            APP.error(ex);
        }
        finally {
            try {
                connectionManager.releaseConnection(con);
            }
            catch (SQLException es) {
                APP.error(es);
            }
        }
    }


    /**
     * Construit la string avec toutes les jointures utiles à la requête que l'on souhaite exécuter (parcours
     * des tables liées présentes dans la requête).
     *
     * @return La string des jointures
     */
    private String buildInnerJoinKeys() {
        String innerJoinKey = "";
        for (int i = 0; i < sqlListModel.size(); i++) {
            String reqTableName = req.getTable(i).getDBTableName();
            String currentTableName = table.getTable().getDBTableName();
            if ((!reqTableName.equals(currentTableName))
                && (innerJoinKey.indexOf(reqTableName) < 0)) {
                innerJoinKey += innerJoins.get(req.getTable(i).getDBTableName());
            }
        }
        return innerJoinKey;
    }


    /**
     * Rempli la Map des jointures (clé : nom physique de la table liée, valeur : la string de jointure avec
     * la table courante).
     *
     * @throws Exception PersistenceException
     */
    private void initInnerJoinsMap() throws Exception {
        innerJoins = new HashMap();
        List srcLinkFieldName;
        List destLinkFieldName;

        for (Iterator iter = srcLinkTableFieldsName.entrySet().iterator();
             iter.hasNext();) {
            int linkTableId = ((Integer)(((Map.Entry)iter.next()).getKey())).intValue();
            Table linkTable = (Table)th.getTable(linkTableId);
            String linkTableName = linkTable.getDBTableName();
            String currentTable = table.getTable().getDBTableName();

            srcLinkFieldName = (List)srcLinkTableFieldsName.get(linkTable.getTableId());
            destLinkFieldName = (List)destLinkTableFieldsName.get(linkTable.getTableId());

            StringBuffer innerJoinStr = new StringBuffer(" INNER JOIN ");
            innerJoinStr.append(linkTableName).append(" ON ").append(currentTable)
                  .append(".").append(srcLinkFieldName.get(0)).append(" = ")
                  .append(linkTableName).append(".").append(destLinkFieldName.get(0))
                  .append(" ");
            for (int i = 0; i < srcLinkFieldName.size() - 1; i++) {
                innerJoinStr.append("AND ").append(currentTable).append(".")
                      .append(srcLinkFieldName.get(i + 1)).append(" = ")
                      .append(linkTableName).append(".")
                      .append(destLinkFieldName.get(i + 1)).append(" ");
            }
            innerJoins.put(linkTableName, innerJoinStr.toString());
        }
    }


    /**
     * Rempli le combo des tables liées.
     */
    private void fillLinkTablesComboBox() {
        linkTablesComboBox =
              new TableComboBox(th, srcLinkTableFieldsName.keySet().toArray());

        if (table.getTable().getId() != null) {
            linkTablesComboBox.addItem(table.getTable().getId());
        }
        else {
            labelLinkTables.setVisible(false);
            linkTablesComboBox.setVisible(false);
        }
    }


    /**
     * Ajoute une nouvelle ligne à la requête.
     */
    private void addNewLine() {
        int idx = sqlListModel.getSize();
        req.addElements(idx);
        req.setLogicalOper(" and ", idx);
        sqlListModel.add(idx, "");
        updateSqlRequest();
        listSqlRequest.clearSelection();
        listSqlRequest.setVisibleRowCount(listSqlRequest.getModel().getSize() - 1);
    }


    /**
     * Ajoute l'ancienne requête à la requête actuelle pour faire une recherche dans sélection.
     */
    private void addOldRequest() {
        findInSelection = true;
        if (findAction.getPreviousRequest() != null) {
            req = new SqlRequetorRequest(findAction.getPreviousRequest());
            sqlListModel.removeAllElements();
            for (int i = 0; i < req.getRequestListSize(); i++) {
                sqlListModel.add(i, req.getRequest(i));
            }
            addNewLine();
        }
    }


    /**
     * Supprime l'ancienne requête de la requête actuelle pour faire une recherche sur la table entière.
     */
    private void removeOldRequest() {
        findInSelection = false;
        sqlListModel.removeAllElements();
        req = new SqlRequetorRequest();
    }


    /**
     * Charge la requête présente dans le fichier texte dans le requêteur. Par défaut on supprime les critères
     * précédents (on décoche la case à cocher).
     *
     * @param fileName Le nom du fichier à charger
     *
     * @throws Exception -
     */
    private void loadRequest(String fileName) throws Exception {
        findInSelectionCheckBox.setSelected(false);
        removeOldRequest();
        textFieldValue.setText("");
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(new File(fileName)));
            String line;
            int row = 0;
            while ((line = reader.readLine()) != null) {
                req.addElements(row);
                sqlListModel.add(row, "");
                String element = null;
                StringTokenizer st = new StringTokenizer(line, "\t", false);
                int idx = 0;
                while (st.hasMoreTokens()) {
                    if (row == 0 && idx == 0) {
                        req.setLogicalOper("", row);
                        idx++;
                    }
                    element = st.nextToken();
                    updateRequestElement(row, idx, element);
                    idx++;
                }
                row++;
            }
            updateSqlRequest();
        }
        finally {
            reader.close();
        }
    }


    /**
     * Permet de mettre à jour un élément de la requête.
     *
     * @param row     La ligne en cours de lecture
     * @param idx     L'élément de la ligne
     * @param element L'élément
     *
     * @throws Exception                -
     * @throws IllegalArgumentException TODO
     */
    private void updateRequestElement(int row, int idx, String element)
          throws Exception {
        switch (idx) {
            case 0:
                req.setLogicalOper(element, row);
                break;
            case 1:
                req.setTable(th.getTable(element), row);
                break;
            case 2:
                req.setField(element, row);
                break;
            case 3:
                req.setCompareOper(Integer.parseInt(element), row);
                break;
            case 4:
                req.setPrefixValue(element, row);
                break;
            case 5:
                req.setValue(element, row);
                break;
            case 6:
                req.setSuffixValue(element, row);
                break;
            default:
                throw new IllegalArgumentException("Trop d'éléments !");
        }
    }


    /**
     * Sauvegarde la requête courante dans un fichier texte.
     *
     * @param fileName Le nom du fichier à sauvegarder
     *
     * @throws IOException -
     */
    private void saveRequest(String fileName) throws IOException {
        FileWriter out = null;
        try {
            out = new FileWriter(new File(fileName));
            for (int i = 0; i < sqlListModel.size(); i++) {
                out.write(req.getLogicalOper(i) + "\t" + req.getTable(i).getDBTableName()
                          + "\t" + req.getField(i) + "\t" + req.getCompareOperValue(i) + "\t"
                          + req.getPrefixValue(i) + "\t" + req.getValue(i) + "\t"
                          + req.getSuffixValue(i) + "\r" + "\n");
            }
        }
        finally {
            if (out != null) {
                out.close();
            }
        }
    }


    /**
     * Classe gérant la sélection sur la liste des champs de la table courante.
     *
     * @author $Author: marcona $
     * @version $Revision: 1.8 $
     */
    private class CurrentFieldSelectionListener implements ListSelectionListener {
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
            }
            else {
                if (sqlListModel.isEmpty()) {
                    req.addElements(0);
                    sqlListModel.add(0, "");
                }
                req.setField(((String)listCurrentFields.getSelectedValue()), getIndex());
                req.setTable(table.getTable(), getIndex());
                if (req.getCompareOperValue(getIndex()) != -1) {
                    req.updatePrefSuffValue(req.getCompareOperValue(getIndex()),
                                            getIndex(), findSqlType());
                }
                updateSqlRequest();
            }
            listCurrentFields.clearSelection();
        }
    }

    /**
     * Classe gérant la sélection sur la liste des champs de la table liée.
     *
     * @author $Author: marcona $
     * @version $Revision: 1.8 $
     */
    private class LinkFieldSelectionListener implements ListSelectionListener {
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
            }
            else {
                if (sqlListModel.isEmpty()) {
                    sqlListModel.add(0, "");
                    req.addElements(0);
                }

                req.setTable(linkTablesComboBox.getSelectedTable(), getIndex());
                req.setField(listLinkFields.getSelectedValue().toString(), getIndex());

                if (req.getCompareOperValue(getIndex()) != -1) {
                    req.updatePrefSuffValue(req.getCompareOperValue(getIndex()),
                                            getIndex(), findSqlType());
                }
                updateSqlRequest();
                listLinkFields.clearSelection();
            }
        }
    }

    /**
     * Classe gérant la sélection sur la liste des opérateurs.
     *
     * @author $Author: marcona $
     * @version $Revision: 1.8 $
     */
    private class OperatorSelectionListener implements ListSelectionListener {
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
            }
            else {
                if (!sqlListModel.isEmpty()) {
                    if (!"".equalsIgnoreCase(req.getField(getIndex()))) {
                        req.setCompareOper(listOperators.getSelectedIndex(), getIndex());
                        req.updatePrefSuffValue(listOperators.getSelectedIndex(),
                                                getIndex(), findSqlType());
                        updateSqlRequest();
                        if ("Est null".equals(listOperators.getSelectedValue())
                            || "Est pas null".equals(listOperators.getSelectedValue())) {
                            labelValue.setVisible(false);
                            textFieldValue.setVisible(false);
                        }
                        else {
                            labelValue.setVisible(true);
                            textFieldValue.setVisible(true);
                            textFieldValue.setEditable(true);
                            textFieldValue.selectAll();
                            textFieldValue.requestFocus();
                        }
                    }
                }
            }
            listOperators.clearSelection();
        }
    }

    /**
     * Classe gérant la sélection sur la liste des requêtes.
     *
     * @author $Author: marcona $
     * @version $Revision: 1.8 $
     */
    private class SqlRequestSelectionListener implements ListSelectionListener {
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
            }
            else {
                if (!sqlListModel.isEmpty()) {
                    textFieldValue.setText(removeQuote(req.getValue(
                          listSqlRequest.getSelectedIndex())));
                }
                else {
                    textFieldValue.setText("");
                }
            }
        }
    }
}
