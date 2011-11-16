/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.utils;
import java.io.StringReader;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
/**
 * Test de la classe FileComparator
 *
 * @author $Author: spinae $
 * @version $Revision: 1.2 $
 */
public class FileComparatorTest extends TestCase {
    FileComparator comparator;

    /**
     * Constructor for the FileComparatorTest object
     *
     * @param name Description of Parameter
     */
    public FileComparatorTest(String name) {
        super(name);
    }

    /**
     * A unit test suite for JUnit
     *
     * @return The test suite
     */
    public static Test suite() {
        return new TestSuite(FileComparatorTest.class);
    }


    /**
     * A unit test for JUnit
     *
     * @exception Exception Description of the Exception
     */
    public void test_isEqual() throws Exception {
        StringReader a = new StringReader("le petit chien est grand");
        StringReader model = new StringReader("le petit chien est grand");

        assertTrue(comparator.equals(model, a));
    }


    /**
     * A unit test for JUnit
     *
     * @exception Exception Description of the Exception
     */
    public void test_isEqual_Bug() throws Exception {
        StringReader a =
            new StringReader(
                "9557VL 2001-10-290000000000102.000000EUR20011120.0000AURORE\rff");
        StringReader model =
            new StringReader(
                "9557VL 2001-10-290000000000102.000000EUR*************AURORE"
                + System.getProperty("line.separator") + "ff");

        assertTrue(comparator.equals(model, a));
    }


    /**
     * Test le cas de comparaison de lignes de tailles differentes.
     *
     * @throws Exception
     */
    public void test_isEqual_notSameSize() throws Exception {
        StringReader current = new StringReader("9557VL ");
        StringReader expected = new StringReader("9557VL 2001-10-29000");

        assertTrue(!comparator.equals(expected, current));

        current = new StringReader("9557VL ");
        expected = new StringReader("9557VL 2001-10-29000");

        assertTrue(!comparator.equals(current, expected));
    }


    /**
     * A unit test for JUnit
     *
     * @exception Exception Description of the Exception
     */
    public void test_isEqual_MultiLine() throws Exception {
        StringReader a = new StringReader("le petit \nchien est grand");
        StringReader model =
            new StringReader("le petit " + System.getProperty("line.separator")
                + "chien est grand");

        assertTrue(comparator.equals(model, a));
    }


    public void test_isEqual_MultiLine_NotOrdered()
            throws Exception {
        StringReader a = new StringReader("le petit \nchien est grand");
        StringReader model =
            new StringReader("chien est grand" + System.getProperty("line.separator")
                + "le petit ");

        assertTrue(comparator.equalsNotOrdered(model, a));
    }


    public void test_isEqual_MultiLine_NotOrdered_Failure()
            throws Exception {
        StringReader a = new StringReader("le petit \nchien est pas grand");
        StringReader model =
            new StringReader("chien est grand" + System.getProperty("line.separator")
                + "le petit ");

        assertTrue(!comparator.equalsNotOrdered(model, a));
    }


    /**
     * A unit test for JUnit
     *
     * @exception Exception Description of the Exception
     */
    public void test_isEqual_false() throws Exception {
        StringReader a = new StringReader("le petit chien est grand");
        StringReader model = new StringReader("les petit chien est grand");
        assertTrue("Différence : ", comparator.equals(model, a) == false);
    }


    /**
     * A unit test for JUnit
     *
     * @exception Exception Description of the Exception
     */
    public void test_isEqual_withJOKER() throws Exception {
        StringReader a = new StringReader("le petit chien est grand");
        StringReader b = new StringReader("le petit doggy est grand");
        StringReader model = new StringReader("le petit ***** est grand");

        assertTrue("equals(model, a)", comparator.equals(model, a));
        model.reset();
        a.reset();
        assertTrue("equals(a, model)", comparator.equals(a, model));
        model.reset();
        b.reset();
        assertTrue("equals(model, b)", comparator.equals(model, b));
        model.reset();
        b.reset();
        assertTrue("equals(b, model)", comparator.equals(b, model));
    }


    /**
     * A unit test for JUnit
     *
     * @exception Exception Description of the Exception
     */
    public void test_isEqual_withJOKER_false() throws Exception {
        StringReader a = new StringReader("le petit chienX est grand");
        StringReader model = new StringReader("le petit ***** est grand");

        assertTrue(comparator.equals(model, a) == false);
    }


    /**
     * The JUnit setup method
     *
     * @exception Exception Description of Exception
     */
    protected void setUp() throws Exception {
        comparator = new FileComparator("*");
    }


    /**
     * The teardown method for JUnit
     */
    protected void tearDown() {}
}
