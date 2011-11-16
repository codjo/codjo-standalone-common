/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.gui.model;
import net.codjo.gui.renderer.FieldNameRenderer;
import net.codjo.model.Table;
import net.codjo.utils.ConnectionManager;
import net.codjo.utils.sql.AbstractDetailWindow;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
/**
 * Overview.
 * 
 * <p>
 * Description
 * </p>
 *
 * @author $Author: blazart $
 * @version $Revision: 1.5 $
 *
 *
 */
public class LinkTableDetailWindow extends AbstractDetailWindow {
    /** Description of the Field */
    public JTextField LINK_TABLE_ID = new JTextField();
    /** Description of the Field */
    public TableComboBox DB_TABLE_NAME_ID = new TableComboBox(Dependency.getTableHome());
    /** Description of the Field */
    public TableComboBox LINK_DB_TABLE_NAME_ID =
        new TableComboBox(Dependency.getTableHome());
    /** Description of the Field */
    public JComboBox SOURCE_DB_FIELD_NAME = new JComboBox();
    /** Description of the Field */
    public JComboBox DEST_DB_FIELD_NAME = new JComboBox();
    /** Description of the Field */
    public JButton okButton = new JButton();
    /** Description of the Field */
    public JButton cancelButton = new JButton();
    JTextField sourceTableName = new JTextField();
    JPanel jPanel1 = new JPanel();
    JPanel jPanel2 = new JPanel();
    Border border1;
    JLabel sourceTableLabel1 = new JLabel();
    JPanel jPanel3 = new JPanel();
    Border border2;
    TitledBorder titledBorder1;
    JLabel sourceTableLabel2 = new JLabel();
    JPanel jPanel4 = new JPanel();
    Border border3;
    TitledBorder titledBorder2;
    JLabel sourceTableLabel3 = new JLabel();
    JLabel sourceTableLabel4 = new JLabel();
    GridBagLayout gridBagLayout1 = new GridBagLayout();
    GridBagLayout gridBagLayout2 = new GridBagLayout();
    GridBagLayout gridBagLayout3 = new GridBagLayout();
    GridBagLayout gridBagLayout4 = new GridBagLayout();
    GridBagLayout gridBagLayout5 = new GridBagLayout();
    ConnectionManager cm;

    /**
     * Constructor for the TableDetailWindow object
     *
     * @exception java.sql.SQLException Description of Exception
     */
    public LinkTableDetailWindow() throws java.sql.SQLException {
        try {
            cm = Dependency.getConnectionManager();
            jbInit();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Overview.
     * 
     * <p>
     * Description
     * </p>
     *
     * @param defaultValues Description of Parameter
     */
    public void fillDefaultValues(HashMap defaultValues) {
        super.fillDefaultValues(defaultValues);

        sourceTableName.setText(this.DB_TABLE_NAME_ID.getSelectedTable().getTableName());

        Object fieldSource = SOURCE_DB_FIELD_NAME.getSelectedItem();
        fillComboFields(DB_TABLE_NAME_ID.getSelectedTable(), SOURCE_DB_FIELD_NAME);
        SOURCE_DB_FIELD_NAME.setSelectedItem(fieldSource);
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
    void LINK_DB_TABLE_NAME_ID_actionPerformed(ActionEvent evt) {
        if (LINK_DB_TABLE_NAME_ID.getSelectedIndex() == -1) {
            return;
        }

        Object destField = DEST_DB_FIELD_NAME.getSelectedItem();
        fillComboFields(LINK_DB_TABLE_NAME_ID.getSelectedTable(), DEST_DB_FIELD_NAME);
        DEST_DB_FIELD_NAME.setSelectedItem(destField);
    }


    /**
     * Overview.
     * 
     * <p>
     * Description
     * </p>
     *
     * @param table Description of Parameter
     * @param comboBox Description of Parameter
     */
    private void fillComboFields(Table table, JComboBox comboBox) {
        try {
            FieldNameRenderer fieldNameRenderer =
                new FieldNameRenderer(cm, table.getDBTableName());
            Map m = table.getAllColumns();
            Object[] o = m.keySet().toArray();
            Arrays.sort(o,
                new net.codjo.gui.renderer.FieldLabelComparator(cm, table.getDBTableName()));
            ComboBoxModel c = new DefaultComboBoxModel(o);
            comboBox.setModel(c);
            comboBox.setRenderer(fieldNameRenderer);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /**
     * Overview.
     * 
     * <p>
     * Description
     * </p>
     *
     * @exception Exception Description of Exception
     */
    private void jbInit() throws Exception {
        border1 = BorderFactory.createEtchedBorder(Color.white, new Color(178, 178, 178));
        border2 = BorderFactory.createEtchedBorder(Color.white, new Color(134, 134, 134));
        titledBorder1 = new TitledBorder(border2, "Table origine");
        border3 = BorderFactory.createEmptyBorder();
        titledBorder2 =
            new TitledBorder(BorderFactory.createEtchedBorder(Color.white,
                    new Color(134, 134, 134)), "Table liée");
        okButton.setText("Valider");
        this.setResizable(true);
        this.setClosable(true);
        this.setSelected(true);
        this.setIconifiable(true);
        this.setTitle("Détail du lien");
        this.getContentPane().setBackground(Color.lightGray);
        this.getContentPane().setLayout(gridBagLayout5);
        cancelButton.setText("Annuler");
        jPanel1.setLayout(gridBagLayout3);
        jPanel2.setBorder(BorderFactory.createEtchedBorder());
        jPanel2.setLayout(gridBagLayout4);
        sourceTableName.setEditable(false);
        LINK_DB_TABLE_NAME_ID.addActionListener(new LinkTableDetailWindow_LINK_DB_TABLE_NAME_ID_actionAdapter(
                this));
        sourceTableLabel1.setText("Jointure sur le champ");
        jPanel3.setBorder(titledBorder1);
        jPanel3.setLayout(gridBagLayout1);
        sourceTableLabel2.setText("Nom");
        jPanel4.setBorder(titledBorder2);
        jPanel4.setLayout(gridBagLayout2);
        sourceTableLabel3.setText("Nom");
        sourceTableLabel4.setText("Jointure sur le champ");
        this.getContentPane().add(jPanel1,
            new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.NORTH,
                GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 5), 5, 4));
        jPanel1.add(jPanel3,
            new GridBagConstraints(0, 0, 1, 1, 0.5, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.BOTH, new Insets(0, 0, 2, 0), 0, 0));
        jPanel3.add(sourceTableLabel2,
            new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
        jPanel3.add(sourceTableName,
            new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, new Insets(0, 10, 10, 10), 0, 0));
        jPanel3.add(sourceTableLabel1,
            new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, new Insets(0, 10, 0, 10), 0, 0));
        jPanel3.add(SOURCE_DB_FIELD_NAME,
            new GridBagConstraints(0, 3, 1, 1, 1.0, 1.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, new Insets(0, 10, 10, 10), 0, 0));
        jPanel1.add(jPanel4,
            new GridBagConstraints(1, 0, 1, 1, 0.5, 0.0, GridBagConstraints.EAST,
                GridBagConstraints.BOTH, new Insets(0, 0, 2, 0), 0, 0));
        jPanel4.add(sourceTableLabel3,
            new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
        jPanel4.add(sourceTableLabel4,
            new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, new Insets(0, 10, 0, 10), 0, 0));
        jPanel4.add(DEST_DB_FIELD_NAME,
            new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, new Insets(0, 10, 10, 10), 0, 0));
        jPanel4.add(LINK_DB_TABLE_NAME_ID,
            new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, new Insets(0, 10, 10, 10), 0, 0));
        this.getContentPane().add(jPanel2,
            new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.NORTH,
                GridBagConstraints.HORIZONTAL, new Insets(145, 5, 5, 5), 0, 0));
        jPanel2.add(cancelButton,
            new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.EAST,
                GridBagConstraints.NONE, new Insets(5, 0, 3, 3), 0, 0));
        jPanel2.add(okButton,
            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                GridBagConstraints.NONE, new Insets(5, 0, 3, 80), 0, 0));

        LINK_DB_TABLE_NAME_ID.setSelectedIndex(-1);
    }
}



/**
 * Overview.
 * 
 * <p>
 * Description
 * </p>
 *
 * @author $Author: blazart $
 * @version $Revision: 1.5 $
 */
class LinkTableDetailWindow_LINK_DB_TABLE_NAME_ID_actionAdapter
    implements java.awt.event.ActionListener {
    LinkTableDetailWindow adaptee;

    /**
     * Constructor for the LinkTableDetailWindow_LINK_DB_TABLE_NAME_ID_actionAdapter
     * object
     *
     * @param adaptee Description of Parameter
     */
    LinkTableDetailWindow_LINK_DB_TABLE_NAME_ID_actionAdapter(
        LinkTableDetailWindow adaptee) {
        this.adaptee = adaptee;
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
    public void actionPerformed(ActionEvent evt) {
        adaptee.LINK_DB_TABLE_NAME_ID_actionPerformed(evt);
    }
}
