package de.zalando.jpa.validation;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

/**
 * Prints {@link ConstraintViolation}-messages.
 *
 * @author  jbellmann
 * @see     ConstraintViolationExceptionLogger
 */
public interface ConstraintViolationPrinter {

    /**
     * Prints a message from the set of violations.
     */
    void printValidationErrors(Set<ConstraintViolation<?>> violations);

    /**
     * Prints a message {@link ConstraintViolationException}.
     */
    void printValidationErrors(ConstraintViolationException constraintViolationException);

}
