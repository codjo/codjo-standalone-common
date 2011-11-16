/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.utils;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
/**
 * Classe Test pour <code>TableFilter</code> .
 *
 * @author $Author: rivierv $
 * @version $Revision: 1.4 $
 */
public class TableFilterTest extends TestCase {
    TableFilter filterModel;
    BasicModel model;

    /**
     * Constructor for the TableFilterTest object
     *
     * @param name Description of Parameter
     */
    public TableFilterTest(String name) {
        super(name);
    }

    /**
     * A unit test for JUnit
     */
    public void testNoFilter() {
        assertEquals("Ligne", filterModel.getRowCount(), model.getRowCount());
        assertEquals("Colonne", filterModel.getColumnCount(), model.getColumnCount());
        assertEquals("value", filterModel.getValueAt(0, 0), model.getValueAt(0, 0));
        assertEquals("name", filterModel.getColumnName(0), "COL_0");
    }


    /**
     * A unit test for JUnit
     */
    public void testPropertyChangeEvent() {
        BasicListener listener = new BasicListener();
        filterModel.addPropertyChangeListener(1, listener);
        assertNull(listener.event);

        List list = new ArrayList();
        list.add(new Integer(5));

        filterModel.setFilter(1, new Integer(5));
        assertEquals("set", listener.event.getPropertyName(), "1");
        Object[] newVal = (Object[])listener.event.getNewValue();
        assertEquals("set", java.util.Arrays.asList(newVal), list);
        assertEquals("set", listener.event.getOldValue(), null);

        filterModel.clearFilter(1);
        assertEquals("clear", listener.event.getPropertyName(), "1");
        assertEquals("clear", listener.event.getNewValue(), null);
        Object[] oldVal = (Object[])listener.event.getOldValue();
        assertEquals("clear", java.util.Arrays.asList(oldVal), list);
    }


    /**
     * A unit test for JUnit
     */
    public void test_clearFilter() {
        filterModel.setFilter(0, new Integer(1));
        assertEquals("Apres Filtre", filterModel.getRowCount(), 5);
        filterModel.clearFilter(0);
        assertEquals("Apres clearFilter", filterModel.getRowCount(), model.getRowCount());
    }


    /**
     * A unit test for JUnit
     */
    public void test_setFilter() {
        filterModel.setFilter(0, new Integer(1));
        assertEquals("Ligne", filterModel.getRowCount(), 5);
        assertEquals("Colonne", filterModel.getColumnCount(), 10);
        assertEquals("value  ", filterModel.getValueAt(0, 1), new Integer(5));
    }


    /**
     * A unit test for JUnit
     */
    public void test_addFilter() {
        filterModel.addFilter(0, new Integer(1));
        assertEquals("A Ligne", filterModel.getRowCount(), 5);
        assertEquals("A Colonne", filterModel.getColumnCount(), 10);
        assertEquals("A value  ", filterModel.getValueAt(0, 1), new Integer(5));

        filterModel.addFilter(0, new Integer(0));
        assertEquals("B Ligne", filterModel.getRowCount(), 10);
        assertEquals("B Colonne", filterModel.getColumnCount(), 10);
        assertEquals("B value", filterModel.getValueAt(0, 0), model.getValueAt(0, 0));
        assertEquals("B name", filterModel.getColumnName(0), "COL_0");
    }


    /**
     * A unit test for JUnit
     */
    public void test_removeFilter() {
        filterModel.addFilter(0, new Integer(0));
        filterModel.addFilter(0, new Integer(1));
        filterModel.addFilter(1, new Integer(1));
        assertEquals("A Ligne", filterModel.getRowCount(), 1);

        filterModel.removeFilter(0, new Integer(1));
        assertEquals("B Ligne", filterModel.getRowCount(), 1);

        filterModel.removeFilter(1, new Integer(1));
        assertEquals("C Ligne", filterModel.getRowCount(), 5);

        filterModel.removeFilter(0, new Integer(0));
        assertEquals("D Ligne", filterModel.getRowCount(), 10);
    }


    public void test_removeFilter_twice() {
        filterModel.addFilter(0, new Integer(0));
        assertEquals(filterModel.containsFilterValue(0, new Integer(0)), true);
        filterModel.removeFilter(0, new Integer(0));
        assertEquals(filterModel.containsFilterValue(0, new Integer(0)), false);

        filterModel.removeFilter(0, new Integer(0));
        assertEquals(filterModel.containsFilterValue(0, new Integer(0)), false);
    }


    public void test_removeAllColumnFilter() {
        filterModel.addFilter(0, new Integer(0));
        filterModel.addFilter(1, new Integer(0));
        filterModel.addFilter(1, new Integer(1));
        assertEquals("A Ligne", filterModel.getRowCount(), 2);

        filterModel.clearAllColumnFilter();
        assertEquals("B Ligne", filterModel.getRowCount(), 10);
    }


    public void test_containsFilterValue() {
        assertEquals(filterModel.containsFilterValue(0, new Integer(0)), false);
        filterModel.addFilter(0, new Integer(0));
        assertEquals(filterModel.containsFilterValue(0, new Integer(0)), true);
        filterModel.addFilter(0, new Integer(1));
        assertEquals(filterModel.containsFilterValue(0, new Integer(1)), true);

        filterModel.removeFilter(0, new Integer(0));
        assertEquals(filterModel.containsFilterValue(0, new Integer(1)), true);
        assertEquals(filterModel.containsFilterValue(0, new Integer(0)), false);
    }


    /**
     * A unit test for JUnit
     */
    public void testPropertyChangeEvent2() {
        BasicListener listener = new BasicListener();
        filterModel.addPropertyChangeListener(1, listener);
        assertNull(listener.event);

        List list = new ArrayList();
        list.add(new Integer(5));

        filterModel.addFilter(1, new Integer(5));
        assertEquals("add", listener.event.getPropertyName(), "1");
        Object[] newVal = (Object[])listener.event.getNewValue();
        assertEquals("add", java.util.Arrays.asList(newVal), list);
        assertEquals("add", listener.event.getOldValue(), null);

        filterModel.removeFilter(1, new Integer(5));
        assertEquals("remove", listener.event.getPropertyName(), "1");
        assertEquals("remove", listener.event.getNewValue(), null);
        Object[] oldVal = (Object[])listener.event.getOldValue();
        assertEquals("remove", java.util.Arrays.asList(oldVal), list);
    }


    /**
     * A unit test for JUnit
     */
    public void test_getFilterValueList() {
        filterModel.addFilter(0, new Integer(1));
        filterModel.addFilter(0, new Integer(0));

        assertEquals(filterModel.getFilterValueList(0).length, 2);
        java.util.List filters =
            java.util.Arrays.asList(filterModel.getFilterValueList(0));
        assertTrue(filters.contains(new Integer(1)));
        assertTrue(filters.contains(new Integer(0)));
    }


    /**
     * A unit test for JUnit
     */
    public void test_add_set_clear() {
        filterModel.addFilter(0, new Integer(1));
        filterModel.addFilter(0, new Integer(0));
        assertEquals(filterModel.getFilterValueList(0).length, 2);
        filterModel.setFilter(0, new Integer(5));
        assertEquals(filterModel.getFilterValueList(0).length, 1);
        assertEquals(filterModel.getFilterValue(0), new Integer(5));
        filterModel.clearFilter(0);
        assertEquals(filterModel.getFilterValueList(0), null);
    }


    /**
     * A unit test for JUnit
     */
    public void test_getValueAt_Error() {
        filterModel.setFilter(0, new Integer(1));
        assertEquals(filterModel.getValueAt(8, 0), null);
    }


    /**
     * A unit test for JUnit
     */
    public void test_setFilter_Combined() {
        filterModel.setFilter(0, new Integer(1));
        filterModel.setFilter(1, new Integer(5));
        assertEquals("Ligne", filterModel.getRowCount(), 1);
        assertEquals("Colonne", filterModel.getColumnCount(), 10);
        assertEquals("value  ", filterModel.getValueAt(0, 1), new Integer(5));

        model.simulateDataChange();
        assertEquals("Ligne", filterModel.getRowCount(), 1);
    }


    /**
     * A unit test for JUnit
     */
    public void test_getFilterValue() {
        filterModel.setFilter(0, new Integer(1));
        assertEquals(filterModel.getFilterValue(0), new Integer(1));
    }


    /**
     * The JUnit setup method
     *
     * @exception java.lang.Exception Description of Exception
     */
    protected void setUp() throws java.lang.Exception {
        model = new BasicModel();
        filterModel = new TableFilter(model);
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
        return new TestSuite(TableFilterTest.class);
    }

    /**
     * Listener basic.
     *
     * @author $Author: rivierv $
     * @version $Revision: 1.4 $
     */
    static class BasicListener implements PropertyChangeListener {
        /**
                                                                                        		 */
        public PropertyChangeEvent event = null;

        /**
         * DOCUMENT ME!
         *
         * @param evt
         */
        public void propertyChange(PropertyChangeEvent evt) {
            event = evt;
        }
    }


    /**
     * Model de table basique pour les test de TableFilter.
     *
     * @author $Author: rivierv $
     * @version $Revision: 1.4 $
     */
    static class BasicModel extends AbstractTableModel {
        private int value = 0;

        /**
         * DOCUMENT ME!
         *
         * @return The ColumnCount value
         */
        public int getColumnCount() {
            return 10;
        }


        /**
         * DOCUMENT ME!
         *
         * @return The RowCount value
         */
        public int getRowCount() {
            return 10;
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
            if (col == 0) {
                return new Integer(row / 5 + value);
            }
            else {
                return new Integer(row * col + value);
            }
        }


        /**
         * Simule un changement dans le tableau
         */
        public void simulateDataChange() {
            fireTableDataChanged();
        }


        /**
         * Simule un changement dans le tableau avec modification du contenu.
         *
         * @param value Description of Parameter
         */
        public void simulateDataChange(int value) {
            this.value = value;
            fireTableDataChanged();
        }
    }
}
