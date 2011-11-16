/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.operation;
import net.codjo.model.Period;
import net.codjo.model.Table;
import net.codjo.model.TableRecordingMode;
import net.codjo.persistent.AbstractPersistent;
import net.codjo.persistent.PersistenceException;
import net.codjo.persistent.Reference;
import net.codjo.utils.ConnectionManager;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * Classe générique pour tous les types d'opération.
 *
 * <p> La méthode <code>proceed</code> permet d'executer l'opération. Cette méthode délègue le traitement à
 * son comportement. Le comportement défini donc l'action : import, traitement,... </p>
 *
 * @author $Author: marcona $
 * @version $Revision: 1.17 $
 * @see net.codjo.operation.OperationSettings
 */
public final class Operation extends AbstractPersistent {
    private static final Logger USER = Logger.getLogger("journal");

    // Log
    private static final Logger APP = Logger.getLogger(Operation.class);
    private Reference operationSettingsRef;
    private OperationState operationState = new OperationState();
    private Reference periodRef;
    private String portfolioGroupName;

    private String company;
    private List ptfRestrictionList;
    private AnomalyReport report;

    private static OperationSaveListener staticSaveListener;

    private OperationSaveListener saveListener;

    // private ArrayList whereClauseListUpdateWithPortfolio = new ArrayList(6);
    private SqlWhereClauseUtil sqlWhereClauseUtil = new SqlWhereClauseUtil();


    /**
     * Constructor
     *
     * @param r Description of Parameter
     *
     * @throws IllegalArgumentException si l'un des arguments est invalide
     */
    Operation(Reference selfRef, Reference period, String pfGrp, String company, Reference settings,
              AnomalyReport r) throws PersistenceException {
        super(selfRef);
        if (period == null || pfGrp == null || settings == null) {
            throw new IllegalArgumentException();
        }
        portfolioGroupName = pfGrp;
        operationSettingsRef = settings;
        periodRef = period;
        operationSettingsRef.getObject();
        periodRef.getObject();
        this.report = r;
        this.company = company;
    }


    /**
     * Construction de la clause table pour une table donnee. Cette methode est utilise par deleteTable() et
     * markAnomalyField().
     *
     * @param tableOfQuery Table sur laquelle vas porter la requete
     *
     * @return liste de table (ex: "AP_PORTFOLIO, BO_PORTFOLIO")
     */
    public String buidTableClauseFor(Table tableOfQuery) {
        if (((getDestTable().getRecordingMode() == TableRecordingMode.BY_PERIOD_AND_PORTFOLIOGROUP)
             || (getDestTable().getRecordingMode() == TableRecordingMode.BY_PORTFOLIOGROUP))
            && ((!("SANS".equals(getPortfolioGroupName())))
                || (ptfRestrictionList != null))) {
            return tableOfQuery.getDBTableName() + ", BO_PORTFOLIO";
        }
        else {
            return tableOfQuery.getDBTableName();
        }
    }


    /**
     * Construction de la clause "where" pour une table donnee. Cette methode retourne la clause "where"
     * utilise par deleteTable() et markAnomalyField().
     *
     * <p> ATTENTION : La methode buidTableClauseFor() doit etre utilise pour construire la clause de
     * selection des tables. </p> REMARQUE : l'objet whereClauseListUpdateWithPortfolio est utilisé à des fins
     * d'optimisation
     *
     * @param tableOfQuery Table sur laquelle vas porter la clause "where"
     *
     * @return Une clause where (ex: " where ...") ou null
     *
     * @throws IllegalArgumentException si le mode d'enregistrement est inconnu
     * @see #deleteTable(net.codjo.model.Table)
     * @see #markAnomalyField(boolean)
     * @see #buildWhereClauseFor(net.codjo.model.Table)
     */
    public String buildWhereClauseFor(Table tableOfQuery) {
        String sourceSystem = getSourceTable().getSource();
        String period = getPeriod().getPeriod();

        // Initialisation de la liste des clauses where servant à l'update
//        whereClauseListUpdateWithPortfolio.add(0,"");
//        whereClauseListUpdateWithPortfolio.add(1,"");
        sqlWhereClauseUtil.init();

        String whereClause = null;
        if (getDestTable().equals(tableOfQuery) || getSourceTable().equals(tableOfQuery)) {
            switch (getDestTable().getRecordingMode()) {
                case TableRecordingMode.NONE:
                    whereClause = null;
                    break;
                case TableRecordingMode.BY_PERIOD_AND_PORTFOLIOGROUP:
                    if (!"SANS".equals(getPortfolioGroupName())) {
                        whereClause =
                              tableOfQuery.getDBTableName()
                              + ".PORTFOLIO_CODE = BO_PORTFOLIO.PORTFOLIO_CODE"
                              + " and PORTFOLIO_GROUP ='" + getPortfolioGroupName() + "'";
                        sqlWhereClauseUtil.dealWithPortfolioGroup(getPortfolioGroupName(),
                                                                  tableOfQuery.getDBTableName());
                    }
                case TableRecordingMode.BY_PERIOD:
                    if (tableOfQuery.containsColumn("PERIOD")) {
                        whereClause =
                              "PERIOD='" + period + "'"
                              + ((whereClause != null) ? (" and " + whereClause) : "");

                        sqlWhereClauseUtil.dealWithPeriod(period);
                    }
                    break;
                case TableRecordingMode.BY_PORTFOLIOGROUP:
                    if (!"SANS".equals(getPortfolioGroupName())) {
                        whereClause =
                              tableOfQuery.getDBTableName()
                              + ".PORTFOLIO_CODE = BO_PORTFOLIO.PORTFOLIO_CODE"
                              + " and PORTFOLIO_GROUP ='" + getPortfolioGroupName() + "'";

                        sqlWhereClauseUtil.dealWithPortfolioGroup(getPortfolioGroupName(),
                                                                  tableOfQuery.getDBTableName());
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Unknown Recording Mode :"
                                                       + getDestTable().getRecordingMode());
            }

            // Choix du Source System
            if (whereClause != null) {
                whereClause = " where " + whereClause;
                if (tableOfQuery.containsColumn("SOURCE_SYSTEM")) {
                    if (!"MULTI_PTF".equals(sourceSystem)) {
                        whereClause += " and " + tableOfQuery.getDBTableName()
                                       + ".SOURCE_SYSTEM = " + "'" + sourceSystem + "'";
                        sqlWhereClauseUtil.dealWithSourceSystem(sourceSystem,
                                                                tableOfQuery.getDBTableName());
                    }
                }
            }
        }

        // Restriction portefeuilles
        if ((getDestTable().getRecordingMode() == TableRecordingMode.BY_PERIOD_AND_PORTFOLIOGROUP
             && ptfRestrictionList != null)
            || (getDestTable().getRecordingMode() == TableRecordingMode.BY_PORTFOLIOGROUP
                && ptfRestrictionList != null)) {
            whereClause = addPtfRestrictionList(whereClause);
            String str = addPtfRestrictionList(sqlWhereClauseUtil.getSelectTerm());
            sqlWhereClauseUtil.addPtfRestrictionList(str);
        }

        // Critère de sélection ou de delete
        if (getOperationSettings().getDestTable().equals(tableOfQuery)) {
            whereClause =
                  addWhereCriteria(getOperationSettings().getDeleteCriteria(), whereClause);
        }
        else {
            whereClause =
                  addWhereCriteria(getOperationSettings().getSelectCriteria(), whereClause);
        }

        String criteriaTemp = "";
        criteriaTemp =
              addWhereCriteria((getOperationSettings().getDestTable().equals(tableOfQuery))
                               ? getOperationSettings().getDeleteCriteria()
                               : getOperationSettings().getSelectCriteria(), criteriaTemp);

// Découpage du critère sélection en 2 sous-critères (1 pour le select et un pour l'update)
        sqlWhereClauseUtil.buildCriteria(criteriaTemp, tableOfQuery);
        // Renseignement des champs période avec la période concernée et la compagnie
        sqlWhereClauseUtil.fill(getPeriod().toString(), getPreviousPeriod(), getCompany());
        return fillWhereClauseWithPeriod(whereClause);
    }


    /**
     * Remplissage de la whereClause pour prendre en compte le paramétrage $CURRENT_PERIOD$ à remplacer
     * automatiquement par la période courante $PREVIOUS_PERIOD$ à remplacer automatiquement par la période
     * précédente
     *
     * @param initialWhereClause Description of the Parameter
     *
     * @return Description of the Return Value
     */
    private String fillWhereClauseWithPeriod(String initialWhereClause) {
        if (initialWhereClause != null) {
            String clause = initialWhereClause;
            int idx = clause.indexOf("$CURRENT_PERIOD$");
            if (idx >= 0) {
                StringBuilder criteria = new StringBuilder(clause);
                criteria.replace(idx, idx + 16, "'" + getPeriod() + "'");
                clause = criteria.toString();
            }
            int idxprev = clause.indexOf("$PREVIOUS_PERIOD$");
            if (idxprev >= 0) {
                StringBuilder criteriaPrev = new StringBuilder(clause);
                criteriaPrev.replace(idxprev, idxprev + 17,
                                     "'" + getPreviousPeriod() + "'");
                clause = criteriaPrev.toString();
            }

            int idxCompany = clause.indexOf("$COMPANY$");
            if (idxCompany >= 0) {
                StringBuilder criteriaPrev = new StringBuilder(clause);
                criteriaPrev.replace(idxCompany, idxCompany + 9,
                                     "'" + getCompany() + "'");
                clause = criteriaPrev.toString();
            }

            return clause;
        }
        return initialWhereClause;
    }


    /**
     * Determine la longueur de l'opération à effectuer (ex : Nb de lignes du fichier à importer).
     *
     * <p> <b>Attention</b> : Le comportement doit etre deja charge. </p>
     *
     * @throws OperationFailureException Si la determination echoue (L'etat de l'operation est mis en ECHEC)
     */
    public void determineLengthOfTask() throws OperationFailureException {
        try {
            getLoadedBehavior().determineLengthOfTask(this);
        }
        catch (Exception ex) {
            manageOperationFailure(ex);
        }
    }


    /**
     * Retourne L'objet responsable de la gestion des anomaly pour cette operation.
     *
     * @return The AnomalyReport value
     */
    public AnomalyReport getAnomalyReport() {
        return report;
    }


    /**
     * Retourne le comportement de l'opération. Si le comportement n'est pas déjà chargé, alors la méthode le
     * charge.
     *
     * @return le comportement.
     *
     * @throws PersistenceException -
     */
    public Behavior getBehavior() throws PersistenceException {
        return getOperationSettings().getBehavior();
    }


    /**
     * Gets the Commentry attribute of the ImportOperation object
     *
     * @return The OperationState value
     */
    public String getCommentry() {
        return getOperationSettings().getCommentry();
    }


    /**
     * Récupère la table destination de l'opération
     *
     * @return The Table value
     */
    public Table getDestTable() {
        return getOperationSettings().getDestTable();
    }


    /**
     * Retourne le comportement charge en memoire (sinon <code>null</code> ).
     *
     * @return le comportement (peut etre null).
     */
    public Behavior getLoadedBehavior() {
        return getOperationSettings().getLoadedBehavior();
    }


    /**
     * Gets the OperationSettings attribute of the Operation object
     *
     * @return The OperationSettings value
     */
    public OperationSettings getOperationSettings() {
        return (OperationSettings)operationSettingsRef.getLoadedObject();
    }


    /**
     * Gets the OperationState attribute of the ImportOperation object
     *
     * @return The OperationState value
     */
    public OperationState getOperationState() {
        return operationState;
    }


    /**
     * Retourne le type de l'operation.
     *
     * @return le type de l'operation
     *
     * @see net.codjo.operation.OperationSettings#getOperationType
     */
    public String getOperationType() {
        return getOperationSettings().getOperationType();
    }


    /**
     * Gets the Period attribute of the ImportOperation object
     *
     * @return The Period value
     */
    public Period getPeriod() {
        return (Period)periodRef.getLoadedObject();
    }


    /**
     * Gets the PortfolioGroup attribute of the ImportOperation object
     *
     * @return The PortfolioGroup value
     */
    public String getPortfolioGroupName() {
        return portfolioGroupName;
    }


    /**
     * Gets the Priority attribute of the ImportOperation object
     *
     * @return The Priority value
     */
    public int getPriority() {
        return getOperationSettings().getPriority();
    }


    /**
     * Récupère la table source de l'opération
     *
     * @return The Table value
     */
    public Table getSourceTable() {
        return getOperationSettings().getSourceTable();
    }


    /**
     * Gets the Automatic attribute of the ImportOperation object
     *
     * @return The Automatic value
     */
    public boolean isAutomatic() {
        return getOperationSettings().isAutomatic();
    }


    /**
     * Prepare l'execution de l'operation.
     *
     * @param firstLaunch Description of Parameter
     *
     * @throws OperationFailureException Description of Exception
     */
    public void prepareProceed(boolean firstLaunch)
          throws OperationFailureException {
        try {
            getOperationSettings().lockUnloadBehavior();

            getBehavior().prepareProceed();

            if (firstLaunch || "Traitement".equals(getOperationType())) {
                deleteTable(getBehavior().getDestTable());
            }

            if (getBehavior().getSourceTable() != null
                && getBehavior().getDestTable() != null) {
                markAnomalyField(firstLaunch);
            }
        }
        catch (Exception ex) {
            manageOperationFailure(ex);
        }
    }


    /**
     * Execute l'opération. Cette methode mets à jours automatiquement l'état de l'opération. Attention : Le
     * comportement doit etre prealablement charge.
     *
     * @throws IllegalArgumentException si la Behavior n'est pas chargée
     */
    public void proceed() throws OperationFailureException {
        if (getLoadedBehavior() == null) {
            throw new IllegalArgumentException("Behavior non charge");
        }
        try {
            log(getPeriod() + " " + getOperationType() + " " + getSourceTable() + " -> "
                + getDestTable());

            getLoadedBehavior().proceed(this);
            setOperationState(new OperationState(new Date(), OperationState.DONE));
        }
        catch (Exception ex) {
            manageOperationFailure(ex);
        }
        finally {
            log("Fin opération : " + getOperationType());
        }
    }


    /**
     * Sets the OperationState attribute of the ImportOperation object
     *
     * @param operationState The new OperationState value
     *
     * @throws IllegalArgumentException si l'un des arguments est invalide
     */
    public void setOperationState(OperationState operationState) {
        if (operationState == null) {
            throw new IllegalArgumentException();
        }
        this.operationState = operationState;
        setSynchronized(false);
        getOperationSettings().unlockUnloadBehavior();
    }


    /**
     * Constructor for the setPortfolioGroupName object
     *
     * @param name Description of Parameter
     */
    public void setPortfolioGroupName(String name) {
        portfolioGroupName = name;
    }


    /**
     * Fixe la restriction sur les codes portefeuilles
     *
     * @param ptfRestrictList La liste
     */
    public void setPtfRestrictionList(List ptfRestrictList) {
        ptfRestrictionList = ptfRestrictList;
    }


    /**
     * Ajoute la liste des restriction / codes ptf à la clause where
     *
     * @param whereClause La clause Where avant ajout
     *
     * @return La clause where après ajout
     */
    private String addPtfRestrictionList(String whereClause) {
        if (!ptfRestrictionList.isEmpty()) {
            if (whereClause != null) {
                whereClause += " and BO_PORTFOLIO.PORTFOLIO_CODE in (";
            }
            else {
                whereClause = " where BO_PORTFOLIO.PORTFOLIO_CODE in (";
            }

            for (int i = 0; i < ptfRestrictionList.size(); i++) {
                whereClause += "'" + ptfRestrictionList.get(i) + "'";
                if (i < ptfRestrictionList.size() - 1) {
                    whereClause += ",";
                }
            }
            whereClause += ")";
        }
        return whereClause;
    }


    /**
     * Ajoute le critère a la clause where.
     *
     * @param criteria    Le critere
     * @param whereClause La clause where
     *
     * @return la nouvelle clause where
     */
    private String addWhereCriteria(String criteria, String whereClause) {
        if (criteria != null && !"".equals(criteria)) {
            criteria = replaceVariable(criteria);
            if (whereClause != null) {
                whereClause += " and " + criteria;
            }
            else {
                whereClause = " where " + criteria;
            }
        }
        return whereClause;
    }


    /**
     * Efface le contenu de la table en fonction de son mode d'historisation.
     *
     * @param destTable Table de destination a effacer
     *
     * @throws Exception Description of the Exception
     * @see net.codjo.model.TableRecordingMode
     */
    private void deleteTable(Table destTable) throws Exception {
        if (destTable == null) {
            return;
        }
        String whereClause = buildWhereClauseFor(destTable);
        String query =
              "set rowcount 1000  while exists (select 1 from  "
              + buidTableClauseFor(destTable)
              + ((whereClause != null) ? whereClause : (""))
              + ")  begin  begin tran delete " + destTable.getDBTableName() + " from "
              + buidTableClauseFor(destTable)
              + ((whereClause != null) ? whereClause : ("")) + " if @@error > 0"
              + " rollback else commit  end  set rowcount 0 ";

        ConnectionManager connectionManager = getLoadedBehavior().getConnectionManager();
        Connection con = connectionManager.getConnection();
        Statement stmt = null;
        try {
            APP.debug("Query delete = " + query);
            stmt = con.createStatement();
            stmt.executeUpdate(query);
        }
        finally {
            connectionManager.releaseConnection(con, stmt);
        }
    }


    /**
     * DOCUMENT ME!
     */
    private void error(String msg, Throwable e) {
        USER.error(msg, e);
//		APP.error(msg, e);
    }


    /**
     * Retourne la period Begin Of The Year.
     *
     * @return La valeur de boyPeriod
     */
    private String getBoyPeriod() {
        String year = getPeriod().getPeriod().substring(0, 4);
        int y = Integer.parseInt(year) - 1;

        return y + "12";
    }


    /**
     * Retourne la period N-1
     *
     * @return Période N-1
     */
    public String getPreviousPeriod() {
        String year = getPeriod().getPeriod().substring(0, 4);
        String month = getPeriod().getPeriod().substring(4, 6);
        int y = Integer.parseInt(year) - 1;
        int m = Integer.parseInt(month) - 1;
        if (m == 0) {
            m = 12;
            year = String.valueOf(y);
        }
        String prevm = String.valueOf(m);
        if (prevm.length() <= 1) {
            prevm = "0" + prevm;
        }
        return year + prevm;
    }


    /**
     * DOCUMENT ME!
     */
    private void log(String msg) {
        USER.info(msg);
//		APP.debug(msg);
    }


    /**
     * Gere l'echec de l'operation.
     *
     * @param ex L'exception ayant fait echouer l'operation
     */
    private void manageOperationFailure(Exception ex)
          throws OperationFailureException {
        setOperationState(new OperationState(new Date(), OperationState.FAILED));
        error("Erreur : ", ex);
        ex.printStackTrace();
        if (ex instanceof OperationFailureException) {
            throw (OperationFailureException)ex;
        }
        else {
            throw new OperationFailureException(ex.getLocalizedMessage(), this);
        }
    }


    /**
     * Mets le champ ANOMALY à -1 pour les enregistrements qui doivent être traités.
     *
     * <p> Si le AnomalyReport ne fait pas d'update en source, cette methode ne fait rien. </p>
     *
     * <p> <b>ATTENTION</b> : Cette methode assume que le behavior est en memoire. </p>
     *
     * @param firstLaunch Traitement normal (ou reprise sur erreur)
     *
     * @throws Exception Description of the Exception
     */
    private void markAnomalyField(boolean firstLaunch)
          throws Exception {
        if (!report.needsSourceUpdatable()) {
            return;
        }

        // Création la requete
        String query = buildUpdateQuery(firstLaunch);

        ConnectionManager connectionManager = getLoadedBehavior().getConnectionManager();
        Connection con = connectionManager.getConnection();

//        Connection con = getLoadedBehavior().getConnectionManager().getConnection();
        Statement stmt = null;
        try {
            stmt = con.createStatement();
            stmt.executeUpdate(query);
        }
        finally {
//            getLoadedBehavior().getConnectionManager().releaseConnection(con, stmt);
            connectionManager.releaseConnection(con, stmt);
        }
    }


    /**
     * Permet la création des requêtes permettant un update optimisé.
     *
     * @param firstLaunch Traitement normal (ou reprise sur erreur)
     *
     * @throws Exception Description of the Exception
     */
    private String buildUpdateQuery(boolean firstLaunch)
          throws Exception {
        Table srcTable = getLoadedBehavior().getSourceTable();
        String srcTableName = srcTable.getDBTableName();
        String query = "";

        String whereClause = buildWhereClauseFor(srcTable);

        if (!firstLaunch) {
            if (whereClause != null) {
                whereClause += "and " + srcTableName + ".ANOMALY > 0";
            }
            else {
                whereClause = " where " + srcTableName + ".ANOMALY > 0";
            }
        }

        if (((getDestTable().getRecordingMode() == TableRecordingMode.BY_PERIOD_AND_PORTFOLIOGROUP)
             || (getDestTable().getRecordingMode() == TableRecordingMode.BY_PORTFOLIOGROUP))
            && ((!"SANS".equals(getPortfolioGroupName()))
                || (ptfRestrictionList != null))) {

            // Cas d'un update (avec jointure) à partir la table source et BO_PORTFOLIO
            // Construction du select
            query = " select PORTFOLIO_CODE into #BO_PORTFOLIO_TMP from BO_PORTFOLIO ";
            query += sqlWhereClauseUtil.getSelectWhereClause();

            // Création de l'index
            query +=
                  " create clustered index idx1 on #BO_PORTFOLIO_TMP(PORTFOLIO_CODE) ";

            // Création de l'update
            String udpateQuery = " update " + srcTableName + " set " + srcTableName + ".ANOMALY=-1,"
                                 + srcTableName + ".ANOMALY_LOG=NULL" + " from " + srcTableName
                                 + ", BO_PORTFOLIO ";

            udpateQuery += sqlWhereClauseUtil.getUpdateWhereClause();
            query += udpateQuery.replaceAll("BO_PORTFOLIO", "#BO_PORTFOLIO_TMP");

            // drop de la table temporaire
            query += " drop table #BO_PORTFOLIO_TMP";
            APP.debug("Update optimisé: " + query);
        }
        else {
            // Cas d'un update uniquement à partir de la table source
            query =
                  "update " + srcTableName + " set " + srcTableName + ".ANOMALY=-1,"
                  + srcTableName + ".ANOMALY_LOG=NULL" + " from "
                  + buidTableClauseFor(srcTable)
                  + ((whereClause != null) ? whereClause : "");
            APP.debug("Update non-optimisé: " + query);
        }

        return query;
    }


    /**
     * DOCUMENT ME!
     *
     * @param query Description of Parameter
     *
     * @return Description of the Returned Value
     */
    private String replaceVariable(String query) {
        int idx = query.indexOf("##BOY##");

        if (idx < 0) {
            return query;
        }

        StringBuffer builtQuery = new StringBuffer(query);
        builtQuery.replace(idx, idx + 7, getBoyPeriod());

        return replaceVariable(builtQuery.toString());
    }


    /**
     * <p>Appel la methode save du model.</p>
     *
     * <p>Appelle l'OperationSaveListener (static, ou d'instance) s'il existe, pour lui notifier
     * l'avant-sauvegarde et l'après-sauvegarde.</p>
     *
     * @throws net.codjo.persistent.PersistenceException
     *          -
     * @see net.codjo.persistent.Model#save
     */
    @Override
    public void save() throws PersistenceException {
        if (saveListener != null) {
            saveListener.onBeforeSave(this);
        }
        else {
            if (staticSaveListener != null) {
                staticSaveListener.onBeforeSave(this);
            }
        }

        super.save();

        if (saveListener != null) {
            saveListener.onAfterSave(this);
        }
        else {
            if (staticSaveListener != null) {
                staticSaveListener.onAfterSave(this);
            }
        }
    }


    public static OperationSaveListener getStaticSaveListener() {
        return staticSaveListener;
    }


    public static void setStaticSaveListener(OperationSaveListener staticSaveListener) {
        Operation.staticSaveListener = staticSaveListener;
    }


    public OperationSaveListener getSaveListener() {
        return saveListener;
    }


    public void setSaveListener(OperationSaveListener saveListener) {
        this.saveListener = saveListener;
    }


    public String getCompany() {
        return company;
    }


    public void setCompany(String company) {
        this.company = company;
    }
}
