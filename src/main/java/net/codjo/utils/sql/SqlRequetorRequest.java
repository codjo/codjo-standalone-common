/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.utils.sql;

// Common
import net.codjo.model.Table;
//Penelope
import net.codjo.utils.SqlTypeConverter;
import java.sql.Types;

//Java
import java.util.ArrayList;
/**
 * Cette classe gère 6 listes en parallèle de la sqlList de la classe SqlRequetor. Pour
 * un index donné, l'ensemble des éléments de ces listes correspond à la ligne de même
 * index de la sqlList.
 *
 * @author $Author: spinae $
 * @version $Revision: 1.2 $
 */
class SqlRequetorRequest {
    // Constantes
    static final int EQUAL = 0;
    static final int SUP = 1;
    static final int SUP_EQUAL = 2;
    static final int INF = 3;
    static final int INF_EQUAL = 4;
    static final int DIFFERENT = 5;
    static final int BEGIN_BY = 6;
    static final int END_BY = 7;
    static final int CONTAIN = 8;
    static final int NOT_CONTAIN = 9;
    static final int IS_NULL = 10;
    static final int IS_NOT_NULL = 11;

    //L'opérateur logique (and ou or)
    private ArrayList logicalOper = new ArrayList();

    //L'objet table
    private ArrayList table = new ArrayList();

    //Le nom physique du champ de la table courante
    private ArrayList field = new ArrayList();

    //L'opérateur de comparaison (=, like, ...)
    private ArrayList compareOper = new ArrayList();

    //Le préfixe de la valeur du champ(', '%, ...)
    private ArrayList prefixValue = new ArrayList();

    //La valeur du champ
    private ArrayList value = new ArrayList();

    //Le suffixe de la valeur du champ(', %', ...)
    private ArrayList suffixValue = new ArrayList();

    /**
     * Constructor for the SqlRequetorRequest object
     */
    public SqlRequetorRequest() {}


    /**
     * Constructor par copie
     *
     * @param req Description of Parameter
     */
    public SqlRequetorRequest(SqlRequetorRequest req) {
        logicalOper = new ArrayList(req.logicalOper);
        table = new ArrayList(req.table);
        field = new ArrayList(req.field);
        compareOper = new ArrayList(req.compareOper);
        prefixValue = new ArrayList(req.prefixValue);
        value = new ArrayList(req.value);
        suffixValue = new ArrayList(req.suffixValue);
    }

    /**
     * Met à jour l'opérateur logique à l'index donné.
     *
     * @param newLogicalOper La valeur de l'opérateur
     * @param idx L'index
     */
    public void setLogicalOper(String newLogicalOper, int idx) {
        logicalOper.set(idx, newLogicalOper);
    }


    /**
     * Met à jour le nom physique du champ à l'index donné.
     *
     * @param newField Le nom du champ
     * @param idx L'index
     */
    public void setField(String newField, int idx) {
        field.set(idx, newField);
    }


    /**
     * Met à jour l'opérateur de comparaison à l'index donné.
     *
     * @param newCompareOper La valeur de l'opérateur
     * @param idx L'index
     */
    public void setCompareOper(int newCompareOper, int idx) {
        compareOper.set(idx, new Integer(newCompareOper));
    }


    /**
     * Met à jour le préfixe de la valeur du champ à l'index donné.
     *
     * @param newPrefixValue La valeur du préfixe
     * @param idx L'index
     */
    public void setPrefixValue(String newPrefixValue, int idx) {
        prefixValue.set(idx, newPrefixValue);
    }


    /**
     * Met à jour la valeur du champ à l'index donné.
     *
     * @param newValue La valeur du champ
     * @param idx L'index
     */
    public void setValue(String newValue, int idx) {
        value.set(idx, addQuote(newValue));
    }


    /**
     * Met à jour le suffixe de la valeur du champ à l'index donné.
     *
     * @param newSuffixValue La valeur du suffixe
     * @param idx L'index
     */
    public void setSuffixValue(String newSuffixValue, int idx) {
        suffixValue.set(idx, newSuffixValue);
    }


    /**
     * Retourne l'opérateur logique à l'index donné.
     *
     * @param idx L'index
     *
     * @return L'opérateur
     */
    public String getLogicalOper(int idx) {
        return (String)logicalOper.get(idx);
    }


    /**
     * Retourne le nom physique du champ à l'index donné.
     *
     * @param idx L'index
     *
     * @return Le nom du champ
     */
    public String getField(int idx) {
        return (String)field.get(idx);
    }


    /**
     * Retourne la taille des listes de la requête.
     *
     * @return La taille des listes.
     */
    public int getRequestListSize() {
        return field.size();
    }


    /**
     * Retourne l'opérateur de comparaison à l'index donné.
     *
     * @param idx L'index
     *
     * @return L'opérateur
     */
    public int getCompareOperValue(int idx) {
        return ((Integer)compareOper.get(idx)).intValue();
    }


    /**
     * Retourne l'opérateur de comparaison à l'index donné.
     *
     * @param idx L'index
     *
     * @return L'opérateur
     */
    public String getCompareOperTraducValue(int idx) {
        int oper = getCompareOperValue(idx);
        if (oper != -1) {
            return traductOperator(oper);
        }
        else {
            return "";
        }
    }


    /**
     * Retourne le préfixe de la valeur du champ à l'index donné.
     *
     * @param idx L'index
     *
     * @return Le préfixe
     */
    public String getPrefixValue(int idx) {
        return (String)prefixValue.get(idx);
    }


    /**
     * Retourne la valeur du champ à l'index donné.
     *
     * @param idx L'index
     *
     * @return La valeur du champ
     */
    public String getValue(int idx) {
        return (String)value.get(idx);
    }


    /**
     * Retourne le suffixe de la valeur du champ à l'index donné.
     *
     * @param idx L'index
     *
     * @return Le suffixe
     */
    public String getSuffixValue(int idx) {
        return (String)suffixValue.get(idx);
    }


    /**
     * Retourne l'ensemble des éléments des listes à l'index donné.
     *
     * @param idx L'index
     *
     * @return Les éléments des listes
     */
    public String getRequest(int idx) {
        StringBuffer str = new StringBuffer();
        if (logicalOper.size() != 0 && logicalOper.size() > idx) {
            str.append(getLogicalOper(idx));
        }
        if (table.size() != 0 && table.size() > idx) {
            if (getTable(idx) != null) {
                str.append(getTable(idx).getDBTableName() + ".");
            }
        }

        if (field.size() != 0 && field.size() > idx) {
            str.append(getField(idx));
        }
        if (compareOper.size() != 0 && compareOper.size() > idx) {
            str.append(getCompareOperTraducValue(idx));
        }
        if (prefixValue.size() != 0 && prefixValue.size() > idx) {
            str.append(getPrefixValue(idx));
        }
        if (value.size() != 0 && value.size() > idx) {
            str.append(getValue(idx));
        }
        if (suffixValue.size() != 0 && suffixValue.size() > idx) {
            str.append(getSuffixValue(idx));
        }
        return str.toString();
    }


    /**
     * Overview.
     * 
     * <p>
     * Description
     * </p>
     *
     * @param idx Description of Parameter
     */
    public void removeElements(int idx) {
        logicalOper.remove(idx);
        field.remove(idx);
        compareOper.remove(idx);
        prefixValue.remove(idx);
        value.remove(idx);
        suffixValue.remove(idx);
        table.remove(idx);
    }


    /**
     * Ajoute un élément vide à chacune des listes pour l'index donné.
     *
     * @param idx L'index
     */
    public void addElements(int idx) {
        logicalOper.add(idx, "");
        field.add(idx, "");
        compareOper.add(idx, new Integer(-1));
        prefixValue.add(idx, "");
        value.add(idx, "");
        suffixValue.add(idx, "");
        table.add(idx, null);
    }


    /**
     * Supprime tous les éléments des listes.
     */
    public void removeAllElements() {
        for (int i = 0; i < value.size(); i++) {
            removeElements(i);
        }
    }


    /**
     * Met à jour l'objet table à l'index donné.
     *
     * @param newTable L'objet table
     * @param idx L'index
     */
    void setTable(Table newTable, int idx) {
        table.set(idx, newTable);
    }


    /**
     * Retourne l'objet table à l'index donné
     *
     * @param idx L'index
     *
     * @return L'objet table
     */
    Table getTable(int idx) {
        return (Table)table.get(idx);
    }


    /**
     * Met à jour le préfixe et le suffixe d'une valeur pour l'index donné en fonction de
     * l'opérateur de comparaison et du type SQL du champ.
     *
     * @param oper L'opérateur de comparaison.
     * @param idx L'index.
     * @param sqlType Le type SQL du champ.
     */
    void updatePrefSuffValue(int oper, int idx, int sqlType) {
        if (oper == CONTAIN || oper == NOT_CONTAIN) {
            setPrefixValue("'%", idx);
            setSuffixValue("%'", idx);
        }
        else if (oper == BEGIN_BY) {
            setPrefixValue("'", idx);
            setSuffixValue("%'", idx);
        }
        else if (oper == END_BY) {
            setPrefixValue("'%", idx);
            setSuffixValue("'", idx);
        }
        else if (sqlType == Types.BIT
                || SqlTypeConverter.isNumeric(sqlType)
                || oper == IS_NULL
                || oper == IS_NOT_NULL) {
            setPrefixValue("", idx);
            setSuffixValue("", idx);
        }
        else {
            setPrefixValue("'", idx);
            setSuffixValue("'", idx);
        }
    }


    /**
     * Adds a feature to the Quote attribute of the SqlRequetorRequest object
     *
     * @param value The feature to be added to the Quote attribute
     *
     * @return Description of the Returned Value
     */
    private String addQuote(String value) {
        StringBuffer tmp = new StringBuffer(value);
        char quote = '\'';
        int index = 0;
        while (index < tmp.length()) {
            if (tmp.charAt(index) == quote) {
                tmp.insert(index, quote);
                index++;
            }
            index++;
        }
        return tmp.toString();
    }


    /**
     * Traduit les opérateurs de comparaison en "langage Sybase".
     *
     * @param oper L'opérateur sélectioné dans la liste.
     *
     * @return La valeur traduite.
     *
     * @throws IllegalArgumentException TODO
     */
    private String traductOperator(int oper) {
        String strOper;
        switch (oper) {
            case EQUAL:
                strOper = " = ";
                break;
            case SUP:
                strOper = " > ";
                break;
            case SUP_EQUAL:
                strOper = " >= ";
                break;
            case INF:
                strOper = " < ";
                break;
            case INF_EQUAL:
                strOper = " <= ";
                break;
            case DIFFERENT:
                strOper = " <> ";
                break;
            case BEGIN_BY:
                strOper = " like ";
                break;
            case END_BY:
                strOper = " like ";
                break;
            case CONTAIN:
                strOper = " like ";
                break;
            case NOT_CONTAIN:
                strOper = " not like ";
                break;
            case IS_NULL:
                strOper = " is null ";
                break;
            case IS_NOT_NULL:
                strOper = " is not null ";
                break;
            default:
                throw new IllegalArgumentException("Operateur inconnu");
        }
        return strOper;
    }
}
