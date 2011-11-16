/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.model;
/**
 * L'interface <code>TableRecordingMode</code> definit les differents mode d' archivage
 * possible pour les tables BD.
 *
 * @version $Revision: 1.3 $
 *
 */
public interface TableRecordingMode {
    /** Represente le mode <i>sans archivage</i> . */
    public static final int NONE = 0;
    /**
     * Represente le mode <i>archivage par periode</i> . La table stocke les informations
     * par periode. La periode se trouve dans la colonne <code>PERIOD </code>.
     */
    public static final int BY_PERIOD = 1;
    /**
     * Represente le mode <i>archivage par periode et groupe de portefeuille </i> . La
     * table stocke les informations par periode. La periode se trouve dans la colonne
     * <code>PERIOD</code> .
     */
    public static final int BY_PERIOD_AND_PORTFOLIOGROUP = 2;
    /** Represente le mode <i>archivage par groupe de portefeuille </i> . */
    public static final int BY_PORTFOLIOGROUP = 3;
}
