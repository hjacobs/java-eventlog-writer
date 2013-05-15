package de.zalando.jpa.validation;

/**
 * {@link ConstraintViolationPrinter} that prints messages to {@link System#err}.
 *
 * @author  jbellmann
 */
public class SystemErrConstraintViolationPrinter extends AbstractConstraintViolationPrinter {

    @Override
    public void printMessage(final String message) {
        System.err.println(message);
    }

}
