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
 * Report les anomalies dans la table source.
 *
 * @author $Author: blazart $
 * @version $Revision: 1.6 $
 *
 */
public class SourceAnomalyReport implements AnomalyReport {
    transient StringBuffer anomalyLog = new StringBuffer();
    transient int anomaly = 0;

    /**
     * Constructor for the SourceAnomalyReport object
     */
    public SourceAnomalyReport() {}

    /**
     * Ecriture autorise si aucune erreur.
     *
     * @return 'true' si aucune erreur
     */
    public boolean isWriteAllowed() {
        return hasAnomaly() == false;
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
        return true;
    }


    /**
     * DOCUMENT ME!
     *
     * @return false
     */
    public boolean needsDestinationUpdatable() {
        return false;
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
     * Maj des champs dans la table source.
     *
     * @param rs ResultSet updatable sur la table source.
     *
     * @exception SQLException Erreur base
     */
    public void updateSource(ResultSet rs) throws SQLException {
        rs.updateInt("ANOMALY", anomaly);
        if (anomaly == 0) {
            rs.updateNull("ANOMALY_LOG");
        }
        else {
            rs.updateString("ANOMALY_LOG", anomalyLog.toString());
        }
        rs.updateRow();
    }


    /**
     * DOCUMENT ME!
     *
     * @param stmt unused
     * @param idx unused
     */
    public void updateDestination(PreparedStatement stmt, int idx) {}


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
