/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.gui;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
/**
 * Test <code>NumberField</code> .
 *
 * @author $Author: spinae $
 * @version $Revision: 1.2 $
 */
public class NumberFieldTest extends TestCase {
    NumberField nbField;

    /**
     * Constructor for the NumberFieldTest object
     *
     * @param name Description of Parameter
     */
    public NumberFieldTest(String name) {
        super(name);
    }

    /**
     * A unit test suite for JUnit
     *
     * @return The test suite
     */
    public static Test suite() {
        return new TestSuite(NumberFieldTest.class);
    }


    /**
     * A unit test for JUnit
     *
     * @exception Exception Description of Exception
     */
    public void test_Double() throws Exception {
        nbField.setText("12.2");

        assertEquals("double value", nbField.getDoubleValue(), 12.2, 0);
        assertEquals("Double", nbField.getNumberValue(), new Double(12.2));
    }


    /**
     * A unit test for JUnit
     *
     * @exception Exception Description of Exception
     */
    public void test_Integer() throws Exception {
        nbField.setParseIntegerOnly(true);

        nbField.setText("12");

        assertEquals("int value", nbField.getIntValue(), 12);
        assertEquals("Integer", nbField.getNumberValue(), new Long(12));
    }


    /**
     * A unit test for JUnit
     *
     * @exception Exception Description of Exception
     */
    public void test_Integer_point() throws Exception {
        nbField.setParseIntegerOnly(true);
        nbField.setText("12");
        nbField.getDocument().insertString(1, ".", null);
        assertEquals(nbField.getText(), "12");
    }


    /**
     * A unit test for JUnit
     *
     * @exception Exception Description of Exception
     */
    public void test_getNumberValue_EmptyText() throws Exception {
        nbField.setText("");
        assertEquals("double value", nbField.getDoubleValue(), 0, 0);
        assertEquals("Double", nbField.getNumberValue(), null);
    }


    /**
     * A unit test for JUnit
     *
     * @exception Exception Description of Exception
     */
    public void test_insertString() throws Exception {
        nbField.setText("12");
        nbField.getDocument().insertString(2, ".", null);

        assertEquals(nbField.getText(), "12.");

        nbField.getDocument().insertString(3, "05", null);
        assertEquals(nbField.getText(), "12.05");

        nbField.getDocument().insertString(3, "y", null);
        assertEquals(nbField.getText(), "12.05");

        nbField.getDocument().insertString(0, " ", null);
        assertEquals(nbField.getText(), "12.05");
    }


    /**
     * A unit test for JUnit
     *
     * @exception Exception Description of Exception
     */
    public void test_insertString_2points() throws Exception {
        nbField.setText("125.05");
        nbField.getDocument().insertString(2, ".", null);

        assertEquals(nbField.getText(), "125.05");
    }


    /**
     * A unit test for JUnit
     *
     * @exception Exception Description of Exception
     */
    public void test_insertString_Casparticulier()
            throws Exception {
        nbField.setText(".5");
        assertEquals(nbField.getText(), ".5");
        assertEquals("double value", nbField.getDoubleValue(), 0.5, 0);
    }


    /**
     * A unit test for JUnit
     *
     * @exception Exception Description of Exception
     */
    public void test_insertString_maxValue() throws Exception {
        nbField.setParseIntegerOnly(true);
        String maxVal = "" + (Long.MAX_VALUE);
        nbField.setText(maxVal);
        assertEquals("MaxValue", nbField.getNumberValue().longValue(), Long.MAX_VALUE);

        nbField.getDocument().insertString(maxVal.length(), "0", null);
        assertEquals("b", nbField.getText(), maxVal);
    }


    public void test_negative() throws Exception {
        nbField.setText("12");
        nbField.getDocument().insertString(0, "-", null);

        assertEquals(nbField.getText(), "-12");
        assertEquals(-12, nbField.getIntValue());
    }


    public void test_negative_first() throws Exception {
        nbField.setText("-");
        assertEquals(nbField.getText(), "-");
        assertEquals(0, nbField.getIntValue());
        assertEquals(0., nbField.getDoubleValue(), 0.);

        nbField.getDocument().insertString(1, "25", null);
        assertEquals(nbField.getText(), "-25");
        assertEquals(-25, nbField.getIntValue());
        assertEquals(-25., nbField.getDoubleValue(), 0.);
    }


    /**
     * The JUnit setup method
     *
     * @exception Exception Description of Exception
     */
    protected void setUp() throws Exception {
        nbField = new NumberField();
    }


    /**
     * The teardown method for JUnit
     */
    protected void tearDown() {}
}
