package de.zalando.catalog.domain.article;

import javax.persistence.Column;
import javax.persistence.Version;

import javax.xml.bind.annotation.XmlElement;

public abstract class Versioned {

    @Column
    @Version
    private Integer version;

    @XmlElement(required = false)
    public Integer getVersion() {
        return version;
    }

    public void setVersion(final Integer version) {
        this.version = version;
    }
}
