/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.gui.broadcast;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import javax.swing.JComboBox;
import javax.swing.JPanel;
/**
 * Interface des préférences IHM pour la diffusion en Client serveur.
 *
 * @author $Author: poucher $ 29 janvier 2002
 * @version $Revision: 1.4 $
 */
public interface GuiPreferences {
    /**
     * DOCUMENT ME!
     *
     * @return La famille de diffusion.
     */
    public String getFamily();


    /**
     * Retourne le ComboBox des selecteur de Selection pour une famille.
     *
     * @return un combo
     */
    public JComboBox buildSelectionComboBox(Connection con)
          throws SQLException;


    /**
     * Retourne Un JPanel contenant les champs optionnels specifiques a l'application se trouvant dans une
     * table liée a la table des sections.
     *
     * @param con       Description of the Parameter
     * @param sectionId Description of the Parameter
     *
     * @return un pannel
     *
     * @throws SQLException Description of the Exception
     */
    public JPanel buildSectionOptionPanel(Connection con, int sectionId)
          throws SQLException;


    /**
     * Retourne Un JPanel contenant les champs optionnels specifiques a l'application se trouvant dans une
     * table liée a la table des contents.
     *
     * @param con       Description of the Parameter
     * @param contentId Description of the Parameter
     *
     * @return un pannel
     *
     * @throws SQLException Description of the Exception
     */
    public JPanel buildContentOptionPanel(Connection con, int contentId)
          throws SQLException;


    /**
     * Enregistre les données du pannel optionnel des Contents.
     *
     * @param pk    TODO
     * @param panel TODO
     */
    public void saveContentOptionPanel(Map pk, Connection con, JPanel panel)
          throws SQLException;


    /**
     * Enregistre les données du pannel optionnel des Sections.
     *
     * @param pk    TODO
     * @param panel TODO
     */
    public void saveSectionOptionPanel(Map pk, Connection con, JPanel panel)
          throws SQLException;
}
