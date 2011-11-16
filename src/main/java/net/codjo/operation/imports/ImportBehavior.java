/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.operation.imports;
import net.codjo.model.Period;
import net.codjo.model.Table;
import net.codjo.operation.Behavior;
import net.codjo.operation.Operation;
import net.codjo.persistent.PersistenceException;
import net.codjo.persistent.Reference;
import net.codjo.utils.QueryHelper;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/**
 * Comportement d'import.
 * 
 * <p>
 * Cette classe definit le comportement d'import d'une opération.
 * </p>
 *
 * @author $Author: marcona $
 * @version $Revision: 1.5 $
 *
 */
public class ImportBehavior extends Behavior {
    private String commentry;
    private List fieldImportList = new ArrayList();
    private String fieldSeparator;
    private String fileType;
    private ImportFilter filter;
    private boolean fixedLength;
    private boolean fixedReadLine = true;
    private boolean headerLine;
    private String inBox;
    private String location;
    private String outBox;
    private int recordLength;
    private String stdFileName;

    /**
     * Constructeur. Les arguments correspondent aux champs de la table
     * PM_IMPORT_SETTINGS
     *
     * @param selfRef Self Reference
     * @param fileType -
     * @param stdFileName -
     * @param location -
     * @param recordLength -
     * @param commentry -
     * @param outBox -
     * @param inBox -
     * @param fixedLength -
     * @param fieldSeparator -
     * @param headerLine -
     * @param destTable Description of Parameter
     */
    public ImportBehavior(Reference selfRef, String fileType, String stdFileName,
        String location, int recordLength, String commentry, String outBox, String inBox,
        boolean fixedLength, String fieldSeparator, boolean headerLine, Table destTable) {
        super(selfRef, null, destTable);
        init(fileType, stdFileName, location, recordLength, commentry, outBox, inBox,
            fixedLength, fieldSeparator, headerLine);
    }


    /**
     * Constructeur pour les tests.
     * 
     * <p>
     * Les arguments correspondent aux champs de la table PM_IMPORT_SETTINGS
     * </p>
     *
     * @param fileType -
     * @param stdFileName -
     * @param location -
     * @param recordLength -
     * @param commentry -
     * @param outBox -
     * @param inBox -
     * @param fixedLength -
     * @param fieldSeparator -
     * @param headerLine -
     * @param destTable Description of Parameter
     */
    ImportBehavior(String fileType, String stdFileName, String location,
        int recordLength, String commentry, String outBox, String inBox,
        boolean fixedLength, String fieldSeparator, boolean headerLine, Table destTable) {
        super(null, destTable);
        init(fileType, stdFileName, location, recordLength, commentry, outBox, inBox,
            fixedLength, fieldSeparator, headerLine);
    }

    /**
     * Overview.
     * 
     * <p>
     * Description
     * </p>
     *
     * @param fileName Description of Parameter
     * @param currentPeriod Description of Parameter
     *
     * @return Description of the Returned Value
     *
     * @exception BadFormatException Description of Exception
     */
    public static File findRealFileName(File fileName, Period currentPeriod)
            throws BadFormatException {
        List listFormatIN = new ArrayList(4);
        listFormatIN.add("aaaa");
        listFormatIN.add("mmaa");
        listFormatIN.add("aamm");
        listFormatIN.add("aa");

        List listFormatOUT = new ArrayList(4);
        listFormatOUT.add("yyyy");
        listFormatOUT.add("MMyy");
        listFormatOUT.add("yyMM");
        listFormatOUT.add("yy");

        StringBuffer tmp = new StringBuffer(fileName.toString());
        int idx = 0;
        int cpt = 0;
        do {
            idx = tmp.toString().indexOf(listFormatIN.get(cpt).toString());
            cpt++;
        }
        while ((idx < 0) && (cpt < listFormatIN.size()));

        if (idx >= 0) {
            SimpleDateFormat formatOUT =
                new SimpleDateFormat(listFormatOUT.get(cpt - 1).toString());
            SimpleDateFormat formatIN = new SimpleDateFormat("yyyyMM");
            String strPeriod = currentPeriod.toString().substring(0, 6);
            try {
                java.util.Date datePeriod = formatIN.parse(strPeriod);
                String dateString = formatOUT.format(datePeriod);
                tmp.replace(idx, idx + listFormatIN.get(cpt - 1).toString().length(),
                    dateString);
            }
            catch (java.text.ParseException ex) {
                throw new BadFormatException(ex.getMessage());
            }
        }
        else {
            throw new BadFormatException("Mauvais format d'instanciation du fichier");
        }

        return new File(tmp.toString());
    }


    /**
     * Determine la longueur de l'opération à effectuer (ex : Nb de lignes du fichier à
     * importer). La methode met a jour l'attribut lengthOfTask.
     *
     * @param ope Operation courante
     *
     * @exception Exception Description of Exception
     */
    public void determineLengthOfTask(Operation ope)
            throws Exception {
        File file = determineInputFile(ope);
        LineNumberReader lnr = new LineNumberReader(new FileReader(file));
        while (lnr.readLine() != null) {}
        setLengthOfTask(lnr.getLineNumber());
    }


    /**
     * Retourne le fichier d'entrée utilisé lorsque l'opération est automatique.
     *
     * @return -
     */
    public File getAutoInputFile() {
        return new File(inBox, stdFileName);
    }


    /**
     * Retourne le commentaire.
     *
     * @return le commentaire.
     */
    public String getCommentry() {
        return commentry;
    }


    /**
     * Gets the fieldImportList attribute of the ImportBehavior object
     *
     * @return The fieldImportList value
     */
    public List getFieldImportList() {
        return fieldImportList;
    }


    /**
     * Retourne le separateur de champs. Cette information n'est utilise que pour les
     * imports a taille variable.
     *
     * @return Le separateur
     */
    public String getFieldSeparator() {
        return fieldSeparator;
    }


    /**
     * Gets the FileType attribute of the ImportBehavior object
     *
     * @return The FileType value
     */
    public String getFileType() {
        return fileType;
    }


    /**
     * Retourne inBox.
     *
     * @return inBox.
     */
    public String getInBox() {
        return inBox;
    }


    /**
     * Retourne l'attribut de localisation du fichier.
     *
     * @return la localisation
     */
    public String getLocation() {
        return location;
    }


    /**
     * Retourne outBox.
     *
     * @return outBox.
     */
    public String getOutBox() {
        return outBox;
    }


    /**
     * Retourne la taille d'une ligne importe.
     *
     * @return La taille.
     */
    public int getRecordLength() {
        return recordLength;
    }


    /**
     * Retourne le nom du fichier formatté (ex : FICHIERmmaa.pen)
     *
     * @return la localisation
     */
    public String getStdFileName() {
        return stdFileName;
    }


    /**
     * Retourne l'attribut de localisation du fichier
     *
     * @return la localisation
     */
    public boolean isFixedLength() {
        return fixedLength;
    }


    /**
     * Retourne l'attribut fixedReadLine de ImportBehavior
     *
     * @return La valeur de fixedReadLine
     *
     * @see #setFixedReadLine()
     */
    public boolean isFixedReadLine() {
        return fixedReadLine;
    }


    /**
     * Gets the HeaderLine attribute of the ImportBehavior object
     *
     * @return The HeaderLine value
     */
    public boolean isHeaderLine() {
        return headerLine;
    }


    /**
     * Charge (si necessaire) tous les <code>FieldImport</code> necessaire a l'execution
     * de ce comportement.
     *
     * @exception PersistenceException
     *
     * @todo quand le passage de fieldImportList sera fait en Reference, il faudra la
     *       parcourir pour la charger
     */
    public void prepareProceed() throws PersistenceException {}


    /**
     * Lance l'import.
     * 
     * <p>
     * Lecture du fichier d'entree ligne par ligne.
     * </p>
     *
     * @param ope -
     *
     * @exception Exception -
     * @throws IllegalArgumentException TODO
     */
    public void proceed(Operation ope) throws Exception {
        if (ope == null) {
            throw new IllegalArgumentException("Opération invalide");
        }
        if (fieldImportList.size() > getDestTable().getNumberOfCol()) {
            throw new IllegalArgumentException("Nb de FieldImport > Nb de colonnes");
        }
        File inputFile = determineInputFile(ope);

        Connection con = getConnectionManager().getConnection();
        try {
            doImport(con, inputFile, getDestTable().getDBTableName());
        }
        finally {
            getConnectionManager().releaseConnection(con);
        }
    }


    /**
     * Positionne un filtre d'import.
     *
     * @param f La nouvelle valeur de filter
     */
    public void setFilter(ImportFilter f) {
        filter = f;
    }


    /**
     * Positionne l'attribut fixedReadLine de ImportBehavior qui indique si la lecture
     * d'une ligne (pour le type fichier a longueur fixe) doit etre faites de maniere
     * fixe ou en tenant compte du retour chariot.
     *
     * @param newFixedReadLine La nouvelle valeur de fixedReadLine
     */
    public void setFixedReadLine(boolean newFixedReadLine) {
        fixedReadLine = newFixedReadLine;
    }


    /**
     * Lance l'mport du fichier <code>inputFile</code>.
     *
     * @param con Description of the Parameter
     * @param inputFile Description of the Parameter
     * @param dbDestName Description of the Parameter
     *
     * @exception IOException Erreur de lecture du fichier
     * @exception SQLException Erreur d'acces BD
     * @exception InterruptedException Interruption utilisateur
     * @exception FieldNotFoundException Un champ est introuvable dans la ligne
     * @exception BadFormatException Un champ n'est pas au bon format
     */
    protected void doImport(Connection con, File inputFile, String dbDestName)
            throws IOException, SQLException, InterruptedException, 
                FieldNotFoundException, BadFormatException {
        testInputFile(inputFile);

        BufferedReader reader;
        if (isFixedLength() && isFixedReadLine()) {
            reader = new FixedReader(new FileReader(inputFile), getRecordLength());
        }
        else {
            reader = new BufferedReader(new FileReader(inputFile));
        }
        String line;

        setCurrentOfTask(0);
        PreparedStatement insertQuery = null;
        try {
            insertQuery =
                QueryHelper.buildInsertStatement(dbDestName, getDbFieldNameList(), con);

            if (headerLine == true) {
                reader.readLine();
                incrementCurrentOfTask();
            }
            while ((line = reader.readLine()) != null) {
                if (filter != null && filter.filteredLine(line)) {
                    continue;
                }
                if (Thread.interrupted()) {
                    throw new InterruptedException("Interruption utilisateur");
                }
                incrementCurrentOfTask();
                fillPreparedStatement(insertQuery, line);
                insertQuery.executeUpdate();
                insertQuery.clearParameters();
            }
        }
        finally {
            reader.close();
            if (insertQuery != null) {
                insertQuery.close();
            }
        }
    }


    /**
     * Ajoute un FieldImport à ImportBehavior.
     *
     * @param fieldImport Le FieldImport à ajouter
     *
     * @throws IllegalArgumentException TODO
     */
    protected void addFieldImport(FieldImport fieldImport) {
        if (fieldImport == null) {
            throw new IllegalArgumentException();
        }

        // UGLY : L'initialisation des champs FixedLength et Separator se font
        //          ici, car ces informations sont stockee dans la table
        //          PM_IMPORT.
        fieldImport.setFixedLength(fixedLength);
        fieldImport.setSeparator(fieldSeparator);

        if ((fieldImport.getFixedLength() == true)
                && ((fieldImport.getPosition() + fieldImport.getLength()) > recordLength)) {
            throw new IllegalArgumentException(
                "Dépassement de la longueur d'enregistrement");
        }

        fieldImportList.add(fieldImport);
    }


    /**
     * Description of the Method
     *
     * @param st Description of the Parameter
     * @param line Description of the Parameter
     *
     * @exception BadFormatException Description of the Exception
     * @exception FieldNotFoundException Description of the Exception
     * @exception SQLException Description of the Exception
     */
    void fillPreparedStatement(PreparedStatement st, String line)
            throws BadFormatException, FieldNotFoundException, SQLException {
        Iterator iter = fieldImportList.iterator();

        for (int pos = 1; iter.hasNext(); pos++) {
            FieldImport fi = (FieldImport)iter.next();
            st.setObject(pos, fi.convertFieldToSQL(line), fi.getSQLType());
        }
    }


    /**
     * Retourne l'attribut autoOutputFile de ImportBehavior
     *
     * @return La valeur de autoOutputFile
     */
    File getAutoOutputFile() {
        return new File(outBox, stdFileName);
    }


    /**
     * Retourne l'attribut manuInputFile de ImportBehavior
     *
     * @return La valeur de manuInputFile
     */
    File getManuInputFile() {
        return new File(location, stdFileName);
    }


    /**
     * Determine le fichier en entrée.
     *
     * @param ope L'operation
     *
     * @return Le fichier d'entree
     *
     * @exception Exception Description of Exception
     */
    private File determineInputFile(Operation ope)
            throws Exception {
        File inputFile;
        Period currentPeriod = ope.getPeriod();
        if (ope.isAutomatic()) {
            inputFile = findRealFileName(getAutoInputFile(), currentPeriod);
        }
        else {
            inputFile = findRealFileName(getManuInputFile(), currentPeriod);
        }
        return inputFile;
    }


    /**
     * Retourne la liste des Noms de colonne.
     *
     * @return Liste de String.
     */
    private List getDbFieldNameList() {
        List dbFieldNameList = new ArrayList();
        Iterator iter = fieldImportList.iterator();
        while (iter.hasNext()) {
            FieldImport fi = (FieldImport)iter.next();
            dbFieldNameList.add(fi.getDBDestFieldName());
        }
        return dbFieldNameList;
    }


    /**
     * Constructor for the init object
     *
     * @param fileType -
     * @param stdFileName -
     * @param location -
     * @param recordLength -
     * @param commentry -
     * @param outBox -
     * @param inBox -
     * @param fixedLength -
     * @param fieldSeparator -
     * @param headerLine -
     */
    private void init(String fileType, String stdFileName, String location,
        int recordLength, String commentry, String outBox, String inBox,
        boolean fixedLength, String fieldSeparator, boolean headerLine) {
        this.fileType = fileType;
        this.stdFileName = stdFileName;
        this.location = location;
        this.recordLength = recordLength;
        this.commentry = commentry;
        this.outBox = outBox;
        this.inBox = inBox;
        this.fixedLength = fixedLength;
        this.fieldSeparator = fieldSeparator;
        this.headerLine = headerLine;
    }


    /**
     * Test la validite du fichier (ecrituer, etc.).
     *
     * @param inputFile Le fichier a tester
     *
     * @exception IOException Fichier non validep
     */
    private void testInputFile(File inputFile) throws IOException {
        if (inputFile.exists() == false) {
            throw new IOException("Fichier introuvable : " + inputFile.getAbsoluteFile());
        }

        if (inputFile.isFile() == false) {
            throw new IOException("Ce n'est pas un fichier : "
                + inputFile.getAbsoluteFile());
        }

        if (inputFile.canRead() == false) {
            throw new IOException("Fichier illisible : " + inputFile.getAbsoluteFile());
        }
    }
}
