CREATE OR REPLACE FUNCTION article_facet_create_article_config(
    p_model_sku                    text,
    p_article_config               article_config,
    p_accounting_config            accounting_config,
    p_logistics_config             logistics_config,
    p_production_config            production_config,
    p_sales_config                 sales_config,
    p_supplier_container_config    supplier_container_config,
    p_multimedia_config            multimedia_config,
    p_scope                        flow_scope
) RETURNS int AS
$BODY$
/*
  $Id$
  $HeadURL$
*/
DECLARE
    l_article_config_id     int;
    l_model_sku_id          int;
    l_config_sku_id         int;
    l_supplier_config       supplier_config;
    l_multimedia_config     multimedia_config;
BEGIN

    IF position(p_model_sku in p_article_config.config_sku) != 1 THEN
        RAISE EXCEPTION 'Config sku: % is not based on model sku: %', p_article_config.config_sku, p_model_sku;
    END IF;

    -- determine model sku id
    SELECT article_get_sku_id(p_model_sku, 'MODEL') INTO l_model_sku_id;

    -- create the new config sku:
    SELECT article_create_sku(p_article_config.config_sku, l_model_sku_id, null, 'CONFIG', p_scope)
      INTO l_config_sku_id;

    INSERT INTO zcat_data.article_config
                (ac_config_sku_id,

                 ac_created_by,
                 ac_last_modified_by,
                 ac_flow_id,

                 ac_first_season_code,
                 ac_season_code,
                 ac_sub_season_id,

                 ac_main_color_code,
                 ac_second_color_code,
                 ac_third_color_code,
                 ac_main_material_code,

                 ac_is_reloaded_article,
                 ac_is_keystyle,
                 ac_keystyle_delivery_date,
                 ac_is_disposition_locked,
                 ac_main_supplier_code,
                 ac_pattern_id,
                 ac_is_globally_rebateable,
                 ac_is_risk_article,
                 ac_is_commission_article,
                 ac_is_key_value_item)
         VALUES (l_config_sku_id,

                 p_scope.user_id,
                 p_scope.user_id,
                 p_scope.flow_id,

                 p_article_config.first_season_code,
                 p_article_config.season_code,
                 (select master_data_get_option_value_type_code_id(p_article_config.sub_season_type_code)),

                 p_article_config.main_color_code,
                 p_article_config.second_color_code,
                 p_article_config.third_color_code,
                 p_article_config.main_material_code,

                 p_article_config.reloaded_article,
                 p_article_config.keystyle,
                 p_article_config.keystyle_delivery_date,
                 p_article_config.disposition_locked,
                 p_article_config.main_supplier_code,
                 (select master_data_get_option_value_type_code_id(p_article_config.pattern_type_code)),
                 p_article_config.globally_rebateable,
                 p_article_config.risk_article,
                 p_article_config.commission_article,
                 COALESCE(p_article_config.key_value_item, FALSE));

    PERFORM article_facet_update_sales_channels_release(l_config_sku_id,
                                                        p_article_config.sales_channels_release,
                                                        p_scope);

      -- accounting facets
      IF p_accounting_config.config_sku IS NOT NULL THEN
          PERFORM article_facet_accounting_create_or_update_config(p_accounting_config, p_scope);
      END IF;

      -- logistics facets
      IF p_logistics_config.config_sku IS NOT NULL THEN
          PERFORM article_facet_logistics_create_or_update_config(p_logistics_config, p_scope);
      END IF;

      -- production facets
      IF p_production_config.config_sku IS NOT NULL THEN
          PERFORM article_facet_production_create_or_update_config(p_production_config, p_scope);
      END IF;

      -- sales facets
      IF p_sales_config.config_sku IS NOT NULL THEN
          PERFORM article_facet_sales_create_or_update_config(p_sales_config, p_scope);
      END IF;

      -- supplier container facets
      IF p_supplier_container_config.suppliers IS NOT NULL THEN
          FOREACH l_supplier_config IN ARRAY (p_supplier_container_config.suppliers) LOOP
              PERFORM article_facet_supplier_create_or_update_config(l_supplier_config, p_scope);
          END LOOP;
      END IF;

    -- multimedia facets
    IF p_multimedia_config.config_sku IS NOT NULL THEN
        PERFORM article_facet_multimedia_create_or_update((null, p_multimedia_config, null)::multimedia_facet_wrapper, p_scope);
    END IF;

    RETURN l_config_sku_id;
END;
$BODY$
LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
COST 100;
