create or replace function price_conf_mgmt_get_changing_price_definitions (
    p_start_date     timestamptz,
    p_end_date       timestamptz,
    p_skus           text[],
    p_app_domain_ids int[])
returns SETOF changing_price_definition as
$BODY$
/*
-- $Id$
-- $HeadURL$
*/
/**
 * Selects price definitions for specific skus where start_date or end_date is between p_start_date and p_end_date.
 *
 * p_skus - null or empty array is resolved to all skus
 * p_app_domain_ids - null or empty array is resolved to all app_domain_ids
 *
 * @ExpectedExecutionTime 1s
 * @ExpectedExecutionFrequency EveryMinute
 */
/* --testing

  begin;

    -- set search_path to zcat_api, public;
    select price_conf_mgmt_get_changing_price_definitions('2013-01-01 15:00:00+01', '2013-12-31 16:00:00+01', null, null);

  rollback;
*/
begin

    p_start_date := date_trunc('second', p_start_date);
    p_end_date := date_trunc('second', p_end_date);

    return query
    select pd_id,
           as_sku,
           ROW (pl_id, pl_level, pl_name, pl_is_promotional, pl_is_layouted, pl_is_fallback)::price_level as price_level,
           pd_price,
           pd_appdomain_id,
           pd_country_code,
           date_trunc('second', pd_start_date),
           date_trunc('second', pd_end_date),
           pd_partner_id,
           ROW (NULL, NULL)::price_level_reason_code as price_level_reason_code,
           COALESCE(pdai_is_high_priority, false)
      from zcat_data.price_definition
      join zcat_commons.price_level on pl_id = pd_price_level_id
      join zcat_data.article_sku on as_id = pd_sku_id
      left join zcat_data.price_definition_additional_info on pdai_price_definition_id = pd_id
     where (
             (date_trunc('second', pd_start_date) >= p_start_date and date_trunc('second', pd_start_date) < p_end_date)
             or
             (date_trunc('second', pd_end_date) >= p_start_date and date_trunc('second', pd_end_date) < p_end_date)
           )
       and (
             p_skus is null
             or
             as_sku = any(p_skus)
           )
       and (
             p_app_domain_ids is null
             or
             pd_appdomain_id = any(p_app_domain_ids)
           );
end;
$BODY$

language plpgsql
    volatile
    security definer
    cost 100;
