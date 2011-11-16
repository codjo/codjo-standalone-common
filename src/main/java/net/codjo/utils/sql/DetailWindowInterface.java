/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.utils.sql;
import net.codjo.utils.QueryHelper;
import net.codjo.utils.SQLFieldList;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
/**
 * Interface entre les AbstractDetailAction et leurs fenetre de detail.
 *
 * @author $Author: marcona $
 * @version $Revision: 1.4 $
 *
 */
public interface DetailWindowInterface {
    /**
     * Positionne le type d'action qui a causé l'ouverture de l'écran de détail (ajout ou
     * modification).
     *
     * @param actionType Le type d'action (add ou modify)
     */
    public void setActionType(String actionType);


    /**
     * Retourne la fenetre de detail.
     *
     * @return Une JInternalFrame
     */
    public JInternalFrame getInternalFrame();


    /**
     * Rempli un JTextComponent ou un JCheckBox des écrans page avec une valeur par
     * défaut.
     *
     * @param defaultValues Description of Parameter
     */
    public void fillDefaultValues(HashMap defaultValues);


    /**
     * Retourne la Bouton "Appliquer". Ce bouton est optionnel.
     *
     * @return Le bouton (ou null si non defini)
     */
    public JButton getApplyButton();


    /**
     * Retourne la Bouton "Précédent". Ce bouton est optionnel.
     *
     * @return Le bouton (ou null si non defini)
     */
    public JButton getPreviousButton();


    /**
     * Retourne la Bouton "Suivant". Ce bouton est optionnel.
     *
     * @return Le bouton (ou null si non defini)
     */
    public JButton getNextButton();


    /**
     * Retourne la Bouton "Enregistrer".
     *
     * @return Le bouton
     */
    public JButton getOkButton();


    /**
     * Retourne la Bouton "Annuler".
     *
     * @return Le bouton
     */
    public JButton getCancelButton();


    /**
     * Retourne les noms des attributs (composants graphiques) définis dans la fenêtre de
     * détail.
     *
     * @return Liste des noms (String).
     */
    public java.util.List getListOfComponents();


    /**
     * Remplit le QueryHelper avec les valeurs des colonnes avec les champs de la fenetre
     * detail.
     *
     * @param columns Liste de colonnes a remplir dans le query helper.
     * @param qh Le QueryHelper a remplir.
     */
    public void fillQueryHelper(SQLFieldList columns, QueryHelper qh);


    /**
     * Rempli les Composants des écrans page avec les valeurs des champs correspondant.
     * 
     * <p>
     * L'iteration sur le ResultSet est faite dans la methode.
     * </p>
     *
     * @param columns Liste des colonnes
     * @param rs ResultSet des valeurs à renseigner
     *
     * @exception SQLException -
     */
    public void fillComponent(SQLFieldList columns, ResultSet rs)
            throws SQLException;


    /**
     * Enregistre les liens (repercute les modifications sur les tables liees).
     * 
     * <p>
     * Remarque : Lorsque cette methode est appele, le detail est deja enregistre dans
     * une transaction non committe.
     * </p>
     *
     * @param pk La clef primaire de l'ecran detail (Nom/valeur)
     * @param con La connection de la transaction
     *
     * @exception Exception
     */
    public void saveLinks(Map pk, Connection con)
            throws Exception;
}
