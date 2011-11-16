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
 * @author $Author: levequt $
 * @version $Revision: 1.4 $
 */
public class FieldImportTest extends TestCase {
    //Tests pour fichiers à longueur fixe
    /** Description of the Field */
    protected FieldImport fieldA;

    // Extraction Pos:5 Length:2
    // RemoveLeftZero:f
    /** Description of the Field */
    protected FieldImport fieldB;

    // Extraction Pos:1 Length:4
    // RemoveLeftZero:f
    /** Description of the Field */
    protected FieldImport fieldE;

    // Extraction Pos:5 Length:2
    // RemoveLeftZero:T
    //Tests pour fichiers à longueur variable
    /** Description of the Field */
    protected FieldImport fieldF;

    // Extraction Pos:2 Separator:tabulation
    // RemoveLeftZero:f
    /** Description of the Field */
    protected FieldImport fieldG;

    // Extraction Pos:2 Separator:";"
    // RemoveLeftZero:f
    /** Description of the Field */
    protected FieldImport fieldH;

    // Extraction Pos:2 Separator:tabulation
    // RemoveLeftZero:T
    /** Description of the Field */
    protected FieldImport fieldI;

    // Extraction Pos:1 Separator:";"
    // RemoveLeftZero:f
    protected FieldImport fieldJ;

    // Extraction Pos:5 Separator:tabulation
    // RemoveLeftZero:f
    /**
     * Constructeur de FieldImportTest
     *
     * @param name Description of Parameter
     */
    public FieldImportTest(String name) {
        super(name);
    }

    /**
     * A unit test suite for JUnit
     *
     * @return The test suite
     */
    public static Test suite() {
        return new TestSuite(FieldImportTest.class);
    }


    /**
     * A unit test for JUnit
     *
     * @exception Throwable Description of Exception
     */
    public void test_extractField_Basic() throws Throwable {
        //Tests pour fichiers à longueur fixe
        assertEquals(fieldA.convertFieldToSQL("ABC;25;12/01/2001"), "25");
        assertEquals(fieldA.convertFieldToSQL("ABC; 2;12/01/2001"), "2");

        assertEquals(fieldB.convertFieldToSQL("1.34"), "1.34");
        assertEquals(fieldB.convertFieldToSQL("1.34;FFEERTR"), "1.34");

        //Tests pour fichiers à longueur variable
        assertEquals(fieldF.convertFieldToSQL("102\t01\t3080\tFIL\tFIPA\tS"), "01");
        assertEquals(fieldF.convertFieldToSQL("102\t\t01\t3080\tFIL\tFIPA\tS"), "");

        assertEquals(fieldG.convertFieldToSQL("102;1.2;3080;FIL;FIPA;S"), "1.2");

        assertEquals(fieldI.convertFieldToSQL("102\t\t\t01\t3080\tFIL\tFIPA\tS"), "3080");
        assertEquals("1.2", fieldJ.convertFieldToSQL(";1.2;3080;FIL;FIPA;S"));
    }


    /**
     * A unit test for JUnit
     *
     * @exception Throwable Description of Exception
     */
    public void test_convertFieldToSQL_Filter() throws Throwable {
        //Tests pour fichiers à longueur fixe
        assertEquals(fieldE.convertFieldToSQL("ABC;20"), "20");
        assertEquals(fieldE.convertFieldToSQL("ABC;02"), "2");
        assertEquals(fieldE.convertFieldToSQL("ABC;00"), "0");

        //Tests pour fichiers à longueur variable
        assertEquals(fieldH.convertFieldToSQL("102	10	3080	FIL	FIPA	S"), "10");
        assertEquals(fieldH.convertFieldToSQL("102	01	3080	FIL	FIPA	S"), "1");
    }


    /**
     * A unit test for JUnit
     *
     * @exception Throwable Description of Exception
     */
    public void test_convertFieldToSQL_Filter_Trim()
            throws Throwable {
        //Tests pour fichiers à longueur fixe
        assertEquals(fieldB.convertFieldToSQL("   4;gg"), "4");
        assertEquals(fieldB.convertFieldToSQL("4   ;gg"), "4");
        assertEquals(fieldB.convertFieldToSQL(" 23 ;gg"), "23");
        assertEquals(fieldB.convertFieldToSQL("    ;gg"), "");

        //Tests pour fichiers à longueur variable
        assertEquals(fieldF.convertFieldToSQL("102\t 01\t3080\tFIL\tFIPA\tS"), "01");
        assertEquals(fieldF.convertFieldToSQL("102\t01 \t3080\tFIL\tFIPA\tS"), "01");
        assertEquals(fieldF.convertFieldToSQL("102\t 01 \t3080\tFIL\tFIPA\tS"), "01");
        assertEquals(fieldF.convertFieldToSQL("102\t3080\tFIL\tFIPA\tS"), "3080");
    }


    /**
     * A unit test for JUnit
     *
     * @exception BadFormatException Description of Exception
     */
    public void test_convertFieldToSQL_Error() throws BadFormatException {
        //Tests pour fichiers à longueur fixe
        try {
            fieldE.convertFieldToSQL("e");
            fail("Une exception devrait etre lancee (fixe).");
        }
        catch (FieldNotFoundException ef) {}

        //Tests pour fichiers à longueur variable
        try {
            fieldH.convertFieldToSQL("e");
            fail("Une exception devrait etre lancee (variable).");
        }
        catch (FieldNotFoundException ev) {}
    }


    /**
     * The JUnit setup method
     */
    protected void setUp() {
        //Tests pour fichiers à longueur fixe
        fieldA = new BasicFieldImport("a");
        fieldA.setPosition(5);
        fieldA.setLength(2);

        fieldB = new BasicFieldImport("b");
        fieldB.setPosition(1);
        fieldB.setLength(4);

        fieldE = new BasicFieldImport("e");
        fieldE.setPosition(5);
        fieldE.setLength(2);
        fieldE.setRemoveLeftZeros(true);

        //Tests pour fichiers à longueur variable
        fieldF = new BasicFieldImport("a");
        fieldF.setPosition(2);
        fieldF.setSeparator("\t");
        fieldF.setFixedLength(false);

        fieldG = new BasicFieldImport("b");
        fieldG.setPosition(2);
        fieldG.setSeparator(";");
        fieldG.setFixedLength(false);

        fieldH = new BasicFieldImport("e");
        fieldH.setPosition(2);
        fieldH.setSeparator("\t");
        fieldH.setFixedLength(false);
        fieldH.setRemoveLeftZeros(true);

        fieldI = new BasicFieldImport("f");
        fieldI.setPosition(5);
        fieldI.setSeparator("\t");
        fieldI.setFixedLength(false);

        fieldJ = new BasicFieldImport("j");
        fieldJ.setPosition(2);
        fieldJ.setSeparator(";");
        fieldJ.setFixedLength(false);
    }

    class BasicFieldImport extends FieldImport {
        /**
         * Constructeur de BasicFieldImport
         *
         * @param dbName Description of Parameter
         */
        public BasicFieldImport(String dbName) {
            super(dbName);
        }

        public int getSQLType() {
            return java.sql.Types.VARCHAR;
        }


        public Object translateField(String field)
                throws BadFormatException {
            return field;
        }
    }
}
