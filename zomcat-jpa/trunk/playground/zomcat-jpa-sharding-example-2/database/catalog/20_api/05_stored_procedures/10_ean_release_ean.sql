CREATE OR REPLACE FUNCTION ean_release_ean(
  p_number_range_type zcat_commons.ean_number_range_type,
  p_ean13             EAN13,
  p_scope             flow_scope
)
returns void as
/*
-- $Id$
-- $Id$
-- $HeadURL$
*/
/**
 * Releases a previous generated ean. The ean will be placed into the zcat_commons.ean_released_ean
 * table. This table will be requested first if new eans for the number range are created.
 *
 * @ExpectedExecutionTime 10 ms
 * @ExpectedExecutionFrequency 100 per day
 */
/**  Test
  set search_path=zcat_api_r13_00_07,public;
  select * from ean_release_ean('ZALANDO', '0000000000000', 'user', 'flowId');
*/
$BODY$
DECLARE
    l_number_range_id integer;
    l_end_ean  EAN13;
BEGIN

    -- first get an active number range:
    SELECT enr_id into l_number_range_id
      FROM zcat_commons.ean_number_range
     WHERE enr_ean_number_range_type = p_number_range_type
       AND enr_active = true
     LIMIT 1;    -- return only the one active and valid number range.

    IF NOT FOUND THEN
       raise 'could not find active number_range for tpye %', p_number_range_type;
    END IF;

   -- now insert the released ean into the zcat_commons.ean_released_ean table:
    INSERT INTO zcat_commons.ean_released_ean
       (ere_created_by , ere_last_modified_by, ere_flow_id    , ere_ean_number_range_id, ere_released_ean) VALUES
       (p_scope.user_id, p_scope.user_id     , p_scope.flow_id, l_number_range_id      , p_ean13);
END;
$BODY$
language plpgsql
    volatile security definer
    cost 100;
