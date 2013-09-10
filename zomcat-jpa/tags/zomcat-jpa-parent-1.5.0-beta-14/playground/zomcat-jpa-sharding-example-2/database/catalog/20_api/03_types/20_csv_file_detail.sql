CREATE TYPE csv_file_details AS (
    original_file_name  text,
    file_name           text,
    email               text,
    upload_time         timestamptz,
    priority            smallint
);
