package de.zalando.jpa.springframework;

import javax.persistence.RollbackException;

import javax.validation.ConstraintViolationException;

import org.springframework.dao.DataAccessException;

import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaDialect;

import org.springframework.util.Assert;

import de.zalando.jpa.validation.ConstraintViolationPrinter;
import de.zalando.jpa.validation.LogConstraintViolationPrinter;

/**
 * This extended version for {@link EclipseLinkJpaDialect} is intended to only log {@link ConstraintViolationException}s.
 *
 * @author  jbellmann
 */
public class ExtendedEclipseLinkJpaDialect extends EclipseLinkJpaDialect {

    private static final long serialVersionUID = 1L;

    private ConstraintViolationPrinter violationPrinter = new LogConstraintViolationPrinter();

    /**
     * This method will be called when the {@link JpaTransactionManager} tries to translate the 'cause' for the
     * {@link RollbackException}.<br/>
     * We use this only for logging the message in a more readable manner.
     *
     * @see  JpaTransactionManager#doCommit(org.springframework.transaction.TransactionStatus)
     */
    @Override
    public DataAccessException translateExceptionIfPossible(final RuntimeException ex) {
        DataAccessException translated = super.translateExceptionIfPossible(ex);
        if (ex instanceof ConstraintViolationException) {
            violationPrinter.printValidationErrors((ConstraintViolationException) ex);
        }

        return translated;
    }

    public ConstraintViolationPrinter getViolationPrinter() {
        return violationPrinter;
    }

    public void setViolationPrinter(final ConstraintViolationPrinter constraintViolationPrinter) {
        Assert.notNull(constraintViolationPrinter, "ConstraintViolationPrinter should never be null");
        this.violationPrinter = constraintViolationPrinter;
    }

}
