/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.operation.treatment;
import net.codjo.expression.ExpressionManager;
import net.codjo.model.Table;
import net.codjo.model.TableHome;
import net.codjo.persistent.Reference;
import net.codjo.persistent.UnknownIdException;
import net.codjo.utils.TestEnvironnement;
import fakedb.FakeDriver;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.ListResourceBundle;
import junit.framework.TestCase;
import org.apache.log4j.Logger;
/**
 * Test <code>AbstractTreatmentBehaviorHome</code> .
 *
 * @author $Author: blazart $
 * @version $Revision: 1.3 $
 */
public class TreatmentBehaviorHomeTest extends TestCase {
    private static final Logger APP = Logger.getLogger(TreatmentBehaviorHomeTest.class);
    private TestEnvironnement testEnv;

    public void test_getReference() throws Exception {
        BasicTreatmentHome behaviorHome = getTreatmentBehaviorHome();

        Reference ref = behaviorHome.getReference(800);

        fakeExpressionRow(1);
        fakeUnitRow(800, 1);
        // Table 2
        fakeGetColumnsAndPK(2);
        testEnv.fakeTableRow(2);
        // Table 1
        fakeGetColumnsAndPK(1);
        testEnv.fakeTableRow(1);
        // Treatment
        fakeTreatmentRow(800);
        TreatmentBehavior obj = (TreatmentBehavior)ref.getObject();

        if (behaviorHome.calledMethod.size() != 3) {
            APP.debug("Une methode du TreatmentBehaviorHome " + "n'est pas appele : "
                + behaviorHome.calledMethod);
        }
        assertEquals("Methodes", behaviorHome.calledMethod.size(), 3);
    }


    public void test_loadExpressionManager() throws Exception {
        AbstractTreatmentBehaviorHome behaviorHome = getTreatmentBehaviorHome();

        Reference ref = behaviorHome.getReference(800);

        FakeDriver.getDriver()
                  .pushResultSet(FakeDriver.EMPTY,
            "select *  from PM_TREATMENT_UNIT where TREATMENT_SETTINGS_ID=800");
        fakeGetColumnsAndPK(2);
        testEnv.fakeTableRow(2);
        fakeGetColumnsAndPK(1);
        testEnv.fakeTableRow(1);
        fakeTreatmentRow(800);
        TreatmentBehavior obj = (TreatmentBehavior)ref.getObject();

        fakeExpressionRow(1);
        ExpressionManager em = behaviorHome.loadExpressionManager(1, obj);

        List destField = em.getDestFieldList();
        APP.debug("DestFields -> " + destField);
        assertTrue("A", destField.contains("A"));
        assertTrue("B", destField.contains("B"));
        assertTrue("A>B", destField.indexOf("B") > destField.indexOf("A"));

        assertNotNull("DestColumn", em.getDestColumn());
        assertNotNull("SourceColumn", em.getSourceColumn());
    }


    @Override
    protected void setUp() throws Exception {
        System.getProperties().put("TEST_ENVIRONMENT", "net.codjo.utils.TestEnvironnement");
        TestEnvironnement.forceFakeDriver();
        testEnv = TestEnvironnement.newEnvironment();
    }


    @Override
    protected void tearDown() {
        testEnv.close();
    }


    private void fakeExpressionRow(int unitId) {
        Object[][] matrix = {
                {"DB_TARGET_FIELD_NAME", "EXPRESSION"},
                {"A", "SRC_A"},
                {"B", "SRC_B"},
            };
        FakeDriver.getDriver()
                  .pushResultSet(matrix,
            "select DB_TARGET_FIELD_NAME, EXPRESSION from PM_TREATMENT_EXPRESSION"
            + " where TREATMENT_UNIT_SETTINGS_ID=" + unitId + " order by PRIORITY");
    }


    private void fakeGetColumnsAndPK(int tableId) {
        // Pk
        // Requete MetaData pour connaitre la table
        Object[][] tablePk = {
                {"A"}
            };

        // GetColumns
        Object[][] matrix =
            {
                {},
                {null, null, null, "A", Types.INTEGER},
                {null, null, null, "B", Types.VARCHAR},
                {null, null, null, "C", Types.NUMERIC}
            };
        FakeDriver.getDriver()
                  .pushResultSet(matrix,
            "FakeDatabaseMetaData.getColumns(null, null, TABLE_" + tableId + ", null)");
        FakeDriver.getDriver()
                  .pushResultSet(tablePk,
            "FakeDatabaseMetaData.getPrimaryKeys(null, null, TABLE_" + tableId + ")");
    }


    /**
     * Constructor for the fakeTreatmentRow object
     *
     * @param id Description of Parameter
     */
    private void fakeTreatmentRow(int id) {
        Object[][] matrix =
            {
                {"TREATMENT_SETTINGS_ID", "DEST_TABLE_ID", "SOURCE_TABLE_ID", "SELECT_TABLE_ID", "SOURCE_TYPE"},
                {id, 1, 1, 2, "T"}
            };
        FakeDriver.getDriver()
                  .pushResultSet(matrix,
            "select * from PM_TREATMENT_SETTINGS where TREATMENT_SETTINGS_ID=" + id);
    }


    /**
     * Overview.
     *
     * @param id Description of Parameter
     * @param unitId Description of Parameter
     */
    private void fakeUnitRow(int id, int unitId) {
        Object[][] matrix =
            {
                {
                    "TREATMENT_UNIT_SETTINGS_ID", "TREATMENT_SETTINGS_ID", "INSERT_CRITERION", "COMMENTRY",
                    "AGGREGATION", "WRITE_MODE", "UPDATE_SOURCE_KEY", "UPDATE_DEST_KEY", "UPDATE_CRITERIA"
                },
                {unitId, id, "", "", Boolean.FALSE, "INSERT", "", "", ""}
            };
        FakeDriver.getDriver()
                  .pushResultSet(matrix, "select *  from PM_TREATMENT_UNIT where TREATMENT_SETTINGS_ID=" + id);
    }


    /**
     * DOCUMENT ME!
     *
     * @return The TreatmentBehaviorHome value
     *
     * @throws Exception Description of Exception
     */
    private BasicTreatmentHome getTreatmentBehaviorHome()
            throws Exception {
        Object[][] matrix = {
              {},
                {null, null, null, "TREATMENT_SETTINGS_ID", new Integer(Types.INTEGER)},
                {null, null, null, "DEST_TABLE_ID", new Integer(Types.INTEGER)},
                {null, null, null, "SOURCE_TABLE_ID", new Integer(Types.INTEGER)},
                {null, null, null, "SELECT_TABLE_ID", new Integer(Types.INTEGER)},
                {null, null, null, "SOURCE_TYPE", new Integer(Types.CHAR)}
            };
        FakeDriver.getDriver()
                  .pushResultSet(matrix,
            "FakeDatabaseMetaData.getColumns(null, null, PM_TREATMENT_SETTINGS, null)");

        return new BasicTreatmentHome(testEnv);
    }

    /**
     * Overview.
     *
     * @author $Author: blazart $
     * @version $Revision: 1.3 $
     */
    public static class BasicTreatmentHome extends AbstractTreatmentBehaviorHome {
        /** Description of the Field */
        public List<String> calledMethod = new ArrayList<String>();
        /** Description of the Field */
        public TableHome tableHome;

/**
         * DOCUMENT ME!
         *
         * @param testEnv Description of Parameter
         *
         * @throws SQLException Description of Exception
         */
        public BasicTreatmentHome(TestEnvironnement testEnv)
                throws SQLException {
            super(testEnv.getHomeConnection(), new BasicBundle(), testEnv.getConnectionManager(),
                testEnv.getTableHome(), testEnv.getPeriodHome());
            tableHome = testEnv.getTableHome();
        }

        /**
         * DOCUMENT ME!
         *
         * @param parm1 Description of Parameter
         * @param parm2 Description of Parameter
         * @param parm3 Description of Parameter
         * @param parm4 Description of Parameter
         *
         * @return Description of the Returned Value
         */
        protected TreatmentSelection buildTreatmentSelection(Table parm1, Table parm2, Table parm3,
            String parm4) {
            calledMethod.add("buildTreatmentSelection");
            return null;
        }


        /**
         * DOCUMENT ME!
         *
         * @param parm1 Description of Parameter
         * @param parm2 Description of Parameter
         * @param bpk
         * @param parm3 Description of Parameter
         *
         * @return Description of the Returned Value
         *
         * @throws UnknownIdException Description of Exception
         * @throws SQLException Description of Exception
         */
        protected TreatmentUnitSelection buildTreatmentUnitSelection(int parm1, boolean parm2, String[] bpk,
            TreatmentBehavior parm3) throws UnknownIdException, SQLException {
            calledMethod.add("buildTreatmentUnitSelection");
            return null;
        }


        /**
         * DOCUMENT ME!
         *
         * @param aggregation Description of Parameter
         * @param wm
         * @param updateSrcKey
         * @param behavior Description of Parameter
         *
         * @return Description of the Returned Value
         */
        protected String[] determineBreakKeys(boolean aggregation, String wm, String updateSrcKey,
            TreatmentBehavior behavior) {
            calledMethod.add("determineBreakKeys");
            return null;
        }
    }


    /**
     * Bundle d'init.
     *
     * @author $Author: blazart $
     * @version $Revision: 1.3 $
     */
    private static class BasicBundle extends ListResourceBundle {
        static final Object[][] contents =
            {
                {"home.dbTableName", "PM_TREATMENT_SETTINGS"},
                {"object.class", "net.codjo.operation.treatment.TreatmentBehavior"},
                {"object.constructor", "SOURCE_TABLE_ID;DEST_TABLE_ID;SELECT_TABLE_ID;SOURCE_TYPE"},
                {"primaryKey", "AUTOMATIC"},
                {"primaryKey.constructor", "TREATMENT_SETTINGS_ID"},
                {"property.destTable", "DEST_TABLE_ID"},
                {"property.sourceTable", "SOURCE_TABLE_ID"},
                {"property.id", "TREATMENT_SETTINGS_ID"},
                {"property.selectionTable", "SELECT_TABLE_ID"},
                {"property.sourceType", "SOURCE_TYPE"},
                {"translator.destTable", "tableHome.getTable"},
                {"translator.sourceTable", "tableHome.getTable"},
                {"translator.selectionTable", "tableHome.getTable"},
            };

        /**
         * Gets the Contents attribute of the MyResource object
         *
         * @return The Contents value
         */
        public Object[][] getContents() {
            return contents;
        }
    }
}
