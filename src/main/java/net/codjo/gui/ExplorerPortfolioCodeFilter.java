/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.gui;
import net.codjo.gui.toolkit.util.ErrorDialog;
import net.codjo.gui.utils.OneKeySelectionManager;
import net.codjo.utils.ConnectionManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
/**
 * Construit un filtre d'affichage sur le champ PORTFOLIO_CODE pour l'explorateur des données.
 *
 * @version $Revision: 1.3 $
 */
public class ExplorerPortfolioCodeFilter implements ExplorerFilter {
    private JComboBox ptfComboBox = new JComboBox();
    private JLabel ptfLabel = new JLabel();
    private DefaultComboBoxModel modelPtf;


    /**
     * Constructeur.
     */
    public ExplorerPortfolioCodeFilter() {
        ptfLabel.setText("Portefeuille");
        initComponent();
        ptfComboBox.setKeySelectionManager(new OneKeySelectionManager());
    }


    /**
     * Retourne le label du filtre.
     *
     * @return Le JLabel.
     */
    public JLabel getLabel() {
        return ptfLabel;
    }


    /**
     * Retourne le composant du filtre (ici un combo).
     *
     * @return Le JComponent.
     */
    public JComponent getComponent() {
        return ptfComboBox;
    }


    /**
     * Retourne la clause where à utiliser pour le filtrage.
     *
     * @return La String de la clause where.
     */
    public String getWhereClause() {
        String whereClause = "";
        String ptf = (String)ptfComboBox.getSelectedItem();
        if (!"Tous".equals(ptf)) {
            whereClause += "PORTFOLIO_CODE='" + ptf + "'";
        }
        return whereClause;
    }


    /**
     * Retourne le nom DB de la colonne sur laquelle porte le filtre.
     *
     * @return La String du nom DB de la colonne.
     */
    public String getFilterColumnName() {
        return "PORTFOLIO_CODE";
    }


    /**
     * Initialise le combo des périodes pour le filtrage des données.
     */
    public void initComponent() {
        Connection con = null;
        Statement stmt = null;
        ConnectionManager conManager = Dependency.getConnectionManager();
        try {
            con = conManager.getConnection();
            stmt = con.createStatement();
            ResultSet rs =
                  stmt.executeQuery(
                        "select PORTFOLIO_CODE from BO_PORTFOLIO group by PORTFOLIO_CODE");
            List ptfList = new ArrayList();
            while (rs.next()) {
                ptfList.add(rs.getObject("PORTFOLIO_CODE"));
            }
            Object[] ptfs = ptfList.toArray();
            Arrays.sort(ptfs);
            modelPtf = new DefaultComboBoxModel(ptfs);
            modelPtf.insertElementAt("Tous", 0);
            ptfComboBox.setModel(modelPtf);
        }
        catch (SQLException ex) {
            ErrorDialog.show(ptfComboBox,
                             "Impossible d'obtenir la liste des portefeuilles", ex);
            ex.printStackTrace();
        }
        finally {
            try {
                conManager.releaseConnection(con, stmt);
            }
            catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }


    /**
     * Gets the ptfComboBox attribute of the ExplorerPortfolioCodeFilter object
     *
     * @return The ptfComboBox value
     */
    public JComboBox getPtfComboBox() {
        return ptfComboBox;
    }


    /**
     * Gets the modelPtf attribute of the ExplorerPortfolioCodeFilter object
     *
     * @return The modelPtf value
     */
    public DefaultComboBoxModel getModelPtf() {
        return modelPtf;
    }
}
