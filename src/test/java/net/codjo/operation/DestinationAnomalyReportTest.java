/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.operation;
import net.codjo.utils.TestEnvironnement;
import fakedb.FakeDriver;
import java.sql.PreparedStatement;
import java.util.Arrays;
import java.util.List;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
/**
 * Test <code>DestinationAnomalyReport</code> .
 *
 * @author $Author: GONNOT $
 * @version $Revision: 1.2 $
 */
public class DestinationAnomalyReportTest extends TestCase {
    TestEnvironnement testEnv;

    /**
     * Constructor for the DestinationAnomalyReportTest object
     *
     * @param name Description of Parameter
     */
    public DestinationAnomalyReportTest(String name) {
        super(name);
    }

    /**
     * A unit test for JUnit
     *
     * @exception Exception Description of Exception
     */
    public void test_clone() throws Exception {
        AnomalyReport report = new DestinationAnomalyReport();

        assertTrue(report.clone() instanceof DestinationAnomalyReport);
    }


    /**
     * A unit test for JUnit
     *
     * @exception Exception Description of Exception
     */
    public void test_isWriteAllowed() throws Exception {
        AnomalyReport report = new DestinationAnomalyReport();

        assertTrue("Cas normal", report.isWriteAllowed());
        report.addAnomaly("erreur");
        assertTrue("ligne en erreur", report.isWriteAllowed());
    }


    /**
     * A unit test for JUnit
     *
     * @exception Exception Description of Exception
     */
    public void test_getColumnsName() throws Exception {
        AnomalyReport report = new DestinationAnomalyReport();
        List a = Arrays.asList(report.getColumnsName());
        String[] array = {"ANOMALY", "ANOMALY_LOG"};
        List b = Arrays.asList(array);

        assertTrue(a.containsAll(b));
    }


    /**
     * A unit test for JUnit
     *
     * @exception Exception Description of Exception
     */
    public void test_needsX() throws Exception {
        AnomalyReport report = new DestinationAnomalyReport();
        assertTrue("destination", report.needsDestinationUpdatable());
        assertTrue("source", report.needsSourceUpdatable() == false);
    }


    /**
     * A unit test for JUnit
     *
     * @exception Exception Description of Exception
     */
    public void test_hasAnomaly() throws Exception {
        AnomalyReport report = new DestinationAnomalyReport();
        assertTrue("pas d'erreur", report.hasAnomaly() == false);
        report.addAnomaly("e");
        assertTrue("une d'erreur", report.hasAnomaly());
        report.clearAnomaly();
        assertTrue("Les erreurs sont pardonnees", report.hasAnomaly() == false);
    }


    /**
     * A unit test for JUnit
     *
     * @exception Exception Description of Exception
     */
    public void test_updateDestination() throws Exception {
        AnomalyReport report = new DestinationAnomalyReport();

        FakeDriver.getDriver().pushUpdateConstraint("update MA_TABLE "
            + "set ANOMALY=1, " + "ANOMALY_LOG=bobo");

        PreparedStatement stmt =
            testEnv.getHomeConnection().prepareStatement("update MA_TABLE set ANOMALY=?, "
                + "ANOMALY_LOG=?");

        report.addAnomaly("bobo");
        report.updateDestination(stmt, 1);

        stmt.executeUpdate();
    }


    public void test_updateDestination_NoError() throws Exception {
        AnomalyReport report = new DestinationAnomalyReport();

        FakeDriver.getDriver().pushUpdateConstraint("update MA_TABLE "
            + "set ANOMALY=0, " + "ANOMALY_LOG=null(sqlType=12)");

        PreparedStatement stmt =
            testEnv.getHomeConnection().prepareStatement("update MA_TABLE set ANOMALY=?, "
                + "ANOMALY_LOG=?");

        report.updateDestination(stmt, 1);

        stmt.executeUpdate();
    }


    /**
     * A unit test for JUnit
     *
     * @exception Exception Description of Exception
     */
    public void test_updateSource() throws Exception {
        AnomalyReport report = new DestinationAnomalyReport();

        report.addAnomaly("e");
        report.updateSource(null);
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
        return new TestSuite(DestinationAnomalyReportTest.class);
    }
}
