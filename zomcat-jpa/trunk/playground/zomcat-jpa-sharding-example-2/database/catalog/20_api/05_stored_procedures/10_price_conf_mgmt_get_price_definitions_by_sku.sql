create or replace function price_conf_mgmt_get_price_definitions_by_sku (
  p_sku           text,
  p_sku_type      zcat_data.sku_type,
  p_app_domain_id int,
  p_country_code  zz_commons.country_code,
  p_partner_id    int,
  p_price_level   price_level
) returns setof price_definition as
  $BODY$
/*
-- $Id$
-- $Id$
-- $HeadURL$
*/
/**
 * Retreives list of price definitions matching specified parameters.
 *
 * @ExpectedExecutionTime 10ms
 * @ExpectedExecutionFrequency EveryNewPriceDefinitionCreated
 */
/**  Test
  set search_path to zcat_api_r12_00_49, public;
  select price_conf_mgmt_get_price_definitions_by_sku('TI3-fzw-0006-99-0012', 'SIMPLE', 5, 'NL', null, ROW(1, 0, 'test', false)::price_level)
*/
begin
    return query
    select pd_id,
           as_sku,
           ROW (pl_id, pl_level, pl_name, pl_is_promotional, pl_is_layouted, pl_is_fallback)::price_level,
           pd_price,
           pd_appdomain_id,
           pd_country_code,
           date_trunc('second', pd_start_date),
           date_trunc('second', pd_end_date),
           pd_partner_id,
           ROW (plrc_id, plrc_value)::price_level_reason_code
      from zcat_data.price_definition
      join zcat_data.article_sku on as_id = pd_sku_id
      join zcat_commons.price_level on pl_id = pd_price_level_id
      LEFT JOIN zcat_data.price_level_reason_code ON plrc_id = pd_price_level_reason_code_id
     where as_sku = p_sku
       and as_sku_type = p_sku_type
       and pd_appdomain_id is not distinct from p_app_domain_id
       and pd_country_code = p_country_code
       and pd_partner_id is not distinct from p_partner_id
       and pd_price_level_id = p_price_level.id;
end
$BODY$

language plpgsql
volatile
security definer
cost 100;
