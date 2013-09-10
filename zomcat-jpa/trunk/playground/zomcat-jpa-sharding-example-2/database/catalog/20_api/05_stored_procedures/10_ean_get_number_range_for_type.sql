CREATE OR REPLACE FUNCTION ean_get_number_range_for_type(
  p_number_range_type zcat_commons.ean_number_range_type
)
returns SETOF ean_number_range as
/*
-- $Id$
-- $Id$
-- $HeadURL$
*/
/**
 * Get ean ranges of type or all if type is null.
 *
 * @ExpectedExecutionTime 10 ms
 * @ExpectedExecutionFrequency 10.000 per day
 */
/**  Test
  set search_path=zcat_api_r13_00_07,public;
  select * from ean_get_ean_number_ranges(null);
  select * from ean_get_ean_number_ranges('DUMMY');
*/
$BODY$
BEGIN
  RETURN QUERY
    SELECT enr_id,
           enr_name,
           enr_active,
           enr_ean_number_range_type,
           enr_start_ean::text,
           enr_end_ean::text,
           enr_increase_by,
           enr_last_ean::text
      FROM zcat_commons.ean_number_range
 WHERE enr_ean_number_range_type = p_number_range_type
       AND enr_active = true
       AND (
            enr_end_ean IS DISTINCT FROM enr_last_ean
         OR ((SELECT COUNT(ere_id) FROM zcat_commons.ean_released_ean WHERE ere_ean_number_range_id = enr_id) > 0)
           )
 LIMIT 1;    -- return only the one active and valid number range.
END
$BODY$
language plpgsql
    volatile security definer
    cost 100;
