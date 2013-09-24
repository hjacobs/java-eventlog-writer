CREATE OR REPLACE FUNCTION csv_import_get_first_import_state_to_process(
  p_waiting_time integer
)
RETURNS SETOF csv_import_state AS
/*
-- $Id:
-- $HeadURL$
*/
/**
 * Get the first csv import to process.
 *
 * @ExpectedExecutionTime 10 ms
 * @ExpectedExecutionFrequency
 */
/* -- test
 begin;
  set search_path=zcat_api_r13_00_30,public;
  set client_min_messages to debug1;
  select * from csv_import_get_first_import_state_to_process(60);
 rollback;
 */
$$
    SELECT (cis_uuid,
           cis_original_file_name,
           cis_file_name,
           cis_email,
           cis_upload_time,
           cis_start_time,
           cis_last_modified,
           cis_status,
           cis_failure_reason,
           cis_lines_count,
           cis_invalid_sku_lines,
           cis_priority) :: csv_import_state
      FROM zcat_commons.csv_import_state
     WHERE cis_status = 'PENDING'
       AND (SELECT EXTRACT (epoch FROM now ()) - EXTRACT (epoch FROM cis_upload_time) > $1)
       AND cis_original_file_name IS NOT NULL
       AND cis_file_name IS NOT NULL
     ORDER BY cis_priority IS NULL ASC,
              cis_priority DESC,
              cis_upload_time ASC
     LIMIT 1
$$
LANGUAGE SQL VOLATILE SECURITY DEFINER
COST 100;
