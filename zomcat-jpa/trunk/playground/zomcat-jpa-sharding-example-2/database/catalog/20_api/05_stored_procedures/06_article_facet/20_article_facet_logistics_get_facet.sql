create or replace function article_facet_logistics_get_facet(
    p_model_sku text,
    p_config_sku text,
    p_simple_sku text,
    p_query facet_query
) RETURNS SETOF logistics_model AS
$BODY$
DECLARE
    l_model_sku_id   integer := null;
    l_config_sku_id  integer := null;
    l_simple_sku_id  integer := null;
BEGIN

    SELECT as_id INTO l_model_sku_id
      FROM zcat_data.article_sku
     WHERE as_sku = p_model_sku AND as_sku_type = 'MODEL';

    IF l_model_sku_id IS NULL THEN
        RAISE EXCEPTION 'sku % not found', p_model_sku USING ERRCODE = 'Z0001';
    END IF;

    IF p_config_sku IS NOT NULL THEN

        SELECT config_sku.as_id
          INTO l_config_sku_id
          FROM zcat_data.article_sku config_sku
          JOIN zcat_data.article_sku model_sku ON config_sku.as_model_id = model_sku.as_id
                                                  AND model_sku.as_sku_type = 'MODEL'
                                                  AND model_sku.as_sku = p_model_sku
         WHERE config_sku.as_sku = p_config_sku AND config_sku.as_sku_type = 'CONFIG';

        IF NOT FOUND THEN
            RAISE EXCEPTION 'config sku % not found for model sku %', p_config_sku, p_model_sku USING ERRCODE = 'Z0001';
        END IF;

        IF p_simple_sku IS NOT NULL THEN

            SELECT simple_sku.as_id
              INTO l_simple_sku_id
              FROM zcat_data.article_sku simple_sku
              JOIN zcat_data.article_sku config_sku ON simple_sku.as_config_id = config_sku.as_id
                                                       AND config_sku.as_sku_type = 'CONFIG'
                                                       AND config_sku.as_sku = p_config_sku
             WHERE simple_sku.as_sku = p_simple_sku AND simple_sku.as_sku_type = 'SIMPLE';

            IF NOT FOUND THEN
                RAISE EXCEPTION 'simple sku % not found for config sku %', p_simple_sku, p_config_sku USING ERRCODE = 'Z0001';
            END IF;

        END IF;

    END IF;

    RETURN QUERY
    SELECT
        aflm_version,
        as_sku,
        aflm_country_of_origin,
        CASE WHEN bootleg_type.ov_code IS NULL THEN NULL::option_value_type_code ELSE
        ROW(
          'BOOTLEG_TYPE',
          bootleg_type.ov_code
        )::option_value_type_code END, -- (SELECT master_data_get_option_value_type_code_by_id(aflm_bootleg_type_id)),
        aflm_customs_code,
        aflm_alcohol_strength,
        ARRAY (
            SELECT (aflc_version,
                    config_sku.as_sku,
                    aflc_country_of_origin,
                    aflc_customs_code,
                    aflc_net_weight,
                    aflc_gross_weight,
                    aflc_net_volume,
                    aflc_gross_volume,
                    CASE WHEN shipping_placement.ov_code IS NULL THEN NULL::option_value_type_code ELSE
                    ROW(
                      'SHIPPING_PLACEMENT',
                      shipping_placement.ov_code
                    )::option_value_type_code END, -- (SELECT master_data_get_option_value_type_code_by_id(aflc_shipping_placement_id)),
                    aflc_is_cage_product,
                    aflc_is_fragile,
                    aflc_is_oversized_package,
                    aflc_is_extra_heavy,
                    aflc_is_hazardous,
                    aflc_is_not_individually_packed,
                    aflc_has_no_packaging,
                    aflc_has_unsafe_packaging,
                    aflc_is_ce_certified,
                    aflc_article_set_size,
                    aflc_is_hanging_garment,
                    aflc_is_functional_test_required,
                    aflc_is_perishable,
                    aflc_is_comes_as_set,
                    ARRAY (
                        SELECT (afls_version,
                                simple_sku.as_sku,
                                afls_country_of_origin,
                                afls_customs_code,
                                afls_net_weight,
                                afls_gross_weight,
                                afls_net_volume,
                                afls_gross_volume,
                                CASE WHEN shipping_placement_inner.ov_code IS NULL THEN NULL::option_value_type_code ELSE
                                ROW(
                                  'SHIPPING_PLACEMENT',
                                  shipping_placement_inner.ov_code
                                )::option_value_type_code END, -- (SELECT master_data_get_option_value_type_code_by_id(afls_shipping_placement_id)),
                                afls_package_height,
                                afls_package_width,
                                afls_package_length,
                                afls_is_customs_notification_required,
                                afls_has_preferential_treatment)::logistics_simple
                          FROM zcat_data.article_sku simple_sku
                          JOIN zcat_data.article_simple article ON article.as_simple_sku_id = simple_sku.as_id
                     LEFT JOIN zcat_data.article_facet_logistics_simple ON afls_simple_sku_id = simple_sku.as_id
                     LEFT JOIN zcat_option_value.shipping_placement shipping_placement_inner ON afls_shipping_placement_id = shipping_placement_inner.ov_id
                         WHERE simple_sku.as_config_id = config_sku.as_id
                           AND simple_sku.as_sku_type = 'SIMPLE'
                           -- if p_simple_sku is provided, than take only one simple, otherwise take all simples for config
                           AND (l_simple_sku_id IS NULL OR simple_sku.as_id = l_simple_sku_id))::logistics_simple[])::logistics_config
              FROM zcat_data.article_sku config_sku
              JOIN zcat_data.article_config article ON article.ac_config_sku_id = config_sku.as_id
         LEFT JOIN zcat_data.article_facet_logistics_config ON config_sku.as_id = aflc_config_sku_id
         LEFT JOIN zcat_option_value.shipping_placement shipping_placement ON aflc_shipping_placement_id = shipping_placement.ov_id
             WHERE config_sku.as_model_id = model_sku.as_id
               AND config_sku.as_sku_type = 'CONFIG'
               -- if p_config_sku is provided, than take only one config, otherwise take all configs for model
               AND (l_config_sku_id IS NULL OR config_sku.as_id = l_config_sku_id))::logistics_config[]
      FROM zcat_data.article_sku model_sku
      JOIN zcat_data.article_model article ON article.am_model_sku_id = model_sku.as_id
 LEFT JOIN zcat_data.article_facet_logistics_model ON aflm_model_sku_id = model_sku.as_id
 LEFT JOIN zcat_option_value.bootleg_type bootleg_type ON aflm_bootleg_type_id = bootleg_type.ov_id
     WHERE model_sku.as_id = l_model_sku_id;

end;
$BODY$

language 'plpgsql' stable security definer
cost 100;
