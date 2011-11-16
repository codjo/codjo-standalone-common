/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.utils;
import net.codjo.model.Period;
import net.codjo.model.PeriodHome;
import net.codjo.model.PortfolioGroupHome;
import net.codjo.model.Table;
import net.codjo.model.TableHome;
import net.codjo.persistent.PersistenceException;
import net.codjo.util.system.WindowsExec;
import fakedb.FakeDriver;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 * Environnement de test.
 *
 * @author $Author: acharif $
 * @version $Revision: 1.6 $
 */
public class TestEnvironnement {
    // Factory stuff
    private static Class environmentClass;
    private static boolean forceFakeDriver = false;
    private static boolean disableFakeDriver = false;
    /**
     * Description of the Field
     */
    protected TableHome tableHome;
    private String catalog;
    private String dbUrl;
    private String driverClass;

    // Exec Stuff
    private WindowsExec executoor = new WindowsExec();

    // Db Connection
    private Connection homeConnection = null;
    private ConnectionManager manager = null;
    private boolean numericTruncationWarning = false;
    private String password;

    // Builtin object
    private PeriodHome periodHome;
    private PortfolioGroupHome portfolioGroupHome;
    private String server;

    // Database Init Information
    private String user;
    // Log
    private static final Logger APP = Logger.getLogger(TestEnvironnement.class);


    /**
     * Constructor for the TestEnvironnement object
     *
     * @param usr     Db User
     * @param pwd     User pwd
     * @param driver  Driver JDBC
     * @param dbUrl   DB URL
     * @param catalog Catalogue de la base
     * @param server  Description of the Parameter
     */
    protected TestEnvironnement(String usr, String pwd, String driver, String dbUrl,
                                String catalog, String server) {
        this(usr, pwd, driver, dbUrl, catalog, server, false);
    }


    /**
     * Constructeur de TestEnvironnement
     *
     * @param usr                      Description of the Parameter
     * @param pwd                      Description of the Parameter
     * @param driver                   Description of the Parameter
     * @param dbUrl                    Description of the Parameter
     * @param catalog                  Description of the Parameter
     * @param server                   Description of the Parameter
     * @param numericTruncationWarning Description of the Parameter
     */
    protected TestEnvironnement(String usr, String pwd, String driver, String dbUrl,
                                String catalog, String server, boolean numericTruncationWarning) {
        this.numericTruncationWarning = numericTruncationWarning;
        if (isFakeDriverOn()) {
            APP.debug("Mode FAKE_DRIVER");
            user = "usr";
            password = "pwd";
            driverClass = "fakedb.FakeDriver";
            this.dbUrl = "jdbc:fakeDriver";
            this.catalog = null;
            this.server = null;
        }
        else {
            environmentClass = this.getClass();
            APP.debug("Mode DB : " + environmentClass);
            user = usr;
            password = pwd;
            driverClass = driver;
            this.dbUrl = dbUrl;
            this.catalog = catalog;
            this.server = server;
        }
    }


    /**
     * Constructor for the TestEnvironnement object
     */
    protected TestEnvironnement() {
        this(null, null, null, null, null, null);
    }


    /**
     * Retourne le chemin racine de l'application. Exemple : D:\Penelope
     *
     * @return La valeur de rootPath
     */
    public static String getRootPath() {
        String rootPath = System.getProperty("ROOT_PATH");
        if (rootPath == null) {
            // La property "user.dir" renvoie le chemin D:\Penelope\Project
            // Donc on rajoute "\\..\\" pour revenir dans le repertoire de
            // l'application.
            rootPath = System.getProperty("user.dir") + "\\..\\";
        }
        return rootPath;
    }


    /**
     * Indique si l'environnement est initialise avec le FakeDriver.
     *
     * @return The FakeDriverOn value
     */
    public static boolean isFakeDriverOn() {
        if (disableFakeDriver == true) {
            return false;
        }
        return forceFakeDriver || "ON".equals(System.getProperty("FAKE_DRIVER"));
    }


    /**
     * Force le mode FakeDriver.
     */
    public static void forceFakeDriver() {
        forceFakeDriver = true;
    }


    /**
     * Description of the Method
     */
    public static void disableFakeDriver() {
        disableFakeDriver = true;
    }


    /**
     * Factory d'environment.
     *
     * @return Description of the Returned Value
     *
     * @throws RuntimeException TODO
     */
    public static TestEnvironnement newEnvironment() {
        if (isFakeDriverOn()) {
            return new TestEnvironnement();
        }
        try {
            if (environmentClass == null) {
                String c = System.getProperty("TEST_ENVIRONMENT");
                environmentClass = Class.forName(c);
            }

            return (TestEnvironnement)environmentClass.newInstance();
        }
        catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("Impossible de creer un environement de test : "
                                       + " Assurez-vous que la property TEST_ENVIRONMENT existe");
        }
    }


    /**
     * Description of the Method
     *
     * @param con       Description of the Parameter
     * @param tableName Description of the Parameter
     *
     * @throws SQLException Description of the Exception
     */
    public static void spoolTable(Connection con, String tableName)
          throws SQLException {
        Statement stmt = con.createStatement();
        APP.debug("******************** SPOOL de " + tableName
                  + "********************");
        try {
            ResultSet rs = stmt.executeQuery("select * from " + tableName);
            ResultSetMetaData rsmd = rs.getMetaData();

            // Spool Header
            int colmumnCount = rsmd.getColumnCount();
            String strColmumnCount = "";
            for (int i = 1; i <= colmumnCount; i++) {
                strColmumnCount += "\t" + rsmd.getColumnName(i);
            }
            APP.debug(strColmumnCount);

            strColmumnCount = "";
            for (int i = 1; i <= colmumnCount; i++) {
                strColmumnCount += "\t" + rsmd.getColumnTypeName(i);
            }
            APP.debug(strColmumnCount);

            // Spool Content
            strColmumnCount = "";
            while (rs.next()) {
                for (int i = 1; i <= colmumnCount; i++) {
                    strColmumnCount += "\t" + rs.getObject(i);
                }
                APP.debug(strColmumnCount);
            }
        }
        finally {
            stmt.close();
        }
    }


    /**
     * Retourne l'attribut columns de TestEnvironnement
     *
     * @param tableName Description of the Parameter
     *
     * @return La valeur de columns
     *
     * @throws NoSuchElementException Description of the Exception
     */
    public List getColumns(String tableName) throws NoSuchElementException {
        try {
            Table table = getTableHome().getTable(tableName);
            return new ArrayList(table.getAllColumns().keySet());
        }
        catch (Exception ex) {
            throw new NoSuchElementException("table " + tableName + " est inconnu");
        }
    }


    /**
     * Retourne le pool de connection.
     *
     * @return The ConnectionManager value
     */
    public ConnectionManager getConnectionManager() {
        if (manager == null) {
            builConnectionManager();
        }
        return manager;
    }


    /**
     * Retourne la connection utilise par les home.
     *
     * @return The HomeConnection value
     *
     * @throws SQLException Description of Exception
     */
    public Connection getHomeConnection() throws SQLException {
        if (homeConnection == null) {
            homeConnection = getConnectionManager().getConnection();
        }
        return homeConnection;
    }


    /**
     * Gets the Period000000 attribute of the TestEnvironnement object
     *
     * @return The Period000000 value
     *
     * @throws SQLException Description of Exception
     * @throws Error        TODO
     */
    public Period getPeriod200008() throws SQLException {
        try {
            Object[][] matrix = {
                  {"PERIOD", "VISIBLE"},
                  {"200008", Boolean.TRUE}
            };
            FakeDriver.getDriver().pushResultSet(matrix);
            return (Period)getPeriodHome().getReference("200008").getObject();
        }
        catch (PersistenceException ex) {
            throw new Error("Erreur");
        }
    }


    /**
     * Gets the Period000011 attribute of the TestEnvironnement object
     *
     * @return The Period000011 value
     *
     * @throws SQLException Description of Exception
     * @throws Error        TODO
     */
    public Period getPeriod200011() throws SQLException {
        try {
            Object[][] matrix = {
                  {"PERIOD", "VISIBLE"},
                  {"200011", Boolean.FALSE}
            };
            FakeDriver.getDriver().pushResultSet(matrix);
            return (Period)getPeriodHome().getReference("200011").getObject();
        }
        catch (PersistenceException ex) {
            throw new Error("Erreur");
        }
    }


    /**
     * Gets the Period200012 attribute of the TestEnvironnement object
     *
     * @return The Period200012 value
     *
     * @throws SQLException Description of Exception
     * @throws Error        TODO
     */
    public Period getPeriod200012() throws SQLException {
        try {
            Object[][] matrix = {
                  {"PERIOD", "VISIBLE"},
                  {"200012", Boolean.TRUE}
            };
            FakeDriver.getDriver().pushResultSet(matrix);
            return (Period)getPeriodHome().getReference("200012").getObject();
        }
        catch (PersistenceException ex) {
            throw new Error("Erreur");
        }
    }


    /**
     * Gets the Period200101 attribute of the TestEnvironnement object
     *
     * @return The Period200101 value
     *
     * @throws SQLException Description of Exception
     * @throws Error        TODO
     */
    public Period getPeriod200101() throws SQLException {
        try {
            Object[][] matrix = {
                  {"PERIOD", "VISIBLE"},
                  {"200101", Boolean.TRUE}
            };
            FakeDriver.getDriver().pushResultSet(matrix);
            return (Period)getPeriodHome().getReference("200101").getObject();
        }
        catch (PersistenceException ex) {
            throw new Error("Erreur");
        }
    }


    /**
     * Gets the Period200106 attribute of the TestEnvironnement object
     *
     * @return The Period200106 value
     *
     * @throws SQLException Description of Exception
     * @throws Error        TODO
     */
    public Period getPeriod200106() throws SQLException {
        try {
            Object[][] matrix = {
                  {"PERIOD", "VISIBLE"},
                  {"200106", Boolean.TRUE}
            };
            FakeDriver.getDriver().pushResultSet(matrix);
            return (Period)getPeriodHome().getReference("200106").getObject();
        }
        catch (PersistenceException ex) {
            throw new Error("Erreur");
        }
    }


    /**
     * Gets the Period200107 attribute of the TestEnvironnement object
     *
     * @return The Period200107 value
     *
     * @throws SQLException Description of Exception
     * @throws Error        TODO
     */
    public Period getPeriod200107() throws SQLException {
        try {
            Object[][] matrix = {
                  {"PERIOD", "VISIBLE"},
                  {"200107", Boolean.TRUE}
            };
            FakeDriver.getDriver().pushResultSet(matrix);
            return (Period)getPeriodHome().getReference("200107").getObject();
        }
        catch (PersistenceException ex) {
            throw new Error("Erreur");
        }
    }


    /**
     * Gets the Period200108 attribute of the TestEnvironnement object
     *
     * @return The Period200108 value
     *
     * @throws SQLException Description of Exception
     * @throws Error        TODO
     */
    public Period getPeriod200108() throws SQLException {
        try {
            Object[][] matrix = {
                  {"PERIOD", "VISIBLE"},
                  {"200108", Boolean.TRUE}
            };
            FakeDriver.getDriver().pushResultSet(matrix);
            return (Period)getPeriodHome().getReference("200108").getObject();
        }
        catch (PersistenceException ex) {
            throw new Error("Erreur");
        }
    }


    /**
     * Gets the PeriodHome attribute of the TestEnvironnement object
     *
     * @return The PeriodHome value
     *
     * @throws SQLException Description of Exception
     */
    public PeriodHome getPeriodHome() throws SQLException {
        if (periodHome == null) {
            Object[][] matrix =
                  {
                        {},
                        {null, null, null, "PERIOD", new Integer(Types.VARCHAR)}
                  };
            FakeDriver.getDriver().pushResultSet(matrix);
            periodHome = new PeriodHome(getHomeConnection());
        }
        return periodHome;
    }


    /**
     * Gets the PortfolioGroupHome attribute of the TestEnvironnement object
     *
     * @return The PortfolioGroupHome value
     *
     * @throws SQLException Description of Exception
     */
    public PortfolioGroupHome getPortfolioGroupHome()
          throws SQLException {
        if (portfolioGroupHome == null) {
            Object[][] matrix =
                  {
                        {},
                        {null, null, null, "PORTFOLIO_GROUP", new Integer(Types.VARCHAR)},
                        {null, null, null, "PORTFOLIO_GROUP_ID", new Integer(Types.INTEGER)}
                  };
            FakeDriver.getDriver().pushResultSet(matrix);
            portfolioGroupHome = new PortfolioGroupHome(getHomeConnection());
        }
        return portfolioGroupHome;
    }


    /**
     * Retourne le type sql du champ <code>fieldName</code> de la table <code>tableName</code>
     *
     * @return La valeur de sqlType
     */
    public int getSqlType(String tableName, String fieldName)
          throws NoSuchElementException {
        try {
            Table table = getTableHome().getTable(tableName);
            return table.getColumnSqlType(fieldName);
        }
        catch (Exception ex) {
//            e.printStackTrace();
            throw new NoSuchElementException("field " + tableName + "." + fieldName
                                             + " est inconnu");
        }
    }


    /**
     * Gets the TableHome attribute of the TestEnvironnement object
     *
     * @return The TableHome value
     *
     * @throws SQLException Description of Exception
     */
    public TableHome getTableHome() throws SQLException {
        if (tableHome == null) {
            Object[][] matrix =
                  {
                        {},
                        {null, null, null, "DB_TABLE_NAME_ID", new Integer(Types.INTEGER)},
                        {null, null, null, "DB_TABLE_NAME", new Integer(Types.VARCHAR)},
                        {null, null, null, "TABLE_NAME", new Integer(Types.VARCHAR)},
                        {null, null, null, "STEP", new Integer(Types.VARCHAR)},
                        {null, null, null, "SOURCE_SYSTEM", new Integer(Types.VARCHAR)},
                        {null, null, null, "RECORDING_MODE", new Integer(Types.INTEGER)},
                        {null, null, null, "APPLICATION", new Integer(Types.VARCHAR)}
                  };
            FakeDriver.getDriver().pushResultSet(matrix);
            tableHome = new TableHome(getHomeConnection(), getConnectionManager());
        }
        return tableHome;
    }


    /**
     * Indique si la table existe en BD. Remarque cette methode utilise la connection des homes pour faire le
     * test.
     *
     * @param dbTableName Le nom physique de la table
     *
     * @return 'true' si elle existe, 'false' sinon.
     *
     * @throws IllegalStateException TODO
     */
    public boolean isDbTableExist(String dbTableName) {
        if (isFakeDriverOn()) {
            throw new IllegalStateException(
                  "isDbTableExist ne marche pas en mode FakeDriver");
        }
        else {
            try {
                getHomeConnection().createStatement().executeQuery("select * from "
                                                                   + dbTableName);
                return true;
            }
            catch (SQLException ex) {
                return false;
            }
        }
    }


    /**
     * Ferme l'environnement de connection.
     */
    public void close() {
        forceFakeDriver = false;
        disableFakeDriver = false;
        if (manager != null) {
            manager.closeAllConnections();
        }
    }


    /**
     * Efface la table.
     *
     * @param dbTableName le nom de la table
     *
     * @throws SQLException -
     */
    public void deleteTable(String dbTableName) throws SQLException {
        deleteTable(dbTableName, "");
    }


    /**
     * Efface la table en specifiant la clause where
     *
     * @param dbTableName Le nom de la table
     * @param whereClause La clause where avec le mot-cle where
     *
     * @throws SQLException -
     */
    public void deleteTable(String dbTableName, String whereClause)
          throws SQLException {
        Connection con = getHomeConnection();
        Statement stmt = con.createStatement();
        stmt.executeUpdate("delete " + dbTableName + " " + whereClause);
        stmt.close();
    }


    /**
     * Initialise le FakeDriver afin de simuler une table dans PM_TABLE.
     *
     * @param tableId Description of Parameter
     */
    public void fakeTableRow(int tableId) {
        Object[][] matrix =
              {
                    {
                          "DB_TABLE_NAME_ID", "DB_TABLE_NAME", "TABLE_NAME", "STEP",
                          "SOURCE_SYSTEM", "RECORDING_MODE", "APPLICATION"
                    },
                    {
                          new Integer(tableId), "TABLE_" + tableId,
                          "une table d'index " + tableId, null, null, new Integer(2), "TEST_TU"
                    }
              };
        FakeDriver.getDriver().pushResultSet(matrix,
                                             "select * from PM_TABLE where DB_TABLE_NAME_ID=" + tableId);
    }


    /**
     * Initialise la table. Cette methode bloque le thread courant tant que le bcp n'est pas termine.
     *
     * @param dbTableName Le nom de la table
     * @param fileName    Nom du fichier BCP pour initialiser la table
     *
     * @throws Error TODO
     */
    public void initTable(String dbTableName, String fileName)
          throws Exception {
        deleteTable(dbTableName);
//        String bcpFile = System.getProperty("user.dir") + "\\..\\" + fileName;
        String bcpFile = getRootPath() + fileName;

        int r =
              executoor.exec("bcp " + catalog + ".." + dbTableName + " in \"" + bcpFile
                             + "\"" + " -U" + user + " -P" + password + " -S" + server + " -c");

        if (r != 0) {
            throw new Error("Le bcp a echoue : " + dbTableName);
        }
    }


    /**
     * Construction du pool de connection.
     */
    private void builConnectionManager() {
        try {
            Properties props = new Properties();
            props.put("USER", user);
            props.put("PASSWORD", password);
            props.put("HOSTNAME", "TU_" + System.getProperty("user.name"));

            manager =
                  new ConnectionManager(driverClass, dbUrl, catalog, props,
                                        numericTruncationWarning);
        }
        catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
