/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.utils.sql;
import net.codjo.gui.DetailButtonsPanel;
import net.codjo.gui.NumberField;
import net.codjo.gui.toolkit.date.DateField;
import net.codjo.util.string.StringUtil;
import net.codjo.utils.IntegerField;
import net.codjo.utils.QueryHelper;
import net.codjo.utils.SQLFieldList;
import java.awt.Color;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;
import org.apache.log4j.Logger;
/**
 * Cette classe permet de construire des ecrans de detail.
 *
 * <p> Les sous-classes doivent definir deux JButton : <code>okButton</code> et <code>cancelButton</code>
 * </p>
 *
 * @author $Author: marcona $
 * @version $Revision: 1.8 $
 */
public class AbstractDetailWindow extends JInternalFrame implements DetailWindowInterface {
    // Log
    private static final Logger APP = Logger.getLogger(AbstractDetailWindow.class);
    /**
     * Ligne inseree dans la comboBox pour simuler une valeur <code>Null</code> . Voir <code>getValue</code>
     * .
     */
    protected final String NULL_VALUE_COMBO = new String("          ");
    private final NumberFormat NUMBER_FORMAT_2DEC =
          new DecimalFormat("###,##0.00", new DecimalFormatSymbols(Locale.ENGLISH));
    private final NumberFormat NUMBER_FORMAT_5DEC =
          new DecimalFormat("###,##0.00000", new DecimalFormatSymbols(Locale.ENGLISH));
    private final NumberFormat NUMBER_FORMAT_6DEC =
          new DecimalFormat("###,##0.000000", new DecimalFormatSymbols(Locale.ENGLISH));
    private final NumberFormat NUMBER_FORMAT_DEFAULT =
          NumberFormat.getNumberInstance(Locale.ENGLISH);
    private String actionType;


    /**
     * Constructor for the AbstractDetailWindow object
     */
    public AbstractDetailWindow() {
        try {
            jbInit();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /**
     * Constructor for the AbstractDetailWindow object
     *
     * @param title Titre de la fenetre
     */
    public AbstractDetailWindow(String title) {
        super(title);
    }


    /**
     * Retourne la Bouton "Appliquer".
     *
     * @return Le bouton ou null
     *
     * @throws IllegalArgumentException TODO
     */
    public JButton getApplyButton() {
        try {
            Field field = getDeclaredField(getClass(), "applyButton");
            return (JButton)field.get(this);
        }
        catch (NoSuchFieldException ex) {
            return null;
        }
        catch (IllegalAccessException ex) {
            throw new IllegalArgumentException(
                  "Bouton Appliquer trouvé mais inaccessible");
        }
    }


    /**
     * Retourne la Bouton "Annuler".
     *
     * @return Le bouton
     *
     * @throws IllegalArgumentException TODO
     */
    public JButton getCancelButton() {
        try {
            Field field = getDeclaredField(getClass(), "cancelButton");
            return (JButton)field.get(this);
        }
        catch (NoSuchFieldException ex) {
            try {
                Field field = getDeclaredField(getClass(), "detailButtonsPanel");
                return ((DetailButtonsPanel)field.get(this)).getCancelButton();
            }
            catch (NoSuchFieldException e1) {
                throw new IllegalArgumentException(
                      "La fenetre ne definit pas de bouton Annuler");
            }
            catch (IllegalAccessException e2) {
                throw new IllegalArgumentException(
                      "Bouton Annuler trouvé dans le Panel mais inaccessible");
            }
        }
        catch (IllegalAccessException ex) {
            throw new IllegalArgumentException("Bouton Annuler trouvé mais inaccessible");
        }
    }


    public java.util.List getDeclaredFields(Class cl)
          throws SecurityException {
        if (cl == null) {
            return new ArrayList();
        }
        java.util.List fields = getDeclaredFields(cl.getSuperclass());
        fields.addAll(Arrays.asList(cl.getDeclaredFields()));
        return fields;
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
     * Retourne les noms des attributs (composants graphiques) définis dans la fenêtre de détail.
     *
     * @return Liste des noms (String).
     *
     * @throws IllegalArgumentException TODO
     */
    public java.util.List getListOfComponents() {
        java.util.List colsName = new ArrayList();
        Field field = null;
        Field[] fieldArray =
              (Field[])getDeclaredFields(getClass()).toArray(new Field[]{});

        try {
            for (int i = 0; i < fieldArray.length; i++) {
                field = fieldArray[i];
                if (field.getModifiers() == Modifier.PUBLIC) {
                    Object objectField = field.get(this);
                    if (objectField instanceof JComponent) {
                        colsName.add(field.getName());
                    }
                }
            }
            return colsName;
        }
        catch (IllegalAccessException ex) {
            throw new IllegalArgumentException("Composant inaccessible : "
                                               + field.getName());
        }
    }


    /**
     * Retourne la Bouton "Suivant".
     *
     * @return Le bouton
     *
     * @throws IllegalArgumentException TODO
     */
    public JButton getNextButton() {
        try {
            Field field = getDeclaredField(getClass(), "nextButton");
            return (JButton)field.get(this);
        }
        catch (NoSuchFieldException ex) {
            try {
                Field field = getDeclaredField(getClass(), "detailButtonsPanel");
                return ((DetailButtonsPanel)field.get(this)).getNextButton();
            }
            catch (NoSuchFieldException e1) {
                return null;
            }
            catch (IllegalAccessException e2) {
                throw new IllegalArgumentException(
                      "Bouton Suivant trouvé dans le Panel mais inaccessible");
            }
        }
        catch (IllegalAccessException ex) {
            throw new IllegalArgumentException("Bouton Suivant trouvé mais inaccessible");
        }
    }


    /**
     * Retourne la Bouton "Valider".
     *
     * @return Le bouton
     *
     * @throws IllegalArgumentException TODO
     */
    public JButton getOkButton() {
        try {
            Field field = getDeclaredField(getClass(), "okButton");
            return (JButton)field.get(this);
        }
        catch (NoSuchFieldException ex) {
            try {
                Field field = getDeclaredField(getClass(), "detailButtonsPanel");
                return ((DetailButtonsPanel)field.get(this)).getOkButton();
            }
            catch (NoSuchFieldException e1) {
                throw new IllegalArgumentException(
                      "La fenetre ne definit pas de bouton OK");
            }
            catch (IllegalAccessException e2) {
                throw new IllegalArgumentException(
                      "Bouton OK trouvé dans le Panel mais inaccessible");
            }
        }
        catch (IllegalAccessException ex) {
            throw new IllegalArgumentException("Bouton OK trouvé mais inaccessible");
        }
    }


    /**
     * Retourne la Bouton "Précédent".
     *
     * @return Le bouton
     *
     * @throws IllegalArgumentException TODO
     */
    public JButton getPreviousButton() {
        try {
            Field field = getDeclaredField(getClass(), "previousButton");
            return (JButton)field.get(this);
        }
        catch (NoSuchFieldException ex) {
            try {
                Field field = getDeclaredField(getClass(), "detailButtonsPanel");
                return ((DetailButtonsPanel)field.get(this)).getPreviousButton();
            }
            catch (NoSuchFieldException e1) {
                return null;
            }
            catch (IllegalAccessException e2) {
                throw new IllegalArgumentException(
                      "Bouton Précédent trouvé dans le Panel mais inaccessible");
            }
        }
        catch (IllegalAccessException ex) {
            throw new IllegalArgumentException(
                  "Bouton Précédent trouvé mais inaccessible");
        }
    }


    /**
     * Positionne le type d'action qui a causé l'ouverture de l'écran de détail (ajout ou modification).
     *
     * @param actionType Le type d'action (add ou modify)
     */
    public void setActionType(String actionType) {
        this.actionType = actionType;
    }


    /**
     * Retourne le type d'action qui a causé l'ouverture de l'écran de détail (ajout ou modification).
     *
     * @return Le type d'action (add ou modify)
     */
    public String getActionType() {
        return actionType;
    }


    /**
     * Rempli les JTextComponent et les JCheckBox des écrans page avec les valeurs des champs correspondant.
     *
     * @param columns Liste des colonnes
     * @param rs      ResultSet des valeurs à renseigner
     *
     * @throws SQLException             -
     * @throws IllegalArgumentException TODO
     */
    public void fillComponent(SQLFieldList columns, ResultSet rs)
          throws SQLException {
        if (rs.next() == false) {
            throw new IllegalArgumentException("Manque une ligne");
        }

        for (Iterator iter = columns.fieldNames(); iter.hasNext();) {
            String columnName = (String)iter.next();
            setValue(columnName, rs.getObject(columnName));
        }

        for (Iterator iter = columns.fieldNames(); iter.hasNext();) {
            String columnName = (String)iter.next();
            initModificationListener(columnName);
        }
    }


    /**
     * Rempli un JTextComponent ou un JCheckBox des écrans page avec une valeur par défaut.
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
     * Remplit le QueryHelper avec les valeurs des colonnes avec les champs de la fenetre detail.
     *
     * @param columns Liste de colonnes a remplir dans le query helper.
     * @param qh      Le QueryHelper a remplir.
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
     * Par defaut, rien n'est fait.
     *
     * @param pk  TODO
     * @param con TODO
     */
    public void saveLinks(Map pk, Connection con)
          throws Exception {
    }


    /**
     * Positionne la valeur du combobox. Cette methode gere aussi la valeur null
     *
     * @param cb          Le ComboBox
     * @param columnValue La valeur
     */
    private void setComboBoxValue(JComboBox cb, Object columnValue) {
        if (columnValue == null) {
            cb.setSelectedIndex(-1);
        }
        else {
            boolean itemFound = false;
            int count = cb.getItemCount();
            for (int i = 0; i < count; i++) {
                if (columnValue.equals(cb.getItemAt(i))) {
                    itemFound = true;
                    cb.setSelectedIndex(i);
                    break;
                }
            }
            if (!itemFound) {
                cb.addItem(columnValue);
                cb.setSelectedItem(columnValue);
            }
        }
    }


    /**
     * Renseigne la valeur du champ de la colonne.
     *
     * @param columnName  Le nom physique de la colonne
     * @param columnValue La valeur du champ à renseigner
     *
     * @throws IllegalArgumentException TODO
     */
    private void setValue(String columnName, Object columnValue) {
        try {
            Field field = getDeclaredField(getClass(), columnName);
            Object objectField = field.get(this);

            if (objectField == null) {
                field.set(this, columnValue);
            }
            else if (objectField instanceof NumberField || objectField instanceof IntegerField) {
                if (columnValue == null) {
                    ((JTextField)objectField).setText("");
                }
                else {
                    ((JTextField)objectField).setText(columnValue.toString());
                }
            }
            else if (objectField instanceof JTextComponent) {
                if (columnValue == null) {
                    ((JTextComponent)objectField).setText("");
                }
                else if (columnValue instanceof java.util.Date) {
                    DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                    ((JTextComponent)objectField).setText(df.format(columnValue));
                }
                else if (columnValue instanceof java.lang.Number) {
                    if (columnValue instanceof java.lang.Integer) {
                        ((JTextComponent)objectField).setText(columnValue.toString());
                    }
                    else {
                        BigDecimal number = new BigDecimal(columnValue.toString());
                        int NbDec = number.scale();

                        switch (NbDec) {
                            case 2:
                                ((JTextComponent)objectField).setText(NUMBER_FORMAT_2DEC.format(
                                      (columnValue)));
                                break;
                            case 5:
                                ((JTextComponent)objectField).setText(NUMBER_FORMAT_5DEC.format(
                                      (columnValue)));
                                break;
                            case 6:
                                ((JTextComponent)objectField).setText(NUMBER_FORMAT_6DEC.format(
                                      (columnValue)));
                                break;
                            default:
                                ((JTextComponent)objectField).setText(NUMBER_FORMAT_DEFAULT.format(
                                      (columnValue)));
                        }
                    }
                }
                else {
                    ((JTextComponent)objectField).setText(columnValue.toString());
                }
                ((JTextComponent)objectField).setCaretPosition(0);
            }
            else if (objectField instanceof JComboBox) {
                setComboBoxValue((JComboBox)objectField, columnValue);
            }
            else if (objectField instanceof JCheckBox) {
                ((JCheckBox)objectField).setSelected(((Boolean)columnValue).booleanValue());
            }
            else if (objectField instanceof DateField) {
                if (columnValue instanceof java.util.Date) {
                    ((DateField)objectField).setDate((java.util.Date)(columnValue));
                }
            }
            else if (!(objectField instanceof JComponent)) {
                field.set(this, columnValue);
            }
            else {
                throw new IllegalArgumentException("Type Composant inconnu : "
                                                   + columnName);
            }
        }
        catch (NoSuchFieldException ex) {
            APP.debug("Le champs " + columnName + " est introuvable (" + getTitle() + ")",
                      ex);
            return;
        }
        catch (IllegalAccessException ex) {
            throw new IllegalArgumentException("Composant inaccessible : " + columnName);
        }
    }


    /**
     * Recherche le field <code>name</code> dans la classe courante et aussi dans les sous-classes.
     *
     * @param cl   la classe de recherche
     * @param name le nom du champs
     *
     * @return Le Field
     *
     * @throws NoSuchFieldException Le field n'existe pas
     * @throws SecurityException    Le field est private.
     */
    private Field getDeclaredField(Class cl, String name)
          throws NoSuchFieldException, SecurityException {
        if (cl == null) {
            throw new NoSuchFieldException(name);
        }
        try {
            return cl.getDeclaredField(name);
        }
        catch (NoSuchFieldException ex) {
            return getDeclaredField(cl.getSuperclass(), name);
        }
        catch (SecurityException ex) {
            return getDeclaredField(cl.getSuperclass(), name);
        }
    }


    /**
     * Retourne la valeur du champ de la colonne.
     *
     * @param columnName Le nom physique de la colonne
     * @param sqlType    Le type sql du champs
     *
     * @return la valeur.
     *
     * @throws IllegalArgumentException TODO
     */
    protected Object getValue(String columnName, int sqlType) {
        try {
            Field field = getDeclaredField(getClass(), columnName);
            Object o = field.get(this);

            if (o instanceof JTextComponent) {
                try {
                    ((JTextComponent)o).setForeground(Color.black);
                    return translateValue(((JTextComponent)o).getText(), columnName,
                                          sqlType);
                }
                catch (IllegalArgumentException ex) {
                    ((JTextComponent)o).setForeground(Color.red);
                    throw ex;
                }
            }
            else if (o instanceof JComboBox) {
                Object result = ((JComboBox)o).getSelectedItem();
                if (NULL_VALUE_COMBO == result) {
                    return null;
                }
                else {
                    return result;
                }
            }
            else if (o instanceof JCheckBox) {
                return new Boolean(((JCheckBox)o).isSelected());
            }
            else if (o instanceof DateField) {
                return ((DateField)o).getDate();
            }
            else {
                throw new IllegalArgumentException("Composant inconnu : " + columnName);
            }
        }
        catch (NoSuchFieldException ex) {
            return null;
        }
        catch (IllegalAccessException ex) {
            throw new IllegalArgumentException("Composant inaccessible : " + columnName);
        }
    }


    /**
     * Ajoute un ModificationListener à AbstractDetailWindow.
     *
     * @param columnName Le ModificationListener à ajouter
     */
    private void initModificationListener(String columnName) {
        try {
            Field field = getDeclaredField(getClass(), columnName);
            Object fieldValue = field.get(this);
            if ((fieldValue instanceof JComponent) == false) {
                return;
            }
            JComponent component = (JComponent)fieldValue;

            ModificationListener oldML =
                  (ModificationListener)component.getClientProperty("modificationListener");
            if (oldML != null) {
                oldML.clear();
                return;
            }

            if (component instanceof JTextComponent) {
                ModificationListener ml = new ModificationListener(component);
                ((JTextComponent)component).getDocument().addDocumentListener(ml);
                component.putClientProperty("modificationListener", ml);
            }
            else if (component instanceof JComboBox) {
                ModificationListener ml = new ModificationListener(component);
                ((JComboBox)component).addActionListener(ml);
                component.putClientProperty("modificationListener", ml);
            }
            else if (component instanceof JCheckBox) {
                ModificationListener ml = new ModificationListener(component);
                ((JCheckBox)component).addActionListener(ml);
                component.putClientProperty("modificationListener", ml);
            }
        }
        catch (Exception nsfe) {
            nsfe.printStackTrace();
        }
    }


    /**
     * Init IHM.
     *
     * @throws Exception Description of Exception
     */
    private void jbInit() throws Exception {
    }


    /**
     * Converti le texte dans le format bd.
     *
     * @param textValue  Valeur sous forme String
     * @param columnName Le nom physique de la colonne
     * @param sqlType    type sql du format
     *
     * @return Valeur convertie.
     *
     * @throws IllegalArgumentException TODO
     */
    protected Object translateValue(String textValue, String columnName, int sqlType) {
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
            case Types.TINYINT:
                return Integer.decode(textValue);
            case Types.FLOAT:
                return new Float(StringUtil.removeAllCharOccurrence(textValue, ','));
            case Types.DOUBLE:
                return new Double(StringUtil.removeAllCharOccurrence(textValue, ','));
            case Types.NUMERIC:
                return new BigDecimal(StringUtil.removeAllCharOccurrence(textValue, ','));
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
                                                       + " pour le champ " + columnName
                                                       + " (format DD/MM/AAAA)");
                }
            default:
                throw new IllegalArgumentException("Type SQL non supporte : "
                                                   + columnName);
        }
    }
}
