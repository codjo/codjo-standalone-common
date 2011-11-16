/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.operation.treatment;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
/**
 * Classe faisant la selection pour un lot.
 *
 * @author $Author: blazart $
 * @version $Revision: 1.2 $
 *
 */
public interface TreatmentUnitSelection {
    /**
     * Renvoie un ResultSet de sélection (pour le lot) trie sur les clés de rupture.
     *
     * @param con La connection sur laquelle sera fait la selection
     * @param stmt Le statement utilise pour effectuer le select
     * @param ope Operation ayant lance le traitement (interface permettant d'adapter le
     *        code en fonction de l'application).
     *
     * @return Un ResultSet trié sur la table de selection
     *
     * @exception SQLException Erreur Base
     */
    public ResultSet doSelectUnit(Connection con, Statement stmt, OperationData ope)
            throws SQLException;


    /**
     * Mise a jours des champs ANOMALY de la table source a partir de la table de
     * selection.
     *
     * @param con Connection utilise pour faire la maj
     *
     * @exception SQLException Erreur Base
     */
    public void updateSourceTableAnomalies(Connection con)
            throws SQLException;
}
