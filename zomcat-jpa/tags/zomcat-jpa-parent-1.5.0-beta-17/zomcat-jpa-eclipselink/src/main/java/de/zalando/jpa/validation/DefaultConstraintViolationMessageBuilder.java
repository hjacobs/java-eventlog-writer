package de.zalando.jpa.validation;

import java.util.Set;

import javax.validation.ConstraintViolation;

/**
 * @author  jbellmann
 */
public class DefaultConstraintViolationMessageBuilder implements ConstraintViolationMessageBuilder {

    @Override
    public String buildMessage(final Set<ConstraintViolation<?>> violations) {
        StringBuilder sb = new StringBuilder("\n\n");
        for (final ConstraintViolation<?> violation : violations) {
            sb.append("\t[VALIDATION-ERROR]  ");
            if (!violation.getRootBean().equals(violation.getLeafBean())) {
                sb.append(violation.getRootBean()).append(" ");
            }

            sb.append(violation.getLeafBean().getClass().getSimpleName());
            sb.append("#");
            sb.append(violation.getPropertyPath().toString());
            sb.append(" -- '");
            sb.append(violation.getMessage());
            sb.append("', but has value : ");
            sb.append(violation.getInvalidValue());
            sb.append(" on object : ").append(violation.getLeafBean().toString());
            sb.append("\n");
        }

        return sb.toString();
    }

}
