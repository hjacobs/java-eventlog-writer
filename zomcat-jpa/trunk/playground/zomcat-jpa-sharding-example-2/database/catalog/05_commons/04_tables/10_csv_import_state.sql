CREATE TABLE zcat_commons.csv_import_state (
    cis_id                  serial        PRIMARY KEY,
    cis_uuid                text          NOT NULL,
    cis_original_file_name  text,
    cis_file_name           text,
    cis_email               text          NOT NULL,
    cis_upload_time         timestamptz   NOT NULL,
    cis_start_time          timestamptz   DEFAULT NULL,
    cis_last_modified       timestamptz   DEFAULT now(),
    cis_status              zcat_commons.csv_import_status  NOT NULL  DEFAULT 'PENDING',
    cis_failure_reason      text          DEFAULT NULL,
    cis_lines_count         int,
    cis_invalid_sku_lines   int[],
    cis_priority            smallint
);

CREATE UNIQUE INDEX csv_import_state_uuid_uidx
  ON zcat_commons.csv_import_state(cis_uuid);
