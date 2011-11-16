/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.gui.broadcast;
import net.codjo.broadcast.common.Preferences;
import net.codjo.broadcast.common.PreferencesManager;
import net.codjo.broadcast.common.computed.ComputedField;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.AbstractButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.text.JTextComponent;
/**
 */
public class GuiPreferencesManager {
    private Map<Object, GuiFieldProperties> guiFieldsProperties = new HashMap<Object, GuiFieldProperties>();
    private Map<String, GuiPreferences> guiPreferences = new HashMap<String, GuiPreferences>();
    private PreferencesManager preferencesManager = null;


    public GuiPreferencesManager(PreferencesManager preferencesManager) {
        this(preferencesManager, new HashMap<Object, GuiFieldProperties>());
    }


    public GuiPreferencesManager(PreferencesManager preferencesManager,
                                 Map<Object, GuiFieldProperties> guiFieldsProperties) {
        if (preferencesManager == null) {
            throw new NullPointerException("preferencesManager");
        }
        this.preferencesManager = preferencesManager;
        this.guiFieldsProperties.putAll(guiFieldsProperties);
    }


    /**
     * Ajout d'une nouvelle préférence.
     */
    public void addPreferences(GuiPreferences pref) {
        guiPreferences.put(pref.getFamily(), pref);
    }


    public Map<String, GuiPreferences> getAllGuiPreferences() {
        return new HashMap<String, GuiPreferences>(this.guiPreferences);
    }


    /**
     * Retourne la liste des répértoires de destination.
     *
     * @return La valeur de vtomBatchFilesNames
     */
    public String[] getBroadcastLocations() {
        String[] list = getVariablesList();
        List<String> locationList = new ArrayList<String>();
        for (String aList : list) {
            if (aList.endsWith("directory")) {
                locationList.add("$" + aList + "$");
            }
        }
        return locationList.toArray(new String[locationList.size()]);
    }


    public String getColumnsTableName() {
        return preferencesManager.getColumnsTableName();
    }


    public String[] getComputedFieldNames(String family) {
        Preferences pref = preferencesManager.getPreferences(family);
        ComputedField[] fields = pref.getComputedFields();
        String[] names = new String[fields.length];
        for (int i = 0; i < fields.length; i++) {
            names[i] = fields[i].getName();
        }
        return names;
    }


    public String getComputedTableName(String family) {
        Preferences pref = preferencesManager.getPreferences(family);
        return pref.getComputedTableName();
    }


    /**
     * Retourne la liste des diffuser possible.
     *
     * @return La valeur de diffuserCode
     */
    public String[] getDiffuserCode() {
        return preferencesManager.getDiffusersCode();
    }


    public String getFileContentsTableName() {
        return preferencesManager.getFileContentsTableName();
    }


    public String getFileTableName() {
        return preferencesManager.getFileTableName();
    }


    public GuiPreferences getGuiPreferences(String family) {
        return guiPreferences.get(family);
    }


    public String getSectionTableName() {
        return preferencesManager.getSectionTableName();
    }


    public String getSelectionTableName(String family) {
        Preferences pref = preferencesManager.getPreferences(family);
        return pref.getSelectionTableName();
    }


    public Collection<String> getTableList(String family) {
        Preferences familyPref = preferencesManager.getPreferences(family);
        return new ArrayList<String>(familyPref.getTableList());
    }


    /**
     * Retourne la liste des noms de fichiers batch.
     *
     * @return La valeur de vtomBatchFilesNames
     */
    public String[] getVtomBatchFilesNames() {
        List<String> vtomBatchList = new ArrayList<String>();
        for (String aVariablesList : getVariablesList()) {
            if (aVariablesList.endsWith("vtom")) {
                vtomBatchList.add("$" + aVariablesList + "$");
            }
        }
        return vtomBatchList.toArray(new String[vtomBatchList.size()]);
    }


    /**
     * @param label    Le label du champ a modifier
     * @param field    Le champ a modifier
     * @param identify Identifiant du champ.
     */
    public void setProperties(Object label, Object field, Object identify) {
        GuiFieldProperties guiFieldProperties = getGuiFieldProperties(identify);

        // libellé
        if (label != null) {
            if (label instanceof JLabel) {
                JLabel jlabel = ((JLabel)label);
                jlabel.setText(guiFieldProperties.getLabel(jlabel.getText()));
                jlabel.setVisible(guiFieldProperties.isVisible(jlabel.isVisible()));
            }
            else if (label instanceof AbstractButton) {
                AbstractButton button = ((AbstractButton)label);
                button.setText(guiFieldProperties.getLabel(button.getText()));
                button.setVisible(guiFieldProperties.isVisible(button.isVisible()));
            }
            else {
                throw new IllegalArgumentException("Composant inconnu " + label.getClass());
            }
        }

        // champ
        if (field != null) {
            if (field instanceof JTextComponent) {
                JTextComponent textComponent = ((JTextComponent)field);
                textComponent.setEditable(guiFieldProperties.isEditable(textComponent.isEditable()));
                textComponent.setVisible(guiFieldProperties.isVisible(textComponent.isVisible()));
            }
            else if (field instanceof AbstractButton) {
                AbstractButton button = ((AbstractButton)field);
                button.setEnabled(guiFieldProperties.isEditable(button.isEnabled()));
                button.setVisible(guiFieldProperties.isVisible(button.isVisible()));
            }
            else if (field instanceof JComboBox) {
                JComboBox comboBox = ((JComboBox)field);
                comboBox.setEditable(guiFieldProperties.isEditable(comboBox.isEditable()));
                comboBox.setVisible(guiFieldProperties.isVisible(comboBox.isVisible()));
            }
            else {
                throw new IllegalArgumentException("Composant inconnu " + field.getClass());
            }
        }
    }


    private GuiFieldProperties getGuiFieldProperties(Object identify) {
        GuiFieldProperties guiFieldProperties = this.guiFieldsProperties.get(identify);
        if (guiFieldProperties == null) {
            // Dans ce cas aucune proprieté du champ sera modifié.
            guiFieldProperties = new GuiFieldProperties(null, null, null);
        }
        return guiFieldProperties;
    }


    private String[] getVariablesList() {
        Set<String> stringSet = preferencesManager.getRootContext().getParameters().keySet();
        return stringSet.toArray(new String[stringSet.size()]);
    }
}
