package de.zalando.jpa.eclipselink.partitioning.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.eclipse.persistence.descriptors.partitioning.PartitioningPolicy;

/**
 * To create custom {@link PartitioningPolicy}s.
 *
 * @author  jbellmann
 */
@Target({ TYPE })
@Retention(RUNTIME)
public @interface CustomPartitioning {

    /**
     * The name of the partition policy, names must be unique for the persistence unit.
     */
    String name();

    /**
     * (Required) Full package.class name of a subclass of PartitioningPolicy.
     */
    Class<? extends PartitioningPolicy> partitioningClass();

    /**
     * Defines if queries that do not contain the partition field should be sent to every database and have the result
     * unioned.
     */
    boolean unionUnpartitionableQueries() default false;

}
