/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.utils.sql;
import net.codjo.profile.User;
import net.codjo.utils.sql.event.DbChangeListener;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TooManyListenersException;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
/**
 * Toolbar contenant les actions DB attache a une table.
 *
 * <p> Cette Toolbar contient les actions suivantes : Ajouter, Modifier, Dupliquer, Supprimer, Rechercher,
 * Tout afficher, Quitter </p>
 *
 * <p> Cette ToolBar rajoute un popup sur la table avec les actions : Modifier, Dupliquer, Supprimer </p>
 *
 * <p></p>
 *
 * @author $Author: marcona $
 * @version $Revision: 1.7 $
 */
public class DbToolBar extends JToolBar {
    // Constante ACTIONS
    public static final int FIND = 0;
    public static final int SHOW = 1;
    public static final int CLOSE = 2;
    public static final int MODIFY = 3;
    public static final int DUPLICATE = 4;
    public static final int DELETE = 5;
    public static final int ADD = 6;
    public static final int SEPARATOR = 7;
    public static final int PREVIOUS = 8;
    public static final int NEXT = 9;
    public static final int PRINT = 10;
    public static final int EXPORT = 11;
    public static final int FORCE = 12;
    static final int[] ALL_ACTIONS =
          {
                PREVIOUS, NEXT, SEPARATOR, EXPORT, PRINT, SEPARATOR, ADD, MODIFY, DUPLICATE,
                DELETE, SEPARATOR, FIND, SHOW, SEPARATOR, CLOSE
          };
    static final int[] ALL_ACTIONS_NO_CLOSE =
          {
                PREVIOUS, NEXT, SEPARATOR, EXPORT, PRINT, SEPARATOR, ADD, MODIFY, DUPLICATE,
                DELETE, SEPARATOR, FIND, SHOW
          };
    AddAction addAction;
    List allActions = new ArrayList();
    CloseAction closeAction;
    DeleteAction deleteAction;
    ForceRecordAction forceAction;

    // Divers
    JDesktopPane desktopPane;
    DuplicateAction duplicateAction;

    // Action
    ExportAction exportAction;
    FindAction findAction;
    GenericTable genericTable;
    JInternalFrame internalFrame;
    ModifyAction modifyAction;
    NextPageAction nextPageAction;
    String packageName;

    // Popup
    JPopupMenu popupMenu = new JPopupMenu();
    PreviousPageAction previousPageAction;
    PrintAction printAction;
    ShowAllAction showAllAction;
    public static final String USER_PROFILE_KEY = "user.profile";


    /**
     * Constructor for designer.
     */
    public DbToolBar() {
        add(new JButton(new ImageIcon("../images/Previous.gif")));
        add(new JButton(new ImageIcon("../images/Next.gif")));
        addSeparator();
        add(new JButton(new ImageIcon("../images/Add.gif")));
        add(new JButton(new ImageIcon("../images/Edit.gif")));
        add(new JButton(new ImageIcon("../images/Delete.gif")));
        add(new JButton(new ImageIcon("../images/force.gif")));
        addSeparator();

        add(new JButton(new ImageIcon("../images/Find.gif")));
        add(new JButton(new ImageIcon("../images/ShowAll.gif")));
        addSeparator();
        add(new JButton(new ImageIcon("../images/Close.gif")));
    }


    /**
     * Constructor for the DbToolBar object
     *
     * @param dp       Le DesktopPane
     * @param gt       La GenericTable source
     * @param jf       L'InternelFrame sur laquelle on ajoute cette ToolBar
     * @param packName Nom du package dans lequel se trouve la classe de l'écran détail
     */
    public DbToolBar(JDesktopPane dp, GenericTable gt, JInternalFrame jf, String packName) {
        this(dp, gt, jf, packName, ALL_ACTIONS);
    }


    /**
     * Constructor for the DbToolBar object
     *
     * @param dp          Le DesktopPane
     * @param gt          La GenericTable source
     * @param jf          L'InternelFrame sur laquelle on ajoute cette ToolBar
     * @param packName    Nom du package dans lequel se trouve la classe de l'écran détail
     * @param closeButton Affiche ou non le bouton "Fermer"
     */
    public DbToolBar(JDesktopPane dp, GenericTable gt, JInternalFrame jf,
                     String packName, boolean closeButton) {
        this(dp, gt, jf, packName, ((closeButton) ? ALL_ACTIONS : ALL_ACTIONS_NO_CLOSE));
    }


    /**
     * Constructor for the DbToolBar object
     *
     * @param dp       Le DesktopPane
     * @param gt       La GenericTable source
     * @param jf       L'InternelFrame sur laquelle on ajoute cette ToolBar
     * @param packName Nom du package dans lequel se trouve la classe de l'écran détail
     * @param actions  Liste des actions a definir dans la toolbar
     */
    public DbToolBar(JDesktopPane dp, GenericTable gt, JInternalFrame jf,
                     String packName, int[] actions) {
        desktopPane = dp;
        genericTable = gt;
        internalFrame = jf;
        packageName = packName;
        try {
            jbInit();
            initPopup();
            init(actions);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /**
     * Constructor for the DbToolBar object
     *
     * @param dp       Le DesktopPane
     * @param gt       La GenericTable source
     * @param jf       L'InternelFrame sur laquelle on ajoute cette ToolBar
     * @param packName Nom du package dans lequel se trouve la classe de l'écran détail
     * @param actions  Liste des actions a definir dans la toolbar
     * @param popUp    Description of the Parameter
     */
    public DbToolBar(JDesktopPane dp, GenericTable gt, JInternalFrame jf,
                     String packName, int[] actions, boolean popUp) {
        desktopPane = dp;
        genericTable = gt;
        internalFrame = jf;
        packageName = packName;
        try {
            jbInit();
            if (popUp) {
                initPopup();
            }
            else {
                initRightClick();
            }
            init(actions);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /**
     * Ajoute le DbChangeListener sur toutes les actions definies dans la ToolBar.
     *
     * @param l Le listener
     *
     * @throws TooManyListenersException Description of Exception
     */
    public void add(DbChangeListener l) throws TooManyListenersException {
        for (Iterator iter = allActions.iterator(); iter.hasNext();) {
            AbstractDbAction action = (AbstractDbAction)iter.next();
            action.addDbChangeListener(l);
        }
    }


    /**
     * Ajoute un listener qui ecoute le declenchement des actions de la toolbar.
     *
     * @param l
     */
    public void addActionListener(ActionListener l) {
        for (Iterator iter = allActions.iterator(); iter.hasNext();) {
            AbstractDbAction action = (AbstractDbAction)iter.next();
            action.addActionListener(l);
        }
    }


    /**
     * Gets the popupMenu attribute of the DbToolBar object
     *
     * @return The popupMenu value
     */
    public JPopupMenu getPopupMenu() {
        return popupMenu;
    }


    /**
     * Renseigne une valeur par défaut pour un champ de l'écran de détail lors d'un ajout ou d'une
     * modification.
     *
     * @param columnName  Le nom du champ
     * @param columnValue La valeur du champ
     */
    public void putDefaultValueForDetail(String columnName, Object columnValue) {
        if (addAction != null) {
            addAction.setDefaultValue(columnName, columnValue);
        }
        if (modifyAction != null) {
            modifyAction.setDefaultValue(columnName, columnValue);
        }
    }


    /**
     * Enleve un listener.
     *
     * @param l
     *
     * @see #addActionListener
     */
    public void removeActionListener(ActionListener l) {
        for (Iterator iter = allActions.iterator(); iter.hasNext();) {
            AbstractDbAction action = (AbstractDbAction)iter.next();
            action.removeActionListener(l);
        }
    }


    /**
     * Renseigne le message de confirmation pour l'action de suppression.
     *
     * @param msg Le message
     */
    public void setConfirmMsg(String msg) {
        if (deleteAction != null) {
            deleteAction.setConfirmMsg(msg);
        }
        if (forceAction != null) {
            forceAction.setConfirmMsg(msg);
        }
    }


    /**
     * Renseigne une valeur par défaut pour l'action de recherche
     *
     * @param impliciteClause The new DefaultValueforFindAction value
     */
    public void setDefaultValueforFindAction(String impliciteClause) {
        if (findAction != null) {
            findAction.setDefaultValue(impliciteClause);
        }
    }


    /**
     * Renseigne une clause obligatoire pour le requeteur
     *
     * @param mandatoryClause clause obligatoire
     */
    public void setMandatoryClauseforFindAction(String mandatoryClause) {
        if (findAction != null) {
            findAction.setMandatoryClause(mandatoryClause);
        }
    }


    /**
     * Renseigne une valeur par défaut pour l'action tout afficher (prise en compte des valeurs des filtres de
     * l'explorateur)
     *
     * @param impliciteClause The new DefaultValueforFindAction value
     */
    public void setDefaultValueforShowAllAction(String impliciteClause) {
        if (showAllAction != null) {
            showAllAction.setDefaultValue(impliciteClause);
        }
    }


    /**
     * Sets the Connection attribute of the DbToolBar object
     *
     * @param connection The new Connection value
     */
    void setConnection(Connection connection) {
        for (Iterator iter = allActions.iterator(); iter.hasNext();) {
            AbstractDbAction action = (AbstractDbAction)iter.next();
            action.setConnection(connection);
        }
    }


    /**
     * Gère le double-clique sur une ligne afin d'afficher l'écran détail
     *
     * @param e L'événement souris
     */
    void tableMouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2 && modifyAction.isEnabled()) {
            modifyAction.actionPerformed(new ActionEvent(this, 0, "Modification"));
        }
    }


    /**
     * Gère la sélection d'une ligne sur la GenericTable
     *
     * @param e L'événement souris
     */
    void tableMousePressed(MouseEvent e) {
        if (javax.swing.SwingUtilities.isRightMouseButton(e)) {
            int row = genericTable.rowAtPoint(e.getPoint());
            if (row != -1) {
                genericTable.setRowSelectionInterval(row, row);
            }
        }
        maybeShowPopup(e);
    }


    /**
     * Reduire la taille du bouton
     *
     * @param tb La toolbar
     */
    private static void doEffect(JToolBar tb) {
        java.awt.Component component;
        int i = 0;
        do {
            component = tb.getComponentAtIndex(i);
            if (component instanceof JButton) {
                ((JButton)component).setMargin(new java.awt.Insets(1, 3, 1, 3));
            }
            i++;
        }
        while (component != null);
    }


    /**
     * Ajoute une AbstractDbAction a la toolbar.
     *
     * @param action Description of Parameter
     */
    private void addDbAction(AbstractDbAction action) {
        allActions.add(action);
        addAction(action);
    }


    private AbstractDbAction manageSecurity(AbstractDbAction action) {
        User user = (User)desktopPane.getClientProperty(USER_PROFILE_KEY);
        if (user == null) {
            return action;
        }
        String className = action.getClass().getSimpleName();
        String function = className.substring(0, className.length() - "Action".length()).toLowerCase()
                          + "." + genericTable.getTable().getDBTableName();
        action.setForcedDisabled(!user.isAllowedTo(function));
        return action;
    }


    /**
     * Construit les actions
     *
     * @param actions Description of Parameter
     *
     * @throws IllegalArgumentException TODO
     */
    private void init(int[] actions) {
        if (BcpOutAction.isInited() && "ON".equals(System.getProperty("DEBUG"))) {
            addAction(new BcpOutAction(genericTable.getTable().getDBTableName()));
            addSeparator();
        }

        for (int i = 0; i < actions.length; i++) {
            switch (actions[i]) {
                case EXPORT:
                    exportAction = new ExportAction(genericTable);
                    addAction(exportAction);
                    break;
                case PRINT:
                    printAction = new PrintAction(genericTable);
                    addAction(printAction);
                    break;
                case PREVIOUS:
                    previousPageAction = new PreviousPageAction(genericTable);
                    addAction(previousPageAction);
                    break;
                case NEXT:
                    nextPageAction = new NextPageAction(genericTable);
                    addAction(nextPageAction);
                    break;
                case ADD:
                    addAction =
                          new AddAction(desktopPane, internalFrame, genericTable, packageName);
                    addAction.setModifyAction(modifyAction);
                    addDbAction(manageSecurity(addAction));
                    break;
                case MODIFY:
                    modifyAction =
                          new ModifyAction(desktopPane, internalFrame, genericTable, packageName);
                    if (addAction != null) {
                        addAction.setModifyAction(modifyAction);
                    }
                    addDbAction(manageSecurity(modifyAction));
                    popupMenu.add(modifyAction);
                    break;
                case FIND:
                    findAction = new FindAction(desktopPane, internalFrame, genericTable);
                    addDbAction(findAction);
                    break;
                case SHOW:
                    showAllAction = new ShowAllAction(genericTable);
                    addDbAction(showAllAction);
                    break;
                case CLOSE:
                    closeAction = new CloseAction(internalFrame);
                    addDbAction(closeAction);
                    break;
                case DUPLICATE:
                    duplicateAction = new DuplicateAction(genericTable);
                    addDbAction(manageSecurity(duplicateAction));
                    popupMenu.add(duplicateAction);
                    break;
                case DELETE:
                    deleteAction = new DeleteAction(genericTable);
                    addDbAction(manageSecurity(deleteAction));
                    popupMenu.add(deleteAction);
                    break;
                case FORCE:
                    forceAction = new ForceRecordAction(genericTable);
                    addDbAction(manageSecurity(forceAction));
                    popupMenu.add(forceAction);
                    break;
                case SEPARATOR:
                    addSeparator();
                    break;
                default:
                    throw new IllegalArgumentException();
            }
            setNameForPopUpMenuForTest();
            doEffect(this);
        }
    }


    private void setNameForPopUpMenuForTest() {
        try {
            Component[] component = popupMenu.getComponents();
            for (Component aComponent : component) {
                if (aComponent instanceof JMenuItem) {
                    aComponent.setName("popUpMenu." +
                                       ((JMenuItem)aComponent).getAction().getValue(AbstractAction.NAME));
                }
            }
        }
        catch (Exception ex) {
            //Modification pour les tests IHM, ne doit pas faire planter la methode !
        }
    }


    /**
     * Ajoute l'action et fait setName()
     *
     * @param dbAction
     */
    private void addAction(AbstractAction dbAction) {
        try {
            add(dbAction).setName(genericTable.getTable().getTableName() + "."
                                  + dbAction.getValue(AbstractAction.NAME));
        }
        catch (Exception e) {
            add(dbAction);
        }
    }


    /**
     * Récupère une action à partir de son identifiant
     *
     * @param actionId
     *
     * @return
     */
    public AbstractAction getAction(int actionId) {
        switch (actionId) {
            case EXPORT:
                return exportAction;
            case PRINT:
                return printAction;
            case PREVIOUS:
                return previousPageAction;
            case NEXT:
                return nextPageAction;
            case ADD:
                return addAction;
            case MODIFY:
                return modifyAction;
            case FIND:
                return findAction;
            case SHOW:
                return showAllAction;
            case CLOSE:
                return closeAction;
            case DUPLICATE:
                return duplicateAction;
            case DELETE:
                return deleteAction;
            case FORCE:
                return forceAction;
            default:
                throw new IllegalArgumentException();
        }
    }


    /**
     * Init un popup sur la genericTable.
     */
    private void initPopup() {
        MouseListener l =
              new java.awt.event.MouseAdapter() {
                  /**
                   * DOCUMENT ME!
                   *
                   * @param e Description of Parameter
                   */
                  public void mouseClicked(MouseEvent e) {
                      tableMouseClicked(e);
                  }


                  /**
                   * DOCUMENT ME!
                   *
                   * @param e Description of Parameter
                   */
                  public void mousePressed(MouseEvent e) {
                      tableMousePressed(e);
                  }


                  /**
                   * DOCUMENT ME!
                   *
                   * @param e Description of Parameter
                   */
                  public void mouseReleased(MouseEvent e) {
                      maybeShowPopup(e);
                  }
              };
        genericTable.addMouseListener(l);
    }


    /**
     * Init un clic droit sur la genericTable.
     */
    private void initRightClick() {
        MouseListener l =
              new java.awt.event.MouseAdapter() {
                  /**
                   * DOCUMENT ME!
                   *
                   * @param e Description of Parameter
                   */
                  public void mousePressed(MouseEvent e) {
                      if (javax.swing.SwingUtilities.isRightMouseButton(e)) {
                          int row = genericTable.rowAtPoint(e.getPoint());
                          if (row != -1) {
                              genericTable.setRowSelectionInterval(row, row);
                          }
                      }
                  }
              };
        genericTable.addMouseListener(l);
    }


    /**
     * Init GUI.
     *
     * @throws Exception
     */
    private void jbInit() throws Exception {
        setLayout(new FlowLayout(FlowLayout.RIGHT));
        setFloatable(false);
    }


    /**
     * Affiche le popupMenu si necessaire
     *
     * @param e L'événement souris
     */
    private void maybeShowPopup(MouseEvent e) {
        if (e.isPopupTrigger()) {
            popupMenu.show(e.getComponent(), e.getX(), e.getY());
        }
    }
}
