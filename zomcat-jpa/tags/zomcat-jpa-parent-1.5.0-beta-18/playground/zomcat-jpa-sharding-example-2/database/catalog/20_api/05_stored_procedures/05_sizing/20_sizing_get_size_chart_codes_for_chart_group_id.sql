CREATE OR REPLACE FUNCTION sizing_get_size_chart_codes_for_chart_group_id (
  p_size_chart_group_id int
) RETURNS setof text as
$$
/*
-- $Id$
-- $HeadURL$
*/

  SELECT sc_code
    FROM zcat_commons.size_chart_group
    JOIN zcat_commons.size_dimension_group_binding ON sdgb_group_id = scg_dimension_group_id
    JOIN zcat_commons.size_chart_group_binding ON scgb_size_chart_group_id = scg_id
                                              AND substring(scgb_size_chart_code FROM 9) = sdgb_code
    JOIN zcat_commons.size_chart ON sc_code = scgb_size_chart_code
   WHERE scg_id = $1
   ORDER BY sdgb_position;

$$
LANGUAGE SQL volatile security definer
cost 100;