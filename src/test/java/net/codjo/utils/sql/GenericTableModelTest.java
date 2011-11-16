/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.utils.sql;
import net.codjo.model.Table;
import net.codjo.utils.TestEnvironnement;
import fakedb.FakeDriver;
import java.math.BigDecimal;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.log4j.Logger;

/**
 * DOCUMENT ME!
 *
 * @author $Author: blazart $
 * @version $Revision: 1.3 $
 */
public class GenericTableModelTest extends TestCase {
    GenericTableModel model;
    TestEnvironnement testEnv;
    // Log
    private static final Logger APP = Logger.getLogger(GenericTableModelTest.class);

    /**
     * Constructor for the NonPersistentTableModelTest object
     *
     * @param name Description of Parameter
     */
    public GenericTableModelTest(String name) {
        super(name);
    }

    /**
     * A unit test suite for JUnit
     *
     * @return The test suite
     */
    public static Test suite() {
        return new TestSuite(GenericTableModelTest.class);
    }


    /**
     * A unit test for JUnit
     *
     * @exception Exception Description of Exception
     */
    public void test_addNewLine() throws Exception {
        assertEquals(model.getRowCount(), 5);
        model.addNewLine();
        model.addNewLine();
        assertEquals(model.getRowCount(), 7);
        model.deleteLine(0);
        model.deleteLine(0);
        assertEquals(model.getRowCount(), 5);
    }


    /**
     * A unit test for JUnit
     */
    public void test_getALineOfKey() {
        Map hm = model.getALineOfKey(4);
        assertNotNull(hm);
        assertEquals(((BigDecimal)hm.get("THE_IDENTITY")), model.getValueAt(4, 0));
    }


    /**
     * A unit test for JUnit
     */
    public void test_getColumnClass() {
        assertEquals(model.getColumnClass(0), Number.class);
        assertEquals(model.getColumnClass(1), Boolean.class);
        assertEquals(model.getColumnClass(2), java.sql.Date.class);
        assertEquals(model.getColumnClass(3), Integer.class);
        assertEquals(model.getColumnClass(4), String.class);
        assertEquals(model.getColumnClass(5), String.class);
        assertEquals(model.getColumnClass(6), String.class);
        assertEquals(model.getColumnClass(7), Number.class);
        assertEquals(model.getColumnClass(8), String.class);
        assertEquals(model.getColumnClass(9), Integer.class);
    }


    /**
     * A unit test for JUnit
     */
    public void test_getColumnCount() {
        assertEquals(model.getColumnCount(), 10);
    }


    /**
     * A unit test for JUnit
     */
    public void test_getColumnName() {
        assertEquals(model.getColumnName(0), "colonne_identité");
        assertEquals(model.getColumnName(1), "colonne_bit");
        assertEquals(model.getColumnName(2), "colonne_date");
        assertEquals(model.getColumnName(3), "colonne_entier");
        assertEquals(model.getColumnName(4), "colonne_varchar");
        assertEquals(model.getColumnName(5), "colonne_char_1");
        assertEquals(model.getColumnName(6), "colonne_char_3");
        assertEquals(model.getColumnName(7), "colonne_numeric_17_5");
        assertEquals(model.getColumnName(8), "colonne_text");
        assertEquals(model.getColumnName(9), "colonne_small_int");
    }


    /**
     * A unit test for JUnit
     *
     * @exception Exception Description of Exception
     */
    public void test_getValueAt() throws Exception {
//        assertEquals(model.getValueAt(0, 0), new BigDecimal(1));
        assertEquals(model.getValueAt(1, 1), Boolean.TRUE);
        assertEquals(model.getValueAt(2, 3), new Integer(100));
        assertEquals(model.getValueAt(2, 4), "et obscure toute seule, perdue");
        assertEquals(model.getValueAt(2, 5), "C");
        assertEquals(model.getValueAt(3, 6), "KLM");
        assertEquals(model.getValueAt(4, 7), new BigDecimal("123456789012.12345"));
    }


    /**
     * The JUnit setup method
     *
     * @exception Exception Description of Exception
     */
    protected void setUp() throws Exception {
        TestEnvironnement.forceFakeDriver();
        testEnv = TestEnvironnement.newEnvironment();

        Dependency.setConnectionManager(testEnv.getConnectionManager());
        Dependency.setHomeConnection(testEnv.getHomeConnection());

        Table table = getTableBidon();
        fakeBidonRow();
        Object[][] matrix = {
                {},
                {new Integer(5)}
            };
        FakeDriver.getDriver().pushResultSet(matrix, "select count(*)  from BIDON ");
        initAllLabel();
        initGuiFields();
        APP.debug("-----------");
        GenericTable jtable = new GenericTable(table, false);
        APP.debug("ffff-----------");

        APP.debug("setUp  :  ");
        model = jtable.getTableModel();
    }


    /**
     * The teardown method for JUnit
     */
    protected void tearDown() {
        testEnv.close();
    }


    /**
     * Overview.
     */
    void fakeBidonRow() {
        Object[][] matrix =
            {
                {
                    "THE_IDENTITY", "THE_BIT", "THE_DATE", "THE_INTEGER", "THE_VARCHAR_30",
                    "THE_CHAR_1", "THE_CHAR_3", "THE_NUMERIC_17_5", "THE_TEXT",
                    "THE_SMALL_INT", "THE_IDENTITY"
                },
                {
                    new BigDecimal(0), Boolean.FALSE, null, new Integer(0),
                    "Le petit chaperon rouge se", "A", "ABC",
                    new BigDecimal("12345678.12345"),
                    "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa_ok",
                    new Integer(0), new BigDecimal(0)
                },
                {
                    new BigDecimal(1), Boolean.TRUE, null, new Integer(10),
                    "ballade dans la forêt sombre", "B", "DEF",
                    new BigDecimal("123456789.12345"),
                    "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb_ok",
                    new Integer(10), new BigDecimal(1)
                },
                {
                    new BigDecimal(2), Boolean.FALSE, null, new Integer(100),
                    "et obscure toute seule, perdue", "C", "GHI",
                    new BigDecimal("1234567890.12345"),
                    "ccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc_ok",
                    new Integer(20), new BigDecimal(2)
                },
                {
                    new BigDecimal(3), Boolean.TRUE, null, new Integer(1000),
                    "quand soudain un hérisson", "D", "KLM",
                    new BigDecimal("12345678901.12345"),
                    "ddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd_ok",
                    new Integer(30), new BigDecimal(3)
                },
                {
                    new BigDecimal(4), Boolean.FALSE, null, new Integer(10000),
                    "traverse la route et splatch", "E", "NOP",
                    new BigDecimal("123456789012.12345"),
                    "eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee_ok",
                    new Integer(40), new BigDecimal(4)
                }
            };
        FakeDriver.getDriver().pushResultSet(matrix,
            "select BIDON.THE_IDENTITY, BIDON.THE_BIT, BIDON.THE_DATE, BIDON.THE_INTEGER, BIDON.THE_VARCHAR_30, BIDON.THE_CHAR_1, BIDON.THE_CHAR_3, BIDON.THE_NUMERIC_17_5, BIDON.THE_TEXT, BIDON.THE_SMALL_INT, BIDON.THE_IDENTITY  from BIDON  ");
    }


    /**
     * Overview.
     */
    void initAllLabel() {
        String[][] matrix =
            {
                {"THE_IDENTITY", "colonne_identité"},
                {"THE_BIT", "colonne_bit"},
                {"THE_DATE", "colonne_date"},
                {"THE_INTEGER", "colonne_entier"},
                {"THE_VARCHAR_30", "colonne_varchar"},
                {"THE_CHAR_1", "colonne_char_1"},
                {"THE_CHAR_3", "colonne_char_3"},
                {"THE_NUMERIC_17_5", "colonne_numeric_17_5"},
                {"THE_TEXT", "colonne_text"},
                {"THE_SMALL_INT", "colonne_small_int"}
            };
        for (int i = matrix.length - 1; i >= 0; i--) {
            initFieldLabel(matrix[i][0], matrix[i][1]);
        }
    }


    /**
     * Overview.
     *
     * @param col Description of Parameter
     * @param label Description of Parameter
     */
    void initFieldLabel(String col, String label) {
        Object[][] matrix = {
                {"FIELD_LABEL"},
                {label}
            };
        FakeDriver.getDriver().pushResultSet(matrix,
            "select FIELD_LABEL from PM_FIELD_LABEL where DB_TABLE_NAME='BIDON' "
            + "and DB_FIELD_NAME='" + col + "'");
    }


    /**
     * Overview.
     */
    void initGuiFields() {
        Object[][] tableDef =
            {
                {"DB_TABLE_NAME", "DB_FIELD_NAME", "COLUMN_INDEX", "SIZE", "EDITABLE"},
                {"BIDON", "THE_IDENTITY", new Integer(1), new Integer(5), Boolean.FALSE},
                {"BIDON", "THE_BIT", new Integer(2), new Integer(10), Boolean.TRUE},
                {"BIDON", "THE_DATE", new Integer(3), new Integer(15), Boolean.TRUE},
                {"BIDON", "THE_INTEGER", new Integer(4), new Integer(20), Boolean.TRUE},
                {"BIDON", "THE_VARCHAR_30", new Integer(5), new Integer(25), Boolean.FALSE},
                {"BIDON", "THE_CHAR_1", new Integer(6), new Integer(30), Boolean.FALSE},
                {"BIDON", "THE_CHAR_3", new Integer(7), new Integer(35), Boolean.FALSE},
                {
                    "BIDON", "THE_NUMERIC_17_5", new Integer(8), new Integer(40),
                    Boolean.TRUE
                },
                {"BIDON", "THE_TEXT", new Integer(9), new Integer(45), Boolean.TRUE},
                {"BIDON", "THE_SMALL_INT", new Integer(10), new Integer(50), Boolean.TRUE}
            };

        FakeDriver.getDriver().pushResultSet(tableDef,
            "select * from PM_GUI_FIELDS where DB_TABLE_NAME='BIDON' order by COLUMN_INDEX");
    }


    /**
     * Gets the TableBidon attribute of the GenericTableModelTest object
     *
     * @return The TableBidon value
     *
     * @exception Exception Description of Exception
     */
    private Table getTableBidon() throws Exception {
        // Requete MetaData pour connaitre la table
        Object[][] pk = {
                {"COLUMN_NAME"},
                {"THE_IDENTITY"}
            };
        FakeDriver.getDriver().pushResultSet(pk,
            "FakeDatabaseMetaData.getPrimaryKeys(null, null, BIDON)");
        Object[][] tableDef =
            {
                {},
                {null, null, null, "THE_IDENTITY", new Integer(Types.NUMERIC)},
                {null, null, null, "THE_BIT", new Integer(Types.BIT)},
                {null, null, null, "THE_DATE", new Integer(Types.TIMESTAMP)},
                {null, null, null, "THE_INTEGER", new Integer(Types.INTEGER)},
                {null, null, null, "THE_VARCHAR_30", new Integer(Types.VARCHAR)},
                {null, null, null, "THE_CHAR_1", new Integer(Types.CHAR)},
                {null, null, null, "THE_CHAR_3", new Integer(Types.VARCHAR)},
                {null, null, null, "THE_NUMERIC_17_5", new Integer(Types.NUMERIC)},
                {null, null, null, "THE_TEXT", new Integer(Types.LONGVARCHAR)},
                {null, null, null, "THE_SMALL_INT", new Integer(Types.SMALLINT)}
            };
        FakeDriver.getDriver().pushResultSet(tableDef,
            "FakeDatabaseMetaData.getColumns(null, null, BIDON, null)");
        FakeDriver.getDriver().pushResultSet(FakeDriver.EMPTY,
            "select * from PM_TABLE where DB_TABLE_NAME='BIDON'");
        return testEnv.getTableHome().getTable("BIDON");
    }
}