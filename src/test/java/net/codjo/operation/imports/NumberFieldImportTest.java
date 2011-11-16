/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.operation.imports;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
/**
 * Overview.
 * 
 * <p>
 * Description
 * </p>
 *
 * @author $Author: spinae $
 * @version $Revision: 1.2 $
 */
public class NumberFieldImportTest extends TestCase {
    /**
     * Constructor for the NumberFieldImportTest object
     *
     * @param name Description of Parameter
     */
    public NumberFieldImportTest(String name) {
        super(name);
    }

    /**
     * A unit test suite for JUnit
     *
     * @return The test suite
     */
    public static Test suite() {
        return new TestSuite(NumberFieldImportTest.class);
    }


    /**
     * A unit test for JUnit
     *
     * @exception Exception Description of Exception
     */
    public void test_convertFieldToSQL_BigFloat()
            throws Exception {
        NumberFieldImport fieldE = new NumberFieldImport("label", '.');
        fieldE.setPosition(1);
        fieldE.setLength(18);
        Number n = (Number)fieldE.convertFieldToSQL("1234567890123.1234;2");
        assertTrue(n.doubleValue() == 1234567890123.1234);
    }


    /**
     * A unit test for JUnit
     *
     * @exception Exception Description of the Exception
     */
    public void test_convertFieldToSQL_BigFloat_Twice()
            throws Exception {
        NumberFieldImport fieldE = new NumberFieldImport("label", '.');
        fieldE.setPosition(1);
        fieldE.setLength(18);
        Number n = (Number)fieldE.convertFieldToSQL("1234567890123.1234;2");
        assertTrue(n.doubleValue() == 1234567890123.1234);

        Number b = (Number)fieldE.convertFieldToSQL("1111111111111.1111;2");
        assertTrue(b.doubleValue() == 1111111111111.1111);
    }


    /**
     * A unit test for JUnit
     *
     * @exception Exception Description of the Exception
     */
    public void test_convertFieldToSQL_BugEugenio()
            throws Exception {
        NumberFieldImport fieldE = new NumberFieldImport("label", ',');
        fieldE.setPosition(1);
        fieldE.setLength(18);
        try {
            Object str = fieldE.convertFieldToSQL("1234567890123.1234;2");
            fail("Conversion doit echouer car le separateur parametré est ',' "
                + " alors que le nombre utilise '.' : " + str);
        }
        catch (Exception e) {}
    }


    /**
     * A unit test for JUnit
     */
    public void test_convertFieldToSQL_Error() {
        NumberFieldImport fieldA = new NumberFieldImport("label");
        fieldA.setPosition(5);
        fieldA.setLength(3);
        try {
            fieldA.translateField("vvv");
            fail("Une exception devrait etre lance. vv n'est pas un nombre");
        }
        catch (BadFormatException e) {}
        try {
            Object str = fieldA.translateField("2.5");
            fail("Une exception devrait etre lance. 2.5 n'est pas un entier : " + str);
        }
        catch (BadFormatException e) {}
        try {
            Object str = fieldA.translateField("2,5");
            fail("Une exception devrait etre lance. 2,5 n'est pas un entier : " + str);
        }
        catch (BadFormatException e) {}
    }


    /**
     * A unit test for JUnit
     *
     * @exception Exception Description of Exception
     */
    public void test_convertFieldToSQL_Float() throws Exception {
        //Tests pour fichiers à longueur fixe
        NumberFieldImport fieldA = new NumberFieldImport("label", ',');
        fieldA.setPosition(5);
        fieldA.setLength(3);
        assertEquals("2.5", fieldA.convertFieldToSQL("102	2,5;2001/12/30").toString());

        NumberFieldImport fieldB = new NumberFieldImport("label", '.');
        fieldB.setPosition(5);
        fieldB.setLength(3);
        assertEquals("2.5", fieldB.convertFieldToSQL("102	2.5;2001/12/30").toString());

        NumberFieldImport fieldC = new NumberFieldImport("label", '.');
        fieldC.setPosition(5);
        fieldC.setLength(2);
        assertEquals("25", fieldC.convertFieldToSQL("102	25;2001/12/30").toString());

        NumberFieldImport fieldD = new NumberFieldImport("label", '.');
        fieldD.setPosition(5);
        fieldD.setLength(5);
        assertEquals("2.500", fieldD.convertFieldToSQL("102	2.500;2001/12/30").toString());

        NumberFieldImport fieldE = new NumberFieldImport("label", '.');
        fieldE.setPosition(5);
        fieldE.setLength(12);
        assertEquals("2.5000000001",
            fieldE.convertFieldToSQL("102	2.5000000001;2001/12/30").toString());

        NumberFieldImport fieldF = new NumberFieldImport("label", '.');
        fieldF.setPosition(5);
        fieldF.setLength(2);
        assertEquals("0.0", fieldF.convertFieldToSQL("102	.0;2001/12/30").toString());

        NumberFieldImport fieldG = new NumberFieldImport("label", '.');
        fieldG.setPosition(5);
        fieldG.setLength(2);
        assertEquals("0.1", fieldG.convertFieldToSQL("102	.1;2001/12/30").toString());
    }


    /**
     * A unit test for JUnit
     *
     * @exception Exception Description of the Exception
     */
    public void test_convertFieldToSQL_Float_Grouping()
            throws Exception {
        NumberFieldImport fieldE = new NumberFieldImport("label", '.');
        fieldE.setPosition(1);
        fieldE.setLength(9);
        try {
            fieldE.convertFieldToSQL("123,456.1");
            fail("Les séparateurs de miliers sont interdits");
        }
        catch (BadFormatException e) {}
    }


    /**
     * A unit test for JUnit
     *
     * @exception Exception Description of the Exception
     */
    public void test_convertFieldToSQL_Formatted()
            throws Exception {
        NumberFieldImport fieldE = new NumberFieldImport("label", '.');
        fieldE.setPosition(1);
        fieldE.setLength(18);
        Number n = (Number)fieldE.convertFieldToSQL("1 345 789 123.123 ;2");
        assertEquals("1345789123.123", n.toString());
    }


    /**
     * A unit test for JUnit
     *
     * @exception Exception Description of Exception
     */
    public void test_convertFieldToSQL_Integer() throws Exception {
        //Tests pour fichiers à longueur fixe
        NumberFieldImport fieldA = new NumberFieldImport("label");
        fieldA.setPosition(5);
        fieldA.setLength(2);
        assertEquals(fieldA.convertFieldToSQL("ABC;26;2001/12/30"), new Integer(26));

        NumberFieldImport fieldB = new NumberFieldImport("label");
        fieldB.setPosition(5);
        fieldB.setLength(2);
        assertEquals(fieldB.convertFieldToSQL("ABC;  ;2001/12/30"), null);
    }


    /**
     * A unit test for JUnit
     *
     * @exception Exception Description of Exception
     */
    public void test_convertFieldToSQL_Negative()
            throws Exception {
        //Tests pour fichiers à longueur fixe
        NumberFieldImport fieldA = new NumberFieldImport("label");
        fieldA.setPosition(5);
        fieldA.setLength(3);
        assertEquals("-25", fieldA.convertFieldToSQL("ABC;-25;2001/12/30").toString());

        NumberFieldImport fieldB = new NumberFieldImport("label");
        fieldB.setPosition(5);
        fieldB.setLength(5);
        assertEquals("-25", fieldB.convertFieldToSQL("ABC;-  25;2001/12/30").toString());

        NumberFieldImport fieldC = new NumberFieldImport("label", '.');
        fieldC.setPosition(5);
        fieldC.setLength(5);
        assertEquals("-25.1", fieldC.convertFieldToSQL("ABC;-25.1;2001/12/30").toString());

        NumberFieldImport fieldD = new NumberFieldImport("label", '.');
        fieldD.setPosition(5);
        fieldD.setLength(7);
        assertEquals("-25.2",
            fieldD.convertFieldToSQL("ABC;-  25.2;2001/12/30").toString());
    }


    /**
     * A unit test for JUnit
     *
     * @exception Exception Description of the Exception
     */
    public void test_convertFieldToSQL_Numeric_Decimal()
            throws Exception {
        NumberFieldImport fieldE = new NumberFieldImport("label", '.');
        fieldE.setPosition(1);
        fieldE.setLength(19);
        Number n = (Number)fieldE.convertFieldToSQL("123456789012.312340;2");
        assertEquals("123456789012.312340", n.toString());
    }


    /**
     * A unit test for JUnit
     *
     * @exception Exception Description of the Exception
     */
    public void test_convertFieldToSQL_Numeric_Entier()
            throws Exception {
        NumberFieldImport fieldE = new NumberFieldImport("label", '.');
        fieldE.setPosition(1);
        fieldE.setLength(18);
        Number n = (Number)fieldE.convertFieldToSQL("123456789012312340;2");
        assertEquals("123456789012312340", n.toString());
    }


    /**
     * A unit test for JUnit
     *
     * @exception Exception Description of Exception
     */
    public void test_convertFieldToSQL_Zero() throws Exception {
        //Tests pour fichiers à longueur fixe
        NumberFieldImport fieldA = new NumberFieldImport("label");
        fieldA.setPosition(5);
        fieldA.setLength(3);
        assertEquals(fieldA.convertFieldToSQL("ABC;005;2001/12/30"), new Integer(5));

        NumberFieldImport fieldB = new NumberFieldImport("label");
        fieldB.setPosition(5);
        fieldB.setLength(3);
        assertEquals(fieldB.convertFieldToSQL("ABC;-05;2001/12/30"), new Integer(-5));
    }


    /**
     * A unit test for JUnit
     */
    public void test_getSQLType() {
        NumberFieldImport fieldG = new NumberFieldImport("label", '.');
        fieldG.setPosition(5);
        fieldG.setLength(2);
        assertEquals(fieldG.getSQLType(), java.sql.Types.NUMERIC);

        NumberFieldImport fieldA = new NumberFieldImport("label");
        fieldA.setPosition(5);
        fieldA.setLength(3);
        assertEquals(fieldA.getSQLType(), java.sql.Types.INTEGER);
    }


    /**
     * A unit test for JUnit
     *
     * @exception Exception Description of Exception
     */
    public void test_translateField() throws Exception {
        NumberFieldImport field = new NumberFieldImport("label");
        field.setPosition(1);
        field.setLength(3);
        assertEquals(field.translateField(null), null);
        assertEquals(field.translateField(""), null);
    }
}
