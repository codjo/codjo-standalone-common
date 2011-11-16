/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.model;
import net.codjo.persistent.PersistenceException;
import net.codjo.utils.TestEnvironnement;
import fakedb.FakeDriver;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.SQLException;
import java.util.List;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
/**
 * Test de la classe PeriodHome et Period.
 *
 * @author $Author: gonnot $
 * @version $Revision: 1.1.1.1 $
 */
public class PeriodHomeTest extends TestCase {
    TestEnvironnement testEnv;

    /**
     * Constructor for the PeriodHomeTest object
     *
     * @param name Description of Parameter
     */
    public PeriodHomeTest(String name) {
        super(name);
    }

    /**
     * A unit test for JUnit
     *
     * @exception Exception Description of Exception
     */
    public void test_getPeriod() throws Exception {
        PeriodHome home = testEnv.getPeriodHome();

        Object[][] matrix = {
                {"PERIOD", "VISIBLE"},
                {"200012", Boolean.TRUE}
            };
        FakeDriver.getDriver().pushResultSet(matrix,
            "select * from AP_PERIOD where PERIOD=200012");

        Period period = (Period)home.getReference("200012").getObject();

        assertEquals(period.getPeriod(), "200012");
    }


    /**
     * A unit test for JUnit
     *
     * @exception Exception Description of Exception
     */
    public void test_currentPeriod() throws Exception {
        PeriodHome home = testEnv.getPeriodHome();

        ListenerTest l = new ListenerTest();
        assertNull("Before", l.event);
        assertNull("start Period", home.getCurrentPeriod());

        home.addPropertyChangeListener(l);
        home.setCurrentPeriod(testEnv.getPeriod200011());

        assertEquals("a.property", l.event.getPropertyName(), "currentPeriod");
        assertNull("a.value Before", l.event.getOldValue());
        assertEquals("a.value After", l.event.getNewValue(), testEnv.getPeriod200011());

        home.setCurrentPeriod(testEnv.getPeriod200012());

        assertEquals("b.property", l.event.getPropertyName(), "currentPeriod");
        assertEquals("b.value Before", l.event.getOldValue(), testEnv.getPeriod200011());
        assertEquals("b.value After", l.event.getNewValue(), testEnv.getPeriod200012());

        home.removePropertyChangeListener(l);
        l.event = null;
        home.setCurrentPeriod(testEnv.getPeriod200011());

        assertNull("listener not removed", l.event);
    }


    /**
     * A unit test for JUnit
     *
     * @exception Exception Description of Exception
     */
    public void test_getPreviousPeriod() throws Exception {
        PeriodHome home = testEnv.getPeriodHome();

        Period currentPeriod = testEnv.getPeriod200012();

        Object[][] matrix = {
                {"PERIOD", "VISIBLE"},
                {"200011", Boolean.TRUE}
            };
        FakeDriver.getDriver().pushResultSet(matrix,
            "select * from AP_PERIOD where PERIOD=200011");
        Period prevPeriod = home.getPreviousPeriod(currentPeriod);

        assertEquals("prev a 200012", prevPeriod.getPeriod(), "200011");

        try {
            Object[][] m = {
                    {"PERIOD", "VISIBLE"}
                };
            FakeDriver.getDriver().pushResultSet(m);
            home.getPreviousPeriod(testEnv.getPeriod200008());
            fail("Aucune periode ne precede 200008");
        }
        catch (PersistenceException e) {}
    }


    /**
     * A unit test for JUnit
     *
     * @exception Exception Description of Exception
     */
    public void test_determinePreviousPeriod() throws Exception {
        PeriodHome home = testEnv.getPeriodHome();
        String prevPeriod = home.determinePreviousPeriod("200101");
        assertEquals("prev a 200101", prevPeriod, "200012");
        String prevPeriodBis = home.determinePreviousPeriod("200101-bis");
        assertEquals("prev a 200101-bis", prevPeriodBis, "200012");

        try {
            home.determinePreviousPeriod("je suis pas une periode");
            fail("periode invalide");
        }
        catch (java.text.ParseException e) {}
    }


    /**
     * A unit test for JUnit
     *
     * @exception Exception Description of Exception
     */
    public void test_Create_Delete() throws Exception {
        testEnv.getHomeConnection().setAutoCommit(false);

        Object[][] matrix = {
                {"PERIOD", "VISIBLE"},
                {"200012", Boolean.TRUE}
            };
        FakeDriver.getDriver().pushResultSet(matrix,
            "select * from AP_PERIOD where PERIOD=200012");
        Period period =
            (Period)testEnv.getPeriodHome().getReference("200012").getObject();
        assertTrue("Periode synchronisee", period.isSynchronized());

        FakeDriver.getDriver().pushUpdateConstraint("delete from AP_PERIOD where PERIOD=200012");
        period.delete();
        assertTrue("Periode deleted", period.isDead());

        testEnv.getHomeConnection().rollback();
    }


    /**
     * A unit test for JUnit
     *
     * @exception Exception Description of Exception
     */
    public void testequals() throws Exception {
        assertTrue("200012==200008",
            !testEnv.getPeriod200012().equals(testEnv.getPeriod200008()));
        assertTrue("200008==200012",
            !testEnv.getPeriod200008().equals(testEnv.getPeriod200012()));
        assertTrue("200012==200012bis",
            testEnv.getPeriod200012().equals(new Period(
                    testEnv.getPeriodHome().getReference("200012"),
                    "200012",
                    false)));
    }


    /**
     * A unit test for JUnit A unit test for JUnit A unit test for JUnit A unit test for
     * JUnit
     *
     * @exception Exception Description of Exception
     */
    public void testNumberOfPeriod() throws Exception {
        testEnv.close();
        TestEnvironnement.forceFakeDriver();
        testEnv = TestEnvironnement.newEnvironment();
        Object[][] matrix =
            {
                {"PERIOD", "VISIBLE"},
                {"200101", Boolean.TRUE},
                {"200012", Boolean.TRUE},
                {"200011", Boolean.TRUE},
                {"200010", Boolean.TRUE},
                {"200009", Boolean.TRUE},
                {"200008", Boolean.TRUE},
                {"200106", Boolean.TRUE},
                {"200107", Boolean.TRUE}
            };
        FakeDriver.getDriver().pushResultSet(matrix, "select * from AP_PERIOD");
        List ma_liste = testEnv.getPeriodHome().getAllObjects();
        assertEquals(ma_liste.size(), 8);
    }


    /**
     * The JUnit setup method
     *
     * @exception SQLException Description of Exception
     */
    protected void setUp() throws SQLException {
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
     * A unit test suite for JUnit
     *
     * @return The test suite
     */
    public static Test suite() {
        return new TestSuite(PeriodHomeTest.class);
    }

    /**
     * Overview.
     *
     * @author $Author: gonnot $
     * @version $Revision: 1.1.1.1 $
     */
    private static class ListenerTest implements PropertyChangeListener {
        /** Description of the Field */
        public PropertyChangeEvent event = null;

        /**
         * Overview.
         *
         * @param evt Description of Parameter
         */
        public void propertyChange(PropertyChangeEvent evt) {
            event = evt;
        }
    }
}
