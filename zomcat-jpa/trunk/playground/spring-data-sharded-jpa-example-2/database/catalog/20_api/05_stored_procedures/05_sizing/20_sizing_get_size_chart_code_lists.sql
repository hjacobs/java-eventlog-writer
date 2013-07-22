CREATE OR REPLACE FUNCTION sizing_get_size_chart_code_lists (
    p_brand_code text,
    p_commodity_group_code text,
    p_include_inactive boolean
) RETURNS setof size_chart_code_list as
$BODY$

/*
 *
CREATE TYPE size_chart_code_list AS (
  size_chart_codes text[]
);

-- $Id$
-- $HeadURL$
*/
BEGIN
    RETURN QUERY
     SELECT ARRAY ( SELECT sizing_get_size_chart_codes_for_chart_group_id (bscg_size_chart_group) )
       FROM zcat_commons.brand_size_chart_group
      WHERE bscg_brand_code = p_brand_code
        AND (    (p_commodity_group_code IS NULL AND bscg_commodity_group_code IS NULL)
              OR (p_commodity_group_code IS NOT NULL AND bscg_commodity_group_code = p_commodity_group_code)
            )
        AND (p_include_inactive OR bscg_is_active);
END
$BODY$
language plpgsql
    volatile
    security definer
    cost 100;
