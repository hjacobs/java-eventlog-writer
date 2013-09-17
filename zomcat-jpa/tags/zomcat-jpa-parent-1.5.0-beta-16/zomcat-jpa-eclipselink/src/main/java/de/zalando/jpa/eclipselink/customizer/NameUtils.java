package de.zalando.jpa.eclipselink.customizer;

import com.google.common.base.CaseFormat;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

/**
 * Utilities for DB-Name-Conventions at Zalando.
 *
 * @author  jbellmann
 */
public final class NameUtils {

    private NameUtils() {
        // hide constructor
    }

    /**
     * Builds an fieldname (or column) from provided tablename and attributename.<br/>
     * For an tablename like 'purchase_order_head' and an attributename 'brandCode' you will get 'poh_brand_code'.
     *
     * @param   tableName
     * @param   attributeName
     *
     * @return
     */
    public static String buildFieldName(final String tableName, final String attributeName) {
        return iconizeTableName(tableName) + "_" + camelCaseToUnderscore(attributeName);
    }

    /**
     * Builds an fieldname (or column) from provided tablename and attributename for boolean fields.<br/>
     * For an tablename like 'purchase_order_head' and an attributename 'ordered' you will get 'poh_is_ordered'.
     *
     * @param   tableName
     * @param   attributeName
     *
     * @return
     */
    public static String buildBooleanFieldName(final String tableName, String attributeName) {
        if (attributeName.toLowerCase().startsWith("is")) {
            attributeName = attributeName.substring(2);
        }

        return iconizeTableName(tableName) + "_is_" + camelCaseToUnderscore(attributeName);
    }

    /**
     * @param   name
     *
     * @return
     */
    public static String camelCaseToUnderscore(final String name) {
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, name);
    }

    /**
     * Builds an prefix from tablename for columns. Always in lowercase.<br/>
     * So for an tablename like 'TA_BLENAME' you would get 'tb'.<br/>
     * For an tablename like 'thisIs_BlOED' you would get 'tb'.
     *
     * @param   tableName
     *
     * @return
     */
    public static String iconizeTableName(final String tableName) {
        Preconditions.checkArgument(not(Strings.isNullOrEmpty(tableName)),
            "TableName '%s' to iconize should never be null or empty", tableName);

        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(tableName.charAt(0));
        for (int i = 1; i < tableName.length(); ++i) {
            final char charAt = tableName.charAt(i);
            if (charAt == '_') {
                stringBuilder.append(tableName.charAt(i + 1));
            }
        }

        return stringBuilder.toString().toLowerCase();
    }

    public static boolean not(final boolean toNegate) {
        return !toNegate;
    }

}
