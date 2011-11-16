/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.utils;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
/**
 * Overview.
 * 
 * <p>
 * Description
 * </p>
 *
 * @author $Author: marcona $
 * @version $Revision: 1.3 $
 *
 */
public class IntegerField extends JTextField {
    /**
     * Constructor for the IntegerField object
     */
    public IntegerField() {
        super(10);
        this.setHorizontalAlignment(JTextField.RIGHT);
    }

    /**
     * Gets the Value attribute of the IntegerField object
     *
     * @return The Value value
     */
    public Integer getIntegerValue() {
        try {
            if (getText().length() != 0) {
                return new Integer(getText());
            }
        }
        catch (Throwable e) {
            return new Integer(Integer.MAX_VALUE);
        }
        return new Integer(0);
    }


    /**
     * Overview.
     * 
     * <p>
     * Description
     * </p>
     *
     * @return Description of the Returned Value
     */
    protected Document createDefaultModel() {
        return new IntegerDocument();
    }

    /**
     * Overview.
     * 
     * <p>
     * Description
     * </p>
     *
     * @author $Author: marcona $
     * @version $Revision: 1.3 $
     */
    static class IntegerDocument extends PlainDocument {
        /**
         * Overview.
         * 
         * <p>
         * Description
         * </p>
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
            try {
                Integer.decode(str);
                super.insertString(offs, str, a);
            }
            catch (Throwable e) {}
        }
    }
}
