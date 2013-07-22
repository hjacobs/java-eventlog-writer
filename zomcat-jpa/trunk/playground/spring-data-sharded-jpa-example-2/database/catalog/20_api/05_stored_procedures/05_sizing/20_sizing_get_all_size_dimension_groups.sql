CREATE OR REPLACE FUNCTION sizing_get_all_size_dimension_groups ()
RETURNS setof size_dimension_group as
$$
/*
-- $Id$
-- $HeadURL$
*/

  SELECT selector, array_agg(ROW(sdgb_code, sdgb_prefix_id, sdgb_position)::size_dimension_group_binding order by sdgb_position)
    FROM (
            select sdgb_group_id group_id, array_agg(sdgb_code order by sdgb_position) selector
              from zcat_commons.size_dimension_group_binding
             group
                by sdgb_group_id
         ) sq
    JOIN zcat_commons.size_dimension_group_binding
      ON sdgb_group_id = group_id
   GROUP
      BY group_id, selector;
$$
LANGUAGE SQL volatile security definer
cost 100;