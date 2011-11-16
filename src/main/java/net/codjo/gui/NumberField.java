/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.gui;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
/**
 * Description of the Class
 *
 * @version $Revision: 1.4 $
 *
 *
 */
public class NumberField extends JTextField {
    private NumberFormat numberFormat = null;
    private boolean isIntegerOnly = false;

    /**
     * Constructeur de NumberField
     */
    public NumberField() {
        super(10);
        this.setHorizontalAlignment(JTextField.RIGHT);
        numberFormat = NumberFormat.getNumberInstance(Locale.ENGLISH);
    }

    /**
     * Positionne l attribut parseIntegerOnly de l object NumberField
     *
     * @param v La nouvelle valeur de parseIntegerOnly
     */
    public void setParseIntegerOnly(boolean v) {
        numberFormat.setParseIntegerOnly(v);
        isIntegerOnly = v;
    }


    /**
     * Retourne l attribut intValue de l object NumberField
     *
     * @return La valeur de intValue
     */
    public int getIntValue() {
        Number nb = getNumberValue();
        if (nb == null) {
            return 0;
        }
        else {
            return nb.intValue();
        }
    }


    /**
     * Retourne l attribut doubleValue de l object NumberField
     *
     * @return La valeur de doubleValue
     */
    public double getDoubleValue() {
        Number nb = getNumberValue();
        if (nb == null) {
            return 0.;
        }
        else {
            return nb.doubleValue();
        }
    }


    /**
     * Retourne l attribut number de l object NumberField
     *
     * @return La valeur de number
     *
     * @throws IllegalStateException TODO
     */
    public Number getNumberValue() {
        if (getText().length() == 0) {
            return null;
        }
        if ("-".equals(getText())) {
            return new Integer(0);
        }
        try {
            return numberFormat.parse(getText());
        }
        catch (java.text.ParseException ex) {
            throw new IllegalStateException("Texte n'est pas un nombre");
        }
    }


    /**
     * DOCUMENT ME!
     *
     * @return NumberDocument
     */
    protected Document createDefaultModel() {
        return new NumberDocument();
    }

    /**
     * Description of the Class
     *
     * @author VIRASIS
     */
    class NumberDocument extends PlainDocument {
        /**
         * Description of the Method
         *
         * @param offs Description of Parameter
         * @param str Description of Parameter
         * @param a Description of Parameter
         *
         * @exception BadLocationException Description of Exception
         */
        public void insertString(int offs, String str, AttributeSet a)
                throws BadLocationException {
            if (str == null) {
                return;
            }
            if (str.indexOf(' ') != -1) {
                return;
            }
            if ("-".equals(str) && NumberField.this.getText().length() == 0) {
                super.insertString(offs, str, a);
            }
            try {
                String curr = NumberField.this.getText();
                String newStr = curr.substring(0, offs) + str + curr.substring(offs);

                if (isIntegerOnly) {
                    Long.decode(newStr);
                }
                else {
                    Double.parseDouble(newStr);
                }
                super.insertString(offs, str, a);
            }
            catch (Throwable e) {}
        }
    }
}
