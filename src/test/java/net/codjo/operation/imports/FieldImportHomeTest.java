/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.operation.imports;
import net.codjo.persistent.PersistenceException;
import net.codjo.persistent.Reference;
import net.codjo.utils.TestEnvironnement;
import fakedb.FakeDriver;
import java.sql.SQLException;
import java.sql.Types;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
/**
 * Test
 *
 * @author $Author: spinae $
 * @version $Revision: 1.2 $
 */
public class FieldImportHomeTest extends TestCase {
    TestEnvironnement testEnv;
    FieldImportHome fieldImportHome;


    /**
     * Constructor for the FieldImportHomeTest object
     *
     * @param name Description of Parameter
     */
    public FieldImportHomeTest(String name) {
        super(name);
    }


    /**
     * A unit test suite for JUnit
     *
     * @return The test suite
     */
    public static Test suite() {
        return new TestSuite(FieldImportHomeTest.class);
    }


    /**
     * A unit test for JUnit
     *
     * @throws PersistenceException Description of Exception
     */
    public void test_getReference_String() throws PersistenceException {
        fakePortfolioCodeImportRow();

        Reference ref = fieldImportHome.getReference(14, "PORTFOLIO_CODE");
        assertNotNull(ref);

        FieldImport f = (FieldImport)ref.getObject();
        assertTrue("StringFieldImport", f instanceof StringFieldImport);

        assertEquals("Position", f.getPosition(), 25);
        assertEquals("Longueur", f.getLength(), 6);
    }


    /**
     * A unit test for JUnit
     *
     * @throws Exception Description of Exception
     */
    public void test_save() throws Exception {
        fieldImportHome.getConnection().setAutoCommit(false);
        fakePortfolioCodeImportRow();
        Reference ref = fieldImportHome.getReference(14, "PORTFOLIO_CODE");
        ref.getObject().setSynchronized(false);

        FakeDriver.getDriver()
              .pushUpdateConstraint("update PM_FIELD_IMPORT_SETTINGS "
                                    + "set "
                                    + "DECIMAL_SEPARATOR=null , "
                                    + "DB_DESTINATION_FIELD_NAME=PORTFOLIO_CODE , "
                                    + "REMOVE_LEFT_ZEROS=false , "
                                    + "POSITION=25 , "
                                    + "IMPORT_SETTINGS_ID=14 , "
                                    + "DESTINATION_FIELD_TYPE=S , "
                                    + "LENGTH=6 , "
                                    + "INPUT_DATE_FORMAT=null "
                                    + "where "
                                    + "DB_DESTINATION_FIELD_NAME=PORTFOLIO_CODE "
                                    + "and IMPORT_SETTINGS_ID=14");
        ref.getObject().save();
        fieldImportHome.getConnection().rollback();
    }


    /**
     * A unit test for JUnit
     *
     * @throws PersistenceException Description of Exception
     */
    public void test_getReference_Number() throws PersistenceException {
        fakeNumberImportRow();
        Reference ref = fieldImportHome.getReference(18, "QUANTITY");
        assertNotNull(ref);

        NumberFieldImport f = (NumberFieldImport)ref.getObject();

        assertEquals("Longueur", f.getLength(), 18);
    }


    /**
     * The JUnit setup method
     *
     * @throws SQLException Description of the Exception
     */
    protected void setUp() throws SQLException {
        TestEnvironnement.forceFakeDriver();
        testEnv = TestEnvironnement.newEnvironment();
        fakeFieldImportSettingsTableRow();

        fieldImportHome = new FieldImportHome(testEnv.getHomeConnection());
    }


    /**
     * The teardown method for JUnit
     */
    protected void tearDown() {
        try {
            testEnv.getConnectionManager().releaseConnection(fieldImportHome
                  .getConnection());
        }
        catch (Exception ex) {
        }
        testEnv.close();
    }


    /**
     * Description of the Method
     */
    private void fakePortfolioCodeImportRow() {
        Object[][] rs =
              {
                    {
                          "IMPORT_SETTINGS_ID", "POSITION", "LENGTH",
                          "DB_DESTINATION_FIELD_NAME", "DESTINATION_FIELD_TYPE",
                          "INPUT_DATE_FORMAT", "REMOVE_LEFT_ZEROS", "DECIMAL_SEPARATOR"
                    },
                    {
                          new Integer(14), new Integer(25), new Integer(6), "PORTFOLIO_CODE",
                          "S", new Integer(0), Boolean.FALSE, null
                    }
              };
        FakeDriver.getDriver().pushResultSet(rs,
                                             "select * from PM_FIELD_IMPORT_SETTINGS "
                                             + "where DB_DESTINATION_FIELD_NAME=PORTFOLIO_CODE "
                                             + "and IMPORT_SETTINGS_ID=14");
    }


    private void fakeNumberImportRow() {
        Object[][] rs =
              {
                    {
                          "IMPORT_SETTINGS_ID", "POSITION", "LENGTH",
                          "DB_DESTINATION_FIELD_NAME", "DESTINATION_FIELD_TYPE",
                          "INPUT_DATE_FORMAT", "REMOVE_LEFT_ZEROS", "DECIMAL_SEPARATOR"
                    },
                    {
                          new Integer(18), new Integer(25), new Integer(18), "QUANTITY", "N",
                          null, Boolean.FALSE, "."
                    }
              };
        FakeDriver.getDriver().pushResultSet(rs,
                                             "select * from PM_FIELD_IMPORT_SETTINGS "
                                             + "where DB_DESTINATION_FIELD_NAME=QUANTITY "
                                             + "and IMPORT_SETTINGS_ID=18");
    }


    /**
     * Description of the Method
     */
    private void fakeFieldImportSettingsTableRow() {
        Object[][] tableDef =
              {
                    {},
                    {null, null, null, "IMPORT_SETTINGS_ID", new Integer(Types.INTEGER)},
                    {null, null, null, "POSITION", new Integer(Types.INTEGER)},
                    {null, null, null, "LENGTH", new Integer(Types.INTEGER)},
                    {null, null, null, "DB_DESTINATION_FIELD_NAME", new Integer(Types.VARCHAR)},
                    {null, null, null, "DESTINATION_FIELD_TYPE", new Integer(Types.CHAR)},
                    {null, null, null, "INPUT_DATE_FORMAT", new Integer(Types.INTEGER)},
                    {null, null, null, "REMOVE_LEFT_ZEROS", new Integer(Types.BIT)},
                    {null, null, null, "DECIMAL_SEPARATOR", new Integer(Types.VARCHAR)}
              };
        FakeDriver.getDriver().pushResultSet(tableDef,
                                             "FakeDatabaseMetaData.getColumns(null, null, PM_FIELD_IMPORT_SETTINGS, null)");
    }
}
