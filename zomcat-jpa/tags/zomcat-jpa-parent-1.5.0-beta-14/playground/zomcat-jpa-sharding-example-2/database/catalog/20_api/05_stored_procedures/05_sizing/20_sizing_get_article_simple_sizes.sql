CREATE OR REPLACE FUNCTION sizing_get_article_simple_sizes (
  p_article_simple_sku_id  int
) RETURNS setof size as
$$
/*
-- $Id$
-- $HeadURL$
*/
  WITH group_bindings AS (
    select sdgb_code, sdgb_position
      from zcat_commons.size_dimension_group_binding
     where sdgb_group_id
        in (
             select sizing_get_dimension_group_id(array_agg(substr(s_size_chart_code,9)), true)
               from zcat_data.article_simple_size
               join zcat_commons.size
                 on s_size_chart_code = ass_size_chart_code
                and s_code = ass_size_code
              where ass_article_simple_sku_id = $1
           )
  )
    SELECT ROW(s_code,s_size_chart_code)::size_code,
           s_supplier_size,
           s_sort_key,
           s_value
      FROM zcat_data.article_simple_size
      JOIN zcat_commons.size
        ON s_size_chart_code = ass_size_chart_code
       AND s_code = ass_size_code
      JOIN zcat_commons.size_chart
        ON sc_code = s_size_chart_code
      JOIN group_bindings
        ON sdgb_code = sc_dimension_code
     WHERE ass_article_simple_sku_id = $1
     ORDER
        BY sdgb_position;
$$
LANGUAGE SQL STABLE security definer
cost 100;
