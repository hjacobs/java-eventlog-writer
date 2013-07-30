CREATE OR REPLACE FUNCTION csv_import_get_line_states_by_uuid(
  p_uuid        text
)
RETURNS SETOF csv_import_line_state AS
/*
-- $Id: 10_csv_import_get_line_states_by_uuid.sql 4209 2013-06-06 14:25:24Z anton.smolich $
-- $HeadURL: https://svn.zalando.net/zeos-catalog/trunk/catalog-service/backend/database/catalog/20_api/05_stored_procedures/10_csv_import_get_line_state_by_line_number_and_uuid.sql $
*/
/**
 * Get line state by id.
 *
 * @ExpectedExecutionTime 10 ms
 * @ExpectedExecutionFrequency
 */
/* -- test
 begin;
  set search_path=zcat_api_r13_00_23,public;
  set client_min_messages to debug1;
  select * from csv_import_get_line_state_by_uuid("");
 rollback;
 */
$$
    SELECT (cils_id,
           cis_uuid,
           cils_status,
           cils_failure_reason,
           cils_line_number,
           cils_last_modified) :: csv_import_line_state
      FROM zcat_data.csv_import_line_state cls
      JOIN zcat_commons.csv_import_state cs ON cs.cis_id = cls.cils_csv_import_state_id
      WHERE cs.cis_uuid = $1
$$
LANGUAGE SQL STABLE SECURITY DEFINER
COST 100;