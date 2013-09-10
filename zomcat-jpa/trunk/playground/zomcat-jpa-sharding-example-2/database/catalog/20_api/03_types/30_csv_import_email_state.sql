CREATE TYPE csv_import_email_state AS (
   csv_import_state csv_import_state,
   ready_line_count_per_shard bigint
);
