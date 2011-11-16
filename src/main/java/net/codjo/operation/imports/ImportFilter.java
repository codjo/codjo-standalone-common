/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.operation.imports;
/**
 * Interface decrivant un filtre d'import.
 *
 * @author $Author: blazart $
 * @version $Revision: 1.3 $
 *
 */
public interface ImportFilter {
    /**
     * Indique si la ligne passé en paramètre doit être filtrée (cad elle n'est pas
     * importée).
     *
     * @param line La ligne a tester
     *
     * @return <code>true</code> si la ligne est filtrée.
     */
    public boolean filteredLine(String line);
}
