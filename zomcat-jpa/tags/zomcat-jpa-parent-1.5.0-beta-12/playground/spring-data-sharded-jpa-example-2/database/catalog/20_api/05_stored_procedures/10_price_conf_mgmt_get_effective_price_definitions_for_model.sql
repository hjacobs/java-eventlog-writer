create or replace function price_conf_mgmt_get_effective_price_definitions_for_model  (
    p_model_sku         text,
    p_is_target_lounge  boolean,
    p_partner_id        integer,
    p_active_on         timestamptz)
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
  select * from price_conf_mgmt_get_effective_price_definitions_for_model(
    'AI211C00M-7080360000',
    ARRAY[15, 6, 1],
    null,
    now()
  );

  rollback;

 */
begin

  return query
      select distinct pd_id as id,
             out_sku.as_sku as sku,
             ROW (pl_id, pl_level, pl_name, pl_is_promotional, pl_is_layouted, pl_is_fallback)::price_level as price_level,
             pd_price as price,
             pd_appdomain_id::smallint as appdomain_id,
             pd_country_code as country_code,
             date_trunc('second', pd_start_date) as start_date,
             date_trunc('second', pd_end_date) as end_date,
             pd_partner_id,
             ROW (NULL, NULL)::price_level_reason_code
        from zcat_data.price_definition
        join zz_commons.appdomain on (
               (ad_id is not distinct from pd_appdomain_id)
            or lower (pd_country_code::text) = lower (substring (ad_locale from 4 for 2)))
           and ad_is_lounge = p_is_target_lounge -- select lounge <-> or not lounge
        join zcat_commons.price_level on pl_id = pd_price_level_id

        -- select the whole model tree:
        join zcat_data.article_sku s1 on pd_sku_id = s1.as_id
        join zcat_data.article_sku s2 on
            (s1.as_model_id IN (s2.as_model_id, s2.as_config_id, s2.as_id) or  -- select the subtree
            (s2.as_model_id is null and s1.as_id=s2.as_id))                    -- select the model

        join zcat_data.article_sku as out_sku on pd_sku_id = out_sku.as_id
       where s2.as_sku = p_model_sku
         and s2.as_sku_type = 'MODEL'
         and p_active_on >= date_trunc('second', pd_start_date) and p_active_on < date_trunc('second', pd_end_date)
         and p_partner_id is not distinct from pd_partner_id;
end
$BODY$

language plpgsql
    volatile
    security definer
    cost 100;
