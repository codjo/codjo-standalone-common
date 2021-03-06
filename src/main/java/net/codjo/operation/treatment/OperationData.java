/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.operation.treatment;
import net.codjo.model.Table;
import net.codjo.operation.AnomalyReport;
/**
 * Interface permettant de red�finir des m�thodes de la classe Operation. Cela est
 * n�cessaire pour lancer une operation de traitement en tenant compte des sp�cificit�s
 * de l'application d'appel (Penelope / Alis / Paris). Elle est utilis�e dans la m�thode
 * proceed du TreatmentBehavior.
 *
 * @author $Author: blazart $
 * @version $Revision: 1.5 $
 *
 */
public interface OperationData {
    /**
     * Retourne la p�riode de l'op�ration.
     *
     * @return La p�riode de l'op�ration.
     */
    public String getPeriod();


    /**
     * Retourne la p�riode N-1 de l'op�ration.
     *
     * @return La p�riode N-1 de l'op�ration.
     */
    public String getPreviousPeriod();


    /**
     * Retourne le groupe de portefeuilles de l'op�ration.
     *
     * @return Le groupe de portefeuilles de l'op�ration.
     */
    public String getPortfolioGroupName();


    /**
     * Retourne le behavior de l'op�ration.
     *
     * @return Le behavior de l'op�ration.
     */
    public TreatmentBehavior getLoadedBehavior();


    /**
     * Retourne l'anomalyReport de l'op�ration.
     *
     * @return L'anomalyReport de l'op�ration.
     */
    public AnomalyReport getAnomalyReport();


    /**
     * Construction de la clause "from" pour une table donn�e.
     *
     * @param tableOfQuery Table sur laquelle va porter la requ�te
     *
     * @return Une liste de table (ex: "AP_PORTFOLIO, BO_PORTFOLIO")
     */
    public String buidTableClauseFor(Table tableOfQuery);


    /**
     * Construction de la clause "where" pour une table donn�e.
     *
     * @param tableOfQuery Table sur laquelle vas porter la clause "where"
     *
     * @return Une clause where (ex: " where ...") ou null
     */
    public String buildWhereClauseFor(Table tableOfQuery);
}
