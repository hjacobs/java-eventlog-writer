/**
 * Marcel Wieczorek
 * Zalando GmbH
 * Nov 8, 2012 11:52:29 AM
 */
package de.zalando.jpa.validation;

import javax.validation.ConstraintViolationException;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.PersistenceExceptionTranslator;

/**
 * @author  <a href="mailto:marcel.wieczorek@zalando.de" title="Marcel Wieczorek">mwieczorek</a>
 */
public class ConstraintViolationExceptionTranslator implements PersistenceExceptionTranslator {

    /*
     * (non-Javadoc)
     *
     * @see org.springframework.dao.support.PersistenceExceptionTranslator#
     * translateExceptionIfPossible(java.lang.RuntimeException)
     */
    @Override
    public DataAccessException translateExceptionIfPossible(final RuntimeException ex) {
        if (ex instanceof ConstraintViolationException) {
            return new ReportingConstraintViolationException((ConstraintViolationException) ex);
        }

        return null;
    }

}
