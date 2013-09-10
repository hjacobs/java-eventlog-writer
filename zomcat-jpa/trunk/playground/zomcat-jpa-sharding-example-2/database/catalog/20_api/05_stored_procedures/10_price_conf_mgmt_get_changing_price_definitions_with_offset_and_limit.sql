create or replace function price_conf_mgmt_get_changing_price_definitions_with_offset_and_limit  (
    p_start_date     timestamptz,
    p_end_date       timestamptz,
    p_skus           text[],
    p_app_domain_ids int[],
    p_offset         int,
    p_limit          int)
returns SETOF price_definition as
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

    -- set search_path to zcat_api_r12_00_43, public;
    select price_conf_mgmt_get_changing_price_definitions_with_offset_and_limit('2012-06-07 15:00:00+01', '2012-06-07 16:00:00+01', null, null, 0, 100);

  rollback;
*/
begin

    p_start_date := date_trunc('second', p_start_date);
    p_end_date := date_trunc('second', p_end_date);

    return query
    with
    app_domain_tmp as (
        select distinct unnest (ad_id || pfa_fallback_sequence || p_app_domain_ids :: smallint[]) as adt_id
          from zz_commons.appdomain
          left join zcat_commons.price_fallback_appdomains on ad_id = pfa_appdomain_id
         where coalesce (array_upper(p_app_domain_ids :: smallint[], 1) = 0, true) or pfa_appdomain_id = any(p_app_domain_ids :: smallint[])
    ),
    article_tmp as (
        select as_id as at_id, as_sku as at_sku
          from zcat_data.article_sku
         where coalesce (array_upper(p_skus, 1) = 0, true) or as_sku = any(p_skus)
    )
    select pd_id,
           at_sku,
           ROW (pl_id, pl_level, pl_name, pl_is_promotional, pl_is_layouted, pl_is_fallback)::price_level as price_level,
           pd_price,
           pd_appdomain_id,
           pd_country_code,
           date_trunc('second', pd_start_date),
           date_trunc('second', pd_end_date),
           pd_partner_id,
           ROW (NULL, NULL)::price_level_reason_code
      from zcat_data.price_definition
      join zcat_commons.price_level on pl_id = pd_price_level_id
      join app_domain_tmp a on adt_id = pd_appdomain_id
      join article_tmp on at_id = pd_sku_id
     where ((date_trunc('second', pd_start_date) >= p_start_date and date_trunc('second', pd_start_date) < p_end_date)
        or (date_trunc('second', pd_end_date) >= p_start_date and date_trunc('second', pd_end_date) < p_end_date))
     limit p_limit
    offset p_offset;
end;
$BODY$

language plpgsql
    volatile
    security definer
    cost 100;
