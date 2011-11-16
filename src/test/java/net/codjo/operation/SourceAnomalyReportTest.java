/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.operation;
import net.codjo.utils.TestEnvironnement;
import fakedb.FakeResultSet;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
/**
 * Test <code>SourceAnomalyReport</code> .
 *
 * @author $Author: gonnot $
 * @version $Revision: 1.1.1.1 $
 */
public class SourceAnomalyReportTest extends TestCase {
    TestEnvironnement testEnv;

    /**
     * Constructor for the SourceAnomalyReportTest object
     *
     * @param name Description of Parameter
     */
    public SourceAnomalyReportTest(String name) {
        super(name);
    }

    public void test_clone() throws Exception {
        AnomalyReport report = new SourceAnomalyReport();
        AnomalyReport reportClone = (AnomalyReport)report.clone();

        assertTrue(reportClone instanceof SourceAnomalyReport);

        report.addAnomaly("Anomaly");

        assertTrue(report.hasAnomaly());
        assertTrue(reportClone.hasAnomaly() == false);
    }


    public void test_clone_bis() throws Exception {
        AnomalyReport report = new SourceAnomalyReport();
        report.addAnomaly("Anomaly");
        AnomalyReport reportClone = (AnomalyReport)report.clone();
        reportClone.clearAnomaly();

        assertTrue(reportClone instanceof SourceAnomalyReport);
        assertTrue(report.hasAnomaly());
        assertTrue(reportClone.hasAnomaly() == false);
    }


    /**
     * A unit test for JUnit
     *
     * @exception Exception Description of Exception
     */
    public void test_isWriteAllowed() throws Exception {
        AnomalyReport report = new SourceAnomalyReport();

        assertTrue("Cas normal", report.isWriteAllowed());
        report.addAnomaly("erreur");
        assertTrue("ligne en erreur", report.isWriteAllowed() == false);
    }


    /**
     * A unit test for JUnit
     *
     * @exception Exception Description of Exception
     */
    public void test_getColumnsName() throws Exception {
        AnomalyReport report = new SourceAnomalyReport();
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
        AnomalyReport report = new SourceAnomalyReport();
        assertTrue("destination", report.needsDestinationUpdatable() == false);
        assertTrue("source", report.needsSourceUpdatable());
    }


    /**
     * A unit test for JUnit
     *
     * @exception Exception Description of Exception
     */
    public void test_hasAnomaly() throws Exception {
        AnomalyReport report = new SourceAnomalyReport();
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
        AnomalyReport report = new SourceAnomalyReport();

        report.updateDestination(null, 0);
    }


    /**
     * A unit test for JUnit
     *
     * @exception Exception Description of Exception
     */
    public void test_updateSource() throws Exception {
        AnomalyReport report = new SourceAnomalyReport();

        Object[][] matrix = {
                {"ANOMALY", "ANOMALY_LOG"},
                {new Integer(0), null}
            };
        ResultSet rs = new FakeResultSet(matrix);
        rs.next();

        // Verifie Etat Originel
        assertEquals("No Anomaly", rs.getInt("ANOMALY"), 0);
        assertEquals("No log", rs.getString("ANOMALY_LOG"), null);

        // Ajoute Erreur
        report.addAnomaly("e");
        report.updateSource(rs);
        assertEquals("Add Anomaly", rs.getInt("ANOMALY"), 1);
        assertEquals("Add log", rs.getString("ANOMALY_LOG"), "e");

        // Efface erreur
        report.clearAnomaly();
        report.updateSource(rs);
        assertEquals("Clear Anomaly", rs.getInt("ANOMALY"), 0);
        assertEquals("Clear log", rs.getString("ANOMALY_LOG"), null);
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
        return new TestSuite(SourceAnomalyReportTest.class);
    }
}
