CREATE TABLE multimedia (m_code VARCHAR NOT NULL, m_checksum VARCHAR, m_is_external BOOLEAN, m_height INTEGER, m_media_character_code VARCHAR, m_path VARCHAR, m_type_code VARCHAR, m_width INTEGER, m_sku_id BIGINT, PRIMARY KEY (m_code));
CREATE TABLE article_sku (as_id BIGINT NOT NULL, as_sku VARCHAR, as_sku_type VARCHAR, PRIMARY KEY (as_id));
ALTER TABLE multimedia ADD CONSTRAINT FK_multimedia_m_sku_id FOREIGN KEY (m_sku_id) REFERENCES article_sku (as_id);
