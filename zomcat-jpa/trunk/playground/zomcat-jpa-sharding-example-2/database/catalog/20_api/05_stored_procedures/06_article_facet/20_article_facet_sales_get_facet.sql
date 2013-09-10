create or replace function article_facet_sales_get_facet(
    p_model_sku text,
    p_config_sku text,
    p_simple_sku text,
    p_query facet_query
) RETURNS SETOF sales_model AS
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
             WHERE simple_sku.as_sku = p_simple_sku
               AND simple_sku.as_sku_type = 'SIMPLE';

            IF NOT FOUND THEN
                RAISE EXCEPTION 'simple sku % not found for config sku %', p_simple_sku, p_config_sku USING ERRCODE = 'Z0001';
            END IF;

        END IF;

    END IF;

    RETURN QUERY
        SELECT
            afsm_version,
            as_sku,
            CASE WHEN fitting.ov_code IS NULL THEN NULL::option_value_type_code ELSE
            ROW(
              'FITTING',
              fitting.ov_code
            )::option_value_type_code END, -- (SELECT master_data_get_option_value_type_code_by_id(afsm_fitting_id)),
            CASE WHEN closure.ov_code IS NULL THEN NULL::option_value_type_code ELSE
            ROW(
              'CLOSURE',
              closure.ov_code
            )::option_value_type_code END, -- (SELECT master_data_get_option_value_type_code_by_id(afsm_closure_id)),
            CASE WHEN toe_cap.ov_code IS NULL THEN NULL::option_value_type_code ELSE
            ROW(
              'TOE_CAP',
              toe_cap.ov_code
            )::option_value_type_code END, -- (SELECT master_data_get_option_value_type_code_by_id(afsm_toe_cap_id)),
            CASE WHEN sleeve_type.ov_code IS NULL THEN NULL::option_value_type_code ELSE
            ROW(
              'SLEEVE_TYPE',
              sleeve_type.ov_code
            )::option_value_type_code END, -- (SELECT master_data_get_option_value_type_code_by_id(afsm_sleeve_type_id)),
            afsm_heel_height,
            CASE WHEN heel_type.ov_code IS NULL THEN NULL::option_value_type_code ELSE
            ROW(
              'HEEL_TYPE',
              heel_type.ov_code
            )::option_value_type_code END, -- (SELECT master_data_get_option_value_type_code_by_id(afsm_heel_type_id)),
            CASE WHEN leg_type.ov_code IS NULL THEN NULL::option_value_type_code ELSE
            ROW(
              'LEG_TYPE',
              leg_type.ov_code
            )::option_value_type_code END, -- (SELECT master_data_get_option_value_type_code_by_id(afsm_leg_type_id)),
            CASE WHEN neck_line.ov_code IS NULL THEN NULL::option_value_type_code ELSE
            ROW(
              'NECK_LINE',
              neck_line.ov_code
            )::option_value_type_code END, -- (SELECT master_data_get_option_value_type_code_by_id(afsm_neck_line_id)),
            CASE WHEN shoe_upper.ov_code IS NULL THEN NULL::option_value_type_code ELSE
            ROW(
              'SHOE_UPPER',
              shoe_upper.ov_code
            )::option_value_type_code END, -- (SELECT master_data_get_option_value_type_code_by_id(afsm_shoe_upper_id)),
            CASE WHEN textile_membrane.ov_code IS NULL THEN NULL::option_value_type_code ELSE
            ROW(
              'TEXTILE_MEMBRANE',
              textile_membrane.ov_code
            )::option_value_type_code END, -- (SELECT master_data_get_option_value_type_code_by_id(afsm_textile_membrane_id)),
            CASE WHEN fit_type.ov_code IS NULL THEN NULL::option_value_type_code ELSE
            ROW(
              'FIT_TYPE',
              fit_type.ov_code
            )::option_value_type_code END, -- (SELECT master_data_get_option_value_type_code_by_id(afsm_fit_type_id)),
            afsm_is_extra_large,
            CASE WHEN sport_type.ov_code IS NULL THEN NULL::option_value_type_code ELSE
            ROW(
              'SPORT_TYPE',
              sport_type.ov_code
            )::option_value_type_code END, -- (SELECT master_data_get_option_value_type_code_by_id(afsm_sport_type_id)),
            CASE WHEN sub_sport_type.ov_code IS NULL THEN NULL::option_value_type_code ELSE
            ROW(
              'SUB_SPORT_TYPE',
              sub_sport_type.ov_code
            )::option_value_type_code END, -- (SELECT master_data_get_option_value_type_code_by_id(afsm_sub_sport_type_id)),
            ARRAY (
                SELECT (afsc_version,
                        config_sku.as_sku,
                        afsc_comment,
                          CASE WHEN sole_type.ov_code IS NULL THEN NULL::option_value_type_code ELSE
                          ROW(
                          'SOLE_TYPE',
                          sole_type.ov_code
                        )::option_value_type_code END, -- (SELECT master_data_get_option_value_type_code_by_id(afsc_sole_type_id)),
                        CASE WHEN insole_type.ov_code IS NULL THEN NULL::option_value_type_code ELSE
                        ROW(
                          'INSOLE_TYPE',
                          insole_type.ov_code
                        )::option_value_type_code END, -- (SELECT master_data_get_option_value_type_code_by_id(afsc_insole_type_id)),
                        CASE WHEN trend1.ov_code IS NULL THEN NULL::option_value_type_code ELSE
                        ROW(
                          'TREND',
                          trend1.ov_code
                        )::option_value_type_code END, -- (SELECT master_data_get_option_value_type_code_by_id(afsc_trend1_id)),
                        CASE WHEN trend2.ov_code IS NULL THEN NULL::option_value_type_code ELSE
                        ROW(
                          'TREND',
                          trend2.ov_code
                        )::option_value_type_code END, -- (SELECT master_data_get_option_value_type_code_by_id(afsc_trend2_id)),
                        CASE WHEN textile_upper.ov_code IS NULL THEN NULL::option_value_type_code ELSE
                        ROW(
                          'TEXTILE_UPPER',
                          textile_upper.ov_code
                        )::option_value_type_code END, -- (SELECT master_data_get_option_value_type_code_by_id(afsc_textile_upper_id)),
                        CASE WHEN shoe_upper.ov_code IS NULL THEN NULL::option_value_type_code ELSE
                        ROW(
                          'SHOE_UPPER',
                          shoe_upper.ov_code
                        )::option_value_type_code END, -- (SELECT master_data_get_option_value_type_code_by_id(afsc_shoe_upper_id)),
                        CASE WHEN target_group_age.ov_code IS NULL THEN NULL::option_value_type_code ELSE
                        ROW(
                          'TARGET_GROUP_AGE',
                          target_group_age.ov_code
                        )::option_value_type_code END, -- (SELECT master_data_get_option_value_type_code_by_id(afsc_target_group_age_id)),
                        CASE WHEN lining_type.ov_code IS NULL THEN NULL::option_value_type_code ELSE
                        ROW(
                          'LINING_TYPE',
                          lining_type.ov_code
                        )::option_value_type_code END, -- (SELECT master_data_get_option_value_type_code_by_id(afsc_lining_type_id)),
                        CASE WHEN shoe_lining_material.ov_code IS NULL THEN NULL::option_value_type_code ELSE
                        ROW(
                          'SHOE_LINING_MATERIAL',
                          shoe_lining_material.ov_code
                        )::option_value_type_code END, -- (SELECT master_data_get_option_value_type_code_by_id(afsc_shoe_lining_material_id)),
                        CASE WHEN textile_lining_material.ov_code IS NULL THEN NULL::option_value_type_code ELSE
                        ROW(
                          'TEXTILE_LINING_MATERIAL',
                          textile_lining_material.ov_code
                        )::option_value_type_code END, -- (SELECT master_data_get_option_value_type_code_by_id(afsc_textile_lining_material_id)),
                        CASE WHEN leather_type.ov_code IS NULL THEN NULL::option_value_type_code ELSE
                        ROW(
                          'LEATHER_TYPE',
                          leather_type.ov_code
                        )::option_value_type_code END, -- (SELECT master_data_get_option_value_type_code_by_id(afsc_leather_type_id)),
                        ARRAY (
                            SELECT ROW(NULL::integer, simple_sku.as_sku)::sales_simple
                              FROM zcat_data.article_sku simple_sku
                              JOIN zcat_data.article_simple article ON article.as_simple_sku_id = simple_sku.as_id
                             WHERE simple_sku.as_config_id = config_sku.as_id
                               AND simple_sku.as_sku_type = 'SIMPLE'
                               -- if p_simple_sku is provided, than take only one simple, otherwise take all simples for config
                               AND (l_simple_sku_id IS NULL OR simple_sku.as_id = l_simple_sku_id))::sales_simple[])::sales_config
                  FROM zcat_data.article_sku config_sku
                  JOIN zcat_data.article_config article ON article.ac_config_sku_id = config_sku.as_id
             LEFT JOIN zcat_data.article_facet_sales_config ON config_sku.as_id = afsc_config_sku_id
             LEFT JOIN zcat_option_value.sole_type sole_type ON afsc_sole_type_id = sole_type.ov_id
             LEFT JOIN zcat_option_value.insole_type insole_type ON afsc_insole_type_id = insole_type.ov_id
             LEFT JOIN zcat_option_value.trend trend1 ON afsc_trend1_id = trend1.ov_id
             LEFT JOIN zcat_option_value.trend trend2 ON afsc_trend2_id = trend2.ov_id
             LEFT JOIN zcat_option_value.textile_upper textile_upper ON afsc_textile_upper_id = textile_upper.ov_id
             LEFT JOIN zcat_option_value.shoe_upper shoe_upper ON afsc_shoe_upper_id = shoe_upper.ov_id
             LEFT JOIN zcat_option_value.target_group_age target_group_age ON afsc_target_group_age_id = target_group_age.ov_id
             LEFT JOIN zcat_option_value.lining_type lining_type ON afsc_lining_type_id = lining_type.ov_id
             LEFT JOIN zcat_option_value.shoe_lining_material shoe_lining_material ON afsc_shoe_lining_material_id = shoe_lining_material.ov_id
             LEFT JOIN zcat_option_value.textile_lining_material textile_lining_material ON afsc_textile_lining_material_id = textile_lining_material.ov_id
             LEFT JOIN zcat_option_value.leather_type leather_type ON afsc_leather_type_id = leather_type.ov_id
                 WHERE config_sku.as_model_id = model_sku.as_id
                   AND config_sku.as_sku_type = 'CONFIG'
                   -- if p_config_sku is provided, than take only one config, otherwise take all configs for model
                   AND (l_config_sku_id IS NULL OR config_sku.as_id = l_config_sku_id))::sales_config[]
          FROM zcat_data.article_sku model_sku
          JOIN zcat_data.article_model article ON article.am_model_sku_id = model_sku.as_id
     LEFT JOIN zcat_data.article_facet_sales_model ON afsm_model_sku_id = model_sku.as_id
     LEFT JOIN zcat_option_value.fitting fitting ON afsm_fitting_id = fitting.ov_id
     LEFT JOIN zcat_option_value.closure closure ON afsm_closure_id = closure.ov_id
     LEFT JOIN zcat_option_value.toe_cap toe_cap ON afsm_toe_cap_id = toe_cap.ov_id
     LEFT JOIN zcat_option_value.sleeve_type sleeve_type ON afsm_sleeve_type_id = sleeve_type.ov_id
     LEFT JOIN zcat_option_value.heel_type heel_type ON afsm_heel_type_id = heel_type.ov_id
     LEFT JOIN zcat_option_value.leg_type leg_type ON afsm_leg_type_id = leg_type.ov_id
     LEFT JOIN zcat_option_value.neck_line neck_line ON afsm_neck_line_id = neck_line.ov_id
     LEFT JOIN zcat_option_value.shoe_upper shoe_upper ON afsm_shoe_upper_id = shoe_upper.ov_id
     LEFT JOIN zcat_option_value.textile_membrane textile_membrane ON afsm_textile_membrane_id = textile_membrane.ov_id
     LEFT JOIN zcat_option_value.fit_type fit_type ON afsm_fit_type_id = fit_type.ov_id
     LEFT JOIN zcat_option_value.sport_type sport_type ON afsm_sport_type_id = sport_type.ov_id
     LEFT JOIN zcat_option_value.sub_sport_type sub_sport_type ON afsm_sub_sport_type_id = sub_sport_type.ov_id
         WHERE model_sku.as_id = l_model_sku_id;

end;
$BODY$

language 'plpgsql' volatile security definer
cost 100;
