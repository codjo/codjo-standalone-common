/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.gui;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
/**
 * Cette classe contient le paramétrage d'une application (versionning,serveurs sybase,utilisateur par
 * défaut...) .
 *
 * <p> Cf le fichier se trouvant ici \Lib\Common\private\Application.properties pour un exemple. </p>
 *
 * @version $Revision: 1.4 $
 */
public class ApplicationData {
    Properties data;
    Server[] servers = null;


    public ApplicationData(InputStream confFile) throws IOException {
        Properties props = new Properties();
        props.load(confFile);
        init(props);
    }


    public ApplicationData(Properties props) {
        init(props);
    }


    /**
     * Retourne toutes les proprietes définie dans le fichier de configuration.
     *
     * @return Les properties
     */
    public Properties getData() {
        return data;
    }


    public String getDefaultLogin() {
        return data.getProperty("login.default.name");
    }


    public String getHelpUrl() {
        return data.getProperty("help.default.url", "NONE");
    }


    public String getDefaultPassword() {
        return data.getProperty("login.default.pwd");
    }


    public String getDriver() {
        return data.getProperty("server.driver");
    }


    public javax.swing.Icon getIcon() {
        return new javax.swing.ImageIcon(ApplicationData.class.getResource(
              data.getProperty("application.icon")));
    }


    public String getName() {
        return data.getProperty("application.name");
    }


    public Server[] getServers() {
        return servers;
    }


    public String getVersion() {
        return data.getProperty("application.version");
    }


    private Server buildServer(String serverData) {
        StringTokenizer tokenizer = new StringTokenizer(serverData, ",");

        if (tokenizer.countTokens() != 3) {
            throw new IllegalArgumentException();
        }
        return new Server(tokenizer.nextToken().trim(), tokenizer.nextToken().trim(),
                          tokenizer.nextToken().trim());
    }


    private void init(Properties props) {
        data = new Properties();
        data.putAll(props);
        data.putAll(System.getProperties());

        String defaultServerData =
              data.getProperty(data.getProperty("server.default.url", "NONE"));
        if (defaultServerData != null) {
            servers = new Server[]{buildServer(defaultServerData)};
        }
        else {
            Set<Server> serverSet = new TreeSet<Server>();
            for (Enumeration iter = data.keys(); iter.hasMoreElements();) {
                String key = (String)iter.nextElement();
                if (key.startsWith("server.url.")) {
                    serverSet.add(buildServer(data.getProperty(key)));
                }
            }
            servers = serverSet.toArray(new Server[]{});
        }
    }


    /**
     * Classe deccrivant un serveur/base SYBASE.
     *
     * @author $Author: acharif $
     * @version $Revision: 1.4 $
     */
    public static final class Server implements Comparable {
        private String catalog;
        private String name;
        private String url;


        Server(String name, String url, String catalog) {
            if (name == null || url == null || catalog == null) {
                throw new IllegalArgumentException();
            }
            this.name = name;
            this.url = url;
            this.catalog = catalog;
        }


        public String getCatalog() {
            return catalog;
        }


        public String getName() {
            return name;
        }


        public String getUrl() {
            return url;
        }


        public int compareTo(Object obj) {
            return name.compareTo(((Server)obj).getName());
        }
    }
}
