package de.zalando.sprocwrapper.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.zalando.sprocwrapper.sharding.VirtualShardKeyStrategy;

/**
 * @author  jmussler
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface SProcCall {
    String name() default "";

    String sql() default "";

    Class shardStrategy() default VirtualShardKeyStrategy.class;
}
