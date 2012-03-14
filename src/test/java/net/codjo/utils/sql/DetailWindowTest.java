/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.utils.sql;
import net.codjo.utils.SQLFieldList;
import fakedb.FakeResultSet;
import java.sql.ResultSet;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JTextField;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
/**
 * Test la classe AbstractDetailWindow
 *
 * @author $Author: rivierv $
 * @version $Revision: 1.4 $
 */
public class DetailWindowTest extends TestCase {
    DetailWindow_ForTest detailInterface;

    public void test_getApplyButton() throws Exception {
        assertNull(detailInterface.getApplyButton());
    }


    public void test_getPreviousButton() throws Exception {
        assertNull(detailInterface.getPreviousButton());
    }


    /**
     * DOCUMENT ME!
     *
     * @exception Exception Description of Exception
     */
    public void test_getNextButton() throws Exception {
        assertNull(detailInterface.getNextButton());
    }


    /**
     * DOCUMENT ME!
     *
     * @exception Exception Description of Exception
     */
    public void test_getListOfComponents() throws Exception {
        List list = detailInterface.getListOfComponents();

        assertTrue("FAMILY_ID", list.contains("FAMILY_ID"));
        assertTrue("FAMILY_NAME", list.contains("FAMILY_NAME"));
    }


    /**
     * A unit test for JUnit
     *
     * @exception Exception Description of Exception
     */
    public void test_fillComponent() throws Exception {
        SQLFieldList columns = new SQLFieldList();
        columns.addIntegerField("FAMILY_ID");
        columns.addStringField("FAMILY_NAME");

        Object[][] matrix = {
              {"FAMILY_ID", "FAMILY_NAME"},
                {7, "Devise"}
            };
        ResultSet rs = new FakeResultSet(matrix).getStub();
        detailInterface.fillComponent(columns, rs);

        assertEquals("FAMILY_ID", detailInterface.FAMILY_ID.getText(), "7");
        assertEquals("FAMILY_NAME", detailInterface.FAMILY_NAME.getText(), "Devise");
    }


    @Override
    protected void setUp() throws Exception {
        detailInterface = new DetailWindow_ForTest();
    }


    /**
     * A unit test suite for JUnit
     *
     * @return The test suite
     */
    public static Test suite() {
        return new TestSuite(DetailWindowTest.class);
    }

    /**
     * Fenetre de detail pour les tests.
     *
     * @author $Author: rivierv $
     * @version $Revision: 1.4 $
     */
    public static class DetailWindow_ForTest extends AbstractDetailWindow {
        /**
                                                                                                         */
        public JTextField FAMILY_ID = new JTextField();
        /**
                                                                                                         */
        public JTextField FAMILY_NAME = new JTextField();
        /**
                                                                                                         */
        public JButton okButton = new JButton();
        /**
                                                                                                         */
        public JButton cancelButton = new JButton();

        /**
         * Constructor for the DetailWindow_ForTest object
         */
        public DetailWindow_ForTest() {}
    }
}
