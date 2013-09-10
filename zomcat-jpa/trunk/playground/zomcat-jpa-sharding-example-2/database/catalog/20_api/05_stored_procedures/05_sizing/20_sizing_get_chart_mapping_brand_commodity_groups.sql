CREATE OR REPLACE FUNCTION sizing_get_chart_mapping_brand_commodity_groups (
    p_brand_code_commodity_group_codes brand_code_commodity_group_code[],
    p_include_inactive boolean
) RETURNS setof size_chart_mapping_brand_commodity_group as
$BODY$

/*
 *
CREATE TYPE size_chart_mapping_brand_commodity_group AS (
  brand_code            text,
  commodity_group_code  text,
  size_chart_code_lists size_chart_code_list[]
);
-- $Id$
-- $HeadURL$
*/
BEGIN
    RETURN QUERY

    SELECT
        brand_code,
        commodity_group_code,
        ARRAY(
            SELECT sizing_get_size_chart_code_lists(brand_code, commodity_group_code, p_include_inactive)
        )
    FROM unnest(p_brand_code_commodity_group_codes);
END
$BODY$
language plpgsql
    volatile
    security definer
    cost 100;
