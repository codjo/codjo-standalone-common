/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.model;
import net.codjo.persistent.PersistenceException;
import net.codjo.persistent.Reference;
import net.codjo.utils.TestEnvironnement;
import fakedb.FakeDriver;
import java.sql.SQLException;
import java.util.List;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
/**
 * Overview.
 *
 * @author $Author: gonnot $
 * @version $Revision: 1.1.1.1 $
 */
public class PortfolioGroupHomeTest extends TestCase {
    TestEnvironnement testEnv;
    PortfolioGroupHome portfolioGroupHome;


    /**
     * Constructor for the PortfolioGroupHomeTest object
     *
     * @param name Description of Parameter
     */
    public PortfolioGroupHomeTest(String name) {
        super(name);
    }


    /**
     * A unit test for JUnit
     *
     * @throws Exception Description of Exception
     */
    public void test_newPortfolioGroup() throws Exception {
        testEnv.getPortfolioGroupHome().getConnection().setAutoCommit(false);
        PortfolioGroup obj = testEnv.getPortfolioGroupHome().newPortfolioGroup("BOBO");

        // Requete d'insert
        FakeDriver.getDriver().pushResultSet(FakeDriver.RESULT_ONE,
                                             "insert into AP_PORTFOLIO_GROUP (PORTFOLIO_GROUP, PORTFOLIO_GROUP_ID) values (BOBO, 2) select @@identity");

        // Requete pour determiner la prochaine PK
        FakeDriver.getDriver().pushResultSet(FakeDriver.RESULT_ONE,
                                             "select max(PORTFOLIO_GROUP_ID) from AP_PORTFOLIO_GROUP");

        obj.save();
        assertTrue("enregistree", obj.isStored());
        assertTrue("synchronisee", obj.isSynchronized());
        testEnv.getPortfolioGroupHome().getConnection().rollback();
    }


    /**
     * A unit test for JUnit
     *
     * @throws PersistenceException Description of Exception
     */
    public void test_getAllObjects() throws PersistenceException {
        initFullTable("select * from AP_PORTFOLIO_GROUP");
        List ma_liste = portfolioGroupHome.getAllObjects();
        assertNotNull(ma_liste);
        assertEquals(ma_liste.size(), 5);
    }


    /**
     * A unit test for JUnit
     *
     * @throws PersistenceException Description of Exception
     */
    public void testgetAllRealPortfolioGroup() throws PersistenceException {
        initFullTable("select * from AP_PORTFOLIO_GROUP");
        List ma_liste = portfolioGroupHome.getAllRealPortfolioGroup();
        assertNotNull(ma_liste);
        assertEquals(ma_liste.size(), 3);
    }


    /**
     * A unit test for JUnit
     *
     * @throws PersistenceException Description of Exception
     */
    public void test_hasAPortfolioGroup() throws PersistenceException {
        initFullTable("select * from AP_PORTFOLIO_GROUP");
        List ma_liste = portfolioGroupHome.getAllObjects();
        Reference refA = portfolioGroupHome.getReference(1);
        assertEquals(ma_liste.contains(refA), true);
    }


    /**
     * A unit test for JUnit
     *
     * @throws PersistenceException Description of Exception
     */
    public void testNumberOfPortfolioGroup() throws PersistenceException {
        initFullTable("select * from AP_PORTFOLIO_GROUP");
        List ma_liste = portfolioGroupHome.getAllObjects();
        assertEquals(ma_liste.size(), 5);
    }


    /**
     * A unit test for JUnit
     *
     * @throws PersistenceException Description of Exception
     */
    public void testEqualsBeetweenPortfolio() throws PersistenceException {
        initFullTable("select * from AP_PORTFOLIO_GROUP");
        portfolioGroupHome.getAllObjects();
        Reference refA = portfolioGroupHome.getReference(4);
        PortfolioGroup ptf_SANS = (PortfolioGroup)refA.getObject();

        //PortfolioGroup ptf_SANS = new PortfolioGroup("SANS");
        Reference refB = portfolioGroupHome.getReference(3);
        PortfolioGroup ptf_OPCVM = (PortfolioGroup)refB.getObject();

        //PortfolioGroup ptf_OPCVM = new PortfolioGroup("OPCVM");
        Reference refC = portfolioGroupHome.getReference(1);
        PortfolioGroup ptf_ASSURANCE = (PortfolioGroup)refC.getObject();

        //PortfolioGroup ptf_ASSURANCE = new PortfolioGroup("ASSURANCE");
        assertEquals(ptf_SANS, ptf_SANS);
        assertEquals(ptf_SANS, ptf_OPCVM);
        assertEquals(ptf_OPCVM, ptf_SANS);
        if (ptf_OPCVM.equals(ptf_ASSURANCE)) {
            fail("Le groupe de ptf OPCVM ne doit pas etre egal au groupe ASSURANCE");
        }
    }


    /**
     * A unit test for JUnit
     *
     * @throws Exception Description of Exception
     */
    public void test_getReference() throws Exception {
        initFullTable("select * from AP_PORTFOLIO_GROUP where PORTFOLIO_GROUP_ID=1");

        PortfolioGroup obj =
              (PortfolioGroup)portfolioGroupHome.getReference(1).getObject();

        assertEquals(obj.getPortfolioGroupName(), "ASSURANCE");
    }


    /**
     * The JUnit setup method
     *
     * @throws SQLException Description of Exception
     */
    protected void setUp() throws SQLException {
        System.getProperties().put("TEST_ENVIRONMENT", "net.codjo.utils.TestEnvironnement");
        TestEnvironnement.forceFakeDriver();
        testEnv = TestEnvironnement.newEnvironment();
        portfolioGroupHome = testEnv.getPortfolioGroupHome();
    }


    /**
     * The teardown method for JUnit
     *
     * @throws SQLException Description of Exception
     */
    protected void tearDown() throws SQLException {
        testEnv.close();
    }


    /**
     * Initialise un ResultSet contenant la definition de toute la table.
     *
     * @param query Requete modele
     */
    private void initFullTable(String query) {
        Object[][] matrix =
              {
                    {"PORTFOLIO_GROUP", "PORTFOLIO_GROUP_ID"},
                    {"ASSURANCE", new Integer(1)},
                    {"ASSUR_UC", new Integer(2)},
                    {"OPCVM", new Integer(3)},
                    {"SANS", new Integer(4)},
                    {"TOUT", new Integer(5)}
              };
        FakeDriver.getDriver().pushResultSet(matrix, query);
    }


    /**
     * A unit test suite for JUnit
     *
     * @return The test suite
     */
    public static Test suite() {
        return new TestSuite(PortfolioGroupHomeTest.class);
    }
}
