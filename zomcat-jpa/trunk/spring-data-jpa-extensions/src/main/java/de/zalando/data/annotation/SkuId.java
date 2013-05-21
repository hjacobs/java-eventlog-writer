package de.zalando.data.annotation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a member of an Entity as SkuId.
 *
 * @author  jbellmann
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {FIELD, METHOD, ANNOTATION_TYPE})
public @interface SkuId {

    /**
     * @return  the sequenceName to be used for generation
     */
    String value();

    /**
     * should the result negated.
     *
     * @return  defaults to false
     */
    boolean negate() default false;

}
