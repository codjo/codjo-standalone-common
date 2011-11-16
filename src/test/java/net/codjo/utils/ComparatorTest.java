/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.utils;
import net.codjo.model.Table;
import net.codjo.model.TableHome;
import fakedb.FakeDriver;
import java.math.BigDecimal;
import java.sql.Types;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
/**
 * Test de la classe Comparator
 *
 * @author $Author: spinae $
 * @version $Revision: 1.2 $
 */
public class ComparatorTest extends TestCase {
    Comparator comparator;
    TableHome tablehome;
    TestEnvironnement testEnv;

    /**
     * Constructor for the ComparatorTest object
     *
     * @param name Description of Parameter
     */
    public ComparatorTest(String name) {
        super(name);
    }

    /**
     * A unit test suite for JUnit
     *
     * @return The test suite
     */
    public static Test suite() {
        return new TestSuite(ComparatorTest.class);
    }


    /**
     * A unit test for JUnit
     *
     * @exception Exception Description of Exception
     */
    public void test_Equals_BadComparison() throws Exception {
        Table table1 = getTable(1);
        Table table2 = getTable(2);
        fakeTable(2, 100, 0);
        fakeTable(1, 100, 1);
        FakeDriver.getDriver().pushResultSet(FakeDriver.RESULT_ONE,
            "select count(*) from TABLE_2");
        FakeDriver.getDriver().pushResultSet(FakeDriver.RESULT_ONE,
            "select count(*) from TABLE_1");
        assertEquals(comparator.Equals(table1, table2), false);
    }


    /**
     * A unit test for JUnit
     *
     * @exception Exception Description of Exception
     */
    public void test_Equals_GoodComparison() throws Exception {
        Table table1 = getTable(1);
        Table table2 = getTable(2);
        fakeTable(2, 100, 0);
        fakeTable(1, 100, 0);
        FakeDriver.getDriver().pushResultSet(FakeDriver.RESULT_ONE,
            "select count(*) from TABLE_2");
        FakeDriver.getDriver().pushResultSet(FakeDriver.RESULT_ONE,
            "select count(*) from TABLE_1");
        assertEquals(comparator.Equals(table1, table2), true);
    }


    /**
     * A unit test for JUnit
     */
    public void test_isEqual() {
        assertTrue("null, null", comparator.isEqual(null, null));
        assertTrue("!null, null", comparator.isEqual(comparator, null) == false);
        assertTrue("int : 1,1", comparator.isEqual(new Integer(1), new Integer(1)));
        assertTrue("int : 2,1",
            comparator.isEqual(new Integer(2), new Integer(1)) == false);
        assertTrue("BigDecimal : 2.1 , 2.1",
            comparator.isEqual(new BigDecimal(2.1), new BigDecimal(2.1)));
        assertTrue("BigDecimal : 2.1 , 2.2",
            comparator.isEqual(new BigDecimal(2.1), new BigDecimal(2.2)) == false);
        assertTrue("String : a , a", comparator.isEqual("a", "a"));
        assertTrue("String : a , b", comparator.isEqual("a", "b") == false);
    }


    public void test_isEqual_Precision() {
        comparator.setPrecision(1);
        assertTrue("int : 2,1", comparator.isEqual(new Integer(2), new Integer(1)));
        assertTrue("int : 9,10", comparator.isEqual(new Integer(9), new Integer(10)));

        comparator.setPrecision(0.9);
        assertTrue("BigDecimal (precision 0.9): 2.1 , 2.8",
            comparator.isEqual(new BigDecimal(2.1), new BigDecimal(2.8)));
    }


    public void test_isEqual_Precision_BUG() {
        comparator.setPrecision(0.9);
        assertTrue(comparator.isEqual(new BigDecimal("105.85000"),
                new BigDecimal("10585.00000")) == false);
        assertTrue(comparator.isEqual(new BigDecimal("-10"), new BigDecimal("10")) == false);
        assertTrue(comparator.isEqual(new BigDecimal("10"), new BigDecimal("-10")) == false);
    }


    /**
     * A unit test for JUnit
     *
     * @exception Exception Description of Exception
     */
    public void test_setPrecision() throws Exception {
        Table table1 = getTable(1);
        Table table2 = getTable(2);
        fakeTable(2, 1, 0, ".1");
        fakeTable(1, 1, 0, ".01");
        FakeDriver.getDriver().pushResultSet(FakeDriver.RESULT_ONE,
            "select count(*) from TABLE_2");
        FakeDriver.getDriver().pushResultSet(FakeDriver.RESULT_ONE,
            "select count(*) from TABLE_1");
        comparator.setPrecision(0.1);
        assertEquals(comparator.Equals(table1, table2), true);
    }


    /**
     * The JUnit setup method
     *
     * @exception Exception Description of Exception
     */
    protected void setUp() throws Exception {
        TestEnvironnement.forceFakeDriver();
        testEnv = TestEnvironnement.newEnvironment();
        tablehome = testEnv.getTableHome();
        comparator = new Comparator(testEnv);
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
     * @param id Description of Parameter
     * @param nbRow Description of Parameter
     * @param val Description of Parameter
     */
    private void fakeTable(int id, int nbRow, int val) {
        fakeTable(id, nbRow, val, "0");
    }


    /**
     * Description of the Method
     *
     * @param id Description of Parameter
     * @param nbRow Description of Parameter
     * @param val Description of Parameter
     * @param decimal Description of Parameter
     */
    private void fakeTable(int id, int nbRow, int val, String decimal) {
        Object[][] matrix = new Object[nbRow + 1][4];
        matrix[0][0] = "COL_A";
        matrix[0][1] = "COL_B";
        matrix[0][2] = "COL_C";
        matrix[0][3] = "COL_D";

        for (int i = 1; i <= nbRow; i++) {
            matrix[i][0] = "ROW_" + i / 100 + "_A";
            matrix[i][1] = "ROW_" + i / 10 + "_B";
            matrix[i][2] = "ROW_" + (i + val) + "_C";
            matrix[i][3] = new BigDecimal(i + decimal);
        }

        FakeDriver.getDriver().pushResultSet(matrix, "select * from TABLE_" + id);
    }


    /**
     * Gets the Table attribute of the ComparatorTest object
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
                {null, null, null, "COL_A", new Integer(Types.VARCHAR)},
                {null, null, null, "COL_B", new Integer(Types.VARCHAR)},
                {null, null, null, "COL_C", new Integer(Types.VARCHAR)},
                {null, null, null, "COL_D", new Integer(Types.NUMERIC)},
            };
        FakeDriver.getDriver().pushResultSet(tableDef,
            "FakeDatabaseMetaData.getColumns(null, null, TABLE_" + id + ", null)");
        FakeDriver.getDriver().pushResultSet(FakeDriver.EMPTY,
            "select * from PM_TABLE where DB_TABLE_NAME='TABLE_" + id + "'");
        return tablehome.getTable("TABLE_" + id);
    }
}
