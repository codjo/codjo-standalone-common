/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.utils;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JInternalFrame;
/**
 * Ensemble de méthodes utilitaires pour l'IHM
 *
 * @author $Author: GONNOT $
 * @version $Revision: 1.2 $
 */
public final class GuiUtil {
    /**
     * Bloque la creation d'instances de GuiUtil
     */
    private GuiUtil() {}

    /**
     * Centre une fenetre dans son <code>Container</code> .
     * 
     * <p>
     * ATTENTION: Pour les fenetres <code>JInternalFrame</code> , cette methode doit etre
     * appelee apres l'ajout dans le desktop. (en general dans l'action qui fabrique le
     * <code>JInternalFrame</code> ).
     * </p>
     *
     * @param cp La fenetre a centrer
     *
     * @throws IllegalArgumentException TODO
     * @throws IllegalStateException TODO
     */
    public static final void centerWindow(Component cp) {
        if (cp == null) {
            throw new IllegalArgumentException();
        }

        Dimension containerSize;

        if (cp instanceof JInternalFrame) {
            if (cp.getParent() == null) {
                throw new IllegalStateException("L'appel a la methode 'centerWindow'"
                    + " doit s'effectuer apres l'ajout au desktop");
            }
            containerSize = cp.getParent().getSize();
        }
        else {
            containerSize = Toolkit.getDefaultToolkit().getScreenSize();
        }

        Dimension frameSize = cp.getSize();

        if (frameSize.height > containerSize.height) {
            frameSize.height = containerSize.height;
            cp.setSize(frameSize);
        }
        if (frameSize.width > containerSize.width) {
            frameSize.width = containerSize.width;
            cp.setSize(frameSize);
        }

        cp.setLocation((containerSize.width - frameSize.width) / 2,
            (containerSize.height - frameSize.height) / 2);
    }
}
