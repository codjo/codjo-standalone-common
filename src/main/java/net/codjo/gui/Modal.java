/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.gui;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import javax.swing.JInternalFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
/**
 * Classe utilitaire permettant de simuler le mode Modal pour une JInternalFrame.
 *
 * @version $Revision: 1.4 $
 *
 *
 */
public class Modal {
    private JPanel glassPanel = new JPanel();
    private ModalKeyFeedback keyFeedback = new ModalKeyFeedback();
    private JInternalFrame modalFrame;
    private ModalMouseFeedback mouseFeedback = new ModalMouseFeedback();
    private Component oldGlassPane = null;
    private JInternalFrame parentFrame;
    private ParentVetoListener parentVeto = new ParentVetoListener();
    private boolean parentWasClosable;

    /**
     * Constructeur de Modal.
     *
     * @param parentFrame Fenetre parente a la fenetre modal
     * @param modalFrame La fenetre modal
     */
    public Modal(JInternalFrame parentFrame, JInternalFrame modalFrame) {
        this.parentFrame = parentFrame;
        this.modalFrame = modalFrame;
        modalFrame.addInternalFrameListener(new ModalStateUpdater());
        if (modalFrame.isShowing()) {
            addModalRestriction();
        }

        glassPanel.setOpaque(false);
        glassPanel.addKeyListener(keyFeedback);
        glassPanel.addMouseListener(mouseFeedback);
    }

    /**
     * Ajoute une restriction Modal
     */
    private void addModalRestriction() {
        oldGlassPane = parentFrame.getGlassPane();
        parentFrame.setGlassPane(glassPanel);
        parentFrame.addVetoableChangeListener(parentVeto);
        modalFrame.setLayer(JLayeredPane.MODAL_LAYER);
        parentWasClosable = parentFrame.isClosable();
        parentFrame.setClosable(false);
    }


    /**
     * Enleve la restriction modal
     */
    private void removeModalRestriction() {
        parentFrame.remove(glassPanel);
        parentFrame.setGlassPane(oldGlassPane);
        parentFrame.removeVetoableChangeListener(parentVeto);
        parentFrame.setClosable(parentWasClosable);
    }

    /**
     * Realise un retour auditif de l'echec d'un evt clavier sur la fenetre parente.
     *
     * @author $Author: acharif $
     * @version $Revision: 1.4 $
     */
    private static class ModalKeyFeedback extends KeyAdapter {
        public void keyTyped(KeyEvent e) {
            Toolkit.getDefaultToolkit().beep();
        }
    }


    /**
     * Realise un retour auditif de l'echec d'un click sur la fenetre parente.
     *
     * @author $Author: acharif $
     * @version $Revision: 1.4 $
     */
    private static class ModalMouseFeedback extends MouseAdapter {
        public void mouseClicked(MouseEvent e) {
            Toolkit.getDefaultToolkit().beep();
        }
    }


    /**
     * Listener mettant a jours la modalite en fonction de l'etat d'ouverture de la
     * fenetre modal.
     *
     * @author $Author: acharif $
     * @version $Revision: 1.4 $
     */
    private class ModalStateUpdater extends InternalFrameAdapter {
        public void internalFrameClosed(InternalFrameEvent evt) {
            removeModalRestriction();
            try {
                parentFrame.setSelected(true);
            }
            catch (PropertyVetoException ex) {}
        }


// TEMP
        public void internalFrameClosing(InternalFrameEvent evt) {
            internalFrameClosed(evt);
        }


// END TEMP
        public void internalFrameOpened(InternalFrameEvent evt) {
            addModalRestriction();
        }
    }


    /**
     * Empeche la fenetre parente d'etre selectionner.
     *
     * @author $Author: acharif $
     * @version $Revision: 1.4 $
     */
    private class ParentVetoListener implements VetoableChangeListener {
        public void vetoableChange(PropertyChangeEvent evt)
                throws PropertyVetoException {
            if ("selected".equals(evt.getPropertyName())
                    && Boolean.TRUE.equals(evt.getNewValue())) {
                modalFrame.setSelected(true);
                throw new PropertyVetoException("En cours d'edition", evt);
            }
            if ("icon".equals(evt.getPropertyName())) {
//                modalFrame.setIcon(Boolean.TRUE.equals(evt.getNewValue()));
                modalFrame.setVisible(Boolean.FALSE.equals(evt.getNewValue()));
            }
        }
    }
}
