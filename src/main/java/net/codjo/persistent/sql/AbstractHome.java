/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.persistent.sql;

// Persistance
import net.codjo.persistent.AbstractModel;
import net.codjo.persistent.PersistenceException;
import net.codjo.persistent.Persistent;
import net.codjo.persistent.Reference;
import net.codjo.utils.QueryHelper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
/**
 * Cette Classe facilite l'implantation de la couche de persistence pour les classes Home
 * de Penelope.
 *
 * @author $Author: blazart $
 * @version $Revision: 1.3 $
 *
 */
public abstract class AbstractHome extends AbstractModel {
    /** Le QueryHelper utilise pour acceder a la base. */
    protected QueryHelper queryHelper;

    /**
     * Constructor for the AbstractHome object
     *
     * @param con Description of Parameter
     */
    public AbstractHome(Connection con) {
        super(con);
    }

    /**
     * Retourne toutes les instances de ce home présents dans la base.
     *
     * @return Une liste de Reference
     *
     * @exception PersistenceException -
     */
    public List getAllObjects() throws PersistenceException {
        List allObjects = null;
        try {
            allObjects = loadAllObjects();
        }
        catch (SQLException ex) {
            throw new PersistenceException(ex);
        }
        return allObjects;
    }


    /**
     * Enregistre une reference.
     *
     * @param ref La reference.
     *
     * @exception java.lang.Exception -
     */
    protected void saveSQL(Reference ref) throws java.lang.Exception {
        Persistent obj = ref.getLoadedObject();
        if (obj.isStored()) {
            fillQueryHelperSelector(ref);
            fillQueryHelperForInsert(ref);
            queryHelper.doUpdate();
        }
        else {
            fillQueryHelperForInsert(ref);
            queryHelper.doInsert();
        }
    }


    /**
     * Efface une reference.
     *
     * @param ref La reference.
     *
     * @exception java.lang.Exception -
     */
    protected void deleteSQL(Reference ref) throws java.lang.Exception {
        fillQueryHelperSelector(ref);
        queryHelper.doDelete();
    }


    /**
     * Charge une reference.
     *
     * @param ref Le reference
     *
     * @exception java.lang.Exception -
     * @throws net.codjo.persistent.UnknownIdException TODO
     */
    protected void loadSQL(Reference ref) throws java.lang.Exception {
        fillQueryHelperSelector(ref);
        ResultSet rs = queryHelper.doSelect();

        if (rs.next() == false) {
            throw new net.codjo.persistent.UnknownIdException("Identifiant inconnue : "
                + ref.getId().toString() + "(" + getClass().toString() + ")");
        }

        loadObject(rs, ref);
        rs.close();
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
    protected abstract Reference loadReference(ResultSet rs)
            throws SQLException;


    /**
     * Construction d'une instance de ce Home. L'objet est instancié en fonction des
     * information contenu dans le ResultSet.
     *
     * @param rs Le ResultSet utilise pour la construction.
     * @param ref Reference sur l'instance a construire.
     *
     * @return Une instance non null.
     *
     * @exception SQLException Erreur d'acces a la base
     * @exception PersistenceException Erreur dans la couche de persistence.
     */
    protected abstract Persistent loadObject(ResultSet rs, Reference ref)
            throws SQLException, PersistenceException;


    /**
     * Methode utilitaire qui remplit la requete pour une insertion. L'objet reference
     * doit etre en memoire. De plus, cette methode doit creer un identifiant pour
     * l'enregistrement est necessaire.
     *
     * @param ref La reference de l'objet a inserer.
     *
     * @exception SQLException Si il est impossible de recuperer un ID.
     */
    protected abstract void fillQueryHelperForInsert(Reference ref)
            throws SQLException;


    /**
     * Methode utilitaire qui remplit la clause where du QueryHelper.
     *
     * @param ref La reference utilisee pour remplir la clause.
     */
    protected abstract void fillQueryHelperSelector(Reference ref);


    /**
     * Retourne une liste de toute les instances de ce home.
     *
     * @return Une liste de reference.
     *
     * @exception SQLException En cas d'erreur d'acces a la base
     * @exception PersistenceException -
     */
    private List loadAllObjects() throws SQLException, PersistenceException {
        List allObjects = new java.util.ArrayList();

        ResultSet rs = queryHelper.doSelectAll();
        while (rs.next()) {
            Reference ref = loadReference(rs);
            loadObject(rs, ref);
            allObjects.add(ref);
        }
        rs.close();
        return allObjects;
    }
}
