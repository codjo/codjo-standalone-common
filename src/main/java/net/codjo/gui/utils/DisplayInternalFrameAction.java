/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.gui.utils;
import net.codjo.gui.toolkit.util.ErrorDialog;
import net.codjo.utils.GuiUtil;
import java.awt.event.ActionEvent;
import java.lang.reflect.Constructor;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
/**
 * Action qui lance l'affichage d'une <code>JInternalFrame</code> paramètrable.
 *
 * <p> <b>Exemple d'utilisation simple</b> - La classe MyFrame doit être public et avoir un seul constructeur
 * (public) avec comme argument (dans le même ordre) un <code>ConnectionManager</code>, et un TableHome.
 * <pre>
 *  action = new DisplayInternalFrameAction("Ordres", "Affiche ordres" , desktop
 *  , MyFrame.class , new Object[]{connectionManager, tableHome});
 *  </pre>
 * </p>
 *
 * <p> <b>Exemple d'utilisation avancé</b> - Instanciation de la frame en utilisant une factory (cette méthode
 * permet de ne pas mettre la classe public).
 * <pre>
 *  action = new DisplayInternalFrameAction("Ordres", "Affiche ordres" , desktop
 *  , new DisplayInternalFrameAction.WindowFactory() {
 *  public JInternalFrame buildWindow() {
 *  return new MyFrame(connectionManager, tableHome);
 *  }
 *  });
 *  </pre>
 * </p>
 *
 * @version $Revision: 1.4 $
 */
public class DisplayInternalFrameAction extends AbstractAction {
    private JDesktopPane gexPane;
    private WindowFactory factory;


    public DisplayInternalFrameAction(String name, String tooltip, JDesktopPane dp,
                                      Class windowClass, Object[] arguments, Class[] classes) {
        this(name, tooltip, dp, new WindowFactory(windowClass, arguments, classes));
    }


    public DisplayInternalFrameAction(String name, String tooltip, Icon icon,
                                      JDesktopPane dp,
                                      Class windowClass,
                                      Object[] arguments,
                                      Class[] classes) {
        this(name, tooltip, icon, dp, new WindowFactory(windowClass, arguments, classes));
    }


    public DisplayInternalFrameAction(String name, String tooltip, JDesktopPane dp,
                                      WindowFactory factory) {
        this(name, tooltip, null, dp, factory);
    }


    public DisplayInternalFrameAction(String name, String tooltip, Icon icon,
                                      JDesktopPane dp, WindowFactory factory) {
        if (dp == null || factory == null) {
            throw new IllegalArgumentException();
        }
        putValue(NAME, name);
        putValue(SHORT_DESCRIPTION, tooltip);
        if (icon != null) {
            putValue(SMALL_ICON, icon);
        }
        this.gexPane = dp;
        this.factory = factory;
    }


    public void actionPerformed(ActionEvent evt) {
        try {
            displayWindow();
        }
        catch (Exception ex) {
            ex.printStackTrace();
            ErrorDialog.show(gexPane, "Impossible d'afficher la fenêtre: ", ex);
        }
    }


    private void displayWindow() throws Exception {
        JInternalFrame window = factory.buildWindow();
        window.addInternalFrameListener(new CleanUpListener());
        gexPane.add(window);
        window.pack();
        window.setVisible(true);
        GuiUtil.centerWindow(window);
        try {
            window.setSelected(true);
        }
        catch (java.beans.PropertyVetoException ex) {
            ex.printStackTrace();
        }
    }


    public static class WindowFactory {
        private Object[] arguments = {};
        private Class[] classes = {};
        private Class windowClass = null;


        public WindowFactory(Class windowClass) {
            this(windowClass, new Object[]{}, new Class[]{});
        }


        public WindowFactory(Class windowClass, Object[] arguments, Class[] classes) {
            this.arguments = arguments;
            this.windowClass = windowClass;
            this.classes = classes;
        }


        public JInternalFrame buildWindow() throws Exception {
            Constructor constructor = windowClass.getConstructor(getClasses());
            return (JInternalFrame)constructor.newInstance(getArguments());
        }


        protected Object[] getArguments() {
            return arguments;
        }


        protected Class[] getClasses() {
            return classes;
        }

//        protected Class[] getClassArguments() {
//            Class[] classArgumentArray = new Class[arguments.length];
//            if (arguments.length > 0) {
//                for (int i = 0; i < arguments.length; i++) {
//                    if (arguments[i].getClass().isInterface()) {
//                        classArgumentArray[i] = arguments[i].getClass().getInterfaces()[0];
//                    }
//                    else {
//                        classArgumentArray[i] = arguments[i].getClass();
//                    }
//                }
//            }
//            return classArgumentArray;
//        }
    }

    private class CleanUpListener extends InternalFrameAdapter {
        @Override
        public void internalFrameActivated(InternalFrameEvent evt) {
            setEnabled(false);
        }


        @Override
        public void internalFrameClosed(InternalFrameEvent evt) {
            setEnabled(true);
        }


        @Override
        public void internalFrameClosing(InternalFrameEvent evt) {
            setEnabled(true);
        }
    }
}
