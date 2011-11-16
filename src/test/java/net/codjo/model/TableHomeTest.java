/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.model;
import net.codjo.persistent.PersistenceException;
import net.codjo.persistent.Reference;
import net.codjo.utils.TestEnvironnement;
import fakedb.FakeDriver;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
/**
 * Test de TableHome et Table.
 *
 * @author $Author: blazart $
 * @version $Revision: 1.2 $
 */
public class TableHomeTest extends TestCase {
    private static final String createTableTestQuery =
          "create table TABLE_TEMP_FOR_TU ( NAME varchar(15) not null )";
    private static final String dropTableTestQuery = "drop table TABLE_TEMP_FOR_TU";
    private static final String insertTableTestQuery =
          "insert into PM_TABLE values(255, 'TABLE_TEMP_FOR_TU', 'une table', null, null, 2, 'PENELOPE')";
    private static final String deleteTableTestQuery =
          "delete from PM_TABLE where DB_TABLE_NAME='TABLE_TEMP_FOR_TU'";
    TestEnvironnement testEnv;
    TableHome tablehome;


    /**
     * Constructor for the TableHomeTest object
     *
     * @param name Description of Parameter
     */
    public TableHomeTest(String name) {
        super(name);
    }


    /**
     * A unit test for JUnit
     *
     * @throws Exception Description of Exception
     */
    public void test_containsColumn() throws Exception {
        Table table = getPmTable();
        assertTrue("Colonne existe", table.containsColumn("STEP"));
        assertTrue("Colonne n'existe pas", !table.containsColumn("BOBO"));
    }


    /**
     * A unit test for JUnit
     *
     * @throws Exception Description of Exception
     */
    public void test_getPkNames() throws Exception {
        Table table = getPmTable();
        assertEquals("Une pk", table.getPkNames().size(), 1);
        assertTrue("Colonne pk", table.getPkNames().contains("DB_TABLE_NAME_ID"));
    }


    /**
     * A unit test for JUnit
     *
     * @throws Exception Description of Exception
     */
    public void test_getPkNames_NoPK() throws Exception {
        Connection con = testEnv.getHomeConnection();
        Statement stmt = con.createStatement();
        try {
            FakeDriver.getDriver().pushUpdateConstraint(insertTableTestQuery);
            FakeDriver.getDriver().pushUpdateConstraint(createTableTestQuery);
            stmt.executeUpdate(createTableTestQuery);
            stmt.executeUpdate(insertTableTestQuery);
            Table table = (Table)getTableTempForTU().getObject();
            assertEquals("Une pk", table.getPkNames().size(), 0);
        }
        finally {
            FakeDriver.getDriver().pushUpdateConstraint(dropTableTestQuery);
            FakeDriver.getDriver().pushUpdateConstraint(deleteTableTestQuery);
            stmt.executeUpdate(deleteTableTestQuery);
            stmt.executeUpdate(dropTableTestQuery);
        }
    }


    /**
     * A unit test for JUnit
     *
     * @throws Exception Description of Exception
     */
    public void test_getReference() throws Exception {
        Connection con = testEnv.getHomeConnection();
        Statement stmt = con.createStatement();
        try {
            FakeDriver.getDriver().pushUpdateConstraint(insertTableTestQuery);
            FakeDriver.getDriver().pushUpdateConstraint(createTableTestQuery);
            stmt.executeUpdate(createTableTestQuery);
            stmt.executeUpdate(insertTableTestQuery);

            Reference ref = getTableTempForTU();
            assertNotNull(ref);
            Table table = (Table)ref.getObject();
            assertEquals(table.getDBTableName(), "TABLE_TEMP_FOR_TU");
            assertEquals(table.getRecordingMode(),
                         TableRecordingMode.BY_PERIOD_AND_PORTFOLIOGROUP);
        }
        finally {
            FakeDriver.getDriver().pushUpdateConstraint(dropTableTestQuery);
            FakeDriver.getDriver().pushUpdateConstraint(deleteTableTestQuery);
            stmt.executeUpdate(deleteTableTestQuery);
            stmt.executeUpdate(dropTableTestQuery);
        }
    }


    /**
     * A unit test for JUnit
     *
     * @throws Exception Description of Exception
     */
    public void test_getTable_DuringTransaction()
          throws Exception {
        tablehome.getConnection().setAutoCommit(false);
        Table table = getPmTable();
        assertEquals(table.getDBTableName(), "PM_TABLE");
        tablehome.getConnection().rollback();
    }


    /**
     * A unit test for JUnit
     *
     * @throws Exception Description of Exception
     */
    public void test_getTable_notInPMTable() throws Exception {
//		Table table = getPmTable();
//		assertNotNull(table);
//		assertEquals(table.getDBTableName(), "PM_TABLE");
//		assertNull("La table PM_TABLE n'est pas dans la table PM_TABLE", table.getTableName());
    }


    /**
     * A unit test for JUnit
     *
     * @throws Exception Description of Exception
     */
    public void test_getTable_inPMTable() throws Exception {
        Connection con = testEnv.getHomeConnection();
        Statement stmt = con.createStatement();
        try {
            FakeDriver.getDriver().pushUpdateConstraint(insertTableTestQuery);
            FakeDriver.getDriver().pushUpdateConstraint(createTableTestQuery);
            stmt.executeUpdate(createTableTestQuery);
            stmt.executeUpdate(insertTableTestQuery);
            Table table = (Table)getTableTempForTU().getObject();
            assertEquals(table.getDBTableName(), "TABLE_TEMP_FOR_TU");
            assertEquals(table.getTableName(), "une table");
            assertEquals(table.getTableStep(), null);
            assertEquals(table.getSource(), null);
            assertEquals(table.getRecordingMode(),
                         TableRecordingMode.BY_PERIOD_AND_PORTFOLIOGROUP);
        }
        finally {
            FakeDriver.getDriver().pushUpdateConstraint(dropTableTestQuery);
            FakeDriver.getDriver().pushUpdateConstraint(deleteTableTestQuery);
            stmt.executeUpdate(deleteTableTestQuery);
            stmt.executeUpdate(dropTableTestQuery);
        }
    }


    /**
     * A unit test for JUnit
     *
     * @throws PersistenceException Description of Exception
     */
    public void test_getTable_UnknownTable() throws PersistenceException {
        FakeDriver.getDriver().pushResultSet(FakeDriver.EMPTY,
                                             "FakeDatabaseMetaData.getColumns(null, null, BAD_TABLE_NAME, null)");
        FakeDriver.getDriver().pushResultSet(FakeDriver.EMPTY,
                                             "select * from PM_TABLE where DB_TABLE_NAME='BAD_TABLE_NAME'");

        Table ma_table = tablehome.getTable("BAD_TABLE_NAME");
        assertNull("Table inconnue mais trouvee", ma_table);
    }


    /**
     * A unit test for JUnit
     *
     * @throws Exception Description of Exception
     */
    public void test_getTable() throws Exception {
        Table ma_table = getPmTable();
        assertEquals(ma_table.getDBTableName(), "PM_TABLE");
        assertEquals(ma_table.getNumberOfCol(), 7);
    }


    /**
     * The JUnit setup method
     *
     * @throws Exception Description of Exception
     */
    protected void setUp() throws Exception {
        TestEnvironnement.forceFakeDriver();
        testEnv = TestEnvironnement.newEnvironment();
        tablehome = testEnv.getTableHome();
    }


    /**
     * The teardown method for JUnit
     */
    protected void tearDown() {
        testEnv.close();
    }


    /**
     * Overview.
     *
     * @throws SQLException Description of Exception
     */
    void initPmTableColumns() throws SQLException {
        Object[][] tableDef =
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
        FakeDriver.getDriver().pushResultSet(tableDef,
                                             "FakeDatabaseMetaData.getColumns(null, null, PM_TABLE, null)");
    }


    /**
     * Gets the TableTempForTU attribute of the TableHomeTest object
     *
     * @return The TableTempForTU value
     *
     * @throws Exception Description of Exception
     */
    private Reference getTableTempForTU() throws Exception {
        // Requete MetaData pour connaitre la table
        FakeDriver.getDriver().pushResultSet(FakeDriver.EMPTY,
                                             "FakeDatabaseMetaData.getPrimaryKeys(null, null, TABLE_TEMP_FOR_TU)");
        FakeDriver.getDriver().pushResultSet(FakeDriver.EMPTY,
                                             "FakeDatabaseMetaData.getColumns(null, null, TABLE_TEMP_FOR_TU, null)");

        // Requete du select dans la table
        Object[][] matrix =
              {
                    {
                          "DB_TABLE_NAME_ID", "DB_TABLE_NAME", "TABLE_NAME", "STEP",
                          "SOURCE_SYSTEM", "RECORDING_MODE", "APPLICATION"
                    },
                    {
                          new Integer(255), "TABLE_TEMP_FOR_TU", "une table", null, null,
                          new Integer(2), "TEST_TU"
                    }
              };
        FakeDriver.getDriver().pushResultSet(matrix,
                                             "select * from PM_TABLE where DB_TABLE_NAME_ID=255");

        return tablehome.getReference(255);
    }


    /**
     * Gets the PmTable attribute of the TableHomeTest object
     *
     * @return The PmTable value
     *
     * @throws Exception Description of Exception
     */
    private Table getPmTable() throws Exception {
        // Requete MetaData pour connaitre la table
        Object[][] tablePk = {
              {"COLUMN_NAME"},
              {"DB_TABLE_NAME_ID"}
        };
        FakeDriver.getDriver().pushResultSet(tablePk,
                                             "FakeDatabaseMetaData.getPrimaryKeys(null, null, PM_TABLE)");
        initPmTableColumns();

        // Requete du select dans la table
        FakeDriver.getDriver().pushResultSet(FakeDriver.EMPTY,
                                             "select * from PM_TABLE where DB_TABLE_NAME='PM_TABLE'");

        return tablehome.getTable("PM_TABLE");
    }


    /**
     * A unit test suite for JUnit
     *
     * @return The test suite
     */
    public static Test suite() {
        return new TestSuite(TableHomeTest.class);
    }
}
