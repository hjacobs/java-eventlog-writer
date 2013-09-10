create or replace function price_conf_mgmt_get_effective_price_definitions  (
    p_simple_sku        text,
    p_app_domain_ids    integer[],
    p_partner_id        integer,
    p_active_on         timestamptz,
    p_use_base_fallback boolean)
returns SETOF price_definition as
$BODY$
/*
-- $Id$
-- $HeadURL$
*/
/**
 * Selects first two price definitions ordered by rank in app_domain_ids parameter.
 *
 * app_domain_ids is most of the time app_domain_id + fallback_ids
 * partner_id can be null if querying for Zalando/Lounge.
 *
 * @ExpectedExecutionTime 20ms
 * @ExpectedExecutionFrequency EveryWebServiceRequest
 */
/* --testing

  begin;

  -- set search_path to zcat_api_r12_00_39, public;
  select * from price_conf_mgmt_get_effective_price_definitions(
    'AI211C00M-7080360000',
    ARRAY[15, 6, 1],
    null,
    now(),
    true
  );

  rollback;

 */
begin

  return query
      select distinct pd_id as id,
             out_sku.as_sku as sku,
             ROW (pl_id, pl_level, pl_name, pl_is_promotional, pl_is_layouted, pl_is_fallback)::price_level as price_level,
             pd_price as price,
             pd_appdomain_id as appdomain_id,
             pd_country_code as country_code,
             date_trunc('second', pd_start_date) as start_date,
             date_trunc('second', pd_end_date) as end_date,
             pd_partner_id,
             ROW (plrc_id, plrc_value)::price_level_reason_code as price_level_reason_code
        from zcat_data.price_definition
        join zz_commons.appdomain on (
                -- if we have a fallback sequence and this appdomain is on the list
                (array_upper (p_app_domain_ids, 1) > 1 and ad_id = ANY (p_app_domain_ids))
                -- then either the price definition is in this appdomain
                and ((ad_id is not distinct from pd_appdomain_id)
                    -- or we are on the same country, but this is valid only if both are either lounge or not lounge
                    or (coalesce ((select ad_is_lounge from zz_commons.appdomain where ad_id = p_app_domain_ids[1]), false) = coalesce ((select ad_is_lounge from zz_commons.appdomain where ad_id = pd_appdomain_id), false)
                        and lower (pd_country_code::text) = lower (substring (ad_locale from 4 for 2))
                        -- but this does not count if the price_defintion is on offline outlet.
                        and pd_appdomain_id is distinct from 22
                        )
                    ))
             -- otherwise, if the fallback sequence is empty (array_upper (p_app_domain_ids, 1) = 1, implicitly understood as having no fallback), then the price_definition itself must be on this appdomain.
             or pd_appdomain_id = p_app_domain_ids[1]
        join zcat_commons.price_level on pl_id = pd_price_level_id
        join zcat_data.article_sku on pd_sku_id IN (as_id, as_config_id, as_model_id) and as_sku = p_simple_sku and as_sku_type = 'SIMPLE'
        join zcat_data.article_sku as out_sku on pd_sku_id = out_sku.as_id
        left join zcat_data.price_level_reason_code on plrc_id = pd_price_level_reason_code_id
       where p_active_on >= date_trunc('second', pd_start_date) and p_active_on < date_trunc('second', pd_end_date)
         and p_partner_id is not distinct from pd_partner_id

       union

       /*
        * ZEOS-9327 - adding here price definitions with base_fallback price level that is used as as last resort fallback
        *             variable p_use_base_fallback should be set to true (feature is enabled and not looking for partner price, appDomain is not lounge or offline outlet)
        */
      select distinct pd_id as id,
                      out_sku.as_sku as sku,
                      ROW (pl_id, pl_level, pl_name, pl_is_promotional, pl_is_layouted, pl_is_fallback)::price_level as price_level,
                      pd_price as price,
                      pd_appdomain_id as appdomain_id,
                      pd_country_code as country_code,
                      date_trunc('second', pd_start_date) as start_date,
                      date_trunc('second', pd_end_date) as end_date,
                      pd_partner_id,
                      ROW (plrc_id, plrc_value)::price_level_reason_code as price_level_reason_code
        from zcat_data.price_definition
        join zcat_commons.price_level on pl_id = pd_price_level_id and pl_name = 'base_fallback'
        join zcat_data.article_sku on pd_sku_id IN (as_id, as_config_id, as_model_id) and as_sku = p_simple_sku and as_sku_type = 'SIMPLE'
        join zcat_data.article_sku as out_sku on pd_sku_id = out_sku.as_id
        left join zcat_data.price_level_reason_code on plrc_id = pd_price_level_reason_code_id
       where p_use_base_fallback = true;

end
$BODY$

language plpgsql
    volatile
    security definer
    cost 100;
