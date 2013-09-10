create or replace function price_conf_mgmt_get_price_definitions (
  p_offset int,
  p_limit  int
) returns setof price_definition as
  $BODY$
/*
-- $Id$
-- $Id$
-- $HeadURL$
*/
/**
 * Selects all price definitions.
 *
 *
 * @ExpectedExecutionTime depends on p_limit parameter
 * @ExpectedExecutionFrequency EveryWsRequest
 */
/**  Test
  set search_path=zcat_api_r12_00_40,public;
  select * from price_conf_mgmt_get_price_definitions(0, 10);
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
    limit p_limit
    offset p_offset;
end
$BODY$

language plpgsql
volatile
security definer
cost 100;
