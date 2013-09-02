CREATE OR REPLACE FUNCTION csv_import_create_or_update_line_states(
  p_csv_import_line_states csv_import_line_state[]
)
RETURNS void AS
$BODY$
/*
  -- $Id$
  -- $HeadURL$
*/
/* -- test

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


select * from csv_import_create_or_update_line_states(ARRAY [
            ROW (null, '795c6452-9e2b-41c6-9cca-e36962a330f8', 'COMMITTED'::zcat_data.csv_import_line_status, null, 3, null)::csv_import_line_state,
            ROW (null, '795c6452-9e2b-41c6-9cca-e36962a330f8', 'FAILED'::zcat_data.csv_import_line_status,   'foo', 2, null)::csv_import_line_state,
            ROW (null, '795c6452-9e2b-41c6-9cca-e36962a330f8', 'COMMITTED'::zcat_data.csv_import_line_status, null, 1, null)::csv_import_line_state
            ]::csv_import_line_state[]);

select * from zcat_data.csv_import_line_state
join zcat_commons.csv_import_state on cis_id = cils_csv_import_state_id
where cis_uuid = '795c6452-9e2b-41c6-9cca-e36962a330f8';

--commit;
rollback;

 */
BEGIN

  with line_state_data as (
    select cis_id as import_id,
        t.csv_import_state_uuid,
        coalesce (t.status, 'PROCESSING'::zcat_data.csv_import_line_status) as status,
        t.failure_reason,
        t.line_number,
        t.last_modified,
        cils_id as line_id
    from unnest (p_csv_import_line_states) t
        join zcat_commons.csv_import_state on t.csv_import_state_uuid = cis_uuid
        left join zcat_data.csv_import_line_state on cis_id = cils_csv_import_state_id
            and cils_line_number = t.line_number
    ),
    insert_new_lines as (
        insert into zcat_data.csv_import_line_state (
            cils_csv_import_state_id,
            cils_status,
            cils_failure_reason,
            cils_line_number)
        select lsd.import_id,
            lsd.status,
            lsd.failure_reason,
            lsd.line_number
        from line_state_data lsd
        where lsd.line_id is null
    )

    UPDATE zcat_data.csv_import_line_state
    SET cils_status              = lsd.status,
        cils_failure_reason      = lsd.failure_reason,
        cils_last_modified       = now()
    from line_state_data lsd
    WHERE cils_id = lsd.line_id;

END
$BODY$
language plpgsql
    volatile security definer
    cost 100;