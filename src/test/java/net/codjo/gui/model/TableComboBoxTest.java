/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.gui.model;
import net.codjo.utils.TestEnvironnement;
import java.util.ArrayList;
import java.util.List;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
/**
 * Test <code>TableComboBox</code> ;
 *
 * @author $Author: blazart $
 * @version $Revision: 1.2 $
 */
public class TableComboBoxTest extends TestCase {
    TestEnvironnement testEnv;
    TableComboBox combo;


    /**
     * Constructor for the TableComboBoxTest object
     *
     * @param name Description of Parameter
     */
    public TableComboBoxTest(String name) {
        super(name);
    }


    /**
     * A unit test for JUnit
     */
    public void test_contains() {
        assertTrue("Table AP_EXCHANGE_RATE", combo.contains(1));
        assertTrue("Table AP_GPF_TRANSACTION_TR", combo.contains(24));
    }


    /**
     * A unit test for JUnit
     *
     * @throws Exception Description of Exception
     */
    public void test_constructor() throws Exception {
        TableNameRendererTest.initPmTable();
        combo = new TableComboBox(testEnv.getTableHome(), "IMPORTEE");
        assertTrue("Table AP_EXCHANGE_RATE", combo.contains(1));
        assertTrue("Table AP_GPF_TRANSACTION_TR", !combo.contains(24));
    }


    /**
     * A unit test for JUnit
     *
     * @throws Exception Description of Exception
     */
    public void test_constructorAllListSteps() throws Exception {
        List listStep = new ArrayList();
        listStep.add("IMPORTEE");
        listStep.add("TRANSCODEE");
        TableNameRendererTest.initPmTable();
        combo = new TableComboBox(testEnv.getTableHome(), listStep);
        assertTrue("Table AP_EXCHANGE_RATE", combo.contains(1));
        assertTrue("Table AP_GPF_TRANSACTION_TR", combo.contains(24));
    }


    /**
     * A unit test for JUnit
     *
     * @throws Exception Description of Exception
     */
    public void test_constructorImporteeListSteps()
          throws Exception {
        List listStep = new ArrayList();
        listStep.add("IMPORTEE");
        TableNameRendererTest.initPmTable();
        combo = new TableComboBox(testEnv.getTableHome(), listStep);
        assertTrue("Table AP_EXCHANGE_RATE", combo.contains(1));
        assertTrue("Table AP_GPF_TRANSACTION_TR", !combo.contains(24));
    }


    /**
     * A unit test for JUnit
     *
     * @throws Exception Description of Exception
     */
    public void test_constructorTranscodeeListSteps()
          throws Exception {
        List listStep = new ArrayList();
        listStep.add("TRANSCODEE");
        TableNameRendererTest.initPmTable();
        combo = new TableComboBox(testEnv.getTableHome(), listStep);
        assertTrue("Table AP_EXCHANGE_RATE", !combo.contains(1));
        assertTrue("Table AP_GPF_TRANSACTION_TR", combo.contains(24));
    }


    /**
     * A unit test for JUnit
     *
     * @throws Exception Description of Exception
     */
    public void test_constructorListId() throws Exception {
        List listId = new ArrayList();
        listId.add((Object)new Integer(36));
        TableNameRendererTest.initPmTable();
        combo = new TableComboBox(testEnv.getTableHome(), listId.toArray());
        assertTrue("Table BO_SECURITY", combo.contains(36));
        assertTrue("Table AP_GPF_TRANSACTION_TR", !combo.contains(24));
    }


    /**
     * A unit test for JUnit
     */
    public void test_getRenderer() {
        assertTrue(combo.getRenderer() instanceof TableNameRenderer);
    }


    /**
     * A unit test for JUnit
     */
    public void test_getSelectedTable() {
        combo.setSelectedIndex(-1);
        assertNull(combo.getSelectedTable());
        combo.setSelectedItem(new Integer(1));
        assertEquals(combo.getSelectedTable().getDBTableName(), "AP_EXCHANGE_RATE");
    }


    /**
     * The JUnit setup method
     *
     * @throws Exception Description of Exception
     */
    protected void setUp() throws Exception {
        System.getProperties().put("TEST_ENVIRONMENT", "net.codjo.utils.TestEnvironnement");
        TestEnvironnement.forceFakeDriver();
        testEnv = TestEnvironnement.newEnvironment();
        TableNameRendererTest.initPmTable();
        combo = new TableComboBox(testEnv.getTableHome());
    }


    /**
     * The teardown method for JUnit
     */
    protected void tearDown() {
        testEnv.close();
    }


    /**
     * A unit test suite for JUnit
     *
     * @return The test suite
     */
    public static Test suite() {
        return new TestSuite(TableComboBoxTest.class);
    }
}
