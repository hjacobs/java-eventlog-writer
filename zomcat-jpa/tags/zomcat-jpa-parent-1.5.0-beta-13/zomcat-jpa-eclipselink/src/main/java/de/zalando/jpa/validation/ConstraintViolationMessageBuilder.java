package de.zalando.jpa.validation;

import java.util.Set;

import javax.validation.ConstraintViolation;

/**
 * @author  jbellmann
 */
public interface ConstraintViolationMessageBuilder {

    String buildMessage(final Set<ConstraintViolation<?>> violations);

}
