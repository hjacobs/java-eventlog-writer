CREATE OR REPLACE FUNCTION article_facet_create_article_simple(
    p_model_sku                   text,
    p_config_sku                  text,
    p_article_simple              article_simple,
    p_accounting_simple           accounting_simple,
    p_logistics_simple            logistics_simple,
    p_supplier_container_simple   supplier_container_simple,
    p_multimedia_simple           multimedia_simple,
    p_scope                       flow_scope
) RETURNS int AS
$BODY$
/*
  $Id$
  $HeadURL$

  Creates a new article simple.
*/
DECLARE
    l_article_simple_id     int;
    l_model_sku_id          int;
    l_config_sku_id         int;
    l_simple_sku_id         int;
    l_supplier_simple       supplier_simple;
BEGIN

    IF position(p_model_sku in p_config_sku) != 1 THEN
        RAISE EXCEPTION 'Config sku: % is not based on model sku: %', p_config_sku, p_model_sku;
    END IF;

    IF position(p_config_sku in p_article_simple.simple_sku) != 1 THEN
        RAISE EXCEPTION 'Simple sku: % is not based on config sku: %', p_article_simple.simple_sku, p_config_sku;
    END IF;

    -- determine model sku id
    SELECT article_get_sku_id(p_model_sku, 'MODEL') INTO l_model_sku_id;

    -- determine sku id
    SELECT article_get_sku_id(p_config_sku, 'CONFIG') INTO l_config_sku_id;

      -- create the new simple sku:
    SELECT article_create_sku(p_article_simple.simple_sku, l_model_sku_id, l_config_sku_id, 'SIMPLE', p_scope)
      INTO l_simple_sku_id;

    -- set the article simple data:
    INSERT INTO zcat_data.article_simple
                (as_simple_sku_id,
                 as_created_by,
                 as_last_modified_by,
                 as_flow_id,
                 as_is_zalando_article,
                 as_is_partner_article,
                 as_is_globally_rebateable,
                 as_is_risk_article)
         VALUES (l_simple_sku_id,
                 p_scope.user_id,
                 p_scope.user_id,
                 p_scope.flow_id,
                 p_article_simple.zalando_article,
                 p_article_simple.partner_article,
                 p_article_simple.globally_rebateable,
                 p_article_simple.risk_article);

    -- set the ean for the simple:
    IF p_article_simple.ean IS NOT NULL THEN
      PERFORM article_simple_set_ean(p_article_simple.simple_sku, p_article_simple.ean);
    END IF;

    -- set the sizes for the simple:
    PERFORM article_facet_update_article_simple_sizes(l_simple_sku_id,
                                                      p_article_simple.size_codes,
                                                      p_scope);

    -- set the sales channels release for the simple:
    PERFORM article_facet_update_sales_channels_release(l_simple_sku_id,
                                                        p_article_simple.sales_channels_release,
                                                        p_scope);

    PERFORM sizing_create_or_update_size_info(l_simple_sku_id, p_scope);

    -- accounting facets
    IF p_accounting_simple.simple_sku IS NOT NULL THEN
        PERFORM article_facet_accounting_create_or_update_simple(p_accounting_simple, p_scope);
    END IF;

    -- logistics facets
    IF p_logistics_simple.simple_sku IS NOT NULL THEN
        PERFORM article_facet_logistics_create_or_update_simple(p_logistics_simple, p_scope);
    END IF;

    -- supplier_container facets
    IF p_supplier_container_simple.suppliers IS NOT NULL THEN
        FOREACH l_supplier_simple IN ARRAY (p_supplier_container_simple.suppliers) LOOP
            PERFORM article_facet_supplier_create_or_update_simple(l_supplier_simple, p_scope);
        END LOOP;
    END IF;

    -- multimedia facets
    IF p_multimedia_simple.simple_sku IS NOT NULL THEN
        PERFORM article_facet_multimedia_create_or_update((null, null, p_multimedia_simple)::multimedia_facet_wrapper, p_scope);
    END IF;

  RETURN l_simple_sku_id;
END;
$BODY$
LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
COST 100;
