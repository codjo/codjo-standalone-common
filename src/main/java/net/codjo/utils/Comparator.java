/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.utils;
import net.codjo.model.Table;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.log4j.Logger;

/**
 * Compare deux tables
 *
 * @author $Author: acharif $
 * @version $Revision: 1.5 $
 *
 */
public class Comparator {
    int[] columnsToSkip = null;
    String orderClause = "";
    double precision = -1;
    TestEnvironnement testEnv = null;
    Connection connection = null;
    // Log
    private static final Logger APP = Logger.getLogger(Comparator.class);

    /**
     * Constructeur complet
     *
     * @param t Environnement de test
     * @param columnToSkip Numéro de la colonne à zapper
     * @param orderClause Le tri souhaité avant comparaison
     */
    public Comparator(TestEnvironnement t, int columnToSkip, String orderClause) {
        this.testEnv = t;
        initColumnToSkip(columnToSkip);
        this.orderClause = orderClause;
    }


    /**
     * Constructeur allégé 2
     *
     * @param t Environnement de test
     * @param columnToSkip Numéro de la colonne à zapper
     */
    public Comparator(TestEnvironnement t, int columnToSkip) {
        this.testEnv = t;
        initColumnToSkip(columnToSkip);
    }


    /**
     * Constructeur allégé 3
     *
     * @param columnToSkip Numéro de la colonne à zapper
     */
    public Comparator(int columnToSkip) {
        this.testEnv = TestEnvironnement.newEnvironment();
        initColumnToSkip(columnToSkip);
    }


    /**
     * Constructeur allégé 4
     *
     * @param t Environnement de test
     */
    public Comparator(TestEnvironnement t) {
        this.testEnv = t;
    }

    /**
     * Fixe la connexion à utiliser pour la comparaison
     *
     * @param con La connexion à utilisée
     */
    public void setConnection(Connection con) {
        connection = con;
    }


    /**
     * Lancement de la comparaison de deux tables
     *
     * @param table1 La première table
     * @param table2 La deuxième table (et oui !)
     *
     * @return Egalité des deux tables
     *
     * @exception SQLException Problème d'accès base
     */
    public boolean Equals(Table table1, Table table2)
            throws SQLException {
        if (sameNumberOfCol(table1, table2) == false) {
            return false;
        }

        Connection con = null;
        Statement stmt1 = null;
        Statement stmt2 = null;
        ResultSet rs1 = null;
        ResultSet rs2 = null;
        Object obj1;
        Object obj2 = null;

        try {
            if (connection == null) {
                con = testEnv.getHomeConnection();
            }
            else {
                con = connection;
            }
            stmt1 = con.createStatement();
            stmt2 = con.createStatement();

            if (sameNumberOfRow(stmt1, table1, table2) == false) {
                return false;
            }

            if ("".equals(orderClause)) {
                rs1 = stmt1.executeQuery("select * from " + table1.getDBTableName());
                rs2 = stmt2.executeQuery("select * from " + table2.getDBTableName());
            }
            else {
                rs1 = stmt1.executeQuery("select * from " + table1.getDBTableName()
                        + " order by " + orderClause);
                rs2 = stmt2.executeQuery("select * from " + table2.getDBTableName()
                        + " order by " + orderClause);
            }

            int numLine = 0;
            while ((rs1.next()) && (rs2.next())) {
                numLine++;
                for (int i = 1; i <= table1.getNumberOfCol(); i++) {
                    if (!isColumnToSkip(i)) {
                        obj1 = rs1.getObject(i);
                        obj2 = rs2.getObject(i);

                        if (isEqual(obj1, obj2) == false) {
                            APP.debug("[Comparator] Ligne " + numLine
                                + " Colonne " + rs1.getMetaData().getColumnName(i) + "\n"
                                + " Table " + table1.getDBTableName() + " valeur = ("
                                + obj1 + ")" + "\n" + " Table " + table2.getDBTableName()
                                + " valeur = (" + obj2 + ")");
                            return false;
                        }
                    }
                }
            }
            return true;
        }
        finally {
            if (stmt1 != null) {
                stmt1.close();
            }
            if (stmt2 != null) {
                stmt2.close();
            }
        }
    }


    /**
     * Positionne les colonnes a ne pas comparer
     *
     * @param columns liste d'indice de colonne
     */
    public void setColumnsToSkip(int[] columns) {
        this.columnsToSkip = columns;
    }


    /**
     * Positionne la precision utilisee lors de la comparaison de deux numerique. On dit
     * que a et b sont egaux si :  <code>(abs(a - b)) =&lt; precision</code>
     *
     * @param precision La nouvelle valeur de precision
     */
    public void setPrecision(double precision) {
        this.precision = precision;
    }


    /**
     * Retourne l'attribut equal de Comparator
     *
     * @param obj1 Description of the Parameter
     * @param obj2 Description of the Parameter
     *
     * @return La valeur de equal
     */
    boolean isEqual(Object obj1, Object obj2) {
        if (obj1 == obj2) {
            return true;
        }

        if (obj1 != null && obj1.equals(obj2)) {
            return true;
        }

        if (obj1 != null
                && obj2 != null
                && precision > 0
                && obj1 instanceof Number
                && obj2 instanceof Number) {
            double a = ((Number)obj1).doubleValue();
            double b = ((Number)obj2).doubleValue();
            return Math.abs(a - b) <= precision;
        }

        return false;
    }


    /**
     * Initialise le tableau des colonnes "a ne pas comparer" avec une seule colonne.
     *
     * @param columnToSkip Description of the Parameter
     */
    private void initColumnToSkip(int columnToSkip) {
        this.columnsToSkip = new int[] {columnToSkip};
    }


    /**
     * Retourne l'attribut columnToSkip de Comparator
     *
     * @param col Description of the Parameter
     *
     * @return La valeur de columnToSkip
     */
    private boolean isColumnToSkip(int col) {
        if (columnsToSkip == null) {
            return false;
        }
        for (int i = 0; i < columnsToSkip.length; i++) {
            if (columnsToSkip[i] == col) {
                return true;
            }
        }
        return false;
    }


    /**
     * Description of the Method
     *
     * @param table1 Description of Parameter
     * @param table2 Description of Parameter
     *
     * @return Description of the Returned Value
     */
    private boolean sameNumberOfCol(Table table1, Table table2) {
        if (table1.getNumberOfCol() != table2.getNumberOfCol()) {
            APP.debug("[Comparator] Nombre de colonnes différent !" + "\n"
                + " Table " + table1.getDBTableName() + " : " + table1.getNumberOfCol()
                + " colonne(s)" + "\n" + " Table " + table2.getDBTableName() + " : "
                + table2.getNumberOfCol() + " colonne(s)");
            return false;
        }
        return true;
    }


    /**
     * Description of the Method
     *
     * @param stmt1 Description of Parameter
     * @param table1 Description of Parameter
     * @param table2 Description of Parameter
     *
     * @return Description of the Returned Value
     *
     * @exception SQLException Description of Exception
     */
    private boolean sameNumberOfRow(Statement stmt1, Table table1, Table table2)
            throws SQLException {
        ResultSet rs =
            stmt1.executeQuery("select count(*) from " + table1.getDBTableName());
        try {
            rs.next();
            int nbRow1 = rs.getInt(1);
            rs = stmt1.executeQuery("select count(*) from " + table2.getDBTableName());
            rs.next();
            int nbRow2 = rs.getInt(1);
            if (nbRow1 != nbRow2) {
                APP.debug("[Comparator] Nombre de lignes différent !" + "\n"
                    + " Table " + table1.getDBTableName() + " : " + nbRow1 + " ligne(s)"
                    + "\n" + " Table " + table2.getDBTableName() + " : " + nbRow2
                    + " ligne(s)");
                return false;
            }
            return true;
        }
        finally {
            rs.close();
        }
    }
}
