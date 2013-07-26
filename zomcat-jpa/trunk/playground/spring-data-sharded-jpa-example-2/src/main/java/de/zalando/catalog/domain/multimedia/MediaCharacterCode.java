package de.zalando.catalog.domain.multimedia;

import java.io.Serializable;

import javax.persistence.Embeddable;

import javax.xml.bind.annotation.XmlElement;

import org.hibernate.validator.constraints.NotBlank;

import com.google.common.base.Objects;

// @XmlType(propOrder = {"code"})
// @XmlJavaTypeAdapter(MediaCharacterCodeAdapter.class)
// This can be modeled as an Embeddable
@Embeddable
public class MediaCharacterCode implements Serializable {

    private static final long serialVersionUID = -7281525429134342812L;

    private String code;

    protected MediaCharacterCode() {
        // just for JPA
    }

    public MediaCharacterCode(final String code) {
        this.code = code;
    }

    @NotBlank
    @XmlElement(name = "code", required = true)
    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("code", code).toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (code == null ? 0 : code.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final MediaCharacterCode other = (MediaCharacterCode) obj;
        if (code == null) {
            if (other.code != null) {
                return false;
            }
        } else if (!code.equals(other.code)) {
            return false;
        }

        return true;
    }

}
