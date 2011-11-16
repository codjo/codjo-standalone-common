/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.utils.sql;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;
import javax.swing.UIManager;
/**
 * Permet d'imprimer les données de la GenericTable.
 *
 * @author $Author: marcona $
 * @version $Revision: 1.4 $
 *
 */
public class PrintAction extends AbstractDbAction {
    GenericTablePrinter genericTablePrinter;

    /**
     * Constructeur
     *
     * @param gt La GenericTable
     *
     * @throws IllegalArgumentException TODO
     */
    public PrintAction(GenericTable gt) {
        super(null, null, gt);
        if (gt == null) {
            throw new IllegalArgumentException();
        }
        genericTablePrinter = new GenericTablePrinter(getGenericTable());
        putValue(NAME, "Imprimer");
        putValue(SHORT_DESCRIPTION, "Impression des données");
        putValue(SMALL_ICON, UIManager.getIcon("ListTable.print"));
    }

    /**
     * Lance l'impression des données.
     *
     * @param evt L'evenement.
     */
    public void actionPerformed(ActionEvent evt) {
        PrinterJob job = PrinterJob.getPrinterJob();
        Book book = new Book();

        // cover page could be appended to book here
        PageFormat pf = job.pageDialog(job.defaultPage());
        int count = genericTablePrinter.calculatePageCount(pf);
        book.append((Printable)genericTablePrinter, pf, count);
        job.setPageable(book);
        if (job.printDialog()) {
            try {
                job.print();
            }
            catch (PrinterException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Cette classe construit et gère l'impression des données.
     *
     * @author $Author: marcona $
     * @version $Revision: 1.4 $
     */
    static class GenericTablePrinter implements Printable {
        ArrayList pages;
        PageFormat curPageFormat;
        Font font = new Font("TimesRoman", Font.PLAIN, 12);
        GenericTable genericTable;
        DataFormater dataFormater;

        /**
         * Constructeur.
         *
         * @param gt La GenericTable.
         */
        public GenericTablePrinter(GenericTable gt) {
            genericTable = gt;
            dataFormater = new DataFormater(genericTable);
        }

        /**
         * Détermine le nombre de pages à imprimer.
         *
         * @param pf Le PageFormat (portrait ou paysage).
         *
         * @return Le nombre de pages à imprimer.
         */
        public int calculatePageCount(PageFormat pf) {
            ArrayList pgs = repaginate(pf);
            return pgs.size();
        }


        /**
         * Lance l'impression d'une page si elle exisre.
         *
         * @param g Le Graphics de l'impression.
         * @param pf Le PageFormat (portrait ou paysage).
         * @param idx Le numéro de la page.
         *
         * @return Si la page doit être imprimer ou non.
         *
         * @exception PrinterException Pb d'impression ?
         */
        public int print(Graphics g, PageFormat pf, int idx)
                throws PrinterException {
            // Printable's method implementation
            if (curPageFormat != pf) {
                curPageFormat = pf;
                pages = repaginate(pf);
            }
            if (idx >= pages.size()) {
                return Printable.NO_SUCH_PAGE;
            }
            g.setFont(font);
            g.setColor(Color.black);
            renderPage(g, pf, idx);
            return Printable.PAGE_EXISTS;
        }


        /**
         * Construit les lignes à imprimer pour une page.
         *
         * @param g Le Graphics.
         * @param pf Le PageFormat.
         * @param idx Le numéro de la page.
         */
        void renderPage(Graphics g, PageFormat pf, int idx) {
            // render the lines from the pages list
            int xo = (int)pf.getImageableX();
            int yo = (int)pf.getImageableY();
            int y = font.getSize();
            ArrayList page = (ArrayList)pages.get(idx);
            Iterator it = page.iterator();
            while (it.hasNext()) {
                String line = (String)it.next();
                g.drawString(line, xo, y + yo);
                y += font.getSize();
            }
        }


        /**
         * Construit les pages à imprimer en fonction des données issues de la
         * GenericTable.
         *
         * @param pf Le PageFormat.
         *
         * @return La liste des pages à imprimer.
         */
        private ArrayList repaginate(PageFormat pf) {
            // creating pages of lines
            int maxh = (int)pf.getImageableHeight();
            int lineh = font.getSize();
            ArrayList pgs = new ArrayList();
            ArrayList page = new ArrayList();
            int pageh = 0;

            // headers
//				page.add("Author: " + art.toString());
//				page.add(" ");
//				pageh += (lineh * 2);
            // body
            StringTokenizer st =
                new StringTokenizer(dataFormater.buildDataForPrint(), "\n");
            while (st.hasMoreTokens()) {
                String line = st.nextToken();
                if (pageh + lineh > maxh) {
                    // need new page
                    pgs.add(page);
                    page = new ArrayList();
                    pageh = 0;
                }
                page.add(line);
                pageh += lineh;
            }
            pgs.add(page);
            return pgs;
        }
    }
}
