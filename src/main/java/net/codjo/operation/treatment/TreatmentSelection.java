/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.operation.treatment;
import java.sql.Connection;
import java.sql.SQLException;
/**
 * Interface permettant de faire une selection pour un traitement global pour un
 * traitement.
 *
 * @author $Author: blazart $
 * @version $Revision: 1.2 $
 *
 */
public interface TreatmentSelection {
    /**
     * Lance la sélection pour le traitement souhaité.
     *
     * @param con La connection.
     * @param currentPeriod La periode courante
     * @param previousPeriod La periode precedante
     * @param portfolioGroup Le groupe de portefeuille
     *
     * @exception SQLException Pb base.
     */
    public void doSelect(Connection con, String currentPeriod, String previousPeriod,
        String portfolioGroup) throws SQLException;
}
