create or replace function price_conf_mgmt_get_price_definition (
    p_id        bigint
) returns setof price_definition as
$BODY$
/*
-- $Id$
-- $HeadURL$
*/
/** -- test
show search_path
select * from price_conf_mgmt_get_price_definition (5)

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
    where pd_id = p_id;

end
$BODY$

language plpgsql
  volatile
  security definer
  cost 100;
