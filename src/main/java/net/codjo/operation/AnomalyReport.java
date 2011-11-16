/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.operation;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
/**
 * Cette interface definit une classe responsable de la gestion des anomalies d'un
 * traitement.
 *
 * @author $Author: blazart $
 * @version $Revision: 1.2 $
 *
 */
public interface AnomalyReport extends Cloneable {
    /**
     * Duplication de cette AnomalyReport. Cette methode est utilise pour le pattern
     * Prototype.
     *
     * @return Une copie conforme de cette instance.
     */
    public Object clone();


    /**
     * Indique si cette <code>AnomalyReport</code> a besoin de pouvoir ecrire dans la
     * table source.
     *
     * @return 'true' si les anomalies sont repercutées dans le table source.
     */
    boolean needsSourceUpdatable();


    /**
     * Indique si cette <code>AnomalyReport</code> a besoin de pouvoir ecrire dans la
     * table destination.
     *
     * @return 'true' si les anomalies sont repercutées dans le table dest.
     */
    boolean needsDestinationUpdatable();


    /**
     * Retourne les noms de colonne mis-a-jour pour la gestion des anomalies.
     *
     * @return Tableau de String (ex: "ANOMALY", "ANOMALY_LOG")
     */
    String[] getColumnsName();


    /**
     * Indique si cet <code>AnomalyReport</code> autorise le traitement d'ecrire dans la
     * table destination.
     *
     * @return 'true' l'ecriture est possible
     */
    boolean isWriteAllowed();


    /**
     * Indique si il y a des anomalies.
     *
     * @return 'true' si anomaly
     */
    boolean hasAnomaly();


    /**
     * Efface toutes les anomalies.
     */
    void clearAnomaly();


    /**
     * Mets a jour les champs Anomaly de la table source (si necessaire). Si la methode
     * <code>needsSourceUpdatable()</code> retourne false, alors cette methode ne fait
     * rien.
     *
     * @param rsSrc Un resultSet sur la table source
     *
     * @exception SQLException Erreur base
     */
    void updateSource(ResultSet rsSrc) throws SQLException;


    /**
     * Mets a jour les champs Anomaly de la table destination (si necessaire). Si la
     * methode <code>needsDestinationUpdatable()</code> retourne false, alors cette
     * methode ne fait rien.
     *
     * @param stmt Le statement ecrivant dans la table destination
     * @param idx L'index ou se trouve le premier parametre des colonnes Anomalies
     *        (retourne par getColumnsName())
     *
     * @exception SQLException Erreur base
     */
    void updateDestination(PreparedStatement stmt, int idx)
            throws SQLException;


    /**
     * Ajoute une erreur dans cette <code>AnomalyReport</code> .
     *
     * @param anomalyLog Le texte de l'erreur.
     */
    void addAnomaly(String anomalyLog);
}
