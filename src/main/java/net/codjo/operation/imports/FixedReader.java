/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.operation.imports;
import java.io.IOException;
import java.io.Reader;
import org.apache.log4j.Logger;

/**
 * Classe specifique a l'import permettant de lire des fichier de taille fixe.
 * 
 * <p>
 * <b>Attention</b> : La methode <code>getLine</code> retourne une "ligne" de la taille
 * specifiee dans le constructeur.
 * </p>
 *
 * @author $Author: spinae $
 * @version $Revision: 1.2 $
 */
final class FixedReader extends java.io.BufferedReader {
    private char[] line;
    private int lineNumber = 0;
    private int lineSize;
    // Log
    private static final Logger APP = Logger.getLogger(FixedReader.class);

    /**
     * Constructor for the FixedReader object
     *
     * @param reader Description of Parameter
     * @param lineSize Description of Parameter
     */
    public FixedReader(Reader reader, int lineSize) {
        super(reader);
        setLineSize(lineSize);
    }

    /**
     * Gets the LineSize attribute of the FixedReader object
     *
     * @return The LineSize value
     */
    public final int getLineSize() {
        return lineSize;
    }


    /**
     * Positionne la taille d'une ligne.
     *
     * @param newLineSize The new LineSize value
     */
    public final void setLineSize(int newLineSize) {
        lineSize = newLineSize;
        line = new char[lineSize];
    }


    /**
     * Retourne une ligne de taille fixe.
     * 
     * <p>
     * La ligne retourne est de taille fixe.
     * </p>
     *
     * @return une "ligne" du fichier.
     *
     * @exception java.io.IOException si IO exception + si ligne incomplete (taille ligne
     *            != de celle indique)
     * @throws IOException TODO
     */
    public String readLine() throws java.io.IOException {
        int size = read(line, 0, lineSize);

        if (size < 0) {
            return null;
        }
        lineNumber++;

        if (size != lineSize) {
//			Log.USER.error("Ligne trops courte >" + new String(line) + "<");
//			Log.APP.error("Ligne trops courte " + size + " char, au lieu de " + lineSize);
            APP.error("Ligne trops courte >" + new String(line) + "<");
            throw new IOException("Ligne " + lineNumber + " du fichier trops courte ("
                + size + " charactères au lieu de " + lineSize + ")");
        }

        return new String(line);
    }
}
