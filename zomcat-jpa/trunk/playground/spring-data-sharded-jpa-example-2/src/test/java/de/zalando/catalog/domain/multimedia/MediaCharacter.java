package de.zalando.catalog.domain.multimedia;

import java.io.Serializable;

import javax.persistence.Column;

import javax.validation.constraints.NotNull;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.validator.constraints.NotBlank;

import de.zalando.catalog.domain.article.Versioned;

import de.zalando.typemapper.annotations.DatabaseType;

@DatabaseType(inheritance = true)
@XmlType(propOrder = {"mediaCharacterCode", "name", "active"})
public class MediaCharacter extends Versioned implements Serializable {

    private static final long serialVersionUID = -6118229386023480372L;

    @Column
    private MediaCharacterCode mediaCharacterCode;
    @Column
    private String name;
    @Column(name = "is_active")
    private boolean active;

    @XmlElement(name = "mediaCharacterTypeCode", required = true)
    @NotNull
    public MediaCharacterCode getMediaCharacterCode() {
        return mediaCharacterCode;
    }

    public void setMediaCharacterCode(final MediaCharacterCode mediaCharacterCode) {
        this.mediaCharacterCode = mediaCharacterCode;
    }

    @XmlElement(name = "name", required = true)
    @NotBlank
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
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
        final StringBuilder sb = new StringBuilder("MediaCharacter{");
        sb.append("mediaCharacterCode=").append(mediaCharacterCode);
        sb.append(", name='").append(name).append('\'');
        sb.append(", active=").append(active);
        sb.append('}');
        return sb.toString();
    }

}
