/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.operation.treatment;
import net.codjo.utils.TestEnvironnement;
import fakedb.FakeDriver;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
/**
 * DOCUMENT ME!
 *
 * @author $Author: blazart $
 * @version $Revision: 1.3 $
 */
public class BreakDetectorTest extends TestCase {
    TestEnvironnement testEnv;

    /**
     * Constructor for the UtilsTest object
     *
     * @param name Description of Parameter
     */
    public BreakDetectorTest(String name) {
        super(name);
    }

    /**
     * A unit test suite for JUnit
     *
     * @return The test suite
     */
    public static Test suite() {
        return new TestSuite(BreakDetectorTest.class);
    }


    /**
     * A unit test for JUnit
     *
     * @exception SQLException Description of Exception
     */
    public void test_isBreakPoint_NoAggregate() throws SQLException {
        BreakDetector breakDetectorNoAggregate = new BreakDetector();

        fakeTable(3);
        Connection con = testEnv.getConnectionManager().getConnection();
        PreparedStatement stmt = con.prepareStatement("select * from A_TABLE");
        ResultSet rs = stmt.executeQuery();

        assertTrue(rs.next());
        assertTrue(breakDetectorNoAggregate.isBreakPoint(rs) == false);
        assertTrue(rs.next());
        assertTrue(breakDetectorNoAggregate.isBreakPoint(rs) == true);
        assertTrue(rs.next());
        assertTrue(breakDetectorNoAggregate.isBreakPoint(rs) == true);
        assertTrue("Fin", rs.next() == false);
    }


    /**
     * A unit test for JUnit
     *
     * @exception SQLException Description of Exception
     */
    public void test_isBreakPoint_Aggregate() throws SQLException {
        String[] pk = {"COL_A", "COL_B"};
        BreakDetector breakDetector = new BreakDetector(pk);
        runTestAggregate(breakDetector);
    }


    /**
     * A unit test for JUnit
     *
     * @exception SQLException Description of Exception
     */
    public void test_clear() throws SQLException {
        String[] pk = {"COL_A", "COL_B"};
        BreakDetector breakDetector = new BreakDetector(pk);

        runTestAggregate(breakDetector);
        breakDetector.clear();
        runTestAggregate(breakDetector);
    }


    /**
     * The JUnit setup method
     */
    protected void setUp() {
        TestEnvironnement.forceFakeDriver();
        testEnv = TestEnvironnement.newEnvironment();
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
     * @param nbRow Description of Parameter
     */
    private void fakeTable(int nbRow) {
        Object[][] matrix = new Object[nbRow + 1][3];
        matrix[0][0] = "COL_A";
        matrix[0][1] = "COL_B";
        matrix[0][2] = "COL_C";

        for (int i = 1; i <= nbRow; i++) {
            matrix[i][0] = "ROW_" + i / 100 + "_A";
            matrix[i][1] = "ROW_" + i / 10 + "_B";
            matrix[i][2] = "ROW_" + i + "_C";
        }

        FakeDriver.getDriver().pushResultSet(matrix, "select * from A_TABLE");
    }


    private void runTestAggregate(BreakDetector breakDetector)
            throws SQLException {
        fakeTable(20);
        Connection con = testEnv.getConnectionManager().getConnection();

        // "order by" non specifie
        PreparedStatement stmt = con.prepareStatement("select * from A_TABLE");
        ResultSet rs = stmt.executeQuery();

        int i = 1;
        while (rs.next()) {
//                APP.debug(" ligne " + i + " : "
//                    + "\n   COL_A = " + rs.getString("COL_A")
//                    + "\n   COL_B = " + rs.getString("COL_B")
//                    + "\n   COL_C = " + rs.getString("COL_C")
//                );
            if (i == 10) {
                assertEquals("Ligne " + i, breakDetector.isBreakPoint(rs), true);
                return;
            }
            else {
                assertEquals("Ligne " + i, breakDetector.isBreakPoint(rs), false);
            }
            i++;
        }
    }
}
