create or replace function article_facet_production_get_facet(
    p_model_sku text,
    p_config_sku text,
    p_simple_sku text,
    p_query facet_query
) RETURNS SETOF production_model AS
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
            afpm_version,
            as_sku,
            afpm_quality_group_q,
            CASE WHEN type_q.ov_code IS NULL THEN NULL::option_value_type_code ELSE
            ROW(
              'TYPE_Q',
              type_q.ov_code
            )::option_value_type_code END, -- (SELECT master_data_get_option_value_type_code_by_id(afpm_type_q_id)),
            afpm_material_weight,
            afpm_mesh,
            CASE WHEN material_detail.ov_code IS NULL THEN NULL::option_value_type_code ELSE
                    ROW(
                    'MATERIAL_DETAIL',
                    material_detail.ov_code
                )::option_value_type_code END, -- (SELECT master_data_get_option_value_type_code_by_id(afpm_material_detail_id)),
            ARRAY (
                SELECT (afpc_version,
                        config_sku.as_sku,
                        afpc_lead_time_days,
                        CASE WHEN production_material.ov_code IS NULL THEN NULL::option_value_type_code ELSE
                                ROW(
                                'PRODUCTION_MATERIAL',
                                production_material.ov_code
                            )::option_value_type_code END, -- (SELECT master_data_get_option_value_type_code_by_id(afpc_material_id)),
                        ARRAY (
                            SELECT ROW(NULL::integer, simple_sku.as_sku)::production_simple
                              FROM zcat_data.article_sku simple_sku
                              JOIN zcat_data.article_simple article ON article.as_simple_sku_id = simple_sku.as_id
                             WHERE simple_sku.as_config_id = config_sku.as_id
                               AND simple_sku.as_sku_type = 'SIMPLE'
                               -- if p_simple_sku is provided, than take only one simple, otherwise take all simples for config
                               AND (l_simple_sku_id IS NULL OR simple_sku.as_id = l_simple_sku_id))::production_simple[])::production_config
                  FROM zcat_data.article_sku config_sku
                  JOIN zcat_data.article_config article ON article.ac_config_sku_id = config_sku.as_id
             LEFT JOIN zcat_data.article_facet_production_config ON config_sku.as_id = afpc_config_sku_id
             LEFT JOIN zcat_option_value.production_material production_material ON afpc_material_id = production_material.ov_id
                 WHERE config_sku.as_model_id = model_sku.as_id
                   AND config_sku.as_sku_type = 'CONFIG'
                   -- if p_config_sku is provided, than take only one config, otherwise take all configs for model
                   AND (l_config_sku_id IS NULL OR config_sku.as_id = l_config_sku_id))::production_config[]
          FROM zcat_data.article_sku model_sku
          JOIN zcat_data.article_model article ON article.am_model_sku_id = model_sku.as_id
     LEFT JOIN zcat_data.article_facet_production_model ON afpm_model_sku_id = model_sku.as_id
     LEFT JOIN zcat_option_value.type_q type_q ON afpm_type_q_id = type_q.ov_id
     LEFT JOIN zcat_option_value.material_detail material_detail ON afpm_material_detail_id = material_detail.ov_id
         WHERE model_sku.as_id = l_model_sku_id;

END;
$BODY$

language 'plpgsql' volatile security definer
cost 100;
