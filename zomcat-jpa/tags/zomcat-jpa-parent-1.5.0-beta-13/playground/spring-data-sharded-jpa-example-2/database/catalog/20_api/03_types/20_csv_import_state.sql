CREATE TYPE csv_import_state AS (
    uuid                text,
    original_file_name  text,
    file_name           text,
    email               text,
    upload_time         timestamptz,
    start_time          timestamptz,
    last_modified       timestamptz,
    status              zcat_commons.csv_import_status,
    failure_reason      text,
    lines_count         int,
    invalid_sku_lines   int[],
    priority            smallint
);
