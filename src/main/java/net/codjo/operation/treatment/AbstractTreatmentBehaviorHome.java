/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.operation.treatment;
import net.codjo.expression.ExpressionManager;
import net.codjo.expression.FunctionHolder;
import net.codjo.expression.FunctionManager;
import net.codjo.model.PeriodHome;
import net.codjo.model.Table;
import net.codjo.model.TableHome;
import net.codjo.operation.BehaviorLoader;
import net.codjo.persistent.Model;
import net.codjo.persistent.PersistenceException;
import net.codjo.persistent.Persistent;
import net.codjo.persistent.Reference;
import net.codjo.persistent.UnknownIdException;
import net.codjo.persistent.sql.SimpleHome;
import net.codjo.utils.ConnectionManager;
import net.codjo.utils.sql.event.DbChangeListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
/**
 * Classe Home des objets <code>TreatmentBehavior</code> .
 * 
 * <p>
 * Cette classe utilise soit la table <code>PM_TREATMENT_SETTINGS</code> pour
 * PENELOPE/ALIS soit la table <code>PR_PERFORMANCE_SETTINGS</code> pour PARIS.
 * </p>
 *
 * @author $Author: marcona $
 * @version $Revision: 1.7 $
 *
 */
public abstract class AbstractTreatmentBehaviorHome extends SimpleHome
    implements BehaviorLoader {
    private ConnectionManager connectionManager;
    private String expressionTableName = "PM_TREATMENT_EXPRESSION";
    private FunctionManager functionManager = new FunctionManager();
    private PeriodHome periodHome;
    private String settingsTableName = "PM_TREATMENT_SETTINGS";
    private String unitTableName = "PM_TREATMENT_UNIT";

    /**
     * Constructeur.
     *
     * @param con Connection du home
     * @param rb le Ressource Bundle contenant la definition du home
     * @param cm Connection manager pour le comportement
     * @param th Table Home
     * @param periodHome Period Home
     *
     * @exception SQLException
     * @throws IllegalArgumentException TODO
     */
    protected AbstractTreatmentBehaviorHome(Connection con, ResourceBundle rb,
        ConnectionManager cm, TableHome th, PeriodHome periodHome)
            throws SQLException {
        super(con, rb);
        if (th == null || cm == null) {
            throw new IllegalArgumentException();
        }
        this.connectionManager = cm;
        this.periodHome = periodHome;
        settingsTableName = rb.getString("home.dbTableName");
        if (settingsTableName == null) {
            throw new IllegalArgumentException("Le nom physique de la table "
                + " de settings n'est pas renseigne (propriete: home.dbTableName)");
        }

        // Partie spécifique pour PARIS
        // Les noms des tables de paramétrage sont différents
        // et sont définis dans le fichier properties.
        try {
            if (rb.getString("table.expressionTableName") != null) {
                expressionTableName = rb.getString("table.expressionTableName");
            }
            if (rb.getString("table.unitTableName") != null) {
                unitTableName = rb.getString("table.unitTableName");
            }
        }
        catch (java.util.MissingResourceException ex) {}
    }

    /**
     * Retourne l'identifiant de traitement.
     *
     * @return L'identifiant "W"
     */
    public final String getBehaviorID() {
        return "W";
    }


    /**
     * Retourne le label du comportement.
     *
     * @return Le label "Traitement"
     */
    public final String getBehaviorLabel() {
        return "Traitement";
    }


    /**
     * Retourne tous les TREATMENT_SETTINGS_ID dans un tableau d'objets.
     *
     * @return Tous les TREATMENT_SETTINGS_ID
     *
     * @exception SQLException -
     */
    public Object[] getAllId() throws SQLException {
        Statement stmt = getConnection().createStatement();
        List listId = new ArrayList();
        try {
            ResultSet rs =
                stmt.executeQuery("select TREATMENT_SETTINGS_ID" + " from "
                    + settingsTableName);
            while (rs.next()) {
                listId.add(rs.getObject(1));
            }
        }
        finally {
            stmt.close();
        }
        return listId.toArray();
    }


    /**
     * Retourne un listener mettant a jours la couche de persistance au niveau de
     * AbstractTreatmentBehaviorHome lors des changements directe en Base.
     *
     * @return The DbChangeListener value
     *
     * @see net.codjo.persistent.sql.SimpleHome.DefaultDbChangeListener
     */
    public DbChangeListener getDbChangeListener() {
        return new DefaultDbChangeListener();
    }


    /**
     * Gets the FunctionManager attribute of the AbstractTreatmentBehaviorHome object
     *
     * @return The FunctionManager value
     */
    public FunctionManager getFunctionManager() {
        return functionManager;
    }


    /**
     * Retourne le Home gerant le comportement.
     *
     * @return <code>this</code>
     */
    public Model getHome() {
        return this;
    }


    /**
     * Retourne une reference sur un objet <code>TreatmentBehavior</code> .
     *
     * @param behaviorId TRANSLATION_SETTINGS_ID
     *
     * @return Une reference sur un <code>TreatmentBehavior</code>
     */
    public Reference getReference(int behaviorId) {
        return getReference(new Integer(behaviorId));
    }


    /**
     * Ajoute un Porteur de fonction au FunctionManager.
     *
     * @param fh Un objet portant des fonctions pour les expressions
     */
    protected void addFunctionHolder(FunctionHolder fh) {
        functionManager.addFunctionHolder(fh);
    }


    /**
     * Construction de la methode de selection du traitement.
     *
     * @param sourceTable La table source
     * @param destTable La table destination
     * @param selectionTable La table de selection
     * @param sourceType Le type source
     *
     * @return La methode de selection.
     */
    protected abstract TreatmentSelection buildTreatmentSelection(Table sourceTable,
        Table destTable, Table selectionTable, String sourceType);


    /**
     * Construction de la methode de selection d'un lot.
     *
     * @param unitId L'id du lot
     * @param aggregation Mode aggregation
     * @param breakKeys Les clefs de rupture
     * @param behavior Le comportement du lot
     *
     * @return La methode de selection
     *
     * @exception SQLException Erreur base
     * @exception UnknownIdException Id inconnu
     */
    protected abstract TreatmentUnitSelection buildTreatmentUnitSelection(int unitId,
        boolean aggregation, String[] breakKeys, TreatmentBehavior behavior)
            throws SQLException, UnknownIdException;


    /**
     * Determine les clefs utilisee pour faire l'aggregation lors du traitement d'un lot.
     *
     * @param aggregation indique si le lot est en aggregation
     * @param writeMode Mode d'ecriture du lot
     * @param updateSrcKey Description of Parameter
     * @param behavior Le comportement du lot
     *
     * @return la liste des clefs de rupture (ou null)
     */
    protected abstract String[] determineBreakKeys(boolean aggregation, String writeMode,
        String updateSrcKey, TreatmentBehavior behavior);


    /**
     * Termine la creation de l'objet. Surcharge de la methode afin de positionner le
     * <code>ConnectionManager</code> .
     *
     * @param rs -
     * @param ref -
     *
     * @return -
     *
     * @exception PersistenceException
     * @exception SQLException
     */
    protected Persistent loadObject(ResultSet rs, Reference ref)
            throws PersistenceException, SQLException {
        TreatmentBehavior b = (TreatmentBehavior)super.loadObject(rs, ref);

        b.setConnectionManager(connectionManager);
        b.setPeriodHome(periodHome);
        TreatmentSelection s =
            buildTreatmentSelection(b.getSourceTable(), b.getDestTable(),
                b.getSelectionTable(), b.getSourceType());
        b.setTreatmentSelection(s);

        // Chargement des Lots
        List unitList = loadTreatmentUnitList(((Integer)b.getId()).intValue(), b);
        b.setTreatmentUnitList(unitList);

        return b;
    }


    /**
     * Charge les champs de destination définis en variables dans une Map (Nom de la
     * variable, son type SQL). Par défaut, on renvoie une map vide (pas de variable).
     * Il suffit de la remplir dans le TreatmentBehaviorHome de chaque appli pour
     * pouvoir utiliser ce système de variables.
     *
     * @param sourceType Le source type du traitement
     *
     * @return La Map des variables
     *
     * @exception SQLException Erreur base
     */
    protected Map loadVariable(String sourceType)
            throws SQLException {
        return new HashMap();
    }


    /**
     * Construction de l'<code>ExpressionManager</code> .
     *
     * @param unitId L'id du lot
     * @param b Le comportement de l'ExpressionManager
     *
     * @return L'ExpressionManager
     *
     * @exception SQLException Erreur base
     */
    protected ExpressionManager loadExpressionManager(int unitId, TreatmentBehavior b)
            throws SQLException {
        Statement stmt = getConnection().createStatement();
        ExpressionManager em;
        try {
            ResultSet rs =
                stmt.executeQuery("select DB_TARGET_FIELD_NAME, EXPRESSION" + " from "
                    + expressionTableName + " where TREATMENT_UNIT_SETTINGS_ID=" + unitId
                    + " order by PRIORITY");

            em = new ExpressionManager(functionManager);
            em.setDestColumn(b.getDestTable().getAllColumns());
            em.setSourceColumn(b.getSelectionTable().getAllColumns());
            em.setVarColumn(loadVariable(b.getSourceType()));

            while (rs.next()) {
                em.add(rs.getString(1), rs.getString(2));
            }
        }
        finally {
            stmt.close();
        }
        return em;
    }


    /**
     * Chargement d'un lot.
     *
     * @param rs Le ResultSet pointant sur le lot
     * @param b Le comportement du lot
     *
     * @return Un TreatmentUnit
     *
     * @exception SQLException Erreur Base
     * @exception UnknownIdException Id inconnu
     */
    protected TreatmentUnit loadTreatmentUnit(ResultSet rs, TreatmentBehavior b)
            throws SQLException, UnknownIdException {
        int unitId = rs.getInt("TREATMENT_UNIT_SETTINGS_ID");
        boolean aggregation = rs.getBoolean("AGGREGATION");
        String writeMode = rs.getString("WRITE_MODE");
        String updateSrcKey = rs.getString("UPDATE_SOURCE_KEY");

        String[] breakKeys = determineBreakKeys(aggregation, writeMode, updateSrcKey, b);
        BreakDetector breakDetector = new BreakDetector(breakKeys);

        TreatmentUnitSelection selection =
            buildTreatmentUnitSelection(unitId, aggregation, breakKeys, b);

        ExpressionManager expMng = loadExpressionManager(unitId, b);

        return new TreatmentUnit(unitId, expMng, breakDetector, selection,
            b.getDestTable().getDBTableName(), rs.getString("INSERT_CRITERION"),
            writeMode, updateSrcKey, rs.getString("UPDATE_DEST_KEY"),
            rs.getString("UPDATE_CRITERIA"));
    }


    /**
     * Chargement les lots du comportement.
     *
     * @param treatmentSettingsId L'id du comportement
     * @param b Le comportement
     *
     * @return Liste des lot du comportement
     *
     * @exception SQLException Erreur base
     * @exception UnknownIdException Id du comportement
     */
    public List loadTreatmentUnitList(int treatmentSettingsId, TreatmentBehavior b)
            throws SQLException, UnknownIdException {
        List unitList = new java.util.ArrayList();
        Statement stmt = getConnection().createStatement();
        try {
            ResultSet rs =
                stmt.executeQuery("select * " + " from " + unitTableName
                    + " where TREATMENT_SETTINGS_ID=" + treatmentSettingsId);
            while (rs.next()) {
                TreatmentUnit unit = loadTreatmentUnit(rs, b);
                unitList.add(unit);
            }
        }
        finally {
            stmt.close();
        }
        return unitList;
    }

    public String getUnitTableName() {
        return unitTableName;
    }

}
