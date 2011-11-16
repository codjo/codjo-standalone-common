/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.utils.sql;
//Lib
import net.codjo.gui.renderer.NumberFormatRenderer;
import net.codjo.operation.OperationInterruptedException;
import java.awt.Component;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
/**
 * Permet de formater les données de la GenericTable pour l'export et l'impression.
 *
 * @author $Author: blazart $
 * @version $Revision: 1.3 $
 */
public class DataFormater {
    private int colsNumber;
    private int currentLine = 0;
    private GenericTable genericTable;


    /**
     * Constructeur.
     *
     * @param gt La GenericTable.
     */
    public DataFormater(GenericTable gt) {
        genericTable = gt;
        colsNumber = genericTable.getColumnCount();
    }


    /**
     * Rempli un FileWriter avec toutes les données de la GenericTable pour l'export.
     *
     * @param out Le FileWriter.
     *
     * @throws IOException                   Pb d'IO ?
     * @throws OperationInterruptedException Interruption utilisateur.
     */
    public void buildDataForExport(FileWriter out)
          throws IOException, OperationInterruptedException {
        out.write(buildHeaderLine("\t"));

        if (!genericTable.hasMoreData()) {
            buildAPageOfDataForExport(1, genericTable.getRowCount(), out);
        }
        else {
            buildAPageOfDataForExport(1, genericTable.getRowCount(), out);

            while (genericTable.hasMoreData()) {
                genericTable.nextPage();
                buildAPageOfDataForExport(1, genericTable.getRowCount(), out);
            }
        }
    }


    /**
     * Construit une String contenant toutes les données de la GenericTable pour l'impression.
     *
     * @return La String.
     */
    public String buildDataForPrint() {
        StringBuffer data = new StringBuffer(buildHeaderLine("   "));
        if (!genericTable.hasMoreData()) {
            data.append(buildAPageOfDataForPrint(1, genericTable.getRowCount()));
        }
        else {
            data.append(buildAPageOfDataForPrint(1, genericTable.getRowCount()));
            while (genericTable.hasMoreData()) {
                genericTable.nextPage();
                data.append(buildAPageOfDataForPrint(1, genericTable.getRowCount()));
            }
        }
        return data.toString();
    }


    /**
     * Retourne le numéro de la ligne exportée.
     *
     * @return Le numéro de la ligne exportée.
     */
    public int getCurrentLine() {
        return currentLine;
    }


    protected Object getRenderedValue(JTable table, int row, int col) {
        TableCellRenderer renderer =
              table.getCellRenderer(row, table.convertColumnIndexToView(col));

        Object value = table.getValueAt(row, col);
        if (renderer != null && renderer instanceof NumberFormatRenderer) {
            Component component = renderer
                  .getTableCellRendererComponent(table, value, false, false, row, col);
            if (component instanceof JLabel) {
                return ((JLabel)component).getText();
            }
        }
        return value;
    }


    /**
     * Construit une String contenant les données d'une ligne de la GenericTable.
     *
     * @param row        Numéro de la ligne.
     * @param delimiter  Le délimiteur de colonnes.
     * @param newLine    Le caractère "Nouvelle ligne" (String).
     * @param returnChar Le caractère "Retour chariot" (String).
     * @param isAnExport TODO
     *
     * @return La String.
     */
    private String buildALineOfData(int row, String delimiter, String newLine,
                                    String returnChar, boolean isAnExport) {
        StringBuffer dataRow = new StringBuffer();
        StringBuffer field;
        int position;
        for (int i = 0; i < colsNumber; i++) {
            Object value = getRenderedValue(genericTable, row, i);

            if (value == null) {
                dataRow.append("");
            }
            else {
                if (isAnExport) {
                    field = new StringBuffer(value.toString());
                    //On vire les caractères importuns
                    while ((position = field.toString().indexOf("\n")) != -1) {
                        field.delete(position, position + 1);
                    }
                    while ((position = field.toString().indexOf(delimiter)) != -1) {
                        field.delete(position, position + 1);
                    }
                    dataRow.append(field.toString());
                }
                else {
                    dataRow.append(value.toString());
                }
            }
            if (i < colsNumber) {
                dataRow.append(delimiter);
            }
        }
        dataRow.append(returnChar).append(newLine);
        return dataRow.toString();
    }


    /**
     * Rempli un FileWriter avec les données du Buffer de la GenericTable pour l'export.
     *
     * @param firstRow La première ligne du Buffer.
     * @param lastRow  La dernière ligne du Buffer.
     * @param out      Le FileWriter.
     *
     * @throws IOException                   Pb d'IO ?
     * @throws OperationInterruptedException Interruption utilisateur.
     */
    private void buildAPageOfDataForExport(int firstRow, int lastRow, FileWriter out)
          throws IOException, OperationInterruptedException {
        for (int i = firstRow - 1; i < lastRow; i++) {
            if (Thread.interrupted()) {
                throw new OperationInterruptedException(
                      "Export interrompu par l'utilisateur");
            }
            currentLine++;
            out.write(buildALineOfData(i, "\t", "\n", "\r", true));
        }
    }


    /**
     * Construit une String contenant les données du Buffer de la GenericTable pour l'impression.
     *
     * @param firstRow La première ligne du Buffer.
     * @param lastRow  La dernière ligne du Buffer.
     *
     * @return La String.
     */
    private String buildAPageOfDataForPrint(int firstRow, int lastRow) {
        StringBuffer data = new StringBuffer();
        for (int i = firstRow - 1; i < lastRow; i++) {
            data.append(buildALineOfData(i, "   ", "\n", "", false));
        }
        return data.toString();
    }


    private String buildHeaderLine(String columnSeparator) {
        TableColumnModel colModel = genericTable.getColumnModel();
        StringWriter header = new StringWriter();
        PrintWriter writer = new PrintWriter(header);
        for (int i = 0; i < colModel.getColumnCount(); i++) {
            writer.print(colModel.getColumn(i).getHeaderValue());
            if (i + 1 < colModel.getColumnCount()) {
                writer.print(columnSeparator);
            }
        }
        writer.println();
        return header.toString();
    }
}
