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
            // StringBuilder sb = new StringBuilder("\n\n");
            // for (final ConstraintViolation<?> violation : violations) {
            // sb.append("\t[VALIDATION-ERROR]  ");
            // if (!violation.getRootBean().equals(violation.getLeafBean())) {
            // sb.append(violation.getRootBean()).append(" ");
            // }
            //
            // sb.append(violation.getLeafBean().getClass().getSimpleName());
            // sb.append("#");
            // sb.append(violation.getPropertyPath().toString());
            // sb.append(" -- '");
            // sb.append(violation.getMessage());
            // sb.append("', but has value : ");
            // sb.append(violation.getInvalidValue());
            // sb.append(" on object : ").append(violation.getLeafBean().toString());
            // sb.append("\n");
            // }
            //
            // return sb.toString();
    }

    @Override
    public void printValidationErrors(final ConstraintViolationException constraintViolationException) {
        printValidationErrors(constraintViolationException.getConstraintViolations());
    }

    public void setConstraintViolationMessageBuilder(
            final ConstraintViolationMessageBuilder constraintViolationMessageBuilder) {

        Preconditions.checkNotNull(constraintViolationMessageBuilder,
            "ConstraintViolationMessageBuilder should not be null");

        this.constraintViolationMessageBuilder = constraintViolationMessageBuilder;
    }

}
