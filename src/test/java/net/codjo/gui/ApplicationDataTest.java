/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.gui;
import java.util.Properties;
import junit.framework.TestCase;
/**
 * Test <code>ApplicationData</code> .
 *
 * @author $Author: spinae $
 * @version $Revision: 1.2 $
 */
public class ApplicationDataTest extends TestCase {
    private ApplicationData data;
    private Properties props;


    public void test_getVersion() throws Exception {
        props.put("server.default.url", "NONE");
        data = new ApplicationData(props);
        assertEquals("1.00.00.00", data.getVersion());

        props.put("server.default.url", "server.url.production");
        data = new ApplicationData(props);

        assertEquals("1.00.00.00", data.getVersion());
    }


    /**
     * Test que getServers renvoie tous les serveurs definie lorsqu'aucun serveur par defaut n'est configuré.
     *
     * @throws Exception
     */
    public void test_getServers_NoDefault() throws Exception {
        props.put("server.default.url", "NONE");
        data = new ApplicationData(props);
        ApplicationData.Server[] servers = data.getServers();

        assertEquals(4, servers.length);
        assertEquals("Développement", servers[0].getName());
        assertEquals("Intégration", servers[1].getName());
        assertEquals("Production", servers[2].getName());
        assertEquals("Recette", servers[3].getName());
    }


    /**
     * Test que getServers renvoie que le serveur definie par defaut lorsque la property "server.default.url"
     * est definie.
     *
     * @throws Exception
     */
    public void test_getServers_OneDefault() throws Exception {
        props.put("server.default.url", "server.url.production");
        props.put("server.url.production",
                  "Production, jdbc:sybase:Tds:ap_orbis:14100, ORBIS");
        data = new ApplicationData(props);
        ApplicationData.Server[] servers = data.getServers();

        assertEquals(1, servers.length);
        assertEquals("Production", servers[0].getName());
        assertEquals("jdbc:sybase:Tds:ap_orbis:14100", servers[0].getUrl());
        assertEquals("ORBIS", servers[0].getCatalog());
    }


    /**
     * Verifie que les property definie dans Systeme outrepasse les réglages du fichier de configuration.
     *
     * @throws Exception
     */
    public void test_getServers_OneDefault_FromSystemProperty()
          throws Exception {
        props.put("server.default.url", "NONE");
        props.put("server.url.production",
                  "Production, jdbc:sybase:Tds:ap_orbis:14100, ORBIS");
        System.setProperty("server.default.url", "server.url.production");
        data = new ApplicationData(props);
        ApplicationData.Server[] servers = data.getServers();

        assertEquals(1, servers.length);
        assertEquals("Production", servers[0].getName());
        assertEquals("jdbc:sybase:Tds:ap_orbis:14100", servers[0].getUrl());
        assertEquals("ORBIS", servers[0].getCatalog());
    }


    @Override
    protected void setUp() throws Exception {
        props = new Properties();
        props.load(ApplicationData.class.getResourceAsStream("/Application.properties"));
    }
}
