/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.profile;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
/**
 * Classe decrivant l'utilisateur de l'application.
 *
 * <p> Un fichier se trouvant dans le CLASS_PATH, nommé "profile.properties" contient la définition de tous
 * les profiles. Un profile est déterminé par un ensemble de clef/valeur: ou les clefs définissent une action
 * et les valeurs definissent un ou des groupes qui sont autorisés pour faire l'action. </p>
 *
 * <p> <b>NB</b> : La localisation du fichier "profile.properties" peut etre changée en positionnant la
 * properties system "net.codjo.profile.file" (e.g. <code>System.setProperty("net.codjo.profile.file",
 * "/preferences/profileAlis.properties");</code> . Cette property doit être positionnée avant le premier
 * appel a getUserFor(). </p>
 *
 * <p> Exemple de fichier :
 * <pre>
 *  ....
 *  SEE_ADMIN_MENU = Maintenance
 *  DELETE_QUOTATION = Maintenance, Reporting
 *  ...
 *  </pre>
 * </p>
 *
 * <p> Exemple d'utilisation :
 * <pre>
 *  <au démarrage de l'application> user = User.getUserFor(con);
 *  ...
 *  if (user.isAllowedTo("SEE_ADMIN_MENU"))
 *  buildAdminMenu();
 *  </pre>
 * </p>
 *
 * @author $Author: blazart $
 * @version $Revision: 1.3 $
 */
public class User {
    private static final String GET_GROUP_QUERY =
          "select name from sysusers "
          + " where gid = (select gid from sysusers where uid =(select user_id())) "
          + "  and uid = gid";
    private Properties allProfiles = new Properties();
    private String group;
    private String name;


    User(String name, String group) throws IOException {
        if (name == null || group == null) {
            throw new IllegalArgumentException();
        }
        this.name = name;
        this.group = group;
        loadAllProfiles();
    }


    protected User() {
    }


    /**
     * Factory method permettant de creer l'Utilisateur attaché a cette connection.
     *
     * @param con une connection
     *
     * @return L'utilisateur
     *
     * @throws SQLException Impossible de déterminer le group ou le profile
     * @throws IOException  Impossible de charger le profile.
     */
    public static User getUserFor(Connection con)
          throws SQLException, IOException {
        Statement stmt = con.createStatement();
        try {
            ResultSet rs = stmt.executeQuery(GET_GROUP_QUERY);
            if (rs.next() == false) {
                throw new SQLException("La base SYBASE est incapable de "
                                       + "renvoyer le groupe de l'utilisateur");
            }
            String group = rs.getString(1);
            return new User(con.getMetaData().getUserName(), group);
        }
        finally {
            stmt.close();
        }
    }


    /**
     * Retourne le groupe de l'utilisateur.
     *
     * @return La valeur de group
     */
    public String getGroup() {
        return group;
    }


    /**
     * Retourne le nom de l'utilisateur.
     *
     * @return La valeur de name
     */
    public String getName() {
        return name;
    }


    /**
     * Indique si cet utilisateur a le droit de faire l'action décrit par <code>actionKey</code>.
     *
     * @param actionKey Clef de l'action (e.g. DISPLAY_REPORT_WINDOW)
     *
     * @return <code>true</code> si cet utilisateur a le droit de faire cette action.
     */
    public boolean isAllowedTo(String actionKey) {
        String allowedGroups = allProfiles.getProperty(actionKey);
        if (allowedGroups != null && allowedGroups.indexOf(getGroup()) >= 0) {
            return true;
        }
        return false;
    }


    public String toString() {
        return "Utilisateur(" + getName() + "/" + getGroup() + ")";
    }


    private void loadAllProfiles() throws IOException {
        InputStream is =
              User.class.getResourceAsStream(System.getProperty("net.codjo.profile.file",
                                                                "/profile.properties"));
        try {
            allProfiles.load(is);
        }
        finally {
            is.close();
        }
    }
}
