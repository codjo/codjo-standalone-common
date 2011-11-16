/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.gui;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
/**
 * Construit un filtre d'affichage sur les tables partagées entre les applications
 * PENELOPE, PARIS et ALIS pour l'explorateur des données. Ce filtre est basé sur la
 * valeur du champ RECORD_ACCESS.
 *
 * @version $Revision: 1.3 $
 *
 *
 */
public class ExplorerRecordManagerFilter implements ExplorerFilter {
    private JComboBox recordComboBox = new JComboBox();
    private JLabel recordLabel = new JLabel();

    /**
     * Constructeur.
     */
    public ExplorerRecordManagerFilter() {
        recordLabel.setText("Lignes");
        initComponent();
    }

    /**
     * Retourne le label du filtre.
     *
     * @return Le JLabel.
     */
    public JLabel getLabel() {
        return recordLabel;
    }


    /**
     * Retourne le composant du filtre (ici un combo).
     *
     * @return Le JComponent.
     */
    public JComponent getComponent() {
        return recordComboBox;
    }


    /**
     * Retourne la clause where à utiliser pour le filtrage.
     *
     * @return La String de la clause where.
     */
    public String getWhereClause() {
        StringBuffer whereClause = new StringBuffer("");
        String record = (String)recordComboBox.getSelectedItem();
        if (!"Toutes".equals(record)) {
            if ("INFOCENTRE".equals(record)) {
                whereClause.append("RECORD_ACCESS = 0");
            }
            else if ("Ajout PARIS".equals(record)) {
                whereClause.append("RECORD_ACCESS = 2");
            }
            else if ("Modification PARIS".equals(record)) {
                whereClause.append("RECORD_ACCESS = 3");
            }
            else if ("Suppression PARIS".equals(record)) {
                whereClause.append("RECORD_ACCESS = 1");
            }
        }
        return whereClause.toString();
    }


    /**
     * Retourne le nom DB de la colonne sur laquelle porte le filtre.
     *
     * @return La String du nom DB de la colonne.
     */
    public String getFilterColumnName() {
        return "RECORD_ACCESS";
    }


    /**
     * Initialise le combo des lignes pour le filtrage des données.
     */
    public void initComponent() {
        recordComboBox.addItem("Toutes");
        recordComboBox.addItem("INFOCENTRE");
        recordComboBox.addItem("Ajout PARIS");
        recordComboBox.addItem("Modification PARIS");
        recordComboBox.addItem("Suppression PARIS");
    }
}
