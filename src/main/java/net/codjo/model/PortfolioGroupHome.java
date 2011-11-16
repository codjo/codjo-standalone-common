/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.model;
import net.codjo.persistent.PersistenceException;
import net.codjo.persistent.Reference;
import net.codjo.persistent.UnknownIdException;
import net.codjo.persistent.sql.SimpleHome;
import net.codjo.utils.sql.event.DbChangeListener;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
/**
 * Classe qui fait le lien entre l'objet PortfolioGroup et la BDD.
 *
 * @version $Revision: 1.3 $
 *
 */
public class PortfolioGroupHome extends SimpleHome {
    /**
     * Constructeur de l'objet PortfolioGroupHome
     *
     * @param con Une connection a la base
     *
     * @exception SQLException En cas d'erreur lors de l'acces a la base.
     */
    public PortfolioGroupHome(Connection con) throws SQLException {
        super(con, ResourceBundle.getBundle("PortfolioGroup"));
    }

    /**
     * Retourne une reference sur un gourpe de portefeuille.
     *
     * @param id PORTFOLIO_GROUP_ID
     *
     * @return Une reference
     */
    public Reference getReference(int id) {
        return getReference(new Integer(id));
    }


    /**
     * Retourne un listener mettant a jours la couche de persistance au niveau de
     * PortfolioGroupHome lors des changements directe en Base.
     *
     * @return The DbChangeListener value
     *
     * @see net.codjo.utils.SimpleHome#DefaultDbChangeListener
     */
    public DbChangeListener getDbChangeListener() {
        return new DefaultDbChangeListener();
    }


    /**
     * Récupère la liste des groupes de portefeuilles existants dans la BDD
     *
     * @return La liste des groupes de portefeuilles
     *
     * @exception PersistenceException Erreur dans la couche de persistance.
     */
    public List getAllPortfolioGroup() throws PersistenceException {
        List allRefObj = getAllObjects();
        List allObj = new ArrayList();
        for (Iterator iter = allRefObj.iterator(); iter.hasNext();) {
            Reference ref = (Reference)iter.next();
            allObj.add(ref.getObject());
        }
        return allObj;
    }


    /**
     * Récupère la liste des groupes de portefeuilles existants dans la BDD sans le
     * groupe 'SANS' et 'TOUT'.
     *
     * @return La liste de Reference
     *
     * @exception PersistenceException En cas d'erreur lors de la recuperation.
     */
    public List getAllRealPortfolioGroup() throws PersistenceException {
        List allRefObj = getAllObjects();
        List realList = new ArrayList();

        for (Iterator iter = allRefObj.iterator(); iter.hasNext();) {
            Reference ref = (Reference)iter.next();
            PortfolioGroup ptf = (PortfolioGroup)ref.getObject();
            if (!"TOUT".equals(ptf.getPortfolioGroupName())
                    && (!"SANS".equals(ptf.getPortfolioGroupName()))) {
                realList.add(ref);
            }
        }

        return realList;
    }


    /**
     * Retourne le Groupe de portfeuille.
     *
     * @param pfGroupName Description of Parameter
     *
     * @return Le groupe de portefeuille.
     *
     * @exception PersistenceException En cas d'erreur
     * @throws UnknownIdException TODO
     *
     * @todo Cette methode n'est pas optimiser. Elle recupere toutes les Grp de
     *       portefeuille pour recuperer un Groupe
     */
    public PortfolioGroup getPortfolioGroup(String pfGroupName)
            throws PersistenceException {
        List allRefObj = getAllObjects();
        for (Iterator iter = allRefObj.iterator(); iter.hasNext();) {
            Reference ref = (Reference)iter.next();
            PortfolioGroup ptf = (PortfolioGroup)ref.getObject();
            if (pfGroupName.equals(ptf.getPortfolioGroupName())) {
                return ptf;
            }
        }
        throw new UnknownIdException("Nom de groupe de portefeuille inconnue : "
            + pfGroupName);
    }


    /**
     * Creation d'un groupe de portefeuille. Le nouveau groupe cree est enregistres sans
     * faire aucun commit.
     *
     * @param name Le nom du groupe de portefeuille
     *
     * @return Le nouveau groupe.
     *
     * @exception PersistenceException Si impossibilite de creer le groupe.
     */
    public PortfolioGroup newPortfolioGroup(String name)
            throws PersistenceException {
        PortfolioGroup obj = new PortfolioGroup(new Reference(this), name);
        return obj;
    }
}
