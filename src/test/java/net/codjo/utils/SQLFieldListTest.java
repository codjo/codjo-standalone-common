/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.utils;
import fakedb.FakeDriver;
import java.sql.SQLException;
import java.sql.Types;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
/**
 * DOCUMENT ME!
 *
 * @author $Author: gonnot $
 * @version $Revision: 1.1.1.1 $
 */
public class SQLFieldListTest extends TestCase {
    TestEnvironnement testEnv;

    /**
     * Constructor for the SQLFieldListTest object
     *
     * @param Name_ Description of Parameter
     */
    public SQLFieldListTest(String Name_) {
        super(Name_);
    }

    /**
     * A unit test for JUnit
     */
    public void test_clear() {
        SQLFieldList list = new SQLFieldList();
        list.addStringField("a");
        list.setFieldValue("a", "valA");
        assertEquals(list.getFieldValue("a"), "valA");
        list.clearValues();
        assertEquals(list.getFieldValue("a"), null);
    }


    public void test_addAll() {
        SQLFieldList listA = new SQLFieldList();
        listA.addStringField("a");
        listA.setFieldValue("a", "listA_valA");
        listA.addStringField("c");
        listA.setFieldValue("c", "listA_valC");

        SQLFieldList listB = new SQLFieldList();
        listB.addStringField("a");
        listB.setFieldValue("a", "listB_valA");
        listB.addStringField("b");
        listB.setFieldValue("b", "listB_valB");

        listA.addAll(listB);
        assertEquals(listA.getFieldValue("a"), "listB_valA");
        assertEquals(listA.getFieldValue("b"), "listB_valB");
        assertEquals(listA.getFieldValue("c"), "listA_valC");
    }


    /**
     * A unit test for JUnit
     */
    public void test_removeField() {
        SQLFieldList list = new SQLFieldList();
        list.addStringField("a");
        list.addStringField("b");
        list.setFieldValue("a", "valA");
        assertEquals(list.getFieldValue("a"), "valA");
        list.removeField("a");
        try {
            list.getFieldValue("a");
            fail("Colonne a supprime");
        }
        catch (Exception ex) {}
    }


    /**
     * A unit test for JUnit
     *
     * @exception Exception Description of Exception
     */
    public void test_constructor() throws Exception {
        initMetadata();
        SQLFieldList list = new SQLFieldList("A_TABLE", testEnv.getHomeConnection());

        // Verifie que le champs existe
        assertEquals(list.getFieldValue("COL_B"), null);
        assertEquals(list.getFieldType("COL_B"), java.sql.Types.VARCHAR);
    }


    /**
     * A unit test for JUnit
     *
     * @exception Exception Description of Exception
     */
    public void test_sortDBFieldList() throws Exception {
        initMetadata();
        SQLFieldList list = new SQLFieldList("A_TABLE", testEnv.getHomeConnection());

        // Verifie que les champs sont triés
        assertEquals((list.getSortedDBFieldNameList()).get(0), "COL_A");
        assertEquals((list.getSortedDBFieldNameList()).get(1), "COL_B");
    }


    /**
     * The JUnit setup method
     *
     * @exception Exception Description of Exception
     */
    protected void setUp() throws Exception {
        TestEnvironnement.forceFakeDriver();
        testEnv = TestEnvironnement.newEnvironment();
    }


    /**
     * The teardown method for JUnit
     *
     * @exception SQLException Description of Exception
     */
    protected void tearDown() throws SQLException {
        testEnv.close();
    }


    /**
     * Overview.
     */
    private void initMetadata() {
        Object[][] tableDef =
            {
                {},
                {null, null, null, "COL_B", new Integer(Types.VARCHAR)},
                {null, null, null, "COL_A", new Integer(Types.INTEGER)},
                {null, null, null, "COL_C", new Integer(Types.NUMERIC)},
            };
        FakeDriver.getDriver().pushResultSet(tableDef,
            "FakeDatabaseMetaData.getColumns(null, null, A_TABLE, null)");
    }


    /**
     * A unit test suite for JUnit
     *
     * @return The test suite
     */
    public static Test suite() {
        return new TestSuite(SQLFieldListTest.class);
    }
}
