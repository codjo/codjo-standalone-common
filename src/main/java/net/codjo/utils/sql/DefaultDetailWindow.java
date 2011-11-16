/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.utils.sql;
import net.codjo.gui.renderer.FieldLabelComparator;
import net.codjo.gui.renderer.FieldNameRenderer;
import net.codjo.model.Table;
import net.codjo.utils.ConnectionManager;
import net.codjo.utils.QueryHelper;
import net.codjo.utils.SQLFieldList;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

/**
 * Création et affichage d'une fenêtre de détail (sans les clés primaires)
 *
 * @author $Author: marcona $
 * @version $Revision: 1.5 $
 *
 */
public class DefaultDetailWindow extends JInternalFrame implements DetailWindowInterface {
    private GridBagLayout gridBagLayout1 = new GridBagLayout();
    private TreeMap componentList;
    private JPanel bottomPanel = new JPanel();
    private JButton okButton = new JButton("OK");
    private JButton cancelButton = new JButton("Annuler");
    private JScrollPane scrollPane = new JScrollPane();
    private JPanel firstPanel = new JPanel();
    private JPanel componentPanel = new JPanel();
    private Table tableToDisplay = null;
    private BorderLayout borderLayout1 = new BorderLayout();
    private BorderLayout borderLayout2 = new BorderLayout();
    private FlowLayout flowLayout1 = new FlowLayout();
    private String actionType;

    /**
     * Constructeur
     *
     * @param tableToDisplay Table à afficher
     * @param conMan Description of Parameter
     *
     * @exception SQLException Description of Exception
     */
    public DefaultDetailWindow(Table tableToDisplay, ConnectionManager conMan)
            throws SQLException {
        this.tableToDisplay = tableToDisplay;

        initComponentList(conMan);
        jbInit();
    }

    /**
     * Positionne le type d'action qui a causé l'ouverture de l'écran de détail (ajout ou
     * modification).
     *
     * @param actionType Le type d'action (add ou modify)
     */
    public void setActionType(String actionType) {
        this.actionType = actionType;
    }


    /**
     * Retourne le type d'action qui a causé l'ouverture de l'écran de détail (ajout ou
     * modification).
     *
     * @return Le type d'action (add ou modify)
     */
    public String getActionType() {
        return actionType;
    }


    /**
     * Retourne la fenetre de detail.
     *
     * @return Une JInternalFrame
     */
    public JInternalFrame getInternalFrame() {
        return this;
    }


    /**
     * Retourne la Bouton "Enregistrer".
     *
     * @return Le bouton
     */
    public JButton getOkButton() {
        return okButton;
    }


    /**
     * Retourne null, car la fenetre ne definit pas de bouton "Appliquer".
     *
     * @return null
     */
    public JButton getApplyButton() {
        return null;
    }


    /**
     * Retourne null, car la fenetre ne definit pas de bouton "Précédent".
     *
     * @return null
     */
    public JButton getPreviousButton() {
        return null;
    }


    /**
     * Retourne null, car la fenetre ne definit pas de bouton "Suivant".
     *
     * @return null
     */
    public JButton getNextButton() {
        return null;
    }


    /**
     * Retourne la Bouton "Annuler".
     *
     * @return Le bouton
     */
    public JButton getCancelButton() {
        return cancelButton;
    }


    /**
     * Retourne les noms des attributs (composants graphiques) définis dans la fenêtre de
     * détail.
     *
     * @return Liste des noms (String).
     */
    public java.util.List getListOfComponents() {
        if (componentList == null) {
            return null;
        }
        List liste = new ArrayList();
        Iterator iter = componentList.keySet().iterator();
        while (iter.hasNext()) {
            String fieldName = (String)iter.next();
            liste.add(fieldName);
        }
        return liste;
    }


    /**
     * Rempli un JTextComponent ou un JCheckBox des écrans page avec une valeur par
     * défaut.
     *
     * @param defaultValues Description of Parameter
     */
    public void fillDefaultValues(HashMap defaultValues) {
        for (Iterator iter = defaultValues.keySet().iterator(); iter.hasNext();) {
            String fieldName = (String)iter.next();
            Object value = defaultValues.get(fieldName);
            setValue(fieldName, value);
        }
    }


    /**
     * Rempli les JTextComponent et les JCheckBox des écrans page avec les valeurs des
     * champs correspondant. (idem original)
     *
     * @param columns Liste des colonnes
     * @param rs ResultSet des valeurs à renseigner
     *
     * @exception SQLException -
     * @throws IllegalArgumentException TODO
     *
     * @todo idem classe AbstractDetailWindow
     */
    public void fillComponent(SQLFieldList columns, ResultSet rs)
            throws SQLException {
        if (rs.next() == false) {
            throw new IllegalArgumentException("Manque une ligne");
        }

        Iterator iter = columns.fieldNames();
        while (iter.hasNext()) {
            String columnName = (String)iter.next();
            setValue(columnName, rs.getObject(columnName));
        }
    }


    /**
     * Remplit le QueryHelper avec les valeurs des colonnes avec les champs de la fenetre
     * detail.
     *
     * @param columns Liste de colonnes a remplir dans le query helper.
     * @param qh Le QueryHelper a remplir.
     *
     * @todo idem classe AbstractDetailWindow
     */
    public void fillQueryHelper(SQLFieldList columns, QueryHelper qh) {
        Iterator iter = columns.fieldNames();

        while (iter.hasNext()) {
            String columnName = (String)iter.next();
            Object value = getValue(columnName, columns.getFieldType(columnName));
            qh.setInsertValue(columnName, value);
        }
    }


    /**
     * DOCUMENT ME!
     *
     * @param pk
     * @param con
     *
     * @exception Exception
     */
    public void saveLinks(Map pk, Connection con)
            throws Exception {}


    /**
     * Renseigne la valeur du champ de la colonne.
     *
     * @param fieldName The new Value value
     * @param fieldValue The new Value value
     *
     * @throws IllegalArgumentException TODO
     */
    private void setValue(String fieldName, Object fieldValue) {
        Object o = componentList.get(fieldName);

        if (o instanceof JTextField) {
            if (fieldValue == null) {
                ((JTextField)o).setText("");
            }
            else if (fieldValue instanceof java.util.Date) {
                DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                ((JTextField)o).setText(df.format(fieldValue));
            }
            else {
                ((JTextField)o).setText(fieldValue.toString());
            }
            ((JTextField)o).setCaretPosition(0);
            ((JTextField)o).getDocument().addDocumentListener(new ModificationListener(
                    ((JTextField)o)));
        }
        else if (o instanceof JCheckBox) {
            ((JCheckBox)o).setSelected(((Boolean)fieldValue).booleanValue());
            ((JCheckBox)o).addActionListener(new ModificationListener(((JCheckBox)o)));
            ((JCheckBox)o).setForeground(((JCheckBox)o).getBackground());
        }
        else {
            throw new IllegalArgumentException("Type Composant inconnu : " + fieldName);
        }
    }


    /**
     * Retourne la valeur du champ de la colonne.
     *
     * @param fieldName Le nom du champ
     * @param sqlType Le type sql du champ
     *
     * @return la valeur.
     *
     * @throws IllegalArgumentException TODO
     */
    private Object getValue(String fieldName, int sqlType) {
        Object o = componentList.get(fieldName);

        if (o instanceof JTextField) {
            try {
                ((JTextField)o).setForeground(Color.black);
                return translateValue(((JTextField)o).getText(), fieldName, sqlType);
            }
            catch (IllegalArgumentException ex) {
                ((JTextField)o).setForeground(Color.red);
                throw ex;
            }
        }
        else if (o instanceof JCheckBox) {
            return new Boolean(((JCheckBox)o).isSelected());
        }
        else {
            throw new IllegalArgumentException("Composant inconnu : " + fieldName);
        }
    }


    /**
     * Converti le texte dans le format bd.
     *
     * @param textValue Valeur sous forme String
     * @param columnName Le nom physique de la colonne
     * @param sqlType type sql du format
     *
     * @return Valeur convertie.
     *
     * @throws IllegalArgumentException TODO
     */
    private Object translateValue(String textValue, String columnName, int sqlType) {
        if ("".equals(textValue)) {
            return null;
        }
        switch (sqlType) {
            case Types.CHAR:
            case Types.VARCHAR:
            case Types.LONGVARCHAR:
                return textValue;
            case Types.INTEGER:
            case Types.SMALLINT:
                return Integer.decode(textValue);
            case Types.FLOAT:
                return new Float(textValue);
            case Types.DOUBLE:
                return new Double(textValue);
            case Types.NUMERIC:
                return new BigDecimal(textValue);
            case Types.BIT:
                return new Boolean(textValue);
            case Types.DATE:
            case Types.TIMESTAMP:
                DateFormat df =
                    DateFormat.getDateInstance(DateFormat.SHORT, Locale.FRANCE);
                df.setLenient(false);
                try {
                    return df.parse(textValue);
                }
                catch (ParseException ex) {
                    throw new IllegalArgumentException("Mauvais format de date"
                                                       + " pour le champ " + columnName + " (format DD/MM/AAAA)");
                }
            default:
                throw new IllegalArgumentException("Type SQL non supporte : "
                                                   + columnName);
        }
    }


    /**
     * Remplit la HashMap des composants avec le nom du champ comme clé et le composant
     * comme valeur
     *
     * @param conMan Description of Parameter
     *
     * @exception SQLException Description of Exception
     */
    private void initComponentList(ConnectionManager conMan)
            throws SQLException {
        FieldLabelComparator comparator =
            new FieldLabelComparator(conMan, tableToDisplay.getDBTableName());
        componentList = new TreeMap(comparator);
        Map columnList = tableToDisplay.getAllColumns();
        Iterator iter = columnList.keySet().iterator();
        while (iter.hasNext()) {
            String fieldName = (String)iter.next();
            Integer sqlType = (Integer)columnList.get(fieldName);
            if (sqlType.intValue() == Types.BIT) {
                JCheckBox cbx = new JCheckBox();
                cbx.setText("(modifié)");
                componentList.put(fieldName, cbx);
            }
            else {
                JTextField txf = new JTextField();
                componentList.put(fieldName, txf);
            }
        }
        if (tableToDisplay.getPkNames().size() == 1) {
            componentList.remove(tableToDisplay.getPkNames().get(0));
        }
    }


    /**
     * Init IHM.
     *
     * @exception SQLException Description of Exception
     */
    private void jbInit() throws SQLException {
        this.setResizable(true);
        this.setClosable(true);
        try {
            this.setSelected(true);
        }
        catch (java.beans.PropertyVetoException ex) {
            ex.printStackTrace();
        }
        this.setIconifiable(true);
        this.setTitle("Détail de la table " + this.tableToDisplay.getTableName());
        this.getContentPane().setLayout(borderLayout1);
        okButton.setText("Valider");
        bottomPanel.setLayout(flowLayout1);
        this.getContentPane().add(scrollPane, BorderLayout.CENTER);
        scrollPane.getViewport().add(firstPanel);
        firstPanel.setLayout(borderLayout2);
        firstPanel.add(componentPanel, BorderLayout.NORTH);
        componentPanel.setLayout(gridBagLayout1);
        insertComponent();
        this.getContentPane().add(bottomPanel, BorderLayout.SOUTH);
        flowLayout1.setAlignment(FlowLayout.RIGHT);
        bottomPanel.add(okButton, null);
        bottomPanel.add(cancelButton, null);
        this.setPreferredSize(new Dimension(400, 500));
    }


    /**
     * Placement des composants dans la fenêtre
     *
     * @exception SQLException Oups.
     */
    private void insertComponent() throws SQLException {
        int gridy;
        gridy = 0;
        Map traductTable =
            FieldNameRenderer.loadTraducTable(Dependency.getConnectionManager(),
                                              tableToDisplay.getDBTableName());
        Iterator iter = this.componentList.keySet().iterator();
        while (iter.hasNext()) {
            String fieldName = (String)iter.next();
            String nameToDisplay = "";
            if (traductTable.containsKey(fieldName)) {
                nameToDisplay = (String)traductTable.get(fieldName);
            }
            else {
                nameToDisplay = fieldName;
            }
            componentPanel.add(new JLabel(nameToDisplay),
                               new GridBagConstraints(0, gridy, 2, 1, 0.0, 0.0, GridBagConstraints.WEST,
                                                      GridBagConstraints.NONE, new Insets(5, 5, 0, 0), 0, 0));

            componentPanel.add((Component)componentList.get(fieldName),
                               new GridBagConstraints(1, gridy, 2, 1, 1.0, 0.0, GridBagConstraints.EAST,
                                                      GridBagConstraints.HORIZONTAL, new Insets(5, 250, 0, 5), 0, 0));

            //On met à jour les contraintes de positionnement
            gridy++;
        }
    }
}
