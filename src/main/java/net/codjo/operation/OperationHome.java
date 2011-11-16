/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.operation;
// Penelope
import net.codjo.model.Period;
import net.codjo.model.PeriodHome;
import net.codjo.model.PortfolioGroup;
import net.codjo.model.PortfolioGroupHome;
import net.codjo.persistent.PersistenceException;
import net.codjo.persistent.Persistent;
import net.codjo.persistent.Reference;
import net.codjo.utils.QueryHelper;
import net.codjo.utils.SQLFieldList;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
/**
 * Class Home pour les objets ImportOperation.
 * 
 * <p>
 * Cette classe utilis les tables AP_OPERATION.
 * </p>
 *
 * @author $Author: marcona $
 * @version $Revision: 1.6 $
 *
 */
public class OperationHome extends net.codjo.persistent.sql.AbstractHome {
    // Log
    private static final Logger APP = Logger.getLogger(OperationHome.class);
    private static final Logger USER = Logger.getLogger("journal");

    // AnomalyReport prototype : Comportement par defaut Source
    private AnomalyReport anomalyReportProto = new SourceAnomalyReport();
    private PeriodHome periodHome;
    private PortfolioGroupHome portfolioGroupHome;
    private OperationSettingsHome settingsHome;

    /**
     * Constructor for the ImportOperationHome object
     *
     * @param con Connection du home
     * @param perHome Home des objets Period
     * @param setHome Home des objets OperationSettings
     * @param pfGrpHome Description of Parameter
     *
     * @exception SQLException Description of Exception
     * @throws IllegalArgumentException TODO
     */
    public OperationHome(Connection con, PeriodHome perHome,
        OperationSettingsHome setHome, PortfolioGroupHome pfGrpHome)
            throws SQLException {
        super(con);
        if ((perHome == null) || (setHome == null)) {
            throw new IllegalArgumentException();
        }
        periodHome = perHome;
        settingsHome = setHome;
        portfolioGroupHome = pfGrpHome;

        SQLFieldList selectById = new SQLFieldList();
        selectById.addIntegerField("OPERATION_SETTINGS_ID");
        selectById.addStringField("PERIOD");
        selectById.addStringField("PORTFOLIO_GROUP");

        SQLFieldList tableFields = new SQLFieldList("AP_OPERATION", con);

        queryHelper = new QueryHelper("AP_OPERATION", con, tableFields, selectById);
        //On désactive le buffer pour le suivi des opérations
        //Permet le raffraichissement du formulaire de suivi
        //à optimiser plus tard
        setBufferOn(false);
    }

    /**
     * Positionne le prototype des AnomalyReport. Toutes les operations utiliseront une
     * copie de ce prototype pour la gestion des anomalies.
     *
     * @param proto Le prototype d'AnomalyReport
     *
     * @throws IllegalArgumentException TODO
     */
    public void setAnomalyReportPrototype(AnomalyReport proto) {
        if (proto == null) {
            throw new IllegalArgumentException();
        }
        this.anomalyReportProto = proto;
    }


    /**
     * Retourne toutes les opération d'import pour la période.
     *
     * @param period La période de filtre.
     *
     * @return Une liste de Reference
     *
     * @exception PersistenceException Description of Exception
     * @throws IllegalArgumentException TODO
     */
    public List getAllObjects(Period period) throws PersistenceException {
        if (period == null) {
            throw new IllegalArgumentException();
        }

        List allOperations;
        try {
            allOperations = loadAllOperations(period);
        }
        catch (SQLException ex) {
            throw new PersistenceException(ex);
        }

        return allOperations;
    }


    /**
     * Retourne toutes les opération d'import pour la période.
     *
     * @param period La période de filtre.
     *
     * @return Une liste de toutes les opérations
     *
     * @exception PersistenceException Description of Exception
     * @throws IllegalArgumentException TODO
     *
     * @see #getAllObjects()
     */
    public List getAllOperation(Period period) throws PersistenceException {
        if (period == null) {
            throw new IllegalArgumentException();
        }

        List allOperations;
        try {
            allOperations = loadAllOperationsBAD(period);
        }
        catch (SQLException ex) {
            throw new PersistenceException(ex);
        }

        return allOperations;
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
        return getOperationSettingsHome().getBehaviorLoader(behaviorType);
    }


    /**
     * Gets the OperationSettingsHome attribute of the OperationHome object
     *
     * @return The OperationSettingsHome value
     */
    public OperationSettingsHome getOperationSettingsHome() {
        return settingsHome;
    }


    /**
     * Retourne une reference avec ID.
     *
     * @param period PERIOD
     * @param pfGroup PORTFOLIO_GROUP
     * @param opeId OPERATION_SETTINGS_ID
     *
     * @return The Reference value
     */
    public Reference getReference(String period, String pfGroup, int opeId) {
        return getReference(new OperationPK(period, pfGroup, opeId));
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
        getOperationSettingsHome().addBehaviorLoader(loader);
    }


    /**
     * Génère des lignes d'opération à partir d'un objet Period
     *
     * @param period La période (Objet Period)
     *
     * @exception PersistenceException Si impossible.
     */
    public void instanciateOperations(Period period)
            throws PersistenceException {
        USER.info("Creation d'une nouvelle periode : " + period);
        APP.debug("Creation d'une nouvelle periode : " + period);

        List newOperationList = new ArrayList();
        try {
            getConnection().setAutoCommit(false);
            instanciateOperationsForPeriod(period, newOperationList);
            getConnection().commit();
            getConnection().setAutoCommit(true);
        }
        catch (PersistenceException ex) {
            cancelNewPeriod(newOperationList, ex);
            throw ex;
        }
        catch (Exception ex) {
            cancelNewPeriod(newOperationList, ex);
            throw new PersistenceException(ex, "Impossible d'instancier la periode");
        }
    }


    /**
     * Suppression des opérations d'une période
     *
     * @param period Description of the Parameter
     *
     * @throws PersistenceException TODO
     */
    public void deleteOperationsForPeriod(Period period)
            throws PersistenceException {
        List operationList = getAllObjects(period);
        for (Object anOperationList : operationList) {
            Reference obj = (Reference)anOperationList;
            this.delete(obj);
        }
    }


    /**
     * Prepare une insertion.
     *
     * @param ref La reference
     *
     * @exception SQLException
     */
    @Override
    protected void fillQueryHelperForInsert(Reference ref)
            throws SQLException {
        APP.debug("fillQueryHelperForInsert : " + ref.toString());
        Operation obj = (Operation)ref.getLoadedObject();

        if (ref.getId() == null) {
            Integer settingsId = (Integer)obj.getOperationSettings().getId();
            ref.setId(new OperationPK(obj.getPeriod().getPeriod(),
                    obj.getPortfolioGroupName(), settingsId.intValue()));
        }

        queryHelper.setInsertValue("PERIOD", obj.getPeriod().getPeriod());
        queryHelper.setInsertValue("PORTFOLIO_GROUP", obj.getPortfolioGroupName());
        queryHelper.setInsertValue("STATUS", obj.getOperationState().getState());
        queryHelper.setInsertValue("OPERATION_DATE", obj.getOperationState().getDate());
        queryHelper.setInsertValue("OPERATION_SETTINGS_ID",
            obj.getOperationSettings().getId());
    }


    /**
     * Prepare une selection.
     *
     * @param ref La reference
     */
    @Override
    protected void fillQueryHelperSelector(Reference ref) {
        APP.debug("fillQueryHelperSelector : " + ref.toString());
        OperationPK pk = (OperationPK)ref.getId();

        queryHelper.setSelectorValue("PERIOD", pk.period);
        queryHelper.setSelectorValue("PORTFOLIO_GROUP", pk.portfolioGroup);
        queryHelper.setSelectorValue("OPERATION_SETTINGS_ID", pk.operationSettingsId);
    }


    /**
     * Charge un objet.
     *
     * @param rs
     * @param ref
     *
     * @return
     *
     * @exception SQLException
     * @exception PersistenceException
     */
    protected Persistent loadObject(ResultSet rs, Reference ref)
            throws SQLException, PersistenceException {
        if (ref.isLoaded()) {
            return ref.getLoadedObject();
        }

        Operation ope =
            new Operation(ref, periodHome.getReference(rs.getString("PERIOD")),
                rs.getString("PORTFOLIO_GROUP"),null,
                settingsHome.getReference(rs.getInt("OPERATION_SETTINGS_ID")),
                newAnomalyReport());

        ope.setStored();
        ope.setOperationState(new OperationState(rs.getTimestamp("OPERATION_DATE"),
                rs.getInt("STATUS")));

        // @ugly : repositionne l'etat de l'objet fausse par le set ci-dessus.
        ope.setSynchronized(true);

        return ope;
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
        return getReference(rs.getString("PERIOD"), rs.getString("PORTFOLIO_GROUP"),
            rs.getInt("OPERATION_SETTINGS_ID"));
    }


    /**
     * Annulation de l'instantiation d'une nouvelle Periode.
     *
     * @param newOperationList La liste de Reference des Operations crée
     * @param ex L'exception ayant interrompue le process
     */
    private void cancelNewPeriod(List newOperationList, Exception ex) {
        USER.error("Impossible d'instancier la periode", ex);
        APP.error("Impossible d'instancier la periode", ex);
        for (Iterator iter = newOperationList.iterator(); iter.hasNext();) {
            Reference obj = (Reference)iter.next();
            removeReference(obj);
        }
        try {
            getConnection().rollback();
            getConnection().setAutoCommit(true);
        }
        catch (SQLException ee) {
            // Erreur lors du Rollback : on est mal, tout par en cou..
            ee.printStackTrace();
        }
    }


    /**
     * Instanciation des operations pour une nouvelle Periode.
     *
     * @param newPeriod La nouvelle Periode
     * @param newOperationList La liste ou les nouvelles Operations sont mise
     *
     * @exception PersistenceException
     * @exception SQLException
     */
    private void instanciateOperationsForPeriod(Period newPeriod, List newOperationList)
            throws PersistenceException, SQLException {
        List listRealPortfolioGroup = portfolioGroupHome.getAllRealPortfolioGroup();

        Statement stmt = getConnection().createStatement();
        try {
            ResultSet rs =
                stmt.executeQuery("select OS.OPERATION_SETTINGS_ID, P.PORTFOLIO_GROUP "
                    + "from PM_OPERATION_SETTINGS OS, PM_GROUP_OPERATION_LINKS OL, AP_PORTFOLIO_GROUP P "
                    + "where OS.OPERATION_SETTINGS_ID = OL.OPERATION_SETTINGS_ID "
                    + "and OL.PORTFOLIO_GROUP_ID = P.PORTFOLIO_GROUP_ID");
            while (rs.next()) {
//                APP.debug("Traitement settings : " + rs.getInt(1));

                if ("TOUT".equals(rs.getString(2))) {
//                    APP.debug("PortfolioGroup : TOUT");
                    for (int i = 0; i < listRealPortfolioGroup.size(); i++) {
//                        APP.debug("               : " + listRealPortfolioGroup.get(i));
                        Reference ref =
                            newOperation(rs.getInt(1), newPeriod,
                                (PortfolioGroup)((Reference)listRealPortfolioGroup.get(i))
                                .getObject());
                        newOperationList.add(ref);
                    }
                }
                else {
//                    APP.debug("PortfolioGroup : " + rs.getString(2));
                    Reference ref =
                        newOperation(rs.getInt(1), newPeriod,
                            portfolioGroupHome.getPortfolioGroup(rs.getString(2)));
                    newOperationList.add(ref);
                }
            }
        }
        finally {
            stmt.close();
        }
    }


    /**
     * Charge toutes les operations.
     *
     * @param period -
     *
     * @return La liste de reference
     *
     * @exception PersistenceException -
     * @exception SQLException -
     */
    private List loadAllOperations(Period period)
            throws PersistenceException, SQLException {
        List allOperations = new ArrayList();
        Statement stmt = getConnection().createStatement();
        try {
            ResultSet rs =
                stmt.executeQuery("select * from AP_OPERATION" + " where PERIOD = '"
                    + period.getPeriod() + "'");

            while (rs.next()) {
                Reference self =
                    getReference(rs.getString(1), rs.getString(2), rs.getInt(5));
                loadObject(rs, self);
                allOperations.add(self);
            }
        }
        finally {
            stmt.close();
        }
        return allOperations;
    }


    /**
     * Laisse pour compatibilite
     *
     * @param period -
     *
     * @return La liste
     *
     * @exception PersistenceException -
     * @exception SQLException -
     */
    private List loadAllOperationsBAD(Period period)
            throws PersistenceException, SQLException {
        List allOperations = new ArrayList();
        Statement stmt = getConnection().createStatement();
        try {
            ResultSet rs =
                stmt.executeQuery("select * from AP_OPERATION" + " where PERIOD = '"
                    + period.getPeriod() + "'");

            while (rs.next()) {
                Reference self =
                    getReference(rs.getString(1), rs.getString(2), rs.getInt(5));
                allOperations.add(loadObject(rs, self));
            }
        }
        finally {
            stmt.close();
        }
        return allOperations;
    }


    /**
     * Creation d'un nouveau AnomalyReport a partir du prototype.
     *
     * @return AnomalyReport
     */
    private AnomalyReport newAnomalyReport() {
        return (AnomalyReport)anomalyReportProto.clone();
    }


    /**
     * Creation d'une nouvelle operation
     *
     * @param opeID OPERATION_SETTINGS_ID
     * @param period La Periode de l'operation
     * @param pfGrp Le Groupe de Portefeuille
     *
     * @return Une reference sur l'operation
     *
     * @exception PersistenceException Erreur dans la couche de persistance.
     */
    private Reference newOperation(int opeID, Period period, PortfolioGroup pfGrp)
            throws PersistenceException {
        Reference ref = new Reference(this);
        Operation ope =
            new Operation(ref, period.getReference(), pfGrp.getPortfolioGroupName(),null,
                settingsHome.getReference(opeID), newAnomalyReport());
        ope.save();
        return ref;
    }

    /**
     * Clef primaire pour une Operation.
     * 
     * <p>
     * Une clef primaire est constituee de PERIOD, PORTFOLIO_GROUP et
     * OPERATION_SETTINGS_ID.
     * </p>
     *
     * @author $Author: marcona $
     * @version $Revision: 1.6 $
     */
    private static class OperationPK {
        int operationSettingsId;
        String period;
        String portfolioGroup;

        /**
         * Constructor for the OperationPK object
         *
         * @param period PERIOD
         * @param pfGroup PORTFOLIO_GROUP
         * @param opeId OPERATION_SETTINGS_ID
         */
        OperationPK(String period, String pfGroup, int opeId) {
            this.period = period;
            portfolioGroup = pfGroup;
            operationSettingsId = opeId;
        }

        /**
         * DOCUMENT ME!
         *
         * @param o
         *
         * @return
         */
        public boolean equals(Object o) {
            if (o != null && o.getClass() == OperationPK.class) {
                OperationPK pk = (OperationPK)o;
                return operationSettingsId == pk.operationSettingsId
                && period.equals(pk.period) && portfolioGroup.equals(pk.portfolioGroup);
            }
            return false;
        }


        /**
         * DOCUMENT ME!
         *
         * @return
         */
        public int hashCode() {
            return operationSettingsId;
        }


        /**
         * DOCUMENT ME!
         *
         * @return
         */
        public String toString() {
            return "pk(" + period + "," + portfolioGroup + "," + operationSettingsId
            + ")";
        }
    }
}
