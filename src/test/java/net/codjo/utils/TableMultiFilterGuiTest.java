/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.utils;
import javax.swing.table.AbstractTableModel;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
/**
 * Classe Test pour <code>TableMultiFilterGui</code> .
 *
 * @author $Author: gonnot $
 * @version $Revision: 1.1.1.1 $
 */
public class TableMultiFilterGuiTest extends TestCase {
    TableFilter filterModel;
    TableFilterTest.BasicModel model;
    TableMultiFilterGui multiFilterGui;

    /**
     * Constructor for the TableMultiFilterGuiTest object
     *
     * @param name Description of Parameter
     */
    public TableMultiFilterGuiTest(String name) {
        super(name);
    }

    /**
     * A unit test for JUnit
     */
    public void test_getModel_NoDuplicateItem() {
        multiFilterGui.setTableFilter(filterModel, 0);
        assertEquals(multiFilterGui.getRowCount(), 2);
        assertEquals(multiFilterGui.getValueAt(0, 0), Boolean.FALSE);
        assertEquals(multiFilterGui.getValueAt(0, 1), new Integer(0));
        assertEquals(multiFilterGui.getValueAt(1, 0), Boolean.FALSE);
        assertEquals(multiFilterGui.getValueAt(1, 1), new Integer(1));
    }


    /**
     * A unit test for JUnit
     */
    public void test_getModel_NoDuplicateItem_DuringModelUpdate() {
        multiFilterGui.setTableFilter(filterModel, 0);
        model.simulateDataChange(5);
        assertEquals(multiFilterGui.getRowCount(), 2);
        assertEquals(multiFilterGui.getValueAt(0, 1), new Integer(5));
        assertEquals(multiFilterGui.getValueAt(1, 1), new Integer(6));
    }


    /**
     * A unit test for JUnit
     */
    public void test_getModel_NoDuplicateItem_NullValue() {
        ModelWithNullValue modelNV = new ModelWithNullValue();
        filterModel = new TableFilter(modelNV);
        multiFilterGui.setTableFilter(filterModel, 0);

        assertEquals(multiFilterGui.getRowCount(), 2);
        assertEquals(multiFilterGui.getValueAt(0, 1), null);
        assertEquals(multiFilterGui.getValueAt(1, 1), new Integer(0));
    }


    /**
     * A unit test for JUnit
     */
    public void test_setFilter() {
        multiFilterGui.setTableFilter(filterModel, 0);
        assertEquals(multiFilterGui.getRowCount(), 2);
        filterModel.setFilter(0, new Integer(1));
        assertEquals(multiFilterGui.getRowCount(), 2);
    }


    /**
     * A unit test for JUnit
     */
    public void test_setFilter_User() {
        multiFilterGui.setTableFilter(filterModel, 0);
        assertEquals("Par defaut 0 n'est pas dans le filtre",
            multiFilterGui.getValueAt(0, 0), Boolean.FALSE);
        assertEquals("NoFilter", filterModel.getFilterValue(0), null);

        multiFilterGui.setValueAt(Boolean.TRUE, 0, 0);
        assertEquals("0 est dans le filtre", multiFilterGui.getValueAt(0, 0), Boolean.TRUE);
        assertEquals("etat du Filtre", filterModel.getFilterValue(0), new Integer(0));
    }


    /**
     * A unit test for JUnit
     */
    public void test_setFilter_Programaticaly() {
        multiFilterGui.setTableFilter(filterModel, 0);
        assertNull("NoFilter", filterModel.getFilterValue(0));
        assertEquals("No_filter 0 ", multiFilterGui.getValueAt(0, 0), Boolean.FALSE);
        assertEquals("No_filter 1 ", multiFilterGui.getValueAt(1, 0), Boolean.FALSE);

        filterModel.setFilter(0, new Integer(1));
        assertEquals("No_filter 0 ", multiFilterGui.getValueAt(0, 0), Boolean.FALSE);
        assertEquals("Filter 1 ", multiFilterGui.getValueAt(1, 0), Boolean.TRUE);
    }


    /**
     * The JUnit setup method
     *
     * @exception java.lang.Exception Description of Exception
     */
    protected void setUp() throws java.lang.Exception {
        model = new TableFilterTest.BasicModel();
        filterModel = new TableFilter(model);
        multiFilterGui = new TableMultiFilterGui();
    }


    /**
     * The teardown method for JUnit
     *
     * @exception java.lang.Exception Description of Exception
     */
    protected void tearDown() throws java.lang.Exception {}


    /**
     * A unit test suite for JUnit
     *
     * @return The test suite
     */
    public static Test suite() {
        return new TestSuite(TableMultiFilterGuiTest.class);
    }

    /**
     * Overview.
     * 
     * <p>
     * Description
     * </p>
     *
     * @author $Author: gonnot $
     * @version $Revision: 1.1.1.1 $
     */
    static class ModelWithNullValue extends AbstractTableModel {
        /**
         * DOCUMENT ME!
         *
         * @return The ColumnCount value
         */
        public int getColumnCount() {
            return 1;
        }


        /**
         * DOCUMENT ME!
         *
         * @return The RowCount value
         */
        public int getRowCount() {
            return 3;
        }


        /**
         * Gets the ColumnName attribute of the BasicModel object
         *
         * @param aColumn Description of Parameter
         *
         * @return The ColumnName value
         */
        public String getColumnName(int aColumn) {
            return "COL_" + aColumn;
        }


        /**
         * DOCUMENT ME!
         *
         * @param row Description of Parameter
         * @param col Description of Parameter
         *
         * @return The ValueAt value
         */
        public Object getValueAt(int row, int col) {
            switch (row) {
                case 0:
                    return new Integer(0);
                case 1:
                    return null;
                case 2:
                    return new Integer(0);
            }
            return null;
        }
    }
}
