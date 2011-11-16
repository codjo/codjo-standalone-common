/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.utils;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * This class reads a typical configuration file, and stocks configuration domains into an hash table. Each
 * configuration domain contains a set of property of configuration item.
 *
 * <p> The file has the following format :
 * <pre>
 *  ([configuration domain] '{'
 *  ( [configuration item] '=' [value] )*
 *  '}')*
 *  </pre>
 * </p> <hr> Example of file:
 * <pre>
 *  GLOBAL {
 *  Prompt.name     = true
 *  Prompt.fullName = true
 *  debug
 *  }
 *  OUTPUT {
 *  ALL = "./desTraces.trace"
 *  }
 *  </pre>
 * <hr> Example of use:
 * <pre>
 * 	ConfigurationFile cfr = new ConfigurationFile("trace.conf");
 * 	Properties props = cfr.getDomain("OUTPUT");
 * 	String fileName = props.getProperty("ALL");
 *  </pre>
 * <hr>
 *
 * @author Boris Gonnot
 * @version 1 - 04/02/00 - Creation
 */
public final class ConfigurationFile {
    private Map domains = new HashMap();


    /**
     * Constructor.
     *
     * @param confFile A stream to the configuration file
     *
     * @throws IOException If an I/O failure occured.
     */
    public ConfigurationFile(Reader confFile) throws IOException {
        load(confFile);
    }


    /**
     * Default constructor.
     */
    public ConfigurationFile() {
    }


    /**
     * Constructor.
     *
     * @param fileName file name of the configuration file
     *
     * @throws IOException If an I/O failure occured.
     */
    public ConfigurationFile(String fileName) throws IOException {
        load(new FileReader(fileName));
    }


    /**
     * Add a new domain to this configuration file.
     *
     * @param name       domain name
     * @param properties the domain's properties
     *
     * @throws IllegalArgumentException TODO
     */
    public void addDomain(String name, Properties properties) {
        // Precondition
        if (name == null) {
            throw new IllegalArgumentException();
        }
        if (properties == null) {
            properties = new Properties();
        }

        // Add the new Domain
        domains.put(name, properties);
    }


    /**
     * Tests if the specified domain is defined in this configuration file.
     *
     * @param name the domain name.
     *
     * @return <code>true</code> if the domain is defined
     */
    public boolean containsDomain(String name) {
        return domains.containsKey(name);
    }


    /**
     * Returns all the read domains name.
     *
     * @return all the domains name
     */
    public Iterator domainsName() {
        return domains.keySet().iterator();
    }


    /**
     * Returns the set of property of the specified domain.
     *
     * @param name domain name
     *
     * @return the domain definition
     */
    public Properties getDomain(String name) {
        return (Properties)domains.get(name);
    }


    /**
     * Load a configuration file.
     *
     * @param confFile Reader on the File.
     *
     * @throws IOException              Read error
     * @throws IllegalArgumentException TODO
     */
    public void load(Reader confFile) throws IOException {
        if (confFile == null) {
            throw new IllegalArgumentException();
        }
        domains = new java.util.HashMap();
        StringBuffer buffer;
        try {
            buffer = loadFile(confFile);
        }
        finally {
            confFile.close();
        }
        loadAllDomains(buffer.toString());
    }


    /**
     * Load a configuration file.
     *
     * @param fileName Nom de fichier
     *
     * @throws IOException Read error
     */
    public void load(String fileName) throws IOException {
        load(new FileReader(fileName));
    }


    /**
     * Saves this configuration file to the specified stream.
     *
     * <p> <b>NB:</b> The writer is not closed. </p>
     *
     * @param out    an output stream.
     * @param header a description of the property list.
     *
     * @throws IOException              Description of Exception
     * @throws IllegalArgumentException TODO
     */
    public void save(OutputStream out, String header)
          throws IOException {
        // Precondition
        if (out == null) {
            throw new IllegalArgumentException();
        }

        // Init
        PrintWriter prnt = new PrintWriter(out);

        // Save - Header
        if (header != null) {
            prnt.write("// ");
            prnt.println(header);
        }
        prnt.write("//");
        prnt.println(new java.util.Date());
        // Save - all domains
        for (Iterator e = domains.keySet().iterator(); e.hasNext();) {
            String key = (String)e.next();
            prnt.print(key);
            prnt.println(" {");
            // Save content
            Properties props = getDomain(key);
            prnt.flush();
            props.store(out, null);
            prnt.println("}");
        }

        // Ensure that everything is spooled
        prnt.flush();
    }


    /**
     * Description of the Method
     *
     * @param file Description of the Parameter
     *
     * @return Description of the Return Value
     */
    private int findDomainClosingBracket(String file) {
        int bracket = 0;

        for (int i = 0; i < file.length(); i++) {
            if ('{' == file.charAt(i)) {
                bracket++;
            }
            else if ('}' == file.charAt(i)) {
                bracket--;
                if (bracket == 0) {
                    return i;
                }
            }
        }
        return -1;
    }


    /**
     * Description of the Method
     *
     * @param file      Description of the Parameter
     * @param fromIndex Description of the Parameter
     *
     * @return Description of the Return Value
     */
    private int findEndOfProperty(String file, int fromIndex) {
        int idx = file.indexOf('\n', fromIndex);
        if (idx == -1) {
            return -1;
        }

        if (idx == 0 || '\\' != file.charAt(idx - 1)) {
            return idx;
        }

        return findEndOfProperty(file, idx + 1);
    }


    private String jenAiMarre(String str) {
        int idx = str.indexOf('\\');
        if (idx == -1) {
            return str;
        }
        String result = str.substring(0, idx);
        if (idx + 1 > str.length()) {
            return result;
        }
        result = result + str.charAt(idx + 1);
        if (idx + 2 > str.length()) {
            return result;
        }
        else {
            return result + jenAiMarre(str.substring(idx + 2));
        }
    }


    /**
     * Find the next domains and launch the loadDomain method on.
     *
     * @param file Description of Parameter
     *
     * @throws IOException For I/O exception.
     */
    private void loadAllDomains(String file) throws IOException {
        int startDomainIdx = file.indexOf('{');
        int endDomainIdx = findDomainClosingBracket(file);
        if (startDomainIdx < 0) {
            return;
        }
        String domainName = file.substring(0, startDomainIdx).trim();
        Properties props = loadDomain(file.substring(startDomainIdx + 1, endDomainIdx));
        domains.put(domainName, props);

        loadAllDomains(file.substring(endDomainIdx + 1));
    }


    /**
     * Load domain as Properties object.
     *
     * @param file Description of Parameter
     *
     * @return Description of the Returned Value
     *
     * @throws IOException For I/O exception.
     */
    private Properties loadDomain(String file) throws IOException {
        Properties props = new Properties();

        loadProperty(props, file);

        return props;
    }


    /**
     * Description of the Method
     *
     * @param r Description of the Parameter
     *
     * @return Description of the Return Value
     *
     * @throws IOException Description of the Exception
     */
    private StringBuffer loadFile(Reader r) throws IOException {
        BufferedReader reader = new BufferedReader(r);
        StringBuffer buffer = new StringBuffer();
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("//") == false) {
                buffer.append(line);
                if (reader.ready()) {
                    buffer.append("\n");
                }
            }
        }
        return buffer;
    }


    /**
     * Description of the Method
     *
     * @param props Description of the Parameter
     * @param file  Description of the Parameter
     */
    private void loadProperty(Properties props, String file) {
        if (file.length() == 0) {
            return;
        }
        int eolIdx = findEndOfProperty(file, 0);
        if (eolIdx == -1) {
            eolIdx = file.length();
        }
        int equalIdx = file.indexOf('=');

        String name;
        String value = "";
        if (equalIdx != -1 && eolIdx > equalIdx) {
            name = file.substring(0, equalIdx).trim();
            value = file.substring(equalIdx + 1, eolIdx).trim();
            value = jenAiMarre(value);
        }
        else {
            name = file.substring(0, eolIdx).trim();
        }
        if (name.length() > 0) {
            props.setProperty(name, value);
        }
        loadProperty(props, file.substring(Math.min(eolIdx + 1, file.length())));
    }


    /**
     * Signals that a file have a bad format.
     *
     * @author $Author: blazart $
     * @version $Revision: 1.3 $
     */
    private static class FileFormatException extends java.io.IOException {
        /**
         * Constructs a <code>FileFormatException</code> with the specified detail message.
         *
         * @param s the detail message.
         */
        public FileFormatException(String s) {
            super(s);
        }
    }
}
