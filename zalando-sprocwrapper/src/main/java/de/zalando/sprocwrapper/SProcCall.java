package de.zalando.sprocwrapper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author  jmussler
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface SProcCall {

    public static enum AdvisoryLock {
        NO_LOCK,
        EMAIL_SELECT_UNPROCESSED_LOCK
    }

    String name() default "";

    String sql() default "";

    Class shardStrategy() default Void.class;

    /**
     * whether the stored procedure should be called on all shards --- results are concatenated together.
     *
     * @return
     */
    boolean runOnAllShards() default false;

    /**
     * whether the stored procedure should be called on all shards --- return the first result found.
     *
     * @return
     */
    boolean searchShards() default false;

    /**
     * run sproc on multiple shards in parallel?
     *
     * @return
     */
    boolean parallel() default false;

    /**
     * flag this stored procedure call as read only: read only sprocs may run in cases were writing calls would not be
     * allowed (maintenance, migration, ..)
     *
     * @return
     */
    boolean readOnly() default false;

    Class resultMapper() default Void.class;

    long timeoutInMilliSeconds() default 0;

    AdvisoryLock adivsoryLockType() default AdvisoryLock.NO_LOCK;
}
