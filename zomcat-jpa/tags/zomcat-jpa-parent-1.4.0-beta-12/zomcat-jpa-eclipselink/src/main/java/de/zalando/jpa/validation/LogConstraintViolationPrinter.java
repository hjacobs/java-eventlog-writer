package de.zalando.jpa.validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link ConstraintViolationPrinter} that prints messages to the underlying LOG-System.
 *
 * @author  jbellmann
 */
public class LogConstraintViolationPrinter extends AbstractConstraintViolationPrinter {

    private static final Logger LOG = LoggerFactory.getLogger(LogConstraintViolationPrinter.class);

    @Override
    public void printMessage(final String message) {
        LOG.error(message);
    }

}
