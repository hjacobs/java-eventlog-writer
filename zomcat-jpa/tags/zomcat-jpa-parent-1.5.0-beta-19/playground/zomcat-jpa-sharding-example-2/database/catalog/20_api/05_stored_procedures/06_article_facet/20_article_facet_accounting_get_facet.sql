create or replace function article_facet_accounting_get_facet(
    p_model_sku text,
    p_config_sku text,
    p_simple_sku text,
    p_query facet_query
) RETURNS SETOF accounting_model AS
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
    SELECT afam_version,
           as_sku,
           CASE WHEN value_added_tax.ov_code IS NULL THEN NULL::option_value_type_code ELSE
           ROW(
            'TAX_CLASSIFICATION',
            value_added_tax.ov_code
           )::option_value_type_code END, -- (SELECT master_data_get_option_value_type_code_by_id(afam_value_added_tax_classification_id))

           CASE WHEN input_tax.ov_code IS NULL THEN NULL::option_value_type_code ELSE
           ROW(
            'TAX_CLASSIFICATION',
            input_tax.ov_code
           )::option_value_type_code END, -- (SELECT master_data_get_option_value_type_code_by_id(afam_input_tax_classification_id))
           ARRAY (
               SELECT (afac_version,
                       config_sku.as_sku,
                       afac_initial_purchase_price,
                       afac_initial_purchase_currency,
                       afac_last_purchase_price,
                       afac_last_purchase_currency,
                       afac_valuation_price,
                       afac_valuation_currency,
                       afac_landed_cost_price,
                       afac_landed_cost_currency,
                       afac_amortization_rate,
                       ARRAY (
                            SELECT (afas_version,
                                    simple_sku.as_sku,
                                    afas_initial_purchase_price,
                                    afas_initial_purchase_currency,
                                    afas_last_purchase_price,
                                    afas_last_purchase_currency,
                                    afas_valuation_price,
                                    afas_valuation_currency,
                                    afas_landed_cost_price,
                                    afas_landed_cost_currency,
                                    afas_amortization_rate)::accounting_simple
                              FROM zcat_data.article_sku simple_sku
                              JOIN zcat_data.article_simple article ON article.as_simple_sku_id = simple_sku.as_id
                         LEFT JOIN zcat_data.article_facet_accounting_simple simple ON afas_simple_sku_id = simple_sku.as_id
                             WHERE simple_sku.as_config_id = config_sku.as_id
                               AND simple_sku.as_sku_type = 'SIMPLE'
                               -- if p_simple_sku is provided, than take only one simple, otherwise take all simples for config
                               AND (l_simple_sku_id IS NULL OR simple_sku.as_id = l_simple_sku_id))::accounting_simple[])::accounting_config
                 FROM zcat_data.article_sku config_sku
                 JOIN zcat_data.article_config article ON article.ac_config_sku_id = config_sku.as_id
            LEFT JOIN zcat_data.article_facet_accounting_config ON config_sku.as_id = afac_config_sku_id
                WHERE config_sku.as_model_id = model_sku.as_id
                  AND config_sku.as_sku_type = 'CONFIG'
                  AND (l_config_sku_id IS NULL OR config_sku.as_id = l_config_sku_id))::accounting_config[]
      FROM zcat_data.article_sku model_sku
      JOIN zcat_data.article_model article ON article.am_model_sku_id = model_sku.as_id
 LEFT JOIN zcat_data.article_facet_accounting_model ON afam_model_sku_id = model_sku.as_id
 LEFT JOIN zcat_option_value.tax_classification value_added_tax ON afam_value_added_tax_classification_id = value_added_tax.ov_id
 LEFT JOIN zcat_option_value.tax_classification input_tax ON afam_input_tax_classification_id = input_tax.ov_id
     WHERE model_sku.as_id = l_model_sku_id;

end;
$BODY$

language 'plpgsql' stable security definer
cost 100;
