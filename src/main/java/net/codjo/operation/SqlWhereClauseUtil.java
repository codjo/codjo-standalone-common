/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.operation;
import net.codjo.model.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
/**
 * Utilitaire pour le decoupage du champ SELECT_CRITERIA en 2 parties l'une concernant le critere sur la table
 * BO_PORTFOLIO et l'autre sur la table a updater.
 */
public class SqlWhereClauseUtil {
    private static String[] whereClauseOperandes = new String[10];
    private static String[] whereClauseFields = new String[10];
    private List<String> clauseList = new ArrayList<String>(6);
    private String[] operateurs = new String[]{"<=", ">=", "<", ">", "=", "<>", "in ("};
//    private String[] operateurs = new String[]{ "=", "<>", "in ("};


    SqlWhereClauseUtil() {
    }


    SqlWhereClauseUtil(String whereClause) {
        init();
        getWhereClauseFields(whereClause);
    }


    private String[] getWhereClauseOperandes() {
        return whereClauseOperandes;
    }


    public void init() {
        clauseList.clear();
        clauseList.add(0, "");
        clauseList.add(1, "");
        purge();
    }


    public void addPtfRestrictionList(String str) {
        clauseList.set(0, str);
    }


    public String getSelectTerm() {
        return clauseList.get(0);
    }


    public String getSelectWhereClause() {
        return (getSelectTerm() == null || "".equals(getSelectTerm())) ? ""
                                                                       : " where "
                                                                         + getSelectTerm();
    }


    public String getUpdateWhereClause() {
        return (getUpdateTerm() == null || "".equals(getUpdateTerm())) ? ""
                                                                       : " where "
                                                                         + getUpdateTerm();
    }


    public String getUpdateTerm() {
        return clauseList.get(1);
    }


    public void dealWithPortfolioGroup(String portfolioGroupName, String tableName) {
        clauseList.set(0, "BO_PORTFOLIO.PORTFOLIO_GROUP ='" + portfolioGroupName + "'");
        clauseList.set(1, tableName + ".PORTFOLIO_CODE = BO_PORTFOLIO.PORTFOLIO_CODE");
    }


    public void buildCriteria(String criteriaTemp, Table tableOfQuery) {
        getWhereClauseFields(criteriaTemp);
        String[] tempWhereClauseOperandes = getWhereClauseOperandes();
        String[] tempWhereClauseFields = getWhereClauseFields();

        for (int i = 0; i < tempWhereClauseFields.length; i++) {
            if (!("".equals(tempWhereClauseOperandes[i]))) {
                if (tableOfQuery.containsColumn(tempWhereClauseFields[i])) {
                    addToClauseList(i, 1, tempWhereClauseOperandes);
                }
                else {
                    addToClauseList(i, 0, tempWhereClauseOperandes);
                }
            }
        }
    }


    private void addToClauseList(int index, int clauseIndex, String[] tempWhereClauseOperandes) {
        clauseList.set(clauseIndex,
                       clauseList.get(clauseIndex)
                       + ("".equals(tempWhereClauseOperandes[index]) ? ""
                                                                     : " and "
                                                                       + tempWhereClauseOperandes[index]));
    }


    String fillWhereClauseWithPeriod(String initialWhereClause, String period,
                                     String previousPeriod, String company) {
        if (initialWhereClause != null) {
            String clause = initialWhereClause;
            int idx = clause.indexOf("$CURRENT_PERIOD$");
            if (idx >= 0) {
                StringBuilder criteria = new StringBuilder(clause);
                criteria.replace(idx, idx + 16, "'" + period + "'");
                clause = criteria.toString();
            }

            int idxprev = clause.indexOf("$PREVIOUS_PERIOD$");
            if (idxprev >= 0) {
                StringBuilder criteriaPrev = new StringBuilder(clause);
                criteriaPrev.replace(idxprev, idxprev + 17, "'" + previousPeriod + "'");
                clause = criteriaPrev.toString();
            }

            int idxCompany = clause.indexOf("$COMPANY$");
            if (idxCompany >= 0) {
                StringBuilder criteriaPrev = new StringBuilder(clause);
                criteriaPrev.replace(idxCompany, idxCompany + 9,
                                     "'" + company + "'");
                clause = criteriaPrev.toString();
            }

            return clause;
        }
        return initialWhereClause;
    }


    public void dealWithPeriod(String period) {
        String wCltemp = clauseList.get(1);
        if ("".equals(wCltemp)) {
            clauseList.set(1, "PERIOD='" + period + "'");
        }
        else {
            clauseList.set(1, wCltemp + " and PERIOD='" + period + "'");
        }
    }


    public void dealWithSourceSystem(String sourceSystem, String tableName) {
        String wCltemp = clauseList.get(1);
        clauseList.set(1,
                       ("".equals(wCltemp) ? " " : clauseList.get(1)) + " and " + tableName
                       + ".SOURCE_SYSTEM = " + "'" + sourceSystem + "'");
    }


    public String[] getWhereClauseOperandes(String whereClauseToCut) {
        int pos = 0;

        // Purge des tableaux
        purge();

        whereClauseToCut = whereClauseToCut.replaceAll("and ", ";");
        for (StringTokenizer tokenizer =
              new StringTokenizer(whereClauseToCut, ";", false);
             tokenizer.hasMoreTokens();) {
            String str = tokenizer.nextToken();
            whereClauseOperandes[pos++] = str.trim();
        }

        return whereClauseOperandes;
    }


    private void purge() {
        // Purge des tableaux
        for (int i = 0; i < whereClauseOperandes.length; i++) {
            whereClauseOperandes[i] = "";
            whereClauseFields[i] = "";
        }
    }


    String getLeftElementOfOperande(String operande) {
        String str;
        int pos = -1;

        for (String operateur : operateurs) {
            if (pos == -1) {
                pos = operande.indexOf(operateur);
            }
        }

        str = operande.substring(0, (pos == -1) ? 0 : pos);
        return str.trim();
    }


    String getFieldOfElement(String element) {
        String str = element;

        int posDot = element.indexOf(".");
        int posOperateur = -1;

        for (String operateur : operateurs) {
            if (posOperateur == -1) {
                posOperateur = element.indexOf(operateur);
            }
        }

        if (posOperateur == -1) {
            posOperateur = element.length();
        }
        if (posDot != -1 || posOperateur != -1) {
            str = element.substring(posDot + 1, posOperateur);
        }

        while (str.contains("(")) {
            int posParenthesis = str.indexOf("(");
            if (posParenthesis != -1) {
                str = str.substring(posParenthesis + 1);
            }
        }

        return str.trim();
    }


    private String[] getWhereClauseFields() {
        for (int i = 0; i < whereClauseOperandes.length; i++) {
            String whereClauseOperande = whereClauseOperandes[i];
            whereClauseFields[i] =
                  getFieldOfElement(getLeftElementOfOperande(whereClauseOperande));
        }
        return whereClauseFields;
    }


    String[] getWhereClauseFields(String whereClause) {
        purge();
        getWhereClauseOperandes(whereClause);
        getWhereClauseFields();
        return whereClauseFields;
    }


    public void fill(String period, String previousPeriod, String company) {
        clauseList.set(0,
                       fillWhereClauseWithPeriod(getSelectTerm(), period, previousPeriod, company));
        clauseList.set(1,
                       fillWhereClauseWithPeriod(getUpdateTerm(), period, previousPeriod, company));
    }
}
