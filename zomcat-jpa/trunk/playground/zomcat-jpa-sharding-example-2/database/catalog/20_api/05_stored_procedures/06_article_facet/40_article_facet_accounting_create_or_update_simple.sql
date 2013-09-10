CREATE OR REPLACE FUNCTION article_facet_accounting_create_or_update_simple(
    p_accounting_simple  accounting_simple,
    p_scope              flow_scope
) RETURNS text AS
$BODY$
/*
    $Id$
    $HeadURL$

    Creates or updates a accounting simple facet.
*/
DECLARE
    l_simple_sku_id int;
BEGIN

    -- determine sku id
    SELECT article_get_sku_id(p_accounting_simple.simple_sku, 'SIMPLE')
      INTO l_simple_sku_id;

    IF NOT EXISTS(SELECT 1 FROM zcat_data.article_simple WHERE as_simple_sku_id = l_simple_sku_id) THEN
        RAISE EXCEPTION 'Article model with sku % not found.', p_accounting_simple.simple_sku USING ERRCODE = 'Z0004';
    END IF;

    BEGIN

        INSERT INTO zcat_data.article_facet_accounting_simple (
            afas_simple_sku_id,
            afas_created_by,
            afas_last_modified_by,
            afas_flow_id,
            afas_initial_purchase_price,
            afas_initial_purchase_currency,
            afas_last_purchase_price,
            afas_last_purchase_currency,
            afas_valuation_price,
            afas_valuation_currency,
            afas_landed_cost_price,
            afas_landed_cost_currency,
            afas_amortization_rate
        )
        VALUES (
            l_simple_sku_id,
            p_scope.user_id,
            p_scope.user_id,
            p_scope.flow_id,
            p_accounting_simple.initial_purchase_price,
            p_accounting_simple.initial_purchase_currency,
            p_accounting_simple.last_purchase_price,
            p_accounting_simple.last_purchase_currency,
            p_accounting_simple.valuation_price,
            p_accounting_simple.valuation_currency,
            p_accounting_simple.landed_cost_price,
            p_accounting_simple.landed_cost_currency,
            p_accounting_simple.amortization_rate);

        RETURN 'CREATE';

    EXCEPTION
        WHEN unique_violation THEN

        UPDATE zcat_data.article_facet_accounting_simple
           SET
               afas_last_modified              = now(),
               afas_last_modified_by           = p_scope.user_id,
               afas_flow_id                    = p_scope.flow_id,
               afas_version                    = p_accounting_simple.version,

               afas_initial_purchase_price     = p_accounting_simple.initial_purchase_price,
               afas_initial_purchase_currency  = p_accounting_simple.initial_purchase_currency,
               afas_last_purchase_price        = p_accounting_simple.last_purchase_price,
               afas_last_purchase_currency     = p_accounting_simple.last_purchase_currency,
               afas_valuation_price            = p_accounting_simple.valuation_price,
               afas_valuation_currency         = p_accounting_simple.valuation_currency,
               afas_landed_cost_price          = p_accounting_simple.landed_cost_price,
               afas_landed_cost_currency       = p_accounting_simple.landed_cost_currency,
               afas_amortization_rate          = p_accounting_simple.amortization_rate
        WHERE afas_simple_sku_id = l_simple_sku_id;

        RETURN 'UPDATE';

    END;

END;
$BODY$
LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
COST 100;