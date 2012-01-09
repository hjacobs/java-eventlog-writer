package de.zalando.storedprocedurewrapper.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author  jmussler
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface SprocParam {
    String name() default "";

    String type() default "";

    int javaPosition() default -1;

    int sqlPosPosition() default -1;
}
