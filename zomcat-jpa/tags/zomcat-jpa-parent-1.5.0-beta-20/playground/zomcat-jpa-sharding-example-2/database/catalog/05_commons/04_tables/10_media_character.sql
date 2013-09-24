CREATE TABLE zcat_commons.media_character (
    mc_code                    text primary key,

    mc_created                 timestamptz                      NOT NULL  DEFAULT now(),
    mc_created_by              text                             NOT NULL,
    mc_last_modified           timestamptz                      NOT NULL  DEFAULT now(),
    mc_last_modified_by        text                             NOT NULL,
    mc_flow_id                 text                             NOT NULL,
    mc_version                 integer                          NOT NULL,

    mc_name                    text                             NOT NULL,
    mc_is_active               boolean                          NOT NULL DEFAULT true
);

SELECT zcat_commons.create_optimistic_locking_version_trigger('zcat_commons.media_character'::regclass);
