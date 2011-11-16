/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.profile;
import junit.framework.TestCase;
/**
 * Test de la classe User
 *
 * @author $Author: gonnot $
 * @version $Revision: 1.1.1.1 $
 */
public class UserTest extends TestCase {
    User adminUser;
    User simpleUser;

    public UserTest(String name) {
        super(name);
    }

    public void test_isAllowedTo() {
        assertTrue(adminUser.isAllowedTo("SEE_ADMIN_MENU"));
        assertTrue(simpleUser.isAllowedTo("SEE_ADMIN_MENU") == false);

        assertTrue(adminUser.isAllowedTo("QUIT_APP"));
        assertTrue(simpleUser.isAllowedTo("QUIT_APP"));

        assertTrue(adminUser.isAllowedTo("UNKNOWN") == false);
        assertTrue(simpleUser.isAllowedTo("UNKNOWN") == false);
    }


    protected void setUp() throws Exception {
        System.setProperty("net.codjo.profile.file", "profile.properties");
        adminUser = new User("admin", "Maintenance");
        simpleUser = new User("bobo", "Utilisateur");
    }
}
