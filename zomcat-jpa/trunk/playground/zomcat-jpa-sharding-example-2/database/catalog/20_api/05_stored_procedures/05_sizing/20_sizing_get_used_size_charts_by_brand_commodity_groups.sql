CREATE OR REPLACE FUNCTION sizing_get_used_size_charts_by_brand_commodity_groups (
    p_size_chart_codes text[]
) RETURNS setof size_chart as
$BODY$

/*
 *
 * TO BE REMOVED
-- $Id$
-- $HeadURL$
*/
BEGIN
    RETURN QUERY
  SELECT sc_code::text,
         sc_description_message_key,
         ARRAY(
            select row(
                  row(s_code, s_size_chart_code)::size_code,
                  s_supplier_size,
                  s_sort_key,
                  s_value
                )::size
            from zcat_commons.size
           where s_size_chart_code = sc_code
        ) FROM zcat_commons.size_chart
        WHERE sc_code = any(p_size_chart_codes);
END
$BODY$
language plpgsql
    volatile
    security definer
    cost 100;
