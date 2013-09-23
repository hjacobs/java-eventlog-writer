CREATE TYPE zcat_commons.csv_import_status as ENUM (
    'PENDING',
    'PROCESSING',
    'SUCCESSFUL',
    'FAILED',
    'EMAIL_SUCCESSFUL',
    'EMAIL_FAILED'
);
