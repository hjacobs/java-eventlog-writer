package de.zalando.data.jpa.auditing;

import com.google.common.base.Objects;

/**
 * Holds the auditor-name.
 *
 * @author  jbellmann
 */
class AuditorContextImpl implements AuditorContext {

    private static final long serialVersionUID = 1L;

    private String auditor;

    @Override
    public String getAuditor() {
        return auditor;
    }

    @Override
    public void setAuditor(final String auditor) {
        this.auditor = auditor;
    }

    @Override
    public int hashCode() {
        if (this.auditor == null) {
            return -1;
        } else {
            return this.auditor.hashCode();
        }
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }

        if (!(obj instanceof AuditorContextImpl)) {
            return false;
        }

        final AuditorContextImpl other = (AuditorContextImpl) obj;
        return Objects.equal(this.auditor, other.auditor);
    }

    @Override
    public String toString() {
        return "AuditorContextImpl[auditor=" + getAuditor() + "]";
    }

}
