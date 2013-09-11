CREATE OR REPLACE FUNCTION sizing_get_size_chart_mapping_configuration (
    p_brand_code_commodity_group_codes brand_code_commodity_group_code[],
    p_include_inactive boolean
) RETURNS size_chart_mapping_configuration as
$BODY$

/*
    size_chart_mapping_brand_commodity_groups size_chart_mapping_brand_commodity_group[],
    used_size_charts                          size_chart[]-- $Id$
-- $HeadURL$
*/
DECLARE
    l_chart_mapping_brand_commodity_groups size_chart_mapping_brand_commodity_group[];
    l_used_size_charts_codes text[];
    l_used_size_charts size_chart[];
    l_size_chart_mapping_configuration size_chart_mapping_configuration;

   size_chart_mapping_brand_commodity_group size_chart_mapping_brand_commodity_group;
   size_chart_code_list size_chart_code_list;
   size_chart_code text;
BEGIN

    l_chart_mapping_brand_commodity_groups := ARRAY(
        SELECT sizing_get_chart_mapping_brand_commodity_groups(p_brand_code_commodity_group_codes, p_include_inactive)
    );

    IF array_length(l_chart_mapping_brand_commodity_groups, 1) > 0 THEN
       FOREACH size_chart_mapping_brand_commodity_group IN ARRAY l_chart_mapping_brand_commodity_groups
       LOOP
           FOREACH size_chart_code_list IN ARRAY size_chart_mapping_brand_commodity_group.size_chart_code_lists
           LOOP
              FOREACH size_chart_code IN ARRAY size_chart_code_list.size_chart_codes
                LOOP
                  l_used_size_charts_codes := l_used_size_charts_codes || size_chart_code;
              END LOOP;
           END LOOP;
       END LOOP;

       l_used_size_charts := ARRAY(SELECT sizing_get_used_size_charts_by_brand_commodity_groups(l_used_size_charts_codes));

    END IF;

    l_size_chart_mapping_configuration.size_chart_mapping_brand_commodity_groups := l_chart_mapping_brand_commodity_groups;
    l_size_chart_mapping_configuration.used_size_charts := l_used_size_charts;

    RETURN l_size_chart_mapping_configuration;
END
$BODY$
language plpgsql
    volatile
    security definer
    cost 100;
