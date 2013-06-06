package de.zalando.jpa;

import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import javax.validation.constraints.NotNull;

import org.junit.Assert;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.PersistenceExceptionTranslator;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.zalando.jpa.validation.ConstraintViolationExceptionLogger;
import de.zalando.jpa.validation.ConstraintViolationPrinter;

/**
 * @author  jbellmann
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/validator.xml"})
public class ConstraintViolationExceptionLoggerTest {

    @Autowired
    private Validator validator;

    @Test
    public void testLogConstraintViolationException() {
        ConstraintViolationPrinter mockPrinter = Mockito.mock(ConstraintViolationPrinter.class);
        ConstraintViolationExceptionLogger cvel = new ConstraintViolationExceptionLogger();
        cvel.setViolationPrinter(mockPrinter);

        DataAccessException dataAccessException = ((PersistenceExceptionTranslator) cvel).translateExceptionIfPossible(
                raiseException(new Bean()));
        Assert.assertNull("Should be null. We only log messages. No translation take place", dataAccessException);
        Mockito.verify(mockPrinter, Mockito.times(1)).printValidationErrors(Mockito.any(
                ConstraintViolationException.class));
    }

    @Test
    public void testLogPrinterNotInvoked() {
        ConstraintViolationPrinter mockPrinter = Mockito.mock(ConstraintViolationPrinter.class);
        ConstraintViolationExceptionLogger cvel = new ConstraintViolationExceptionLogger();
        cvel.setViolationPrinter(mockPrinter);

        DataAccessException dataAccessException = ((PersistenceExceptionTranslator) cvel).translateExceptionIfPossible(
                raiseException(new Bean("first", "last")));
        Assert.assertNull("Should be null. We only log messages. No translation take place", dataAccessException);
        Mockito.verify(mockPrinter, Mockito.never()).printValidationErrors(Mockito.any(
                ConstraintViolationException.class));
    }

    protected RuntimeException raiseException(final Bean bean) {
        SimpleBeanValidator beanValidator = new SimpleBeanValidator(validator);
        try {
            beanValidator.validate(bean);
        } catch (ConstraintViolationException e) {
            return e;
        }

        return new RuntimeException("Not a ConstraintViolation");
    }

    static final class Bean {
        @NotNull
        private String firstname;

        @NotNull
        private String lastname;

        public Bean() { }

        public Bean(final String firstname, final String lastname) {
            this.firstname = firstname;
            this.lastname = lastname;
        }
    }
}
