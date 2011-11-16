/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.utils.sql;
import net.codjo.utils.TableMap;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
/**
 * Sert à trier les colonnes.
 *
 * @author $Author: marcona $
 * @version $Revision: 1.6 $
 *
 */
public class TableRendererSorter extends TableMap implements TableCellRenderer {
    int[] indexes;
    Vector sortingColumns = new Vector();
    boolean ascending = true;
    int columnSorted = -1;
    int compares;
    JLabel Renderer;
    ImageIcon ascendingIcon;
    ImageIcon descendingIcon;
    JTable table;

    /**
     * Constructor for the TableRendererSorter object
     */
    public TableRendererSorter() {
        indexes = new int[0];
        initCustomHeaderRenderer();
    }


    /**
     * Constructor for the TableRendererSorter object
     *
     * @param theTable Description of Parameter
     */
    public TableRendererSorter(JTable theTable) {
        table = theTable;

        if (theTable instanceof GenericTable) {
            setModel(((GenericTable)theTable).getTableModel());
        }
        else {
            setModel(theTable.getModel());
        }
        initCustomHeaderRenderer();
    }

    /**
     * Sets the Model attribute of the TableRendererSorter object
     *
     * @param model The new Model value
     */
    public void setModel(TableModel model) {
        super.setModel(model);
        reallocateIndexes();
    }


    /**
     * Sets the ValueAt attribute of the TableRendererSorter object
     *
     * @param aValue The new ValueAt value
     * @param aRow The new ValueAt value
     * @param aColumn The new ValueAt value
     */
    public void setValueAt(Object aValue, int aRow, int aColumn) {
        checkModel();
        model.setValueAt(aValue, indexes[aRow], aColumn);
    }


    /**
     * Converti l'index trié en index du model origine.
     *
     * @param row l'index trie.
     *
     * @return l'index converti du model origine.
     */
    public int getConvertedIndex(int row) {
        return indexes[row];
    }


    /**
     * Converti l'index du model origine en index trié.
     *
     * @param row l'index du model.
     *
     * @return l'index trié.
     */
    public int getRealIndex(int row) {
        int realIdx = -1;
        for (int i = 0; i < indexes.length; i++) {
            if (indexes[i] == row) {
                realIdx = i;
            }
        }
        return realIdx;
    }


    // The mapping only affects the contents of the data rows.
    // Pass all requests to these rows through the mapping array: "indexes".
    /**
     * Gets the ValueAt attribute of the TableRendererSorter object
     *
     * @param aRow Description of Parameter
     * @param aColumn Description of Parameter
     *
     * @return The ValueAt value
     */
    public Object getValueAt(int aRow, int aColumn) {
        checkModel();
        return model.getValueAt(indexes[aRow], aColumn);
    }


    /**
     * Gets the TableCellRendererComponent attribute of the TableRendererSorter object
     *
     * @param Table Description of Parameter
     * @param Value Description of Parameter
     * @param Selected Description of Parameter
     * @param HasFocus Description of Parameter
     * @param Row Description of Parameter
     * @param Col Description of Parameter
     *
     * @return The TableCellRendererComponent value
     */
    public Component getTableCellRendererComponent(JTable Table, Object Value,
        boolean Selected, boolean HasFocus, int Row, int Col) {
        // Assign the column's name
        Renderer.setText(Value.toString());
        if (Table.convertColumnIndexToModel(Col) == columnSorted) {
            //Le booleen à déjà été inversé alors on inverse l'icone renvoyé / ascending
            Renderer.setIcon(ascending ? descendingIcon : ascendingIcon);
        }
        else {
            Renderer.setIcon(null);
        }
        return Renderer;
    }


    /**
     * Overview.
     * 
     * <p>
     * Description
     * </p>
     *
     * @param row1 Description of Parameter
     * @param row2 Description of Parameter
     * @param column Description of Parameter
     *
     * @return Description of the Returned Value
     */
    public int compareRowsByColumn(int row1, int row2, int column) {
        Class type = model.getColumnClass(column);
        TableModel data = model;
        Object o1;
        Object o2;

        int columnView = table.convertColumnIndexToView(column);
        TableCellRenderer tcr = table.getCellRenderer(row1, columnView);
        if ((tcr.getClass() == DefaultTableCellRenderer.class)
                || ("class javax.swing.JTable$NumberRenderer".equals(tcr.getClass().toString()))
                || ("class javax.swing.JTable$BooleanRenderer".equals(tcr.getClass().toString()))
                || ("class javax.swing.JTable$DoubleRenderer".equals(tcr.getClass().toString()))
                || ("class net.codjo.gui.renderer.NumberFormatRenderer".equals(tcr.getClass().toString()))) {
            o1 = data.getValueAt(row1, column);
            o2 = data.getValueAt(row2, column);
        }
        else {
            o1 = tcr.getTableCellRendererComponent(table, data.getValueAt(row1, column),
                    false, false, row1, columnView);
            o1 = ((JLabel)o1).getText();
            o2 = tcr.getTableCellRendererComponent(table, data.getValueAt(row2, column),
                    false, false, row2, columnView);
            o2 = ((JLabel)o2).getText();
            if ((type != java.util.Date.class)
                    && (type.getSuperclass() != java.util.Date.class)) {
                type = String.class;
            }
        }

        // If both values are null return 0
        if (o1 == null && o2 == null) {
            return 0;
        }
        else if (o1 == null) {
            // Define null less than everything.
            return -1;
        }
        else if (o2 == null) {
            return 1;
        }
        /*
        *  We copy all returned values from the getValue call in case
        *  an optimised model is reusing one object to return many values.
        *  The Number subclasses in the JDK are immutable and so will not be used in
        *  this way but other subclasses of Number might want to do this to save
        *  space and avoid unnecessary heap allocation.
        */
        if ((type.getSuperclass() == java.lang.Number.class)
                || (type == java.lang.Number.class)) {
            Number n1 = (Number)data.getValueAt(row1, column);
            double d1 = n1.doubleValue();
            Number n2 = (Number)data.getValueAt(row2, column);
            double d2 = n2.doubleValue();
            if (d1 < d2) {
                return -1;
            }
            else if (d1 > d2) {
                return 1;
            }
            else {
                return 0;
            }
        }
        else if ((type == java.util.Date.class)
                || (type.getSuperclass() == java.util.Date.class)) {
            java.util.Date d1 = (java.util.Date)data.getValueAt(row1, column);
            java.util.Date d2 = (java.util.Date)data.getValueAt(row2, column);

            if (d1 == null && d2 == null) {
                return 0;
            }
            else if (d1 == null) {
                return -1;
            }
            else if (d2 == null) {
                return 1;
            }

            long n1 = d1.getTime();
            long n2 = d2.getTime();

            if (n1 < n2) {
                return -1;
            }
            else if (n1 > n2) {
                return 1;
            }
            else {
                return 0;
            }
        }
        else if (type == String.class) {
            String s1;
            String s2;
            if (table == null) {
                s1 = (String)data.getValueAt(row1, column);
                s2 = (String)data.getValueAt(row2, column);
            }
            else {
                s1 = (String)o1;
                s2 = (String)o2;
            }
            int result = s1.compareTo(s2);

            if (result < 0) {
                return -1;
            }
            else if (result > 0) {
                return 1;
            }
            else {
                return 0;
            }
        }
        else if (type == Boolean.class) {
            Boolean bool1 = (Boolean)data.getValueAt(row1, column);
            boolean b1 = bool1.booleanValue();
            Boolean bool2 = (Boolean)data.getValueAt(row2, column);
            boolean b2 = bool2.booleanValue();

            if (b1 == b2) {
                return 0;
            }
            else if (b1) {
                // Define false < true
                return 1;
            }
            else {
                return -1;
            }
        }
        else {
            Object v1 = data.getValueAt(row1, column);
            String s1 = v1.toString();
            Object v2 = data.getValueAt(row2, column);
            String s2 = v2.toString();
            int result = s1.compareTo(s2);

            if (result < 0) {
                return -1;
            }
            else if (result > 0) {
                return 1;
            }
            else {
                return 0;
            }
        }
    }


    /**
     * DOCUMENT ME!
     *
     * @param row1 Description of Parameter
     * @param row2 Description of Parameter
     *
     * @return Description of the Returned Value
     */
    public int compare(int row1, int row2) {
        compares++;
        for (int level = 0; level < sortingColumns.size(); level++) {
            Integer column = (Integer)sortingColumns.elementAt(level);
            int result = compareRowsByColumn(row1, row2, column.intValue());
            if (result != 0) {
                return ascending ? result : -result;
            }
        }
        return 0;
    }


    /**
     * Overview.
     * 
     * <p>
     * Description
     * </p>
     */
    public void reallocateIndexes() {
        int rowCount = model.getRowCount();
        columnSorted = -1;

        // Set up a new array of indexes with the right number of elements
        // for the new data model.
        indexes = new int[rowCount];

        // Initialise with the identity mapping.
        for (int row = 0; row < rowCount; row++) {
            indexes[row] = row;
        }
        checkModel();
    }


    /**
     * Overview.
     * 
     * <p>
     * Description
     * </p>
     *
     * @param evt Description of Parameter
     */
    public void tableChanged(TableModelEvent evt) {
        columnSorted = -1;
        table.getTableHeader().repaint();

        if (model.getRowCount() != indexes.length) {
            reallocateIndexes();
        }

        super.tableChanged(evt);
    }


    /**
     * Overview.
     * 
     * <p>
     * Description
     * </p>
     */
    public void checkModel() {
        if (indexes.length != model.getRowCount()) {
            System.err.println("Sorter not informed of a change in model. : "
                + indexes.length + " != " + model.getRowCount());
        }
    }


    /**
     * Overview.
     * 
     * <p>
     * Description
     * </p>
     *
     * @param sender Description of Parameter
     */
    public void sort(Object sender) {
        checkModel();

        compares = 0;
        // n2sort();
        // qsort(0, indexes.length-1);
        shuttlesort((int[])indexes.clone(), indexes, 0, indexes.length);
    }


    /**
     * Overview.
     * 
     * <p>
     * Description
     * </p>
     */
    public void n2sort() {
        for (int i = 0; i < getRowCount(); i++) {
            for (int j = i + 1; j < getRowCount(); j++) {
                if (compare(indexes[i], indexes[j]) == -1) {
                    swap(i, j);
                }
            }
        }
    }


    // This is a home-grown implementation which we have not had time
    // to research - it may perform poorly in some circumstances. It
    // requires twice the space of an in-place algorithm and makes
    // NlogN assigments shuttling the values between the two
    // arrays. The number of compares appears to vary between N-1 and
    // NlogN depending on the initial order but the main reason for
    // using it here is that, unlike qsort, it is stable.
    /**
     * Overview.
     * 
     * <p>
     * Description
     * </p>
     *
     * @param from Description of Parameter
     * @param to Description of Parameter
     * @param low Description of Parameter
     * @param high Description of Parameter
     */
    public void shuttlesort(int[] from, int[] to, int low, int high) {
        if (high - low < 2) {
            return;
        }
        int middle = (low + high) / 2;
        shuttlesort(to, from, low, middle);
        shuttlesort(to, from, middle, high);

        int p = low;
        int q = middle;

        if (high - low >= 4 && compare(from[middle - 1], from[middle]) <= 0) {
            for (int i = low; i < high; i++) {
                to[i] = from[i];
            }
            return;
        }

        // A normal merge.
        for (int i = low; i < high; i++) {
            if (q >= high || (p < middle && compare(from[p], from[q]) <= 0)) {
                to[i] = from[p++];
            }
            else {
                to[i] = from[q++];
            }
        }
    }


    /**
     * Overview.
     * 
     * <p>
     * Description
     * </p>
     *
     * @param i Description of Parameter
     * @param j Description of Parameter
     */
    public void swap(int i, int j) {
        int tmp = indexes[i];
        indexes[i] = indexes[j];
        indexes[j] = tmp;
    }


    /**
     * Overview.
     * 
     * <p>
     * Description
     * </p>
     *
     * @param column Description of Parameter
     */
    public void sortByColumn(int column) {
        sortByColumn(column, ascending);
    }


    /**
     * Overview.
     * 
     * <p>
     * Description
     * </p>
     *
     * @param column Description of Parameter
     * @param ascending Description of Parameter
     */
    public void sortByColumn(int column, boolean ascending) {
        columnSorted = column;
        sortingColumns.removeAllElements();
        sortingColumns.addElement(new Integer(column));
        sort(this);
        super.tableChanged(new TableModelEvent(this));
        this.ascending = !ascending;
    }


    // There is no-where else to put this.
    // Add a mouse listener to the Table to trigger a table sort
    // when a column heading is clicked in the JTable.
    /**
     * Adds a feature to the MouseListenerToHeaderInTable attribute of the
     * TableRendererSorter object
     *
     * @param table The feature to be added to the MouseListenerToHeaderInTable attribute
     */
    public void addMouseListenerToHeaderInTable(JTable table) {
        final TableRendererSorter sorter = this;
        final JTable tableView = table;
        tableView.setColumnSelectionAllowed(false);
        MouseAdapter listMouseListener =
            new MouseAdapter() {
                /**
                 * Overview.
                 * 
                 * <p>
                 * Description
                 * </p>
                 *
                 * @param e Description of Parameter
                 */
                public void mousePressed(MouseEvent e) {
                    TableColumnModel columnModel = tableView.getColumnModel();
                    int viewColumn = columnModel.getColumnIndexAtX(e.getX());
                    int column = tableView.convertColumnIndexToModel(viewColumn);
                    if (e.getClickCount() > 1 && column != -1) {
                        sorter.sortByColumn(column);
                    }
                }
            };

        // Add the mouse listener
        JTableHeader th = tableView.getTableHeader();
        th.addMouseListener(listMouseListener);
    }


    /**
     * Overview.
     * 
     * <p>
     * Description
     * </p>
     *
     * @param table Description of Parameter
     */
    public void changeHeaderRenderer(JTable table) {
        columnSorted = -1;
        TableColumn aCol;
        int nbColumn = getColumnCount();
        for (int i = 0; i < nbColumn; i++) {
            aCol = table.getColumn(getColumnName(i));
            aCol.setHeaderRenderer(this);
        }
    }


    /**
     * Overview.
     * 
     * <p>
     * Description
     * </p>
     */
    private final void initCustomHeaderRenderer() {
        // -----------------------------------------------------
        // 1. Load the images that represent the widget
        java.net.URL url;

        // 1.1 Load AscendingIcon
        url = getClass().getResource("Ascending.gif");
        if (url != null) {
            ascendingIcon = new ImageIcon(Toolkit.getDefaultToolkit().createImage(url));
        }
        else {
            ascendingIcon = null;
        }

        // 1.2 Load DescendingIcon
        url = getClass().getResource("Descending.gif");
        if (url != null) {
            descendingIcon = new ImageIcon(Toolkit.getDefaultToolkit().createImage(url));
        }
        else {
            descendingIcon = null;
        }

        // -----------------------------------------------------
        // 2. Set up the Renderer
        Renderer = new JLabel();
        Renderer.setOpaque(true);
        Renderer.setHorizontalAlignment(SwingConstants.CENTER);
        Renderer.setHorizontalTextPosition(SwingConstants.RIGHT);
        Renderer.setVerticalTextPosition(SwingConstants.CENTER);
        Renderer.setBorder(new javax.swing.border.EtchedBorder(
                javax.swing.border.EtchedBorder.LOWERED));
    }
}
