/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.gui.model;
import net.codjo.model.Period;
import net.codjo.model.PeriodHome;
import net.codjo.persistent.Reference;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
/**
 * Composant graphique synchronisé avec la période courante de l'application.
 *
 * @author $Author: blazart $
 * @version $Revision: 1.3 $
 *
 *
 */
public class PeriodComboBox extends javax.swing.JComboBox {
    private InternalListener listener = new InternalListener();
    private DefaultComboBoxModel model;
    private PeriodHome periodHome;

    /**
     * Constructor.
     */
    public PeriodComboBox() {}


    /**
     * Constructor.
     *
     * @param periodHome Le PeriodHome
     */
    public PeriodComboBox(PeriodHome periodHome) {
        setPeriodHome(periodHome);
    }

    /**
     * Positionne le periodHome.
     *
     * @param periodHome Description of Parameter
     */
    public void setPeriodHome(PeriodHome periodHome) {
        this.periodHome = periodHome;
        updateVisiblePeriodList();
        setSelectedItem(this.periodHome.getCurrentPeriod());

        this.addActionListener(listener);
        this.periodHome.addPropertyChangeListener(listener);
    }


    /**
     * Reconstruit le contenu de la comboBox avec les periodes.
     */
    private void updateVisiblePeriodList() {
        try {
            List list = periodHome.getAllObjects();
            List periods = new ArrayList();
            for (int i = 0; i < list.size(); i++) {
                Reference ref = (Reference)list.get(i);
                periods.add(ref.getObject());
            }
            model = new DefaultComboBoxModel(periods.toArray());
            setModel(model);
        }
        catch (net.codjo.persistent.PersistenceException ex) {
            ex.printStackTrace();
            removeActionListener(listener);
            periodHome.removePropertyChangeListener(listener);
            setEnabled(false);
        }
    }


    /**
     * Indique si la period appartient au comboBox.
     *
     * @param period la periode
     *
     * @return 'true' si la periode est contenu dans la comboBox.
     */
    private boolean contains(Object period) {
        return model.getIndexOf(period) >= 0;
    }

    /**
     * Internal Listener au ComboBox.
     * 
     * <p>
     * Ce listener ecoute les changements sur le ComboBox, et de la periode courante.
     * </p>
     *
     * @author $Author: blazart $
     * @version $Revision: 1.3 $
     */
    class InternalListener implements ActionListener, PropertyChangeListener {
        /**
         * Ecoutes le changement de la période courante.
         *
         * @param evt
         */
        public void propertyChange(PropertyChangeEvent evt) {
            if (isVisible() == false) {
                return;
            }

            removeActionListener(this);
            if (contains(evt.getNewValue()) == false) {
                updateVisiblePeriodList();
            }
            setSelectedItem(evt.getNewValue());
            addActionListener(this);
        }


        /**
         * Ecoutes les changements sur le comboBox.
         *
         * @param parm1
         */
        public void actionPerformed(ActionEvent parm1) {
            if (isVisible() == false) {
                return;
            }

            Period period = (Period)getSelectedItem();
            if (period != null) {
                periodHome.setCurrentPeriod(period);
            }
        }
    }
}
