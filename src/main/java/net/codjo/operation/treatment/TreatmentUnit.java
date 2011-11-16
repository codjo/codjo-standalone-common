/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.operation.treatment;
import net.codjo.expression.ExpressionException;
import net.codjo.expression.ExpressionManager;
import net.codjo.operation.AnomalyReport;
import net.codjo.operation.OperationInterruptedException;
import net.codjo.utils.QueryHelper;
import net.codjo.utils.SqlTypeConverter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
/**
 * Gère le traitement d'un lot.
 * 
 * <p>
 * Un lot possede deux mode d'ecriture Insert ou Update. Dans le mode update, la clause
 * where de la requete est: <code>updateDestKey=x</code> , ou <code>updateDestKey</code>
 * est le nom physique d'une colonne dans la table destination, et <code>x</code> est la
 * valeur courante de la colonne <code>updateSourceKey</code> . Dans ce mode une
 * expression "updateDestKey = updateSourceKey" est insere dans
 * l'<code>ExpressionManager</code> .
 * </p>
 *
 * @author $Author: marcona $
 * @version $Revision: 1.9 $
 */
public class TreatmentUnit {
    public static final Logger USER = Logger.getLogger("journal");

    // Log
    private static final Logger APP = Logger.getLogger(TreatmentUnit.class);
    private BreakDetector breakDetector;
    private Object criterionZeroVal;
    private String destTableName;
    private ExpressionManager expressionManager;
    private int id;

    // private int batchCount = 0;
    // Criteres d'insertion
    private String insertCriterion = null;
    private int nbError;

    // Anomaly Report
    private AnomalyReport report;
    private TreatmentUnitSelection unitSelection;
    private String updateCriteria;
    private String updateDestKey;

    // Mode update
    private boolean updateMode = false;
    private String updateSourceKey;

    /**
     * Constructor for the TreatmentUnit object
     *
     * @param id Identifiant du lot
     * @param expressionManager
     * @param breakDetector
     * @param unitSelection
     * @param destTableName
     * @param insertCriterion Critere d'insertion
     * @param writeMode Mode d'ecriture
     * @param updateSourceKey La clef d'update dans la table source
     * @param updateDestKey La clef d'update dans la table destination
     * @param updateCriteria Le critère utilisé pour compléter la clause where de la
     *        requête lorsqu'on est en mode update
     */
    public TreatmentUnit(int id, ExpressionManager expressionManager,
        BreakDetector breakDetector, TreatmentUnitSelection unitSelection,
        String destTableName, String insertCriterion, String writeMode,
        String updateSourceKey, String updateDestKey, String updateCriteria) {
        this.expressionManager = expressionManager;
        this.breakDetector = breakDetector;
        this.unitSelection = unitSelection;
        this.destTableName = destTableName;
        this.id = id;
        this.updateCriteria = updateCriteria;
        setWriteMode(writeMode, updateSourceKey, updateDestKey);

        expressionManager.initExpressions();

        setInsertCriterion(insertCriterion);
    }

    /**
     * Gets the Id attribute of the TreatmentUnit object
     *
     * @return The Id value
     */
    public int getId() {
        return id;
    }


    /**
     * Lance le traitement du lot.
     *
     * @param con La connection utilisée par le traitement.
     * @param operation L'operation (interface permettant d'adapter le code en fonction
     *        de l'application).
     *
     * @exception SQLException Pb base.
     * @exception OperationInterruptedException Description of Exception
     */
    public void proceed(Connection con, OperationData operation)
            throws SQLException, OperationInterruptedException {
        report = operation.getAnomalyReport();
        report.clearAnomaly();
        breakDetector.clear();

        // Log
        if (APP.isDebugEnabled()) {
           APP.debug("Début Traitement du Lot N° "+getId() +". ");
        }

        Statement selectStmt;
        if (report.needsSourceUpdatable()) {
            selectStmt =
                con.createStatement(ResultSet.TYPE_FORWARD_ONLY,
                    ResultSet.CONCUR_UPDATABLE);
        }
        else {
            selectStmt = con.createStatement();
        }
        ResultSet rs = unitSelection.doSelectUnit(con, selectStmt, operation);
        if (rs.next() == false) {
            return;
        }

        List destColumns = expressionManager.getDestFieldList();
        List destSqlType = buildSqlTypeList(destColumns);
        PreparedStatement writeStmt = buildWriteStatement(con, destColumns, operation);

        try {
            nbError = 0;
            do {
                if (Thread.interrupted()) {
                    throw new OperationInterruptedException("Interruption utilisateur");
                }
                operation.getLoadedBehavior().incrementCurrentOfTask();
                if (breakDetector.isBreakPoint(rs)) {
                    if (report.isWriteAllowed()) {
                        doWrite(writeStmt, destColumns, destSqlType);
                    }
                    report.clearAnomaly();
                }

                if (report.hasAnomaly() == false) {
                    doCompute(rs);
                    report.updateSource(rs);
                }
            }
            while (rs.next());

            if (report.isWriteAllowed()) {
                doWrite(writeStmt, destColumns, destSqlType);
            }

//            writeStmt.executeBatch();
//            writeStmt.clearBatch();
        }
        finally {

            // Log
            if (APP.isDebugEnabled()) {
               APP.debug("Fin Traitement du Lot. ");
            }
            writeStmt.close();
            selectStmt.close();
            rs.close();
            unitSelection.updateSourceTableAnomalies(con);
        }
    }


    int getNbError() {
        return nbError;
    }


    /**
     * Rempli la liste des types SQL des champs de destination.
     *
     * @param destColumns Liste des noms de colonne de destination.
     *
     * @return Liste des types SQL.
     */
    private List buildSqlTypeList(List destColumns) {
        List destSqlType = new ArrayList(destColumns.size());

        for (int i = 0; i < destColumns.size(); i++) {
            Object columnName = destColumns.get(i);
            Object sqlType = expressionManager.getDestColumn().get(columnName);

            destSqlType.add(i, sqlType);
        }

        return destSqlType;
    }


    /**
     * Creation de la requete d'ecriture dans la table destination. Retourne une requete
     * insert ou update (suivant le mode d'ecriture)
     *
     * On insert les champs anomalies à la fin de la requête en mode insert et non en mode update.
     *
     * @param con La connection portant le PreparedStatement
     * @param destColumns Liste des colonnes destination
     * @param ope L'operation (interface permettant d'adapter le code en fonction de
     *        l'application).
     *
     * @return le <code>PreparedStatement</code>
     *
     * @exception SQLException Erreur BD
     */
    private PreparedStatement buildWriteStatement(Connection con, List destColumns,
        OperationData ope) throws SQLException {
        if (updateMode) {
            // Log
            if (APP.isDebugEnabled()) {
                APP.debug("\tMode Update");
            }

            // UPDATE Statement
            // Dans ce mode : destColumns contient en derniere position, la
            //   colonne utilise pour la clause where.
            List columns = new ArrayList(destColumns.subList(0, destColumns.size() - 1));
            List whereList =
                destColumns.subList(destColumns.size() - 1, destColumns.size());

            // Mise à jour des anomalies vers la table Destination ?  Si oui, on ajoute les champs Anomalies
            if (report.needsDestinationUpdatable()) {
                columns.addAll(Arrays.asList(report.getColumnsName()));
            }

            if (updateCriteria != null) {
                int idx = updateCriteria.indexOf("$CURRENT_PERIOD$");
                if (idx >= 0) {
                    StringBuffer criteria = new StringBuffer(updateCriteria);
                    criteria.replace(idx, idx + 16, "'" + ope.getPeriod() + "'");
                    updateCriteria = criteria.toString();
                }
                int idxprev = updateCriteria.indexOf("$PREVIOUS_PERIOD$");
                if (idxprev >= 0) {
                    StringBuffer criteria = new StringBuffer(updateCriteria);
                    criteria.replace(idxprev, idxprev + 17,
                        "'" + ope.getPreviousPeriod() + "'");
                    updateCriteria = criteria.toString();
                }
            }

            return QueryHelper.buildUpdateStatementWithWhereClause(destTableName,
                columns, whereList, updateCriteria, con);
        }
        else {
            // INSERT Statement

            // Log
            if (APP.isDebugEnabled()) {
                APP.debug("\tMode Insert");
            }

            List columns = new ArrayList(destColumns);

            // Mise à jour des anomalies vers la table Destination ?  Si oui, on ajoute les champs Anomalies
            if (report.needsDestinationUpdatable()) {
                columns.addAll(Arrays.asList(report.getColumnsName()));
            }

            return QueryHelper.buildInsertStatement(destTableName, columns, con);
        }
    }


    /**
     * Indique si on peut inserer. On insere que si la valeur du critere d'insertion est
     * differente de zero.
     *
     * @return 'true' si le critere d'insertion est valide (non null)
     */
    private boolean canInsert() {
        if (insertCriterion == null) {
            return true;
        }

        Object value = expressionManager.getComputedValue(insertCriterion);

        if (value == null || ((Comparable)criterionZeroVal).compareTo(value) == 0) {
            return false;
        }
        else {
            return true;
        }
    }


    /**
     * Evaluation des expressions d'une ligne. Cette méthode met à jour les champs
     * d'anomalies.
     *
     * @param rs ResultSet pointant sur la ligne à évaluer.
     *
     * @exception SQLException Description of Exception
     */
    private void doCompute(ResultSet rs) throws SQLException {
        try {
            fillSourceField(rs);
            expressionManager.compute();
        }
        catch (ExpressionException ex) {
            for (int i = 0; i < ex.getNbError(); i++) {
                report.addAnomaly(ex.getMessage(i));
            }
            nbError++;
        }
    }


    private void doInsertTrace(List destColumns, SQLException ex) {
        StringBuffer errorMessage = new StringBuffer("Erreur lors de l'insertion :[");
        for (int i = 0; i < destColumns.size(); i++) {
            String columnName = (String)destColumns.get(i);
            Object columnValue = expressionManager.getComputedValue(columnName);

            errorMessage.append(columnName).append("=").append(columnValue);

            if (i + 1 < destColumns.size()) {
                errorMessage.append(",");
            }
        }
        errorMessage.append("]");
        USER.error(errorMessage.toString(), ex);
        System.err.println(errorMessage.toString());
    }


    /**
     * Lance l'insertion dans la base. Le statement est rempli dans cette méthode.
     *
     * @param writeStmt Le Statement d'insert.
     * @param destColumns
     * @param destSqlType
     *
     * @exception SQLException Pb base.
     *
     */
    private void doWrite(PreparedStatement writeStmt, List destColumns, List destSqlType)
            throws SQLException {
        if (canInsert() == false) {
            return;
        }

       // Séparation des champs à updater et ceux de la clause where
       // pour l'insertion de ceux du Rapport d'anomalie (ANOMALY, ANOMALY_LOG)
       List columns = new ArrayList(destColumns.subList(0, destColumns.size() - 1));
       //  List whereList =
       //         destColumns.subList(destColumns.size() - 1, destColumns.size());

         // Ajout dans le statement le type de l'objet
        // sauf pour la dernière colonne car c'est le champ de la clause where

        int splitPosition= destColumns.size();

        // Si Update Alors on Split la dernière colonne pour insérer les colonnes anomaly

        if (report.needsDestinationUpdatable()){
            if(updateMode)
                splitPosition=splitPosition-1;
        }

        for (int i = 0; i < splitPosition; i++) {
            String columnName = (String)destColumns.get(i);
            Integer sqlType = (Integer)destSqlType.get(i);

            writeStmt.setObject(i + 1, expressionManager.getComputedValue(columnName),
                sqlType.intValue());
        }

        // Si Update alors Ajout du dernier champ spliter précédemment
        if (report.needsDestinationUpdatable()) {

            // Ajout des champs et données anomalies
            report.updateDestination(writeStmt, splitPosition + 1);

            if(updateMode){
                // Ajout du dernier champ cas Split (clause where)
                String columnName = (String)destColumns.get(destColumns.size() - 1);
                Integer sqlType = (Integer)destSqlType.get(destColumns.size() - 1);
                writeStmt.setObject(splitPosition+3, expressionManager.getComputedValue(columnName),
                    sqlType.intValue());
            }
        }
        try {
            writeStmt.executeUpdate();
        }
        catch (SQLException ex) {
            doInsertTrace(destColumns, ex);
            throw ex;
        }
        finally {
            writeStmt.clearParameters();
            expressionManager.clear();
        }
    }


    /**
     * Rempli les variables des champs source dans l'expressionManager.
     *
     * @param rs ResultSet pointant sur la ligne à évaluer.
     *
     * @exception SQLException Pb base.
     */
    private void fillSourceField(ResultSet rs) throws SQLException {
        Iterator iter = expressionManager.getSourceColumn().keySet().iterator();
        while (iter.hasNext()) {
            String columnName = (String)iter.next();
            Object value = rs.getObject(columnName);
            expressionManager.setFieldSourceValue(columnName, value);
        }
    }


    /**
     * Positionne le critere d'insertion.
     *
     * @param insertCriterion
     *
     * @throws IllegalArgumentException TODO
     */
    private void setInsertCriterion(String insertCriterion) {
        if (insertCriterion != null && "".equals(insertCriterion.trim()) == false) {
            this.insertCriterion = insertCriterion;

            int sqlType = expressionManager.getDestFieldSQLType(insertCriterion);
            if (expressionManager.isDestFieldNumeric(insertCriterion) == true) {
                criterionZeroVal = SqlTypeConverter.getDefaultSqlValue(sqlType);
            }
            else if (SqlTypeConverter.isString(new Integer(sqlType))) {
                criterionZeroVal = "#NR";
            }
            else {
                throw new IllegalArgumentException("Le critere d'insertion n'est"
                    + " pas un numerique ou une chaine de caractères : "
                    + insertCriterion);
            }
        }
    }


    /**
     * Positionne le mode d'ecriture du lot.
     *
     * @param writeMode Le mode d'ecriture ("INSERT" ou "UPDATE")
     * @param updateSourceKey La clef d'update dans la table source
     * @param updateDestKey La clef d'update dans la table destination
     *
     * @throws IllegalArgumentException TODO
     */
    private void setWriteMode(String writeMode, String updateSourceKey,
        String updateDestKey) {
        if ("INSERT".equals(writeMode)) {
            this.updateMode = false;
            this.updateDestKey = null;
            this.updateSourceKey = null;
        }
        else if ("UPDATE".equals(writeMode)) {
            this.updateMode = true;
            this.updateDestKey = updateDestKey;
            this.updateSourceKey = updateSourceKey;

            expressionManager.add(updateDestKey, "SRC_" + updateSourceKey);
        }
        else {
            throw new IllegalArgumentException("Mode d'ecriture inconnue : " + writeMode);
        }
    }
}
