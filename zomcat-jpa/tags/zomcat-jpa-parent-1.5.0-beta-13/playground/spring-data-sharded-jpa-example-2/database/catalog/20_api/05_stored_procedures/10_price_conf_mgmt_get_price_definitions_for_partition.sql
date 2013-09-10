create or replace function price_conf_mgmt_get_price_definitions_for_partition  (
    p_sku       text,
    p_appdomain_id    integer,
    p_country_code    zz_commons.country_code,
    p_partner_id      integer,
    p_price_level_id  integer,
    p_active_from     timestamptz,
    p_active_to       timestamptz)
returns SETOF price_definition as
$BODY$
/*
-- $Id$
-- $HeadURL$
*/
/**
 *
 *
 * @ExpectedExecutionTime 20ms
 * @ExpectedExecutionFrequency EveryWebServiceRequest
 */
/* --testing

  begin;

  -- set search_path to zcat_api_r13_00_10, public;
  select * from price_conf_mgmt_get_price_defs_for_partition(
    'AI211C00M-7080360000',
    ARRAY[15, 6, 1],
    null,
    now()
  );

  rollback;

 */
begin
    perform 1 from zcat_data.article_sku where as_sku = p_sku limit 1;
    if not found then
        raise exception 'sku % not found.', p_sku;
    end if;

    return query
    select distinct pd_id,
           as_sku,
           ROW (pl_id, pl_level, pl_name, pl_is_promotional, pl_is_layouted, pl_is_fallback)::price_level,
           pd_price,
           pd_appdomain_id,
           pd_country_code,
           date_trunc('second', pd_start_date),
           date_trunc('second', pd_end_date),
           pd_partner_id,
           ROW (NULL, NULL)::price_level_reason_code
      from zcat_data.price_definition
      join zcat_data.article_sku on pd_sku_id = as_id
      join zcat_commons.price_level on pl_id = pd_price_level_id
     where as_sku = p_sku
       and ((not as_is_legacy) or (as_is_legacy and as_sku_type != 'MODEL'))
       and p_active_from <= date_trunc('second', pd_end_date)
       and p_active_to >= date_trunc('second', pd_start_date)
       and pd_country_code is not distinct from p_country_code
       and pd_appdomain_id is not distinct from p_appdomain_id
       and pd_partner_id is not distinct from p_partner_id
       and pd_price_level_id = p_price_level_id;
end
$BODY$

language plpgsql
    volatile
    security definer
    cost 100;
