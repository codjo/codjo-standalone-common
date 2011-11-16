/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.utils;
import java.io.StringReader;
import java.util.Iterator;
import java.util.Properties;
import java.util.StringTokenizer;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.log4j.Logger;

/**
 * Classe de Test de <code>ConfigurationFile</code>.
 *
 * @author $Author: blazart $
 * @version $Revision: 1.4 $
 */
public class ConfigurationFileTest extends TestCase {
    private static final String temporaryDirectory = System.getProperty("java.io.tmpdir");

    // Log
    private static final Logger APP = Logger.getLogger(ConfigurationFileTest.class);

    /**
     * Constructor for the ConfigurationFileTest object
     *
     * @param name Description of Parameter
     */
    public ConfigurationFileTest(String name) {
        super(name);
    }

    /**
     * A unit test suite for JUnit
     *
     * @return The test suite
     */
    public static Test suite() {
        return new TestSuite(ConfigurationFileTest.class);
    }


    /**
     * A unit test for JUnit
     *
     * @exception Exception Description of the Exception
     */
    public void test_addDomain() throws Exception {
        ConfigurationFile p = new ConfigurationFile();
        Properties prop = new Properties();
        p.addDomain("domain", prop);
        assertTrue(p.getDomain("domain") == prop);
        assertTrue(p.containsDomain("domain"));
    }


    /**
     * A unit test for JUnit
     *
     * @exception Exception Description of the Exception
     */
    public void test_domainsName() throws Exception {
        ConfigurationFile p = new ConfigurationFile();
        p.addDomain("domain", new Properties());
        Iterator iter = p.domainsName();
        assertTrue(iter.hasNext());
        assertEquals(iter.next(), "domain");
        assertTrue(iter.hasNext() == false);
    }


    /**
     * A unit test for JUnit
     *
     * @exception Exception Description of the Exception
     */
    public void test_getDomain_Unknown() throws Exception {
        ConfigurationFile p = new ConfigurationFile();

        assertNull(p.getDomain("UNKNOWN"));
    }


    private String slashDubbler(String str) {
        String slash = "\\";
        StringTokenizer tokenizer = new StringTokenizer(str, slash, true);
        String slashDubbled = "";
        String element;
        while (tokenizer.hasMoreElements()) {
            element = (String)tokenizer.nextElement();
            if (slash.equals(element)) {
                slashDubbled += slash;
            }
            slashDubbled += element;
        }
        return slashDubbled;
    }


    public void test_load_AntiSlashChar() throws Exception {
        String propTemporaryDirectory = slashDubbler(temporaryDirectory);
        APP.debug("propTemporaryDirectory = " + propTemporaryDirectory);
        ConfigurationFile p = new ConfigurationFile();
        String str = "DOMAIN1 {\n" + " prop_a = " + propTemporaryDirectory + " \n" + "}";

        p.load(new StringReader(str));
        assertNotNull(p.getDomain("DOMAIN1"));
        assertEquals(temporaryDirectory, p.getDomain("DOMAIN1").getProperty("prop_a"));
    }


    /**
     * A unit test for JUnit
     *
     * @exception Exception Description of the Exception
     */
    public void test_load_BUG_space_Before_DomainClosingBracket()
            throws Exception {
        ConfigurationFile p = new ConfigurationFile();
        String str = "DOMAIN1 {\n" + " prop_a = 1\n" + "   }";
        p.load(new StringReader(str));
        assertNotNull(p.getDomain("DOMAIN1"));
        assertEquals(p.getDomain("DOMAIN1").getProperty("prop_a"), "1");
        assertEquals(p.getDomain("DOMAIN1").size(), 1);
    }


    /**
     * A unit test for JUnit
     *
     * @exception Exception Description of the Exception
     */
    public void test_load_emptyDomains() throws Exception {
        ConfigurationFile p = new ConfigurationFile();
        String str =
            "DOMAIN_1 {\n" + "}\n" + "// Domain 2\n" + "DOMAIN2 {\n" + "}\n"
            + "// Domain 3\n" + "DOMAIN3 {\n" + "}\n";
        p.load(new StringReader(str));
        assertNotNull(p.getDomain("DOMAIN_1"));
        assertNotNull(p.getDomain("DOMAIN2"));
        assertNotNull(p.getDomain("DOMAIN3"));
    }


    /**
     * A unit test for JUnit
     *
     * @exception Exception Description of the Exception
     */
    public void test_load_noDomain() throws Exception {
        ConfigurationFile p = new ConfigurationFile();
        String str = "";

        p.load(new StringReader(str));
    }


    /**
     * A unit test for JUnit
     *
     * @exception Exception Description of the Exception
     */
    public void test_load_oneDomain() throws Exception {
        ConfigurationFile p = new ConfigurationFile();
        String str = "DOMAIN1 {\n" + "   \n" + " prop_a = 1\n" + "}";

        p.load(new StringReader(str));
        assertNotNull(p.getDomain("DOMAIN1"));
        assertEquals(p.getDomain("DOMAIN1").getProperty("prop_a"), "1");
        assertEquals(p.getDomain("DOMAIN1").size(), 1);
    }


    /**
     * A unit test for JUnit
     *
     * @exception Exception Description of the Exception
     */
    public void test_load_oneDomainWithAccolade()
            throws Exception {
        ConfigurationFile p = new ConfigurationFile();
        String str = "DOMAIN1 {\n" + "   \n" + " prop_a = {1}\n" + "}";

        p.load(new StringReader(str));
        assertNotNull(p.getDomain("DOMAIN1"));
        assertEquals(p.getDomain("DOMAIN1").getProperty("prop_a"), "{1}");
        assertEquals(p.getDomain("DOMAIN1").size(), 1);
    }


    /**
     * A unit test for JUnit
     *
     * @exception Exception Description of the Exception
     */
    public void test_load_oneDomainWithLF() throws Exception {
        ConfigurationFile p = new ConfigurationFile();
        String str =
            "DOMAIN1 {\n" + "   \n" + " prop_a = le \\\n" + "          petit chien\n"
            + "}";

        p.load(new StringReader(str));
        assertNotNull(p.getDomain("DOMAIN1"));
        assertEquals("le \n          petit chien",
            p.getDomain("DOMAIN1").getProperty("prop_a"));
        assertEquals(p.getDomain("DOMAIN1").size(), 1);
    }


    /**
     * A unit test for JUnit
     *
     * @exception Exception Description of the Exception
     */
    public void test_load_threeDomains() throws Exception {
        ConfigurationFile p = new ConfigurationFile();
        String str =
            "DOMAIN1 {\n" + " prop = 1\n" + "}\n" + "// Domain 2\n" + "DOMAIN2 {\n"
            + " alone \n" + "}\n" + "// Domain 3\n" + "DOMAIN3 {\n" + "}\n";
        p.load(new StringReader(str));
        assertNotNull(p.getDomain("DOMAIN1"));
        assertNotNull(p.getDomain("DOMAIN2"));
        assertNotNull(p.getDomain("DOMAIN3"));
    }


    /**
     * The JUnit setup method
     */
    protected void setUp() {}


    /**
     * The teardown method for JUnit
     */
    protected void tearDown() {}
}
