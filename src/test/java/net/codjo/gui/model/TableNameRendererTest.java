/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.gui.model;
import net.codjo.utils.TestEnvironnement;
import fakedb.FakeDriver;
import java.util.Arrays;
import java.util.List;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
/**
 * Test <code>TableNameRenderer</code> ;
 *
 * @author $Author: blazart $
 * @version $Revision: 1.2 $
 */
public class TableNameRendererTest extends TestCase {
    TestEnvironnement testEnv;
    TableNameRenderer renderer;

    /**
     * Constructor for the TableNameRendererTest object
     *
     * @param name Description of Parameter
     */
    public TableNameRendererTest(String name) {
        super(name);
    }

    /**
     * A unit test for JUnit
     */
    public void test_translateValue() {
        assertEquals(renderer.translateValue(new Integer(1)), "COURS DE CHANGE");
        assertEquals(renderer.translateValue(new Integer(24)), "GPF MOUVEMENTS TRANSCODES");
    }


    /**
     * A unit test for JUnit
     */
    public void test_translateValue_Unknown() {
        assertEquals(renderer.translateValue(new Integer(69)), "???????");
    }


    /**
     * A unit test for JUnit
     */
    public void test_getListId() {
        List listId = Arrays.asList(renderer.getTableIdList(null, null));
        assertTrue("no filter : AP_EXCHANGE_RATE", listId.contains(new Integer(1)));
        assertTrue("no filter : AP_GPF_TRANSACTION_TR", listId.contains(new Integer(24)));
        java.util.List stepImportee = new java.util.ArrayList();
        stepImportee.add("IMPORTEE");
        listId = Arrays.asList(renderer.getTableIdList(stepImportee, null));
        assertTrue("import : AP_EXCHANGE_RATE", listId.contains(new Integer(1)));
        assertTrue("import : AP_GPF_TRANSACTION_TR",
                   !listId.contains(new Integer(24)));
    }


    /**
     * The JUnit setup method
     *
     * @exception Exception Description of Exception
     */
    protected void setUp() throws Exception {
        TestEnvironnement.forceFakeDriver();
        testEnv = TestEnvironnement.newEnvironment();
        initPmTable();
        renderer = new TableNameRenderer(testEnv.getTableHome());
    }


    /**
     * The teardown method for JUnit
     */
    protected void tearDown() {
        testEnv.close();
    }


    /**
     * Initialise PM_TABLE.
     */
    public static void initPmTable() {
        // Init metadata pour AP_GPF_TRANSACTION_TR
        FakeDriver.getDriver().pushResultSet(FakeDriver.EMPTY,
            "FakeDatabaseMetaData.getPrimaryKeys(null, null, AP_GPF_TRANSACTION_TR)");
        FakeDriver.getDriver().pushResultSet(FakeDriver.EMPTY,
            "FakeDatabaseMetaData.getColumns(null, null, AP_GPF_TRANSACTION_TR, null)");
        // Init metadata pour AP_EXCHANGE_RATE
        FakeDriver.getDriver().pushResultSet(FakeDriver.EMPTY,
            "FakeDatabaseMetaData.getPrimaryKeys(null, null, AP_EXCHANGE_RATE)");
        FakeDriver.getDriver().pushResultSet(FakeDriver.EMPTY,
            "FakeDatabaseMetaData.getColumns(null, null, AP_EXCHANGE_RATE, null)");
        // Init PM_TABLE
        Object[][] matrix =
            {
                {
                    "DB_TABLE_NAME_ID", "DB_TABLE_NAME", "TABLE_NAME", "STEP",
                    "SOURCE_SYSTEM", "RECORDING_MODE", "APPLICATION"
                },
                {
                    new Integer(1), "AP_EXCHANGE_RATE", "COURS DE CHANGE", "IMPORTEE",
                    null, new Integer(2), "TEST_TU"
                },
                {
                    new Integer(24), "AP_GPF_TRANSACTION_TR", "GPF MOUVEMENTS TRANSCODES",
                    "TRANSCODEE", null, new Integer(2), "TEST_TU"
                }
            };
        FakeDriver.getDriver().pushResultSet(matrix, "select * from PM_TABLE");
    }


    /**
     * A unit test suite for JUnit
     *
     * @return The test suite
     */
    public static Test suite() {
        return new TestSuite(TableNameRendererTest.class);
    }
}
