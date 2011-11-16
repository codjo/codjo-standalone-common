/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.operation;
import junit.framework.TestCase;
/**
 *
 */
public class SqlWhereClauseUtilTest extends TestCase {
    private SqlWhereClauseUtil sqlWhereclause;


    @Override
    protected void setUp() throws Exception {
        sqlWhereclause = new SqlWhereClauseUtil();
    }


    public void test_getWhereClauseFields() throws Exception {
        String[] whereClauseFields =
              sqlWhereclause.getWhereClauseFields(
                    "and (BO_TRANSACTION.PERIOD=$CURRENT_PERIOD$ or BO_TRANSACTION.PERIOD=$PREVIOUS_PERIOD$) and INVENTORY_TYPE='AD0'");
        assertEquals(10, whereClauseFields.length);
        assertEquals("PERIOD", whereClauseFields[0]);
        assertEquals("INVENTORY_TYPE", whereClauseFields[1]);
        assertEquals("", whereClauseFields[2]);
        assertEquals("", whereClauseFields[3]);
        assertEquals("", whereClauseFields[4]);
        assertEquals("", whereClauseFields[5]);
        assertEquals("", whereClauseFields[6]);
        assertEquals("", whereClauseFields[7]);
        assertEquals("", whereClauseFields[8]);
        assertEquals("", whereClauseFields[9]);
    }


    public void getWhereClauseOperandes() throws Exception {
        String[] whereClauseOperandes =
              sqlWhereclause.getWhereClauseOperandes(
                    "and (BO_TRANSACTION.PERIOD=$CURRENT_PERIOD$ or BO_TRANSACTION.PERIOD=$PREVIOUS_PERIOD$) and INVENTORY_TYPE='AD0'");
        assertEquals(10, whereClauseOperandes.length);
        assertEquals("(BO_TRANSACTION.PERIOD=$CURRENT_PERIOD$ or BO_TRANSACTION.PERIOD=$PREVIOUS_PERIOD$)",
                     whereClauseOperandes[0]);
        assertEquals("INVENTORY_TYPE='AD0'", whereClauseOperandes[1]);
        assertEquals("", whereClauseOperandes[2]);
        assertEquals("", whereClauseOperandes[3]);
        assertEquals("", whereClauseOperandes[4]);
        assertEquals("", whereClauseOperandes[5]);
        assertEquals("", whereClauseOperandes[6]);
        assertEquals("", whereClauseOperandes[7]);
        assertEquals("", whereClauseOperandes[8]);
        assertEquals("", whereClauseOperandes[9]);
    }


    public void test_getFieldofElement() throws Exception {
        assertEquals("INVENTORY_TYPE",
                     sqlWhereclause.getFieldOfElement("INVENTORY_TYPE='AD0'"));
        assertEquals("INVENTORY_TYPE",
                     sqlWhereclause.getFieldOfElement("MATABLE.INVENTORY_TYPE='AD0'"));
        assertEquals("INVENTORY_TYPE", sqlWhereclause.getFieldOfElement("INVENTORY_TYPE"));

        assertEquals("QUANTITY_TYPE", sqlWhereclause.getFieldOfElement(
              sqlWhereclause.getLeftElementOfOperande(
                    "(QUANTITY_TYPE='P' OR QUANTITY_TYPE='M' OR SECURITY_CLASSIFICATION='TRES' OR SECURITY_CLASSIFICATION='PRET')")));

        assertEquals("QUANTITY_TYPE", sqlWhereclause.getFieldOfElement(
              sqlWhereclause.getLeftElementOfOperande("QUANTITY_TYPE<>'P' ")));

        assertEquals("INVENTORY_TYPE",
                     sqlWhereclause.getFieldOfElement("INVENTORY_TYPE<>'AD0'"));
    }


    public void test_dealWithSourceSystem() throws Exception {
        sqlWhereclause.init();
        sqlWhereclause.dealWithPortfolioGroup("PTF_GROUP_NAME", "MA_TABLE");
        sqlWhereclause.dealWithSourceSystem("SRC_SYSTEM", "MA_TABLE");
        assertEquals("BO_PORTFOLIO.PORTFOLIO_GROUP ='PTF_GROUP_NAME'",
                     sqlWhereclause.getSelectTerm());
        assertEquals(
              "MA_TABLE.PORTFOLIO_CODE = BO_PORTFOLIO.PORTFOLIO_CODE and MA_TABLE.SOURCE_SYSTEM = 'SRC_SYSTEM'",
              sqlWhereclause.getUpdateTerm());
        sqlWhereclause.fill("AAAAMM", "coucou",null);
        assertEquals("BO_PORTFOLIO.PORTFOLIO_GROUP ='PTF_GROUP_NAME'",
                     sqlWhereclause.getSelectTerm());
        assertEquals(
              "MA_TABLE.PORTFOLIO_CODE = BO_PORTFOLIO.PORTFOLIO_CODE and MA_TABLE.SOURCE_SYSTEM = 'SRC_SYSTEM'",
              sqlWhereclause.getUpdateTerm());
    }


    public void test_dealWithPeriod() throws Exception {
        assertEquals("and (BO_TRANSACTION.PERIOD='PERIOD1' or BO_TRANSACTION.PERIOD='PERIOD1-1')",
                     sqlWhereclause.fillWhereClauseWithPeriod(
                           "and (BO_TRANSACTION.PERIOD=$CURRENT_PERIOD$ or BO_TRANSACTION.PERIOD=$PREVIOUS_PERIOD$)",
                           "PERIOD1",
                           "PERIOD1-1",null));
    }
}
