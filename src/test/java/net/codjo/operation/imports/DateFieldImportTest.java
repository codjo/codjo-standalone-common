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
 * Description of the Class
 *
 * @author $Author: spinae $
 * @version $Revision: 1.2 $
 */
public class DateFieldImportTest extends TestCase {
    /**
     * Constructeur de DateFieldImportTest
     *
     * @param name Description of Parameter
     */
    public DateFieldImportTest(String name) {
        super(name);
    }

    /**
     * A unit test suite for JUnit
     *
     * @return The test suite
     */
    public static Test suite() {
        return new TestSuite(DateFieldImportTest.class);
    }


    /**
     * A unit test for JUnit
     */
    public void test_constructor_badFormat() {
        //Tests pour fichiers à longueur fixe
        try {
            DateFieldImport fieldA = new DateFieldImport("label", 9);
            fieldA.setPosition(1);
            fieldA.setLength(3);
            fail("Une exception IllegalArgumentException aurait du etre lancee");
        }
        catch (IllegalArgumentException e) {}

        //Tests pour fichiers à longueur fixe
        try {
            DateFieldImport fieldB = new DateFieldImport("label", 0);
            fieldB.setPosition(1);
            fieldB.setLength(3);
            fail("Une exception IllegalArgumentException aurait du etre lancee");
        }
        catch (IllegalArgumentException e) {}
    }


    /**
     * A unit test for JUnit
     *
     * @exception Exception Description of Exception
     */
    public void test_convertFieldToSQL() throws Exception {
        //Tests pour fichiers à longueur fixe
        DateFieldImport fieldA =
            new DateFieldImport("une Date", FieldImportHome.YYYY_MM_DD_SLASH);
        fieldA.setPosition(8);
        fieldA.setLength(10);
        assertEquals(fieldA.convertFieldToSQL("ABC;25;2001/12/30"),
            java.sql.Date.valueOf("2001-12-30"));

        DateFieldImport fieldB =
            new DateFieldImport("une Date", FieldImportHome.YYYY_MM_DD_HYPHEN);
        fieldB.setPosition(8);
        fieldB.setLength(10);
        assertEquals(fieldB.convertFieldToSQL("ABC;25;2001-12-30"),
            java.sql.Date.valueOf("2001-12-30"));

        DateFieldImport fieldC =
            new DateFieldImport("une Date", FieldImportHome.YYYYMMDD);
        fieldC.setPosition(8);
        fieldC.setLength(8);
        assertEquals(fieldC.convertFieldToSQL("ABC;25;20011230"),
            java.sql.Date.valueOf("2001-12-30"));

        DateFieldImport fieldD =
            new DateFieldImport("une Date", FieldImportHome.DD_MM_YY_HYPHEN);
        fieldD.setPosition(8);
        fieldD.setLength(8);
        assertEquals(fieldD.convertFieldToSQL("ABC;25;30-12-01"),
            java.sql.Date.valueOf("2001-12-30"));

        DateFieldImport fieldE =
            new DateFieldImport("une Date", FieldImportHome.DD_MM_YYYY_HYPHEN);
        fieldE.setPosition(8);
        fieldE.setLength(10);
        assertEquals(fieldE.convertFieldToSQL("ABC;25;30-12-2001"),
            java.sql.Date.valueOf("2001-12-30"));

        DateFieldImport fieldF =
            new DateFieldImport("une Date", FieldImportHome.DDMMYYYY);
        fieldF.setPosition(8);
        fieldF.setLength(8);
        assertEquals(fieldF.convertFieldToSQL("ABC;25;30122001"),
            java.sql.Date.valueOf("2001-12-30"));

        DateFieldImport fieldG =
            new DateFieldImport("une Date", FieldImportHome.DD_MM_YYYY_SLASH);
        fieldG.setPosition(8);
        fieldG.setLength(10);
        assertEquals(fieldG.convertFieldToSQL("ABC;25;30/12/2001"),
            java.sql.Date.valueOf("2001-12-30"));

        DateFieldImport fieldH =
            new DateFieldImport("une Date", FieldImportHome.DD_MM_YY_SLASH);
        fieldH.setPosition(8);
        fieldH.setLength(8);
        assertEquals(fieldH.convertFieldToSQL("ABC;25;30/12/01"),
            java.sql.Date.valueOf("2001-12-30"));
    }


    /**
     * A unit test for JUnit
     */
    public void test_getSQLType() {
        DateFieldImport fieldA =
            new DateFieldImport("une Date", FieldImportHome.YYYY_MM_DD_SLASH);
        fieldA.setPosition(8);
        fieldA.setLength(10);
        assertEquals(fieldA.getSQLType(), java.sql.Types.DATE);
    }


    //Tests pour fichiers à longueur fixe
    /**
     * A unit test for JUnit
     *
     * @exception Exception Description of Exception
     */
    public void test_translateField() throws Exception {
        DateFieldImport field =
            new DateFieldImport("label", FieldImportHome.YYYY_MM_DD_SLASH);
        field.setPosition(1);
        field.setLength(10);
        assertEquals(field.translateField(null), null);
        assertEquals(field.translateField(""), null);
        assertEquals(field.translateField("0000/00/00"), null);
    }


    //Tests pour fichiers à longueur fixe
    /**
     * A unit test for JUnit
     *
     * @exception Exception Description of Exception
     */
    public void test_translateField_DD_MM_YY_HYPHEN()
            throws Exception {
        DateFieldImport field =
            new DateFieldImport("label", FieldImportHome.DD_MM_YY_HYPHEN);
        field.setPosition(1);
        field.setLength(8);
        assertEquals(field.translateField(null), null);
        assertEquals(field.translateField(""), null);
        assertEquals(field.translateField("00-00-00"), null);
    }


    //Tests pour fichiers à longueur fixe
    /**
     * A unit test for JUnit
     *
     * @exception Exception Description of Exception
     */
    public void test_translateField_Error() throws Exception {
        DateFieldImport field =
            new DateFieldImport("label", FieldImportHome.YYYY_MM_DD_SLASH);
        field.setPosition(1);
        field.setLength(3);
        assertEquals(field.translateField("2001/12/30"),
            java.sql.Date.valueOf("2001-12-30"));
        try {
            field.translateField("a string");
            fail("Une exception devrait etre lance. 'a string' n'est pas une date");
        }
        catch (BadFormatException e) {}
        try {
            field.translateField("2001-12-30");
            fail("Une exception devrait etre lance. '2001-12-30' n'est pas une date");
        }
        catch (BadFormatException e) {}
    }


    //Tests pour fichiers à longueur fixe
    /**
     * A unit test for JUnit
     *
     * @exception Exception Description of Exception
     */
    public void test_translateField_ErrorBadOrder()
            throws Exception {
        DateFieldImport field =
            new DateFieldImport("label", FieldImportHome.YYYY_MM_DD_SLASH);
        field.setPosition(1);
        field.setLength(3);
        assertEquals(field.translateField("2001/12/30"),
            java.sql.Date.valueOf("2001-12-30"));
        try {
            Object result = field.translateField("30/12/2001");
            fail("Une exception devrait etre lance. '30/12/2001' n'a pas le bon ordre : "
                + result);
        }
        catch (BadFormatException e) {}
    }


    public void test_translateField_ErrorFormatYear()
            throws Exception {
        DateFieldImport field =
            new DateFieldImport("label", FieldImportHome.DD_MM_YYYY_SLASH);
        field.setPosition(1);
        field.setLength(3);
        assertEquals(field.translateField("30/12/2002"),
            java.sql.Date.valueOf("2002-12-30"));
        try {
            Object result = field.translateField("30/12/02");
            fail("L'année est codé sur 4 charactère et non 2 : " + result);
        }
        catch (BadFormatException e) {}
    }


    public void test_translateField_ErrorALaCon()
            throws Exception {
        DateFieldImport field =
            new DateFieldImport("label", FieldImportHome.DD_MM_YYYY_SLASH);
        field.setPosition(1);
        field.setLength(3);
        try {
            Object result = field.translateField("30  /12/02");
            fail("ya des blancs : " + result);
        }
        catch (BadFormatException e) {}
        try {
            Object result = field.translateField("30/12/02  ");
            fail("ya encore des blancs : " + result);
        }
        catch (BadFormatException e) {}
    }
}
