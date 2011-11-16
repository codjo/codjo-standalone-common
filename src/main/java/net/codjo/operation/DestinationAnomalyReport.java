/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.operation;
import org.apache.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
/**
 * Report les anomalies dans la table destination.
 *
 * @author $Author: marcona $
 * @version $Revision: 1.8 $
 *
 */
public class DestinationAnomalyReport implements AnomalyReport {
    transient StringBuffer anomalyLog = new StringBuffer();
    transient int anomaly = 0;
    // Log
    private static final Logger APP = Logger.getLogger(DestinationAnomalyReport.class);

    /**
     * Constructor for the DestinationAnomalyReport object
     */
    public DestinationAnomalyReport() {}

    /**
     * Ecriture autorise si aucune erreur.
     *
     * @return 'true' si aucune erreur
     */
    public boolean isWriteAllowed() {
        return true;
    }


    /**
     * Retourne les noms de colonne mis-a-jour pour la gestion des anomalies.
     *
     * @return {"ANOMALY", "ANOMALY_LOG"}
     */
    public String[] getColumnsName() {
        String[] a = {"ANOMALY", "ANOMALY_LOG"};
        return a;
    }


    /**
     * Copy de cette AnomalyReport.
     *
     * @return Une copy.
     *
     * @throws Error TODO
     */
    public Object clone() {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException x) {
            // Cas impossible
            x.printStackTrace();
            throw new Error("Duplication impossible");
        }
    }


    /**
     * Maj dans la table source.
     *
     * @return true
     */
    public boolean needsSourceUpdatable() {
        return false;
    }


    /**
     * DOCUMENT ME!
     *
     * @return false
     */
    public boolean needsDestinationUpdatable() {
        return true;
    }


    /**
     * DOCUMENT ME!
     *
     * @return 'true' si erreur
     */
    public boolean hasAnomaly() {
        return anomaly > 0;
    }


    /**
                                                                                            	 */
    public void clearAnomaly() {
        anomaly = 0;
        anomalyLog.setLength(0);
    }


    /**
     * DOCUMENT ME!
     *
     * @param rs unused
     */
    public void updateSource(ResultSet rs) {}


    /**
     * Maj des champs dans la table destination.
     *
     * @param stmt Le statement en ecriture sur la destination
     * @param idx L'index de Anomaly
     *
     * @exception SQLException idx invalide
     */
    public void updateDestination(PreparedStatement stmt, int idx)
            throws SQLException {


        stmt.setInt(idx, anomaly);
        if (anomaly == 0) {
            stmt.setNull(idx + 1, java.sql.Types.VARCHAR);
        }
        else {
            stmt.setString(idx + 1, anomalyLog.toString());
        }
    }


    /**
     * Ajoute une erreur
     *
     * @param log Le msg d'erreur
     */
    public void addAnomaly(String log) {
        anomaly++;
        if (anomaly > 1) {
            anomalyLog.append('\n');
        }
        anomalyLog.append(log);
        if (anomalyLog.length() > 254) {
            anomalyLog.setLength(254);
        }
    }
}
