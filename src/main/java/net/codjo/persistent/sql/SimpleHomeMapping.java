/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.persistent.sql;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.ResourceBundle;
/**
 * Classe responsable de la correspondance colonne (en Base) / propriete (objet).
 * 
 * <p>
 * Cette classe est initialise par un ResourceBundle contenant la definition de la
 * correspondance. Exemple :
 * <pre>
 *  property.tableId = DB_TABLE_NAME_ID
 *  property.DBTableName = DB_TABLE_NAME
 *  property.tableName = TABLE_NAME
 *  property.tableStep = STEP
 *  </pre>
 * <br> Dans cette exemple, la propriete <code>tableId</code> correspond a la colonne
 * <code>DB_TABLE_NAME_ID</code> .
 * </p>
 *
 * @author $Author: spinae $
 * @version $Revision: 1.2 $
 */
class SimpleHomeMapping {
    /** Masque pour initialiser les methode "get" des proprietes. */
    public static final int INIT_GETTER = 0x01;
    /** Masque pour initialiser les methode "set" des proprietes. */
    public static final int INIT_SETTER = 0x10;
    private static final int MASK = 0x11;
    private List dbColumnNames = new ArrayList();
    private List propertyNames = new ArrayList();
    private Method[] getterMethods;
    private Method[] setterMethods;

    /**
     * Constructeur.
     * 
     * <p>
     * L'argument <code>initMask</code> indique quelles sont les methodes accesseurs qui
     * seront recherchées (cf. setPropertyValue et getPropertyValue). Si
     * <code>initMask=INIT_GETTER</code> , alors les methodes "get" seront utilisable.
     * Si <code> initMask=INIT_GETTER & INIT_SETTER</code> , alors les methodes "get" et
     * "set" seront utilisable.
     * </p>
     *
     * @param resb ResourceBundle contenant la description
     * @param prefix Le prefix du mapping (ex : "property.")
     * @param objectClass La classe de l'objet mappe.
     * @param initMask Le masque d'initialisation des accesseurs
     *
     * @exception IntrospectionException Recherche des accesseurs impossible
     * @exception NoSuchMethodException Methode accesseur introuvable
     *
     * @see #setPropertyValue()
     * @see #getPropertyValue()
     */
    public SimpleHomeMapping(ResourceBundle resb, String prefix, Class objectClass,
        int initMask) throws IntrospectionException, NoSuchMethodException {
        for (Enumeration e = resb.getKeys(); e.hasMoreElements();) {
            String id = (String)e.nextElement();
            if (id.startsWith(prefix)) {
                propertyNames.add(id.substring(prefix.length()));
                dbColumnNames.add(resb.getString(id));
            }
        }

        initAccessors(objectClass, initMask);
    }

    /**
     * Positionne la valeur de la propriete (indexe par i) porte par l'objet obj.
     * 
     * <p>
     * Cette methode n'est utilisable que dans le cas ou cette objet Mapping a ete
     * initialise avec un initMask = INIT_SETTER.
     * </p>
     *
     * @param i L'index de la property
     * @param obj L'objet possedant la property
     * @param value La nouvelle valeur de la property
     *
     * @exception InvocationTargetException La methode set a lance une exception
     * @exception IllegalAccessException La methode set est private
     */
    public void setPropertyValue(int i, Object obj, Object value)
            throws InvocationTargetException, IllegalAccessException {
        Object[] args = {value};
        setterMethods[i].invoke(obj, args);
    }


    /**
     * Retourne le nom de la property d'index i.
     *
     * @param i L'index de la property
     *
     * @return Le nom
     */
    public String getName(int i) {
        return (String)propertyNames.get(i);
    }


    /**
     * Retourne le nom de la colonne correspondant a la property d'index i.
     *
     * @param i L'index de la property
     *
     * @return Le nom physique de la colonne
     */
    public String getColumn(int i) {
        return (String)dbColumnNames.get(i);
    }


    /**
     * Retourne la valeur de la propriete (indexe par i) porte par l'objet obj.
     * 
     * <p>
     * Cette methode n'est utilisable que dans le cas ou cette objet Mapping a ete
     * initialise avec un initMask = INIT_GETTER.
     * </p>
     *
     * @param i L'index de la property
     * @param obj L'objet possedant la property
     *
     * @return La valeur de la property
     *
     * @exception InvocationTargetException La methode get a lance une exception
     * @exception IllegalAccessException La methode get est private
     */
    public Object getPropertyValue(int i, Object obj)
            throws InvocationTargetException, IllegalAccessException {
        return getterMethods[i].invoke(obj, null);
    }


    /**
     * Retourne l'index de la colonne.
     *
     * @param columnName Le nom de la colonne
     *
     * @return L'index
     */
    public int columnIndex(String columnName) {
        return dbColumnNames.indexOf(columnName);
    }


    /**
     * Retourne la taille du tableau de correspondance.
     *
     * @return Le nombre de couple (property / colonne)
     */
    public int size() {
        return propertyNames.size();
    }


    /**
     * Retourne le nom de la propriete attache a la colonne.
     * 
     * <p>
     * Exemple, si le fichier de configuration contient :
     * <pre>
     *  property.targetId = TARGET_ID</pre>
     * la methode renverra pour <code>TARGET_ID</code> la valeur <code>targetId </code> .
     * </p>
     *
     * @param columnName Nom physique de la colonne
     *
     * @return Nom de la propriete attache
     *
     * @throws IllegalArgumentException TODO
     */
    public String columnToProperty(String columnName) {
        int idx = dbColumnNames.indexOf(columnName);
        if (idx < 0) {
            throw new IllegalArgumentException("Nom inconnu : " + columnName);
        }
        return (String)propertyNames.get(idx);
    }


    /**
     * Initialisation.
     *
     * @param objectClass La classe de l'objet
     * @param initMask Le masque d'initialisation
     *
     * @exception IntrospectionException Recherche des accesseurs impossible
     * @exception NoSuchMethodException Methode accesseur introuvable
     */
    private void initAccessors(Class objectClass, int initMask)
            throws IntrospectionException, NoSuchMethodException {
        // Init
        if ((initMask & MASK) == INIT_GETTER) {
            getterMethods = new Method[dbColumnNames.size()];
        }
        if ((initMask & MASK) == INIT_SETTER) {
            setterMethods = new Method[dbColumnNames.size()];
        }

        // Cherche les methodes
        BeanInfo info = Introspector.getBeanInfo(objectClass);
        PropertyDescriptor[] desc = info.getPropertyDescriptors();
        for (int i = 0; i < desc.length; i++) {
            int idx = propertyNames.indexOf(desc[i].getName());
            if (idx != -1 && (initMask & MASK) == INIT_GETTER) {
                getterMethods[idx] = desc[i].getReadMethod();
            }
            if (idx != -1 && (initMask & MASK) == INIT_SETTER) {
                setterMethods[idx] = desc[i].getWriteMethod();
            }
        }

        // Verification que les methodes ont ete trouve
        if ((initMask & MASK) == INIT_GETTER) {
            for (int i = 0; i < getterMethods.length; i++) {
                if (getterMethods[i] == null) {
                    throw new NoSuchMethodException("Manque getter pour "
                        + propertyNames.get(i));
                }
            }
        }
        if ((initMask & MASK) == INIT_SETTER) {
            for (int i = 0; i < setterMethods.length; i++) {
                if (setterMethods[i] == null) {
                    throw new NoSuchMethodException("Manque setter pour "
                        + propertyNames.get(i));
                }
            }
        }
    }
}
