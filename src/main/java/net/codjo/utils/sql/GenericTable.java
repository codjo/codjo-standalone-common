/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.utils.sql;
import net.codjo.gui.renderer.NumberFormatRenderer;
import net.codjo.model.Table;
import net.codjo.persistent.Reference;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
/**
 * Affichage générique d'une table.
 *
 * <p></p>
 *
 * @author $Author: marcona $
 * @version $Revision: 1.5 $
 */
public class GenericTable extends JTable implements GenericTableInterface {
    // Creation Renderer pour cellules type Numeric
    NumberFormatRenderer numberFormatRenderer = new NumberFormatRenderer(Locale.FRENCH);
    private List columnSizeList = new ArrayList();
    private String initialFromAndWhereClause;
    private String lastFromAndWhereWhereClause;
    private TableRendererSorter modelSorter;
    private int pageNumber;
    private GenericTableModel tableModel;
    private Reference tableRef;


    /**
     * Constructeur à deux francs.
     */
    public GenericTable() {
    }


    /**
     * Contructeur 5
     *
     * @param con         La connection à utiliser
     * @param tableDest   La table destination
     * @param readOnly    Table en lecture seule
     * @param whereClause Clause where initiale
     *
     * @throws SQLException Erreur SQL
     */
    public GenericTable(Connection con, Table tableDest, boolean readOnly,
                        String whereClause) throws SQLException {
        this(con, tableDest, readOnly, whereClause, null);
    }


    /**
     * Constructeur final GenericTable
     *
     * @param con           La connection à utiliser
     * @param tableDest     La table destination
     * @param readOnly      Table en lecture seule
     * @param whereClause   Clause where initiale
     * @param orderByClause Clause order by (eg "order by LABEL")
     * @param columnsToSkip Liste des colonnes à ne pas afficher (permet de faire un paramètrage PM_GUI_FIELDS
     *                      utilisable differemment par deux GenericTable)
     *
     * @throws SQLException             Description of the Exception
     * @throws IllegalArgumentException : Paramètres incorrects !
     */
    public GenericTable(Connection con, Table tableDest, boolean readOnly,
                        String whereClause, String orderByClause, ArrayList columnsToSkip)
          throws SQLException {
        if (tableDest == null) {
            throw new IllegalArgumentException("Paramètres incorrects !");
        }
        pageNumber = 0;
        tableRef = tableDest.getReference();
        initialFromAndWhereClause =
              "From " + tableDest.getDBTableName() + " " + whereClause;
        lastFromAndWhereWhereClause = initialFromAndWhereClause;
        List columnNameList = new ArrayList();
        List editableColumnList = new ArrayList();

        if (columnsToSkip == null) {
            columnsToSkip = new ArrayList();
        }

        Connection tmp = con;
        if (con == null) {
            tmp = Dependency.getConnectionManager().getConnection();
        }
        try {
            loadPreferences(tmp, columnNameList, editableColumnList, columnsToSkip);
        }
        finally {
            if (con == null) {
                Dependency.getConnectionManager().releaseConnection(tmp);
            }
        }

        tableModel =
              new GenericTableModel(con, tableRef, columnNameList, editableColumnList,
                                    readOnly, whereClause, orderByClause);

        modelSorter = new TableRendererSorter(this);
        modelSorter.addMouseListenerToHeaderInTable(this);
        this.setModel(modelSorter);

        // Applique le Renderer pour les Numeric
        this.setDefaultRenderer(Number.class, numberFormatRenderer);

        this.createDefaultColumnsFromModel();
        modelSorter.changeHeaderRenderer(this);
        this.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        // Fixe la taille des colonnes.
        for (int i = 0; i < columnNameList.size(); i++) {
            int size = ((Integer)columnSizeList.get(i)).intValue();
            if (size != 0) {
                this.getColumn(tableModel.getColumnName(i)).setMinWidth(size);
                this.getColumn(tableModel.getColumnName(i)).setMaxWidth(size);
            }
            else {
                this.getColumn(tableModel.getColumnName(i)).setPreferredWidth(250);
            }
        }

        this.setAutoResizeMode(0);
        setName("genericTable." + tableDest.getTableName());
    }


    /**
     * Contructeur 6
     *
     * @param con           La connection à utiliser
     * @param tableDest     La table destination
     * @param readOnly      Table en lecture seule
     * @param whereClause   Clause where initiale
     * @param orderByClause Clause order by (eg "order by LABEL")
     *
     * @throws SQLException Erreur SQL
     */
    public GenericTable(Connection con, Table tableDest, boolean readOnly,
                        String whereClause, String orderByClause)
          throws SQLException {
        this(con, tableDest, readOnly, whereClause, orderByClause, null);
    }


    /**
     * Constructeur 2
     *
     * @param tableDest   La table destination
     * @param readOnly    Table en lecture seule
     * @param whereClause Clause where initiale
     *
     * @throws SQLException Erreur SQL
     */
    public GenericTable(Table tableDest, boolean readOnly, String whereClause)
          throws SQLException {
        this(tableDest, readOnly, whereClause, "");
    }


    /**
     * Constructeur 3
     *
     * @param tableDest     La table destination
     * @param readOnly      Table en lecture seule
     * @param whereClause   Clause where initiale
     * @param orderByClause Clause order by (eg "order by LABEL")
     *
     * @throws SQLException
     */
    public GenericTable(Table tableDest, boolean readOnly, String whereClause,
                        String orderByClause) throws SQLException {
        this(null, tableDest, readOnly, whereClause, orderByClause);
    }


    /**
     * Constructeur 4
     *
     * @param con       La connection à utiliser
     * @param tableDest L'objet table destination
     * @param readOnly  Composant en lecture seule
     *
     * @throws SQLException Oups SQL
     */
    public GenericTable(Connection con, Table tableDest, boolean readOnly)
          throws SQLException {
        this(con, tableDest, readOnly, "");
    }


    /**
     * Constructor 1
     *
     * @param tableDest L'objet table destination
     * @param readOnly  Composant en lecture seule
     *
     * @throws SQLException Oups SQL
     */
    public GenericTable(Table tableDest, boolean readOnly)
          throws SQLException {
        this(tableDest, readOnly, "");
    }


    /**
     * Ajoute une nouvelle ligne dans le model
     */
    public void addNewLine() {
        tableModel.addNewLine();
    }


    /**
     * Supprime une ligne dans le model
     *
     * @param index Description of Parameter
     */
    public void deleteLine(int index) {
        tableModel.deleteLine(index);
    }


    /**
     * Recharge les données avec la clause WHERE initiale
     *
     * @throws SQLException On a essayé d'executer n'importe quoi
     */
    public void displayAll() throws SQLException {
        lastFromAndWhereWhereClause = initialFromAndWhereClause;
        pageNumber = 0;
        tableModel.reloadData(initialFromAndWhereClause, pageNumber);
    }


    /**
     * Retourne la TableColumn de <code>dbFieldName</code>.
     *
     * @param dbFieldName le nom physique du champ
     *
     * @return la TableColumn
     */
    public TableColumn getColumnByDbField(String dbFieldName) {
        return getColumnModel().getColumn(getColumnNumber(dbFieldName));
    }


    /**
     * Retourne la liste des noms physiques des colonnes affichées
     *
     * @return La liste des colonnes
     */
    public List getColumnDBNameList() {
        return this.getTableModel().getColumnDBNameList();
    }


    /**
     * Retourne le numéro de la colonne / dbName
     *
     * @param columnDBName Le nom physique de la colonne
     *
     * @return La position de la colonne
     */
    public int getColumnNumber(String columnDBName) {
        int columnNumber = this.getTableModel().getColumnNumber(columnDBName);
        return this.convertColumnIndexToView(columnNumber);
    }


    /**
     * Récupère (HashMap) la et les clés de l'objet.
     *
     * @param lineNumber Le numéro de la ligne
     *
     * @return Une hashtable contenant la ou les clés
     */
    public Map getKey(int lineNumber) {
        Map hm;
        int realIndex = ((Integer)getModel().getValueAt(lineNumber, -1)).intValue();
        hm = this.getTableModel().getALineOfKey(realIndex);
        return hm;
    }


    /**
     * Retourne le numéro de la premiere ligne de la page courante
     *
     * @return Le numéro
     */
    public int getNumberOfFirstRow() {
        return tableModel.getNumberOfFirstRow();
    }


    /**
     * Retourne le numéro de la dernière ligne de la page courante
     *
     * @return Le numéro
     */
    public int getNumberOfLastRow() {
        return tableModel.getNumberOfLastRow();
    }


    /**
     * Retourne le nombre de lignes (toutes pages confondues)
     *
     * @return Le nombre de lignes
     */
    public int getNumberOfRows() {
        return tableModel.getNumberOfRows();
    }


    /**
     * Retourne l'attribut orderByClause de GenericTable
     *
     * @return La valeur de orderByClause
     */
    public String getOrderByClause() {
        return tableModel.getOrderByClause();
    }


    /**
     * Retourne le numéro de page en cours
     *
     * @return Le numéro de page
     */
    public int getPageNumber() {
        return pageNumber;
    }


    /**
     * Retourne la table
     *
     * @return The TableName value
     */
    public Table getTable() {
        return (Table)tableRef.getLoadedObject();
    }


    /**
     * Retourne le model du composant GenericTable
     *
     * @return Le model
     */
    public GenericTableModel getTableModel() {
        return tableModel;
    }


    /**
     * Reste-t'il des données après la page courante
     *
     * @return Oui ou Non !
     */
    public boolean hasMoreData() {
        return tableModel.hasMoreData();
    }


    /**
     * Chargement de la page suivante de données
     */
    public void nextPage() {
        pageNumber++;
        try {
            tableModel.reloadData(lastFromAndWhereWhereClause, pageNumber);
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
    }


    /**
     * Chargement de la page précédente de données
     */
    public void previousPage() {
        pageNumber--;
        try {
            tableModel.reloadData(lastFromAndWhereWhereClause, pageNumber);
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
    }


    /**
     * Recharge les données avec la dernière clause utilisée
     *
     * @throws SQLException On a essayé d'executer n'importe quoi
     */
    public void refreshData() throws SQLException {
        tableModel.reloadData(lastFromAndWhereWhereClause, pageNumber);
    }


    /**
     * Recharge les données en appliquant une nouvelle clause
     *
     * @param newFromAndWhereClause     La nouvelle clause
     * @param replaceInitialWhereClause On remplace la clause initiale ?
     *
     * @throws SQLException On a essayé d'executer n'importe quoi
     */
    public void reloadData(String newFromAndWhereClause, boolean replaceInitialWhereClause)
          throws SQLException {
        if (replaceInitialWhereClause) {
            initialFromAndWhereClause = newFromAndWhereClause;
        }
        lastFromAndWhereWhereClause = newFromAndWhereClause;
        pageNumber = 0;
        tableModel.reloadData(newFromAndWhereClause, pageNumber);
    }


    /**
     * Recharge les données en appliquant une autre clause where
     *
     * @param newFromAndWhereClause Description of Parameter
     *
     * @throws SQLException Description of Exception
     */
    public void reloadData(String newFromAndWhereClause)
          throws SQLException {
        reloadData(newFromAndWhereClause, false);
    }


    /**
     * Recharge les données (toutes les pages) avec la dernière clause utilisée
     *
     * @throws SQLException Description of Exception
     */
    public void reloadData() throws SQLException {
        reloadData(lastFromAndWhereWhereClause, false);
    }


    /**
     * Mettre la table en mode redimensionnement automatique
     */
    public void setModeAutoResize() {
        this.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    }


    /**
     * On ne veut récupérer que les valeurs distinctes
     *
     * @param distinctMode La nouvelle valeur de distinctModeOn
     */
    public void setDistinctModeOn(boolean distinctMode) {
        tableModel.setDistinctModeOn(distinctMode);
    }


    /**
     * Défini la nouvelle connexion à utiliser
     *
     * @param con La nouvelle connexion
     *
     * @throws IllegalArgumentException TODO
     */
    public void setNewConnection(Connection con) {
        if (con == null) {
            throw new IllegalArgumentException("La nouvelle connexion est nulle !");
        }
        tableModel.setNewConnection(con);
    }


    /**
     * Positionne l'attribut model de GenericTable
     *
     * @param dataModel La nouvelle valeur de model
     */
    public void setModel(TableModel dataModel) {
        super.setModel(dataModel);
        if (modelSorter != null) {
            this.createDefaultColumnsFromModel();
            modelSorter.changeHeaderRenderer(this);
        }
    }


    /**
     * Change la clause <code>order by</code> de rafraichissement de la table. Aucun rechargement est
     * effectué.
     *
     * @param orderByClause La clause avec le mot clef "order by" (ex: "order by LABEL desc, ID")
     *
     * @throws SQLException
     */
    public void setOrderByClause(String orderByClause)
          throws SQLException {
        tableModel.setOrderByClause(orderByClause);
    }


    /**
     * Initialise avec des preferences par defaut.
     *
     * @param columnNameList     La liste des noms de colonnes à remplir
     * @param editableColumnList Colonnes editables
     */
    private void initDefaultPreferences(List columnNameList, List editableColumnList) {
        columnNameList.clear();
        //Pas de paramètrage trouvé, on prend toutes les colonnes de la table
        for (Iterator it = this.getTable().getAllColumns().keySet().iterator();
             it.hasNext();) {
            String s = (String)it.next();
            columnNameList.add(s);
            columnSizeList.add(new Integer(0));
            editableColumnList.add(Boolean.FALSE);
        }
    }


    /**
     * Charge les préférences pour l'affichage des colonnes : position et longueur du champ.
     *
     * @param con                Description of Parameter
     * @param columnNameList     La liste des noms de colonnes à remplir
     * @param editableColumnList Description of Parameter
     * @param columnsToSkip      TODO
     *
     * @throws SQLException SQL
     */
    private void loadPreferences(Connection con, List columnNameList,
                                 List editableColumnList, ArrayList columnsToSkip)
          throws SQLException {
        String query;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = con.createStatement();
            query =
                  "select * from PM_GUI_FIELDS where DB_TABLE_NAME='"
                  + this.getTable().getDBTableName() + "' order by COLUMN_INDEX";
            rs = stmt.executeQuery(query);

            if (!rs.next()) {
                initDefaultPreferences(columnNameList, editableColumnList);
            }
            else {
                do {
                    if ((rs.getInt("COLUMN_INDEX") != 0)
                        && (!columnsToSkip.contains(rs.getString("DB_FIELD_NAME")))) {
                        columnNameList.add(rs.getString("DB_FIELD_NAME"));
                        columnSizeList.add(new Integer(rs.getInt("SIZE")));
                        editableColumnList.add((rs.getBoolean("EDITABLE")) ? Boolean.TRUE
                                               : Boolean.FALSE);
                    }
                }
                while (rs.next());
            }
        }
        finally {
            if (stmt != null) {
                stmt.close();
            }
        }
    }
}
