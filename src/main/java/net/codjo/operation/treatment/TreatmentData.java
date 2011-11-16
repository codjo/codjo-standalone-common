/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.operation.treatment;
import net.codjo.model.Table;
import net.codjo.operation.AnomalyReport;
import net.codjo.operation.Operation;
/**
 * Implementation de l'interface OperationData pour les applications Penelope/Alis.
 *
 * @author $Author: blazart $
 * @version $Revision: 1.5 $
 *
 * @see net.codjo.operation.treatment.OperationData
 */
public class TreatmentData implements OperationData {
    private Operation operation;

    /**
     * Constructeur.
     *
     * @param operation L'operation.
     *
     * @throws IllegalArgumentException TODO
     */
    public TreatmentData(Operation operation) {
        if (operation == null) {
            throw new IllegalArgumentException();
        }
        this.operation = operation;
    }

    /**
     * Retourne la période de l'opération.
     *
     * @return La période de l'opération.
     */
    public String getPeriod() {
        return operation.getPeriod().getPeriod();
    }


    /**
     * Retourne la période N-1 de l'opération.
     *
     * @return La période de l'opération.
     */
    public String getPreviousPeriod() {
        return operation.getPreviousPeriod();
    }


    /**
     * Retourne le groupe de portefeuilles de l'opération.
     *
     * @return Le groupe de portefeuilles de l'opération.
     */
    public String getPortfolioGroupName() {
        return operation.getPortfolioGroupName();
    }


    /**
     * Retourne le behavior de l'opération.
     *
     * @return Le behavior de l'opération.
     */
    public TreatmentBehavior getLoadedBehavior() {
        return (TreatmentBehavior)operation.getLoadedBehavior();
    }


    /**
     * Retourne l'anomalyReport de l'opération.
     *
     * @return L'anomalyReport de l'opération.
     */
    public AnomalyReport getAnomalyReport() {
        return operation.getAnomalyReport();
    }


    /**
     * Construction de la clause "from" pour une table donnée.
     *
     * @param tableOfQuery Table sur laquelle va porter la requête
     *
     * @return Une liste de table (ex: "AP_PORTFOLIO, BO_PORTFOLIO")
     */
    public String buidTableClauseFor(Table tableOfQuery) {
        return operation.buidTableClauseFor(tableOfQuery);
    }


    /**
     * Construction de la clause "where" pour une table donnée.
     *
     * @param tableOfQuery Table sur laquelle vas porter la clause "where"
     *
     * @return Une clause where (ex: " where ...") ou null
     */
    public String buildWhereClauseFor(Table tableOfQuery) {
        return operation.buildWhereClauseFor(tableOfQuery);
    }


    public Operation getOperation() {
        return operation;
    }
}
