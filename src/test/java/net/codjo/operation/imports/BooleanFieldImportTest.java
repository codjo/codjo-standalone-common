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
 * @author $Author: gonnot $
 * @version $Revision: 1.1.1.1 $
 */
public class BooleanFieldImportTest extends TestCase {
    /**
     * Constructeur de BooleanFieldImportTest
     *
     * @param name Description of Parameter
     */
    public BooleanFieldImportTest(String name) {
        super(name);
    }

    /**
     * A unit test suite for JUnit
     *
     * @return The test suite
     */
    public static Test suite() {
        return new TestSuite(BooleanFieldImportTest.class);
    }


    /**
     * A unit test for JUnit
     *
     * @exception Exception Description of Exception
     */
    public void test_convertFieldToSQL() throws Exception {
        //Tests pour fichiers à longueur fixe
        BooleanFieldImport field = new BooleanFieldImport("label");
        field.setPosition(1);
        field.setLength(4);
        assertEquals(field.convertFieldToSQL("VRAI;25;12/01/2001"), Boolean.TRUE);
        assertEquals(field.convertFieldToSQL("FAUX;25;12/01/2001"), Boolean.FALSE);
        assertEquals(field.convertFieldToSQL("    ;25;12/01/2001"), Boolean.FALSE);
    }


    /**
     * A unit test for JUnit
     *
     * @exception Exception Description of Exception
     */
    public void test_translateField() throws Exception {
        //Tests pour fichiers à longueur fixe
        BooleanFieldImport field = new BooleanFieldImport("label");
        field.setPosition(1);
        field.setLength(3);
        assertEquals(field.translateField(null), Boolean.FALSE);
        assertEquals(field.translateField(""), Boolean.FALSE);
    }


    /**
     * A unit test for JUnit
     *
     * @exception Exception Description of Exception
     */
    public void test_translateField_Error() throws Exception {
        //Tests pour fichiers à longueur fixe
        BooleanFieldImport field = new BooleanFieldImport("label");
        field.setPosition(1);
        field.setLength(3);
        try {
            field.translateField("TOTO");
            fail("TOTO n'est pas un booleen");
        }
        catch (BadFormatException ef) {}
    }


    /**
     * A unit test for JUnit
     */
    public void test_getSQLType() {
        //Tests pour fichiers à longueur fixe
        BooleanFieldImport field = new BooleanFieldImport("label");
        field.setPosition(1);
        field.setLength(3);
        assertEquals(field.getSQLType(), java.sql.Types.BIT);
    }


    /**
     * The JUnit setup method
     */
    protected void setUp() {}
}
