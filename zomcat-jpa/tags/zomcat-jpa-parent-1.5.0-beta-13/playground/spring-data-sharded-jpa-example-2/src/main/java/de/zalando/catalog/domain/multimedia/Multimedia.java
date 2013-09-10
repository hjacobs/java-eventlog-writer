package de.zalando.catalog.domain.multimedia;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import javax.validation.constraints.NotNull;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.persistence.annotations.Partitioned;

import com.google.common.base.Preconditions;

import de.zalando.catalog.domain.ShardedId;
import de.zalando.catalog.domain.article.Versioned;
import de.zalando.catalog.domain.sku.Sku;

import de.zalando.sprocwrapper.sharding.ShardedObject;

@Entity
@Table(name = "multimedia")

// @DatabaseType(inheritance = true)
@XmlType(
    propOrder = {"code", "sku", "typeCode", "external", "path", "mediaCharacterCode", "checksum", "width", "height"}
)
// @Converters(
// {
// @Converter(name = "shardedIdConverter", converterClass = ShardedIdConverter.class)

// @Converter(
// name = "mediaCharacterCodeConverter", converterClass = MediaCharacterCodeConverter.class
// )
// @Converter(
// name = "multimediaTypeCodeConverter", converterClass = MultimediaTypeCodeConverter.class
// )
// }
// )
@Partitioned("SkuSharding")
public class Multimedia extends Versioned implements ShardedObject {

    @Id
// @Convert("shardedIdConverter")
    private Long id;
// private ShardedId code;

    @ManyToOne(targetEntity = ArticleSku.class)
// @DatabaseField
    private Sku sku;

// @Convert("multimediaTypeCodeConverter")
    @NotNull
    @Embedded
    @AttributeOverrides(@AttributeOverride(column = @Column(name = "type_code"), name = "code"))
    private MultimediaTypeCode typeCode;

// @Column(name = "is_external")
    private boolean external;

// @Column
    private String path;

// @Column
// @Convert("mediaCharacterCodeConverter")
    @NotNull
    @Embedded
    @AttributeOverrides(@AttributeOverride(column = @Column(name = "media_character_code"), name = "code"))
    private MediaCharacterCode mediaCharacterCode;

    private String checksum;

    private int width;

    private int height;

    protected Multimedia() {
        // just for JPA
    }

    /**
     * Only useful constructor, ensure an ID for Multimedia.
     *
     * @param  shardedId
     */
    public Multimedia(final ShardedId shardedId) {
        Preconditions.checkNotNull(shardedId, "ShardedId should never be null.");
        this.id = shardedId.asLong();
    }

    @XmlElement(name = "code")
    public ShardedId getCode() {
        return ShardedId.of(id);
    }

    public void setCode(final ShardedId code) {
        this.id = code.asLong();
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
        sb.append("id=").append(id);
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
