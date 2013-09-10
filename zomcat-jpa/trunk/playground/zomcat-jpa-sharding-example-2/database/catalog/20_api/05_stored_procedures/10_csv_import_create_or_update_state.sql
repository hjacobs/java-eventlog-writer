CREATE OR REPLACE FUNCTION csv_import_create_or_update_state(
  p_csv_import_state csv_import_state
)
RETURNS VOID AS
/*
-- $Id$
-- $HeadURL$
*/
/**
 * Create or update line state.
 *
 * @ExpectedExecutionTime 10 ms
 * @ExpectedExecutionFrequency up to 4 times for each csv import
 */
/* -- test
 begin;
  set search_path=zcat_api_r13_00_23,public;
  set client_min_messages to debug1;
  select * from csv_import_create_or_update_state(ROW());
 rollback;
 */
$BODY$
DECLARE
    l_status  zcat_commons.csv_import_status;
    l_uuid    text;
    l_cis_id  integer;
BEGIN

  l_status := COALESCE(p_csv_import_state.status, 'PENDING'::zcat_commons.csv_import_status);

  SELECT cis_id INTO l_cis_id
    FROM zcat_commons.csv_import_state
   WHERE cis_uuid = p_csv_import_state.uuid;

  IF l_cis_id IS NULL THEN
    INSERT
      INTO zcat_commons.csv_import_state (
             cis_uuid,
             cis_original_file_name,
             cis_file_name,
             cis_email,
             cis_upload_time,
             cis_start_time,
             cis_status,
             cis_failure_reason,
             cis_lines_count,
             cis_invalid_sku_lines,
             cis_priority
           )
    VALUES (
             p_csv_import_state.uuid,
             p_csv_import_state.original_file_name,
             p_csv_import_state.file_name,
             p_csv_import_state.email,
             p_csv_import_state.upload_time,
             p_csv_import_state.start_time,
             l_status,
             p_csv_import_state.failure_reason,
             p_csv_import_state.lines_count,
             p_csv_import_state.invalid_sku_lines,
             p_csv_import_state.priority
           );
  ELSE
    UPDATE zcat_commons.csv_import_state
       SET cis_original_file_name = p_csv_import_state.original_file_name,
           cis_file_name          = p_csv_import_state.file_name,
           cis_email              = p_csv_import_state.email,
           cis_upload_time        = p_csv_import_state.upload_time,
           cis_start_time         = p_csv_import_state.start_time,
           cis_last_modified      = now(),
           cis_status             = l_status,
           cis_failure_reason     = p_csv_import_state.failure_reason,
           cis_lines_count        = p_csv_import_state.lines_count,
           cis_invalid_sku_lines  = p_csv_import_state.invalid_sku_lines,
           cis_priority           = p_csv_import_state.priority
     WHERE cis_uuid = p_csv_import_state.uuid;
  END IF;

  RAISE INFO 'csv_import_create_or_update_state : uuid = %', p_csv_import_state.uuid;

END
$BODY$
language plpgsql
    volatile security definer
    cost 100;
