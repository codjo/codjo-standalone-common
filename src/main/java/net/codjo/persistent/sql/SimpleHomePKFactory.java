/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.persistent.sql;
import net.codjo.persistent.Persistent;
import net.codjo.persistent.Reference;
import net.codjo.utils.QueryHelper;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
// Java Class
import java.sql.SQLException;
import java.util.Map;
import java.util.ResourceBundle;
/**
 * Class de fabrique de clef primaire (cad d'identifiant d'une reference).
 *
 * @author $Author: blazart $
 * @version $Revision: 1.3 $
 */
class SimpleHomePKFactory extends SimpleHomeFactory {
    private boolean pkAutomatic;

    /**
     * Constructeur.
     *
     * @param resb ResourceBundle contenant la description
     *
     * @exception ClassNotFoundException Description of Exception
     * @exception NoSuchMethodException Description of Exception
     */
    protected SimpleHomePKFactory(ResourceBundle resb)
            throws ClassNotFoundException, NoSuchMethodException {
        pkAutomatic = "AUTOMATIC".equals(resb.getString("primaryKey"));

        init(resb, "primaryKey.");
    }

    /**
     * Rempli la clause where du QueryHelper.
     *
     * @param qh Description of Parameter
     * @param ref Description of Parameter
     *
     * @exception NoSuchFieldException Description of Exception
     * @exception IllegalAccessException Description of Exception
     */
    public void fillSelectorValue(QueryHelper qh, Reference ref)
            throws NoSuchFieldException, IllegalAccessException {
        Object pk = ref.getId();

        if (isPkAutomatic()) {
            qh.setSelectorValue((String)getConstructorArgs().get(0), pk);
        }
        else if (getObjectClass() == String.class) {
            qh.setSelectorValue((String)getConstructorArgs().get(0), pk);
        }
        else {
            // @ugly : pas clair du tout a la relecture.
            for (int i = 0; i < getConstructorArgs().size(); i++) {
                debug("    selector " + i + " - " + getConstructorArgs().get(i));
                String propertyName =
                    getPropertyMapping().columnToProperty((String)getConstructorArgs()
                                                                      .get(i));
                Field field = getObjectClass().getField(propertyName);
                qh.setSelectorValue((String)getConstructorArgs().get(i), field.get(pk));
            }
        }
    }


    /**
     * Construit l'id (primary key) de la reference. Lorsque la clef primaire est en mode
     * automatique, elle est construite a l'aide du <code>QueryHelper </code>et de sa
     * methode <code>getUniqueId</code> . En mode manuelle, elle est construite a partir
     * de l'obj reference.
     *
     * @param ref Une reference ne possedant pas de clef primaire.
     * @param qh Description of Parameter
     *
     * @exception SQLException En mode automatique, impossible de creer un id
     * @exception InvocationTargetException Description of Exception
     * @exception IllegalAccessException Description of Exception
     * @exception InstantiationException Description of Exception
     *
     * @see net.codjo.utils.sql.QueryHelper#getUniqueID()
     */
    public void buildId(Reference ref, QueryHelper qh)
            throws SQLException, InvocationTargetException, IllegalAccessException, 
                InstantiationException {
        Object pk;
        if (isPkAutomatic()) {
            pk = new Integer(qh.getUniqueID());
        }
        else {
            Persistent obj = ref.getLoadedObject();

            // Init Arg
            Object[] constructorVals = newConstructorVals();
            for (int i = 0; i < constructorVals.length; i++) {
                Object dbName = getConstructorArgs().get(i);
                int idx = getPropertyMapping().columnIndex((String)dbName);
                Object v = getPropertyMapping().getPropertyValue(idx, obj);
                if (v != null && v instanceof Persistent) {
                    v = ((Persistent)v).getId();
                }
                constructorVals[i] = v;
            }

            // Call constructor
            pk = newInstance(constructorVals);
        }
        debug("Creation d'un nouvel ID = " + pk);
        ref.setId(pk);
    }


    /**
     * Description of the Method
     *
     * @param args Description of Parameter
     *
     * @return Description of the Returned Value
     *
     * @exception InstantiationException Description of Exception
     * @exception IllegalAccessException Description of Exception
     * @exception InvocationTargetException Description of Exception
     */
    public Object newInstance(Object[] args)
            throws InstantiationException, IllegalAccessException, 
                InvocationTargetException {
        if (getObjectClass() == BigDecimal.class) {
            return args[0];
        }
        else {
            return super.newInstance(args);
        }
    }


    /**
     * Rempli le tableau des valeurs .
     *
     * @param vals Description of Parameter
     *
     * @return Description of the Returned Value
     *
     * @todo a descendre dans PK
     */
    protected Object[] fillConstructorVals(Map vals) {
        Object[] constructorVals = newConstructorVals();
        for (int i = 0; i < getConstructorArgs().size(); i++) {
            constructorVals[i] = vals.get((String)getConstructorArgs().get(i));
        }
        return constructorVals;
    }


    /**
     * Recherche de la classe de la Clef Primaire.
     *
     * @param resb Description of Parameter
     * @param classProperty Description of Parameter
     *
     * @return Description of the Returned Value
     *
     * @exception ClassNotFoundException Description of Exception
     */
    protected Class findClass(ResourceBundle resb, String classProperty)
            throws ClassNotFoundException {
        if (isPkAutomatic()) {
            return Integer.class;
        }
        else {
            return Class.forName(resb.getString(classProperty));
        }
    }


    /**
     * Recherche du constructeur.
     *
     * @param c Description of Parameter
     *
     * @return Description of the Returned Value
     *
     * @exception NoSuchMethodException Description of Exception
     */
    protected Constructor findConstructor(Class c)
            throws NoSuchMethodException {
        if (isPkAutomatic() || c == Integer.class) {
            Class[] args = {int.class};
            return c.getDeclaredConstructor(args);
        }
        else if (c == String.class) {
            Class[] args = {String.class};
            return c.getDeclaredConstructor(args);
        }
        else if (c == BigDecimal.class) {
            Class[] args = {String.class};
            return c.getDeclaredConstructor(args);
        }
        else {
            return super.findConstructor(c);
        }
    }


    /**
     * Indique si la clef primaire est automatique ou non. Lorsqu'une clef primaire est
     * automatique, c'est un Entier a auto-increment.
     *
     * @return <code>true</code> si la clef est gere par le Home.
     */
    private final boolean isPkAutomatic() {
        return pkAutomatic;
    }
}
