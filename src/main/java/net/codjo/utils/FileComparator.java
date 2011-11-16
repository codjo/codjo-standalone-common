/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.utils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * Compare deux fichiers
 *
 * @author $Author: marcona $
 * @version $Revision: 1.6 $
 *
 */
public class FileComparator {
    static char JOKER = '*';
    private static final Integer[] NOT_ALLOWED_CHAR = {new Integer(10), new Integer(13)};
    // Log
    private static final Logger APP = Logger.getLogger(FileComparator.class);

    /**
     * Constructeur complet
     *
     * @param joker Caractère générique.
     *
     * @throws IllegalStateException TODO
     */
    public FileComparator(String joker) {
        if (joker.length() != 1) {
            throw new IllegalStateException("Le caractère générique >" + joker
                + "< est trop long");
        }

        JOKER = joker.charAt(0);
    }

    /**
     * Comparaison de deux fichiers.
     *
     * @param first
     * @param second
     *
     * @return
     *
     * @exception IOException
     */
    public boolean equals(File first, File second)
            throws IOException {
        FileReader firstReader = new FileReader(first);
        FileReader secondReader = new FileReader(second);
        try {
            return equals(firstReader, secondReader);
        }
        finally {
            firstReader.close();
            secondReader.close();
        }
    }


    public boolean equalsNotOrdered(File first, File second)
            throws IOException {
        FileReader firstReader = new FileReader(first);
        FileReader secondReader = new FileReader(second);
        try {
            return equalsNotOrdered(firstReader, secondReader);
        }
        finally {
            firstReader.close();
            secondReader.close();
        }
    }


    /**
     * Description of the Method
     *
     * @param firstReader Description of the Parameter
     * @param secondReader Description of the Parameter
     *
     * @return Description of the Return Value
     *
     * @exception IOException Description of the Exception
     */
    public boolean equalsNotOrdered(Reader firstReader, Reader secondReader)
            throws IOException {
        BufferedReader readerA = new BufferedReader(firstReader);
        BufferedReader readerB = new BufferedReader(secondReader);
        List firstList = loadFile(readerA);
        List secondList = loadFile(readerB);
        List firstListBackUp = new ArrayList(firstList);
        long nbLineInB = secondList.size();

        firstList.removeAll(secondList);
        secondList.removeAll(firstListBackUp);

        boolean equals = firstList.size() == 0 && secondList.size() == 0;
        if (!equals) {
            APP.debug("#############################################");
            APP.debug("## nombre de lignes dans A : " + firstListBackUp.size());
            APP.debug("## nombre de lignes dans B : " + nbLineInB);
            APP.debug("## nombre de lignes communes : "
                + (nbLineInB - secondList.size()));
            APP.debug("#############################################");
            APP.debug("->" + firstList.size()
                + " Ligne(s) présente(s) dans A mais pas dans B");
            displayList("A>", firstList);
            APP.debug("---------------------------------------------");
            APP.debug("->" + secondList.size()
                + " Ligne(s) présente(s) dans B mais pas dans A");
            displayList("B>", secondList);
        }

        return equals;
    }


    /**
     * Description of the Method
     *
     * @param firstReader Description of the Parameter
     * @param secondReader Description of the Parameter
     *
     * @return Description of the Return Value
     *
     * @exception IOException Description of the Exception
     */
    public boolean equals(Reader firstReader, Reader secondReader)
            throws IOException {
        BufferedReader readerA = new BufferedReader(firstReader);
        BufferedReader readerB = new BufferedReader(secondReader);
        String lineA = readerA.readLine();
        String lineB = readerB.readLine();
        int lineNumber = 1;
        while (lineA != null && lineB != null) {
            int errorCol = equalsRow(lineA, lineB);
            if (errorCol != -1) {
                APP.debug("----------- Erreur de comparaison ");
                APP.debug("ligne = " + lineNumber);
                APP.debug("colonne = " + errorCol);
                APP.debug("char : a = \"" + lineA.charAt(errorCol) + "\" ("
                    + (int)lineA.charAt(errorCol) + ") " + "b = \""
                    + lineB.charAt(errorCol) + "\" (" + (int)lineB.charAt(errorCol) + ")");
                APP.debug(">" + lineA);
                APP.debug(">" + lineB);
                APP.debug(">");
                String strLineA ="";
                for (int i = 0; i < errorCol; i++) {
                    strLineA+=lineA.charAt(i);
                }
                APP.debug(strLineA);
                APP.debug("^");

                return false;
            }
            lineA = readerA.readLine();
            lineB = readerB.readLine();
            lineNumber++;
        }

        if (lineA == lineB) {
            return true;
        }
        else {
            APP.debug("----------- Erreur de comparaison ");
            APP.debug("lineA : " + lineA);
            APP.debug("lineB : " + lineB);
            return false;
        }
    }


    /**
     * Construit une liste des caractères non permis pour la comparaison.
     *
     * @return
     */
    private List builtNotAllowedCharList() {
        List charList = new ArrayList();
        for (int i = 0; i < NOT_ALLOWED_CHAR.length; i++) {
            charList.add(NOT_ALLOWED_CHAR[i]);
        }
        return charList;
    }


    /**
     * Comparaison de deux fichiers.
     *
     * @param lineA Description of the Parameter
     * @param lineB Description of the Parameter
     *
     * @return
     *
     * @exception IOException
     */
    private int equalsRow(String lineA, String lineB)
            throws IOException {
        List notAllowedChars = builtNotAllowedCharList();
        int charNumber = 0;
        while (charNumber < lineA.length() && charNumber < lineB.length()) {
            if (((lineA.charAt(charNumber) != JOKER)
                    && (lineB.charAt(charNumber) != JOKER))
                    && (lineA.charAt(charNumber) != lineB.charAt(charNumber))
                    && (!notAllowedChars.contains(new Integer(lineA.charAt(charNumber))))
                    && (!notAllowedChars.contains(new Integer(lineB.charAt(charNumber))))) {
                return charNumber;
            }
            charNumber++;
        }
        if (lineA.length() != lineB.length()) {
            return charNumber - 1;
        }
        return -1;
    }


    private List loadFile(BufferedReader reader) throws IOException {
        List list = new ArrayList();
        String line = reader.readLine();
        while (line != null) {
            list.add(line);
            line = reader.readLine();
        }
        return list;
    }


    private void displayList(String label, List firstList) {
        for (Iterator i = firstList.iterator(); i.hasNext();) {
            Object line = i.next();
            APP.debug(label + " " + line);
        }
    }
}
