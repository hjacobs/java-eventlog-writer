CREATE OR REPLACE FUNCTION sizing_get_all_size_chart_mapping_configuration (
    p_brand_code_commodity_group_codes brand_code_commodity_group_code[],
    p_include_inactive boolean
) RETURNS size_chart_mapping_configuration as
$BODY$

-- $Id$
-- $HeadURL$
/**
 * Select size charts valid for the specified brand and commodity group. If commodity group is NULL, return
 * for all available commodity goups for the specified brand
 *
 * @ExpectedExecutionTime 15ms
 * @ExpectedExecutionFrequency with every request for sizeChartMapping
 */

/*
-- test

-- Stored Procedure simple test-case
   set search_path to zcat_api, public;

--
-- create test data
--
-- (no simple case yet, easiest would be to run some integrations tests that create the data)

--
-- run test
--
   select * from sizing_get_all_size_chart_mapping_configuration ((ARRAY[('BR1','100')])::brand_code_commodity_group_code[],false);
   select * from sizing_get_all_size_chart_mapping_configuration ((ARRAY[('BR1',null)])::brand_code_commodity_group_code[],false);
   rollback;
*/

    WITH all_size_chart_groups AS (
        SELECT bscg_brand_code, bscg_commodity_group_code, bscg_size_chart_group
          FROM zcat_commons.brand_size_chart_group
          JOIN unnest( $1 )
            ON bscg_brand_code = brand_code
           AND (    (commodity_group_code IS NOT NULL AND bscg_commodity_group_code = commodity_group_code)
                 OR (commodity_group_code IS NULL)
                 OR (bscg_commodity_group_code IS NULL)
               )
           AND ( $2 OR bscg_is_active)
    ), size_chart_codes_arrays AS (
        SELECT ARRAY
               (
                   SELECT DISTINCT sizing_get_size_chart_codes_for_chart_group_id (bscg_size_chart_group)
               ) AS size_chart_codes_row,
               bscg_brand_code,
               bscg_commodity_group_code
          FROM all_size_chart_groups
    ), size_charts AS (
        SELECT array_agg(DISTINCT ROW(size_chart_codes_row)::size_chart_code_list) AS size_chart_list_row,
               bscg_brand_code,
               bscg_commodity_group_code
          FROM size_chart_codes_arrays
         GROUP BY bscg_brand_code, bscg_commodity_group_code
    ), size_chart_mapping_brand_commodity_group_table AS (
        SELECT array_agg( ROW
               (
                   bscg_brand_code,
                   bscg_commodity_group_code,
                   size_chart_list_row
               )::size_chart_mapping_brand_commodity_group) AS result_field1
          FROM size_charts
    ), used_size_charts_table as (
       SELECT ARRAY
              (
                  SELECT DISTINCT sizing_get_used_size_charts_by_brand_commodity_groups(size_chart_codes_row)
                    FROM size_chart_codes_arrays
              ) as result_field2
    )
    SELECT ROW(result_field1, result_field2)::size_chart_mapping_configuration
      FROM size_chart_mapping_brand_commodity_group_table, used_size_charts_table

$BODY$
language sql
    volatile
    security definer
    cost 100;
