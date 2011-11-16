/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.model;
import net.codjo.persistent.AbstractPersistent;
import net.codjo.persistent.Reference;
/**
 * Cette classe représente les utilisateurs du système qui sont identifiés avec un nom.
 *
 * @version $Revision: 1.2 $
 *
 */
public class User extends AbstractPersistent {
    private String name;

    /**
     * Constructeur de l'objet User
     *
     * @param ref Une reference de l'objet
     * @param name Le nom de l'utilisateur
     */
    User(Reference ref, String name) {
        super(ref);
        this.name = name;
    }


    /**
     * Constructor for the User object
     *
     * @param name Le nom de l'utilisateur
     */
    User(String name) {
        this.name = name;
    }

    /**
     * Retourne le nom de l'utilisateur.
     *
     * @return Le nom de l'utilisateur
     */
    public String getName() {
        return name;
    }
}
