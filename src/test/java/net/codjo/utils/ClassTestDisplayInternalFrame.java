package net.codjo.utils;
import javax.swing.JInternalFrame;
/**
 *
 */
public class ClassTestDisplayInternalFrame extends JInternalFrame {

    private int parametreTest = 0;


    public ClassTestDisplayInternalFrame() {
        //constructeur sans paramétres
        parametreTest = 1;
    }


    public ClassTestDisplayInternalFrame(String a, String b) {
        //constructeur a 2 paramétres
        parametreTest = 2;
    }


    public ClassTestDisplayInternalFrame(int a) {
        //constructeur a 1 paramétres
        parametreTest = 3;
    }


    public int getParametreTest() {
        return parametreTest;
    }
}