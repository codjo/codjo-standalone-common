/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.gui.treatment;
/**
 * Helper pour les messages d'erreurs des codes poches.
 *
 */
public class SleeveAuditHelper {
    private static SleeveAuditHelper instance = null;
    private String errorMsg = null;
    private boolean withError = false;
    private java.sql.Date beginDate;
    private java.sql.Date endDate;
    private String period;
    private int lineNumber = 0;

    private SleeveAuditHelper() {}

    public static SleeveAuditHelper getInstance() {
        if (instance == null) {
            instance = new SleeveAuditHelper();
        }
        return instance;
    }


    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }


    public String getErrorMsg() {
        return errorMsg;
    }


    public void clear() {
        instance = new SleeveAuditHelper();
    }


    public void setWithError(boolean withError) {
        this.withError = withError;
    }


    public boolean getWithError() {
        return withError;
    }


    public void setBeginDate(java.sql.Date beginDate) {
        this.beginDate = beginDate;
    }


    public java.sql.Date getBeginDate() {
        return beginDate;
    }


    public void setEndDate(java.sql.Date endDate) {
        this.endDate = endDate;
    }


    public java.sql.Date getEndDate() {
        return endDate;
    }


    public void setPeriod(String period) {
        this.period = period;
    }


    public String getPeriod() {
        return period;
    }


    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }


    public int getLineNumber() {
        return lineNumber;
    }
}
