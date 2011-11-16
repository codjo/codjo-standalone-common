/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.gui;

//JAVA
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
/**
 * Construit un filtre d'affichage sur le champ ANOMALY pour l'explorateur des données.
 *
 * @version $Revision: 1.4 $
 */
public class ExplorerAnomalyFilter implements ExplorerFilter {
    private JComboBox anomalyComboBox = new JComboBox();
    private JLabel anomalyLabel = new JLabel();

    /**
     * Constructeur.
     */
    public ExplorerAnomalyFilter() {
        anomalyLabel.setText("Anomalie");
        initComponent();
    }

    /**
     * Retourne le label du filtre.
     *
     * @return Le JLabel.
     */
    public JLabel getLabel() {
        return anomalyLabel;
    }


    /**
     * Retourne le composant du filtre (ici un combo).
     *
     * @return Le JComponent.
     */
    public JComponent getComponent() {
        return anomalyComboBox;
    }


    /**
     * Retourne la clause where à utiliser pour le filtrage.
     *
     * @return La String de la clause where.
     */
    public String getWhereClause() {
        String whereClause = "";
        String ano = (String)anomalyComboBox.getSelectedItem();
        if (!"Toutes".equals(ano)) {
            whereClause += "ANOMALY" + ano;
        }
        return whereClause;
    }


    /**
     * Retourne le nom DB de la colonne sur laquelle porte le filtre.
     *
     * @return La String du nom DB de la colonne.
     */
    public String getFilterColumnName() {
        return "ANOMALY";
    }


    /**
     * Initialise le combo des anomalies pour le filtrage des données.
     */
    public void initComponent() {
        anomalyComboBox.addItem("Toutes");
        anomalyComboBox.addItem("= -1");
        anomalyComboBox.addItem("= 0");
        anomalyComboBox.addItem(">= 1");
    }
}
