package de.zalando.jpa.validation;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import com.google.common.base.Preconditions;

/**
 * Builds an simple message and prints it to specific target.
 *
 * @author  jbellmann
 * @see     {@link LogConstraintViolationPrinter}
 * @see     {@link SystemErrConstraintViolationPrinter}
 */
public abstract class AbstractConstraintViolationPrinter implements ConstraintViolationPrinter {

    static final String NOT_NULL_MSG = "ConstraintViolationMessageBuilder should not be null";

    private ConstraintViolationMessageBuilder constraintViolationMessageBuilder =
        new DefaultConstraintViolationMessageBuilder();

    @Override
    public void printValidationErrors(final Set<ConstraintViolation<?>> violations) {
        String message = buildMessage(violations);
        printMessage(message);
    }

    public abstract void printMessage(String message);

    protected String buildMessage(final Set<ConstraintViolation<?>> violations) {
        return constraintViolationMessageBuilder.buildMessage(violations);
    }

    @Override
    public void printValidationErrors(final ConstraintViolationException constraintViolationException) {
        printValidationErrors(constraintViolationException.getConstraintViolations());
    }

    public void setConstraintViolationMessageBuilder(
            final ConstraintViolationMessageBuilder constraintViolationMessageBuilder) {

        Preconditions.checkNotNull(constraintViolationMessageBuilder, NOT_NULL_MSG);

        this.constraintViolationMessageBuilder = constraintViolationMessageBuilder;
    }

}
