/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.utils.sql;
import net.codjo.utils.QueryHelper;
import net.codjo.utils.SQLFieldList;
import net.codjo.utils.sql.event.DbChangeEvent;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.UIManager;
/**
 * Affiche un ecran page pour ajouter un enregistrement a une table.
 *
 * @author $Author: marcona $
 * @version $Revision: 1.5 $
 *
 */
public class AddAction extends AbstractDetailAction {
    /**
     * Type de clef primaire "Auto". C'est une clef primaire renseignee par
     * automatiquement par un algorithme logicielle.
     *
     * @see net.codjo.utils.QueryHelper#getUniqueID
     */
    public static final int PK_AUTOMATIC = 1;
    /**
     * Type de clef Primaire "identity".C'est une clef primaire renseignee
     * automatiquement par Sybase.
     */
    public static final int PK_IDENTITY = 0;
    /**
     * Type de clef primaire "Manuel". C'est une clef primaire renseignee par
     * l'utilisateur dans la fenetre de detail.
     */
    public static final int PK_MANUAL = 2;
    private SQLFieldList editableFields;
    private ModifyAction modifyAction = null;
    private int pkType = PK_IDENTITY;

    /**
     * Constructor for the AddAction object
     */
    public AddAction() {
        actionType = "Add";
        putValue(NAME, "Ajouter");
        putValue(SHORT_DESCRIPTION, "Ajout d'un enregistrement");
        putValue(SMALL_ICON, UIManager.getIcon("ListTable.add"));
    }


    /**
     * Constructor for the AddAction object
     *
     * @param dp DesktopPane
     * @param frm Fenêtre source (écran liste)
     * @param gt La table source
     * @param packName Nom du package dans lequel se trouve la classe de l'écran détail
     */
    public AddAction(JDesktopPane dp, JInternalFrame frm, GenericTable gt, String packName) {
        super(dp, frm, gt, packName);
        actionType = "Add";
        putValue(NAME, "Ajouter");
        putValue(SHORT_DESCRIPTION, "Ajout d'un enregistrement");
        putValue(SMALL_ICON, UIManager.getIcon("ListTable.add"));
        setEnabled(isActivatable());
    }

    /**
     * Appui sur le bouton
     *
     * @param ev Description of the Parameter
     */
    public void actionPerformed(ActionEvent ev) {
        getGenericTable().getSelectionModel().clearSelection();
        super.actionPerformed(ev);
    }


    /**
     * Mise à jour des boutons Précédent/Suivant
     */
    protected void updateButtonState() {
        detailWindow.getPreviousButton().setEnabled(false);
        detailWindow.getNextButton().setEnabled(false);
    }


    /**
     * Insere les valeurs dans la table
     *
     * @exception Exception Description of Exception
     */
    protected void executeAction() throws Exception {
        Connection con = getConnection();
        Map pk = null;
        try {
            QueryHelper queryHelper = buildQueryHelper(con);
            con.setAutoCommit(false);
            detailWindow.fillQueryHelper(editableFields, queryHelper);

            pk = new HashMap();
            List listPK = getTable().getPkNames();
            if (getPkType() == PK_AUTOMATIC) {
                String keyName = (String)listPK.get(0);
                Integer value = new Integer(queryHelper.getUniqueID());
                queryHelper.setInsertValue(keyName, value);
                pk.put(keyName, value);
            }
            else if (getPkType() == PK_MANUAL) {
                for (Iterator iter = listPK.iterator(); iter.hasNext();) {
                    String keyName = (String)iter.next();
                    Object value = queryHelper.getInsertValue(keyName);
                    pk.put(keyName, value);
                }
            }

            java.math.BigDecimal idIdentity = queryHelper.doInsert();

            if (getPkType() == PK_IDENTITY) {
                String keyName = (String)listPK.get(0);
                pk.put(keyName, idIdentity);
            }

            detailWindow.saveLinks(pk, con);

            con.commit();
            setGenericTablePK(pk);
        }
        catch (Exception ex) {
            con.rollback();
            throw ex;
        }
        finally {
            con.setAutoCommit(true);
            releaseConnection(con);
        }
        fireDbChange(DbChangeEvent.ADD_EVENT, pk);
    }


    /**
     * Basculement en mode modification si l'action connait un ModifyAction.
     *
     * @exception Exception
     */
    protected void executeApply() throws Exception {
        super.executeApply();
        if (modifyAction != null) {
            removeListeners();
            modifyAction.setGenericTablePK(getGenericTablePK());
            modifyAction.actionPerformed(detailWindow);
            modifyAction.loadData();
        }
    }


    /**
     * Positionne l'attribut modifyAction.
     *
     * @param modifyAction
     */
    void setModifyAction(ModifyAction modifyAction) {
        this.modifyAction = modifyAction;
    }


    /**
     * Sets the PkType attribute of the AddAction object
     *
     * @param pkt The new PkType value
     *
     * @throws IllegalArgumentException Argument illegale
     */
    void setPkType(int pkt) {
        if (pkt < 0 || pkt > 2) {
            throw new IllegalArgumentException();
        }
        pkType = pkt;
    }


    /**
     * Construit la requête d'insert pour les champs présents dans la fenêtre.
     *
     * @param con une connection valide
     *
     * @return Description of the Returned Value
     *
     * @exception SQLException Erreur SQL
     */
    private QueryHelper buildQueryHelper(Connection con)
            throws SQLException {
        List componentList = detailWindow.getListOfComponents();
        editableFields = getEditableFields(componentList);
        SQLFieldList fieldsToBeInserted = new SQLFieldList();
        fieldsToBeInserted.addAll(editableFields);

        SQLFieldList selector = new SQLFieldList();

        List pkList = getTable().getPkNames();
        Map allColumns = getTable().getAllColumns();
        for (Iterator iter = pkList.iterator(); iter.hasNext();) {
            String dbField = (String)iter.next();
            Integer sqlType = (Integer)allColumns.get(dbField);
            selector.addField(dbField, sqlType.intValue());
        }

        if (getPkType() != PK_IDENTITY) {
            fieldsToBeInserted.addAll(selector);
        }

        return new QueryHelper(getDbTableName(), con, fieldsToBeInserted, selector);
    }


    /**
     * Retourne le type de clef primaire.
     *
     * @return The PkType value
     */
    private int getPkType() {
        return pkType;
    }


    /**
     * Action toujours activable des lors que la fenetre de detail existe.
     *
     * @return 'true' si elle peut etre activee
     */
    private boolean isActivatable() {
        return (getWindowClass() != null);
    }
}
