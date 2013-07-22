CREATE OR REPLACE FUNCTION article_facet_create_article_model(
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

  SELECT * from article_facet_create_article_model (

  ROLLBACK;

*/
DECLARE
    l_article_model_id     int;
    l_model_sku_id         int;
    l_size_chart_group_id  int;
    l_supplier_model       supplier_model;
BEGIN
    -- create the new sku:
    SELECT article_create_sku(p_article_model.model_sku, null, null, 'MODEL', p_scope)
      INTO l_model_sku_id;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Commodity group with code "%" does not exist', (p_article_model.commodity_group).code;
    END IF;

    -- finds size chart group - if not existing, an exception will be thrown.
    SELECT sizing_get_chart_group_id(p_article_model.size_chart_codes)
      INTO l_size_chart_group_id;

    INSERT INTO zcat_data.article_model
                (am_model_sku_id,
                 am_name,
                 am_brand_code,
                 am_commodity_group_code,
                 am_target_group_set,
                 am_description,
                 am_size_chart_group_id,
                 am_main_supplier_code,
                 am_is_globally_rebateable,
                 am_is_risk_article,
                 am_is_commission_article,

                 am_created_by,
                 am_last_modified_by,
                 am_flow_id)
         VALUES (l_model_sku_id,
                 p_article_model.name,
                 p_article_model.brand_code,
                 p_article_model.commodity_group_code,
                 p_article_model.target_group_set,
                 p_article_model.description,
                 l_size_chart_group_id,
                 p_article_model.main_supplier_code,
                 p_article_model.globally_rebateable,
                 p_article_model.risk_article,
                 COALESCE(p_article_model.commission_article, false),

                 p_scope.user_id,
                 p_scope.user_id,
                 p_scope.flow_id);

    PERFORM article_facet_update_sales_channels_release(l_model_sku_id,
                                                        p_article_model.sales_channels_release,
                                                        p_scope);

    -- accounting facets
    IF p_accounting_model.model_sku IS NOT NULL THEN
        PERFORM article_facet_accounting_create_or_update_model(p_accounting_model, p_scope);
    END IF;

    -- logistics facets
    IF p_logistics_model.model_sku IS NOT NULL THEN
        PERFORM article_facet_logistics_create_or_update_model(p_logistics_model, p_scope);
    END IF;

    -- production facets
    IF p_production_model.model_sku IS NOT NULL THEN
        PERFORM article_facet_production_create_or_update_model(p_production_model, p_scope);
    END IF;

    -- sales facets
    IF p_sales_model.model_sku IS NOT NULL THEN
        PERFORM article_facet_sales_create_or_update_model(p_sales_model, p_scope);
    END IF;

    -- supplier_container facets
    IF p_supplier_container_model.suppliers IS NOT NULL THEN
        FOREACH l_supplier_model IN ARRAY (p_supplier_container_model.suppliers) LOOP
            PERFORM article_facet_supplier_create_or_update_model(l_supplier_model, p_scope);
        END LOOP;
    END IF;

    -- multimedia facets
    IF p_multimedia_model.model_sku IS NOT NULL THEN
        PERFORM article_facet_multimedia_create_or_update((p_multimedia_model, null, null)::multimedia_facet_wrapper, p_scope);
    END IF;

    RETURN  l_model_sku_id;
END;
$BODY$
LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
COST 100;