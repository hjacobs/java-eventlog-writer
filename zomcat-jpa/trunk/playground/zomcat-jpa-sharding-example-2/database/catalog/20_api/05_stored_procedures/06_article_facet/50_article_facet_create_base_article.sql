CREATE OR REPLACE FUNCTION article_facet_create_base_article(
    p_article_model              article_model,
    p_accounting_model           accounting_model,
    p_logistics_model            logistics_model,
    p_production_model           production_model,
    p_sales_model                sales_model,
    p_supplier_container_model   supplier_container_model,
    p_multimedia_model           multimedia_model,
    p_scope                      flow_scope
) RETURNS int AS
$BODY$
/*
  $Id$
  $HeadURL$

  Creates a new article model.

  Test:

  BEGIN;

  SET search_path TO zcat_api;

  INSERT INTO zcat_commons.brand(b_code, b_name, b_is_own_brand, b_created_by, b_last_modified_by)
    VALUES ('AD1', 'test', false, 'test', 'test');

  INSERT INTO zcat_commons.size_dimension(sd_code, sd_name, sd_created_by, sd_last_modified_by)
  VALUES ('0A', 'my dim', 'me', 'me');

  INSERT INTO zcat_commons.size_dimension_group(sdg_id, sdg_created_by)
  VALUES (123, 'me');

  INSERT INTO zcat_commons.size_dimension_group_binding (sdgb_code, sdgb_group_id, sdgb_position, sdgb_created_by, sdgb_last_modified_by)
  VALUES ('0A', 123, 1, 'me', 'me');

  INSERT INTO zcat_commons.size_chart (sc_code, sc_dimension_code, sc_brand_code, sc_created_by, sc_last_modified_by)
       VALUES ('2MKOAD1E0A', '0A', 'AD1', 'me', 'me');

  INSERT INTO zcat_commons.commodity_group (cg_code, cg_name, cg_created_by, cg_last_modified_by)
       VALUES ('commodity_group_code', 'my fancy commodity group', 'me', 'me');

  INSERT INTO zcat_data.article_sku (as_id, as_sku, as_sku_type, as_is_legacy)
       VALUES (nextval('zcat_data.article_sku_id_model_seq'),'AD111A001', 'MODEL', FALSE);

  SELECT * FROM article_facet_create_article_model (
      ('AD111A001','my fancy article','AD1',
        (null,null,null,'commodity_group_code',null,null,null,null,null,null)::commodity_group,
       '999999', 0,'description','{2MKOAD1E0A}'::text[],null,null)::article_model,
      'me',
      'flow_xy_123');

  SELECT * FROM zcat_data.article_model ORDER BY am_created DESC LIMIT 1;

  ROLLBACK;

*/
DECLARE
    l_article_model_id             int;
    l_article_config               article_config;
    l_article_simple               article_simple;
    l_accounting_config            accounting_config;
    l_accounting_simple            accounting_simple;
    l_logistics_config             logistics_config;
    l_logistics_simple             logistics_simple;
    l_production_config            production_config;
    l_production_simple            production_simple;
    l_sales_config                 sales_config;
    l_sales_simple                 sales_simple;
    l_supplier_container_config    supplier_container_config;
    l_supplier_container_simple    supplier_container_simple;
    l_supplier_model               supplier_model;
    l_supplier_config              supplier_config;
    l_supplier_simple              supplier_simple;
    l_multimedia_model             multimedia_model;
    l_multimedia_config            multimedia_config;
    l_multimedia_simple            multimedia_simple;
BEGIN

    -- creating the model:
    SELECT article_facet_create_article_model(p_article_model, NULL, NULL, NULL, NULL, NULL, NULL, p_scope) INTO l_article_model_id;

    -- creating the configs:
    FOREACH l_article_config IN ARRAY (p_article_model.config_facets) LOOP

        PERFORM article_facet_create_article_config(p_article_model.model_sku, l_article_config, NULL, NULL, NULL, NULL, NULL, NULL, p_scope);

        -- creating the simples:
        FOREACH l_article_simple IN ARRAY (l_article_config.simple_facets) LOOP
            PERFORM article_facet_create_article_simple(p_article_model.model_sku, l_article_config.config_sku, l_article_simple, NULL, NULL, NULL, NULL, p_scope);
        END LOOP;

    END LOOP;

    -- accounting facets
    IF p_accounting_model.model_sku IS NOT NULL THEN
        PERFORM article_facet_accounting_create_or_update_model(p_accounting_model, p_scope);
    END IF;

    FOREACH l_accounting_config IN ARRAY (p_accounting_model.config_facets) LOOP

        IF l_accounting_config.config_sku IS NOT NULL THEN
            PERFORM article_facet_accounting_create_or_update_config(l_accounting_config, p_scope);
        END IF;

        FOREACH l_accounting_simple IN ARRAY (l_accounting_config.simple_facets) LOOP
            IF l_accounting_simple.simple_sku IS NOT NULL THEN
                PERFORM article_facet_accounting_create_or_update_simple(l_accounting_simple, p_scope);
            END IF;
        END LOOP;

    END LOOP;

    -- logistics facets
    IF p_logistics_model.model_sku IS NOT NULL THEN
        PERFORM article_facet_logistics_create_or_update_model(p_logistics_model, p_scope);
    END IF;

    FOREACH l_logistics_config IN ARRAY (p_logistics_model.config_facets) LOOP

        IF l_logistics_config.config_sku IS NOT NULL THEN
            PERFORM article_facet_logistics_create_or_update_config(l_logistics_config, p_scope);
        END IF;

        FOREACH l_logistics_simple IN ARRAY (l_logistics_config.simple_facets) LOOP
            IF l_logistics_simple.simple_sku IS NOT NULL THEN
                PERFORM article_facet_logistics_create_or_update_simple(l_logistics_simple, p_scope);
            END IF;
        END LOOP;

    END LOOP;

    -- production facets
    IF p_production_model.model_sku IS NOT NULL THEN
        PERFORM article_facet_production_create_or_update_model(p_production_model, p_scope);
    END IF;

    FOREACH l_production_config IN ARRAY (p_production_model.config_facets) LOOP

        IF l_production_config.config_sku IS NOT NULL THEN
            PERFORM article_facet_production_create_or_update_config(l_production_config, p_scope);
        END IF;

    END LOOP;

    -- sales facets
    IF p_sales_model.model_sku IS NOT NULL THEN
        PERFORM article_facet_sales_create_or_update_model(p_sales_model, p_scope);
    END IF;

    FOREACH l_sales_config IN ARRAY (p_sales_model.config_facets) LOOP

        IF l_sales_config.config_sku IS NOT NULL THEN
            PERFORM article_facet_sales_create_or_update_config(l_sales_config, p_scope);
        END IF;

    END LOOP;

    -- supplier_container facets
    IF p_supplier_container_model.suppliers IS NOT NULL THEN
        FOREACH l_supplier_model IN ARRAY (p_supplier_container_model.suppliers) LOOP
            PERFORM article_facet_supplier_create_or_update_model(l_supplier_model, p_scope);
        END LOOP;
    END IF;

    FOREACH l_supplier_container_config IN ARRAY (p_supplier_container_model.config_facets) LOOP

        IF l_supplier_container_config.suppliers IS NOT NULL THEN
            FOREACH l_supplier_config IN ARRAY (l_supplier_container_config.suppliers) LOOP
                PERFORM article_facet_supplier_create_or_update_config(l_supplier_config, p_scope);
            END LOOP;
        END IF;

        FOREACH l_supplier_container_simple IN ARRAY (l_supplier_container_config.simple_facets) LOOP
            IF l_supplier_container_simple.suppliers IS NOT NULL THEN
                FOREACH l_supplier_simple IN ARRAY (l_supplier_container_simple.suppliers) LOOP
                    PERFORM article_facet_supplier_create_or_update_simple(l_supplier_simple, p_scope);
                END LOOP;
            END IF;
        END LOOP;

    END LOOP;

    -- multimedia facets
    IF p_multimedia_model.model_sku IS NOT NULL THEN
        PERFORM article_facet_multimedia_create_or_update_model(p_multimedia_model, p_scope);
    END IF;

    FOREACH l_multimedia_config IN ARRAY (p_multimedia_model.config_facets) LOOP

        IF l_multimedia_config.config_sku IS NOT NULL THEN
            PERFORM article_facet_multimedia_create_or_update_config(l_multimedia_config, p_scope);
        END IF;

        FOREACH l_multimedia_simple IN ARRAY (l_multimedia_config.simple_facets) LOOP
            IF l_multimedia_simple.simple_sku IS NOT NULL THEN
                PERFORM article_facet_multimedia_create_or_update_simple(l_multimedia_simple, p_scope);
            END IF;
        END LOOP;

    END LOOP;

    RETURN  l_article_model_id;
END;
$BODY$
LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
COST 100;
