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
public class StringFieldImportTest extends TestCase {
    /**
     * Constructeur de StringFieldImportTest
     *
     * @param name Description of Parameter
     */
    public StringFieldImportTest(String name) {
        super(name);
    }

    /**
     * A unit test suite for JUnit
     *
     * @return The test suite
     */
    public static Test suite() {
        return new TestSuite(StringFieldImportTest.class);
    }


    /**
     * A unit test for JUnit
     *
     * @exception Exception Description of Exception
     */
    public void test_convertFieldToSQL() throws Exception {
        //Tests pour fichiers à longueur fixe
        StringFieldImport field = new StringFieldImport("label");
        field.setPosition(1);
        field.setLength(3);
        assertEquals(field.convertFieldToSQL("ABC;25;12/01/2001"), "ABC");
        assertEquals(field.convertFieldToSQL("A'C;25;12/01/2001"), "A'C");
    }


    /**
     * A unit test for JUnit
     *
     * @exception Exception Description of Exception
     */
    public void test_translateField() throws Exception {
        StringFieldImport field = new StringFieldImport("label");
        field.setPosition(1);
        field.setLength(3);
        assertEquals(field.translateField(null), null);
        assertEquals(field.translateField(""), null);
    }


    /**
     * A unit test for JUnit
     */
    public void test_getSQLType() {
        StringFieldImport field = new StringFieldImport("label");
        field.setPosition(1);
        field.setLength(3);
        assertEquals(field.getSQLType(), java.sql.Types.VARCHAR);
    }


    /**
     * The JUnit setup method
     */
    protected void setUp() {}
}
