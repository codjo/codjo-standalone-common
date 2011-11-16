/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.gui.broadcast;
/**
 * TODO ressemble pas mal a GuiField
 *
 * @author $author$
 * @version $Revision: 1.4 $
 */
public class GuiFieldProperties {
    private Boolean editable = null;
    private String label = null;
    private Boolean visible = null;


    public GuiFieldProperties(String label, Boolean editable, Boolean visible) {
        this.label = label;
        this.editable = editable;
        this.visible = visible;
    }


    public GuiFieldProperties(String guiLabel) {
        this(guiLabel, null, null);
    }


    public GuiFieldProperties(Boolean editable) {
        this(null, editable, null);
    }


    public String getLabel(String oldLabel) {
        if (label != null) {
            return label;
        }

        return oldLabel;
    }


    public boolean isEditable(boolean oldEditable) {
        if (editable != null) {
            return Boolean.TRUE.equals(editable);
        }

        return oldEditable;
    }


    public boolean isVisible(boolean oldVisible) {
        if (visible != null) {
            return Boolean.TRUE.equals(visible);
        }

        return oldVisible;
    }
}
