/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.model;
import net.codjo.persistent.AbstractPersistent;
import net.codjo.persistent.Reference;
/**
 * Classe des groupes de portefeuilles
 *
 * @version $Revision: 1.3 $
 *
 */
public class PortfolioGroup extends AbstractPersistent {
    private String portfolioGroupName;

    /**
     * Constructeur de la classe des groupes de portefeuilles
     *
     * @param ref Description of Parameter
     * @param portfolioGroupName Le nom du groupe de portefeuilles
     */
    public PortfolioGroup(Reference ref, String portfolioGroupName) {
        super(ref);
        this.portfolioGroupName = portfolioGroupName;
    }

    /**
     * Récupère le nom du groupe de portefeuilles
     *
     * @return Le nom du groupe de portefeuilles
     */
    public String getPortfolioGroupName() {
        return portfolioGroupName;
    }


    /**
     * Permet de convertir cet objet en chaine de caractères
     *
     * @return Le nom du groupe de portefeuilles
     */
    public String toString() {
        return portfolioGroupName;
    }


    /**
     * Permet de tester l'égalité entre des objets de ce type.
     *
     * @param obj L'objet à tester
     *
     * @return Egalité VRAI/FAUX
     */
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof PortfolioGroup) {
            return equivalentName(portfolioGroupName,
                (((PortfolioGroup)obj).portfolioGroupName));
        }
        return false;
    }


    /**
     * Permet de tester "l'égalité" entre des noms de portfolio.
     * 
     * <p>
     * Le nom "SANS" est equivalent a tout groupe de portefeuille.
     * </p>
     *
     * @param pfGroupNameA Nom du 1er groupe.
     * @param pfGroupNameB Nom du 2eme groupe.
     *
     * @return 'true' si equivalent
     */
    public static boolean equivalentName(String pfGroupNameA, String pfGroupNameB) {
        if ("SANS".equals(pfGroupNameA)
                || "SANS".equals(pfGroupNameB)
                || pfGroupNameA.equals(pfGroupNameB)) {
            return true;
        }
        else {
            return false;
        }
    }
}
