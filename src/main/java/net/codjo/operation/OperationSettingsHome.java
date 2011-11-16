/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.operation;
import net.codjo.model.PortfolioGroupHome;
import net.codjo.model.TableHome;
import net.codjo.persistent.PersistenceException;
import net.codjo.persistent.Persistent;
import net.codjo.persistent.Reference;
import net.codjo.persistent.UnknownIdException;
import net.codjo.utils.QueryHelper;
import net.codjo.utils.SQLFieldList;
import net.codjo.utils.sql.event.DbChangeEvent;
import net.codjo.utils.sql.event.DbChangeListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
/**
 * Classe Home pour OperationSettings.
 * 
 * <p>
 * Cette classe utilise la table PM_OPERATION_SETTINGS.
 * </p>
 *
 * @author $Author: marcona $
 * @version $Revision: 1.5 $
 *
 */
public class OperationSettingsHome extends net.codjo.persistent.sql.AbstractHome {
    // Log
    private static final Logger APP = Logger.getLogger(OperationSettingsHome.class);
    private Map behaviorLoaders = new HashMap();
    private BehaviorMemoryManager memoryManager = null;
    private PortfolioGroupHome portfolioGroupHome;
    private TableHome tableHome;

    /**
     * Constructeur de TableHome
     *
     * @param con La connection pour le home.
     * @param tableHome Le Home des objets Table
     * @param portfolioGroupHome Le Home des objets PortfolioGroup
     *
     * @exception SQLException En cas d'erreur lors de l'acces a la BD.
     * @throws IllegalArgumentException TODO
     */
    public OperationSettingsHome(Connection con, TableHome tableHome,
        PortfolioGroupHome portfolioGroupHome) throws SQLException {
        super(con);
        // Preconditions
        if (tableHome == null || portfolioGroupHome == null) {
            throw new IllegalArgumentException();
        }

        // Init Home
        this.tableHome = tableHome;
        this.portfolioGroupHome = portfolioGroupHome;

        // Init SQL Helper
        SQLFieldList selectById = new SQLFieldList();
        selectById.addIntegerField("OPERATION_SETTINGS_ID");
        SQLFieldList tableFields = new SQLFieldList("PM_OPERATION_SETTINGS", con);
        queryHelper =
            new QueryHelper("PM_OPERATION_SETTINGS", con, tableFields, selectById);
    }

    /**
     * Ajoute un <code>BehaviorLoader</code> . Un BehaviorLoader est responsable d'un
     * comportement d'operation.
     *
     * @param loader Le loader a ajouter
     *
     * @see BehaviorLoader
     */
    public void addBehaviorLoader(BehaviorLoader loader) {
        behaviorLoaders.put(loader.getBehaviorID(), loader);
        loader.addDbChangeListener(new BehaviorDbChangeListener(loader));
    }


    /**
     * Retourne le <code>BehaviorLoader</code> responsable du type d'operation
     * <code>behaviorType</code> .
     *
     * @param behaviorType Le type d'operation (ex: "I");
     *
     * @return le BehaviorLoader ou null
     */
    public BehaviorLoader getBehaviorLoader(String behaviorType) {
        return (BehaviorLoader)behaviorLoaders.get(behaviorType);
    }


    /**
     * Retourne une Map des BehaviorLoader.
     *
     * @return Une Map : clef=OPERATION_Type(ex "I"), value = BehaviorLoader.
     */
    public Map getBehaviorLoaders() {
        return behaviorLoaders;
    }


    /**
     * Retourne un listener mettant a jours la couche de persistance au niveau de
     * TreatmentBehaviorHome lors des changements directe en Base.
     *
     * @return The DbChangeListener value
     */
    public DbChangeListener getDbChangeListener() {
        return new DefaultDbChangeListener();
    }


    /**
     * Retourne une reference avec ID.
     *
     * @param id OPERATION_SETTINGS_ID
     *
     * @return The Reference value
     */
    public Reference getReference(int id) {
        return getReference(new Integer(id));
    }


    /**
     * Retourne une reference sur l' <code>OperationSettings</code> assigne au
     * comportement.
     *
     * @param behaviorType Le type du comportement
     * @param behaviorId L'id du comportement
     *
     * @return La reference.
     *
     * @exception PersistenceException Si erreur acces, ou aucun OperationSettings n'est
     *            assigne a ce comportement.
     * @throws UnknownIdException TODO
     */
    public Reference getReferenceByBehaviorId(String behaviorType, int behaviorId)
            throws PersistenceException {
        try {
            Statement stmt = getConnection().createStatement();
            try {
                ResultSet rs =
                    stmt.executeQuery("select OPERATION_SETTINGS_ID"
                        + " from PM_OPERATION_SETTINGS" + " where OPERATION_TYPE='"
                        + behaviorType + "'" + "   and SETTINGS_ID =" + behaviorId);

                if (rs.next() == false) {
                    throw new UnknownIdException("ID inconnue : " + "type='"
                        + behaviorType + "'" + " , id='" + behaviorId + "'");
                }

                return getReference(rs.getInt("OPERATION_SETTINGS_ID"));
            }
            finally {
                stmt.close();
            }
        }
        catch (SQLException ex) {
            throw new PersistenceException(ex);
        }
    }


    /**
     * Description of the Method
     *
     * @param refreshDelay Description of the Parameter
     *
     * @throws IllegalStateException TODO
     */
    public void startMemoryCleanUp(long refreshDelay) {
        if (memoryManager != null) {
            throw new IllegalStateException();
        }
        memoryManager = new BehaviorMemoryManager(this, refreshDelay);
    }


    /**
     * DOCUMENT ME!
     *
     * @param ref
     *
     * @exception SQLException
     */
    protected void fillQueryHelperForInsert(Reference ref)
            throws SQLException {
        if (ref.getId() == null) {
            ref.setId(new Integer(queryHelper.getUniqueID()));
        }
        OperationSettings obj = (OperationSettings)ref.getLoadedObject();

        queryHelper.setInsertValue("OPERATION_SETTINGS_ID", ref.getId());
        queryHelper.setInsertValue("OPERATION_TYPE",
            obj.getBehaviorLoader().getBehaviorID());
        queryHelper.setInsertValue("PRIORITY", obj.getPriority());
        queryHelper.setInsertValue("SOURCE_TABLE_ID", obj.getSourceTable().getId());
        queryHelper.setInsertValue("DEST_TABLE_ID", obj.getDestTable().getId());
        queryHelper.setInsertValue("COMMENTRY", obj.getCommentry());
        queryHelper.setInsertValue("AUTOMATIC", obj.isAutomatic());
    }


    /**
     * DOCUMENT ME!
     *
     * @param ref
     */
    protected void fillQueryHelperSelector(Reference ref) {
        queryHelper.setSelectorValue("OPERATION_SETTINGS_ID", ref.getId());
    }


    /**
     * Charge un Settings.
     *
     * @param rs
     * @param ref
     *
     * @return
     *
     * @exception SQLException
     * @exception PersistenceException Impossible de recuperer le comportement.
     */
    protected Persistent loadObject(ResultSet rs, Reference ref)
            throws SQLException, PersistenceException {
        if (ref.isLoaded()) {
            return ref.getLoadedObject();
        }

        int operationId = ((Integer)ref.getId()).intValue();
        APP.debug("Chargement d'OperationSettings (id=" + operationId + ")");

        BehaviorLoader loader =
            (BehaviorLoader)behaviorLoaders.get(rs.getString("OPERATION_TYPE"));

        if (loader != null) {
            int tableSourceID;
            tableSourceID = rs.getInt("SOURCE_TABLE_ID");

            if (tableSourceID == 0) {
                return new OperationSettings(ref, rs.getInt("PRIORITY"), null,
                    tableHome.getReference(rs.getInt("DEST_TABLE_ID")),
                    rs.getString("COMMENTRY"), rs.getBoolean("AUTOMATIC"),
                    loader.getReference(rs.getInt("SETTINGS_ID")),
                    loadPortfolioGroupList(operationId), loader,
                    rs.getString("SELECT_CRITERIA"), rs.getString("DELETE_CRITERIA"));
            }
            else {
                return new OperationSettings(ref, rs.getInt("PRIORITY"),
                    tableHome.getReference(tableSourceID),
                    tableHome.getReference(rs.getInt("DEST_TABLE_ID")),
                    rs.getString("COMMENTRY"), rs.getBoolean("AUTOMATIC"),
                    loader.getReference(rs.getInt("SETTINGS_ID")),
                    loadPortfolioGroupList(operationId), loader,
                    rs.getString("SELECT_CRITERIA"), rs.getString("DELETE_CRITERIA"));
            }
        }
        else {
            throw new PersistenceException("Type d'operation non supporte : "
                + rs.getString("OPERATION_TYPE"));
        }
    }


    /**
     * Construction d'une reference a partir d'un ResultSet.
     *
     * @param rs Le ResultSet a utilise pour la construction.
     *
     * @return Une instance non null.
     *
     * @exception SQLException En cas d'erreur d'acces a la base
     */
    protected Reference loadReference(ResultSet rs)
            throws SQLException {
        return getReference(rs.getInt("OPERATION_SETTINGS_ID"));
    }


    /**
     * Retourne la liste de toutes les references du buffer.
     *
     * @return La valeur de buffer
     */
    Collection getBuffer() {
        return getReferences();
    }


    /**
     * Charge la liste des Groupes de portefeuille. La liste contient des references.
     *
     * @param settingsId L'ID du settings.
     *
     * @return Liste de reference.
     *
     * @exception SQLException -
     */
    private List loadPortfolioGroupList(int settingsId)
            throws SQLException {
        Statement stmt = null;
        List pfList = new java.util.ArrayList();

        try {
            stmt = getConnection().createStatement();
            ResultSet rs =
                stmt.executeQuery("select * from PM_GROUP_OPERATION_LINKS "
                    + " where OPERATION_SETTINGS_ID = " + settingsId);

            while (rs.next()) {
                pfList.add(portfolioGroupHome.getReference(rs.getInt(2)));
            }
        }
        finally {
            if (stmt != null) {
                stmt.close();
            }
        }

        return pfList;
    }

    /**
     * Classe faisant la maj de l'operation settings lors d'un changement d'un type de
     * comportement (Import, ...).
     *
     * @author $Author: marcona $
     * @version $Revision: 1.5 $
     */
    private class BehaviorDbChangeListener implements DbChangeListener {
        DbChangeListener homeListener = getDbChangeListener();
        Map key = new HashMap();
        BehaviorLoader loader;

        /**
         * DOCUMENT ME!
         *
         * @param loader Description of Parameter
         */
        BehaviorDbChangeListener(BehaviorLoader loader) {
            this.loader = loader;
        }

        /**
         * DOCUMENT ME!
         *
         * @param evt Description of Parameter
         */
        public void succeededChange(DbChangeEvent evt) {
            if (isBufferOn() == false) {
                return;
            }
            if (evt.getEventType() == DbChangeEvent.MODIFY_EVENT) {
                APP.debug("OperationSettingsHome (changement behavior) : " + evt);
                Integer settingsId = extractSettingsId(evt.getPrimaryKey());
                APP.debug("     settingsId  = " + settingsId);
                Integer operationSettingsId = findOperationSettingsId(settingsId);
                APP.debug("     operationSettingsId  = " + operationSettingsId);
                if (operationSettingsId != null) {
                    fireModifyChange(operationSettingsId);
                }
            }
        }


        /**
         * DOCUMENT ME!
         *
         * @param key la pk
         *
         * @return Le settingsId
         *
         * @throws RuntimeException TODO
         */
        private Integer extractSettingsId(Map key) {
            if (key.size() != 1) {
                throw new RuntimeException("Le behavior possede une "
                    + "clef primaire composite : " + key);
            }
            Map.Entry entry = (Map.Entry)key.entrySet().iterator().next();
            return (Integer)entry.getValue();
        }


        /**
         * Retourne l'Id de l'OperationSettingsId correspondant au behavior modifie.
         *
         * @param settingsId Le SETTINGS_ID
         *
         * @return le OPERATION_SETTINGS_ID ou null (si n'existe pas)
         */
        private Integer findOperationSettingsId(Integer settingsId) {
            for (Iterator iter = getReferences().iterator(); iter.hasNext();) {
                Reference ref = (Reference)iter.next();
                if (ref.isLoaded()) {
                    OperationSettings opSet = (OperationSettings)ref.getLoadedObject();
                    if (settingsId.equals(opSet.getBehaviorId())
                            && loader == opSet.getBehaviorLoader()) {
                        return (Integer)opSet.getId();
                    }
                }
            }
            return null;
        }


        /**
         * Lance un evt de modification sur un OperationSettings.
         *
         * @param opSetId le OPERATION_SETTINGS_ID
         */
        private void fireModifyChange(Integer opSetId) {
            key.put("OPERATION_SETTINGS_ID", opSetId);
            DbChangeEvent modifyEvt =
                new DbChangeEvent(this, DbChangeEvent.MODIFY_EVENT, key);
            homeListener.succeededChange(modifyEvt);
        }
    }


    /**
     * Classe offrant un comportement par defaut pour la mise a jours de ce Home, lors de
     * modification en directe de la BD.
     * 
     * <p>
     * Lors d'un delete La reference est supprime du buffer. Lors d'un "Modify" la
     * reference est decharge.
     * </p>
     *
     * @author $Author: marcona $
     * @version $Revision: 1.5 $
     */
    private class DefaultDbChangeListener implements DbChangeListener {
        /**
         * DOCUMENT ME!
         *
         * @param evt Description of Parameter
         */
        public void succeededChange(DbChangeEvent evt) {
            if (isBufferOn() == false) {
                return;
            }
            APP.debug("OperationSettingsHome : " + evt.toString());
            Integer id = (Integer)evt.getPrimaryKey().get("OPERATION_SETTINGS_ID");
            Reference ref;
            switch (evt.getEventType()) {
                case DbChangeEvent.DELETE_EVENT:
                    ref = getReference(id.intValue());
                    ref.unload();
                    removeReference(ref);
                    break;
                case DbChangeEvent.MODIFY_EVENT:
                    ref = getReference(id.intValue());
                    ref.unload();
                    try {
                        ref.getObject();
                    }
                    catch (PersistenceException ex) {
                        // cas improbable (de toute facon a ce niveau on ne peut
                        // rien faire)
                    }
                    break;
            }
        }
    }
}
