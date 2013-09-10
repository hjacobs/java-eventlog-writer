CREATE TYPE csv_import_line_state AS (
    id                     bigint,
    csv_import_state_uuid  text,
    status                 zcat_data.csv_import_line_status,
    failure_reason         text,
    line_number            integer,
    last_modified          timestamptz
);
