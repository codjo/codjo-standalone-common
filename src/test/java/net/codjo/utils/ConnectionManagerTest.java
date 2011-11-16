/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.utils;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
/**
 * DOCUMENT ME!
 *
 * @version $Revision: 1.2 $
 */
public class ConnectionManagerTest extends TestCase {
    ConnectionManager manager;

    public static Test suite() {
        return new TestSuite(ConnectionManagerTest.class);
    }


    public void testAutoCloseConnection() throws Exception {
        //Test que la connection n'est pas fermée après un release
        Connection firstConnection = manager.getConnection();
        manager.releaseConnection(firstConnection);
        Connection secondConnection = manager.getConnection();
        manager.releaseConnection(secondConnection);
        assertTrue(firstConnection == secondConnection);

        manager.setCloseDelay(500);

        Object obj = new Object();
        synchronized (obj) {
            obj.wait(1000);
        }

        //Test que la connection est fermée après le delai
        Connection delayConnection = manager.getConnection();
        assertTrue(secondConnection != delayConnection);
        assertTrue(secondConnection.isClosed());
    }


    public void testConnectionAccessRelease() throws SQLException {
        Connection connection = manager.getConnection();
        assertTrue(!connection.isClosed());
        manager.releaseConnection(connection);
    }


    public void testConnectionAfterCloseAll() throws SQLException {
        Connection connection = manager.getConnection();
        assertTrue("Connection ouverte", !connection.isClosed());
        manager.closeAllConnections();
        assertTrue("Connection ferme", connection.isClosed());
    }


    public void testConstructor() throws Exception {
        Connection connection = manager.getConnection();

        createTemporaryTable(connection);

        // Statement
        insertByStatement(connection);

        checkTemporaryTableContainsRow(connection);
        deleteTemporaryTable(connection);

        // PreparedStatement
        PreparedStatement preparedStatement =
            connection.prepareStatement("insert into #TU_BOBO values (?)");
        preparedStatement.setBigDecimal(1, new java.math.BigDecimal("125.235"));
        int nbOfInsertedRow = preparedStatement.executeUpdate();
        assertTrue("1 Ligne insérée ", nbOfInsertedRow == 1);
        assertTrue("Troncature sans warning ", preparedStatement.getWarnings() == null);
        preparedStatement.close();

        checkTemporaryTableContainsRow(connection);
        deleteTemporaryTable(connection);

        // CallableStatement
        CallableStatement callableStatement =
            connection.prepareCall("insert into #TU_BOBO values (?)");
        callableStatement.setBigDecimal(1, new java.math.BigDecimal("125.235"));
        nbOfInsertedRow = callableStatement.executeUpdate();
        assertTrue("1 Ligne insérée ", nbOfInsertedRow == 1);
        assertTrue("Troncature sans warning ", callableStatement.getWarnings() == null);
        callableStatement.close();

        checkTemporaryTableContainsRow(connection);
        deleteTemporaryTable(connection);
    }


    public void testConstructorWithNumericTruncationWarning()
            throws Exception {
        manager.closeAllConnections();
        manager = createConnectionManager("LIB_INT", true);

        Connection connection = manager.getConnection();
        createTemporaryTable(connection);

        try {
            insertByStatement(connection);
            fail("Une exception est lancee a l'insertion");
        }
        catch (SQLException e) {
            ;
        }

        checkTemporaryTableIsEmpty(connection);

        // PreparedStatement
        PreparedStatement preparedStatement =
            connection.prepareStatement("insert into #TU_BOBO values (?)");
        preparedStatement.setBigDecimal(1, new java.math.BigDecimal("125.235"));
        int nbOfInsertedRow = preparedStatement.executeUpdate();
        assertTrue("Aucune Ligne insérée ", nbOfInsertedRow == 0);
        assertTrue("Troncature avec un warning ", preparedStatement.getWarnings() != null);
        preparedStatement.close();

        checkTemporaryTableIsEmpty(connection);

        // CallableStatement
        CallableStatement callableStatement =
            connection.prepareCall("insert into #TU_BOBO values (?)");
        callableStatement.setBigDecimal(1, new java.math.BigDecimal("125.235"));
        nbOfInsertedRow = callableStatement.executeUpdate();
        assertTrue("Aucune Ligne insérée ", nbOfInsertedRow == 0);
        assertTrue("Troncature avec un warning ", callableStatement.getWarnings() != null);
        callableStatement.close();

        checkTemporaryTableIsEmpty(connection);
    }


    public void testGetConnectionAfterBadRelease()
            throws SQLException {
        Connection connection = manager.getConnection();
        connection.close();
        manager.releaseConnection(connection);

        connection = manager.getConnection();
        assertTrue(!connection.isClosed());
    }


    public void testReleaseAllConnections() throws SQLException, ClassNotFoundException {
        manager.releaseConnection(manager.getConnection());
        manager.closeAllConnections();

        ConnectionManager otherConnectionManager =
            createConnectionManager("LIB_INT", false);
        otherConnectionManager.releaseConnection(otherConnectionManager.getConnection());
        manager.closeAllConnections();
        otherConnectionManager.closeAllConnections();
    }


    private ConnectionManager createConnectionManager(String catalog,
        boolean numericTruncationWarning) throws ClassNotFoundException {
        Properties properties = new Properties();
        properties.put("USER", "LIB_INT_dbo");
        properties.put("PASSWORD", "LIB_INT_dbo");
        return new ConnectionManager("com.sybase.jdbc2.jdbc.SybDriver",
            "jdbc:sybase:Tds:ai-lib12:34100", catalog, properties, numericTruncationWarning);
    }

    public void testReleaseConnection() throws SQLException {
        manager.releaseConnection(null);
    }


    public void test_addNewConnection() throws SQLException {
        manager.addNewConnection();
        manager.addNewConnection();
        manager.addNewConnection();
    }


    @Override
    protected void setUp() throws ClassNotFoundException {
        manager = createConnectionManager(null, false);
    }


    @Override
    protected void tearDown() {
        manager.closeAllConnections();
    }


    private void checkTemporaryTableContainsRow(Connection connection)
            throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("select * from #TU_BOBO");
        assertTrue("Une ligne inseré", resultSet.next());
        assertEquals("125.23", resultSet.getString(1));
        assertTrue("Pas d'autre ligne", !resultSet.next());
        statement.close();
    }


    private void checkTemporaryTableIsEmpty(Connection connection)
            throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("select * from #TU_BOBO");
        assertTrue("La table est vide", !resultSet.next());
    }


    private void createTemporaryTable(Connection connection)
            throws SQLException {
        Statement statement = connection.createStatement();
        statement.executeUpdate("create table #TU_BOBO" + " ("
            + "AMOUNT                   	numeric(12,2)   null" + " )");
        statement.close();
    }


    private void deleteTemporaryTable(Connection connection)
            throws Exception {
        Statement statement = connection.createStatement();
        statement.executeUpdate("delete #TU_BOBO");
        statement.close();
    }


    private void insertByStatement(Connection connection)
            throws SQLException {
        Statement statement = connection.createStatement();
        int nbOfInsertedRow =
            statement.executeUpdate("insert into #TU_BOBO values (125.239)");

        assertTrue("Ligne insérée ", nbOfInsertedRow == 1);
        assertTrue("Troncature sans aucun warning (mode par défaut)",
            statement.getWarnings() == null);
        statement.close();
    }
}
