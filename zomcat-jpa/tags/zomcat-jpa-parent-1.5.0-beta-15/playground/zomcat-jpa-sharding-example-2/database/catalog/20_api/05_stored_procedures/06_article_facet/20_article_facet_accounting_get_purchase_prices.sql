CREATE OR REPLACE FUNCTION article_facet_accounting_get_purchase_prices(
    p_simple_skus text[]
) RETURNS SETOF purchase_price AS
$BODY$

/*
-- test
    show search_path;
    set search_path to zcat_api_r13_00_21, public;
    SELECT * from zcat_api_r13_00_21.article_facet_accounting_get_purchase_prices(ARRAY['RE5-fza-0201-99-0035']);
 */

BEGIN
    RAISE INFO 'received %', p_simple_skus;

    RETURN QUERY
    WITH simple_sku as (
      SELECT simple_sku FROM unnest(p_simple_skus) t (simple_sku)
    )
    SELECT simple_sku,
           COALESCE(afas_initial_purchase_price, afac_initial_purchase_price),
           COALESCE(afas_initial_purchase_currency, afac_initial_purchase_currency)::text,
           COALESCE(afas_last_purchase_price, afac_last_purchase_price),
           COALESCE(afas_last_purchase_currency, afac_last_purchase_currency)::text
      FROM simple_sku
      LEFT JOIN zcat_data.article_sku ass on ass.as_sku = simple_sku
      LEFT JOIN zcat_data.article_facet_accounting_simple on afas_simple_sku_id = ass.as_id
      LEFT JOIN zcat_data.article_facet_accounting_config on afac_config_sku_id = ass.as_config_id;
END;
$BODY$

LANGUAGE plpgsql
  STABLE
  SECURITY DEFINER
  COST 100;