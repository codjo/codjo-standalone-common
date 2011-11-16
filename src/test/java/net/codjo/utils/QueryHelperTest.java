/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.utils;
import fakedb.FakeDriver;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
/**
 * -
 *
 * @author $Author: spinae $
 * @version $Revision: 1.2 $
 */
public class QueryHelperTest extends TestCase {
    TestEnvironnement testEnv;
    Connection con;
    QueryHelper query;

    /**
     * DOCUMENT ME!
     *
     * @param Name_ Description of Parameter
     */
    public QueryHelperTest(String Name_) {
        super(Name_);
    }

    /**
     * A unit test suite for JUnit
     *
     * @return The test suite
     */
    public static Test suite() {
        return new TestSuite(QueryHelperTest.class);
    }


    /**
     * DOCUMENT ME!
     *
     * @exception SQLException Description of Exception
     */
    public void test_doSelectAll() throws SQLException {
        // aucun commit
        Object[][] matrix = {
                {"PERIOD"},
                {"200008"},
                {"200009"}
            };
        FakeDriver.getDriver().pushResultSet(matrix, "select * from AP_PERIOD");
        ResultSet rs = query.doSelectAll();

        rs.next();
        assertEquals(rs.getString("PERIOD"), "200008");

        rs.next();
        assertEquals(rs.getString("PERIOD"), "200009");
    }


    /**
     * DOCUMENT ME!
     *
     * @exception SQLException Description of Exception
     */
    public void test_doSelect() throws SQLException {
        Object[][] matrix = {
                {"PERIOD"},
                {"200012"}
            };
        FakeDriver.getDriver().pushResultSet(matrix,
            "select * from AP_PERIOD where PERIOD=200012");
        query.setSelectorValue("PERIOD", "200012");
        ResultSet rs = query.doSelect();
        assertTrue("Ligne existe", rs.next());
        assertEquals(rs.getString("PERIOD"), "200012");
    }


    /**
     * A unit test for JUnit
     */
    public void test_buildSelectQuery() {
        List columnList = new ArrayList();
        columnList.add("C");
        columnList.add("B");
        columnList.add("A");

        List whereList = new ArrayList();
        whereList.add("A");
        whereList.add("C");

        String query = QueryHelper.buildSelectQuery("MA_TABLE", columnList, whereList);
        assertEquals(query, "select C, B, A from MA_TABLE where A=? and C=?");
    }


    /**
     * A unit test for JUnit
     */
    public void test_buildSelectQuery_Star() {
        List columnList = null;

        List whereList = new ArrayList();
        whereList.add("A");
        whereList.add("C");

        String query = QueryHelper.buildSelectQuery("MA_TABLE", columnList, whereList);
        assertEquals(query, "select * from MA_TABLE where A=? and C=?");
    }


    /**
     * A unit test for JUnit
     */
    public void test_buildUpdateQuery() {
        List columnList = new ArrayList();
        columnList.add("C");
        columnList.add("B");
        columnList.add("A");

        List whereList = new ArrayList();
        whereList.add("A");
        whereList.add("C");

        String query = QueryHelper.buildUpdateQuery("MA_TABLE", columnList, whereList);
        assertEquals(query, "update MA_TABLE set C=? , B=? , A=? where A=? and C=?");
    }


    /**
     * A unit test for JUnit
     */
    public void test_buildUpdateQueryWithWhereClause() {
        List columnList = new ArrayList();
        columnList.add("C");
        columnList.add("B");
        columnList.add("A");

        List whereList = new ArrayList();
        whereList.add("A");
        whereList.add("C");

        String whereClause = "D='TITI'";

        String query =
            QueryHelper.buildUpdateQueryWithWhereClause("MA_TABLE", columnList,
                whereList, whereClause);
        assertEquals(query,
            "update MA_TABLE set C=? , B=? , A=? where A=? and C=? and D='TITI'");
    }


    /**
     * A unit test for JUnit
     */
    public void test_buildInsertStatement() {
        List columnList = new ArrayList();
        columnList.add("C");
        columnList.add("B");
        columnList.add("A");

        String query = QueryHelper.buildInsertQuery("MA_TABLE", columnList);
        assertEquals(query, "insert into MA_TABLE (C, B, A) values (?, ?, ?)");
    }


    /**
     * A unit test for JUnit
     *
     * @exception SQLException Description of Exception
     */
    public void test_build_NoSelector() throws SQLException {
        // Construction
        SQLFieldList is = new SQLFieldList();
        is.addStringField("PERIOD");
        QueryHelper q = new QueryHelper("AP_PERIOD", con, is);

        // Insert pour de rire
        con.setAutoCommit(false);

        q.setInsertValue("PERIOD", "BOBO");
        FakeDriver.getDriver().pushResultSet(FakeDriver.RESULT_ONE,
            "insert into AP_PERIOD (PERIOD) values (BOBO) select @@identity");
        q.doInsert();

        q.setInsertValue("PERIOD", "BOBO2");
        FakeDriver.getDriver().pushResultSet(FakeDriver.RESULT_ONE,
            "insert into AP_PERIOD (PERIOD) values (BOBO2) select @@identity");
        q.doInsert();

        con.rollback();
    }


    /**
     * DOCUMENT ME!
     *
     * @exception SQLException Description of Exception
     */
    public void test_doSelect_BadID() throws SQLException {
        Object[][] matrix = {
                {"PERIOD"}
            };
        FakeDriver.getDriver().pushResultSet(matrix,
            "select * from AP_PERIOD where PERIOD=xxxx");
        query.setSelectorValue("PERIOD", "xxxx");
        ResultSet rs = query.doSelect();
        assertTrue("Enregistrement inconnue", rs.next() == false);
    }


    /**
     * DOCUMENT ME!
     *
     * @exception SQLException Description of Exception
     */
    public void test_Insert_Update_Delete() throws SQLException {
        String str = "Bobo's Period";

        // Insert
        query.setInsertValue("PERIOD", str);
        FakeDriver.getDriver().pushResultSet(FakeDriver.RESULT_ONE,
            "insert into AP_PERIOD (PERIOD) values (Bobo's Period) select @@identity");
        query.doInsert();
        // Update
        query.setSelectorValue("PERIOD", str);
        query.setInsertValue("PERIOD", "nouveau");
        FakeDriver.getDriver().pushUpdateConstraint("update AP_PERIOD set PERIOD=nouveau where PERIOD=Bobo's Period");
        query.doUpdate();
        // Delete
        query.setSelectorValue("PERIOD", "nouveau");
        FakeDriver.getDriver().pushUpdateConstraint("delete from AP_PERIOD where PERIOD=nouveau");
        query.doDelete();
    }


    /**
     * DOCUMENT ME!
     *
     * @exception SQLException Description of Exception
     */
    public void test_Insert_Select_Delete() throws SQLException {
        String str = "Bobo's Period";

        // Insert
        query.setInsertValue("PERIOD", str);
        FakeDriver.getDriver().pushResultSet(FakeDriver.RESULT_ONE,
            "insert into AP_PERIOD (PERIOD) values (Bobo's Period) select @@identity");
        query.doInsert();
        // Select
        query.setSelectorValue("PERIOD", str);
        Object[][] matrix = {
                {"PERIOD"},
                {str}
            };
        FakeDriver.getDriver().pushResultSet(matrix,
            "select * from AP_PERIOD where PERIOD=Bobo's Period");
        ResultSet rs = query.doSelect();
        rs.next();
        assertEquals(rs.getString("PERIOD"), str);
        // Delete
        query.setSelectorValue("PERIOD", str);
        FakeDriver.getDriver().pushUpdateConstraint("delete from AP_PERIOD where PERIOD="
            + str);
        query.doDelete();
        // Check
        query.setSelectorValue("PERIOD", str);
        FakeDriver.getDriver().pushResultSet(FakeDriver.EMPTY,
            "select * from AP_PERIOD where PERIOD=Bobo's Period");
        rs = query.doSelect();
        assertTrue("L'enregistrement est efface", rs.next() == false);
    }


    /**
     * DOCUMENT ME!
     *
     * @exception SQLException Description of Exception
     */
    public void test_Insert_RollBack() throws SQLException {
        String str = "Bobo's Period";

        con.setAutoCommit(false);

        query.setInsertValue("PERIOD", str);
        FakeDriver.getDriver().pushResultSet(FakeDriver.RESULT_ONE,
            "insert into AP_PERIOD (PERIOD) values (Bobo's Period) select @@identity");
        query.doInsert();

        con.rollback();

        // Check
        query.setSelectorValue("PERIOD", str);
        FakeDriver.getDriver().pushResultSet(FakeDriver.EMPTY,
            "select * from AP_PERIOD where PERIOD=Bobo's Period");
        ResultSet rs = query.doSelect();
        assertTrue("L'enregistrement est efface", rs.next() == false);
    }


    /**
     * The JUnit setup method
     *
     * @exception Exception Description of Exception
     */
    protected void setUp() throws Exception {
        TestEnvironnement.forceFakeDriver();
        testEnv = TestEnvironnement.newEnvironment();
        con = testEnv.getHomeConnection();

        SQLFieldList selectById = new SQLFieldList();
        selectById.addStringField("PERIOD");

        SQLFieldList insertFields = new SQLFieldList();
        insertFields.addStringField("PERIOD");

        query = new QueryHelper("AP_PERIOD", con, insertFields, selectById);
    }


    /**
     * The teardown method for JUnit
     *
     * @exception SQLException Description of Exception
     */
    protected void tearDown() throws SQLException {
        testEnv.close();
    }
}
