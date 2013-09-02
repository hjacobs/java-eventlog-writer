CREATE OR REPLACE FUNCTION sizing_get_charts_by_group (
  p_chart_group_id  int,
  p_with_sizes boolean default true
) RETURNS setof size_chart as
$$
/*
-- $Id$
-- $HeadURL$
*/

  SELECT sizing_get_size_chart(scgb_size_chart_code, $2)
    FROM zcat_commons.size_chart_group
    JOIN zcat_commons.size_dimension_group_binding ON sdgb_group_id = scg_dimension_group_id
    JOIN zcat_commons.size_chart_group_binding ON scgb_size_chart_group_id = scg_id
                                              AND substring(scgb_size_chart_code FROM 9) = sdgb_code
   WHERE scg_id = $1
   ORDER BY sdgb_position;

$$
LANGUAGE SQL stable security definer
cost 100;