package de.zalando.data.jpa.auditing;

import java.io.Serializable;

/**
 * Simple AuditorContext to match our need.
 *
 * @author  jbellmann
 */
public interface AuditorContext extends Serializable {

    String getAuditor();

    void setAuditor(String auditor);

}
