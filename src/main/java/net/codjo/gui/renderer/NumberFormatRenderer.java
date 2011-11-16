/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.gui.renderer;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.JLabel;
import javax.swing.table.DefaultTableCellRenderer;
/**
 * Renderer pour des Objets Number affichant au format English avec séparateur (,) pour Formulaire Liste
 *
 * @version 1.0
 */
public class NumberFormatRenderer extends DefaultTableCellRenderer {
    Locale locale = Locale.ENGLISH;

    private static NumberFormat NUMBER_FORMAT_2DEC;

    private static NumberFormat NUMBER_FORMAT_5DEC;

    private static NumberFormat NUMBER_FORMAT_6DEC;
    private static NumberFormat NUMBER_FORMAT_8DEC;
    private static NumberFormat NUMBER_FORMAT_15DEC;
    private static NumberFormat NUMBER_FORMAT_DEFAULT;


    /**
     * Constructor for the NumberFormatRenderer object
     */
    public NumberFormatRenderer() {
        initNumberFormat();
        setHorizontalAlignment(JLabel.RIGHT);
    }


    private void initNumberFormat() {
        NUMBER_FORMAT_2DEC = new DecimalFormat("#####0.00", new DecimalFormatSymbols(locale));
        NUMBER_FORMAT_5DEC = new DecimalFormat("#####0.00000", new DecimalFormatSymbols(locale));
        NUMBER_FORMAT_6DEC = new DecimalFormat("#####0.000000", new DecimalFormatSymbols(Locale.FRANCE));
        NUMBER_FORMAT_8DEC = new DecimalFormat("#####0.00000000", new DecimalFormatSymbols(Locale.FRANCE));
        NUMBER_FORMAT_15DEC = new DecimalFormat("#####0.000000000000000",
                                                new DecimalFormatSymbols(Locale.FRANCE));
        NUMBER_FORMAT_DEFAULT = NumberFormat.getNumberInstance(Locale.FRANCE);
    }


    /**
     * Constructor for the NumberFormatRenderer object
     */
    public NumberFormatRenderer(Locale locale) {
        this.locale = locale;
        initNumberFormat();
        setHorizontalAlignment(JLabel.RIGHT);
    }


    /**
     * Sets the Value attribute of the GenericTableModel object
     *
     * @param value The new Value value
     */
    @Override
    public void setValue(Object value) {
        if (value == null) {
            setText("");
        }

        //pas de formattage si Integer (Id)
        else if (value instanceof java.lang.Integer) {
            setText(value.toString());
        }
        else {
            BigDecimal number = new BigDecimal(value.toString());

            switch (number.scale()) {
                case 2:
                    setText(NUMBER_FORMAT_2DEC.format(value));
                    break;
                case 5:
                    setText(NUMBER_FORMAT_5DEC.format(value));
                    break;
                case 6:
                    setText(NUMBER_FORMAT_6DEC.format(value));
                    break;
                case 8:
                    setText(NUMBER_FORMAT_8DEC.format(value));
                    break;
                case 15:
                    setText(NUMBER_FORMAT_15DEC.format(value));
                    break;
                default:
                    setText(NUMBER_FORMAT_DEFAULT.format(value));
            }
        }
    }
}
