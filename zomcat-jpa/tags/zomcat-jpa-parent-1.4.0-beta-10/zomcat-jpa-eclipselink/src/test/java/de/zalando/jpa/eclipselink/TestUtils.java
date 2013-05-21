package de.zalando.jpa.eclipselink;

import java.util.Arrays;
import java.util.Vector;

import org.eclipse.persistence.mappings.DatabaseMapping;

/**
 * Little helpers.
 *
 * @author  jbellmann
 */
public final class TestUtils {

    private TestUtils() {
        // hide constructor
    }

    public static Vector<DatabaseMapping> createVectorOf(final DatabaseMapping... databaseMappings) {
        final Vector v = new Vector();
        v.addAll(Arrays.asList(databaseMappings));
        return v;
    }

}
