/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.model;
import net.codjo.persistent.AbstractModel;
import net.codjo.persistent.PersistenceException;
import net.codjo.persistent.Reference;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
/**
 * Classe qui fait le lien entre l'objet User et la BDD.
 *
 * @version $Revision: 1.3 $
 *
 */
public class UserHome extends AbstractModel {
    /**
     * Constructor for the UserHome object.
     *
     * @param con Une connection valide
     */
    public UserHome(Connection con) {
        super(con);
    }

    /**
     * Récupère un utilisateur / son nom et som mot de passe.
     *
     * @param name Le nom de l'utilisateur
     * @param pwd Le mot de passe de l'utilisateur
     *
     * @return Une reference sur l'utilisateur (ou null)
     *
     * @exception PersistenceException Description of Exception
     */
    public User getUser(String name, String pwd) throws PersistenceException {
        Statement stmt = null;
        try {
            stmt = getConnection().createStatement();
            ResultSet rs =
                stmt.executeQuery("select * from PM_USERS where NAME='" + name
                    + "' and PASSWORD='" + pwd + "'");

            if (rs.next()) {
                Reference ref = getReference(name);
                return new User(ref, name);
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
            throw new PersistenceException(ex);
        }
        finally {
            if (stmt != null) {
                try {
                    stmt.close();
                }
                catch (SQLException ex) {}
            }
        }
        return null;
    }


    /**
     * -
     *
     * @param parm Description of Parameter
     *
     * @exception Exception Description of Exception
     * @throws Error TODO
     */
    protected void saveSQL(Reference parm) throws Exception {
        throw new Error("Un utilisateur ne peut être modifié");
    }


    /**
     * -
     *
     * @param parm1 Description of Parameter
     *
     * @exception Exception Description of Exception
     * @throws Error TODO
     */
    protected void loadSQL(Reference parm1) throws Exception {
        throw new Error("Un utilisateur ne peut être chargé que par la méthode getUser");
    }


    /**
     * -
     *
     * @param parm1 Description of Parameter
     *
     * @exception Exception Description of Exception
     * @throws Error TODO
     */
    protected void deleteSQL(Reference parm1) throws Exception {
        throw new Error("Un utilisateur ne peut être effacé");
    }
}
