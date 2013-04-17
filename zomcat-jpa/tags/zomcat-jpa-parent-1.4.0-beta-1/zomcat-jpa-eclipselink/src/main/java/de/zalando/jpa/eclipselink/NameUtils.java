package de.zalando.jpa.eclipselink;

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
     * @param   tableName
     * @param   attributeName
     *
     * @return
     */
    public static String buildFieldName(final String tableName, final String attributeName) {
        return iconizeTableName(tableName) + "_" + camelCaseToUnderscore(attributeName);
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
