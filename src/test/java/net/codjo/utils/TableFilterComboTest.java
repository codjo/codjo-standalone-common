/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.utils;
import static net.codjo.utils.TableFilterCombo.NO_FILTER;
import javax.swing.table.DefaultTableModel;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
/**
 * Classe Test pour <code>TableFilter</code> .
 *
 * @author $Author: spinae $
 * @version $Revision: 1.2 $
 */
public class TableFilterComboTest extends TestCase {
    TableFilterCombo filterCombo;
    TableFilter filterModel;
    TableFilterTest.BasicModel model;

    public TableFilterComboTest(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(TableFilterComboTest.class);
    }


    /**
     * Test le cas ou la table contient des Booleans.
     */
    public void testBooleanValue() {
        Object[] colNames = {"col_name"};
        Object[][] data = {
                {Boolean.FALSE},
                {Boolean.TRUE}
            };
        DefaultTableModel booleanModel = new DefaultTableModel(data, colNames);
        filterModel = new TableFilter(booleanModel);

        filterCombo.setTableFilter(filterModel, 0);
        assertEquals(3, filterCombo.getModel().getSize());
        assertEquals(NO_FILTER, filterCombo.getModel().getElementAt(0));
        assertEquals(Boolean.FALSE, filterCombo.getModel().getElementAt(1));
        assertEquals(Boolean.TRUE, filterCombo.getModel().getElementAt(2));
    }


    public void testFilterAlreadyExist() {
        filterModel.setFilter(0, 1);
        filterCombo.setTableFilter(filterModel, 0);
        assertEquals(filterCombo.getSelectedItem(), filterModel.getFilterValue(0));
    }


    public void testFilterSetProgramaticaly() {
        filterCombo.setTableFilter(filterModel, 0);
        assertNull("NoFilter", filterModel.getFilterValue(0));
        assertEquals(filterCombo.getSelectedItem(), NO_FILTER);

        filterModel.setFilter(0, 1);
        assertEquals("setFilter", filterCombo.getSelectedItem(), 1);
    }


    public void testNoDuplicateItem() {
        filterCombo.setTableFilter(filterModel, 0);
        assertEquals(filterCombo.getModel().getSize(), 3);
        assertEquals(filterCombo.getModel().getElementAt(0), NO_FILTER);
        assertEquals(0, filterCombo.getModel().getElementAt(1));
        assertEquals(filterCombo.getModel().getElementAt(2), 1);
    }


    public void testSortOrder() {
        filterCombo.setComparator(new InvertComparator());
        filterCombo.setTableFilter(filterModel, 0);
        assertEquals(filterCombo.getModel().getSize(), 3);
        assertEquals(filterCombo.getModel().getElementAt(0), TableFilterCombo.NO_FILTER);
        assertEquals(filterCombo.getModel().getElementAt(1), 1);
        assertEquals(filterCombo.getModel().getElementAt(2), 0);
    }


    public void testSortOrderLateUpdate() {
        filterCombo.setTableFilter(filterModel, 0);
        filterCombo.setComparator(new InvertComparator());
        assertEquals(filterCombo.getModel().getSize(), 3);
        assertEquals(filterCombo.getModel().getElementAt(0), TableFilterCombo.NO_FILTER);
        assertEquals(filterCombo.getModel().getElementAt(1), 1);
        assertEquals(0, filterCombo.getModel().getElementAt(2));
    }


    public void testSortOrderNullValue() {
        // Creation du modele
        Object[] colNames = {"col_name"};
        Object[][] data = {
                {1},
                {2},
                {null}
            };
        DefaultTableModel nullModel = new DefaultTableModel(data, colNames);
        filterModel = new TableFilter(nullModel);

        // Init du Combo
        filterCombo.setComparator(new InvertComparator());
        filterCombo.setTableFilter(filterModel, 0);

        // Verification du resultat
        assertEquals(4, filterCombo.getModel().getSize());
        assertEquals(TableFilterCombo.NO_FILTER, filterCombo.getModel().getElementAt(0));
        assertEquals(filterCombo.getModel().getElementAt(1), TableFilterCombo.NULL_FILTER);
        assertEquals(filterCombo.getModel().getElementAt(2), 2);
        assertEquals(filterCombo.getModel().getElementAt(3), 1);
    }


    public void testNoDuplicateItemModelUpdate() {
        filterCombo.setTableFilter(filterModel, 0);
        model.simulateDataChange(5);
        assertEquals(filterCombo.getModel().getSize(), 3);
        assertEquals(filterCombo.getModel().getElementAt(0), TableFilterCombo.NO_FILTER);
        assertEquals(filterCombo.getModel().getElementAt(1), 5);
        assertEquals(filterCombo.getModel().getElementAt(2), 6);
    }


    /**
     * Test le cas ou le model contient des valeurs nulle.
     */
    public void testNullValue() {
        Object[] colNames = {"col_name"};
        Object[][] data = {
                {"valA"},
                {null}
            };
        DefaultTableModel nullModel = new DefaultTableModel(data, colNames);
        filterModel = new TableFilter(nullModel);

        filterCombo.setTableFilter(filterModel, 0);
        assertEquals(3, filterCombo.getModel().getSize());
        assertEquals(TableFilterCombo.NO_FILTER, filterCombo.getModel().getElementAt(0));
        assertEquals(TableFilterCombo.NULL_FILTER, filterCombo.getModel().getElementAt(1));
        assertEquals("valA", filterCombo.getModel().getElementAt(2));
    }


    public void testScenario() {
        filterCombo.setTableFilter(filterModel, 0);
        assertEquals(filterCombo.getModel().getSize(), 3);
        filterModel.setFilter(0, 1);
        assertEquals(filterCombo.getModel().getSize(), 3);
    }


    public void testUserSelectFilter() {
        filterCombo.setTableFilter(filterModel, 0);
        assertEquals("Par defaut NO_FILTER", filterCombo.getSelectedItem(),
            TableFilterCombo.NO_FILTER);
        assertEquals("NoFilter", filterModel.getFilterValue(0), null);

        filterCombo.setSelectedItem(0);
        assertEquals("Combo setFilter", filterModel.getFilterValue(0), 0);
    }


    @Override
    protected void setUp() throws java.lang.Exception {
        model = new TableFilterTest.BasicModel();
        filterModel = new TableFilter(model);
        filterCombo = new TableFilterCombo();
    }

    private static class InvertComparator implements java.util.Comparator {
        public int compare(Object o1, Object o2) {
            return -1 * ((Integer)o1).compareTo((Integer)o2);
        }
    }
}
