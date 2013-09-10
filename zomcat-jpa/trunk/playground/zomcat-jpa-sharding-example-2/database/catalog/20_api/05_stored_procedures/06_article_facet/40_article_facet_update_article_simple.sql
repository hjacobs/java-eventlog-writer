CREATE OR REPLACE FUNCTION article_facet_update_article_simple(
  p_article_simple  article_simple,
  p_scope           flow_scope
) RETURNS void AS
$BODY$
/*
  $Id$
  $HeadURL$

  Creates a new article simple.
*/
DECLARE
  l_simple_sku_id     int;
BEGIN
  RAISE INFO 'called article_facet_update_article_simple with % database = %', p_article_simple, current_database();

  -- determine sku id
  SELECT article_get_sku_id(p_article_simple.simple_sku, 'SIMPLE')
    INTO l_simple_sku_id;

  UPDATE zcat_data.article_simple
     SET as_last_modified               = now(),
         as_last_modified_by            = p_scope.user_id,
         as_flow_id                     = p_scope.flow_id,
         as_version                     = p_article_simple.version,

         as_is_zalando_article          = p_article_simple.zalando_article,
         as_is_partner_article          = p_article_simple.partner_article,
         as_is_globally_rebateable      = p_article_simple.globally_rebateable,
         as_is_risk_article             = p_article_simple.risk_article

   WHERE as_simple_sku_id = l_simple_sku_id;

  PERFORM article_facet_update_article_simple_sizes(l_simple_sku_id,
                                                    p_article_simple.size_codes,
                                                    p_scope);

  PERFORM article_facet_update_sales_channels_release(l_simple_sku_id,
                                                      p_article_simple.sales_channels_release,
                                                      p_scope);

  PERFORM sizing_create_or_update_size_info(l_simple_sku_id, p_scope);

END;
$BODY$
LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
COST 100;