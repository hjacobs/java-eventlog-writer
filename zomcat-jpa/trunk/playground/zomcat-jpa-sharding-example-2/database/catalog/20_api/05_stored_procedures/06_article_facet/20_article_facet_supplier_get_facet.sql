CREATE OR REPLACE FUNCTION article_facet_supplier_get_facet(
    p_model_sku text,
    p_config_sku text,
    p_simple_sku text,
    p_query facet_query
) RETURNS SETOF supplier_container_model AS
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
            NULL::integer, -- version always null for container
            as_sku,
            (SELECT array_agg(
                ROW(NULL::integer, -- version always null for container
                    outer_config_sku.as_sku,
                    (SELECT array_agg(row(NULL::integer, -- version always null for container
                                          outer_simple_sku.as_sku,
                                          ARRAY (
                                              SELECT
                                                     (afss_version,
                                                      simple_sku.as_sku,
                                                      afss_supplier_code,
                                                      afss_article_code)::supplier_simple
                                              FROM zcat_data.article_sku simple_sku
                                                  JOIN zcat_data.article_simple article ON article.as_simple_sku_id = simple_sku.as_id
                                                  LEFT JOIN zcat_data.article_facet_supplier_simple facet ON facet.afss_simple_sku_id = simple_sku.as_id
                                              WHERE simple_sku.as_id = outer_simple_sku.as_id
                                                    AND simple_sku.as_sku_type = 'SIMPLE'
                                                    AND (l_simple_sku_id IS NULL OR simple_sku.as_id = l_simple_sku_id)
                                                    AND (p_query.supplier_code is null OR afss_supplier_code = p_query.supplier_code))::supplier_simple[])::supplier_container_simple)
                         FROM zcat_data.article_sku outer_simple_sku
                         -- if p_simple_sku is provided, than take only one simple, otherwise take all simples for config
                         WHERE ((p_simple_sku is null AND outer_simple_sku.as_config_id = outer_config_sku.as_id)
                                OR (p_simple_sku is not null AND outer_simple_sku.as_id = l_simple_sku_id))
                               AND outer_simple_sku.as_sku_type = 'SIMPLE'
                    ),
                    ARRAY (
                        SELECT
                               (afsc_version,
                                config_sku.as_sku,
                                afsc_supplier_code,
                                afsc_article_code,
                                afsc_color_code,
                                afsc_color_description,
                                CASE WHEN availability.ov_code IS NULL THEN NULL::option_value_type_code ELSE
                                ROW(
                                  'AVAILABILITY',
                                  availability.ov_code
                                )::option_value_type_code END, -- (SELECT master_data_get_option_value_type_code_by_id(afsc_availability_id)),
                                afsc_upper_material_description,
                                afsc_lining_description,
                                afsc_sole_description,
                                afsc_inner_sole_description,
                                NULL)::supplier_config
                        FROM zcat_data.article_sku config_sku
                        JOIN zcat_data.article_config article ON article.ac_config_sku_id = config_sku.as_id
                   LEFT JOIN zcat_data.article_facet_supplier_config ON config_sku.as_id = afsc_config_sku_id
                   LEFT JOIN zcat_option_value.availability availability ON afsc_availability_id = availability.ov_id
                        WHERE config_sku.as_id = outer_config_sku.as_id
                              AND (l_config_sku_id IS NULL OR config_sku.as_id = l_config_sku_id)
                              AND (p_query.supplier_code is null OR afsc_supplier_code = p_query.supplier_code))::supplier_config[]
                )::supplier_container_config)
               FROM zcat_data.article_sku outer_config_sku
              -- if p_config_sku is provided, than take only one config, otherwise take all configs for model
              WHERE ((p_config_sku is null AND outer_config_sku.as_model_id = model_sku.as_id)
                    OR (p_config_sku is not null AND outer_config_sku.as_id = l_config_sku_id))
                    AND outer_config_sku.as_sku_type = 'CONFIG'
            ),
            (SELECT array_agg(
                ROW(afsm_version,
                    as_sku,
                    afsm_supplier_code,
                    afsm_article_name,
                    afsm_article_code,
                    afsm_shoe_last_group,
                    NULL
                )::supplier_model)
               FROM zcat_data.article_model
          LEFT JOIN zcat_data.article_facet_supplier_model ON afsm_model_sku_id = model_sku.as_id
              WHERE model_sku.as_id = am_model_sku_id
                    AND (p_query.supplier_code is null OR afsm_supplier_code = p_query.supplier_code)
            )
          FROM zcat_data.article_sku model_sku
          JOIN zcat_data.article_model article ON article.am_model_sku_id = model_sku.as_id
         WHERE model_sku.as_id = l_model_sku_id;

end;
$BODY$

language 'plpgsql' volatile security definer
cost 100;
