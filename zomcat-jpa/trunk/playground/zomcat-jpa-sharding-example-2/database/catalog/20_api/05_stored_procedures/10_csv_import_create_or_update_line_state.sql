CREATE OR REPLACE FUNCTION csv_import_create_or_update_line_state(
  p_csv_import_line_state csv_import_line_state
)
RETURNS void AS
/*
-- $Id$
-- $HeadURL$
*/
/**
 * Create or update line state.
 *
 * @ExpectedExecutionTime 10 ms
 * @ExpectedExecutionFrequency up to 4 times for each csv line in csv import
 */
/* -- test
begin;

set search_path = zcat_api_r13_00_30;

select * from csv_import_create_or_update_state((
             '795c6452-9e2b-41c6-9cca-e36962a330f8'::text,
             '795c6452-9e2b-41c6-9cca-e36962a330f8'::text,
             '795c6452-9e2b-41c6-9cca-e36962a330f8'::text,
             '795c6452-9e2b-41c6-9cca-e36962a330f8'::text,
             now(),
             now(),
             now(),
             null,
             'test'::text,
             3,
             '{}'::int[],
             null)::csv_import_state);

select * from csv_import_create_or_update_line_state
            ROW (null, '795c6452-9e2b-41c6-9cca-e36962a330f8', 'COMMITTED'::zcat_data.csv_import_line_status, null, 1, null)::csv_import_line_state
            );

select * from zcat_data.csv_import_line_state
join zcat_commons.csv_import_state on cis_id = cils_csv_import_state_id
where cis_uuid = '795c6452-9e2b-41c6-9cca-e36962a330f8';

rollback;

 */
$BODY$
DECLARE
    l_status                 zcat_data.csv_import_line_status;
    l_id                     bigint;
    l_cis_id                 integer;
    l_csv_import_line_state  csv_import_line_state;
BEGIN

  l_status := COALESCE(p_csv_import_line_state.status, 'PROCESSING'::zcat_data.csv_import_line_status);

  SELECT cis_id INTO l_cis_id
    FROM zcat_commons.csv_import_state
   WHERE cis_uuid = p_csv_import_line_state.csv_import_state_uuid;

  IF l_cis_id IS NULL THEN
    RAISE EXCEPTION 'csv import state % not found on current shard', p_csv_import_line_state.csv_import_state_uuid;
  ELSE
    l_csv_import_line_state := csv_import_get_line_state_by_line_number_and_uuid(
                                  p_csv_import_line_state.line_number,
                                  p_csv_import_line_state.csv_import_state_uuid);
    IF l_csv_import_line_state.id IS NULL THEN
      INSERT
        INTO zcat_data.csv_import_line_state (
              cils_csv_import_state_id,
              cils_status,
              cils_failure_reason,
              cils_line_number
             )
      VALUES (
              l_cis_id,
              l_status,
              p_csv_import_line_state.failure_reason,
              p_csv_import_line_state.line_number
             );
    ELSE
      UPDATE zcat_data.csv_import_line_state
         SET cils_status              = l_status,
             cils_failure_reason      = p_csv_import_line_state.failure_reason,
             cils_last_modified       = now()
       WHERE cils_id = l_csv_import_line_state.id;
    END IF;
  END IF;

END
$BODY$
language plpgsql
    volatile security definer
    cost 100;