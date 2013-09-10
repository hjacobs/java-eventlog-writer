CREATE TABLE zcat_data.multimedia (
    m_code                     bigint primary key,

    m_created                  timestamptz                      NOT NULL  DEFAULT now(),
    m_created_by               text                             NOT NULL,
    m_last_modified            timestamptz                      NOT NULL  DEFAULT now(),
    m_last_modified_by         text                             NOT NULL,
    m_flow_id                  text                             NOT NULL,
    m_version                  integer                          NOT NULL,

    m_sku_id                   int references zcat_data.article_sku(as_id) NOT NULL,
    m_type_code                text references zcat_commons.multimedia_type(mt_code) NOT NULL,
    m_is_external              boolean NOT NULL DEFAULT false,
    m_path                     text NOT NULL,
    m_media_character_code     text references zcat_commons.media_character(mc_code) NOT NULL,
    m_checksum                 text,
    m_width                    int NOT NULL,
    m_height                   int NOT NULL

    CONSTRAINT multimedia_code_id_type_constraint CHECK (m_code::bit(64)::bit(8)::int = 1) -- takes first 8 bits as int, 1 is type for multimedia id
);

SELECT zcat_commons.create_optimistic_locking_version_trigger('zcat_data.multimedia'::regclass);

create sequence zcat_data.shard_id_sequence_multimedia increment by 1;
