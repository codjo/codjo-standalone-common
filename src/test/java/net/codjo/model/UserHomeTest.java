/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.model;
import net.codjo.persistent.PersistenceException;
import net.codjo.utils.TestEnvironnement;
import fakedb.FakeDriver;
import java.sql.Types;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
/**
 * Test de la classe <code>UserHome</code> .
 *
 * @author $Author: gonnot $
 * @version $Revision: 1.1.1.1 $
 */
public class UserHomeTest extends TestCase {
    TestEnvironnement testEnv;
    UserHome userhome;


    /**
     * Constructor for the UserHomeTest object
     *
     * @param name Description of Parameter
     */
    public UserHomeTest(String name) {
        super(name);
    }


    /**
     * A unit test for JUnit
     *
     * @throws PersistenceException Description of Exception
     */
    public void testgetUser() throws PersistenceException {
        FakeDriver.getDriver().pushResultSet(FakeDriver.RESULT_ONE,
                                             "select * from PM_USERS where NAME='a' and PASSWORD='a'");

        User mon_user = userhome.getUser("a", "a");
        assertNotNull(mon_user);
    }


    /**
     * A unit test for JUnit
     *
     * @throws PersistenceException Description of Exception
     */
    public void testgetUser_NotValid() throws PersistenceException {
        FakeDriver.getDriver().pushResultSet(FakeDriver.EMPTY,
                                             "select * from PM_USERS where NAME='XX' and PASSWORD='XX'");

        User mon_user = userhome.getUser("XX", "XX");
        assertNull(mon_user);
    }


    /**
     * A unit test for JUnit
     *
     * @throws PersistenceException Description of Exception
     */
    public void testgetUser_NotValidPassword() throws PersistenceException {
        FakeDriver.getDriver().pushResultSet(FakeDriver.EMPTY,
                                             "select * from PM_USERS where NAME='a' and PASSWORD='XX'");
        User mon_user = userhome.getUser("a", "XX");
        assertNull(mon_user);
    }


    /**
     * A unit test for JUnit
     *
     * @throws PersistenceException Description of Exception
     */
    public void testgetUser_NotValidUser() throws PersistenceException {
        FakeDriver.getDriver().pushResultSet(FakeDriver.EMPTY,
                                             "select * from PM_USERS where NAME='XX' and PASSWORD='a'");
        User mon_user = userhome.getUser("XX", "a");
        assertNull(mon_user);
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

        Object[][] matrix =
              {
                    {},
                    {null, null, null, "NAME", new Integer(Types.VARCHAR)},
                    {null, null, null, "PASSWORD", new Integer(Types.VARCHAR)}
              };
        FakeDriver.getDriver().pushResultSet(matrix);
        userhome = new UserHome(testEnv.getHomeConnection());
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
        return new TestSuite(UserHomeTest.class);
    }
}
