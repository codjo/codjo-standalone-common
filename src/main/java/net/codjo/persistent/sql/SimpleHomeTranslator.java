/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.persistent.sql;

// Persistance
import net.codjo.persistent.PersistenceException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import org.apache.log4j.Logger;
/**
 * Classe implemantant le mecanisme de traduction utilise par <code>SimpleHome
 * </code>lors du chargement d'un objet.
 * 
 * <p>
 * Cette classe contient la liste de tout les traducteurs pour un home specifique. Un
 * traducteur est initialise par la clause <code>translator.xx=yy[.zz]</code> , ou xx
 * est un nom de propriete, yy est l'objet responsable de la traduction (en general un
 * autre Home), et zz est le nom de la methode faisant la traduction (optionnel).
 * </p>
 * 
 * <p>
 * Ce <code>SimpleHomeTranslator</code> utilisera (pour zz) la methode prenant comme
 * parametre un <code>int</code> , et si elle n'existe pas, la premiere methode public
 * nomme "zz".
 * </p>
 * 
 * <p>
 * Le mot clef <code>this</code> peut etre utilise pour yy. Dans ce cas, la methode de
 * traduction se trouve sur le home courant.
 * </p>
 * 
 * <p>
 * Si <code>zz</code> n'est pas precise, le traducteur <code>yy</code> doit contenir une
 * methode : <code>getxx(...)</code> retournant un objet. Cette methode ne sera pas
 * appele pour les valeur <code>null</code> .
 * </p>
 *
 * @author $Author: blazart $
 * @version $Revision: 1.4 $
 */
class SimpleHomeTranslator {
    // Log
    private static final Logger APP = Logger.getLogger(SimpleHomeTranslator.class);
    private Map translatorFields = new HashMap();
    private Map translatorMethods = new HashMap();
    private SimpleHome home;

    /**
     * Constructeur.
     *
     * @param resb ResourceBundle contenant la description
     * @param h Le SimpleHome du translator
     *
     * @exception NoSuchFieldException Si il manque un champ
     * @exception NoSuchMethodException si il manque une methode de traduction
     */
    protected SimpleHomeTranslator(ResourceBundle resb, SimpleHome h)
            throws NoSuchFieldException, NoSuchMethodException {
        home = h;
        for (Enumeration e = resb.getKeys(); e.hasMoreElements();) {
            String id = (String)e.nextElement();
            if (id.startsWith("translator.")) {
                String propertyName = id.substring(11);

                StringTokenizer tokenizer = new StringTokenizer(resb.getString(id), ".");
                String translatorName = tokenizer.nextToken();
                String methodName;
                if (tokenizer.hasMoreTokens()) {
                    methodName = tokenizer.nextToken();
                }
                else {
                    methodName = buildDefautlMethodName(propertyName);
                }

                Class translatorClass = addTranslatorFields(propertyName, translatorName);

                addTranslatorMethod(propertyName, methodName, translatorClass);
            }
        }
    }

    /**
     * Traduit la valeur <code>value</code> de la propriete <code>propertyName
     * </code>avec le traducteur associe (si il existe). Si aucun traducteur n'existe,
     * la valeur est retourne sans traitement.
     * 
     * <p>
     * <b>Remarque</b> : Si <code>value</code> est nulle, le traducteur n'est pas appele.
     * </p>
     *
     * @param propertyName Le nom de la propriete
     * @param value La valeur de la propriete
     *
     * @return La valeur traduite (ou la meme valeur si aucun traducteur)
     *
     * @exception PersistenceException Si la traduction echoue
     */
    public Object translateValue(String propertyName, Object value)
            throws PersistenceException {
        if (value == null) {
            return null;
        }

        if (translatorFields.containsKey(propertyName) == false) {
            return value;
        }

        try {
            Method translateMethod = (Method)translatorMethods.get(propertyName);
            Object[] args = {value};

            Object translatedValue =
                translateMethod.invoke(getTranslator(propertyName), args);
            return translatedValue;
        }
        catch (IllegalAccessException ex) {
            doTranslateValueTrace(propertyName, value);
            ex.printStackTrace();
            throw new PersistenceException(ex, propertyName + " : Access interdit");
        }
        catch (IllegalArgumentException ex) {
            doTranslateValueTrace(propertyName, value);
            ex.printStackTrace();
            throw new PersistenceException(ex, propertyName + " : Argument(s) incorrecte");
        }
        catch (InvocationTargetException ex) {
            doTranslateValueTrace(propertyName, value);
            ex.printStackTrace();
            if (ex.getTargetException() instanceof PersistenceException) {
                throw (PersistenceException)ex.getTargetException();
            }
            else if (ex.getTargetException() instanceof SQLException) {
                throw new PersistenceException((SQLException)ex.getTargetException());
            }
            return new PersistenceException(ex, propertyName + " : Erreur inconnue ");
        }
        catch (java.lang.NullPointerException ex) {
            doTranslateValueTrace(propertyName, value);
            throw new PersistenceException(ex,
                "Traducteur pour " + propertyName + " non initialise");
        }
    }


    /**
     * Retourne le traducteur pour cette property.
     *
     * @param propertyName Le nom de la propriete
     *
     * @return Le traducteur
     *
     * @exception IllegalAccessException Description of Exception
     */
    private Object getTranslator(String propertyName)
            throws IllegalAccessException {
        Object translator = translatorFields.get(propertyName);
        if (translator == null) {
            translator = home;
        }
        else {
            translator = ((Field)translator).get(home);
        }
        return translator;
    }


    /**
     * Realise une trace pour les erreurs lance par la methode translateValue.
     *
     * @param propertyName
     * @param value
     */
    private void doTranslateValueTrace(String propertyName, Object value) {
        debug("  propertyName = " + propertyName);
        debug("         value = " + value);
        debug("        method = " + translatorMethods.get(propertyName));
    }


    /**
     * Log en mode Debug.
     *
     * @param msg message de debug
     */
    private void debug(String msg) {
        APP.debug(msg);
    }


    /**
     * Ajoute une methode de traduction.
     *
     * @param propertyName Le nom de la propriete traduite
     * @param methodName Le nom de la methode
     * @param translatorClass La classe du traducteur
     *
     * @exception NoSuchMethodException Methode introuvable
     */
    private void addTranslatorMethod(String propertyName, String methodName,
        Class translatorClass) throws NoSuchMethodException {
        debug("Recherche methode de traduction : " + methodName + "...");

        // Cas par defaut
        try {
            Class[] param = {int.class};
            Method method = translatorClass.getMethod(methodName, param);
            translatorMethods.put(propertyName, method);
            return;
        }
        catch (NoSuchMethodException ex) {
            debug("...Version avec parametre 'int' non trouve...");
        }

        Method[] methods = translatorClass.getMethods();
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].getName().equals(methodName)) {
                translatorMethods.put(propertyName, methods[i]);
                debug("...Methode trouve :" + methods[i]);
                return;
            }
        }

        throw new NoSuchMethodException(methodName);
    }


    /**
     * Ajoute le champs a la liste. Cette methode comprends le mot clef "this".
     *
     * @param propertyName Le nom de la proprietee
     * @param translatorName Le nom du champs (ou this)
     *
     * @return La classe du traducteur
     *
     * @exception NoSuchFieldException Description of Exception
     */
    private Class addTranslatorFields(String propertyName, String translatorName)
            throws NoSuchFieldException {
        if ("this".equals(translatorName)) {
            translatorFields.put(propertyName, null);
            return home.getClass();
        }
        else {
            Field field = home.getClass().getDeclaredField(translatorName);
            translatorFields.put(propertyName, field);
            return field.getType();
        }
    }


    /**
     * Construit le nom par defaut de la methode de traduction
     *
     * @param propertyName
     *
     * @return get[propertyName]
     */
    private static String buildDefautlMethodName(String propertyName) {
        return "get" + propertyName.substring(0, 1).toUpperCase()
        + propertyName.substring(1);
    }
}
