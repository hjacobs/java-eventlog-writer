CREATE OR REPLACE FUNCTION csv_import_get_line_state_by_line_number_and_uuid(
  p_line_number integer,
  p_uuid        text
)
RETURNS SETOF csv_import_line_state AS
/*
-- $Id$
-- $HeadURL$
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
  select * from csv_import_get_line_state_by_line_number_and_uuid(1);
 rollback;
 */
$$
    SELECT (cils_id,
           cis_uuid,
           cils_status,
           cils_failure_reason,
           cils_line_number,
           cils_last_modified) :: csv_import_line_state
      FROM zcat_data.csv_import_line_state
      JOIN zcat_commons.csv_import_state ON cis_id = cils_csv_import_state_id
     WHERE cils_line_number = $1
       AND cis_uuid = $2
     LIMIT 1;
$$
LANGUAGE SQL STABLE SECURITY DEFINER
COST 100;