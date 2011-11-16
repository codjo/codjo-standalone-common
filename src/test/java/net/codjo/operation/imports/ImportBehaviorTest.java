/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.operation.imports;
import net.codjo.utils.TestEnvironnement;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
/**
 * Test
 *
 * @author $Author: gonnot $
 * @version $Revision: 1.1.1.1 $
 */
public class ImportBehaviorTest extends TestCase {
    //Portefeuille PIMS (fixe): IMPORT_SETTINGS_ID=14
    ImportBehavior importBehaviorF;

    //Complement strategique (variable): IMPORT_SETTINGS_ID=11
    ImportBehavior importBehaviorV;
    TestEnvironnement testEnv;


    /**
     * Constructor for the ImportBehaviorTest object
     *
     * @param name Description of Parameter
     */
    public ImportBehaviorTest(String name) {
        super(name);
    }


    /**
     * A unit test suite for JUnit
     *
     * @return The test suite
     */
    public static Test suite() {
        return new TestSuite(ImportBehaviorTest.class);
    }


    /**
     * A unit test for JUnit
     */
    public void test_addFieldImport_Variable() {
        StringFieldImport fieldImport = new StringFieldImport("DB_NAME");

        assertEquals("FixedLength", fieldImport.getFixedLength(), true);

        importBehaviorV.addFieldImport(fieldImport);

        assertEquals("FixedLength apres add", fieldImport.getFixedLength(), false);
        assertEquals("Separator", fieldImport.getSeparator(), "\t");
    }


    /**
     * A unit test for JUnit
     *
     * @throws Exception Description of Exception
     */
    public void test_FileName() throws Exception {
        //Tests pour fichiers à longueur fixe
        assertEquals(importBehaviorF.findRealFileName(
              importBehaviorF.getAutoInputFile(),
              testEnv.getPeriod200012()).getName(), "PENEL_PTFPIMS1200.TXT");
        assertEquals(importBehaviorF.getAutoInputFile().getParent(),
                     "D:\\Penelope\\test\\Files");
        assertEquals(importBehaviorF.findRealFileName(
              importBehaviorF.getManuInputFile(),
              testEnv.getPeriod200012()).getName(), "PENEL_PTFPIMS1200.TXT");
        assertEquals(importBehaviorF.getManuInputFile().getParent(),
                     "D:\\Penelope\\test\\Files");

        //Tests pour fichiers à longueur variable
        assertEquals(importBehaviorV.findRealFileName(
              importBehaviorV.getAutoInputFile(),
              testEnv.getPeriod200012()).getName(), "PENEL_STRATEG_0012.TXT");
        assertEquals(importBehaviorV.getAutoInputFile().getParent(),
                     "D:\\Penelope\\test\\Files");
        assertEquals(importBehaviorV.findRealFileName(
              importBehaviorV.getManuInputFile(),
              testEnv.getPeriod200012()).getName(), "PENEL_STRATEG_0012.TXT");
        assertEquals(importBehaviorV.getManuInputFile().getParent(),
                     "D:\\Penelope\\test\\Files");
    }


    /**
     * A unit test for JUnit
     *
     * @throws Exception Description of Exception
     */
    public void test_getAutoOutputFile() throws Exception {
        //Tests pour fichiers à longueur fixe
        assertEquals(importBehaviorF.getAutoOutputFile().getName(),
                     "PENEL_PTFPIMSmmaa.TXT");
        assertEquals(importBehaviorF.getAutoOutputFile().getParent(),
                     "\\\\tahiti\\transfert2\\4d\\penelop2\\out_box");

        //Tests pour fichiers à longueur variable
        assertEquals(importBehaviorV.getAutoOutputFile().getName(),
                     "PENEL_STRATEG_aamm.TXT");
        assertEquals(importBehaviorV.getAutoOutputFile().getParent(),
                     "\\\\tahiti\\transfert2\\4d\\penelop2\\out_box");
    }


    /**
     * A unit test for JUnit
     */
    public void test_addFieldImport_NullPointer() {
        //Tests pour fichiers à longueur fixe
        try {
            importBehaviorF.addFieldImport(null);
            fail("Le field Import est incorrecte (=null)");
        }
        catch (IllegalArgumentException ef) {
        }

        //Tests pour fichiers à longueur variable
        try {
            importBehaviorV.addFieldImport(null);
            fail("Le field Import est incorrecte (=null)");
        }
        catch (IllegalArgumentException ev) {
        }
    }


    /**
     * A unit test for JUnit
     */
    public void test_addFieldImport_BadLength() {
        //Tests pour fichiers à longueur fixe
        StringFieldImport field = new StringFieldImport("");
        field.setPosition(570);
        field.setLength(10);
        try {
            importBehaviorF.addFieldImport(field);
            fail(
                  "Le field Import est incorrecte (depassement de la longueur d'enregistrement)");
        }
        catch (IllegalArgumentException ef) {
        }
    }


    /**
     * A unit test for JUnit
     */
    public void test_addFieldImport_BadPosition() {
        //Tests pour fichiers à longueur fixe
        StringFieldImport fieldF = new StringFieldImport("");
        fieldF.setPosition(580);
        fieldF.setLength(10);
        try {
            importBehaviorF.addFieldImport(fieldF);
            fail(
                  "Le field Import est incorrecte (depassement de la longueur d'enregistrement)");
        }
        catch (IllegalArgumentException ef) {
        }
    }


    /**
     * A unit test for JUnit
     *
     * @throws Exception Description of Exception
     */
    public void test_proceed_TooManyField() throws Exception {
//        //Tests pour fichiers à longueur variable
//        TableHome tableHome = testEnv.getTableHome();
//        PortfolioGroupHome pfHome = testEnv.getPortfolioGroupHome();
//        OperationHome opeHome = testEnv.getOperationHome();
//        List opeList = opeHome.getAllOperation(testEnv.getPeriod200012());
//        Operation opeImportCompStrat = (Operation) opeList.get(6);
//
//        // Ajout de FieldImport pour depasser le nombre de
//        // colonnes de la table.
//        importBehaviorV.addFieldImport(new StringFieldImport("DB_NAME"));
//        importBehaviorV.addFieldImport(new StringFieldImport("DB_NAME"));
//        importBehaviorV.addFieldImport(new StringFieldImport("DB_NAME"));
//        importBehaviorV.addFieldImport(new StringFieldImport("DB_NAME"));
//
//        try {
//            importBehaviorV.proceed(opeImportCompStrat);
//            fail("Doit echouer, car trop de FieldImport.");
//        }
//        catch (Exception e) {
//        }
    }


    /**
     * The JUnit setup method
     */
    protected void setUp() {
        //Tests pour fichiers à longueur fixe
        importBehaviorF =
              new ImportBehavior("Portefeuille PIMS", "PENEL_PTFPIMSmmaa.TXT",
                                 "D:\\Penelope\\test\\Files", 573, "Livraison prod faite le 26/01/2001",
                                 "\\\\tahiti\\transfert2\\4d\\penelop2\\out_box\\",
                                 "D:\\Penelope\\test\\Files", true, null, false, null);
        StringFieldImport fieldA = new StringFieldImport("BOOK_KEEPING_PLAN");
        fieldA.setPosition(1);
        fieldA.setLength(4);
        fieldA.setFixedLength(true);
        fieldA.setSeparator(null);
        importBehaviorF.addFieldImport(fieldA);

        StringFieldImport fieldB = new StringFieldImport("PORTFOLIO_TYPE");
        fieldB.setPosition(6);
        fieldB.setLength(5);
        fieldA.setFixedLength(true);
        fieldA.setSeparator(null);
        importBehaviorF.addFieldImport(fieldB);

        //Tests pour fichiers à longueur variable
        importBehaviorV =
              new ImportBehavior("Titres strategiques", "PENEL_STRATEG_aamm.TXT",
                                 "D:\\Penelope\\test\\Files", 0, null,
                                 "\\\\tahiti\\transfert2\\4d\\penelop2\\out_box\\",
                                 "D:\\Penelope\\test\\Files", false, "\t", true, null);
        StringFieldImport fieldC = new StringFieldImport("PORTFOLIO");
        fieldC.setPosition(2);
        fieldC.setLength(0);
        fieldC.setFixedLength(false);
        fieldC.setSeparator("\t");
        importBehaviorV.addFieldImport(fieldC);

        StringFieldImport fieldD = new StringFieldImport("SECURITY_CODE");
        fieldD.setPosition(3);
        fieldD.setLength(0);
        fieldD.setFixedLength(false);
        fieldD.setSeparator("\t");
        importBehaviorV.addFieldImport(fieldD);
        TestEnvironnement.forceFakeDriver();
        testEnv = TestEnvironnement.newEnvironment();
    }


    /**
     * The teardown method for JUnit
     */
    protected void tearDown() {
        testEnv.close();
    }
}
