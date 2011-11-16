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
 * Implementation par defaut de GuiPreferences
 *
 * @author $author$
 * @version $Revision: 1.4 $
 */
public class AbstractGuiPreferences implements GuiPreferences {
    private String family = null;


    protected AbstractGuiPreferences(String family) {
        if (family == null) {
            throw new NullPointerException();
        }

        this.family = family;
    }


    public JPanel buildContentOptionPanel(Connection con, int contentId)
          throws SQLException {
        return null;
    }


    public JPanel buildSectionOptionPanel(Connection con, int sectionId)
          throws SQLException {
        return null;
    }


    public String getFamily() {
        return family;
    }


    public JComboBox buildSelectionComboBox(Connection con)
          throws SQLException {
        return null;
    }


    public void saveContentOptionPanel(Map pk, Connection con, JPanel panel)
          throws SQLException {
    }


    public void saveSectionOptionPanel(Map pk, Connection con, JPanel panel)
          throws SQLException {
    }
}
