CREATE TYPE zcat_data.csv_import_line_status as ENUM (
    'PROCESSING',
    'SUBMITTED',
    'COMMITTED',
    'FAILED'
);
