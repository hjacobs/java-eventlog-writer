package de.zalando.jpa.eclipselink;

/**
 * Defines PersistenceUnitProperties to be used by Zomcat-JPA.
 *
 * @author  jbellmann
 */
public class ZomcatPersistenceUnitProperties {

    public static final String ZOMCAT_ECLIPSELINK_CHANGE_TRACKER_NAME = "zomcat.eclipselink.change-tracker-name";

    public static final String ZOMCAT_ECLIPSELINK_PARTITION_POLICY_FACTORY_NAME =
        "zomcat.eclipselink.partition-policy-factory-name";

}
