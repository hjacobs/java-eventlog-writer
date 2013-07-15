package de.zalando.sprocwrapper.sharding;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// TODO    What is this annotation used for?
/**
 * @author  jmussler
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER, ElementType.FIELD })
@Inherited
public @interface ShardKey {

    // TODO add some documenation what pos() stands for !!!
    int pos() default -1;
}
