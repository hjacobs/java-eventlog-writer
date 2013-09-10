CREATE OR REPLACE FUNCTION article_facet_accounting_create_or_update_config(
    p_accounting_config  accounting_config,
    p_scope              flow_scope
) RETURNS text AS
$BODY$
/*
    $Id$
    $HeadURL$

    Creates or updates a accounting config facet.

*/
DECLARE
    l_config_sku_id int;
BEGIN

    -- determine sku id
    SELECT article_get_sku_id(p_accounting_config.config_sku, 'CONFIG')
      INTO l_config_sku_id;

    IF NOT EXISTS(SELECT 1 FROM zcat_data.article_config WHERE ac_config_sku_id = l_config_sku_id) THEN
        RAISE EXCEPTION 'Article config with sku % not found.', p_accounting_config.config_sku USING ERRCODE = 'Z0003';
    END IF;

    BEGIN

        INSERT INTO zcat_data.article_facet_accounting_config (
            afac_config_sku_id,
            afac_created_by,
            afac_last_modified_by,
            afac_flow_id,
            afac_initial_purchase_price,
            afac_initial_purchase_currency,
            afac_last_purchase_price,
            afac_last_purchase_currency,
            afac_valuation_price,
            afac_valuation_currency,
            afac_landed_cost_price,
            afac_landed_cost_currency,
            afac_amortization_rate
        )
        VALUES (
            l_config_sku_id,
            p_scope.user_id,
            p_scope.user_id,
            p_scope.flow_id,
            p_accounting_config.initial_purchase_price,
            p_accounting_config.initial_purchase_currency,
            p_accounting_config.last_purchase_price,
            p_accounting_config.last_purchase_currency,
            p_accounting_config.valuation_price,
            p_accounting_config.valuation_currency,
            p_accounting_config.landed_cost_price,
            p_accounting_config.landed_cost_currency,
            p_accounting_config.amortization_rate);

        RETURN 'CREATE';

    EXCEPTION
        WHEN unique_violation THEN

        UPDATE zcat_data.article_facet_accounting_config
           SET
               afac_last_modified              = now(),
               afac_last_modified_by           = p_scope.user_id,
               afac_flow_id                    = p_scope.flow_id,
               afac_version                    = p_accounting_config.version,

               afac_initial_purchase_price     = p_accounting_config.initial_purchase_price,
               afac_initial_purchase_currency  = p_accounting_config.initial_purchase_currency,
               afac_last_purchase_price        = p_accounting_config.last_purchase_price,
               afac_last_purchase_currency     = p_accounting_config.last_purchase_currency,
               afac_valuation_price            = p_accounting_config.valuation_price,
               afac_valuation_currency         = p_accounting_config.valuation_currency,
               afac_landed_cost_price          = p_accounting_config.landed_cost_price,
               afac_landed_cost_currency       = p_accounting_config.landed_cost_currency,
               afac_amortization_rate          = p_accounting_config.amortization_rate
        WHERE afac_config_sku_id = l_config_sku_id;

        RETURN 'UPDATE';

    END;

END;
$BODY$
LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
COST 100;