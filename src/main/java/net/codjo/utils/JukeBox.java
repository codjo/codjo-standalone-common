/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.utils;
import java.applet.Applet;
import java.applet.AudioClip;
import org.apache.log4j.Logger;

/**
 * Y a de la musique dans l'air.Sert à jouer deux beaux sons
 *
 * @author $Author: marcona $
 * @version $Revision: 1.4 $
 *
 */
public class JukeBox {
    Applet applet;
    // Log
    private static final Logger APP = Logger.getLogger(JukeBox.class);

    /**
     * Constructeur
     */
    public JukeBox() {
        Applet app = new Applet();
    }

    /**
     * Joue le son de la victoire et le libère sinon le périphérique sonore reste occupé
     * pour les autres applications
     */
    public void playSuccessSound() {
        AudioClip successClip = loadSuccessSound();
        if (successClip != null) {
            successClip.play();
            try {
                Thread.currentThread().sleep(1500);
                successClip = null;
                flushMemory();
            }
            catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }
        else {
            APP.debug("Impossible de jouer le son de Success");
        }
    }


    /**
     * Joue le son de la défaite et le libère sinon le périphérique sonore reste occupé
     * pour les autres applications
     */
    public void playFailureSound() {
        AudioClip failureClip = loadFailureSound();
        if (failureClip != null) {
            failureClip.play();
            try {
                Thread.currentThread().sleep(1500);
                failureClip = null;
                flushMemory();
            }
            catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }
        else {
            APP.debug("Impossible de jouer le son de Failure");
        }
    }


    /**
     * Force le garbage collector
     */
    private void flushMemory() {
        System.gc();
        System.runFinalization();
    }


    /**
     * Chargement du son de success
     *
     * @return Le son success
     */
    private AudioClip loadSuccessSound() {
        return Applet.newAudioClip(JukeBox.class.getResource("/sons/Bravos.wav"));
    }


    /**
     * Chargement du son Failure
     *
     * @return Le son failure
     */
    private AudioClip loadFailureSound() {
        return Applet.newAudioClip(JukeBox.class.getResource("/sons/Verre.wav"));
    }
}
