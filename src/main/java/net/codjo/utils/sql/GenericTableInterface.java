/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.utils.sql;
import net.codjo.model.Table;

import java.sql.SQLException;
/**
 * Interface de la généricTable
 *
 * @author $Author: blazart $
 * @version $Revision: 1.3 $
 *
 */
public interface GenericTableInterface {
    public void reloadData(String newFromAndWhereClause)
            throws SQLException;


    public Table getTable();
}
