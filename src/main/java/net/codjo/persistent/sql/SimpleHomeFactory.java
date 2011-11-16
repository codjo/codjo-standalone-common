/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.persistent.sql;

// Persistance
import net.codjo.persistent.PersistenceException;
import net.codjo.persistent.Reference;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import org.apache.log4j.Logger;
/**
 * Class de fabrique d'objet. Cette classe est utilise par le SimpleHome afin de
 * construire des instances de la Classe administré.
 *
 * @author $Author: rivierv $
 * @version $Revision: 1.3 $
 */
class SimpleHomeFactory {
    // Log
    private static final Logger APP = Logger.getLogger(SimpleHomeFactory.class);
    private SimpleHomeMapping property;
    private SimpleHomeTranslator translator;
    private List constructorArgs = new ArrayList();
    private boolean referenceInConstructor = false;
    private Constructor constructor;
    private Class objectClass;

    /**
     * Constructeur.
     *
     * @param resb ResourceBundle contenant la description
     * @param prefix le prefix "object." ou "primaryKey."
     *
     * @exception ClassNotFoundException Class administre inexistante
     * @exception NoSuchMethodException Constructeur non trouve
     */
    public SimpleHomeFactory(ResourceBundle resb, String prefix)
            throws ClassNotFoundException, NoSuchMethodException {
        referenceInConstructor = true;
        init(resb, prefix);
    }


    /**
     * Constructor.
     */
    protected SimpleHomeFactory() {}

    /**
     * Positionne l'objet responsable des correspondances colonne / property
     *
     * @param p Le SimpleHomeMapping faisant la correspondance pour la classe administré
     */
    public void setPropertyMapping(SimpleHomeMapping p) {
        property = p;
    }


    /**
     * Positionne l'objet responsable de la traduction d'une property
     *
     * @param p Le SimpleHomeTranslator faisant la traduction
     */
    public void setTranslator(SimpleHomeTranslator p) {
        translator = p;
    }


    /**
     * Retourne l'objet responsable des correspondances colonne / property
     *
     * @return Le SimpleHomeMapping
     */
    public SimpleHomeMapping getPropertyMapping() {
        return property;
    }


    /**
     * Retourne la classe des objets administré.
     *
     * @return La classe
     */
    public Class getObjectClass() {
        return objectClass;
    }


    /**
     * Retourne un tableau des valeurs (rempli a partir du ResultSet donné) utilisable
     * pour instancié un objet administré.
     *
     * @param rs Le ResultSet contenant les données d'instanciation
     *
     * @return Un tableau rempli
     *
     * @exception SQLException Erreur d'acces base
     * @throws Error s'il y a un problème de persistence
     */
    public Object[] fillConstructorVals(ResultSet rs)
            throws SQLException {
        try {
            return fillConstructorVals(rs, null);
        }
        catch (PersistenceException ex) {
            // Impossible ??
            throw new Error("Gros Bleme " + ex);
        }
    }


    /**
     * Iterator sur les colonnes utilise pour construire un objet administre.
     *
     * @return Iterator sur des nom de colonne
     */
    public Iterator columns() {
        return constructorArgs.iterator();
    }


    /**
     * Creation d'une instance.
     *
     * @param rs Le ResultSet contenant les données
     * @param reference Reference de l'objet
     *
     * @return une instance d'objet administré
     *
     * @exception SQLException Erreur acces base
     * @exception PersistenceException Traduction impossible
     * @exception InvocationTargetException Constructeur a renvoye une exception
     * @exception IllegalAccessException Le constructeur n'est pas public
     * @exception InstantiationException L'instanciation a echoue
     */
    public Object newInstance(ResultSet rs, Reference reference)
            throws SQLException, PersistenceException, InvocationTargetException, 
                IllegalAccessException, InstantiationException {
        Object[] vals = fillConstructorVals(rs, reference);
        return newInstance(vals);
    }


    /**
     * Creation d'une instance.
     *
     * @param val Tableau de valeur utilise par le constructeur
     *
     * @return une instance d'objet administré
     *
     * @exception InvocationTargetException Constructeur a renvoye une exception
     * @exception IllegalAccessException Le constructeur n'est pas public
     * @exception InstantiationException L'instanciation a echoue
     */
    public Object newInstance(Object[] val)
            throws InvocationTargetException, IllegalAccessException, 
                InstantiationException {
        return constructor.newInstance(val);
    }


    /**
     * Retourne la liste des arguments utilise par le constructeur
     *
     * @return Liste de nom de colonne
     */
    protected List getConstructorArgs() {
        return constructorArgs;
    }


    /**
     * Init.
     *
     * @param resb ResourceBundle contenant la description
     * @param prefix Le prefixe de definition
     *
     * @exception ClassNotFoundException Class administre inexistante
     * @exception NoSuchMethodException Constructeur non trouve
     */
    protected void init(ResourceBundle resb, String prefix)
            throws ClassNotFoundException, NoSuchMethodException {
        objectClass = findClass(resb, prefix + "class");
        fillArgumentsList(resb.getString(prefix + "constructor"), constructorArgs);
        constructor = findConstructor(objectClass);
    }


    /**
     * Description of the Method
     *
     * @return Description of the Return Value
     */
    protected Object[] newConstructorVals() {
        if (referenceInConstructor) {
            return new Object[constructorArgs.size() + 1];
        }
        else {
            return new Object[constructorArgs.size()];
        }
    }


    /**
     * Recherche la classe des objets administré.
     *
     * @param resb Le ResourceBundle
     * @param classProperty Le nom de la property Class
     *
     * @return La Classe
     *
     * @exception ClassNotFoundException Classe introuvable
     */
    protected Class findClass(ResourceBundle resb, String classProperty)
            throws ClassNotFoundException {
        return Class.forName(resb.getString(classProperty));
    }


    /**
     * Retourne le premier constructeur public definie sur la classe <code>c </code>
     *
     * @param c La classe de recherche
     *
     * @return Le premier constructeur public
     *
     * @exception NoSuchMethodException Si aucun constructeur public
     */
    protected Constructor findConstructor(Class c)
            throws NoSuchMethodException {
        Constructor[] classConstructors = c.getDeclaredConstructors();
        for (int i = 0; i < classConstructors.length; i++) {
            if (classConstructors[i].getModifiers() == Modifier.PUBLIC) {
                debug("Constructeur : " + classConstructors[i]);
                return classConstructors[i];
            }
        }
        throw new NoSuchMethodException("Aucun constructeur public : " + c);
    }


    /**
     * Log en mode Debug.
     *
     * @param msg message de debug
     */
    protected void debug(String msg) {
        APP.debug(msg);
    }


    /**
     * Retourne un tableau des valeurs (rempli et traduite a partir du ResultSet donné)
     * utilisable pour instancié un objet administré.
     *
     * @param rs Le ResultSet contenant les données
     * @param reference La reference de l'objet instancié
     *
     * @return Un tableau rempli
     *
     * @exception SQLException Erreur d'acces base.=
     * @exception PersistenceException Traduction a echoue
     */
    private Object[] fillConstructorVals(ResultSet rs, Reference reference)
            throws SQLException, PersistenceException {
        int idx = 0;
        Object[] constructorVals = newConstructorVals();
        if (referenceInConstructor) {
            idx = 1;
            constructorVals[0] = reference;
        }

        for (int i = 0; i < constructorArgs.size(); i++) {
            String dbColumnName = (String)constructorArgs.get(i);
            if (translator != null) {
                constructorVals[i + idx] =
                    translator.translateValue(property.columnToProperty(dbColumnName),
                        rs.getObject(dbColumnName));
            }
            else {
                constructorVals[i + idx] = rs.getObject(dbColumnName);
            }
        }

        return constructorVals;
    }


    /**
     * Remplit la liste <code>args</code> a partir des mots contenu dans <code>str
     * </code>, separe par ";".
     *
     * @param str Une String de type "motA;motB;motC.."
     * @param args La liste des mots contenu dans str
     */
    private void fillArgumentsList(String str, List args) {
        StringTokenizer tokenizer = new StringTokenizer(str, ";");
        while (tokenizer.hasMoreElements()) {
            args.add(args.size(), tokenizer.nextElement());
        }
    }
}
