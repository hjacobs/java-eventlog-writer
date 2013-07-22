package de.zalando.catalog.domain.multimedia;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.Converter;
import org.eclipse.persistence.annotations.Converters;
import org.eclipse.persistence.annotations.Partitioned;

import de.zalando.catalog.domain.ShardedId;
import de.zalando.catalog.domain.article.Versioned;
import de.zalando.catalog.domain.multimedia.adapter.MediaCharacterCodeConverter;
import de.zalando.catalog.domain.multimedia.adapter.MultimediaTypeCodeConverter;
import de.zalando.catalog.domain.sku.Sku;
import de.zalando.catalog.domain.transformer.ShardedIdConverter;

import de.zalando.sprocwrapper.sharding.ShardedObject;

import de.zalando.typemapper.annotations.DatabaseField;
import de.zalando.typemapper.annotations.DatabaseType;

@Entity
@Table(schema = "zcat_data")
@DatabaseType(inheritance = true)
@XmlType(
    propOrder = {"code", "sku", "typeCode", "external", "path", "mediaCharacterCode", "checksum", "width", "height"}
)
@Converters(
    {
        @Converter(name = "shardedIdConverter", converterClass = ShardedIdConverter.class),
        @Converter(
            name = "mediaCharacterCodeConverter", converterClass = MediaCharacterCodeConverter.class
        ), @Converter(
            name = "multimediaTypeCodeConverter", converterClass = MultimediaTypeCodeConverter.class
        )
    }
)
@Partitioned("SkuSharding")
public class Multimedia extends Versioned implements ShardedObject {

    @Column
    @Id
    @Convert("shardedIdConverter")
    private ShardedId code;
    @ManyToOne(targetEntity = ArticleSku.class)
    @DatabaseField
    private Sku sku;
    @Column
    @Convert("multimediaTypeCodeConverter")
    private MultimediaTypeCode typeCode;
    @Column(name = "is_external")
    private boolean external;
    @Column
    private String path;
    @Column
    @Convert("mediaCharacterCodeConverter")
    private MediaCharacterCode mediaCharacterCode;
    @Column
    private String checksum;
    @Column
    private int width;
    @Column
    private int height;

    @XmlElement(name = "code")
    public ShardedId getCode() {
        return code;
    }

    public void setCode(final ShardedId code) {
        this.code = code;
    }

    @XmlElement(name = "sku", required = true)
    public Sku getSku() {
        return sku;
    }

    public void setSku(final Sku sku) {
        this.sku = sku;
    }

    @XmlElement(name = "typeCode", required = true)
    public MultimediaTypeCode getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(final MultimediaTypeCode typeCode) {
        this.typeCode = typeCode;
    }

    @XmlElement(name = "external", required = true)
    public boolean isExternal() {
        return external;
    }

    public void setExternal(final boolean external) {
        this.external = external;
    }

    @XmlElement(name = "path", required = true)
    public String getPath() {
        return path;
    }

    public void setPath(final String path) {
        this.path = path;
    }

    @XmlElement(name = "mediaCharacterCode", required = true)
    public MediaCharacterCode getMediaCharacterCode() {
        return mediaCharacterCode;
    }

    public void setMediaCharacterCode(final MediaCharacterCode mediaCharacterCode) {
        this.mediaCharacterCode = mediaCharacterCode;
    }

    @XmlElement(name = "checksum")
    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(final String checksum) {
        this.checksum = checksum;
    }

    @XmlElement(name = "width", required = true)
    public int getWidth() {
        return width;
    }

    public void setWidth(final int width) {
        this.width = width;
    }

    @XmlElement(name = "height", required = true)
    public int getHeight() {
        return height;
    }

    public void setHeight(final int height) {
        this.height = height;
    }

    @Override
    public Object getShardKey() {
        return sku;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Multimedia{");
        sb.append("code=").append(code);
        sb.append(", sku=").append(sku);
        sb.append(", typeCode=").append(typeCode);
        sb.append(", external=").append(external);
        sb.append(", path='").append(path).append('\'');
        sb.append(", mediaCharacterTypeCode=").append(mediaCharacterCode);
        sb.append(", checksum='").append(checksum).append('\'');
        sb.append(", width=").append(width);
        sb.append(", height=").append(height);
        sb.append('}');
        return sb.toString();
    }
}
