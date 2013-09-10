CREATE OR REPLACE FUNCTION article_facet_update_article_config(
  p_article_model_sku text,
  p_article_config    article_config,
  p_scope             flow_scope
) RETURNS void AS
$BODY$
/*
  $Id$
  $HeadURL$
*/
DECLARE
  l_article_config_id     int;
  l_config_sku_id         int;
BEGIN
  RAISE INFO 'called article_facet_update_article_config with % database = %', p_article_config, current_database();

  -- determine sku id
  SELECT article_get_sku_id(p_article_config.config_sku, 'CONFIG')
    INTO l_config_sku_id;


  -- update any existing sku counter to the new color, material and
  UPDATE zcat_data.sku_config_counter
     SET scc_color_family = c_family_code,
         scc_color_1      = p_article_config.main_color_code,
         scc_color_2      = COALESCE(p_article_config.second_color_code, 'N/A'),
         scc_color_3      = COALESCE(p_article_config.third_color_code, 'N/A'),
         scc_material     = COALESCE(p_article_config.main_material_code, 'N/A'),
         scc_pattern_code = COALESCE((p_article_config.pattern_type_code).code, 'N/A')
    FROM zcat_data.article_config
    JOIN zcat_commons.color on c_code = ac_main_color_code
    LEFT JOIN zcat_option_value.pattern on ov_id = ac_pattern_id
   WHERE ac_config_sku_id   = l_config_sku_id
     AND scc_model_sku      = p_article_model_sku
     AND scc_color_family   IS NOT DISTINCT FROM c_family_code
     AND scc_color_1        IS NOT DISTINCT FROM ac_main_color_code
     AND scc_color_2        IS NOT DISTINCT FROM COALESCE(ac_second_color_code, 'N/A')
     AND scc_color_3        IS NOT DISTINCT FROM COALESCE(ac_third_color_code, 'N/A')
     AND scc_pattern_code   IS NOT DISTINCT FROM COALESCE(ov_code, 'N/A');

  UPDATE zcat_data.article_config
     SET
        ac_last_modified                = now(),
        ac_last_modified_by             = p_scope.user_id,
        ac_flow_id                      = p_scope.flow_id,
        ac_version                      = p_article_config.version,

        ac_first_season_code            = p_article_config.first_season_code,
        ac_season_code                  = p_article_config.season_code,
        ac_sub_season_id                = (select master_data_get_option_value_type_code_id(p_article_config.sub_season_type_code)),

        ac_main_color_code              = p_article_config.main_color_code,
        ac_second_color_code            = p_article_config.second_color_code,
        ac_third_color_code             = p_article_config.third_color_code,
        ac_main_material_code           = p_article_config.main_material_code,

        ac_is_reloaded_article          = p_article_config.reloaded_article,
        ac_is_keystyle                  = p_article_config.keystyle,
        ac_keystyle_delivery_date       = p_article_config.keystyle_delivery_date,
        ac_is_disposition_locked        = p_article_config.disposition_locked,
        ac_main_supplier_code           = p_article_config.main_supplier_code,
        ac_pattern_id                   = (select master_data_get_option_value_type_code_id(p_article_config.pattern_type_code)),
        ac_is_globally_rebateable       = p_article_config.globally_rebateable,
        ac_is_risk_article              = p_article_config.risk_article,
        ac_is_commission_article        = p_article_config.commission_article,
        ac_is_key_value_item            = COALESCE(p_article_config.key_value_item, FALSE)
  WHERE ac_config_sku_id = l_config_sku_id;

  PERFORM article_facet_update_sales_channels_release(l_config_sku_id,
                                                      p_article_config.sales_channels_release,
                                                      p_scope);

END;
$BODY$
LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
COST 100;
