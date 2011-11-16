/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.operation.treatment;
import net.codjo.model.Table;
import net.codjo.operation.AnomalyReport;
/**
 * Interface permettant de redéfinir des méthodes de la classe Operation. Cela est
 * nécessaire pour lancer une operation de traitement en tenant compte des spécificités
 * de l'application d'appel (Penelope / Alis / Paris). Elle est utilisée dans la méthode
 * proceed du TreatmentBehavior.
 *
 * @author $Author: blazart $
 * @version $Revision: 1.5 $
 *
 */
public interface OperationData {
    /**
     * Retourne la période de l'opération.
     *
     * @return La période de l'opération.
     */
    public String getPeriod();


    /**
     * Retourne la période N-1 de l'opération.
     *
     * @return La période N-1 de l'opération.
     */
    public String getPreviousPeriod();


    /**
     * Retourne le groupe de portefeuilles de l'opération.
     *
     * @return Le groupe de portefeuilles de l'opération.
     */
    public String getPortfolioGroupName();


    /**
     * Retourne le behavior de l'opération.
     *
     * @return Le behavior de l'opération.
     */
    public TreatmentBehavior getLoadedBehavior();


    /**
     * Retourne l'anomalyReport de l'opération.
     *
     * @return L'anomalyReport de l'opération.
     */
    public AnomalyReport getAnomalyReport();


    /**
     * Construction de la clause "from" pour une table donnée.
     *
     * @param tableOfQuery Table sur laquelle va porter la requête
     *
     * @return Une liste de table (ex: "AP_PORTFOLIO, BO_PORTFOLIO")
     */
    public String buidTableClauseFor(Table tableOfQuery);


    /**
     * Construction de la clause "where" pour une table donnée.
     *
     * @param tableOfQuery Table sur laquelle vas porter la clause "where"
     *
     * @return Une clause where (ex: " where ...") ou null
     */
    public String buildWhereClauseFor(Table tableOfQuery);
}
