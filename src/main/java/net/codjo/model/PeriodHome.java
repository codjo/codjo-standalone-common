/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.model;

// Persistance
import net.codjo.persistent.PersistenceException;
import net.codjo.persistent.sql.SimpleHome;
import net.codjo.utils.sql.event.DbChangeListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;
/**
 * Classe qui fait le lien entre l'objet Period et la BDD.
 *
 * <p> Cette classe lance un evenement lors de la modification de la periode courante (property
 * "currentPeriod"). </p>
 *
 * @version $Revision: 1.3 $
 * @see net.codjo.model.Period
 */
public class PeriodHome extends SimpleHome {
    private static final DateFormat periodDateFormat = new SimpleDateFormat("yyyyMM");
    private Period currentPeriod;
    private transient PropertyChangeSupport propertyChangeListeners =
          new PropertyChangeSupport(this);


    /**
     * Constructeur de l'objet PeriodHome
     *
     * @param con Connection a la base.
     *
     * @throws SQLException En cas d'erreur lors de l'acces a la base.
     */
    public PeriodHome(java.sql.Connection con) throws SQLException {
        super(con, ResourceBundle.getBundle("PeriodHome"));
        periodDateFormat.setLenient(false);
    }


   /**
     * Constructeur de l'objet PeriodHome
     *
     * @param con Connection a la base.
     * @param resb Le ressourceBundle contenant la definition du home
     *
     * @throws SQLException En cas d'erreur lors de l'acces a la base.
     */
    public PeriodHome(java.sql.Connection con, ResourceBundle resb) throws SQLException {
        super(con, resb);
        periodDateFormat.setLenient(false);
    }

    /**
     * Positionne la periode courante
     *
     * @param newCurrentPeriod La nouvelle periode courante
     */
    public void setCurrentPeriod(Period newCurrentPeriod) {
        Period oldCurrentPeriod = currentPeriod;
        currentPeriod = newCurrentPeriod;
        propertyChangeListeners.firePropertyChange("currentPeriod", oldCurrentPeriod,
                                                   newCurrentPeriod);
    }


    /**
     * Retourne un listener mettant a jours la couche de persistance au niveau de PortfolioGroupHome lors des
     * changements directe en Base.
     *
     * @return The DbChangeListener value
     */
    public DbChangeListener getDbChangeListener() {
        return new DefaultDbChangeListener();
    }


    /**
     * Retourne la periode precedante.
     *
     * @param period La periode courante
     *
     * @return La periode precedente
     *
     * @throws PersistenceException Periode precedente non definie.
     * @throws ParseException       Impossible de decoder la periode courante, elle n'est pas au format
     *                              'YYYYMM'
     */
    public Period getPreviousPeriod(Period period)
          throws PersistenceException, ParseException {
        String prevPeriodTxt = determinePreviousPeriod(period.getPeriod());
        return (Period)getReference(prevPeriodTxt).getObject();
    }


    /**
     * Retourne la periode courante.
     *
     * @return La periode courante (ou null)
     */
    public Period getCurrentPeriod() {
        return currentPeriod;
    }


    /**
     * Determine le nom de la periode precedante.
     *
     * @param period La periode courante
     *
     * @return La periode precedente
     *
     * @throws ParseException Impossible de decoder la periode courante, elle n'est pas au format 'YYYYMM'
     */
    public String determinePreviousPeriod(String period)
          throws ParseException {
        Date date = periodDateFormat.parse(period.substring(0, 6));

        Calendar calendar = periodDateFormat.getCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, -1);

        return periodDateFormat.format(calendar.getTime());
    }


    /**
     * Enleve un listener.
     *
     * @param l Le listener
     */
    public synchronized void removePropertyChangeListener(PropertyChangeListener l) {
        propertyChangeListeners.removePropertyChangeListener(l);
    }


    /**
     * Ajoute un listener sur une Property de ce home.
     *
     * @param l Le Listener
     */
    public synchronized void addPropertyChangeListener(PropertyChangeListener l) {
        propertyChangeListeners.addPropertyChangeListener(l);
    }
}
