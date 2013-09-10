CREATE OR REPLACE FUNCTION sku_get_used_and_possible_size_codes_for_simple_sku(
  p_config_sku text
)
returns setof text as
/*
-- $Id$
-- $Id$
-- $HeadURL$
*/
/**
 * Select all existing simple skus for the config and all size codes for the second dimension if available
 *
 * @ExpectedExecutionTime 10 ms
 * @ExpectedExecutionFrequency 10.000 per day
 */
/**  Test
  set search_path=zcat_api_r13_00_21,public;
  select * from sku_get_used_and_possible_size_codes_for_simple_sku('P4441B03X-404');
*/
$BODY$
DECLARE
BEGIN
  RETURN QUERY
       -- get all simple skus known for this config sku
       select as1.as_sku
         from zcat_data.article_sku as1
         join zcat_data.article_sku as2 on as1.as_config_id = as2.as_id
        where as1.as_sku_type = 'SIMPLE'
          and as2.as_sku = p_config_sku
          and as2.as_sku_type = 'CONFIG'

      union

       -- and select all possible sizes for the second dimension (if there are any in the article model)
      select s_code
        from zcat_data.article_sku
        join zcat_data.article_model on am_model_sku_id = as_model_id

        join zcat_commons.size_chart_group on scg_id = am_size_chart_group_id
        join zcat_commons.size_dimension_group_binding on sdgb_group_id = scg_dimension_group_id and sdgb_position = 2
        join zcat_commons.size_chart_group_binding on scgb_size_chart_group_id = scg_id and substr(scgb_size_chart_code, 9) = sdgb_code
        join zcat_commons.size on s_size_chart_code = scgb_size_chart_code
      where as_sku = p_config_sku
        and as_sku_type = 'CONFIG';
END;
$BODY$
language plpgsql
    volatile security definer
    cost 100;
