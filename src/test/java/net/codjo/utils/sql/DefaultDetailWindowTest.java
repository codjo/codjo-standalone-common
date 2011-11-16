/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.utils.sql;
import net.codjo.model.Table;
import net.codjo.utils.TestEnvironnement;
import fakedb.FakeDriver;
import java.sql.Types;
import java.util.List;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
/**
 * Tests sur la classe DefaultDetailWindow
 *
 * @author $Author: gonnot $
 * @version $Revision: 1.1.1.1 $
 */
public class DefaultDetailWindowTest extends TestCase {
    TestEnvironnement testEnv;
    DefaultDetailWindow detailWindow;

    /**
     * Constructor for the DefaultDetailWindowTest object
     *
     * @param name Description of Parameter
     */
    public DefaultDetailWindowTest(String name) {
        super(name);
    }

    /**
     * Teste que la defaultWindow a bien créér les composants. (16 composants car la clé
     * n'est pas prise)
     *
     * @throws Exception TODO
     */
    public void test_numberofComponent() throws Exception {
        FakeDriver.getDriver().pushResultSet(FakeDriver.EMPTY,
            "select DB_FIELD_NAME, FIELD_LABEL from PM_FIELD_LABEL where DB_TABLE_NAME='TABLE_1' ORDER BY FIELD_LABEL");
        FakeDriver.getDriver().pushResultSet(FakeDriver.EMPTY,
            "select DB_FIELD_NAME, FIELD_LABEL from PM_FIELD_LABEL where DB_TABLE_NAME='TABLE_1' ORDER BY FIELD_LABEL");
        detailWindow =
            new DefaultDetailWindow(getTable(1), testEnv.getConnectionManager());
        List componentList = detailWindow.getListOfComponents();
        assertEquals(componentList.size(), 5);
    }


    /**
     * The JUnit setup method
     *
     * @exception Exception Description of Exception
     */
    protected void setUp() throws Exception {
        TestEnvironnement.forceFakeDriver();
        testEnv = TestEnvironnement.newEnvironment();
        Dependency.setConnectionManager(testEnv.getConnectionManager());
    }


    /**
     * The teardown method for JUnit
     */
    protected void tearDown() {
        testEnv.close();
    }


    /**
     * Gets the Table attribute of the DefaultDetailWindowTest object
     *
     * @param id Description of Parameter
     *
     * @return The Table value
     *
     * @exception Exception Description of Exception
     */
    private Table getTable(int id) throws Exception {
        // Requete MetaData pour connaitre la table
        FakeDriver.getDriver().pushResultSet(FakeDriver.EMPTY,
            "FakeDatabaseMetaData.getPrimaryKeys(null, null, TABLE_" + id + ")");
        Object[][] tableDef =
            {
                {},
                {null, null, null, "COL_A", new Integer(Types.NUMERIC)},
                {null, null, null, "COL_B", new Integer(Types.VARCHAR)},
                {null, null, null, "COL_C", new Integer(Types.DATE)},
                {null, null, null, "COL_D", new Integer(Types.BIT)},
                {null, null, null, "COL_E", new Integer(Types.DISTINCT)}
            };
        FakeDriver.getDriver().pushResultSet(tableDef,
            "FakeDatabaseMetaData.getColumns(null, null, TABLE_" + id + ", null)");
        FakeDriver.getDriver().pushResultSet(FakeDriver.EMPTY,
            "select * from PM_TABLE where DB_TABLE_NAME='TABLE_" + id + "'");
        return testEnv.getTableHome().getTable("TABLE_" + id);
    }


    /**
     * A unit test suite for JUnit
     *
     * @return The test suite
     */
    public static Test suite() {
        return new TestSuite(DefaultDetailWindowTest.class);
    }
}
