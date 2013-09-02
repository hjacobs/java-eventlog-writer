CREATE TABLE zcat_data.csv_import_line_state (
    cils_id                     bigserial    PRIMARY KEY,
    cils_csv_import_state_id    integer      NOT NULL     REFERENCES zcat_commons.csv_import_state (cis_id),
    cils_status                 zcat_data.csv_import_line_status  NOT NULL  DEFAULT 'PROCESSING',
    cils_failure_reason         text         DEFAULT NULL,
    cils_line_number            integer      NOT NULL ,
    cils_last_modified          timestamptz  DEFAULT now()
);
