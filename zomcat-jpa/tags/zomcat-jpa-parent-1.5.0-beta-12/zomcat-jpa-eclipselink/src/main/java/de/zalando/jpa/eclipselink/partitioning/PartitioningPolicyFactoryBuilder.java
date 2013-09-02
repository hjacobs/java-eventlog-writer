package de.zalando.jpa.eclipselink.partitioning;

/**
 * Helper to build an {@link PartitioningPolicyFactory}.
 *
 * @author  jbellmann
 */
public final class PartitioningPolicyFactoryBuilder {

    private PartitioningPolicyFactoryBuilder() {
        // hide constructor
    }

    public static PartitioningPolicyFactory buildFromClassName(final String clazzName) {
        if (clazzName == null || clazzName.trim().isEmpty()) {
            throw new IllegalArgumentException("ClazzName should never be null or empty.");
        }

        // TODO think about privileged access
        try {
            PartitioningPolicyFactory factory = (PartitioningPolicyFactory) Class.forName(clazzName).newInstance();
            return factory;
        } catch (InstantiationException e) {

            throw new RuntimeException(e.getMessage(), e);
        } catch (IllegalAccessException e) {

            throw new RuntimeException(e.getMessage(), e);
        } catch (ClassNotFoundException e) {

            throw new RuntimeException(e.getMessage(), e);
        }
    }

}
