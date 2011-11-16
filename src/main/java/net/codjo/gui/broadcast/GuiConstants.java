/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.gui.broadcast;
/**
 * Interface pour stocker les constantes.
 *
 * @author $author$
 * @version $Revision: 1.4 $
 */
public interface GuiConstants {
    // Table *_BROADCAST_FILES
    public static final String FILE_FILE_ID = "FILE_FILE_ID";
    public static final String FILE_FILE_NAME = "FILE_FILE_NAME";
    public static final String FILE_DESTINATION_SYSTEM = "FILE_DESTINATION_SYSTEM";
    public static final String FILE_FILE_DESTINATION_LOCATION =
          "FILE_FILE_DESTINATION_LOCATION";
    public static final String FILE_FILE_HEADER = "FILE_FILE_HEADER";
    public static final String FILE_FILE_HEADER_TEXT = "FILE_FILE_HEADER_TEXT";
    public static final String FILE_AUTO_DISTRIBUTION = "FILE_AUTO_DISTRIBUTION";
    public static final String FILE_DISTRIBUTION_METHOD = "FILE_DISTRIBUTION_METHOD";
    public static final String FILE_HISTORISE_FILE = "FILE_HISTORISE_FILE";
    public static final String FILE_CFT_BATCH_FILE = "FILE_CFT_BATCH_FILE";
    public static final String FILE_SECTION_SEPARATOR = "FILE_SECTION_SEPARATOR";

    // Table *_BROADCAST_FILE_CONTENTS
    public static final String CONTENTS_CONTENT_ID = "CONTENTS_CONTENT_ID";
    public static final String CONTENTS_FILE_ID = "CONTENTS_FILE_ID";
    public static final String CONTENTS_SECTION_ID = "CONTENTS_SECTION_ID";
    public static final String CONTENTS_SECTION_POSITION = "CONTENTS_SECTION_POSITION";
    public static final String CONTENTS_SECTION_HEADER = "CONTENTS_SECTION_HEADER";
    public static final String CONTENTS_SECTION_HEADER_TEXT =
          "CONTENTS_SECTION_HEADER_TEXT";
    public static final String CONTENTS_COLUMN_SEPARATOR = "CONTENTS_COLUMN_SEPARATOR";
    public static final String CONTENTS_COLUMN_HEADER = "CONTENTS_COLUMN_HEADER";

    // Table *_BROADCAST_SECTION
    public static final String SECTION_SECTION_ID = "SECTION_SECTION_ID";
    public static final String SECTION_SECTION_NAME = "SECTION_SECTION_NAME";
    public static final String SECTION_SELECTION_ID = "SECTION_SELECTION_ID";
    public static final String SECTION_FAMILY = "SECTION_FAMILY";
    public static final String SECTION_FIXED_LENGTH = "SECTION_FIXED_LENGTH";
    public static final String SECTION_RECORD_LENGTH = "SECTION_RECORD_LENGTH";
    public static final String SECTION_DECIMAL_SEPARATOR = "SECTION_DECIMAL_SEPARATOR";

    // Table *_BROADCAST_COLUMNS
    public static final String COLUMNS_COLUMNS_ID = "COLUMNS_COLUMNS_ID";
    public static final String COLUMNS_SECTION_ID = "COLUMNS_SECTION_ID";
    public static final String COLUMNS_DB_TABLE_NAME = "COLUMNS_DB_TABLE_NAME";
    public static final String COLUMNS_DB_FIELD_NAME = "COLUMNS_DB_FIELD_NAME";
    public static final String COLUMNS_COLUMN_NAME = "COLUMNS_COLUMN_NAME";
    public static final String COLUMNS_COLUMN_NUMBER = "COLUMNS_COLUMN_NUMBER";
    public static final String COLUMNS_COLUMN_LENGTH = "COLUMNS_COLUMN_LENGTH";
    public static final String COLUMNS_RIGHT_COLUMN_PADDING =
          "COLUMNS_RIGHT_COLUMN_PADDING";
    public static final String COLUMNS_PADDING_CARACTER = "COLUMNS_PADDING_CARACTER";
    public static final String COLUMNS_EXPRESSION = "COLUMNS_EXPRESSION";
    public static final String COLUMNS_COLUMN_DATE_FORMAT = "COLUMNS_COLUMN_DATE_FORMAT";
    public static final String COLUMNS_COLUMN_NUMBER_FORMAT =
          "COLUMNS_COLUMN_NUMBER_FORMAT";
}
