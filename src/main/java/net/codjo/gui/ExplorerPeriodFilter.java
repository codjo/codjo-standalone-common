/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.gui;
import net.codjo.gui.toolkit.util.ErrorDialog;
import net.codjo.gui.utils.OneKeySelectionManager;
import net.codjo.model.PeriodHome;
import net.codjo.persistent.Reference;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
/**
 * Construit un filtre d'affichage sur le champ PERIOD pour l'explorateur des données.
 *
 * @version $Revision: 1.3 $
 */
public class ExplorerPeriodFilter implements ExplorerFilter {
    private PeriodHome periodHome;
    private JComboBox periodComboBox = new JComboBox();
    private JLabel periodLabel = new JLabel();
    private DefaultComboBoxModel modelPeriod = new DefaultComboBoxModel();


    /**
     * Constructeur.
     *
     * @param ph Le PeriodHome.
     */
    public ExplorerPeriodFilter(PeriodHome ph) {
        periodHome = ph;
        periodLabel.setText("Période");
        initComponent();
        PeriodPropertyListener listener = new PeriodPropertyListener();
        periodHome.addPropertyChangeListener(listener);
        periodComboBox.setKeySelectionManager(new OneKeySelectionManager());
    }


    /**
     * Retourne le label du filtre.
     *
     * @return Le JLabel.
     */
    public JLabel getLabel() {
        return periodLabel;
    }


    /**
     * Retourne le composant du filtre (ici un combo).
     *
     * @return Le JComponent.
     */
    public JComponent getComponent() {
        return periodComboBox;
    }


    /**
     * Retourne la clause where à utiliser pour le filtrage.
     *
     * @return La String de la clause where.
     */
    public String getWhereClause() {
        return "PERIOD='" + periodComboBox.getSelectedItem().toString() + "'";
    }


    /**
     * Retourne le nom DB de la colonne sur laquelle porte le filtre.
     *
     * @return La String du nom DB de la colonne.
     */
    public String getFilterColumnName() {
        return "PERIOD";
    }


    /**
     * Initialise le combo des périodes pour le filtrage des données.
     */
    public void initComponent() {
        try {
            List list = periodHome.getAllObjects();
            Object[] periods = new Object[list.size()];
            for (int i = 0; i < periods.length; i++) {
                Reference ref = (Reference)list.get(i);
                periods[i] = ref.getObject();
            }
            DefaultComboBoxModel modelPeriod = new DefaultComboBoxModel(periods);
            periodComboBox.setModel(modelPeriod);
            periodComboBox.setSelectedItem(periodHome.getCurrentPeriod());
            //periodComboBox.setSelectedIndex(periodComboBox.getItemCount() - 1);
        }
        catch (net.codjo.persistent.PersistenceException ex) {
            ErrorDialog.show(periodComboBox,
                             "Impossible d'obtenir la liste des periodes", ex);
            ex.printStackTrace();
            periodComboBox.setEnabled(false);
        }
    }


    /**
     * Description of the Class
     *
     * @author VIRASIS
     */
    class PeriodPropertyListener implements PropertyChangeListener {
        /**
         * Ecoutes le changement de la période courante.
         */
        public void propertyChange(PropertyChangeEvent evt) {
            //Si la période couratne est nouvelle
            if (modelPeriod.getIndexOf(evt.getNewValue()) < 0) {
                initComponent();
                //Maj de la le liste des périodes
            }
            periodComboBox.setSelectedItem(evt.getNewValue());
        }
    }
}
