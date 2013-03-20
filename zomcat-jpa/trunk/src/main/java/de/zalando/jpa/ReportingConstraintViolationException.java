package de.zalando.jpa;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.springframework.dao.DataIntegrityViolationException;

public class ReportingConstraintViolationException extends DataIntegrityViolationException {

    private static final long serialVersionUID = -7746311056397921637L;

    public ReportingConstraintViolationException(final ConstraintViolationException cause) {
        super(cause.getMessage(), cause);
    }

    /* (non-Javadoc)
     * @see org.springframework.core.NestedRuntimeException#getMessage()
     */
    @Override
    public String getMessage() {
        final StringBuilder sb = new StringBuilder();

        final ConstraintViolationException e = (ConstraintViolationException) getCause();
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        for (final ConstraintViolation<?> violation : violations) {
            sb.append("ConstraintViolationMessage: ").append(violation.getMessage());
            sb.append("! Property \"").append(violation.getPropertyPath().toString()).append("\" of ");
            if (!violation.getRootBean().equals(violation.getLeafBean())) {
                sb.append(violation.getRootBean()).append(" ");
            }

            sb.append(violation.getLeafBean());
            sb.append(" has value: ");
            sb.append(violation.getInvalidValue()).append("! ");
        }

        sb.append("This might cause the error: ").append(e.getLocalizedMessage());

        return sb.toString();
    }
}
