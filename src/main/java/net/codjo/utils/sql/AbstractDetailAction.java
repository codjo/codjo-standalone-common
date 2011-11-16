/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.utils.sql;
import net.codjo.gui.Modal;
import net.codjo.gui.toolkit.util.ErrorDialog;
import net.codjo.utils.GuiUtil;
import net.codjo.utils.SQLFieldList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
/**
 * Classe abstraite pour les actions des fenêtres de détail (ajout et modification).
 *
 * <p> Une action est connectee avec sa table source. </p>
 *
 * @author $Author: blazart $
 * @version $Revision: 1.7 $
 * @see net.codjo.utils.sql.AddAction
 * @see net.codjo.utils.sql.ModifyAction
 */
abstract class AbstractDetailAction extends AbstractDbAction {
    DetailWindowInterface detailWindow;
    private Class windowClass;
    private HashMap defaultValues;
    private ActionListener okActionListener;
    private ActionListener cancelActionListener;
    private ActionListener applyActionListener;
    private ActionListener previousActionListener;
    private ActionListener nextActionListener;
    protected String actionType;


    /**
     * Constructor for the AbstractDetailAction object
     */
    public AbstractDetailAction() {
    }


    /**
     * Constructor for the AbstractDetailAction object
     *
     * @param dp       DesktopPane
     * @param frm      Fenêtre source (écran liste)
     * @param gt       Table source de l'action
     * @param packName Nom du package dans lequel se trouve la classe de l'écran détail
     *
     * @throws IllegalArgumentException TODO
     */
    public AbstractDetailAction(JDesktopPane dp, JInternalFrame frm, GenericTable gt,
                                String packName) {
        super(dp, frm, gt);
        if (dp == null || gt == null || frm == null || packName == null) {
            throw new IllegalArgumentException();
        }
        defaultValues = new HashMap();
        windowClass = findWindowClass(getDbTableName(), packName);
        initListeners();
    }


    /**
     * Ajoute une valeur par défaut pour remplir automatique un champ de l'écran de détail lors d'un ajout.
     *
     * @param fieldName    Le nom du champ
     * @param defaultValue La valeur du champ
     */
    public void setDefaultValue(String fieldName, Object defaultValue) {
        if (defaultValues.containsKey(fieldName)) {
            defaultValues.remove(fieldName);
        }
        defaultValues.put(fieldName, defaultValue);
    }


    /**
     * Retire les listeners.
     */
    public void removeListeners() {
        detailWindow.getOkButton().removeActionListener(okActionListener);
        detailWindow.getCancelButton().removeActionListener(cancelActionListener);
        if (detailWindow.getApplyButton() != null) {
            detailWindow.getApplyButton().removeActionListener(applyActionListener);
        }
        if (detailWindow.getPreviousButton() != null) {
            detailWindow.getPreviousButton().removeActionListener(previousActionListener);
        }
        if (detailWindow.getNextButton() != null) {
            detailWindow.getNextButton().removeActionListener(nextActionListener);
        }
    }


    /**
     * Affiche une nouvelle fenetre detail.
     */
    public void actionPerformed(ActionEvent ev) {
        fireActionEvent(ev);
        detailWindow = buildDetailWindow();
        detailWindow.setActionType(actionType);

        actionPerformed(detailWindow);

        detailWindow.getInternalFrame().setVisible(true);
        detailWindow.getInternalFrame().pack();
        getDesktopPane().add(detailWindow.getInternalFrame());
        GuiUtil.centerWindow(detailWindow.getInternalFrame());
        try {
            detailWindow.getInternalFrame().setSelected(true);
        }
        catch (java.beans.PropertyVetoException g) {
        }
    }


    /**
     * Retourne La classe de la fenetre detail.
     *
     * @return La classe, ou null si inexistante
     */
    protected final Class getWindowClass() {
        return windowClass;
    }


    /**
     * Retourne la liste des champs editables dans la fenêtre detail.
     *
     * @param componentList List des composants definis dans la fenêtre
     *
     * @return La liste des champs (SQLFieldList)
     */
    protected final SQLFieldList getEditableFields(List componentList) {
        SQLFieldList editableFields = new SQLFieldList();

        Map allColumns = getGenericTable().getTable().getAllColumns();
        Iterator iter = allColumns.keySet().iterator();
        while (iter.hasNext()) {
            String fieldName = (String)iter.next();
            if (componentList.contains(fieldName)) {
                Integer sqlType = (Integer)allColumns.get(fieldName);
                editableFields.addField(fieldName, sqlType.intValue());
            }
        }

        return editableFields;
    }


    /**
     * Execute la requete SQL de l'action (ajout ou modification ...). Cette methode est appele par le bouton
     * OK.
     *
     * @throws Exception Description of Exception
     */
    protected void executeOK() throws Exception {
        executeAction();
    }


    /**
     * Applique la requete SQL de l'action (ajout ou modification ...). Cette methode est appele par le bouton
     * Apply.
     */
    protected void executeApply() throws Exception {
        executeAction();
    }


    /**
     * Execute la requete SQL de l'action (ajout ou modification ...). Cette methode est appele par le bouton
     * OK, ou Apply.
     */
    protected abstract void executeAction() throws Exception;


    /**
     * Annule l'action et dispose la fenetre.
     */
    protected void executeCancel() {
        closeDetailWindow();
    }


    /**
     * Construit et initialise une fenetre de detail.
     *
     * @return La fenetre de detail (ou null)
     *
     * @throws Error TODO
     */
    protected final DetailWindowInterface buildDetailWindow() {
        try {
            if (windowClass == DefaultDetailWindow.class) {
                return new DefaultDetailWindow(getTable(),
                                               Dependency.getConnectionManager());
            }
            else {
                return (DetailWindowInterface)windowClass.newInstance();
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
            ErrorDialog.show(getDesktopPane(), "Erreur interne", ex);
            throw new Error("Erreur" + ex);
        }
    }


    /**
     * Recherche la classe de la fenetre detail attachee a la table BD.
     *
     * @param dbTableName Nom physique
     * @param packageName Description of Parameter
     *
     * @return Classe de la fenetre de detail.
     */
    protected final Class findWindowClass(String dbTableName, String packageName) {
        StringBuffer name = new StringBuffer();
        StringTokenizer tokenizer;
        if (dbTableName.indexOf("BO_") < 0) {
            tokenizer = new StringTokenizer(dbTableName.substring(3), "_");
        }
        else {
            tokenizer = new StringTokenizer(dbTableName, "_");
        }

        while (tokenizer.hasMoreElements()) {
            String token = (String)tokenizer.nextToken();
            name.append(token.substring(0, 1));
            name.append(token.substring(1).toLowerCase());
        }

        String className = packageName + "." + name.toString() + "DetailWindow";

        try {
            return Class.forName(className);
        }
        catch (ClassNotFoundException ex) {
            return DefaultDetailWindow.class;
        }
    }


    /**
     * Mise à jour de l'état des boutons Précédent et Suivant / numéro de ligne
     */
    protected void updateButtonState() {
        int currentLineIndex = getGenericTable().getSelectedRow();

        if (currentLineIndex == -1) {
            detailWindow.getPreviousButton().setEnabled(false);
            detailWindow.getNextButton().setEnabled(true);
        }
        else {
            if (currentLineIndex == getGenericTable().getNumberOfFirstRow() - 1) {
                detailWindow.getPreviousButton().setEnabled(false);
            }
            else {
                detailWindow.getPreviousButton().setEnabled(true);
            }

            if (currentLineIndex == getGenericTable().getNumberOfLastRow() - 1) {
                detailWindow.getNextButton().setEnabled(false);
            }
            else {
                detailWindow.getNextButton().setEnabled(true);
            }
        }
    }


    /**
     * Click sur le bouton Précédent.
     */
    protected void previousButton_actionPerformed(ActionEvent ev) {
        if (getGenericTable().getSelectedRow() > 0) {
            try {
                //executeApply();
                int newSelectedRow = getGenericTable().getSelectedRow() - 1;
                getGenericTable().getSelectionModel().setSelectionInterval(newSelectedRow,
                                                                           newSelectedRow);
                setGenericTablePK(getGenericTable().getKey(getGenericTable()
                      .getSelectedRow()));
                refreshGenericTable();
            }
            catch (Exception ex) {
                ex.printStackTrace();
                ErrorDialog.show(getDesktopPane(), "Erreur", ex);
            }
        }
    }


    /**
     * Click sur le bouton Suivant.
     */
    protected void nextButton_actionPerformed(ActionEvent ev) {
        if (getGenericTable().getSelectedRow() < getGenericTable().getNumberOfLastRow()) {
            try {
                //executeApply();
                int newSelectedRow = getGenericTable().getSelectedRow() + 1;
                getGenericTable().getSelectionModel().setSelectionInterval(newSelectedRow,
                                                                           newSelectedRow);
                setGenericTablePK(getGenericTable().getKey(getGenericTable()
                      .getSelectedRow()));
                refreshGenericTable();
            }
            catch (Exception ex) {
                ex.printStackTrace();
                ErrorDialog.show(getDesktopPane(), "Erreur", ex);
            }
        }
    }


    /**
     * Reinitialise la fenetre de detail.
     *
     * @param dwi La fenetre detail a reinitialiser
     *
     * @throws IllegalArgumentException TODO
     */
    void actionPerformed(DetailWindowInterface dwi) {
        detailWindow = dwi;
        if (detailWindow == null) {
            throw new IllegalArgumentException();
        }

        if (defaultValues.size() != 0) {
            detailWindow.fillDefaultValues(defaultValues);
        }

        new Modal(getWindowTable(), detailWindow.getInternalFrame());
        detailWindow.getOkButton().addActionListener(okActionListener);
        detailWindow.getCancelButton().addActionListener(cancelActionListener);

        if (detailWindow.getApplyButton() != null) {
            detailWindow.getApplyButton().addActionListener(applyActionListener);
        }

        if ((detailWindow.getPreviousButton() != null)
            && (detailWindow.getNextButton() != null)) {
            detailWindow.getPreviousButton().addActionListener(previousActionListener);
            detailWindow.getNextButton().addActionListener(nextActionListener);
            getGenericTable().getSelectionModel()
                  .addListSelectionListener(new GenericTableSelectionListener());
            updateButtonState();
        }
    }


    /**
     * Initialisation des listeners.
     */
    private void initListeners() {
        okActionListener =
              new ActionListener() {
                  public void actionPerformed(ActionEvent ev) {
                      okButton_actionPerformed(ev);
                  }
              };

        cancelActionListener =
              new ActionListener() {
                  public void actionPerformed(ActionEvent ev) {
                      executeCancel();
                  }
              };

        applyActionListener =
              new ActionListener() {
                  public void actionPerformed(ActionEvent ev) {
                      applyButton_actionPerformed(ev);
                  }
              };

        previousActionListener =
              new ActionListener() {
                  public void actionPerformed(ActionEvent ev) {
                      previousButton_actionPerformed(ev);
                  }
              };

        nextActionListener =
              new ActionListener() {
                  public void actionPerformed(ActionEvent ev) {
                      nextButton_actionPerformed(ev);
                  }
              };
    }


    /**
     * Click sur le bouton Apply.
     */
    private void applyButton_actionPerformed(ActionEvent ev) {
        try {
            executeApply();
            refreshGenericTable();
        }
        catch (Exception ex) {
            ex.printStackTrace();
            ErrorDialog.show(getDesktopPane(), "Erreur", ex);
        }
    }


    /**
     * Recharge la genericTable.
     */
    private void refreshGenericTable() {
        try {
            int selectedIndex = getGenericTable().getSelectedRow();
            getGenericTable().refreshData();
            if (selectedIndex > (getGenericTable().getNumberOfRows() - 1)) {
                selectedIndex = (getGenericTable().getNumberOfRows() - 1);
            }

            getGenericTable().getSelectionModel().setSelectionInterval(selectedIndex,
                                                                       selectedIndex);
        }
        catch (SQLException ex) {
            ErrorDialog.show(getDesktopPane(), "Impossible de rafraichir " + "la liste",
                             ex);
        }
    }


    /**
     * Ferme la fenetre detail.
     */
    private void closeDetailWindow() {
        try {
            detailWindow.getInternalFrame().setClosed(true);
        }
        catch (java.beans.PropertyVetoException ex) {
        }
        detailWindow.getInternalFrame().setVisible(false);
        removeListeners();
    }


    /**
     * Click sur le bouton OK. Applique les modification et ferme la fenetre si tout c'est bien passe.
     */
    private void okButton_actionPerformed(ActionEvent ev) {
        try {
            executeOK();
            refreshGenericTable();
            closeDetailWindow();
        }
        catch (Exception ex) {
            ex.printStackTrace();
            ErrorDialog.show(getDesktopPane(), "Erreur", ex);
        }
    }


    private class GenericTableSelectionListener implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent e) {
            if (e.getValueIsAdjusting()) {
                return;
            }
            updateButtonState();
        }
    }
}
