CREATE OR REPLACE FUNCTION article_facet_update_article_model(
  p_article_model  article_model,
  p_scope          flow_scope
) RETURNS void AS
$BODY$
/*
  $Id$
  $HeadURL$

  Updates a existing article model.

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
  l_article_model_id     int;
  l_model_sku_id         int;
  l_commodity_group_id   int;
  l_size_chart_group_id  int;
  l_size_chart_codes     text[];
BEGIN
  RAISE INFO 'called article_facet_update_article_model database = %', current_database();

  -- determine sku id
  SELECT article_get_sku_id(p_article_model.model_sku, 'MODEL')
    INTO l_model_sku_id;

  UPDATE zcat_data.article_model
     SET am_name                    = p_article_model.name,
         am_brand_code              = p_article_model.brand_code,
         am_commodity_group_code    = p_article_model.commodity_group_code,
         am_target_group_set        = p_article_model.target_group_set,
         am_description             = p_article_model.description,
--       am_size_chart_group_id     -- cannot be changed!
         am_main_supplier_code      = p_article_model.main_supplier_code,
         am_is_globally_rebateable  = p_article_model.globally_rebateable,
         am_is_risk_article         = p_article_model.risk_article,
         am_is_commission_article   = COALESCE(p_article_model.commission_article, false),

         am_last_modified           = now(),
         am_last_modified_by        = p_scope.user_id,
         am_flow_id                 = p_scope.flow_id,
         am_version                 = p_article_model.version
   WHERE am_model_sku_id = l_model_sku_id;

  PERFORM article_facet_update_sales_channels_release(l_model_sku_id,
                                                      p_article_model.sales_channels_release,
                                                      p_scope);

END;
$BODY$
LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
COST 100;
