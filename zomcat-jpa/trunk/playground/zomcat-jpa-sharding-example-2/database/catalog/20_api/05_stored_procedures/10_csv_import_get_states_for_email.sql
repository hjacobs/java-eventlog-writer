CREATE OR REPLACE FUNCTION csv_import_get_states_for_email(
  p_time_out integer
)
RETURNS SETOF csv_import_email_state AS
/*
-- $HeadURL: https://svn.zalando.net/zeos-catalog/trunk/catalog-service/backend/database/catalog/20_api/05_stored_procedures/10_csv_import_get_states_for_email.sql $
*/
/**
 * Get uuids for the imports which have time_out exceeded or status is set to processing.
 *
 * @ExpectedExecutionTime 10 ms
 * @ExpectedExecutionFrequency
 */
/* -- test
 begin;
  set search_path=zcat_api_r13_00_29,public;
  set client_min_messages to debug1;
  select * from csv_import_get_states_for_email('');
 rollback;
 */
$$
SELECT ROW
       (ROW(cs.cis_uuid,
        cs.cis_original_file_name,
        cs.cis_file_name,
        cs.cis_email,
        cs.cis_upload_time,
        cs.cis_start_time,
        cs.cis_last_modified,
        cs.cis_status,
        cs.cis_failure_reason,
        cs.cis_lines_count,
        cs.cis_invalid_sku_lines,
        cs.cis_priority
        ),
        COUNT (cls.cils_csv_import_state_id)
)  :: csv_import_email_state
FROM zcat_commons.csv_import_state cs
JOIN zcat_data.csv_import_line_state cls ON cs.cis_id = cls.cils_csv_import_state_id
WHERE
      (
         cs.cis_original_file_name IS NOT NULL AND cs.cis_file_name IS NOT NULL  AND cs.cis_lines_count IS NOT NULL
      )
      AND
      (
        cs.cis_status NOT IN ('EMAIL_SUCCESSFUL', 'EMAIL_FAILED')
      )
      AND (

          cls.cils_status IN ('COMMITTED', 'FAILED')

         OR
          (
            SELECT extract (EPOCH FROM now ()) -  extract (EPOCH FROM cls.cils_last_modified) > $1
               AND cls.cils_status IN ('SUBMITTED', 'PROCESSING')
          )
      )


GROUP BY
  cs.cis_uuid,
  cs.cis_original_file_name,
  cs.cis_file_name,
  cs.cis_email,
  cs.cis_upload_time,
  cs.cis_start_time,
  cs.cis_last_modified,
  cs.cis_status,
  cs.cis_failure_reason,
  cs.cis_lines_count,
  cs.cis_invalid_sku_lines,
  cs.cis_priority
HAVING
  COUNT (cls.cils_csv_import_state_id) > 0
$$
LANGUAGE SQL STABLE SECURITY DEFINER
COST 100;
