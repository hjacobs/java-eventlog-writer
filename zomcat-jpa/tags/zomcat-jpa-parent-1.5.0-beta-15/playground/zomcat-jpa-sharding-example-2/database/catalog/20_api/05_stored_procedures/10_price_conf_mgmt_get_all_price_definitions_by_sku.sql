CREATE OR REPLACE FUNCTION price_conf_mgmt_get_all_price_definitions_by_sku (
  p_sku           text,
  p_active_on     timestamp with time zone
) RETURNS SETOF price_definition AS
  $BODY$
/*
-- $Id:
-- $HeadURL:
*/
/**
 * Retreives list of price definitions by sku.
 *
 * @ExpectedExecutionTime 10ms
 * @ExpectedExecutionFrequency EveryNewPriceDefinitionCreated
 */
/**  Test
 begin;
  --set search_path to zcat_api_r13_00_06, public;
  select price_conf_mgmt_get_all_price_definitions_by_sku('TI3-fzw-0006-99-0012', current_timestamp);
 rollback;
*/
BEGIN
    RETURN QUERY
    SELECT pd_id,
           s1.as_sku,
           ROW (pl_id, pl_level, pl_name, pl_is_promotional, pl_is_layouted, pl_is_fallback)::price_level,
           pd_price,
           pd_appdomain_id,
           pd_country_code,
           date_trunc('second', pd_start_date),
           date_trunc('second', pd_end_date),
           pd_partner_id,
           ROW (plrc_id, plrc_value)::price_level_reason_code
      FROM zcat_data.price_definition
      JOIN zcat_data.article_sku s1 ON s1.as_id = pd_sku_id
      JOIN zcat_data.article_sku s2 ON s1.as_id IN (s2.as_model_id, s2.as_config_id, s2.as_id)
      JOIN zcat_commons.price_level ON pl_id = pd_price_level_id
      LEFT JOIN zcat_data.price_level_reason_code ON plrc_id = pd_price_level_reason_code_id
     WHERE p_active_on >= date_trunc('second', pd_start_date)
       AND p_active_on < date_trunc('second', pd_end_date)
       AND s2.as_sku = p_sku;
END;
$BODY$
LANGUAGE plpgsql VOLATILE SECURITY DEFINER
COST 100;
