/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.operation.treatment;
import net.codjo.model.PeriodHome;
import net.codjo.model.Table;
import net.codjo.operation.Behavior;
import net.codjo.operation.Operation;
import net.codjo.operation.OperationFailureException;
import net.codjo.persistent.PersistenceException;
import net.codjo.persistent.Reference;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;
/**
 * Cette classe definit le comportement d'une operation de traitement.
 *
 * @author $Author: marcona $
 * @version $Revision: 1.7 $
 *
 */
public class TreatmentBehavior extends Behavior {
    private List treatmentUnitList = null;
    private Reference selectionTableRef;
    private String sourceType;
    private PeriodHome periodHome;
    private TreatmentSelection trtSelection;
    private boolean errorNumberSurchage;

    /**
     * Constructor.
     *
     * @param selfRef Self Reference
     * @param source Table source
     * @param dest Table destination
     * @param sel Table sélection
     * @param sourceType Source type
     * @param surchage
     */
    public TreatmentBehavior(Reference selfRef, Table source, Table dest, Table sel,
        String sourceType, boolean surchage) {
        super(selfRef, source, dest);
        setSourceType(sourceType);
        setSelectionTable(sel);
        this.errorNumberSurchage = surchage;
    }


    public TreatmentBehavior(Reference selfRef, Table source, Table dest, Table sel,
        String sourceType) {
        this(selfRef, source, dest, sel, sourceType, true);
    }

    /**
     * Sets the TreatmentSelection attribute of the TreatmentBehavior object
     *
     * @param t The new TreatmentSelection value
     */
    public void setTreatmentSelection(TreatmentSelection t) {
        trtSelection = t;
    }


    /**
     * Positionne la liste des <code>TreatmentUnit</code> .
     *
     * @param treatmentUnitList Liste de <code>TreatmentUnit</code>
     */
    public void setTreatmentUnitList(List treatmentUnitList) {
        this.treatmentUnitList = treatmentUnitList;
    }


    /**
     * Sets the SelectionTable attribute of the TreatmentBehavior object
     *
     * @param newSelectionTable The new SelectionTable value
     */
    public void setSelectionTable(Table newSelectionTable) {
        this.selectionTableRef = newSelectionTable.getReference();
    }


    /**
     * Sets the SourceType attribute of the TreatmentBehavior object
     *
     * @param newSourceType The new SourceType value
     */
    public void setSourceType(String newSourceType) {
        sourceType = newSourceType;
    }


    /**
     * Sets the PeriodHome attribute of the TreatmentBehavior object
     *
     * @param newPeriodHome The new PeriodHome value
     */
    public void setPeriodHome(net.codjo.model.PeriodHome newPeriodHome) {
        periodHome = newPeriodHome;
    }


    public void setErrorNumberSurchage(boolean b) {
        this.errorNumberSurchage = b;
    }


    /**
     * Gets the SelectionTable attribute of the TreatmentBehavior object
     *
     * @return The SelectionTable value
     */
    public Table getSelectionTable() {
        return (Table)selectionTableRef.getLoadedObject();
    }


    /**
     * Gets the SourceType attribute of the TreatmentBehavior object
     *
     * @return The SourceType value
     */
    public String getSourceType() {
        return sourceType;
    }


    /**
     * Gets the PeriodHome attribute of the TreatmentBehavior object
     *
     * @return The PeriodHome value
     */
    public net.codjo.model.PeriodHome getPeriodHome() {
        return periodHome;
    }


    /**
     * Gets the TreatmentUnitList attribute of the TreatmentBehavior object
     *
     * @return The TreatmentUnitList value
     */
    public List getTreatmentUnitList() {
        return treatmentUnitList;
    }


    /**
     * Gets the TreatmentSelection attribute of the TreatmentBehavior object
     *
     * @return The TreatmentSelection value
     */
    public TreatmentSelection getTreatmentSelection() {
        return trtSelection;
    }


    public boolean getErrorNumberSurchage() {
        return errorNumberSurchage;
    }


    /**
     * DOCUMENT ME!
     *
     * @exception PersistenceException
     */
    public void prepareProceed() throws PersistenceException {}


    /**
     * Determine la periode precedante.
     *
     * @param currentPeriod La periode courante
     *
     * @return La periode precedente
     *
     * @exception java.text.ParseException Impossible de decoder la periode courante,
     *            elle n'est pas au format 'YYYYMM'
     */
    public String determinePreviousPeriod(String currentPeriod)
            throws java.text.ParseException {
        return periodHome.determinePreviousPeriod(currentPeriod);
    }


    /**
     * Affine la méthode de détermination de la longueur de l'opération à effectuer afin
     * de pouvoir récupérer le nombre de lignes à traiter non pas sur la table source
     * mais sur la table de sélection. Pour cela, cette méthode est appelée après le
     * doSelect qui rempli la table de sélection.
     * 
     * <p>
     * <b>Attention</b> : La methode remet a jour l'attribut lengthOfTask.
     * </p>
     *
     * @param con Une connection
     *
     * @exception OperationFailureException Erreur lors du traitement
     */
    public void refineDetermineLengthOfTask(Connection con)
            throws OperationFailureException {}


    /**
     * Lance le traitement avec une operation en paramétre (point d'entrée pour Penelope
     * et Alis).
     *
     * @param ope Une operation
     *
     * @exception SQLException Erreur de parametrage
     * @exception java.text.ParseException Determination de la periode precedente
     *            impossible
     * @exception OperationFailureException Erreur lors du traitement
     */
    public void proceed(Operation ope)
            throws SQLException, java.text.ParseException, OperationFailureException {
        Connection con = null;
        try {
            con = getConnectionManager().getConnection();
            proceed(new TreatmentData(ope), con);
        }
        finally {
            getConnectionManager().releaseConnection(con);
        }
    }


    /**
     * Lance le traitement avec une operation en paramétre (point d'entrée pour Paris).
     * OperationData est une interface permettant d'adapter le code en fonction de
     * l'application.
     *
     * @param ope Une OperationData
     * @param con Une connection
     *
     * @exception SQLException Erreur de parametrage
     * @exception java.text.ParseException Determination de la periode precedente
     *            impossible
     * @exception OperationFailureException Erreur lors du traitement
     */
    public void proceed(OperationData ope, Connection con)
            throws SQLException, java.text.ParseException, OperationFailureException {
        setCurrentOfTask(0);
        int nbError = 0;

        String currentPeriod = ope.getPeriod();
        String previousPeriod = determinePreviousPeriod(currentPeriod);

        try {
            try {
                trtSelection.doSelect(con, currentPeriod, previousPeriod,
                    ope.getPortfolioGroupName());
                refineDetermineLengthOfTask(con);
            }
            catch (SQLException ex) {
                if ("Certains mouvements annulés sont introuvables".equals(ex.getMessage())) {
                    nbError++;
                }
                else {
                    throw new OperationFailureException("Erreur SQL : " + ex.getMessage());
                }
            }

            for (Iterator iter = treatmentUnitList.iterator(); iter.hasNext();) {
                TreatmentUnit unit = (TreatmentUnit)iter.next();
                unit.proceed(con, ope);
                nbError = nbError + unit.getNbError();
            }
        }
        catch (SQLException ex) {
            throw new OperationFailureException("Erreur SQL : " + ex.getMessage());
        }
        catch (RuntimeException ex) {
            throw new OperationFailureException("Erreur de paramétrage : "
                + ex.toString());
        }

        if ((ope instanceof TreatmentData) && (getErrorNumberSurchage() == true)) {
            nbError =
                getRealErrorNumber(con, getSourceTable(),
                    ((TreatmentData)ope).getOperation());
        }
        if (nbError > 0) {
            throw new OperationFailureException("Il y a " + nbError
                + " ligne(s) en erreur pour le traitement de la table '"
                + getSourceTable().getTableName() + "' vers la table '"
                + getDestTable().getTableName() + "'");
        }
    }


    /**
     * Determine le nombre de lignes à traiter pour les lots du traitement à faire. La
     * methode met a jour l'attribut lengthOfTask.
     *
     * @param ope Operation courante (non utilisee)
     *
     * @exception SQLException Description of Exception
     */
    public void determineLengthOfTask(Operation ope)
            throws SQLException {
        Connection con = getConnectionManager().getConnection();
        Statement stmt = null;

        try {
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(buildSelectSourceQuery(ope));
            if (rs.next()) {
                setLengthOfTask(rs.getInt(1));
            }
        }
        finally {
            getConnectionManager().releaseConnection(con, stmt);
        }
    }


    /**
     * Calcule le nombre reel d'erreur, ie utilise la table source
     *
     * @param con
     * @param srcDb
     * @param ope
     *
     * @return
     *
     * @throws IllegalArgumentException TODO
     */
    private int getRealErrorNumber(Connection con, Table srcDb, Operation ope) {
        int result = -1;
        String sqlQuery = "select count(1) from ";
        sqlQuery += ope.buidTableClauseFor(srcDb);

        String whereClause = ope.buildWhereClauseFor(srcDb);
        if (whereClause == null) {
            whereClause = " where " + srcDb.getDBTableName() + ".ANOMALY >= 1";
        }
        else {
            whereClause += " and " + srcDb.getDBTableName() + ".ANOMALY >= 1";
        }
        sqlQuery = sqlQuery + whereClause;
        try {
            Statement stmt = con.createStatement();
            ResultSet res = stmt.executeQuery(sqlQuery);
            if (res.next()) {
                result = res.getInt(1);
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
            throw new IllegalArgumentException(ex.getLocalizedMessage());
        }
        return result;
    }


    /**
     * Construit une requete <code>select count</code> sur la table source.
     *
     * @param ope Description of Parameter
     *
     * @return Une requete SQL <code>select</code>
     *
     * @exception SQLException Description of Exception
     */
    private String buildSelectSourceQuery(Operation ope)
            throws SQLException {
        Table srcDb = getSourceTable();

        String query = "select count(ANOMALY) from ";
        query += ope.buidTableClauseFor(srcDb);

        String whereClause = ope.buildWhereClauseFor(srcDb);
        if (whereClause == null) {
            whereClause = " where ANOMALY=-1";
        }
        else {
            whereClause += " and ANOMALY=-1";
        }
        return query + whereClause;
    }
}
