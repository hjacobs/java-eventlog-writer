CREATE OR REPLACE FUNCTION csv_import_get_state_by_uuid (
  p_uuid text
)
RETURNS SETOF csv_import_state AS
/*
-- $Id$
-- $Id$
-- $HeadURL$
*/
/**
 * Get csv import state by uuid.
 *
 * @ExpectedExecutionTime 10 ms
 * @ExpectedExecutionFrequency
 */
/* -- test
 begin;
  set search_path=zcat_api_r13_00_23,public;
  set client_min_messages to debug1;
  select * from csv_import_get_state_by_uuid('');
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
     WHERE cis_uuid = $1
     LIMIT 1;
$$
LANGUAGE SQL STABLE SECURITY DEFINER
COST 100;