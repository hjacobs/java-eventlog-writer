CREATE TABLE zcat_commons.multimedia_type (
    mt_code                    text primary key,

    mt_created                 timestamptz                      NOT NULL  DEFAULT now(),
    mt_created_by              text                             NOT NULL,
    mt_last_modified           timestamptz                      NOT NULL  DEFAULT now(),
    mt_last_modified_by        text                             NOT NULL,
    mt_flow_id                 text                             NOT NULL,
    mt_version                 integer                          NOT NULL,

    mt_name                    text                             NOT NULL,
    mt_mime_type               text                             NOT NULL,
    mt_is_active               boolean                          NOT NULL DEFAULT true
);

SELECT zcat_commons.create_optimistic_locking_version_trigger('zcat_commons.multimedia_type'::regclass);