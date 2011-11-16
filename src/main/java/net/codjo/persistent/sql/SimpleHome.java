/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.persistent.sql;

// Persistance
import net.codjo.persistent.PersistenceException;
import net.codjo.persistent.Persistent;
import net.codjo.persistent.Reference;
import net.codjo.utils.QueryHelper;
import net.codjo.utils.SQLFieldList;
import net.codjo.utils.sql.event.DbChangeEvent;
import net.codjo.utils.sql.event.DbChangeListener;
import org.apache.log4j.Logger;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
/**
 * Classe Home generique par Introspection. Cette classe administre un ensemble d'objet.
 * 
 * <p>
 * Une instance de SimpleHome est configure grace a un fichier. Ce fichier est decompose
 * en 5 partie :<br>
 * L'objet gere doit :
 * 
 * <ul>
 * <li>
 * Home spécific :
 * </li>
 * <li>
 * Object - Définition :
 * </li>
 * <li>
 * Primary Key - Définition :
 * </li>
 * <li>
 * Correspondance colonne et property :
 * </li>
 * <li>
 * Traducteurs :
 * </li>
 * </ul>
 * 
 * L'objet administré doit avoir un seul constructeur avec une <code>Reference </code>
 * comme premier argument.
 * </p>
 *
 * @author $Author: blazart $
 * @version $Revision: 1.4 $
 *
 * @see net.codjo.persistent.sql.SimpleHomeTranslator
 */
public abstract class SimpleHome extends AbstractHome {
    // Log
    private static final Logger APP = Logger.getLogger(SimpleHome.class);
    private DbChangeListener dbChangeListener;
    private ResourceBundle homeDef;

    // DB
    private String dbTableName;

    // Mapping property
    private SimpleHomeMapping property;
    private SimpleHomeMapping externalProperty;

    // Traducteur
    private SimpleHomeTranslator translator;

    // Objet
    private SimpleHomeFactory objectFactory;
    private SimpleHomePKFactory pkFactory;

    /**
     * Constructeur.
     *
     * @param con Connection du home
     * @param resb Le ressourceBundle contenant la definition du home
     *
     * @exception SQLException Erreur d'acces a la base
     * @throws RuntimeException si erreur de configuration
     */
    protected SimpleHome(Connection con, ResourceBundle resb)
            throws SQLException {
        super(con);
        homeDef = resb;
        try {
            init();
        }
        catch (SQLException ex) {
            throw ex;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("Erreur de configuration (" + dbTableName + ") :"
                + ex.getLocalizedMessage());
        }
    }

    /**
     * Ajoute un ecouteur sur les modifications BD directe dont ce home est responsable..
     *
     * @param l Le listener
     */
    public void addDbChangeListener(DbChangeListener l) {
        dbChangeListener = l;
    }


    /**
     * Construction d'une reference a partir d'une table de hash.
     *
     * @param pk La map (Colonne / valeur)
     *
     * @return Une instance non null.
     */
    protected Reference getReference(Map pk) {
        return getReference(pkFactory.fillConstructorVals(pk));
    }


    /**
     * Charge un objet (sans les liens externe) a partir du <code>ResultSet
     * </code>donnee. L'objet est construit en appelant le constructeur par Reflection.
     *
     * @param rs Le ResultSet utilise pour la construction.
     * @param ref Reference sur l'instance a construire.
     *
     * @return Une instance non null.
     *
     * @exception SQLException Erreur d'acces a la base
     * @exception PersistenceException Erreur dans la couche de persistence.
     */
    protected Persistent loadObjectInternalProperty(ResultSet rs, Reference ref)
            throws SQLException, PersistenceException {
        if (ref.isLoaded()) {
            return ref.getLoadedObject();
        }

        try {
            Persistent obj = (Persistent)objectFactory.newInstance(rs, ref);
            obj.setStored();
            obj.setSynchronized(true);
            return obj;
        }
        catch (Exception ex) {
            throw newPersistenceException("Chargement ligne", ex);
        }
    }


    /**
     * Charge un objet a partir du <code>ResultSet</code> donnee. L'objet est construit
     * en appelant le constructeur par Reflection.
     *
     * @param rs Le ResultSet utilise pour la construction.
     * @param ref Reference sur l'instance a construire.
     *
     * @return Une instance non null.
     *
     * @exception SQLException Erreur d'acces a la base
     * @exception PersistenceException Erreur dans la couche de persistence.
     */
    protected Persistent loadObject(ResultSet rs, Reference ref)
            throws SQLException, PersistenceException {
        if (ref.isLoaded()) {
            return ref.getLoadedObject();
        }

        try {
            Persistent obj = loadObjectInternalProperty(rs, ref);

            // Positionne les liens externe
            for (int i = 0; i < externalProperty.size(); i++) {
                Object v =
                    translator.translateValue(externalProperty.getName(i),
                        rs.getObject(externalProperty.getColumn(i)));
                externalProperty.setPropertyValue(i, obj, v);
            }
            return obj;
        }
        catch (Exception ex) {
            throw newPersistenceException("Chargement ligne", ex);
        }
    }


    /**
     * Methode utilitaire qui remplit la requete pour une insertion. L'objet reference
     * doit etre en memoire. De plus, cette methode cree un identifiant pour
     * l'enregistrement (si necessaire).
     *
     * @param ref La reference de l'objet a inserer.
     *
     * @exception SQLException Si il est impossible de recuperer un ID.
     */
    protected void fillQueryHelperForInsert(Reference ref)
            throws SQLException {
        debug("Enregistre Reference : " + ref);

        if (ref.getId() == null) {
            buildId(ref);
        }

        Object obj = ref.getLoadedObject();
        try {
            for (int i = 0; i < property.size(); i++) {
                Object value = property.getPropertyValue(i, obj);
                if (value != null && value instanceof Persistent) {
                    value = ((Persistent)value).getId();
                }
                debug("   " + property.getColumn(i) + "=" + value);
                queryHelper.setInsertValue(property.getColumn(i), value);
            }
        }
        catch (Exception ex) {
            throw newReflectionException("Enregistrement ligne", ex);
        }
    }


    /**
     * Construction d'une reference a partir d'un ResultSet.
     *
     * @param rs Le ResultSet a utilise pour la construction.
     *
     * @return Une instance non null.
     *
     * @exception SQLException En cas d'erreur d'acces a la base
     */
    protected Reference loadReference(ResultSet rs)
            throws SQLException {
        return getReference(pkFactory.fillConstructorVals(rs));
    }


    /**
     * Methode utilitaire qui remplit la clause where du QueryHelper.
     *
     * @param ref La reference utilisee pour remplir la clause.
     */
    protected void fillQueryHelperSelector(Reference ref) {
        debug("Requete pour chargement : " + ref.toString());
        try {
            pkFactory.fillSelectorValue(queryHelper, ref);
        }
        catch (Exception ex) {
            throw newReflectionException("Creation clause 'where'", ex);
        }
    }


    /**
     * Retourne une Reference construite avec un tableau d'arguments.
     *
     * @param v Le tableau des arguments utilise pour construire la pk.
     *
     * @return Une reference
     */
    private Reference getReference(Object[] v) {
        try {
            return getReference(pkFactory.newInstance(v));
        }
        catch (Exception ex) {
            throw newReflectionException("Load Reference", ex);
        }
    }


    /**
     * Construit un nouvel id (ou clef primaire) pour une Reference.
     *
     * @param ref La reference a remplir avec un nouvel id.
     */
    private void buildId(Reference ref) {
        try {
            pkFactory.buildId(ref, queryHelper);
        }
        catch (Exception ex) {
            throw newReflectionException("Build ID", ex);
        }
    }


    /**
     * Initialisation du Home.
     *
     * @exception SQLException Erreur acces Base
     * @exception ClassNotFoundException Classe de l'objet ou PK introuvable
     * @exception NoSuchFieldException Erreur de configuration
     * @exception NoSuchMethodException Erreur de configuration
     * @exception IntrospectionException Erreur de configuration
     */
    private void init()
            throws SQLException, ClassNotFoundException, NoSuchFieldException, 
                NoSuchMethodException, IntrospectionException {
        dbTableName = homeDef.getString("home.dbTableName");
        debug("Init Home : " + dbTableName);

        objectFactory = new SimpleHomeFactory(homeDef, "object.");

        pkFactory = new SimpleHomePKFactory(homeDef);

        property =
            new SimpleHomeMapping(homeDef, "property.", objectFactory.getObjectClass(),
                SimpleHomeMapping.INIT_GETTER);

        externalProperty =
            new SimpleHomeMapping(homeDef, "externalProperty.",
                objectFactory.getObjectClass(), SimpleHomeMapping.INIT_SETTER);

        translator = new SimpleHomeTranslator(homeDef, this);

        objectFactory.setPropertyMapping(property);
        objectFactory.setTranslator(translator);
        pkFactory.setPropertyMapping(property);
        pkFactory.setTranslator(translator);

        // Init du QueryHelper
        SQLFieldList tableFields = new SQLFieldList(dbTableName, getConnection());

        SQLFieldList selectById = new SQLFieldList();
        for (Iterator iter = pkFactory.columns(); iter.hasNext();) {
            String fieldName = (String)iter.next();
            int sqlType = tableFields.getFieldType(fieldName);
            selectById.addField(fieldName, sqlType);
        }

        queryHelper =
            new QueryHelper(dbTableName, getConnection(), tableFields, selectById);
    }


    /**
     * Construit une <code>PersistenceException</code> lors d'une erreur dans la
     * mecanique de reflection.
     *
     * @param jobLabel Le nom de la tache lancant l'exception
     * @param ex L'exception lance
     *
     * @return la <code>PersistenceException</code> construite.
     */
    private PersistenceException newPersistenceException(String jobLabel, Exception ex) {
        ex.printStackTrace();
        if (ex instanceof PersistenceException) {
            return (PersistenceException)ex;
        }
        if (ex instanceof IllegalAccessException) {
            return new PersistenceException(ex, jobLabel + " : Access interdit");
        }
        if (ex instanceof IllegalArgumentException) {
            return new PersistenceException(ex, jobLabel + " : Nb argument incorrecte");
        }
        else if (ex instanceof InvocationTargetException) {
            InvocationTargetException invEx = (InvocationTargetException)ex;
            if (invEx.getTargetException() instanceof PersistenceException) {
                return (PersistenceException)invEx.getTargetException();
            }
            else if (invEx.getTargetException() instanceof SQLException) {
                return new PersistenceException((SQLException)invEx.getTargetException());
            }
        }
        return new PersistenceException(ex, jobLabel + " : Erreur inconnue ");
    }


    /**
     * Construit une <code>RuntimeException</code> lors d'une erreur dans la mecanique de
     * reflection.
     *
     * @param jobLabel Le nom de la tache lancant l'exception
     * @param ex L'exception lance
     *
     * @return la <code>RuntimeException</code> construite.
     */
    private RuntimeException newReflectionException(String jobLabel, Exception ex) {
        ex.printStackTrace();
        String exceptionString = ex.getLocalizedMessage();

        if (exceptionString == null) {
            exceptionString = ex.getClass().toString();
        }

        return new RuntimeException("Erreur d'introspection lors de \"" + jobLabel
            + "\" : " + exceptionString);
    }


    /**
     * Fait une trace
     *
     * @param msg message de la trace
     */
    private void debug(String msg) {
        APP.debug("Home(" + dbTableName + ") " + msg);
    }

    /**
     * Classe offrant un comportement par defaut pour la mise a jours de ce Home, lors de
     * modification en directe de la BD.
     * 
     * <p>
     * Lors d'un delete La reference est supprime du buffer. Lors d'un "Modify" la
     * reference est decharge.
     * </p>
     *
     * @author $Author: blazart $
     * @version $Revision: 1.4 $
     */
    /**
     * DOCUMENT ME!
     *
     */
    public class DefaultDbChangeListener implements DbChangeListener {
        /**
         * DOCUMENT ME!
         *
         * @param evt Description of Parameter
         */
        public void succeededChange(DbChangeEvent evt) {
            if (dbChangeListener != null) {
                dbChangeListener.succeededChange(evt);
            }
            if (isBufferOn() == false) {
                return;
            }
            debug(evt.toString());
            switch (evt.getEventType()) {
                case DbChangeEvent.DELETE_EVENT:
                    Reference ref = getReference(evt.getPrimaryKey());
                    ref.unload();
                    removeReference(ref);
                    break;
                case DbChangeEvent.MODIFY_EVENT:
                    getReference(evt.getPrimaryKey()).unload();
                    break;
            }
        }
    }
}
