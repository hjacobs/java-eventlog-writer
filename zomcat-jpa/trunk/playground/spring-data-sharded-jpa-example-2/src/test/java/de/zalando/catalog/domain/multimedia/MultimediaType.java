package de.zalando.catalog.domain.multimedia;

import java.io.Serializable;

import javax.persistence.Column;

import javax.validation.constraints.NotNull;

import javax.xml.bind.annotation.XmlElement;

import org.hibernate.validator.constraints.NotBlank;

import de.zalando.catalog.domain.article.Versioned;

// @DatabaseType(inheritance = true)
// @XmlType(propOrder = {"multimediaTypeCode", "name", "mimeType", "active"})
public class MultimediaType extends Versioned implements Serializable {

    private static final long serialVersionUID = -7014464198279789003L;

    @Column
    private MultimediaTypeCode multimediaTypeCode;
    @Column
    private String name;
    @Column
    private String mimeType;
    @Column(name = "is_active")
    private boolean active;

    @XmlElement(name = "multimediaTypeCode", required = true)
    @NotNull
    public MultimediaTypeCode getMultimediaTypeCode() {
        return multimediaTypeCode;
    }

    public void setMultimediaTypeCode(final MultimediaTypeCode multimediaTypeCode) {
        this.multimediaTypeCode = multimediaTypeCode;
    }

    @XmlElement(name = "name", required = true)
    @NotBlank
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    @XmlElement(name = "name", required = true)
    @NotBlank
    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(final String mimeType) {
        this.mimeType = mimeType;
    }

    @XmlElement(name = "active", required = true)
    public boolean isActive() {
        return active;
    }

    public void setActive(final boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MultimediaType{");
        sb.append("multimediaTypeCode=").append(multimediaTypeCode);
        sb.append(", name='").append(name).append('\'');
        sb.append(", mimeType='").append(mimeType).append('\'');
        sb.append(", active=").append(active);
        sb.append('}');
        return sb.toString();
    }

}
